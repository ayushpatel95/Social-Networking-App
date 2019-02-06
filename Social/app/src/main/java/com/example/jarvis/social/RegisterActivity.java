package com.example.jarvis.social;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PatternMatcher;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RegisterActivity extends AppCompatActivity {

    String date;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.app_icon_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        final EditText firstname = (EditText) findViewById(R.id.firstname);
        final EditText lastname = (EditText) findViewById(R.id.lastname);
        final EditText email = (EditText) findViewById(R.id.emailed);
        final EditText password = (EditText) findViewById(R.id.passworded);
        final EditText repeat = (EditText) findViewById(R.id.repeatpassworded);
        final EditText birthday = (EditText) findViewById(R.id.dateed);

        Button signup = (Button) findViewById(R.id.signupbutton);
        Button cancel = (Button) findViewById(R.id.cancelbutton2);

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
                            birthday.setText(date);
                        }


                    }

                },year,month,day);



        birthday.setOnClickListener(new View.OnClickListener() {
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


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date!=null && !firstname.getText().toString().matches("") && !lastname.getText().toString().matches("") && !email.getText().toString().matches("") && !password.getText().toString().matches("") && !repeat.getText().toString().matches("")){
                    if(!password.getText().toString().matches(repeat.getText().toString())){
                        Toast.makeText(RegisterActivity.this, "Password and Repeat password should be same", Toast.LENGTH_SHORT).show();
                    }
                    else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                        Toast.makeText(getApplicationContext(),"enter Email in correct format", Toast.LENGTH_LONG).show();
                    }
                    else if(password.length()<6){
                        Toast.makeText(getApplicationContext(),"Passwords should be greater than 6", Toast.LENGTH_LONG).show();
                    }
                    else{
                        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_SHORT).show();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                    ref.child("Users").child(auth.getCurrentUser().getUid()).child("First name").setValue(firstname.getText().toString() +" " +lastname.getText().toString());
                                    ref.child("Users").child(auth.getCurrentUser().getUid()).child("DOB").setValue(date);

                                    auth.signOut();
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Registration failure : " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No field should be blank",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode ==2){
            auth.signOut();
            finish();
        }
    }
}
