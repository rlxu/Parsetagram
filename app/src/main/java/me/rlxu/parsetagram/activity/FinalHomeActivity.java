package me.rlxu.parsetagram.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.rlxu.parsetagram.BitmapScaler;
import me.rlxu.parsetagram.fragment.ComposeFragment;
import me.rlxu.parsetagram.fragment.ComposePreviewFragment;
import me.rlxu.parsetagram.fragment.HomeFragment;
import me.rlxu.parsetagram.fragment.ProfileFragment;
import me.rlxu.parsetagram.R;
import me.rlxu.parsetagram.model.Post;

public class FinalHomeActivity extends AppCompatActivity implements
        ProfileFragment.OnFragmentInteractionListener,
        ComposeFragment.OnFragmentInteractionListener,
        ComposePreviewFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener {

    BottomNavigationView bottomNavigationView;
    public final String APP_TAG = "Parsetagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int CAPTURE_IMAGE_PROFILE_REQUEST_CODE = 1001;
    public String photoFileName = "photo";
    public String profilePhotoFileName = "profile";
    File photoFile;
    File profilePhotoFile;

    private boolean change_fragment_compose = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_home);

        // set action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.nav_logo_whiteout);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // define your fragments here
        final Fragment homeFragment = new HomeFragment();
        final Fragment composeFragment = new ComposeFragment();
        final Fragment profileFragment = new ProfileFragment();

        // set default to home fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, homeFragment).commit();

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_home:
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, homeFragment).commit();
                            return true;
                        case R.id.action_compose:
                            FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                            fragmentTransaction2.replace(R.id.flContainer, composeFragment).commit();
                            return true;
                        case R.id.action_profile:
                            FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                            fragmentTransaction3.replace(R.id.flContainer, profileFragment).commit();
                            return true;
                    }
                    return false;
                }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(change_fragment_compose) {
            change_fragment_compose = false;
            // move to new fragment with preview and post editing
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ComposePreviewFragment cpFragment = ComposePreviewFragment.newInstance(photoFile.getAbsolutePath());
            ft.replace(R.id.flContainer, cpFragment).commit();
        }
    }

    @Override
    public void logoutInteraction() {
        ParseUser.logOut();
        Intent intent = new Intent(FinalHomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName + ".jpg");

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(FinalHomeActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                change_fragment_compose = true;
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAPTURE_IMAGE_PROFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Compress image and rotate properly before setting
                // by this point we have the camera photo on disk
                final Bitmap takenImage = ComposePreviewFragment.rotateBitmapOrientation(profilePhotoFile.getAbsolutePath());
                Log.d("SaveImage", profilePhotoFile.getAbsolutePath());

                // RESIZE BITMAP
                File takenPhotoUri = getPhotoFileUri(profilePhotoFileName + ".jpg");
                // by this point we have the camera photo on disk
                Bitmap rawTakenImage = ComposePreviewFragment.rotateBitmapOrientation(takenPhotoUri.getPath());
                // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 500);
                // Configure byte output stream
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
                final File resizedFile = getPhotoFileUri(profilePhotoFileName + "_resized.jpg");
                // Write the bytes of the bitmap to file
                try {
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    fos.write(bytes.toByteArray());
                    fos.close();
                    Log.d("SaveImage", "the image was compressed correctly");
                } catch (IOException e) {
                    Log.d("SaveImage", "problem compressing");
                    e.printStackTrace();
                }

                // update database with new picture and change fragment
                updateProfilePic();
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void createPost(String description, ParseFile imageFile, ParseUser user) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("FinalHomeActivity", "Post created successfully!");
                    // move to home fragment to show latest post
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    HomeFragment hFragment = new HomeFragment();
                    ft.replace(R.id.flContainer, hFragment).commit();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onLaunchCameraProfile(View v) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        profilePhotoFile = getPhotoFileUri(profilePhotoFileName + ".jpg");

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(FinalHomeActivity.this, "com.codepath.fileprovider", profilePhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_PROFILE_REQUEST_CODE);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // placeholder for home fragment until future features are implemented
    }

    // update profile picture on database for user
    private void updateProfilePic() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        File file = new File("/storage/emulated/0/Android/data/me.rlxu.parsetagram/files/Pictures/Parsetagram/"
                + profilePhotoFileName + "_resized.jpg");
        final ParseFile parseFile = new ParseFile(file);
        currentUser.put("profilePic", parseFile);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("FinalHomeActivity", "Profile picture updated successfully!");
                    // move to home fragment to show latest post
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ProfileFragment pFragment = new ProfileFragment();
                    ft.replace(R.id.flContainer, pFragment).commit();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
