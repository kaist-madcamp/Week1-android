package com.example.madcamp1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Random;

import kotlinx.coroutines.scheduling.Task;

public class MapFrag extends Fragment implements LocationListener {
    private Random random = new Random(1984);
    private GoogleMap mGoogleMap;
    private FragmentActivity myContext;
    private MainActivity main = (MainActivity) getActivity();
    private Location mLastLocation;
    private Marker mCurrLocationMarker;

    private double lat = 36.374171;
    private double lng = 127.365293;
    private ClusterManager<MyItem> clusterManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Initialize view
        View rootView = inflater.inflate(R.layout.map_frag, container, false);

        //Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        Button map_type_btn = rootView.findViewById(R.id.map_type_btn);
        map_type_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mGoogleMap.getMapType() == 1) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }else {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        //Async map
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // when map is loaded
                mGoogleMap = googleMap;

                if(ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                } else {
                    checkLocationPermission();
                    return;
                }

                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("카이스트");
                markerOptions.snippet("N1");
                markerOptions.position(latLng);

                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));


                clusterManager = new ClusterManager<MyItem>(getContext(), googleMap);
                googleMap.setOnCameraIdleListener(clusterManager);
                googleMap.setOnMarkerClickListener(clusterManager);

                clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(item.getTitle());
                        markerOptions.position(item.getPosition());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(item.getPosition()));
                        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });

                addItems();
            }
        });

        ActionBar ab = ((MainActivity)getActivity()).getSupportActionBar();
        ab.setTitle("Map");

//        return view
        return rootView;
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    private void addItems() {
        String[] classMates = new String[]{"정이든", "강수아", "이혜민", "송예은", "양현호", "김경하", "금나연", "김영경", "김장현", "김주형", "김찬영", "김태우", "김현수", "박종서", "송인화", "최정재", "송재현", "양현호", "이호준", "정나연", "종회", "주은", "최종윤", "최지호", "추승우"};

        for(int i = 0 ; i < 24; i++) {
            MyItem offsetItem = new MyItem(position(), classMates[i]);
            clusterManager.addItem(offsetItem);
        }
    }

    private LatLng position() {
        return new LatLng(random(36.374171, 36.375171), random(127.365293, 127.363393));
    }

    private double random(double min, double max) {
        return random.nextDouble() * (max - min) + min;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

//    현재 위치가 바뀔때마다 실행되는 콜백.
    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));

    }
}
