package Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import org.json.JSONObject;
import org.json.JSONException;

import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import Config.BaseURL;
import mohaimenul.grocery.AppController;
import mohaimenul.grocery.R;
import service.GetLocationService;
import util.CustomVolleyJsonRequest;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GoogleMap_fragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mMap;
    LocationManager locationManager;
    Marker currentMarker;
    public static final int REQUEST_LOCATION_CODE = 99;

    private GoogleApiClient client;

    private BroadcastReceiver broadcastReceiver;

    TextView tv_leftTime, tv_address, tv_deliver_comment;

    String sale_id;
    double cur_lat, cur_lng, prev_lat, prev_lng;

    public GoogleMap_fragment() {

    }

    Handler mHandler = new Handler();
    Runnable mRun = new Runnable() {
        @Override
        public void run() {

            requestDeliveryManLocation();

            mHandler.postDelayed(mRun, 5000);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(mRun, 5000);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRun);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRun);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);

//        mHandler.postDelayed(mRun, 5000);

        return view;
    }

    @TargetApi(17)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_leftTime = (TextView)view.findViewById(R.id.tv_left_time);
        tv_address = (TextView)view.findViewById(R.id.tv_address);
        tv_deliver_comment = (TextView)view.findViewById(R.id.tv_deliver_comment);

        sale_id = getArguments().getString("sale_id");

        currentMarker = null;
        prev_lat = 0;
        prev_lng = 0;

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestDeliveryManLocation();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        else {
            Toast.makeText(getActivity(), "Google Service is Denied.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    } else
                        Toast.makeText(getActivity(), "11111111", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getActivity(),"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
        Toast.makeText(getActivity(), "5555555", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    public void requestDeliveryManLocation() {
        String tag_json_obj = "json_login_req";

        Map<String, String> params = new HashMap<String, String>();
        params.put("sale_id", sale_id);
//                params.put("password", password);

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
                BaseURL.GET_LOCATION_URL, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean status = response.getBoolean("error");
                    if (!status) {
                        int delivery_status = response.getInt("cur_status");
                        if(delivery_status == 1) {
                            tv_deliver_comment.setVisibility(View.INVISIBLE);

                            //delete previous location marker
                            if(currentMarker != null) {
                                currentMarker.remove();
                            }

                            //draw previous location
                            if((prev_lat != 0) && (prev_lng != 0)) {
                                drawCircle(new LatLng(prev_lat, prev_lng));
                            }

                            Double latitude = response.getDouble("latitude");
                            Double longitude = response.getDouble("longitude");
                            String curr_pos_addr = response.getString("cur_pos");
                            String start_time = response.getString("start_time");

                            //add current location marker
                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng latLng = new LatLng(latitude, longitude);
                            markerOptions.title("Delivery Man");
                            markerOptions.snippet(curr_pos_addr);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            markerOptions.position(latLng);
                            currentMarker = mMap.addMarker(markerOptions);
                            if((prev_lat == 0) || (prev_lng == 0)) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            }

                            prev_lat = latitude;
                            prev_lng = longitude;

                            //set delivery start time and current address
                            tv_leftTime.setText(start_time);
                            tv_address.setText(curr_pos_addr);
                        } else if(delivery_status == 0) {
                            tv_leftTime.setText("N/A");
                            tv_address.setText("N/A");
                            tv_deliver_comment.setVisibility(View.VISIBLE);
                            mHandler.removeCallbacks(mRun);
                        } else if(delivery_status == 2) {
                            tv_leftTime.setText("N/A");
                            tv_address.setText("N/A");
                            tv_deliver_comment.setText("Your Order is Done");
                            tv_deliver_comment.setVisibility(View.VISIBLE);
                            mHandler.removeCallbacks(mRun);
                        }

                    } else {
                        String error = "DataBase ERROR";
                        Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(getActivity(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "GoogleMap connection error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(20);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

}
