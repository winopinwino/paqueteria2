package com.example.paqueteria2;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference ref,ref2 ;
    double latitudOrigen,longitudOrigen,latitudDestino,longitudDestino;
    ArrayList <Marker> tmpRealTimeMarker= new ArrayList<>();
    ArrayList <Marker> realTimeMarker= new ArrayList<>();
    ArrayList <Marker> tmpRealTimeMarker2= new ArrayList<>();
    ArrayList <Marker> realTimeMarker2= new ArrayList<>();
    private TextView distancia,costo;
    private TextView tiempo;
    private JSONObject jso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ref= FirebaseDatabase.getInstance().getReference();
        ref2= FirebaseDatabase.getInstance().getReference();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ref2.child("destino").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Marker marker:realTimeMarker2) {
                    marker.remove();
                }
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    MapsPojo mp = snapshot.getValue(MapsPojo.class);
                    latitudDestino= mp.getLatitude();
                    longitudDestino = mp.getLongitude();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitudDestino,longitudDestino)).title("Destino");
                    tmpRealTimeMarker2.add(mMap.addMarker(markerOptions));


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        realTimeMarker2.clear();
        realTimeMarker2.addAll(tmpRealTimeMarker2);

        ref.child("origen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Marker marker:realTimeMarker) {
                    marker.remove();
                }
                for (DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    MapsPojo mp = snapshot.getValue(MapsPojo.class);
                    latitudOrigen= mp.getLatitude();
                    longitudOrigen = mp.getLongitude();
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitudOrigen,longitudOrigen)).title("Origen");
                    tmpRealTimeMarker.add(mMap.addMarker(markerOptions));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitudOrigen,longitudOrigen))
                            .zoom(14)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    Log.i("jsoOrigen: ",""+latitudOrigen +","+longitudOrigen);
                    Log.i("jsodestin: ",""+latitudDestino+","+longitudDestino);
                    generaRuta(latitudOrigen,longitudOrigen,latitudDestino,longitudDestino);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        realTimeMarker.clear();
        realTimeMarker.addAll(tmpRealTimeMarker);



    }

    private void trazarRuta(JSONObject jso) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");

            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");
                tiempo.setText("" + jLegs.getString(1));
                distancia.setText("Distancia: " + ((JSONObject)((JSONObject)jLegs.get(0)).get("distance")).get("text"));
                tiempo.setText("Tiempo" + ((JSONObject)((JSONObject)jLegs.get(0)).get("duration")).get("text"));
                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k<jSteps.length();k++){


                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        // Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mMap.addPolyline(new PolylineOptions().addAll(list).color(Color.RED).width(8));



                    }



                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void generaRuta (double latitudOrigen,double longitudOrigen,double latitudDestino,double longitudDestino){



        String url =" https://maps.googleapis.com/maps/api/directions/json?origin="+latitudOrigen+","+longitudOrigen+"&destination="+latitudDestino+","+longitudDestino +"&key=AIzaSyDJmRZ_9Jddux73THvgzcve81WM-sbSvvA";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    jso = new JSONObject(response);
                    trazarRuta(jso);
                    Log.i("jsonRuta: ",""+response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }
}
