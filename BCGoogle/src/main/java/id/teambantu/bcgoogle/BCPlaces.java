package id.teambantu.bcgoogle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import id.teambantu.bcgoogle.event.BCPlacesListener;
import id.teambantu.bcgoogle.model.Location;

public class BCPlaces {

    private final static String TAG = BCPlaces.class.getSimpleName();

    private PlacesClient client;
    private Context context;
    private Geocoder geocoder;

    public BCPlaces(Context context) {
        this.context = context;

        Places.initialize(this.context, context.getString(R.string.googleApiKey));
        this.client = Places.createClient(context);
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }

    public void getPlaces(final Location location, final BCPlacesListener listener) {
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

                                Location location1 = new Location();
                                location1.setName(places.get(0).getName());
                                location1.setAddress(places.get(0).getAddress());
                                location1.setLatitude(Objects.requireNonNull(places.get(0).getLatLng()).latitude);
                                location1.setLongitude(Objects.requireNonNull(places.get(0).getLatLng()).longitude);
                                listener.onSuccess(location1);
                            } else {
                                getGeocoderPlaces(location, listener);
                            }
                        } else {
                            getGeocoderPlaces(location, listener);
                        }
                    } else {
                        Log.d(TAG, "onComplete: " + task.getException());
                        getGeocoderPlaces(location, listener);
                    }
                }
            });
        } else listener.onFailed("Permission denied");
    }

    public void getGeocoderPlaces(Location location, BCPlacesListener listener) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            Location location1 = new Location();
            if(addresses.size()>0){
                location1.setAddress(addresses.get(0).getAddressLine(0));
                location1.setLongitude(addresses.get(0).getLongitude());
                location1.setLatitude(addresses.get(0).getLatitude());
                location1.setName(addresses.get(0).getAddressLine(0).split(",")[0]);
                listener.onSuccess(location1);
            } else {
                listener.onFailed("No such as location detected");
            }

        } catch (IOException e) {
            listener.onFailed(e.getMessage());
        }
    }
}
