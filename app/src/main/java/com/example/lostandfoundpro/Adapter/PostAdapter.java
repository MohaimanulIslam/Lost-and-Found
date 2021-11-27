package com.example.lostandfoundpro.Adapter;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lostandfoundpro.Model.Post;
import com.example.lostandfoundpro.Model.Users;
import com.example.lostandfoundpro.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

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
