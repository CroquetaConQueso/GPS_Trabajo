package com.example.gpsapp;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

// El Repositorio maneja las operaciones de datos. En este caso, obtiene la ubicación del dispositivo.
public class LocationRepository {

    private static LocationRepository instance;
    private final FusedLocationProviderClient fusedLocationClient;
    private final MutableLiveData<Location> locationData = new MutableLiveData<>();
    private final Application application;

    private LocationRepository(Application application) {
        this.application = application;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
    }

    // Singleton pattern para asegurar que solo haya una instancia del repositorio.
    public static synchronized LocationRepository getInstance(Application application) {
        if (instance == null) {
            instance = new LocationRepository(application);
        }
        return instance;
    }

    // Obtiene la última ubicación conocida del proveedor de ubicación.
    public LiveData<Location> getLocation() {
        // Verifica si la aplicación tiene permiso para acceder a la ubicación fina o aproximada.
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            // Usamos getCurrentLocation para obtener una ubicación nueva y actualizada.
            // Esto es mejor que getLastLocation(), que puede devolver null si la ubicación no se conoce.
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                    .addOnSuccessListener(location -> {
                        // Cuando se obtiene la ubicación, se actualiza el LiveData.
                        locationData.setValue(location);
                    })
                    .addOnFailureListener(e -> {
                        // Si hay un error, también actualizamos el LiveData a null para notificar a la UI.
                        locationData.setValue(null);
                    });
        } else {
            // Si no hay permisos, establece el valor en null.
            locationData.setValue(null);
        }
        return locationData;
    }
}
