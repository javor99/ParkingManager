package com.parkingmanager.parkingmanager;



import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController()
@RequestMapping("/cities")
public class CityController {

    private String adminKey;
    private final Environment environment;

    private final CityRepository cityRepository;

    private final UserRepository userRepository;
    private final EmailEncryption emailEncryption;

    @Autowired
    public CityController(Environment environment, CityRepository cityRepository, UserRepository userRepository, EmailEncryption emailEncryption) {
        this.environment = environment;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.emailEncryption=emailEncryption;
        adminKey=environment.getProperty("admin.key");
    }

    @PostMapping("/add/{name}")
    public ResponseEntity<City> createCity(
            @PathVariable String name,
            @CookieValue(value = "adminkey", required = false) String adminKeyFromCookie
    ) {
        // Check if the "adminkey" cookie is present and matches the expected value
        if (adminKeyFromCookie == null || !adminKeyFromCookie.equals(adminKey)) {
            // If the admin key is missing or doesn't match, return an error response
            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }

        // Create a new City object
        City city = new City();
        city.setName(name);

        // Save the new City document to the repository
        City savedCity = cityRepository.save(city);

        // Return the saved City object in the response
        return ResponseEntity.ok(savedCity);
    }
    @GetMapping("/all")
    public ResponseEntity<List<City>> getAllCities(@CookieValue(value = "specialToken", required = false) String specialToken,HttpServletRequest request) throws Exception {

        List<City> cities = cityRepository.findAll();
        System.out.println("Retrieved Cities: " + cities);

        if(specialToken.equals(adminKey))
            return ResponseEntity.ok(cities);

        System.out.println("Cookie je " +specialToken);

        Cookie[] cookies = request.getCookies();

        System.out.println(cookies +" my nigga");;


        if (specialToken== null ||  userRepository.findByEmail(emailEncryption.decrypt(specialToken))==null) {

            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }


        return ResponseEntity.ok(cities);
    }
}
