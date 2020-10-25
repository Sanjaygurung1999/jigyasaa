package com.example.jigyasaa;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudmersive.client.RecognizeApi;
import com.cloudmersive.client.model.ObjectDetectionResult;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.*;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mainact_bottnavi;
    FloatingActionButton cameraButton;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    ImageView obtainedImage;
    static ObjectDetectionResult result;
    ImageButton deleteIcon;
    RecyclerView capturedrecycle;
    ArrayList<ContactModel> arrayList = new ArrayList<>();
    SearchView textSearchfor;
    TextView backgroundText;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124,MY_PERMISSIONS_RECORD_AUDIO=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //authentication apikey
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
        Apikey.setApiKey("25011223-f7f2-4900-b2d3-46a84dfc06e7");
        //
        mainact_bottnavi = findViewById(R.id.mainactivity_botnavi);
        capturedrecycle = findViewById(R.id.capture_recycle);
        deleteIcon = findViewById(R.id.deleteIcon);
        cameraButton = findViewById(R.id.floatingActionButton);
        obtainedImage = findViewById(R.id.imageView);
        textSearchfor = findViewById(R.id.texttosearch);
        backgroundText = findViewById(R.id.textView6);
        //recycle
        capturedrecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        capturedrecycle.setAdapter(new ContactAdapter());
        //deleteicon action
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtainedImage.setImageDrawable(null);
                deleteIcon.setVisibility(View.GONE);
                arrayList.clear();
            }
        });
        //camera action
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
        //searchview
        textSearchfor.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(getApplicationContext(), Mainitem_results.class);
                i.putExtra("searchfor", query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //bottnavi
        mainact_bottnavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //speech to text init
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.others:
                    if (textSearchfor.getVisibility() == View.VISIBLE) {
                        textSearchfor.setVisibility(View.GONE);
                    } else {
                        textSearchfor.setVisibility(View.VISIBLE);
                    }
                    return true;
                case R.id.speaker:
                    if (!mIslistening)
                    {
                        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
                        }
                        else{
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        }
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            obtainedImage.setImageBitmap(photo);
            try {
                executeProcess(photo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void executeProcess(Bitmap photo) throws InterruptedException {
        final Uri tempUri = getImageUri(getApplicationContext(), photo);
        final RecognizeApi apiInstance = new RecognizeApi();
        // File | Image file to perform the operation on.  Common file formats such as PNG, JPEG are supported.
        final File imageFile = new File(getRealPathFromURI(tempUri));
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result = apiInstance.recognizeDetectObjects(imageFile);
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        thread.start();
        thread.join();
        obtainedItems(result);
    }

    public void obtainedItems(ObjectDetectionResult result) {
        deleteIcon.setVisibility(View.VISIBLE);
        backgroundText.setVisibility(View.GONE);
        if (result.getObjectCount() == 0) {
            Toast.makeText(this, "sorry couldn't recognise the object", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < result.getObjectCount(); i++) {
                String objectName = result.getObjects().get(i).getObjectClassName();
                arrayList.clear();
                arrayList.add(new ContactModel(objectName));
                capturedrecycle.setAdapter(new ContactAdapter());
                capturedrecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        }
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.fragment_capturedobject, parent, false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(root);
            return contactViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ContactViewHolder holder, int position) {
            holder.textView.setText(arrayList.get(position).getObjectName());
            holder.search_for.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), Mainitem_results.class);
                    i.putExtra("searchfor", holder.textView.getText().toString());
                    startActivity(i);
                }
            });
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView search_for;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            search_for = itemView.findViewById(R.id.search_for);
        }
    }

    public class ContactModel extends ViewModel {
        String objectName;
        public ContactModel(String objectName) {
            this.objectName = objectName;
        }
        public String getObjectName() {
            return objectName;
        }
    }
    //speech to text
    protected class SpeechRecognitionListener implements RecognitionListener
    {
        @Override
        public void onBeginningOfSpeech()
        {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error)
        {
            Toast.makeText(MainActivity.this, "Please Speak up", Toast.LENGTH_SHORT).show();
            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            Toast.makeText(MainActivity.this,"Mic Ready!", Toast.LENGTH_SHORT).show(); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            arrayList.clear();
            arrayList.add(new ContactModel(matches.get(0)));
            capturedrecycle.setAdapter(new ContactAdapter());
            capturedrecycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            deleteIcon.setVisibility(View.VISIBLE);
            // matches are the return values of speech recognition engine
            // Use these values for whatever you wish to do
        }
        @Override
        public void onRmsChanged(float rmsdB)
        {
        }
    }
    //
}