package com.parkingmanager.parkingmanager;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRepository extends MongoRepository<City,String> {


    City findByName(String name);
}
