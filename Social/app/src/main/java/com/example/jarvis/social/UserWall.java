package com.example.jarvis.social;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

public class UserWall extends AppCompatActivity {

    FirebaseAuth auth;
    Toolbar editToolbar;
    ImageView friends_image, home_image;
    EditText post_text;
    Button post_image;
    TextView username;
    String user;
    ArrayList<Post> posts;
    ArrayList<Users> friendslist;
    DatabaseReference root_reference;
    TextView poststitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_user_wall);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_icon_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        editToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        friends_image = (ImageView) findViewById(R.id.toolbar_image);
        username = (TextView) findViewById(R.id.toolbar_text);
        root_reference = FirebaseDatabase.getInstance().getReference();
        poststitle = (TextView) findViewById(R.id.titleposts);
        home_image = (ImageView) findViewById(R.id.toolbar_image2);
        posts = new ArrayList<>();
        friendslist = new ArrayList<>();
     /*   friends_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserWall.this, Friendslist.class);
                startActivity(intent);
            }
        });
*/
        root_reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = new Users();
                users.setUid(dataSnapshot.getKey());
                users.setFirstname(dataSnapshot.child("First name").getValue(String.class));
                friendslist.add(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        root_reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    for (int i = 0; i < friendslist.size(); i++) {
                        if (x.child("UID").getValue(String.class).matches(friendslist.get(i).getUid())) {
                            Post post = new Post();
                            post.setMessage(x.child("Message").getValue(String.class));
                            post.setUser(x.child("Name").getValue(String.class));
                            post.setUid(x.child("UID").getValue(String.class));
                            PrettyTime p = new PrettyTime();
                            String pattern = "yyyy-MM-dd HH:mm:ss";
                            java.util.Date date = null;
                            try {
                                DateFormat dateFormat = new SimpleDateFormat(pattern);
                                dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
                                date = dateFormat.parse(x.child("Time").getValue(String.class));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            post.setTime(p.format(date));

                            posts.add(post);
                        }
                    }

                }

                RecyclerView rcv = (RecyclerView) findViewById(R.id.rcv);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(UserWall.this);
                rcv.setLayoutManager(mLayoutManager);
                Collections.reverse(posts);
                UserWallAdapter adapter = new UserWallAdapter(posts, UserWall.this);
                rcv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        root_reference.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.child("First name").getValue(String.class);
                username.setText(user);
                poststitle.setText("My Posts");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        home_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserWall.this,HomeScreen.class);
                startActivity(intent);
            }
        });

        friends_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserWall.this,EditProfile.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Mypref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("token", 0);
                editor.commit();

                setResult(getIntent().getIntExtra("Login", 0));
                Intent intent = new Intent(UserWall.this,MainActivity.class);
                finishAffinity();

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserWall.this,HomeScreen.class);
        startActivity(intent);
        finish();
    }
}
