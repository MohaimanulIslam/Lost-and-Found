package com.example.lostandfoundpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    private TextView dontHaveAccount,forgotPass;
    private EditText loginEmail,loginPass;
    private Button loginBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        loginEmail = findViewById(R.id.login_email);
        loginPass = findViewById(R.id.login_pass);
        forgotPass = findViewById(R.id.forgot_pass);
        dontHaveAccount = findViewById(R.id.dont_Account);
        loginBtn = findViewById(R.id.login_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginEmail.getText().toString().matches(emailPattern)){
                    if (loginPass.length() >= 6){
                        firebaseAuth.signInWithEmailAndPassword(loginEmail.getText().toString(),loginPass.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    updateUI(user);
                                    Intent intent = new Intent(getApplicationContext(),Dashboard.class);
                                    startActivity(intent);
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else {
                        loginPass.setError("Please Enter Valid Password");
                    }
                } else {
                    loginEmail.setError("Please Enter Valid Email");
                }

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this,Registration.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(FirebaseUser user) {

    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final ImageView imageView = new ImageView(this);
        final EditText emailet= new EditText(this);

        imageView.setImageResource(R.drawable.forgotpasss);
        // write the email using which you registered
        emailet.setHint("Enter Your Registered Email");
        emailet.setMinEms(16);
        emailet.setGravity(Gravity.CENTER);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(imageView,500,600);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setBackground(getDrawable(R.color.teal_200));
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailet.getText().toString().trim();
                if (!email.isEmpty()){
                    beginRecovery(email);
                }
                else {
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {
        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(LogIn.this,"Reset Email Successfully Sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(LogIn.this,"Email sending Faild try again after few minutes",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(LogIn.this,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openDialog(){
        CustomDialog customDialog = new CustomDialog();
        customDialog.show(getSupportFragmentManager(),"Custom Dialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void reload() {

    }

    public void showToast(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.toast_layout, (ViewGroup)findViewById(R.id.toast_root));

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();


    }
}