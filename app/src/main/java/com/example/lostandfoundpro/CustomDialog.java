package com.example.lostandfoundpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class CustomDialog extends AppCompatDialogFragment {

    private EditText enterEamil;
    private Button forgotPass;
    private FirebaseAuth firebaseAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_forgotpass, null);

        enterEamil = view.findViewById(R.id.enter_email);
        forgotPass = view.findViewById(R.id.resetpass_id);
        firebaseAuth = FirebaseAuth.getInstance();

        builder.setView(view)
                .setTitle("Reset Password")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.sendPasswordResetEmail(enterEamil.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getContext(),"Reset Email Successfully Sent",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getContext(),"Email sending Faild try again after few minutes",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
        return builder.create();
    }
}
