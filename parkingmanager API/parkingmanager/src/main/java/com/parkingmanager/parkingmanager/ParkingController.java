package com.parkingmanager.parkingmanager;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parkings")
public class ParkingController {

    private String adminKey= "bafbb70d2bf049f1cb2caf8fa2909ca8619816f4696fbfc5ef2c6b0541010245ec221cc1aa84d6e0875ad98746018f2ca835d3e3d127311c08f70de35fffd22e42e55b6a1b31634780484de0e2dd88f0e4350077a64a966c1f597a9722d7f86f711f3910";

    ParkingService parkingService;

    UserRepository userRepository;

    CityRepository cityRepository;

    EmailEncryption emailEncryption;

    @Autowired
    ParkingController(ParkingService parkingService,UserRepository userRepository, EmailEncryption emailEncryption,CityRepository cityRepository) {
        this.parkingService=parkingService;
        this.emailEncryption=emailEncryption;
        this.userRepository=userRepository;
        this.cityRepository=cityRepository;
    }
    @PostMapping("/add")
    ResponseEntity addParking(@RequestBody Parking body, @CookieValue(value = "specialToken", required = false) String adminKeyFromCookie) {

        if (adminKeyFromCookie == null || !adminKeyFromCookie.equals(adminKey)) {
            // If the admin key is missing or doesn't match, return an error response
            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }

        System.out.println(body);
        parkingService.saveParking(body);
        return ResponseEntity.ok("Success");

    }

    @GetMapping("/getParking/{hoodId}")
    ResponseEntity<List<Parking>> getHoodParkings(@PathVariable String hoodId,@CookieValue(value = "specialToken", required = false) String specialToken) throws Exception {

        if(specialToken.equals(adminKey))
            return ResponseEntity.ok(parkingService.getHoodParkings(hoodId));


        if (specialToken== null ||  userRepository.findByEmail(emailEncryption.decrypt(specialToken))==null) {

            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }

        return ResponseEntity.ok(parkingService.getHoodParkings(hoodId));


    }
    @GetMapping("/getParkingCity/{name}")
    ResponseEntity<List<Parking>> getCityParkings(@PathVariable String name,@CookieValue(value = "specialToken", required = false) String specialToken) throws Exception {
        String cityId=cityRepository.findByName(name.toUpperCase()).getId();

        if(specialToken.equals(adminKey))
            return ResponseEntity.ok(parkingService.getCityParkings(cityId));

           if (specialToken== null ||  userRepository.findByEmail(emailEncryption.decrypt(specialToken))==null) {

            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }


        return ResponseEntity.ok(parkingService.getCityParkings(cityId));


    }

    @GetMapping("/getParkingDetalji/{parkingId}")
    ResponseEntity<Parking> getParkingDetalji(@PathVariable String parkingId, @CookieValue(value = "specialToken", required = false) String specialToken) throws Exception {

        if(specialToken.equals(adminKey))
            return ResponseEntity.ok(parkingService.getDetalji(parkingId));

           if (specialToken== null ||  userRepository.findByEmail(emailEncryption.decrypt(specialToken))==null) {

             return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }

        return ResponseEntity.ok(parkingService.getDetalji(parkingId));


    }




}
