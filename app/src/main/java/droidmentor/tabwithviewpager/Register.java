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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register extends AppCompatActivity {

private FirebaseAuth mauth;
private DatabaseReference storeuserdata;
ProgressDialog load;

EditText e1,e2,e3;
Button b1;
ImageView b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mauth=FirebaseAuth.getInstance();
        e1=(EditText)findViewById(R.id.name);
        e2=(EditText)findViewById(R.id.email);
        e3=(EditText)findViewById(R.id.pass);
        b1=(Button) findViewById(R.id.create);

        load=new ProgressDialog(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 final String name=e1.getText().toString();
                String email=e2.getText().toString();
                String pass=e3.getText().toString();
                RegisterAccount(name,email,pass);
            }
        });

    }

    private void RegisterAccount(final String name, final String email, final String pass) {
        if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),"Please Write Your Name",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please Write Your Email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(),"Please Write Your Password",Toast.LENGTH_LONG).show();
        }
        else
        {
            load.setTitle("Creating Your New Account..");
            load.setMessage("Please Wait");
            load.show();
            mauth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   String Device_Token = FirebaseInstanceId.getInstance().getToken();

                   String Currentuid=mauth.getCurrentUser().getUid();
                   storeuserdata= FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuid);
                   storeuserdata.child("device_token").setValue(Device_Token);
                   storeuserdata.child("user_name").setValue(name);
                   storeuserdata.child("user_email").setValue(email);
                   storeuserdata.child("user_status").setValue("Hey!! I am using Shubham Messanger Developed by Shubham");
                   storeuserdata.child("user_image").setValue("default_profile");
                   storeuserdata.child("user_thumb_image").setValue("default_image").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){sendEmailVerification();
                          Toast.makeText(getApplicationContext(),"Registered!! Please verify Email Confirmation and Login",Toast.LENGTH_LONG).show();
                          Intent b=new Intent(getApplicationContext(),StartPage.class);
                          b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(b);
                          finish();}
                       }
                   });

               }
               else{

                   Toast.makeText(getApplicationContext(),"ERROR OCCURED TRY AGAIN!!",Toast.LENGTH_LONG).show();
               }
               load.dismiss();
                }
            });
        }
    }

    private void sendEmailVerification() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Check your Email", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                    }

                    else{
                        Toast.makeText(getApplicationContext(),"ERROR OCCURED TRY AGAIN!!",Toast.LENGTH_LONG).show();
                    }
                    }
            });
        }
    }

}
