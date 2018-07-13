package me.rlxu.parsetagram.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseImageView;

import java.util.List;

import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.fragment.UserPostsFragment;
import me.rlxu.parsetagram.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<Post> mPosts;
    Context context;

    // pass in Posts array into constructor
    public PostAdapter(List<Post> posts) {
        mPosts = posts;
    }

    // for each row, inflate layout and cache refs into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get data according to position
        final Post post = mPosts.get(position);
        // populate the views according to this data
        holder.tvUsername.setText(post.getUser().getUsername());
        holder.tvUsername2.setText(post.getUser().getUsername());
        holder.tvPostText.setText(post.getDescription());
        holder.tvTimeStamp.setText(post.getCreatedTime());

        Glide.with(context)
                .load(post.getImage().getUrl()).into(holder.ivPostImage);
        Glide.with(context)
                .load(post.getUser().getParseFile("profilePic").getUrl())
                .centerCrop()
                .into(holder.ivProfilePic);
    }

    @Override
    public int getItemCount() { return mPosts.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ParseImageView ivProfilePic;
        public TextView tvUsername;
        public ParseImageView ivPostImage;
        public TextView tvPostText;
        public TextView tvTimeStamp;
        public TextView tvUsername2;
        public TextView tvNumLikes;
        public ImageView btnLike;
        public ImageView btnComment;
        public Button btnViewComments;

        public ViewHolder(final View itemView) {
            super(itemView);

            // do all them findViewByIds
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvPostText = itemView.findViewById(R.id.tvPostText);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvUsername2 = itemView.findViewById(R.id.tvUsername2);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            btnLike = itemView.findViewById(R.id.ivFavorite);
            btnComment = itemView.findViewById(R.id.ivComment);
            btnViewComments = itemView.findViewById(R.id.btnViewComments);

            ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Post post = mPosts.get(pos);
                        String username = post.getUser().getUsername();
                        String imagePath = post.getUser().getParseFile("profilePic").getUrl();
                        // lead to user profile with posts when profile picture is clicked
                        AppCompatActivity activity = (AppCompatActivity) itemView.getContext();
                        UserPostsFragment userFragment = UserPostsFragment.newInstance(username, imagePath, post);
                        //Create a bundle to pass data, add data, set the bundle to your fragment and:
                        activity.getSupportFragmentManager().beginTransaction().
                                replace(R.id.flContainer, userFragment).addToBackStack(null).commit();
                    }
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // do some stuff here later
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }
}
