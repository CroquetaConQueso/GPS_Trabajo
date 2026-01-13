package com.example.gpsapp;

// El Modelo representa los datos de la aplicación. En este caso, la latitud y longitud.
public class LocationModel {
    private double latitude;
    private double longitude;

    public LocationModel(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
