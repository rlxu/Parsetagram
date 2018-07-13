package me.rlxu.parsetagram.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.parse.ParseImageView;

import java.util.List;

import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.model.Post;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder> {
    List<Post> mPosts;
    Context context;

    public UserPostAdapter(List<Post> posts) {
        mPosts = posts;
    }

    // for each row, inflate layout and cache refs into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_user_post, parent, false);
        UserPostAdapter.ViewHolder viewHolder = new UserPostAdapter.ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostAdapter.ViewHolder holder, int position) {
        // get data according to position
        final Post post = mPosts.get(position);

        Glide.with(context)
                .load(post.getParseFile("image").getUrl())
                .centerCrop()
                .into(holder.ivPostImage);
    }

    @Override
    public int getItemCount() { return mPosts.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ParseImageView ivPostImage;

        public ViewHolder(final View itemView) {
            super(itemView);

            // do all them findViewByIds
            ivPostImage = itemView.findViewById(R.id.ivUserPostImage);
        }
    }
}
