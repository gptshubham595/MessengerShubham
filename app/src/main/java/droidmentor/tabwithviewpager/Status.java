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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Status extends AppCompatActivity {
Button b1;
DatabaseReference changestatusref;
FirebaseAuth mauth;
    ProgressDialog load;

EditText e1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        b1=(Button)findViewById(R.id.changestatus);
        load=new ProgressDialog(this);
        mauth=FirebaseAuth.getInstance();
        String uid=mauth.getCurrentUser().getUid();
        changestatusref= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        e1=(EditText)findViewById(R.id.statusnew);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = e1.getText().toString();
                changeprofilestatus(status);
            }
        });
    }

    private void changeprofilestatus(String status) {load.setTitle("Changing Status");
        load.setMessage("Please Wait");
        load.show();
if(TextUtils.isEmpty(status)){
    Toast.makeText(getApplicationContext(),"Please write your status",Toast.LENGTH_LONG).show();
}
else{
    changestatusref.child("user_status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
       if(task.isSuccessful()){Toast.makeText(getApplicationContext(),"Done!!",Toast.LENGTH_LONG).show();
           load.dismiss();
           Intent a=new Intent(getApplicationContext(),Settings.class);
           startActivity(a);
       }
       else {Toast.makeText(getApplicationContext(),"Error Occured!! TRY AGAIN",Toast.LENGTH_LONG).show();}
        }
    });

}
    }
}
