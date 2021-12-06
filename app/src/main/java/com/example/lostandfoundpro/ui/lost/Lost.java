package com.example.lostandfoundpro.ui.lost;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfoundpro.Adapter.PostAdapter;
import com.example.lostandfoundpro.Model.Post;
import com.example.lostandfoundpro.Model.Users;
import com.example.lostandfoundpro.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Lost extends Fragment {

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

        View root = inflater.inflate(R.layout.fragment_lost, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        recyclerView = root.findViewById(R.id.recycler_id_profile);

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

            query = firebaseFirestore.collection("PostDetails").orderBy("Time",Query.Direction.DESCENDING).whereEqualTo("CategoryValue","Lost");
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


        return  root;
    }
}