package me.rlxu.parsetagram.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Activity")
public class Activity extends ParseObject {
    private static final String KEY_TYPE = "type";
    private static final String KEY_POST = "post";
    private static final String KEY_USER = "user";
    private static final String KEY_COMMENT = "comment";

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        put(KEY_TYPE, type);
    }

    public Post getPost() {
        return (Post) getParseObject(KEY_POST);
    }

    public void setPost(Post post) {
        put(KEY_POST, post);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }

    public static class Query extends ParseQuery<Activity> {
        public Query() {
            super(Activity.class);
        }
    }
}
