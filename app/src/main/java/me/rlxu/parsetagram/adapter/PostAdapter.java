package me.rlxu.parsetagram.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.fragment.UserPostsFragment;
import me.rlxu.parsetagram.model.Activity;
import me.rlxu.parsetagram.model.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<Post> mPosts;
    Context context;
    int numLikes;
    boolean userLiked = false;

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

        if (userLiked) {
            Drawable icon = context.getResources().getDrawable(R.drawable.ic_heart_active);
            holder.btnLike.setImageDrawable(icon);
        } else {
            Drawable icon = context.getResources().getDrawable(R.drawable.ic_heart);
            holder.btnLike.setImageDrawable(icon);
        }
        getPostLikes(position, holder.tvNumLikes, holder.btnLike);

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

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (userLiked) {
                        removeLike(mPosts.get(pos));
                    } else {
                        addLike(mPosts.get(pos));
                    }
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // do some stuff here if desired
        }

        private void removeLike(Post post) {
            final Activity.Query likesQuery = new Activity.Query();
            likesQuery.whereEqualTo("post", post);
            likesQuery.whereEqualTo("user", ParseUser.getCurrentUser());
            likesQuery.findInBackground(new FindCallback<Activity>() {
                @Override
                public void done(List<Activity> results, ParseException e) {
                    if (e == null) {
                        for (Activity like : results) {
                            like.deleteInBackground();
                            numLikes -= 1;
                            notifyDataSetChanged();
                            userLiked = false;
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }

        private void addLike(Post post) {
            final Activity activity = new Activity();
            activity.setType("like");
            activity.setUser(ParseUser.getCurrentUser());
            activity.setPost(post);

            activity.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("FinalHomeActivity", "Like added successfully!");
                        numLikes += 1;
                        notifyDataSetChanged();
                        userLiked = true;
                    } else {
                        e.printStackTrace();
                    }
                }
            });
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

    private void getPostLikes(int position, final TextView tvNumLikes, final ImageView btnLike) {
        final Activity.Query likesQuery = new Activity.Query();
        likesQuery.whereEqualTo("post", mPosts.get(position));
        likesQuery.findInBackground(new FindCallback<Activity>() {
            @Override
            public void done(List<Activity> objects, ParseException e) {
                if (e == null) {
                    // set numLikes and if user has liked
                    numLikes = 0;
                    userLiked = false;
                    for (int i = 0; i < objects.size(); i++) {
                        Activity item = objects.get(i);
                        if (item.getType().equals("like")) {
                            numLikes += 1;
                            if (item.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                userLiked = true;
                            }
                        }
                    }
                    // set data into post view
                    tvNumLikes.setText(Integer.toString(numLikes) + " likes");
                    if (userLiked) {
                        Drawable icon = context.getResources().getDrawable(R.drawable.ic_heart_active);
                        btnLike.setImageDrawable(icon);
                    } else {
                        Drawable icon = context.getResources().getDrawable(R.drawable.ic_heart);
                        btnLike.setImageDrawable(icon);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
