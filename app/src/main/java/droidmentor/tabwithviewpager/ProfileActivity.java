package droidmentor.tabwithviewpager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {
private Button sendrequestbtn,declinebtn;
private TextView profile_name,profile_status;
private ImageView profile_image;
private DatabaseReference usersref,friendrequestref,friendsallref,notificationref;
private String current_state,sender_user_id;
private String reciever_visit_user_id;
private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sendrequestbtn=(Button)findViewById(R.id.profile_visit_send_request);
        declinebtn=(Button)findViewById(R.id.profile_visit_decline);
        profile_name=(TextView)findViewById(R.id.profile_visit_username);
        profile_status=(TextView)findViewById(R.id.profile_visit_userstatus);
        profile_image=(ImageView)findViewById(R.id.profile_visit_userprofile);

        current_state="not_friends";
        //creating one more child other than user
        friendrequestref=FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        friendrequestref.keepSynced(true);
        friendsallref=FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationref=FirebaseDatabase.getInstance().getReference().child("Notification");
        notificationref.keepSynced(true);
        friendsallref.keepSynced(true);
        mauth=FirebaseAuth.getInstance();
        sender_user_id=mauth.getCurrentUser().getUid();

        declinebtn.setVisibility(View.INVISIBLE);
        declinebtn.setEnabled(false);
        sendrequestbtn.setVisibility(View.INVISIBLE);
        sendrequestbtn.setEnabled(false);
        if(sender_user_id.equals(reciever_visit_user_id)){
            sendrequestbtn.setVisibility(View.INVISIBLE);
            sendrequestbtn.setEnabled(false);
            declinebtn.setVisibility(View.INVISIBLE);
            declinebtn.setEnabled(false);
        }
        else if(!sender_user_id.equals(reciever_visit_user_id))
        {sendrequestbtn.setVisibility(View.VISIBLE);
            sendrequestbtn.setEnabled(true);
            sendrequestbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendrequestbtn.setEnabled(false);
                    if(current_state.equals("not_friends")){sendFriendreqtofriend();}
                    if(current_state.equals("request_sent")){cancelfriendrequest();}
                    if(current_state.equals("request_recieved")){acceptfriendrequest();}
                    if(current_state.equals("friends")){unfriendafriend();}
                }
            });
        }
        else {
            declinebtn.setVisibility(View.INVISIBLE);
            declinebtn.setEnabled(false);
            sendrequestbtn.setVisibility(View.INVISIBLE);
            sendrequestbtn.setEnabled(false);
        }


        usersref=FirebaseDatabase.getInstance().getReference().child("Users");

         reciever_visit_user_id=getIntent().getExtras().get("visit_user_id").toString();

        usersref.child(reciever_visit_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Retrieve
                String name=dataSnapshot.child("user_name").getValue().toString();
                String status=dataSnapshot.child("user_status").getValue().toString();
                String image=dataSnapshot.child("user_image").getValue().toString();

                //setting
                profile_status.setText(status);
                profile_name.setText(name);
                Picasso.get().load(image).placeholder(R.drawable.user).into(profile_image);

                friendrequestref.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                         if(dataSnapshot.hasChild(reciever_visit_user_id))
                         {
                             String req_type=dataSnapshot.child(reciever_visit_user_id).child("request_type").getValue().toString();
                             if(req_type.equals("sent"))
                             {   current_state="request_sent";
                                 sendrequestbtn.setText("Cancel Request");
                                 declinebtn.setVisibility(View.INVISIBLE);
                                 declinebtn.setEnabled(false);

                             }
                             else if(req_type.equals("reciever"))
                             {
                                 current_state="request_recieved";
                                 sendrequestbtn.setText("Accept Request");
                                 declinebtn.setVisibility(View.VISIBLE);
                                 declinebtn.setEnabled(true);
                                 declinebtn.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         declinefriendrequest();
                                     }
                                 });

                             }
                         }


                     else{
                         friendsallref.child(sender_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 if(dataSnapshot.hasChild(reciever_visit_user_id)){
                                     current_state="friends";
                                     sendrequestbtn.setText("Unfriend");
                                     declinebtn.setVisibility(View.INVISIBLE);
                                     declinebtn.setEnabled(false);

                                 }
                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });
                     }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void declinefriendrequest() {
        friendrequestref.child(sender_user_id).child(reciever_visit_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    friendrequestref.child(reciever_visit_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                sendrequestbtn.setEnabled(true);
                                current_state="not_friends";
                                sendrequestbtn.setText("Send Request");
                                declinebtn.setVisibility(View.INVISIBLE);
                                declinebtn.setEnabled(false);

                            }
                        }
                    });
                }
            }
        });
    }

    private void unfriendafriend() {
        friendsallref.child(sender_user_id).child(reciever_visit_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful()){
               friendsallref.child(reciever_visit_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                  if(task.isSuccessful()){
                      sendrequestbtn.setEnabled(true);
                      current_state="not_friends";
                      sendrequestbtn.setText("Send Request");
                      declinebtn.setVisibility(View.INVISIBLE);
                      declinebtn.setEnabled(false);

                  }
                   }
               });
           }
            }
        });
    }

    private void acceptfriendrequest() {
        Calendar callforDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        final String savecurrentdate=currentDate.format(callforDate.getTime());
        friendsallref.child(sender_user_id).child(reciever_visit_user_id).child("date").setValue(savecurrentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            friendsallref.child(reciever_visit_user_id).child(sender_user_id).child("date").setValue(savecurrentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    friendrequestref.child(sender_user_id).child(reciever_visit_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendrequestref.child(reciever_visit_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            sendrequestbtn.setEnabled(true);
                                            current_state="friends";
                                            sendrequestbtn.setText("Unfriend This person");
                                            declinebtn.setEnabled(false);
                                            declinebtn.setVisibility(View.INVISIBLE);

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

    private void cancelfriendrequest()
    {
        friendrequestref.child(sender_user_id).child(reciever_visit_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
          if(task.isSuccessful()){
              friendrequestref.child(reciever_visit_user_id).child(sender_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                     sendrequestbtn.setEnabled(true);
                     current_state="not_friends";
                     sendrequestbtn.setText("Send Request");
                        declinebtn.setVisibility(View.INVISIBLE);
                        declinebtn.setEnabled(false);

                    }
                  }
              });
          }
            }
        });

    }

    private void sendFriendreqtofriend()
    {
    friendrequestref.child(sender_user_id).child(reciever_visit_user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
       if(task.isSuccessful()){ friendrequestref.child(reciever_visit_user_id).child(sender_user_id).child("request_type").setValue("reciever").addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

               if(task.isSuccessful())
               {
                   HashMap<String,String> notificationData=new HashMap<>();
                   notificationData.put("from",sender_user_id);
                   notificationData.put("type","request");
                   notificationref.child(reciever_visit_user_id).push().setValue(notificationData).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          sendrequestbtn.setEnabled(true);
                          current_state="request_sent";
                          sendrequestbtn.setText("Cancel Friend");
                          declinebtn.setVisibility(View.INVISIBLE);
                          declinebtn.setEnabled(false);
                      }
                       }
                   });



               }

           }
       });}
        }
    });
    }
}
