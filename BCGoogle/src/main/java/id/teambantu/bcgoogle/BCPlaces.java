package id.teambantu.bcgoogle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import id.teambantu.bcgoogle.event.BCPlacesListener;
import id.teambantu.bcgoogle.model.BCSearchLocationResult;
import id.teambantu.bcmodel.helper.Location;

public  class BCPlaces {

    private final static String TAG = BCPlaces.class.getSimpleName();
    private static RequestQueue requestQueue;

    private static PlacesClient initiatePlaces(Context context) {
        Places.initialize(context, context.getString(R.string.googleApiKey));
        return Places.createClient(context);
    }

    private static Geocoder initiateGeocode(Context context) {
        return new Geocoder(context, Locale.getDefault());
    }

    public static void getAddress(final Context context, final Location location, final BCPlacesListener listener) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+location.getLatitude() +","+location.getLongitude()+"&key=" + context.getString(R.string.googleApiKey)+"&language=id";
        getApiFromServer(context, url, Request.Method.GET, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<Location> locations = new ArrayList<>();
                    for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                        JSONObject jsonObject = response.getJSONArray("results").getJSONObject(i);
                        Gson gson = new Gson();
                        BCSearchLocationResult result = gson.fromJson(jsonObject.toString(), BCSearchLocationResult.class);

                        Location location1 = new Location();
                        location1.setName(result.getFormatted_address().split(",")[0]);
                        location1.setAddress(result.getFormatted_address());
                        location1.setLatitude(result.getGeometry().getLocation().getLat());
                        location1.setLongitude(result.getGeometry().getLocation().getLng());

                        locations.add(location1);
                    }
                    listener.onSuccess(locations);
                    listener.onSuccess(locations.size()>0?locations.get(0):new Location());
                } catch (JSONException e) {
                    listener.onFailed(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(error.getMessage());
            }
        });

    }

    public static void getPlaces(final Context context, final Location location, final BCPlacesListener listener) {

        PlacesClient client = initiatePlaces(context);

        List<Place.Field> placeField = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeField);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.findCurrentPlace(request).addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful()) {
                        FindCurrentPlaceResponse response = task.getResult();
                        if (response != null && response.getPlaceLikelihoods().size() > 0) {
                            List<Place> places = new ArrayList<>();
                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                if (placeLikelihood.getLikelihood() > 0.25f)
                                    places.add(placeLikelihood.getPlace());
                            }

                            if (places.size() > 0) {

                                id.teambantu.bcmodel.helper.Location location1 = new id.teambantu.bcmodel.helper.Location();

                                location1.setName(places.get(0).getName());
                                location1.setAddress(places.get(0).getAddress());
                                location1.setLatitude(Objects.requireNonNull(places.get(0).getLatLng()).latitude);
                                location1.setLongitude(Objects.requireNonNull(places.get(0).getLatLng()).longitude);
                                listener.onSuccess(location1);
                            } else {
                                getAddress(context, location, listener);
                            }
                        } else {
                            getAddress(context, location, listener);
                        }
                    } else {
                        Log.d(TAG, "onComplete: " + task.getException());
                        getAddress(context, location, listener);
                    }
                }
            });
        } else listener.onFailed("Permission denied");
    }
    public static void getCurrentLocation(Context context, final BCPlacesListener listener) {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {
                    if (task.isSuccessful()) {
                        android.location.Location location = task.getResult();
                        if (location != null)
                            listener.onSuccess(new Location(location.getLatitude(), location.getLongitude()));
                        else listener.onFailed("No location detected");
                    } else
                        listener.onFailed(Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        } else listener.onFailed("Permission denied");
    }

    public static void searchLocation(Context context, String query, final Location location, final BCPlacesListener listener) {
        String text = query.replaceAll(" ", "+");

        String URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + text
                + "&location=" + location.getLatitude() + "," + location.getLongitude() + "&language=id&rankby=distance&region=id&key=" + context.getString(R.string.googleApiKey);

        getApiFromServer(context, URL, Request.Method.GET, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<Location> locations = new ArrayList<>();
                    for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                        JSONObject jsonObject = response.getJSONArray("results").getJSONObject(i);
                        Gson gson = new Gson();
                        BCSearchLocationResult result = gson.fromJson(jsonObject.toString(), BCSearchLocationResult.class);

                        Location location1 = new Location();
                        location1.setName(result.getName());
                        location1.setAddress(result.getFormatted_address());
                        location1.setLatitude(result.getGeometry().getLocation().getLat());
                        location1.setLongitude(result.getGeometry().getLocation().getLng());

                        locations.add(location1);
                    }
                    listener.onSuccess(locations);
                } catch (JSONException e) {
                    listener.onFailed(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFailed(error.getMessage());
            }
        });
    }

    private static void getApiFromServer(Context context, String URL, int method, Response.Listener listener, Response.ErrorListener error) {
        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(method, URL, null, listener, error);
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        requestQueue.add(jsonObjectRequest);
    }

}
