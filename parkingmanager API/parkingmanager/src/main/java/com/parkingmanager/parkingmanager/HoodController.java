package com.parkingmanager.parkingmanager;


import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hoods")
public class HoodController {
    HoodRepository hoodRepository;
    private String adminKey= "bafbb70d2bf049f1cb2caf8fa2909ca8619816f4696fbfc5ef2c6b0541010245ec221cc1aa84d6e0875ad98746018f2ca835d3e3d127311c08f70de35fffd22e42e55b6a1b31634780484de0e2dd88f0e4350077a64a966c1f597a9722d7f86f711f3910";



    private final UserRepository userRepository;
    private final EmailEncryption emailEncryption;

    @Autowired
    public HoodController(HoodRepository hoodRepository,UserRepository userRepository,EmailEncryption emailEncryption) {
        this.userRepository = userRepository;
        this.hoodRepository = hoodRepository;
        this.emailEncryption=emailEncryption;
    }


    @GetMapping("/getHoods/{city}")
    public ResponseEntity<List<String>> getHoodsforCity(@PathVariable String city,@CookieValue(value = "specialToken", required = false) String specialToken) throws Exception {

        System.out.println(city);

        List<Hood> hoodsInCity = hoodRepository.findByCity(city);

        List<String> hoodovi = hoodsInCity.stream()
                .map(Hood::getName)
                .collect(Collectors.toList());

        System.out.println("Hood token je "+specialToken +"a nhegova dekeripcija je "+ emailEncryption.decrypt(specialToken));

        if(specialToken.equals(adminKey))
            return ResponseEntity.ok(hoodovi);

        if (specialToken== null ||  userRepository.findByEmail(emailEncryption.decrypt(specialToken))==null) {

            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }






        return ResponseEntity.ok(hoodovi);

    }
    @GetMapping("/getHoods/all")
    public ResponseEntity<List<Hood>> getHoodsforCity(@CookieValue(value = "specialToken", required = false) String specialToken) throws Exception {

        System.out.println("GETHOODS");
        List<Hood> hoods = hoodRepository.findAll();
        if(specialToken.equals(adminKey))
            return ResponseEntity.ok(hoods);

        if (specialToken== null ||  userRepository.findByEmail(emailEncryption.decrypt(specialToken))==null) {

            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }






        return ResponseEntity.ok(hoods);

    }

    @PostMapping("/putHood/{city}/{name}")
    public ResponseEntity<Hood> putHood(@PathVariable String city , @PathVariable String name , @CookieValue(value = "adminkey", required = false) String adminKeyFromCookie){

        if (adminKeyFromCookie == null || !adminKeyFromCookie.equals(adminKey)) {
            // If the admin key is missing or doesn't match, return an error response
            return ResponseEntity.badRequest().body(null); // You can customize the error response as needed
        }
        Hood hood = new Hood(city,name);
        Hood ret =hoodRepository.save(hood);

        return ResponseEntity.ok(ret);



    }



}
