package droidmentor.tabwithviewpager.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import droidmentor.tabwithviewpager.R;
import droidmentor.tabwithviewpager.friends;

public class ContactsFragment extends Fragment {

    private RecyclerView myfriendlist;
    private DatabaseReference friendsref,usersref;
    private FirebaseAuth mauth;
    String online_user_id;
    private View myview;
    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myview=inflater.inflate(R.layout.fragment_contacts, container, false);
        myfriendlist=(RecyclerView)myview.findViewById(R.id.friendlist);
        mauth=FirebaseAuth.getInstance();
        usersref=FirebaseDatabase.getInstance().getReference().child("Users");
        online_user_id=mauth.getCurrentUser().getUid();
        friendsref=FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        myfriendlist.setLayoutManager(new LinearLayoutManager(getContext()));
        myfriendlist.setHasFixedSize(true);
        // Inflate the layout for this fragment
        return myview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<friends,FriendsViewHolder> friendsfirebaserecycleradapter =new FirebaseRecyclerAdapter<friends, FriendsViewHolder>(friends.class,R.layout.all_user_display,FriendsViewHolder.class,friendsref) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, friends model, int position) {
                viewHolder.setDate(model.getDate());
                String list_user_id =getRef(position).getKey();
                usersref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                   String userName =dataSnapshot.child("user_name").getValue().toString();
                   String thumb_image=dataSnapshot.child("user_thumb_image").getValue().toString();
                   viewHolder.setUserName(userName);
                   viewHolder.setThumbImage(thumb_image);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        myfriendlist.setAdapter(friendsfirebaserecycleradapter);
    }


public class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mview;
        public  FriendsViewHolder(View itemView)
        {
            super(itemView);
            mview=itemView;

        }



    public void setDate(String date) {
        TextView sincedate=(TextView)mview.findViewById(R.id.profile_visit_userstatus);
        sincedate.setText(date);
        }

    public void setThumbImage(final String thumbImage) {
        final CircleImageView thumb_image = (CircleImageView) mview.findViewById(R.id.all_user_profile);
        Picasso.get().load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(thumb_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(thumbImage).placeholder(R.drawable.user).into(thumb_image);
            }
        });
    }

    public void setUserName(String userName){
    TextView usernamedisplay=(TextView)mview.findViewById(R.id.profile_visit_username);
    usernamedisplay.setText(userName);
}

}


}
