package com.example.gpsapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private TextView txtLocation;
    private Button btnGetLocation;

    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocation = findViewById(R.id.txtLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        locationViewModel.getLocationData().observe(this, this::showLocation);

        btnGetLocation.setOnClickListener(v -> getLocation());
    }

    private void getLocation() {
        // Verifica si la aplicación tiene permiso para acceder a la ubicación fina o aproximada.
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // Si no tiene ninguno de los dos permisos, los solicita.
        if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            // Si tiene al menos uno de los permisos, solicita la actualización de la ubicación.
            locationViewModel.refreshLocation();
        }
    }

    private void showLocation(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            txtLocation.setText(
                    "Latitud: " + lat + "\nLongitud: " + lon
            );
        } else {
            txtLocation.setText("No se pudo obtener la ubicación");
        }
    }

    /*
        El método onRequestPermissionsResult es un callback (método de retorno)
          que Android llama automáticamente cuando el usuario responde a una solicitud
          de permisos en tiempo de ejecución.

        Es decir:

        Tú pides un permiso → Android muestra un diálogo →
        El usuario acepta o rechaza →
        Android llama a onRequestPermissionsResult para informarte del resultado.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            boolean fineLocationGranted = false;
            boolean coarseLocationGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    fineLocationGranted = true;
                } else if (permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    coarseLocationGranted = true;
                }
            }

            if (fineLocationGranted || coarseLocationGranted) {
                getLocation();
            } else {
                Toast.makeText(this,
                        "Permiso de ubicación denegado",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
