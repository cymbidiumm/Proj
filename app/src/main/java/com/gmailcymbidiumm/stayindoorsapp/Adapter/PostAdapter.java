package com.gmailcymbidiumm.stayindoorsapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gmailcymbidiumm.stayindoorsapp.Model.Post;
import com.gmailcymbidiumm.stayindoorsapp.Model.User;
import com.gmailcymbidiumm.stayindoorsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

        if(post.getDescription().equals("")){
            holder.description.setText("comment below a plausible description for my post :)");
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        if(post.getTitle().equals("")){
            holder.title.setText("comment below a suitable title ");
        } else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(post.getTitle());
        }

        if(post.getTime().equals("")){
           holder.time.setText("x");
        }else{
            holder.time.setVisibility(View.VISIBLE);
            holder.time.setText(post.getTime());
        }

        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());


    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, comment, sendToSomeone;
        public TextView username, publisher, title, time, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            comment = itemView.findViewById(R.id.comment);
            sendToSomeone = itemView.findViewById(R.id.sendToSomeone);
            username = itemView.findViewById(R.id.username);
            publisher = itemView.findViewById(R.id.publisher);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getfName());
                publisher.setText(user.getfName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
