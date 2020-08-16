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
import id.teambantu.bcgoogle.model.BCLocation;

public class BCPlaces {

    private final static String TAG = BCPlaces.class.getSimpleName();

    private static PlacesClient initiatePlaces(Context context){
        Places.initialize(context, context.getString(R.string.googleApiKey));
        return Places.createClient(context);
    }

    private static Geocoder initiateGeocode(Context context){
        return new Geocoder(context, Locale.getDefault());
    }

    public static void getPlaces(final Context context, final BCLocation location, final BCPlacesListener listener) {

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

                                BCLocation BCLocation1 = new BCLocation();
                                BCLocation1.setName(places.get(0).getName());
                                BCLocation1.setAddress(places.get(0).getAddress());
                                BCLocation1.setLatitude(Objects.requireNonNull(places.get(0).getLatLng()).latitude);
                                BCLocation1.setLongitude(Objects.requireNonNull(places.get(0).getLatLng()).longitude);
                                listener.onSuccess(BCLocation1);
                            } else {
                                getGeocoderPlaces(context, location, listener);
                            }
                        } else {
                            getGeocoderPlaces(context, location, listener);
                        }
                    } else {
                        Log.d(TAG, "onComplete: " + task.getException());
                        getGeocoderPlaces(context, location, listener);
                    }
                }
            });
        } else listener.onFailed("Permission denied");
    }

    public static void getGeocoderPlaces(Context context, BCLocation location, BCPlacesListener listener) {
        try {
            Geocoder geocoder = initiateGeocode(context);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            BCLocation BCLocation1 = new BCLocation();
            if(addresses.size()>0){
                BCLocation1.setAddress(addresses.get(0).getAddressLine(0));
                BCLocation1.setLongitude(addresses.get(0).getLongitude());
                BCLocation1.setLatitude(addresses.get(0).getLatitude());
                BCLocation1.setName(addresses.get(0).getAddressLine(0).split(",")[0]);
                listener.onSuccess(BCLocation1);
            } else {
                listener.onFailed("No such as location detected");
            }

        } catch (IOException e) {
            listener.onFailed(e.getMessage());
        }
    }
}
