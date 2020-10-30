package rob.myappcompany.mapsactivity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    //we wanna check if user granted permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //first we must check if permission has been granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if user granted permission, we get location and we keep location updated
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //first map must be prepared
        mMap = googleMap;


        //after map is ready, lets get location
        //we create a location manager that will get the user's location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //Toast.makeText(MapsActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
                //first clear map
                mMap.clear();
                // lets get user location and show it on map
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));//choose icon and color for marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
                //we use geocoder to get information about location(like address)
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    //we may get some adresses from a loation.
                  List<Address>listAdresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                  //if list is not empty, we want to get the most accurate address
                    if ((listAdresses!=null) && listAdresses.size()>0){
                       // Log.i("PlaceInfo",listAdresses.get(0).toString());
                        String address="";
                        //get the street address
                        if (listAdresses.get(0).getThoroughfare()!=null){
                            address+=listAdresses.get(0).getThoroughfare()+ " ";
                        }
                        //get the city
                        if (listAdresses.get(0).getLocality()!=null){
                            address+=listAdresses.get(0).getLocality()+ " ";
                        }
                        //get the postal code
                        if (listAdresses.get(0).getPostalCode()!=null){
                            address+=listAdresses.get(0).getPostalCode()+ " ";
                        }
                        //get the state
                        if (listAdresses.get(0).getAdminArea()!=null){
                            address+=listAdresses.get(0).getAdminArea();
                        }
                        Toast.makeText(MapsActivity.this,address,Toast.LENGTH_SHORT).show();





                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //project will run only on sdk 23 and above.let's check user's sdk level
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else {
                //perm was granted, location is given
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                //last known location
                Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //we show the last known location. first clear map
                mMap.clear();
                // lets get user location
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));//choose icon and color for marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,10));

            }
        }
      // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); //satelight image(customize map )
       // mMap.setMapType((GoogleMap.MAP_TYPE_TERRAIN));
        // Add a marker in Sydney and move the camera
        //LatLng israel = new LatLng(32.3047236,34.8954075);
      //  mMap.addMarker(new MarkerOptions().position(israel).title("Valid over here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));//choose icon and color for marker
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(israel,10));
    }
}