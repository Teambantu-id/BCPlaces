package id.teambantu.bcgoole;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import id.teambantu.bcgoogle.BCPlaces;
import id.teambantu.bcgoogle.event.BCPlacesListener;

import static id.teambantu.bcgoogle.BCPlaces.getAddress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAddress(this, new BCPlacesListener() {
            @Override
            public void onFailed(String message) {

            }
        });

    }
}