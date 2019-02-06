package com.example.jarvis.social;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private TextView userName;
    DatabaseReference rootref;
    FirebaseAuth auth;
    GoogleSignInOptions options;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Mypref", Context.MODE_PRIVATE);
        int i =sharedPref.getInt("token",2);
        if(i==1){
            Intent intent = new Intent(MainActivity.this,HomeScreen.class);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       rootref = FirebaseDatabase.getInstance().getReference();

       getSupportActionBar().setDisplayShowHomeEnabled(true);
       getSupportActionBar().setLogo(R.drawable.app_icon_logo);
       getSupportActionBar().setDisplayUseLogoEnabled(true);
       auth = FirebaseAuth.getInstance();
        TextView signup = (TextView) findViewById(R.id.signup_tv);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Button login = (Button) findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView email = (TextView) findViewById(R.id.email);
                TextView password = (TextView) findViewById(R.id.password);

                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(getApplicationContext(),"Enter email in correct format",Toast.LENGTH_LONG).show();
                }

                else if (!email.getText().toString().matches("") && !password.getText().toString().matches("")) {
                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Mypref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("token",1);
                                editor.commit();

                                Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                                intent.putExtra("Login",2);
                                startActivityForResult(intent,1);
                            } else {
                                Toast.makeText(getApplicationContext(), "Incorrect email and/or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"No field should be blank",Toast.LENGTH_SHORT).show();
                }
                }
        });

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();

        SignInButton signInButton = (SignInButton) findViewById(R.id.signinwithgoogle);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        if(requestCode == 1 && resultCode ==1){

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, options);
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                }
            });

        }
        if(requestCode == 1 && resultCode == 2){
            auth.signOut();
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

        }
    }

    private  void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            FirebaseUserAuth(account);
        }
    }
    public void FirebaseUserAuth(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task AuthResultTask) {
                        if (AuthResultTask.isSuccessful()){
                            final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.hasChild(auth.getCurrentUser().getUid())){
                                        ref.child("Users").child(auth.getCurrentUser().getUid()).child("First name").setValue(auth.getCurrentUser().getDisplayName());

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                  //          ref.child("com.example.jarvis.social.Users").child(auth.getCurrentUser().getUid()).child("Last name").setValue(lastname.getText().toString());

                            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("Mypref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt("token",1);
                            editor.commit();

                            Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                            intent.putExtra("Login",1);
                            startActivityForResult(intent,1);

                        }else {
                            Toast.makeText(MainActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}