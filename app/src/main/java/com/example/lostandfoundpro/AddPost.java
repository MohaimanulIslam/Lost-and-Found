package com.example.lostandfoundpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AddPost extends AppCompatActivity {

    private EditText postDec;
    private Button postBtn;
    private ImageView postImg;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private Spinner categorySpinner,locationSpinner;
    String[] Category;
    String[] Location;

    private String Uid;
    private Uri postImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        getSupportActionBar().setTitle("Add a new post");

        postImg = findViewById(R.id.post_imgview);
        postDec = findViewById(R.id.post_dec);
        postBtn = findViewById(R.id.post_btn);
        categorySpinner = findViewById(R.id.category_spinner_id);
        locationSpinner = findViewById(R.id.location_spinner_id);

        Category = getResources().getStringArray(R.array.category);
        Location = getResources().getStringArray(R.array.location);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        Uid = firebaseAuth.getCurrentUser().getUid();

//        arrayAdepter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.spinner_view,R.id.spinner_view_id,Category);
        categorySpinner.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,R.layout.spinner_view,R.id.spinner_view_id,Location);
        locationSpinner.setAdapter(adapter2);


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String PostDec = postDec.getText().toString();
                String CategoryValue = categorySpinner.getSelectedItem().toString();
                String LocationValue = locationSpinner.getSelectedItem().toString();

                if (!PostDec.isEmpty() && postImgUri != null){

                    StorageReference postRef = storageReference.child("Post_photo").child(FieldValue.serverTimestamp().toString()+".jpg");
                    postRef.putFile(postImgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                postRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String,Object> postData = new HashMap<>();
                                        postData.put("Description",PostDec);
                                        postData.put("PostImg",uri.toString());
                                        postData.put("User",Uid);
                                        postData.put("Time",FieldValue.serverTimestamp());
                                        postData.put("CategoryValue",CategoryValue);
                                        postData.put("LocationValue",LocationValue);

                                        firestore.collection("PostDetails").add(postData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                                                    Toast.makeText(getApplicationContext(),"Post Successfully added",Toast.LENGTH_LONG).show();
                                                }else {
                                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                });
                            }else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"Please Select Image and Write a Description about our Found or lost Document",Toast.LENGTH_LONG).show();
                }
            }
        });

        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Storage permission from user to select profile pic

                Toast.makeText(getApplicationContext(),"Click done",Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(AddPost.this , Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddPost.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                    }else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(6,4)
                                .start(AddPost.this);
                    }
                }
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
                postImgUri = result.getUri();
                postImg.setImageURI(postImgUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}