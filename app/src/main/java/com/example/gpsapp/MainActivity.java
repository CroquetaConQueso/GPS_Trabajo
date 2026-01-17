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

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 100;

    private TextView txtLocation;
    private Button btnGetLocation;
    private LocationViewModel locationViewModel;

    // Variables para el mapa
    private MapView mapView;
    private Marker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Es importante configurar el UserAgent antes del layout
        Configuration.getInstance().setUserAgentValue(getPackageName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocation = findViewById(R.id.txtLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);

        // Inicializar el Mapa
        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true); // Permite hacer zoom con los dedos
        mapView.getController().setZoom(18.0); // Nivel de zoom inicial

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        // Observar la ubicación
        locationViewModel.getLocationData().observe(this, location -> {
            if (location != null) {
                showLocation(location);
                updateMap(location.getLatitude(), location.getLongitude()); // Actualizar mapa
            }
        });

        btnGetLocation.setOnClickListener(v -> getLocation());
    }

    // Método para actualizar la posición en el mapa
    private void updateMap(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);
        mapView.getController().setCenter(point); // Centra el mapa en las coordenadas

        if (userMarker == null) {
            userMarker = new Marker(mapView);
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(userMarker);
        }

        userMarker.setPosition(point);
        userMarker.setTitle("Mi ubicación");
        mapView.invalidate(); // Refresca el mapa para mostrar cambios
    }

    private void showLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        txtLocation.setText("Latitud: " + lat + "\nLongitud: " + lon);
    }

    private void getLocation() {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            locationViewModel.refreshLocation();
        }
    }

    // Gestión del ciclo de vida para el mapa
    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            boolean granted = false;
            for (int res : grantResults) {
                if (res == PackageManager.PERMISSION_GRANTED) granted = true;
            }
            if (granted) {
                getLocation();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}