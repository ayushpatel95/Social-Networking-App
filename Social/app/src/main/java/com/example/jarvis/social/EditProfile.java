package com.example.jarvis.social;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditProfile extends AppCompatActivity {

    Toolbar toolbar;
    EditText firstname,lastname,dateofbirth;
    ImageView editfirst,editlast,editdate , home;
    Button save,cancel;
    TextView username;
    FirebaseAuth auth;
    String date;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_icon_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastnameed);
        dateofbirth = (EditText) findViewById(R.id.dateed);
        editfirst = (ImageView) findViewById(R.id.editfirst);
        editlast = (ImageView) findViewById(R.id.editsecond);
        editdate = (ImageView) findViewById(R.id.editthird);
        home= (ImageView) findViewById(R.id.toolbar_image);
        username = (TextView) findViewById(R.id.toolbar_text);
        final String user;
        String first;
        String last;

        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        firstname.setEnabled(false);
        lastname.setEnabled(false);
        dateofbirth.setEnabled(false);
        reference.child("Users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user = dataSnapshot.child("First name").getValue(String.class);
                username.setText(user);
                String[] name = user.split(" ");
                firstname.setText(name[0]);
                lastname.setText(name[1]);
                String dob = dataSnapshot.child("DOB").getValue(String.class);
                dateofbirth.setText(dob);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        editfirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname.setText("");
                firstname.setEnabled(true);
            }
        });
        editlast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastname.setText("");
                lastname.setEnabled(true);
            }
        });

        editdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateofbirth.setEnabled(true);
                dateofbirth.setText("");
            }
        });

        final int day = 0,month=0,year=0;
        final DatePickerDialog datePickerDialog=new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        Calendar userAge = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                        Calendar minAdultAge = new GregorianCalendar();
                        minAdultAge.add(Calendar.YEAR, -13);
                        if (minAdultAge.before(userAge)) {
                            Toast.makeText(getApplicationContext(),"You must be atleast 13 years old",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            int m= monthOfYear+1;
                            date=m +" / " +dayOfMonth +" / " +year;
                            dateofbirth.setText(date);
                        }


                    }

                },year,month,day);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfile.this,HomeScreen.class);
                finishAffinity();
                startActivity(intent);
            }
        });
       dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog.updateDate(mYear,mMonth,mDay);
                datePickerDialog.show();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = firstname.getText().toString() + " " + lastname.getText().toString();
                if(!firstname.getText().toString().matches("") && !lastname.getText().toString().matches("") && !dateofbirth.getText().toString().matches(""))
                {
                    reference.child("Users").child(auth.getCurrentUser().getUid()).child("First name").setValue(name);
                    reference.child("Users").child(auth.getCurrentUser().getUid()).child("DOB").setValue(dateofbirth.getText().toString());

                    reference.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void
                        onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot x: dataSnapshot.getChildren()){
                                if(x.child("UID").getValue(String.class).matches(auth.getCurrentUser().getUid())){
                                    reference.child("Posts").child(x.getKey()).child("Name").setValue(name);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    reference.child("People").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot x: dataSnapshot.getChildren()){
                                if(x.hasChild("Requests") && x.child("Requests").hasChild(auth.getCurrentUser().getUid())){
                                    reference.child("People").child(x.getKey()).child("Requests").child(auth.getCurrentUser().getUid()).setValue(name);
                                }
                                if(x.hasChild("Sent") && x.child("Sent").hasChild(auth.getCurrentUser().getUid())){
                                    reference.child("People").child(x.getKey()).child("Sent").child(auth.getCurrentUser().getUid()).setValue(name);
                                }
                                if(x.hasChild("Friends") && x.child("Friends").hasChild(auth.getCurrentUser().getUid())){
                                    reference.child("People").child(x.getKey()).child("Friends").child(auth.getCurrentUser().getUid()).setValue(name);
                                }
                            }

                            }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(getApplicationContext(),"User Saved",Toast.LENGTH_SHORT).show();
                    firstname.setEnabled(false);
                    username.setText(name);
                    lastname.setEnabled(false);
                    dateofbirth.setEnabled(false);

                    //finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Dont leave anything blank",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
