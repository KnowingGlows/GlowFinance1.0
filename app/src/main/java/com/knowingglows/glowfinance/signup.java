package com.knowingglows.glowfinance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signup extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore db;


    public static final int RC_SIGN_IN = 100;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        Map<String, Object> users = new HashMap<>();


        // Firebase link
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        AppCompatButton loginbtn = findViewById(R.id.signin_btn);
        AppCompatButton google_signupbtn = findViewById(R.id.google_signupbtn);



        // Google Sign In configuration
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        google_signupbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_animation);
                google_signupbtn.startAnimation(anim);
                Intent googleSignInClientSignInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(googleSignInClientSignInIntent, RC_SIGN_IN);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_animation);
                loginbtn.startAnimation(anim);
                startActivity(new Intent(signup.this, login.class));
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogle(signInAccount);
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleSignInAccount)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                {
                    @Override
                    public void onSuccess(AuthResult authResult)
                    {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (Objects.requireNonNull(authResult.getAdditionalUserInfo()).isNewUser())
                        {
                            Toast.makeText(signup.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, home.class));

                        }
                        else
                        {
                            Toast.makeText(signup.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, login.class));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(signup.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }
     @Override
    public void onStart()
    {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null)
        {
            startActivity(new Intent(signup.this, home.class));
        }
    }

}


