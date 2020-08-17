package id.teambantu.bcgoole;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import id.teambantu.bcgoogle.BCPlaces;
import id.teambantu.bcgoogle.event.BCPlacesListener;
import id.teambantu.bcmodel.helper.Location;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        BCPlaces.getAddress(this, new Location(3.562499, 98.659535), new BCPlacesListener() {
//            @Override
//            public void onSuccess(List<Location> locations) {
//                Log.d("TAG", "onSuccess: " + locations);
//            }
//
//            @Override
//            public void onFailed(String message) {
//
//            }
//        });

        BCPlaces.nearbyLocation(this, new Location(3.562788, 98.659356), new BCPlacesListener() {
            @Override
            public void onSuccess(List<Location> locations) {
                for (Location loc:
                     locations) {
                    Log.d("TAG", "onSuccess: " + loc.getName());
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });

    }
}