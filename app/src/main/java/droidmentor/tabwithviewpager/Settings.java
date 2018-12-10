package droidmentor.tabwithviewpager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Settings extends AppCompatActivity {
private CircleImageView img;
private StorageReference storageReference;
private TextView dispname,dispstatus,dispemail;
private Button chngimg,chngstatus;
private DatabaseReference getuser;
private ProgressDialog loadingbar;
private  StorageReference thumbimageref;
private FirebaseAuth mauth;
Bitmap thumb_bitmap=null;
private final static int Gallery=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mauth=FirebaseAuth.getInstance();
        String online_user_id=mauth.getCurrentUser().getUid();
        getuser= FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        getuser.keepSynced(true);
        storageReference= FirebaseStorage.getInstance().getReference().child("Profile_images");
        loadingbar=new ProgressDialog(this);
        thumbimageref=FirebaseStorage.getInstance().getReference().child("Thumb_images");

        img=(CircleImageView)findViewById(R.id.pic);
        dispname=(TextView)findViewById(R.id.username);
        dispemail=(TextView)findViewById(R.id.email);
        dispstatus=(TextView)findViewById(R.id.userstatus);
        chngimg=(Button)findViewById(R.id.changeimg);
        chngstatus=(Button)findViewById(R.id.changestatus);

        chngstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent b=new Intent(getApplicationContext(),Status.class);
                startActivity(b);
            }
        });

        getuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
           String name=dataSnapshot.child("user_name").getValue().toString();
                String email=dataSnapshot.child("user_email").getValue().toString();
                String status=dataSnapshot.child("user_status").getValue().toString();
              final  String image=dataSnapshot.child("user_image").getValue().toString();
                String thumbimg=dataSnapshot.child("user_thumb_image").getValue().toString();


                dispname.setText(name);
                dispstatus.setText(status);
                dispemail.setText(email);
                if(!image.equals("default_profile"))
                {//Picasso.get().load(image).placeholder(R.drawable.user).into(img);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user).into(img, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.user).into(img);
                        }
                    });
                     }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chngimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery=new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,Gallery);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode==Gallery && resultCode==RESULT_OK && data!=null)
            {

                Uri imageuri =data.getData();
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
            }

            if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
                {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK)
                    {   loadingbar.setTitle("updating");
                        loadingbar.setMessage("please wait");
                        loadingbar.show();

                        Uri resulturi = result.getUri();
                        File thumb_filepathuri=new File(resulturi.getPath());

                        String uid=mauth.getCurrentUser().getUid();
                        try{
                            thumb_bitmap=new Compressor(this).setMaxWidth(200).setMaxHeight(200).setQuality(50).compressToBitmap(thumb_filepathuri);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                        final byte[] thumb_byte=byteArrayOutputStream.toByteArray();

                       final StorageReference thumb_filePath =thumbimageref.child(uid+".jpg");

                        StorageReference filepath = storageReference.child(uid + ".jpg");

                        filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Saving Your Profile",Toast.LENGTH_LONG).show();

                                final String downloadurl=task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask=thumb_filePath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                   String thumb_downloadurl=thumb_task.getResult().getDownloadUrl().toString();
                                   if(thumb_task.isSuccessful()){

                                       Map update_user_data=new HashMap();
                                       update_user_data.put("user_image",downloadurl);
                                       update_user_data.put("user_thumb_image",thumb_downloadurl);

                                       getuser.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){Toast.makeText(getApplicationContext(),"Imgae updated!!",Toast.LENGTH_LONG).show(); }
                                               loadingbar.dismiss();
                                           }
                                       });
                                   }
                                    }
                                });



                             }
                             else{Toast.makeText(getApplicationContext(),"Error Occured while uploading",Toast.LENGTH_LONG).show(); loadingbar.dismiss();}
                            }
                        });
                    }
                    else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    {
                        Exception error = result.getError();
                    }
                }

                }
            }


