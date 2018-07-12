package me.rlxu.parsetagram;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.parse.ParseUser;

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

        ivProfilePic.setParseFile(currentUser.getParseFile("profilePic"));
        ivProfilePic.loadInBackground();

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
    }
}
