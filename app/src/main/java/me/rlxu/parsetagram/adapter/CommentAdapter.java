package me.rlxu.parsetagram.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseImageView;

import java.util.List;

import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.model.Activity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<Activity> mComments;
    Context context;

    public CommentAdapter(List<Activity> comments) {
        mComments = comments;
    }

    // for each row, inflate layout and cache refs into ViewHolder
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View commentView = inflater.inflate(R.layout.item_comment, parent, false);
        CommentAdapter.ViewHolder viewHolder = new CommentAdapter.ViewHolder(commentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        // get data according to position
        final Activity comment = mComments.get(position);
        String username = comment.getUser().getUsername();
        int start = 0;
        int end = username.length();
        SpannableStringBuilder str = new SpannableStringBuilder(username + " " + comment.getComment());
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvComment.setText(str);
        holder.tvTimestamp.setText(comment.getTimestamp());

        Glide.with(context)
                .load(comment.getUser().getParseFile("profilePic").getUrl())
                .centerCrop()
                .into(holder.ivProfilePic);
    }

    @Override
    public int getItemCount() { return mComments.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ParseImageView ivProfilePic;
        public TextView tvComment;
        public TextView tvTimestamp;

        public ViewHolder(final View itemView) {
            super(itemView);

            // do all them findViewByIds
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvTimestamp = itemView.findViewById(R.id.tvTimeStamp);
        }
    }
}
