package id.teambantu.bcgoole;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import id.teambantu.bcgoogle.BCPlaces;
import id.teambantu.bcgoogle.event.BCPlacesListener;
import id.teambantu.bcgoogle.model.BCLocation;
import id.teambantu.bcgoogle.model.BCSearchLocationResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}