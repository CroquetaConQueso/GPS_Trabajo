package com.example.gpsapp;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

// El ViewModel actúa como un intermediario entre el Repositorio y la Vista (MainActivity).
// Mantiene los datos de la UI y sobrevive a los cambios de configuración.
public class LocationViewModel extends AndroidViewModel {

    private final LocationRepository locationRepository;
    private final LiveData<Location> locationData;


    // El constructor obtiene la instancia del repositorio y el LiveData de la ubicación.
    public LocationViewModel(@NonNull Application application) {
        super(application);
        locationRepository = LocationRepository.getInstance(application);
        locationData = locationRepository.getLocation();
    }

    // Expone el LiveData de la ubicación para que la Vista lo observe.
    public LiveData<Location> getLocationData() {
        return locationData;
    }

    // Llama al repositorio para actualizar los datos de ubicación.
    public void refreshLocation() {
        locationRepository.getLocation();
    }
}
