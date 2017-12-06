package com.example.corei5.hohome;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener{
    private GoogleMap mMap;
    private TextView txt_view;
    private Boolean isInadd = false;
    private ArrayList<Marker> list_markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        txt_view = (TextView)this.findViewById(R.id.txt);

        FloatingActionButton  storeBtn = (FloatingActionButton)this.findViewById(R.id.store);
        loadData();
        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestParams params = new RequestParams();
                String send = "";
                for (int i = 0; i < list_markers.size(); i++) {
                    LatLng coor = list_markers.get(i).getPosition();
                    send +="{" + coor.latitude + "," +coor.longitude + "} ";
                }
                params.put("coor", send);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://192.168.43.124:3000/sendcoords", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        showToast();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        errorToas();
                    }
                });
            }
        });
        list_markers = new ArrayList<Marker>();
        FloatingActionButton removeBtn = (FloatingActionButton) findViewById(R.id.remove);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_markers.size() > 0) {
                    Marker aux =  list_markers.get(list_markers.size()-1);
                    aux.remove();
                    list_markers.remove(list_markers.size()-1);
                }
            }
        });
        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInadd == true) {
                    isInadd = false;
                }else {
                    isInadd = true;
                }
                upDateMsnText();

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    public void errorToas(){
        Toast.makeText(this,"ERROR!! ", Toast.LENGTH_SHORT).show();
    }
    public void showToast(){
        Toast.makeText(this,"Se inserto con éxito ", Toast.LENGTH_SHORT).show();
    }
    public void upDateMsnText () {
        if (isInadd == true) {
            txt_view.setText("Add Mark");
        }else {
            txt_view.setText("");
        }
    }
    public void loadData(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.43.124:3000/getCoors",null , new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                JSONArray aux = timeline;
                ArrayList<LatLng> list_lat = new ArrayList<LatLng>();
                for (int i = 0; i < aux.length(); i++) {
                    try {
                        JSONObject obj = aux.getJSONObject(i);
                        double lat = Double.parseDouble(obj.get("lat").toString());
                        double lng = Double.parseDouble(obj.get("lng").toString());
                        list_lat.add(new LatLng(lat, lng));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                for (int i = 0; i < list_lat.size(); i++) {
                    setMark(list_lat.get(i));
                }
            }
        });
    }
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng potosi = new LatLng(-19.578297, -65.758633);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(potosi, 14));
        mMap.setOnMapClickListener(this);
    }
    public void setMark(LatLng latLng) {
        Marker m = mMap.addMarker(new MarkerOptions().position(latLng).title("No title"));

    }
    @Override
    public void onMapClick(LatLng latLng) {

        if (isInadd) {
            Marker m = mMap.addMarker(new MarkerOptions().position(latLng).title("No title"));

            list_markers.add(m);
        }
    }

}
