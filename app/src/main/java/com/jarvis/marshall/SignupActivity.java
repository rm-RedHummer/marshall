package com.jarvis.marshall;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jarvis.marshall.dataAccess.UserDA;
import com.jarvis.marshall.model.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private FirebaseAuth mAuth;
    private UserDA userDA;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @BindView(R.id.input_name) EditText nameText;
    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.btn_signup) Button signupButton;
    @BindView(R.id.link_login) TextView loginLink;
    @BindView(R.id.signup_visibilityBtn) ImageButton viewButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        userDA = new UserDA();


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
        viewButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    passwordText.setTransformationMethod(null);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    passwordText.setTransformationMethod(new PasswordTransformationMethod());
                return false;
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");
        boolean validation = validate();
        if (validation==false) {
            onSignupFailed();
            return;
        }
        Snackbar.make(findViewById(R.id.signup_scrollview), "Validation Successful", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            signupButton.setEnabled(false);

            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                    R.style.AppTheme_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Account...");
            progressDialog.show();

            final String name = nameText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();



            // TODO: Implement your own signup logic here.

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                onSignupSuccess(name,progressDialog);

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                onSignupFailed();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
    }


    public void onSignupSuccess(final String name,ProgressDialog progressDialog) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        firebaseUser.updateProfile(profileUpdates);
        User user = new User(mAuth.getCurrentUser().getUid(),name);
        userDA.createNewUser(user);
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
        finish();
    }

    /*@Override
    public void onStop(){
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }*/

    public void onSignupFailed() {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);
        builder1.setMessage("Sorry, there is a problem in creating your account.");
        AlertDialog alert11 = builder1.create();
        alert11.show();
        signupButton.setEnabled(true);
    }



    public boolean validate() {
        boolean valid = true;
        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();


        //usernameText.setTag("true");

        if (name.isEmpty() || name.length() < 6) {
            nameText.setError("at least 6 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20) {
            passwordText.setError("between 6 and 20 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}