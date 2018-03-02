package aceegj.virtualgeocaching;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final LatLng latLng = (LatLng) getIntent().getExtras().get("LatLng");
        final String title = "(" + latLng.latitude + ", " + latLng.longitude + ")";
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, AddActivity.class);
                intent.putExtra("LatLng", latLng);
                startActivity(intent);
            }
        });
    }
}
