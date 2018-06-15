package il.co.afeka.com.memorygame.scoreboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import il.co.afeka.com.memorygame.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private LocationManager locationManager;
    private GoogleMap mMap;

    private double lat,lng;
    private int locationFlag;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);


        setMapIfNeeded();

        return rootView;
    }

    public void onResume() {
        super.onResume();
        setMapIfNeeded();
    }

    public void onPause() {
        super.onPause();
    }

    private void setMapIfNeeded() {
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
//            if(mMap==null){
//                //mMap = (((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getM);
//                if(mMap!=null){
//                    LatLng cameraPos = null;
//                    DataBaseHandler dataBaseHandler = new DataBaseHandler(getActivity().getApplicationContext());
//                    for (Winner winRecord : dataBaseHandler.getTableRecord(mode)){
//                        LatLng latLng = new LatLng(winRecord.getLat() , winRecord.getLng());
//                        MarkerOptions options = new MarkerOptions().position(latLng).title(winRecord.getName());
//                        mMap.addMarker(options);
//
//                        cameraPos = latLng;
//                    }
//                    if(cameraPos != null){
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPos));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPos,8f));
//                    }
//                }
//            }

    }
    private void initLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            lat = location.getLatitude();
            lng = location.getLongitude();
        }
        else{
            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location1 != null){
                lat = location1.getLatitude();
                lng = location1.getLongitude();
            }
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        locationFlag = 0;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            LatLng cameraPos = null;
            //DataBaseHandler dataBaseHandler = new DataBaseHandler(getActivity().getApplicationContext());
            //for (Winner winRecord : dataBaseHandler.getTableRecord(mode)){

            //LatLng latLng = new LatLng(winRecord.getLat(), winRecord.getLng());
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions options = new MarkerOptions().position(latLng).title("test");
            mMap.addMarker(options);
//                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        String winnerName = marker.getTitle();
//                        LatLng pos = marker.getPosition();
//                        ((WinnersTableActivity)getActivity()).markTable(winnerName,pos);
//                        return false;
//                    }
//                });


            cameraPos = latLng;
            //}
            if (cameraPos != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPos));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPos, 8f));
            }
        }
    }

}
