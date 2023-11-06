package com.parkingmanager.parkingmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emailVerification")
public class UserController {

    private String adminKey= "bafbb70d2bf049f1cb2caf8fa2909ca8619816f4696fbfc5ef2c6b0541010245ec221cc1aa84d6e0875ad98746018f2ca835d3e3d127311c08f70de35fffd22e42e55b6a1b31634780484de0e2dd88f0e4350077a64a966c1f597a9722d7f86f711f3910";



    private UserRepository userRepository;

    @Autowired
    UserController(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    @GetMapping("/all")
    ResponseEntity<List<User>> allUsers(@CookieValue(value = "specialToken", required = false) String adminKeyFromCookie) {
        List<User> users = userRepository.findAll();
        if(adminKey.equals(adminKeyFromCookie)) {

            return ResponseEntity.ok(users);

        }

        return ResponseEntity.badRequest().body(null);



    }

    @GetMapping("/delete/{userid}")
    ResponseEntity deleteUser(@CookieValue(value = "specialToken", required = false) String adminKeyFromCookie,@PathVariable String userid) {

        if(adminKey.equals(adminKeyFromCookie)) {

            userRepository.deleteById(userid);
            return ResponseEntity.ok().body("Success");

        }

        return ResponseEntity.badRequest().body(null);



    }

    @PostMapping("/add")
    ResponseEntity addUser(@CookieValue(value = "specialToken", required = false) String adminKeyFromCookie,@RequestBody User body) {

        if(adminKey.equals(adminKeyFromCookie)) {
            userRepository.save(body);
            return ResponseEntity.ok().body("Success");

        }

        return ResponseEntity.badRequest().body(null);



    }


}
