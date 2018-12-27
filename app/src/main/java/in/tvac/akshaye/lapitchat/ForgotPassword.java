package in.tvac.akshaye.lapitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class
ForgotPassword extends AppCompatActivity {

Button forgot;
EditText forgotpass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        forgot=findViewById(R.id.forgotbtn);
        forgotpass=findViewById(R.id.email);
        mAuth=FirebaseAuth.getInstance();
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotpass.getText().toString();
                if(!TextUtils.isEmpty(email)){
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Check Your mail and reset your password",Toast.LENGTH_LONG).show();
                                Intent a =new Intent(getApplicationContext(),StartActivity.class);
                                a.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(a);
                            }
                        }
                    });
                }
                else{Toast.makeText(getApplicationContext(),"Please enter a email..",Toast.LENGTH_SHORT).show();}
            }
        });
    }
}
