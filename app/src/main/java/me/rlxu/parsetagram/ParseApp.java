package me.rlxu.parsetagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import me.rlxu.parsetagram.model.Activity;
import me.rlxu.parsetagram.model.Post;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Activity.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-parsetagram-litty")
                .clientKey("csm-application-woohoo")
                .server("http://rlxu-fbu-parsetagram.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
