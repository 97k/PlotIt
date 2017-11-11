package com.example.plotit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int RC_PICK_CSV = 1;
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 2;
    private static final String ANONYMOUS = "anonymous";
    @BindView(R.id.upload_button)
    Button uploadBtn;

    @BindView(R.id.seek_process)
    SeekBar mProgressBar;

    // Firebase Stuff
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mFirebaseStorgeReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    // Local Variables
    private String mUsername;

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFirebaseAuthListener != null)
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.GONE);
        getSupportActionBar().hide();
        mUsername = ANONYMOUS;
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("head"); /* TODO:// Update here! */
        mFirebaseStorgeReference = mFirebaseStorage.getReference().child("csvs");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //user is signed in
                    onSignedInInitialize(firebaseAuth.getCurrentUser().getDisplayName());
                } else {
                    // user is not signed in
                    Log.i(TAG, "User is not signed in");
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @OnClick(R.id.upload_button) void upload(){
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(true);
        csvFilePicker();
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
    }

    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;

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
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.logIn_sucess), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.sign_in_err), Toast.LENGTH_SHORT).show();
                finish();
            }
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
