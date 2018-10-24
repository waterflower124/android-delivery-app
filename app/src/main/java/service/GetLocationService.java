package service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Config.BaseURL;
import mohaimenul.grocery.AppController;
import mohaimenul.grocery.LoginActivity;
import mohaimenul.grocery.MainActivity;
import mohaimenul.grocery.R;
import util.ConnectivityReceiver;
import util.CustomVolleyJsonRequest;
import util.Session_management;

import static java.lang.Boolean.TRUE;

public class GetLocationService extends Service {
    String sale_id;
    int iii = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    Handler mHandler = new Handler();
//    Runnable mRun = new Runnable() {
//        @Override
//        public void run() {
//
//            requestLocation();
//
//            mHandler.postDelayed(mRun, 1000);
//        }
//    };

    @Override
    public void onCreate() {

        if (ConnectivityReceiver.isConnected()) {

//            timer.schedule(timertask, 0, 1000);
//            makeLocationRequest(sale_id);
//            mHandler.post(mRun);
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        sale_id = bundle.getString("sale_id");
        return START_STICKY;
//        return super.onStartCommand(intent, flags, startId);

    }

//    public void requestLocation() {
//        String tag_json_obj = "json_login_req";
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("sale_id", sale_id);
////                params.put("password", password);
//
//        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.POST,
//                BaseURL.GET_LOCATION_URL, params, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject response) {
////                        Log.d(TAG, response.toString());
//
//                try {
//                    Boolean status = response.getBoolean("error");
//                    if (!status) {
//
////                                JSONObject obj = response.getJSONObject("data");
////                                String user_id = obj.getString("user_id");
////                                String user_fullname = obj.getString("user_fullname");
////                                String user_email = obj.getString("user_email");
////                                String user_phone = obj.getString("user_phone");
////                                String user_image = obj.getString("user_image");
//                        Double latitude = response.getDouble("latitude");
//                        Double longitude = response.getDouble("longitude");
//                        String curr_pos_addr = response.getString("cur_pos");
//
//                        Intent intent = new Intent("Location");
//                        intent.putExtra("latitude", latitude);
//                        intent.putExtra("longitude", longitude);
//                        intent.putExtra("curr_pos_addr", curr_pos_addr);
//                        sendBroadcast(intent);
//
//                    } else {
//                        String error = "DataBase ERROR";
//                        Toast.makeText(getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                        VolleyLog.d(TAG, "Error: " + error.getMessage());
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }


}
