package com.colwanymab.timelynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    CardView cardSignUp;
    EditText etEmail, etPassword;
    Button bReset, bLogin;
    ImageView ivViewPass;

    ProgressBar progressBar;
    FirebaseAuth auth;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String email = "", password = "";
    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        try {
            email = getIntent().getExtras().getString("email");
            password = getIntent().getExtras().getString("password");
        } catch (Exception e) {}

        if (android.os.Build.VERSION.SDK_INT > 20) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        cardSignUp = (CardView) findViewById(R.id.cardSignUp);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        bReset = (Button) findViewById(R.id.bReset);
        bLogin = (Button) findViewById(R.id.bLogin);
        ivViewPass = (ImageView) findViewById(R.id.ivViewPass);

        etEmail.setText(email);
        etPassword.setText(password);

        Animation animBounce = AnimationUtils.loadAnimation(this, R.anim.left_roll);
        cardSignUp.startAnimation(animBounce);

        ivViewPass.setOnClickListener(this);
        cardSignUp.setOnClickListener(this);
        bReset.setOnClickListener(this);
        bLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivViewPass:
                if (passwordVisible) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordVisible = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordVisible = true;
                }
                break;
            case R.id.cardSignUp:
                signUp();
                break;
            case R.id.bReset:
                reset();
                break;
            case R.id.bLogin:
                Intent intent = new Intent(this, Login.class);
                intent.putExtra("email", etEmail.getText().toString());
                intent.putExtra("password", etPassword.getText().toString());
                startActivity(intent);
                finish();
                break;
        }
    }

    private void reset() {
        final String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter your registered email address!", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "We have sent you instructions to reset your password",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to send reset email", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void signUp() {
        final String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user
        auth.signInAnonymously();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Sign up failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            editor = prefs.edit();
                            editor.putBoolean(AppInfo.BOOL_WRITER, true);
                            editor.apply();

                            startActivity(new Intent(SignUp.this, JoinNetwork.class));
                            finish();
                        }
                    }
                });
    }

}
