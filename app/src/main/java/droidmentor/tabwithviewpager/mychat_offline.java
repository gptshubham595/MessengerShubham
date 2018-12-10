package droidmentor.tabwithviewpager;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


public class mychat_offline extends Application {
    private DatabaseReference userref;
    private FirebaseAuth mauth;
    private FirebaseUser currentuser;

    @Override
    public void onCreate(){
    super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //Picasso offline using loading picture
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mauth=FirebaseAuth.getInstance();
        currentuser=mauth.getCurrentUser();
        if(currentuser!=null){
            String online_user_id=mauth.getCurrentUser().getUid();
            userref=FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
            userref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
               userref.child("online").onDisconnect().setValue(false);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


}
