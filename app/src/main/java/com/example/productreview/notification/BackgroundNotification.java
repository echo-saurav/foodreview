package com.example.productreview.notification;

import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.productreview.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.productreview.MainActivity.TAG;

public class BackgroundNotification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title =remoteMessage.getData().get("title");
        String messages=remoteMessage.getData().get("messages");

        Log.d(TAG, "onMessageReceived: title"+title+" messages "+messages);
//        showNotification(title,messages);
    }

    void showNotification(String title, String messages) {
        Intent intent=new Intent(getApplicationContext(),Popup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title",title);
        intent.putExtra("message",messages);
        startActivity(intent);

//        Notification builder = new NotificationCompat.Builder(this, "Notification")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentTitle(title)
//                .setContentText(messages)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
//
//        // Issue the new notification.
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(3,builder);


    }
}
