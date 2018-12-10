package droidmentor.tabwithviewpager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        String notification_title=remoteMessage.getNotification().getTitle();
        String notification_body=remoteMessage.getNotification().getBody();
        String from_sender_id=remoteMessage.getData().get("from_sender_id");
        String click_action=remoteMessage.getNotification().getClickAction();

        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("Shubham Messenger: "+notification_title).setContentText(notification_body);


        Intent result =new Intent(click_action);
        result.putExtra("visitor_user_id",from_sender_id);
        PendingIntent resultpendingintent = PendingIntent.getActivity(this,0,result, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultpendingintent);

        int mNotificationId=(int)System.currentTimeMillis();
        NotificationManager mnotifyMgr =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mnotifyMgr.notify(mNotificationId,mBuilder.build());

    }
}
