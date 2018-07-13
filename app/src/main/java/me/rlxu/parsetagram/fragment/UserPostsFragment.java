package me.rlxu.parsetagram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.adapter.UserPostAdapter;
import me.rlxu.parsetagram.model.Post;

public class UserPostsFragment extends Fragment {

    private String username;
    private String imagePath;
    private Context context;
    private ParseUser user;

    private UserPostAdapter userPostAdapter;
    ArrayList<Post> posts;
    RecyclerView rvGridPosts;
    private ImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvNumPosts;

    public UserPostsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
        imagePath = getArguments().getString("imagePath");
        Post post = (Post) getArguments().getSerializable("postObject");
        if (post == null) {
            user = ParseUser.getCurrentUser();
        } else {
            user = post.getUser();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_posts, container, false);
        context = getActivity();
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvNumPosts = view.findViewById(R.id.tvNumPosts);
        rvGridPosts = view.findViewById(R.id.rvGridPosts);

        posts = new ArrayList<>();
        userPostAdapter = new UserPostAdapter(posts);
        rvGridPosts.setLayoutManager(new GridLayoutManager(context, 3));
        rvGridPosts.setAdapter(userPostAdapter);

        Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(ivProfilePic);

        tvUsername.setText(username);
        populateUserTimeline();

        return view;
    }

    public static UserPostsFragment newInstance(String username, String imagePath, Post post) {
        UserPostsFragment myFragment = new UserPostsFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("imagePath", imagePath);
        args.putSerializable("postObject", post);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void populateUserTimeline() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.whereEqualTo("user", user).orderByDescending("createdAt");
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Post post = objects.get(i);
                        posts.add(post);
                        userPostAdapter.notifyItemInserted(posts.size() - 1);
                        Log.d("UserPosts", "a post has been loaded!");
                    }
                    tvNumPosts.setText(Integer.toString(posts.size()));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
