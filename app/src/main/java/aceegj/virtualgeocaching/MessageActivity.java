package aceegj.virtualgeocaching;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class MessageActivity extends AppCompatActivity {
    private LinearLayout messageLinearLayout;
    private LatLng latLng;

    @Override
    protected void onResume() {
        super.onResume();
        messageLinearLayout.removeAllViews();
        for (GeocacheData.GeocacheMessage geocacheMessage : GeocacheData.getGeocacheData().messagesMap.get(latLng)) {
            addMessage(this, geocacheMessage.name, geocacheMessage.date, geocacheMessage.message, geocacheMessage.imageUri);
        }
        addMessage(this, " ", "", "", null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        latLng = (LatLng) getIntent().getExtras().get("LatLng");
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

        while (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    4
            );
        }

        messageLinearLayout = findViewById(R.id.message_layout);
        /*
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, "Joe Bruin", "1/1/1970", "Hello!", null);
        addMessage(this, "Josie Bruin", "3/3/1973", "Bye!", null);
        addMessage(this, " ", "", "", null);*/
    }

    private void addMessage(final Context context, final String name, final String date, final String message, @Nullable final Uri imageUri) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (layoutInflater == null) {
            return;
        }
        View messageView = layoutInflater.inflate(R.layout.geocache_message_layout, messageLinearLayout, false);
        final TextView nameTextView = messageView.findViewById(R.id.name_text_view);
        final TextView dateTextView = messageView.findViewById(R.id.date_text_view);
        final TextView messageTextView = messageView.findViewById(R.id.message_text_view);

        nameTextView.setText(name);
        dateTextView.setText(date);
        messageTextView.setText(message);
        if (imageUri != null) {
            final ImageView messageImageView = messageView.findViewById(R.id.message_image_view);
            messageImageView.setImageURI(imageUri);
        }
        messageLinearLayout.addView(messageView);
    }
}
