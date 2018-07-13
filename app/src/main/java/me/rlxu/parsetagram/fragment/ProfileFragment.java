package me.rlxu.parsetagram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import me.rlxu.parsetagram.R;

public class ProfileFragment extends Fragment {

    private ParseUser currentUser;
    private OnFragmentInteractionListener mListener;
    private Button logoutButton;
    private ParseImageView ivProfilePic;
    private Button btnChangeProfile;
    private TextView tvUsername;
    private TextView tvEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logoutButton = view.findViewById(R.id.btnLogout);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnChangeProfile = view.findViewById(R.id.btnChangeProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);

        currentUser = ParseUser.getCurrentUser();
        tvUsername.setText(currentUser.getUsername());
        tvEmail.setText(currentUser.getEmail());
        ParseFile image = currentUser.getParseFile("profilePic");

        if (image != null) {
            Glide.with(this)
                    .load(image.getUrl())
                    .centerCrop()
                    .into(ivProfilePic);
        }

        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = currentUser.getUsername();
                String imagePath = currentUser.getParseFile("profilePic").getUrl();
                // lead to user profile with posts when profile picture is clicked
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                UserPostsFragment userFragment = UserPostsFragment.newInstance(username, imagePath, null);
                //Create a bundle to pass data, add data, set the bundle to your fragment and:
                activity.getSupportFragmentManager().beginTransaction().
                        replace(R.id.flContainer, userFragment).addToBackStack(null).commit();
            }
        });

        // click listener for change profile button
        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLaunchCameraProfile(v);
            }
        });

        // click listener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logoutInteraction();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void logoutInteraction();
        void onLaunchCameraProfile(View v);
    }
}
