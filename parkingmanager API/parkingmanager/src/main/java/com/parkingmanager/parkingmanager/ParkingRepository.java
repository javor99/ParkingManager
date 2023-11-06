package com.parkingmanager.parkingmanager;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParkingRepository extends MongoRepository<Parking,String> {

    List<Parking> findByHoodId(String hoodId);

    List<Parking> findByCityId(String cityId);
}
