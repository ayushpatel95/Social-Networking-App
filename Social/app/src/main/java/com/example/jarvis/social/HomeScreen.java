package com.example.jarvis.social;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class HomeScreen extends AppCompatActivity {

    FirebaseAuth auth;
    Toolbar editToolbar;
    ImageView friends_image;
    EditText post_text;
    Button post_image;
    TextView username;
    String user;
    ArrayList<Post> posts;
    ArrayList<Users> friendslist;
    DatabaseReference root_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_icon_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_home_screen);
        editToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        post_text = (EditText) findViewById(R.id.posted);
        post_image = (Button) findViewById(R.id.postimage);
        friends_image = (ImageView) findViewById(R.id.toolbar_image);
        username = (TextView) findViewById(R.id.toolbar_text);
        root_reference = FirebaseDatabase.getInstance().getReference();
        posts = new ArrayList<>();
        friendslist = new ArrayList<>();
        friends_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, Friendslist.class);
                finish();
                startActivity(intent);
            }
        });

        root_reference.child("People").child(auth.getCurrentUser().getUid()).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    Users users = new Users();
                    users.setUid(x.getKey());
                    users.setFirstname(x.getValue(String.class));
                    friendslist.add(users);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

            /*    for(int i=0;i<friendslist.size();i++){
                    root_reference.child("Posts").child(friendslist.get(i).getUid()).child()
                }
            */
                RecyclerView rcv = (RecyclerView) findViewById(R.id.rcv);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(HomeScreen.this);
                rcv.setLayoutManager(mLayoutManager);
                Collections.reverse(posts);
                MyAdapter adapter = new MyAdapter(posts, HomeScreen.this);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,UserWall.class);
                startActivity(intent);
            }
        });

        post_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                String post_message = post_text.getText().toString();
                post_text.setText("");
                Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String formattedDate = df.format(c.getTime());

                if (!post_message.trim().matches("")) {
                    root_reference.child("Posts").child(formattedDate).child("Name").setValue(user);
                    root_reference.child("Posts").child(formattedDate).child("Message").setValue(post_message.trim());
                    root_reference.child("Posts").child(formattedDate).child("Time").setValue(formattedDate);
                    root_reference.child("Posts").child(formattedDate).child("UID").setValue(auth.getCurrentUser().getUid());

                    posts = new ArrayList<>();
                    friendslist = new ArrayList<>();
                    root_reference.child("People").child(auth.getCurrentUser().getUid()).child("Friends").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot x : dataSnapshot.getChildren()) {
                                Users users = new Users();
                                users.setUid(x.getKey());
                                users.setFirstname(x.getValue(String.class));
                                friendslist.add(users);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

            /*    for(int i=0;i<friendslist.size();i++){
                    root_reference.child("Posts").child(friendslist.get(i).getUid()).child()
                }
            */
                            RecyclerView rcv = (RecyclerView) findViewById(R.id.rcv);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(HomeScreen.this);
                            rcv.setLayoutManager(mLayoutManager);
                            Collections.reverse(posts);
                            MyAdapter adapter = new MyAdapter(posts, HomeScreen.this);
                            rcv.setAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(HomeScreen.this, "Please enter something to post", Toast.LENGTH_SHORT).show();
                }
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
