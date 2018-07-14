package me.rlxu.parsetagram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.adapter.CommentAdapter;
import me.rlxu.parsetagram.model.Activity;
import me.rlxu.parsetagram.model.Post;

public class CommentFragment extends Fragment {

    private Post post;
    private Context context;
    private ImageView ivProfilePic;
    private Button btnPost;
    private EditText etComment;
    RecyclerView rvComments;
    ArrayList<Activity> comments;
    private CommentAdapter commentAdapter;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        post = (Post) getArguments().getSerializable("postObject");
    }

    public static CommentFragment newInstance(Post post) {
        CommentFragment myFragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putSerializable("postObject", post);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        context = getActivity();
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        rvComments = view.findViewById(R.id.rvComments);
        btnPost = view.findViewById(R.id.btnPost);
        etComment = view.findViewById(R.id.etComment);

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile("profilePic").getUrl())
                .centerCrop()
                .into(ivProfilePic);

        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        rvComments.setLayoutManager(new LinearLayoutManager(context));
        rvComments.setAdapter(commentAdapter);
        populateComments();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = etComment.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                etComment.setText("");
                postComment(comment, user);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void populateComments() {
        final Activity.Query commentsQuery = new Activity.Query();
        commentsQuery.whereEqualTo("type", "comment");
        commentsQuery.withUser().whereEqualTo("post", post).orderByAscending("createdAt");
        commentsQuery.findInBackground(new FindCallback<Activity>() {
            @Override
            public void done(List<Activity> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Activity comment = objects.get(i);
                        comments.add(comment);
                        commentAdapter.notifyItemInserted(comments.size() - 1);
                        Log.d("Comments", "a comment has been loaded!");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postComment(final String comment, ParseUser user) {
        final Activity newComment = new Activity();
        newComment.setType("comment");
        newComment.setComment(comment);
        newComment.setPost(post);
        newComment.setUser(user);

        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("FinalHomeActivity", "Comment added successfully!");
                    comments.add(newComment);
                    commentAdapter.notifyItemInserted(comments.size() - 1);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
