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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import droidmentor.tabwithviewpager.ViewPager.TabWithIconActivity;

public class Login extends AppCompatActivity {
ImageView b2;
    ProgressDialog load;
    private FirebaseAuth mauth;
private DatabaseReference userref;
EditText e1,e2;
Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mauth=FirebaseAuth.getInstance();
        userref=FirebaseDatabase.getInstance().getReference().child("Users");
        userref.keepSynced(true);

        e1=(EditText)findViewById(R.id.email);
        e2=(EditText)findViewById(R.id.pass);
        b1=(Button)findViewById(R.id.login);
        load=new ProgressDialog(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=e1.getText().toString();
                String pass=e2.getText().toString();
                LoginAccount(email,pass);
            }
        });
    }

    private void LoginAccount(String email, String pass) {
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please Write Your Email",Toast.LENGTH_LONG).show();}
        if(TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(),"Please Write Your Password",Toast.LENGTH_LONG).show();}
        else
        {
            load.setTitle("Loging To Your Account..");
            load.setMessage("Please Wait");
            load.show();
            mauth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String online_user_id=mauth.getCurrentUser().getUid();
                        String Device_Token = FirebaseInstanceId.getInstance().getToken();

                        userref.child(online_user_id).child("device_token").setValue(Device_Token).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Logged In!!",Toast.LENGTH_LONG).show();
                                Intent b=new Intent(getApplicationContext(),TabWithIconActivity.class);
                                b.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(b);
                                finish();
                            }
                        });


                    }
                    else{
                        Toast.makeText(getApplicationContext(),"ERROR OCCURED TRY AGAIN!!",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"Either Password or Email is wrong..",Toast.LENGTH_LONG).show();
                    }
                    load.dismiss();
                }
            });
                    }
    }
}
