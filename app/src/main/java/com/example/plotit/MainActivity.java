package com.example.plotit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int RC_PICK_CSV = 1;
    public static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.upload_button)
    Button uploadBtn;

    @BindView(R.id.seek_process)
    SeekBar mProgressBar;

    // Member Variables
    FirebaseStorage mFirebaseStorage;
    StorageReference mFirebaseStorgeReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.GONE);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseStorgeReference = mFirebaseStorage.getReference().child("csvs");
    }

    @OnClick(R.id.upload_button) void upload(){
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
        csvFilePicker();
    }

    void csvFilePicker(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(intent, RC_PICK_CSV);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PICK_CSV && resultCode == RESULT_OK){
            importCSV(new File(data.getData().getPath()), data);
        }
    }

    void importCSV(File path, Intent data){
        Log.d(TAG, "path of the file is : " + path.toString());
        Uri selectedCsvUri = data.getData();
        StorageReference csvReference = mFirebaseStorgeReference.child(selectedCsvUri.getLastPathSegment());
        csvReference.putFile(selectedCsvUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "File Sucessfully Uploaded", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });
    }
}
