package com.example.lostandfoundpro.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.lostandfoundpro.Adapter.PostAdapter;
import com.example.lostandfoundpro.Dashboard;
import com.example.lostandfoundpro.LogIn;
import com.example.lostandfoundpro.Model.Post;
import com.example.lostandfoundpro.Model.Users;
import com.example.lostandfoundpro.Profile;
import com.example.lostandfoundpro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    DocumentReference documentReference;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> list;
    private List<Users> umsersList;
    private Query query;
    private ListenerRegistration listenerRegistration;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        recyclerView = root.findViewById(R.id.recycler_id);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapter = new PostAdapter((Activity) getContext(),list,umsersList);
        recyclerView.setAdapter(adapter);
//        adapter = new PostAdapter(HomeFragment.this,list);
//        recyclerView.setAdapter(adapter);

        if (firebaseAuth.getCurrentUser() != null){

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom =! recyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(getContext(),"You Reach the last Post",Toast.LENGTH_SHORT).show();
                }
            });

            query = firebaseFirestore.collection("PostDetails").orderBy("Time",Query.Direction.DESCENDING);
            listenerRegistration = query.addSnapshotListener((Activity) getContext(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange doc: value.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            Post post = doc.getDocument().toObject(Post.class);
                            list.add(post);
                            adapter.notifyDataSetChanged();;
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });

        }


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();


//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (firebaseUser != null) {
//            for (UserInfo profile : firebaseUser.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();
//
//                // Name, email address, and profile photo Url
//                String name = profile.getDisplayName();
//                String email = profile.getEmail();
//
//                userProfileEmail.setText(email);
//                userProfileName.setText(name);
//
//            }
//
//    }
}

}