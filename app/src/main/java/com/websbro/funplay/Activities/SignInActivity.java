package com.websbro.funplay.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.websbro.funplay.R;
import com.websbro.funplay.User;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String watching = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 6);
    }

    private void updateUi(){
        Intent i = new Intent(SignInActivity.this,HomeActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.sign_in_button){
            signIn();
        }else if(v.getId() == R.id.continue_without_login){
            updateUi();
        }
    }

    public void withOutLogin(View view){
        updateUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final CollectionReference users = db.collection("Users");




        if(currentUser!=null){
            final ArrayList<String> recentTvShows = new ArrayList<>();
            DocumentReference documentReference =users.document(currentUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            ArrayList<String> temp = (ArrayList<String>)documentSnapshot.get("recentTvShows");
                            watching = documentSnapshot.getString("watching");
                            if(temp!=null) {
                                recentTvShows.addAll(temp);
                            }
                        }

                        User user = new User(currentUser.getEmail(),currentUser.getDisplayName(),
                                currentUser.getPhotoUrl().toString(),watching,new ArrayList<String>(),new ArrayList<String>(),recentTvShows);
                        users.document(currentUser.getUid()).set(user);
                    }
                }
            });

            updateUi();



        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 6) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account!=null){
                    firebaseAuthWithGoogle(account);
                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show();
                Log.w("d", "Google sign in failed", e);
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            final CollectionReference users = db.collection("Users");
                            if(currentUser!=null){
                                final ArrayList<String> recentTvShows = new ArrayList<>();
                                DocumentReference documentReference =users.document(currentUser.getUid());
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            System.out.println(documentSnapshot);
                                            if(documentSnapshot.exists()){
                                                ArrayList<String> temp = (ArrayList<String>)documentSnapshot.get("recentTvShows");
                                                if(temp!=null) {
                                                    recentTvShows.addAll(temp);
                                                }
                                            }
                                        }
                                        User user = new User(currentUser.getEmail(),currentUser.getDisplayName(),
                                                currentUser.getPhotoUrl().toString(),watching,new ArrayList<String>(),new ArrayList<String>(),recentTvShows);
                                        users.document(currentUser.getUid()).set(user);
                                        updateUi();
                                    }
                                });


                            }
                            updateUi();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("d", "signInWithCredential:failure", task.getException());
                            System.out.println("login failed");
                        }

                        // ...
                    }
                });
    }


}
