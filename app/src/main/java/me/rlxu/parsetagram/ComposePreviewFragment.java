package me.rlxu.parsetagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ComposePreviewFragment extends Fragment {

    public final String APP_TAG = "Parsetagram";
    public String photoFileName = "photo";

    private OnFragmentInteractionListener mListener;
    private ImageView ivPreview;
    private EditText descriptionInput;
    private Button createButton;
    private String takenImagePath;

    public ComposePreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        takenImagePath = getArguments().getString("takenImage");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose_preview, container, false);
        ivPreview = view.findViewById(R.id.ivPreview);
        descriptionInput = view.findViewById(R.id.description_et);
        createButton = view.findViewById(R.id.create_btn);

        // by this point we have the camera photo on disk
        final Bitmap takenImage = rotateBitmapOrientation(takenImagePath);
        Log.d("SaveImage", takenImagePath);

        // RESIZE BITMAP
        File takenPhotoUri = getPhotoFileUri(photoFileName + ".jpg");
        // by this point we have the camera photo on disk
        Bitmap rawTakenImage = rotateBitmapOrientation(takenPhotoUri.getPath());
        // See BitmapScaler.java: https://gist.github.com/nesquena/3885707fd3773c09f1bb
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 1000);
        // Configure byte output stream
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        final File resizedFile = getPhotoFileUri(photoFileName + "_resized.jpg");
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

        // Load the taken image into a preview
        ivPreview.setImageBitmap(takenImage);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = descriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                File file = new File("/storage/emulated/0/Android/data/me.rlxu.parsetagram/files/Pictures/Parsetagram/" + photoFileName + "_resized.jpg");
                final ParseFile parseFile = new ParseFile(file);

                mListener.createPost(description, parseFile, user);
            }
        });

        return view;
    }

    public static ComposePreviewFragment newInstance(String imagePath) {
        ComposePreviewFragment myFragment = new ComposePreviewFragment();
        Bundle args = new Bundle();
        args.putString("takenImage", imagePath);
        myFragment.setArguments(args);
        return myFragment;
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
        void createPost(String description, ParseFile imageFile, ParseUser user);
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }
}
