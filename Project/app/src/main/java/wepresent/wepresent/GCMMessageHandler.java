package wepresent.wepresent;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMMessageHandler extends IntentService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    public GCMMessageHandler() {
        super("GCMMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve data extras from push notification
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        // Keys in the data are shown as extras
        String title = extras.getString("title");
        String body = extras.getString("body");
        // Create notification or otherwise manage incoming push
        createNotification(56, R.drawable.notification_icon, title, body);
        // Notify receiver the intent is completed
        GCMBroadcastReceiver.completeWakefulIntent(intent);

        //TODO: Go to quiz question stuff and get question enzo
    }

    // Creates notification based on title and body received
    private void createNotification(int nId, int iconRes, String title, String body) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.notification_icon);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(body)
                .setLargeIcon(bm);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(nId, mBuilder.build());
    }

}