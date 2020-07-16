package com.shubham.rollout;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {

    private RecyclerView mReqList;

    private DatabaseReference friendReqDatabase;
    private DatabaseReference mfriendsDatabase;
    private DatabaseReference mfriendsreqDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mReqList = mMainView.findViewById(R.id.req_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        friendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mfriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mfriendsreqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mReqList.setHasFixedSize(true);
        mReqList.setLayoutManager(linearLayoutManager);
        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(
                Requests.class,
                R.layout.friendreq,
                RequestsFragment.RequestViewHolder.class,
                friendReqDatabase
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Requests model, final int position) {
                final String list_user_id = getRef(position).getKey();
                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String request_type = dataSnapshot.getValue().toString();
                            if (request_type.equals("sent")) {
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                        viewHolder.setSUserName(userName);
                                        viewHolder.setUserStatus(userStatus);
                                        viewHolder.setThumbImage(userThumb, getContext());
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence[] options = new CharSequence[]{"Cancel Friend Request"};

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

                                                builder.setTitle("Friend Options");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        //Click Event for each item.

                                                        if (i == 0) {
                                                            mfriendsreqDatabase.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mfriendsreqDatabase.child(list_user_id).child(mCurrent_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(getContext(), "Friend Request Deleted", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }

                                                    }
                                                });

                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else if (request_type.equals("received")) {
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                                        viewHolder.setUserName(userName);
                                        viewHolder.setUserStatus(userStatus);
                                        viewHolder.setThumbImage(userThumb, getContext());
                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence[] options = new CharSequence[]{"Accept Friend Request", "Cancel Friend Request"};

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));

                                                builder.setTitle("Friend Options");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        //Click Event for each item.
                                                        if (i == 0) {
                                                            Calendar callforDate = Calendar.getInstance();
                                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String savecurrentdate = currentDate.format(callforDate.getTime());
                                                            mfriendsDatabase.child(mCurrent_user_id).child(list_user_id).child("date").setValue(savecurrentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mfriendsDatabase.child(list_user_id).child(mCurrent_user_id).child("date").setValue(savecurrentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            mfriendsreqDatabase.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        mfriendsreqDatabase.child(list_user_id).child(mCurrent_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext(), "Friend Request Accepted", Toast.LENGTH_SHORT).show();

                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });


                                                        }

                                                        if (i == 1) {
                                                            mfriendsreqDatabase.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mfriendsreqDatabase.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    Toast.makeText(getContext(), "Friend Request Declined", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });


                                                        }

                                                    }
                                                });

                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mReqList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setUserName(String userName) {
            TextView user_name = mView.findViewById(R.id.display_user_name);
            user_name.setText(userName);
        }

        public void setSUserName(String userName) {
            TextView user_name = mView.findViewById(R.id.display_user_name);
            user_name.setText("Sent To :" + userName);
        }

        public void setUserStatus(String userStatus) {
            TextView user_status = mView.findViewById(R.id.display_user_status);
            user_status.setText(userStatus);
        }

        public void setThumbImage(final String userThumb, final Context ctx) {
            final CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.display_user_profile);
            Picasso.with(ctx).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar).into(userImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(userThumb).placeholder(R.drawable.default_avatar).into(userImageView);
                }
            });
        }


    }

}

