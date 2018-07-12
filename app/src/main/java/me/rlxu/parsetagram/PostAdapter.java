package me.rlxu.parsetagram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseImageView;

import java.util.List;

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
        holder.tvPostText.setText(post.getDescription());
        holder.tvTimeStamp.setText(post.getCreatedTime());

        Glide.with(context)
                .load(post.getImage().getUrl()).into(holder.ivPostImage);
        holder.ivProfilePic.setParseFile(post.getUser().getParseFile("profilePic"));
        holder.ivProfilePic.loadInBackground();
    }

    @Override
    public int getItemCount() { return mPosts.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ParseImageView ivProfilePic;
        public TextView tvUsername;
        public ParseImageView ivPostImage;
        public TextView tvPostText;
        public TextView tvTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);

            // do all them findViewByIds
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvPostText = itemView.findViewById(R.id.tvPostText);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);

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
