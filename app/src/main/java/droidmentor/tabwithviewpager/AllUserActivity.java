package droidmentor.tabwithviewpager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import droidmentor.tabwithviewpager.ViewPager.TabWithIconActivity;

public class AllUserActivity extends AppCompatActivity {
    private RecyclerView alluserlist;
    private DatabaseReference alluserdatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluseractivity);
        alluserlist=(RecyclerView) findViewById(R.id.recycler);
        alluserlist.setHasFixedSize(true);
        alluserlist.setLayoutManager(new LinearLayoutManager(this));
        alluserdatabaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
        alluserdatabaseReference.keepSynced(true); //for offline
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<allusers, Alluserviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<allusers, Alluserviewholder>(
                allusers.class,R.layout.all_user_display,Alluserviewholder.class,alluserdatabaseReference
        ) {
            @Override
            protected void populateViewHolder(Alluserviewholder viewHolder, allusers model, final int position) {
                viewHolder.setUser_name(model.getUser_name());
                viewHolder.setUser_status(model.getUser_status());
                viewHolder.setUser_thumb_image(model.getUser_thumb_image());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id=getRef(position).getKey();
                        Intent k=new Intent(getApplicationContext(),ProfileActivity.class);
                        k.putExtra("visit_user_id",visit_user_id);
                        startActivity(k);
                    }
                });
            }
        };
        alluserlist.setAdapter(firebaseRecyclerAdapter);
    }
    public static class Alluserviewholder extends RecyclerView.ViewHolder{
        View mview;
        public Alluserviewholder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setUser_name(String user_name){
            TextView name = (TextView) mview.findViewById(R.id.usernameofall);
            name.setText(user_name);
        }
        public void setUser_thumb_image(final String user_thumb_image){
          final CircleImageView thumb_image = (CircleImageView) mview.findViewById(R.id.all_user_profile);
            Picasso.get().load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(thumb_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(user_thumb_image).placeholder(R.drawable.user).into(thumb_image);
                }
            });
        }
        public void setUser_status(String user_status){
            TextView status;
            status = (TextView) mview.findViewById(R.id.userstatusofall);
            status.setText(user_status);
        }
    }

}