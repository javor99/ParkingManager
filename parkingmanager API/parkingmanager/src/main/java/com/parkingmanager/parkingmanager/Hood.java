package com.parkingmanager.parkingmanager;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hoods")
public class Hood {
    @Id
    private String id;
    private String city;

    public Hood(String city, String name) {
        this.city = city;
        this.name = name;
    }

    public Hood() {

    }

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String name;
}
