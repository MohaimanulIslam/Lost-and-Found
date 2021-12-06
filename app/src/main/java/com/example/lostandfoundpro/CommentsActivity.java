package com.example.lostandfoundpro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostandfoundpro.Adapter.CommentsAdapter;
import com.example.lostandfoundpro.Model.Comments;
import com.example.lostandfoundpro.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentRecycler;
    private EditText commentEdit;
    private TextView commentTextView;
    private Button commentButton;

    private FirebaseFirestore firestore;
    private String post_id;
    private String currentUserId;
    private FirebaseAuth auth;
    private CommentsAdapter adapter;
    private List<Comments> mList;
//    private List<Users> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        commentRecycler = findViewById(R.id.comment_recycler_id);
        commentEdit = findViewById(R.id.comment_edittext_id);
        commentButton = findViewById(R.id.comment_button_id);

        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mList = new ArrayList<>();
//        usersList = new ArrayList<>();
        adapter = new CommentsAdapter(CommentsActivity.this , mList);

        post_id = getIntent().getStringExtra("postid");
//        mCommentRecyclerView.setHasFixedSize(true);
//        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecycler.setAdapter(adapter);

        firestore.collection("PostDetails/" + post_id + "/Comments").addSnapshotListener(CommentsActivity.this , new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
//                        Comments comments = documentChange.getDocument().toObject(Comments.class);
//                        .withId(documentChange.getDocument().getId());
//                        String userId = documentChange.getDocument().getString("User");
//                        mList.add(comments);
//                        adapter.notifyDataSetChanged();

                        Comments comments = documentChange.getDocument().toObject(Comments.class);
                        mList.add(comments);
                        adapter.notifyDataSetChanged();


//                        firestore.collection("ProfileDetails").document(userId).get()
//                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        if (task.isSuccessful()){
//                                            Users users = task.getResult().toObject(Users.class);
//                                            usersList.add(users);
//                                            Comments comments = documentChange.getDocument().toObject(Comments.class);
//                                            mList.add(comments);
//                                            adapter.notifyDataSetChanged();
//                                        }else{
//                                            Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });

                    }else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentEdit.getText().toString();
                if (!comment.isEmpty()) {
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("comment", comment);
                    commentsMap.put("time", FieldValue.serverTimestamp());
                    commentsMap.put("User", currentUserId);
                    firestore.collection("PostDetails/" + post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CommentsActivity.this, "Please write Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}