package il.co.afeka.com.memorygame.scoreboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import il.co.afeka.com.memorygame.ClassApplication;
import il.co.afeka.com.memorygame.R;



public class ScoreMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    public ScoreMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);


        setMapIfNeeded();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setMapIfNeeded();

    }
    private void setMapIfNeeded() {
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
//            initLocation();
            LatLng cameraPos = null;
            //DataBaseHandler dataBaseHandler = new DataBaseHandler(getActivity().getApplicationContext());
            //for (Winner winRecord : dataBaseHandler.getTableRecord(mode)){

            //LatLng latLng = new LatLng(winRecord.getLat(), winRecord.getLng());
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
            ClassApplication application = (ClassApplication)getActivity().getApplication();
            DatabaseProvider provider = application.getDatabaseProvider();
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            for(final UserItem item :provider.getMyInv()){
                if(!(item.getLat()==-1 && item.getLng()==-1)) {
                    LatLng latLng = new LatLng(item.getLat(), item.getLng());
                    MarkerOptions options = new MarkerOptions().position(latLng).title(item.getName() + " - " + item.getScore());
                    mMap.addMarker(options);
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            marker.showInfoWindow();
                            return true;
                        }
                    });
                    cameraPos = latLng;
                    //}
                    if (cameraPos != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPos));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPos, 8f));
                    }
                }
            }
//                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                    @Override
//                    public boolean onMarkerClick(Marker marker) {
//                        String winnerName = marker.getTitle();
//                        LatLng pos = marker.getPosition();
//                        ((WinnersTableActivity)getActivity()).markTable(winnerName,pos);
//                        return false;
//                    }
//                });



        }
    }
}
