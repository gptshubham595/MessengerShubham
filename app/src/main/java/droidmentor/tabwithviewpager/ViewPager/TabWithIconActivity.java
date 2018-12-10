package droidmentor.tabwithviewpager.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import droidmentor.tabwithviewpager.About;
import droidmentor.tabwithviewpager.AllUserActivity;
import droidmentor.tabwithviewpager.Fragment.CallsFragment;
import droidmentor.tabwithviewpager.Fragment.ChatFragment;
import droidmentor.tabwithviewpager.Fragment.ContactsFragment;
import droidmentor.tabwithviewpager.R;
import droidmentor.tabwithviewpager.Settings;
import droidmentor.tabwithviewpager.StartPage;
import droidmentor.tabwithviewpager.Status;
import droidmentor.tabwithviewpager.ViewPagerAdapter;

public class TabWithIconActivity extends AppCompatActivity {

    //This is our tablayout
    private TabLayout tabLayout;
    private FirebaseAuth mauth;
     FirebaseUser cu;
     private DatabaseReference usersref;

    //This is our viewPager
    private ViewPager viewPager;

    ViewPagerAdapter adapter;

    //Fragments

    ChatFragment chatFragment;
    CallsFragment callsFragment;
    ContactsFragment contactsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_with_icon);
        mauth=FirebaseAuth.getInstance();
        cu=mauth.getCurrentUser();
        if(cu!=null){
            String online_user_id=mauth.getCurrentUser().getUid();
            usersref=FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        }
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tablayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }
    @Override
    protected void onStart() {
        super.onStart();
        cu=mauth.getCurrentUser();
        if(cu == null){
            Logout();
        }
        else if(cu !=null){
            usersref.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(cu !=null){
            usersref.child("online").setValue(false);
        }
    }

    private void Logout() {
        Intent i = new Intent(getApplicationContext(),StartPage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        // Associate searchable configuration with the SearchView
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_alluser:
                Toast.makeText(this, "All User Clicked", Toast.LENGTH_SHORT).show();
                Intent i2 =new Intent(getApplicationContext(), AllUserActivity.class);
                startActivity(i2);
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Home Settings Clicked", Toast.LENGTH_SHORT).show();
                Intent i =new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
                return true;
            case R.id.action_search:
                Toast.makeText(this, "Search ", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                mauth.signOut();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                Logout();
                return true;
            case R.id.about:
                Intent a =new Intent(getApplicationContext(), About.class);
                startActivity(a);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        callsFragment=new CallsFragment();
        chatFragment=new ChatFragment();
        contactsFragment=new ContactsFragment();
        adapter.addFragment(callsFragment,"REQUESTS");
        adapter.addFragment(chatFragment,"CHATS");
        adapter.addFragment(contactsFragment,"FRIENDS");
        viewPager.setAdapter(adapter);
    }

}
