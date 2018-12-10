package droidmentor.tabwithviewpager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import droidmentor.tabwithviewpager.ViewPager.TabWithIconActivity;

public class Welcome extends AppCompatActivity {
    Thread a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        a=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                }
                catch (Exception e){e.printStackTrace();}
                finally {
                    Intent a=new Intent(getApplicationContext(),TabWithIconActivity.class);
                    startActivity(a);

                }
            }
        };
        a.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
