package com.indcoders.pdftojpgconverter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PDF2JPGFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PDF2JPGFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PDF2JPGFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Button bChoose, bConvert;
    ImageView ivJPG;
    TextView tvPath;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PDF2JPGFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PDF2JPGFragment newInstance(String param1, String param2) {
        PDF2JPGFragment fragment = new PDF2JPGFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PDF2JPGFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pdf2_jpg, container, false);

        bChoose = (Button) v.findViewById(R.id.bFileChooser);
        bConvert = (Button) v.findViewById(R.id.bConvert);
        tvPath = (TextView) v.findViewById(R.id.tvFilePath);
        ivJPG = (ImageView) v.findViewById(R.id.ivJPG);

        bChoose.setOnClickListener(this);
        bConvert.setOnClickListener(this);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bFileChooser:
                pickFile();
                break;

            case R.id.bConvert:

                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void pickFile() {
        // This always works
        //Intent i = new Intent(getActivity(), FilePickerActivity.class);
        // This works if you defined the intent filter
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);

       // i.setType("pdf/*");


        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);


        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, 777);
    }

    /*public boolean convertFile(File f) throws IOException {


        final MediaType MEDIA_TYPE_PDF
                    = MediaType.parse("application/pdf");

             final OkHttpClient client = new OkHttpClient();

                File file = new File(getActivity());

                Request request = new Request.Builder()
                        .url("https://api.github.com/markdown/raw")
                        .post(RequestBody.create(MEDIA_TYPE_PDF, file))
                        .build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println(response.body().string());


        return false;
    }*/


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 777 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            tvPath.setText(uri.toString());
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            tvPath.setText(uri.toString());
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                tvPath.setText(uri.toString());
            }
        }
    }

}

