package com.indcoders.pdftojpgconverter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


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
    Button bConvert;
    ImageView ivSelectPdf;
    EditText etpath, etFilename, etHeight, etWidth;
    File file;
    String base64str;
    byte[] filedata;
    Bitmap[] imagedata;
    ProgressDialog pd;
    String savedFilePath;
    Uri saveduri;
    int height, width;
    Animation anim;
    RadioGroup rbGroup;
    RadioButton rbJPG, rbPNG, rbBMP;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;


    public PDF2JPGFragment() {
        // Required empty public constructor
    }

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

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
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
        View v = inflater.inflate(R.layout.pdf2jpg, container, false);

        ivSelectPdf = (ImageView) v.findViewById(R.id.ivSelectPdf);
        etpath = (EditText) v.findViewById(R.id.etPath);
        etFilename = (EditText) v.findViewById(R.id.etFileName);
        etHeight = (EditText) v.findViewById(R.id.etHeight);
        etWidth = (EditText) v.findViewById(R.id.etWidth);
        bConvert = (Button) v.findViewById(R.id.bConvert);
        rbGroup = (RadioGroup) v.findViewById(R.id.rbGroup);

        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != R.id.rbJPG) {
                    Toast.makeText(getActivity(), "Only JPG can be used in Lite version!", Toast.LENGTH_SHORT).show();
                    rbGroup.check(R.id.rbJPG);
                }
            }
        });

        ivSelectPdf.setOnClickListener(this);
        bConvert.setOnClickListener(this);


        pd = new ProgressDialog(getActivity());

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

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
            case R.id.ivSelectPdf:
                pickFile();

                break;

            case R.id.bConvert:

                if (Build.VERSION.SDK_INT >= 21) {
                    // create a new renderer
                    // pdfRenderer(file);
                }
                new ConvertFile().execute(file);
                break;

            case R.id.bSave:



                break;

        }
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

    private void saveImageToExternalStorage(Bitmap[] finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/PDF2JPG");
        myDir.mkdirs();
        File file = null;
        for (int i = 0; i < finalBitmap.length; i++) {
            String fname = etFilename.getText().toString() + ".jpg";
            file = new File(myDir, fname);
            if (file.exists())
                file.renameTo(new File(myDir, etFilename.getText().toString() + "_" + i + ".jpg"));
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap[i].compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.getFD().sync();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*getActivity().sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));*/

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(getActivity(), new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);

                        savedFilePath = path;
                        saveduri = uri;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showAlert(saveduri, savedFilePath);
                            }
                        });

                    }
                });

    }

    public void showAlert(Uri uri, final String path) {
        final Uri uri1 = uri;
        new AlertDialog.Builder(getActivity())
                .setTitle("File Saved.")
                .setMessage("File saved in :" + path)
                .setPositiveButton("Open File", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        File imagefile = new File(path);
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(imagefile), "image/jpg");
                        startActivity(i);
                        dialog.dismiss();
                        reset();
                    }
                })
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                        reset();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }


    public void reset() {
        etFilename.setText("");
        etpath.setText("");
        etpath.setVisibility(View.INVISIBLE);
        TranslateAnimation moveLefttoRight = new TranslateAnimation(-((width / 2) - 150), 0, 0, 0);
        moveLefttoRight.setDuration(500);
        moveLefttoRight.setFillAfter(true);
        ivSelectPdf.startAnimation(moveLefttoRight);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        TranslateAnimation moveLefttoRight = new TranslateAnimation(0, -((width / 2) - 150), 0, 0);
        moveLefttoRight.setDuration(700);
        moveLefttoRight.setFillAfter(true);
        ivSelectPdf.startAnimation(moveLefttoRight);
        if (requestCode == 777 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            etpath.setText(uri.getPath());
                            try {
                                file = new File(new URI(uri.getPath()));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                                Log.e("File ", e.toString());
                            }
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            etpath.setText(uri.toString());
                            try {
                                file = new File(new URI(uri.getPath()));
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                etpath.setText(uri.getPath());
                etpath.setVisibility(View.VISIBLE);
                Log.e("File Path", uri.getPath());
                file = new File(uri.getPath().toString());

                try {
                    base64str = encodeFileToBase64Binary(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else Toast.makeText(getActivity(), "Error opening file!", Toast.LENGTH_SHORT).show();
    }

    private String encodeFileToBase64Binary(File fileName)
            throws IOException {

        File file = fileName;
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encode(bytes, Base64.DEFAULT);
        String encodedString = new String(encoded);

        return encodedString;
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

    public class ConvertFile extends AsyncTask<File, Void, Void> {

        String jsonStr;
        boolean er = false;

        @Override
        protected Void doInBackground(File... params) {
            File f = params[0];
            if (f != null) {
                final MediaType MEDIA_TYPE_PDF
                        = MediaType.parse("application/pdf");

                final OkHttpClient client = new OkHttpClient();


                RequestBody req = new MultipartBuilder().type(MultipartBuilder.FORM).
                        addFormDataPart("file", "file.pdf", new CustomRequest(f, "application/pdf", new CustomRequest.ProgressListener() {
                                    @Override
                                    public void transferred(long num) {
                                        final long num1 = num / 1000;
                                        getActivity().runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                //Toast.makeText(getActivity(),num1+"% uploaded",Toast.LENGTH_SHORT).show();
                                                pd.setProgress((int) num1);
                                            }
                                        });
                                    }
                                })
                        ).
                        addFormDataPart("output", "json").
                        addFormDataPart("res", "120").build();


                Request request = new Request.Builder()
                        .url("https://mazira-pdf-to-png1.p.mashape.com/")
                        .post(req)
                        .addHeader("X-Mashape-Key", "8oh7FdicbKmshlelUm03nJbdY0o1p1TbPWKjsnef32LQMaWAL6")
                        .build();

                Response response = null;
                try {

                    response = client.newCall(request).execute();
                    jsonStr = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                    er = true;
                    Log.e("Error", e.toString());
                }

                if (jsonStr != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.setMessage("Coverting PDF to JPG");
                        }
                    });
                    try {
                        final JSONArray obj = new JSONArray(jsonStr);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Total Pages : " + obj.length(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (obj.length() > 0) {

                            imagedata = new Bitmap[obj.length()];
                            for (int i = 0; i < obj.length(); i++) {
                                base64str = obj.getString(i);
                                Log.e("Response", base64str);

                                byte[] decodedString = Base64.decode(base64str, Base64.DEFAULT);
                                Log.e("ByteArray", decodedString.toString());
                                imagedata[i] = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Log.e("Heigth:Width", imagedata[i].getHeight() + ":" + imagedata[i].getWidth());
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("Error", e.toString());
                        er = true;
                    }
                } else {
                    Log.e("Error", "Server error");
                    er = true;
                }

            } else {
                er = true;
                Log.e("Error", "File = null");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Converting file...");
            pd.setCancelable(true);
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            if (!er) {
                Collections.reverse(Arrays.asList(imagedata));
                saveImageToExternalStorage(imagedata);

            }
        }
    }
}


