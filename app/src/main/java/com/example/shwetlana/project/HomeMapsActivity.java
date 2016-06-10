package com.example.shwetlana.project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

public class HomeMapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private GoogleMap cabsMap;
    private Button btnFindPath;
    //private EditText etOrigin;
    //private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private SeekBar seekBar;
    private TextView tvRatePerMile;
    double ratePerMile = 0;
    private Marker marker;

    AutoCompleteTextView etDestination;
    AutoCompleteTextView etOrigin;
    String to = "";
    String from = "";
    String distance = "";
    //String duration = "";
    int rate = 2; // per mile rate , shud come from db the price rate of selected taxi
    private PlacesAutoCompleteAdapter adapter;
    Button btnCalculate;
    TextView tvCalculatedPrice;
    TextView tvDistance;

    private GoogleMap googleMap;
    private Button btnLogout;
    private List<Route> taxiPath;
//    private Marker selectedMarker;
    // private AnimatingMarkersFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMap = mapFragment.getMap();

        adapter = new PlacesAutoCompleteAdapter(getApplicationContext(),
                R.layout.autocomplete_list_text);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (AutoCompleteTextView) findViewById(R.id.etOrigin);
        etOrigin.setAdapter(adapter);
        etDestination = (AutoCompleteTextView) findViewById(R.id.etDestination);
        etDestination.setAdapter(adapter);

        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvCalculatedPrice = (TextView) findViewById(R.id.tvCalculatedPrice);


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tvRatePerMile = (TextView) findViewById(R.id.tvRatePerMile);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                tvRatePerMile.setText("" + String.valueOf(i));
                ratePerMile = i + 0.5;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvRatePerMile.setText("" + seekBar.getProgress());


        btnCalculate = (Button) findViewById(R.id.btnCalculate);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnCalculate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                distance = tvDistance.getText().toString();
                String[] res = distance.split("\\s+");

                double f = Double.valueOf(res[0]) * Double.valueOf(tvRatePerMile.getText().toString());

                String dd = "  $" + String.format("%.2f", f) + "";

                tvCalculatedPrice.setVisibility(View.VISIBLE);
                tvCalculatedPrice.setText(dd);

            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeMapsActivity.this,LoginActivity.class));
                finish();

            }
        });

    }

    private void sendRequest() {
        /*-----------------------------------Changes Implemented for GetOrigin-----------*/
        //String origin = mMap.getMyLocation().toString();
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Hardcoded Taxi's...................TEMPORARY

    }


    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        LatLng cab1 = new LatLng(34.069433, -118.167755);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2))
                .position(cab1)
                .title("CAB 1").snippet("Northeast Los Angeles, Los Angeles, CA, USA"));

            /*LatLng cab2 = new LatLng(34.057345, -118.172390);
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2))
                    .position(cab2)
                    .title("CAB 2"));*/

        LatLng cab3 = new LatLng(34.076827, -118.156769);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2))
                .position(cab3)
                .title("CAB 3").snippet("2956 W Shorb St, Alhambra, CA 91803, USA"));

        LatLng cab4 = new LatLng(34.051254, -118.165439);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2))
                .position(cab4)
                .title("CAB 4").snippet("Monterey Park, CA, USA"));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                try {
                    new DirectionFinder(new DirectionFinderListener() {
                        @Override
                        public void onDirectionFinderStart() {
                            progressDialog = ProgressDialog.show(HomeMapsActivity.this, "Please wait.",
                                    "Getting Taxi to Orgin..!", true);
                        }

                        @Override
                        public void onDirectionFinderSuccess(List<Route> route) {
                            progressDialog.dismiss();
                            //HomeMapsActivity.this.onDirectionFinderSuccess(route);

                            List<LatLng> tmpListroute = new ArrayList<LatLng>();
                            tmpListroute.addAll(route.get(0).points);
                            if (taxiPath != null) {
                                List<LatLng> mainPath = taxiPath.get(0).points;
                                for (LatLng item :
                                        mainPath) {
                                    tmpListroute.add(item);

                                }
                                setAnimation(mMap, tmpListroute, marker);
                            } else {
                                setAnimation(mMap, tmpListroute, marker);
                            }
                        }
                    }, marker.getSnippet(), etOrigin.getText().toString()).execute();
                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                return false;
            }
        });
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        taxiPath = routes;
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 13));
           /* ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);*/
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true)./*
                    color(Color.BLUE).*/
                    width(10);


            //Marker movingMarker = mMap.addMarker(new MarkerOptions().position(route.points.get(0)));
            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
//            for (int i = 0; i < routes.size(); i++) {
//                animateMarker(routes.get(i).startLocation
//                        , routes.get(i).endLocation);
//            }
//            if (selectedMarker != null) {
//
//                setAnimation(mMap, routes.get(0).points, selectedMarker);
//                selectedMarker = null;
//            }

        }
    }

    public void animateMarker(final LatLng fromPosition, final LatLng toPosition/*,
                              final boolean hideMarker*/) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        marker = mMap.addMarker(new MarkerOptions()
                .position(fromPosition));
        marker.setPosition(fromPosition);
        final long duration = 5000;
        //marker.remove();
        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("------------------", marker.toString());
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * fromPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * fromPosition.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private void panCamera() {

        LatLng begin = googleMap.getCameraPosition().target;

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(begin)
                        .bearing(45)
                        .tilt(45)
                        .zoom(googleMap.getCameraPosition().zoom)
                        .build();

        googleMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPosition),
                3000,
                new GoogleMap.CancelableCallback() {

                    @Override
                    public void onFinish() {
                        System.out.println("finished camera");
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("cancelling camera");
                    }
                }
        );

    }

    public static void setAnimation(GoogleMap myMap, final List<LatLng> directionPoint, Marker marker) {


//        Marker marker = myMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi2))
//                .position(directionPoint.get(0))
//                .flat(true));

        // myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint.get(0), 10));

        animateMarker(myMap, marker, directionPoint, false);
    }


    private static void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
