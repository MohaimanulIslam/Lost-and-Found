package com.example.lostandfoundpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private EditText Username,Fullname,Address;
    private CircleImageView ProfileImg;
    private Button saveBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private String Uid;
    private Uri mImageUri = null;
    private boolean isphotoselected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");

        Username = findViewById(R.id.username_Id);
        Fullname = findViewById(R.id.fullname_id);
        Address = findViewById(R.id.address_id);
        ProfileImg = findViewById(R.id.profile_img);
        saveBtn = findViewById(R.id.save_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Uid = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("ProfileDetails").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String username = task.getResult().getString("username");
                        String fullname = task.getResult().getString("fullname");
                        String addres = task.getResult().getString("address");
                        String image = task.getResult().getString("imgUri");

                        Username.setText(username);
                        Fullname.setText(fullname);
                        Address.setText(addres);
                        mImageUri = Uri.parse(image);
                        Glide.with(Profile.this).load(image).into(ProfileImg);

                    }
                }
            }
        });

        ProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Storage permission from user to select profile pic

                Toast.makeText(getApplicationContext(),"Click done",Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(Profile.this , Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(Profile.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                    }else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(Profile.this);
                    }
                }
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = Username.getText().toString();
                String fullname = Fullname.getText().toString();
                String address = Address.getText().toString();

                StorageReference imageRef = storageReference.child("Profile_pics").child(Uid + ".jpg");

                if (isphotoselected) {

                    if (mImageUri != null) {

                    imageRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Toast.makeText(Profile.this, "Photo Upload Done", Toast.LENGTH_SHORT).show();

                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadData(task, username, fullname, address, uri);
                                }
                            });


//                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    downloadUri = uri;
//
//                                    Map<Object,String> map = new HashMap<>();
//                                    map.put("username",Username.getText().toString());
//                                    map.put("fullname",Fullname.getText().toString());
//                                    map.put("address",Address.getText().toString());
//                                    map.put("url",downloadUri.toString());
//
//                                    firestore.collection("ProfileDetails").document(Uid).set(map);
//                                    Toast.makeText(Profile.this, "Details Uoload Done", Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
                        }
                    });

                    } else {

                    Toast.makeText(Profile.this, "Please Select picture and write your name", Toast.LENGTH_SHORT).show();
                    }
                }else {

                    uploadData(null,username,fullname,address,mImageUri);
                }
            }
        });

    }

    private void uploadData(Task<UploadTask.TaskSnapshot> task, String username, String fullname, String address, Uri downloadUri) {


        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("fullname",fullname);
        map.put("address",address);
        map.put("imgUri",downloadUri.toString());

        firestore.collection("ProfileDetails").document(Uid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Profile.this, "Data Upload Done", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),Dashboard.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Error"+e, Toast.LENGTH_SHORT).show();
            }
        });

    }


    //    get the profile photo from device
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                mImageUri = result.getUri();
                ProfileImg.setImageURI(mImageUri);
                isphotoselected = true;
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}