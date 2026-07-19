package com.berkay.airline_reservation_system.controller;

import jakarta.validation.constraints.NotBlank;

public class AirportForm {

    @NotBlank(message = "Airport code is required")
    private String code;

    @NotBlank(message = "Need name for airport")
    private String name;

    @NotBlank(message = "Airport needs to belong to a city")
    private String city;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
