//package com.example.plotit;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GraphActivity extends AppCompatActivity {
//
//    private FirebaseDatabase mFirebaseDatabase;
//    private DatabaseReference mFirebaseDatabaseReference;
//    private ChildEventListener mChildEventListener;
//    private Spinner mHeaderSpinner;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_graph);
//        getSupportActionBar().setTitle("Mode");
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        mFirebaseDatabaseReference = mFirebaseDatabase.getReference().child("headers");
//        populateSpinner();
//    }
//
//    private void attachDatabaseReadListener(){
//        final List<String> headers = new ArrayList<>();
//
//        if (mChildEventListener==null){
//            if (mHeaderSpinner==null)
//            headers.clear();
//            mChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    headers.add(dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//            mFirebaseDatabaseReference.addChildEventListener(mChildEventListener);
//        }
//    }
//    void populateSpinner() {
//        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<String> headers = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String header = snapshot.child("headers").getValue(String.class);
//                    headers.add(header);
//                    Log.e("checking headers", header);
//                }
//
//
//                ArrayAdapter<String> headerAdapter = new ArrayAdapter<String>(GraphActivity.this, android.R.layout.simple_spinner_item, headers);
//                headerSpinner.setAdapter(headerAdapter);
//                headerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//}
