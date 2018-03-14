package aceegj.virtualgeocaching;

import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by elton on 3/12/2018.
 */

public class GeocacheData {
    private static GeocacheData geocacheData;

    public HashMap<LatLng, ArrayList<GeocacheMessage>> messagesMap; // accessors are for chumps
    public static class GeocacheMessage {
        public String name;
        public String date;
        public String message;
        public Uri imageUri;

        public GeocacheMessage(final String name, final String date, final String message, @Nullable final Uri imageUri) {
            this.name = name;
            this.date = date;
            this.message = message;
            this.imageUri = imageUri;
        }
    }

    private GeocacheData() {
        messagesMap = new HashMap<>();
    }

    public static GeocacheData getGeocacheData() {
        if (geocacheData == null) {
            geocacheData = new GeocacheData();
            geocacheData.messagesMap.put(new LatLng(34.068921, -118.4473698), new ArrayList<GeocacheMessage>());
            ArrayList<GeocacheMessage> geocacheMessages = geocacheData.messagesMap.get(new LatLng(34.068921, -118.4473698));
            geocacheMessages.add(new GeocacheMessage("All Star", "1999/05/04", "Somebody once told me the world was gonna roll me.", null));
            geocacheMessages.add(new GeocacheMessage("All Star", "1999/05/04", "I ain't the sharpest tool in the shed.", null));
            geocacheMessages = new ArrayList<GeocacheMessage>();
            geocacheData.messagesMap.put(new LatLng(34.0704005, -118.4505021), geocacheMessages);
            geocacheMessages.add(new GeocacheMessage("Smash Mouth", "2018/03/12", "I ain't the sharpest tool in the shed", null));
        }
        return geocacheData;
    }

    public static float distance(LatLng latLng1, LatLng latLng2)  {
        float[] results = new float[1];
        Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results);
        return results[0];
    }
}