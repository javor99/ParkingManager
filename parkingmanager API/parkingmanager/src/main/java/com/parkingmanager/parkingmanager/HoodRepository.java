package com.parkingmanager.parkingmanager;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HoodRepository extends MongoRepository<Hood,String> {
    List<Hood> findByCity(String city);
}
