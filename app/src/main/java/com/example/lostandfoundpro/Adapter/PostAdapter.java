package com.example.lostandfoundpro.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lostandfoundpro.CommentsActivity;
import com.example.lostandfoundpro.Model.Post;
import com.example.lostandfoundpro.Model.Users;
import com.example.lostandfoundpro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mList;
    private List<Users> usersList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public PostAdapter(Activity context, List<Post> mList, List<Users> usersList) {
        this.mList = mList;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_post,parent,false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mList.get(position);
        holder.setPostImageView(post.getPostImg());
        holder.setCategoryPost(post.getCategoryValue());
        holder.setLocationPost(post.getLocationValue());
        holder.setPostDec(post.getDescription());


//        String username = usersList.get(position).getUsername();
//        holder.setUsernamePost(username);

//        Users users = usersList.get(position);
//        holder.setUsernamePost(users.getUsername());

        long milliseconds = post.getTime().getTime();
        String date  = DateFormat.format("dd/MM/yyyy" , new Date(milliseconds)).toString();
        holder.setDatePost(date);

        String userId = post.getUser();
        firestore.collection("ProfileDetails").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String username = task.getResult().getString("username");
                    String images = task.getResult().getString("imgUri");

                    holder.setUserPostImg(images);
                    holder.setUsernamePost(username);
                }else {
                    Toast.makeText(context,task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

        //likebtn
        String postId = post.PostId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.PostLikeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("PostDetails/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Map<String , Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp" , FieldValue.serverTimestamp());
                            firestore.collection("PostDetails/" + postId + "/Likes").document(currentUserId).set(likesMap);
                        }else{
                            firestore.collection("PostDetails/" + postId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        //like color change
        firestore.collection("PostDetails/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (value.exists()){
                        holder.PostLikeImg.setImageDrawable(context.getDrawable(R.drawable.liked));
                    }else{
                        holder.PostLikeImg.setImageDrawable(context.getDrawable(R.drawable.unliked));
                    }
                }
            }
        });

        //likes count
        firestore.collection("PostDetails/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null){
                    if (!value.isEmpty()){
                        int count = value.size();
                        holder.setPostLikeCount(count);
                    }else{
                        holder.setPostLikeCount(0);
                    }
                }
            }
        });

        //comments implementation
        holder.PostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context , CommentsActivity.class);
                commentIntent.putExtra("postid" , postId);
                context.startActivity(commentIntent);
            }
        });

//        if (currentUserId.equals(post.getUser())){
//            holder.deleteBtn.setVisibility(View.VISIBLE);
//            holder.deleteBtn.setClickable(true);
//            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                    alert.setTitle("Delete")
//                            .setMessage("Are You Sure ?")
//                            .setNegativeButton("No" , null)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                    firestore.collection("Posts/" + postId + "/Comments").get()
//                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                    for (QueryDocumentSnapshot snapshot : task.getResult()){
//                                                        firestore.collection("Posts/" + postId + "/Comments").document(snapshot.getId()).delete();
//                                                    }
//                                                }
//                                            });
//                                    firestore.collection("Posts/" + postId + "/Likes").get()
//                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                    for (QueryDocumentSnapshot snapshot : task.getResult()){
//                                                        firestore.collection("Posts/" + postId + "/Likes").document(snapshot.getId()).delete();
//                                                    }
//                                                }
//                                            });
//                                    firestore.collection("Posts").document(postId).delete();
//                                    mList.remove(position);
//                                    notifyDataSetChanged();
//                                }
//                            });
//                    alert.show();
//                }
//            });
//        }

//        String userPic = usersList.get(position).getImgUri();
//        holder.setUserPostImg(userPic);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        ImageView PostImageView,PostLikeImg;
        CircleImageView UserPostImg;
        TextView UsernamePost,CategoryPost,LocationPost,DatePost,PostDec,PostLikeCount,PostComment;
        View mView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            PostLikeImg = mView.findViewById(R.id.post_like_image_id);
            PostComment = mView.findViewById(R.id.post_comment_id);
        }

        public void setPostComment(){

        }

        public void setPostLikeCount(int count){
            PostLikeCount = mView.findViewById(R.id.post_like_count_id);
            PostLikeCount.setText(count + " Likes");
        }

        public void setUserPostImg(String userpostpic){
            UserPostImg = mView.findViewById(R.id.user_post_img);
            Glide.with(context).load(userpostpic).into(UserPostImg);
        }

        public void setUsernamePost(String username){
            UsernamePost = mView.findViewById(R.id.username_post_id);
            UsernamePost.setText(username);
        }

        public void setPostImageView(String PostImg){
            PostImageView = mView.findViewById(R.id.post_imageView_id);
            Glide.with(context).load(PostImg).into(PostImageView);
        }

        public void setCategoryPost(String category){
            CategoryPost = mView.findViewById(R.id.category_post_id);
            CategoryPost.setText(category);
        }

        public void setLocationPost(String location){
            LocationPost = mView.findViewById(R.id.location_post_id);
            LocationPost.setText(location);
        }

        public void setPostDec(String postdecs){
            PostDec = mView.findViewById(R.id.post_dec_id);
            PostDec.setText(postdecs);
        }

        public void setDatePost(String date){
            DatePost = mView.findViewById(R.id.date_post_id);
            DatePost.setText(date);
        }

    }
}
