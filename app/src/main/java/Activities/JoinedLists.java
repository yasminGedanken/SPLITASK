package Activities;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapters.JoinedListAdapter;
import Adapters.ListAdapter;


public class JoinedLists extends Activity implements View.OnClickListener,
        AdapterView.OnItemLongClickListener {


   private Dialog dialog;

    private TextView returnBack;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference();
    private DatabaseReference JoinedListsRef = databaseReference.child(firebaseAuth.getUid()).child("Joined lists");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_list);
        setupUI();

        //Creating a Linear view List to be shown in this activity.
        final ListView list = findViewById(R.id.list);
        ArrayList<String> friendListHistory = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendListHistory);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) list.getItemAtPosition(position);
                String sp[] = clickedItem.split("ID:");
                String key = sp[1];
                Intent i = new Intent(JoinedLists.this, clickedJoinedList.class);
                i.putExtra("listKey", key);
                startActivity(i);
            }
        });

        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(JoinedLists.this, LoggedInProfile.class));
            }
        });

        findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
              @Override
            public void onClick(View v) {
                dialog = new Dialog(JoinedLists.this);
                dialog.setContentView(R.layout.layout_goin_list);
                dialog.findViewById(R.id.action_cancel).setOnClickListener(
                        JoinedLists.this);
                dialog.findViewById(R.id.action_ok).setOnClickListener(
                        JoinedLists.this);
                dialog.show();
            }
        });


        //looping on ALL the database looking for list that the user is part of.
        JoinedListsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot uniqueUserSnapshot1 : dataSnapshot1.getChildren()) {
                    JoinedListAdapter JLA = uniqueUserSnapshot1.getValue(JoinedListAdapter.class);
                    arrayAdapter.notifyDataSetChanged();

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            for (DataSnapshot uniqueUserSnapshot2 : dataSnapshot2.getChildren()) {
                                for (DataSnapshot uniqueUserSnapshot3 : uniqueUserSnapshot2.child("Created lists").getChildren()) {
                                    ListAdapter LA = uniqueUserSnapshot3.getValue(ListAdapter.class);

                                    if (JLA.getId().equals(LA.getId() + "")) {
                                        friendListHistory.add(LA.getName() + " ID:" + LA.getId());
                                        arrayAdapter.notifyDataSetChanged();

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    private void setupUI() {

        returnBack = (TextView) findViewById(R.id.returnK);
    }
    //This is the dialog
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_cancel:
                dialog.dismiss();
                break;

            case R.id.action_ok:
                String text = ((EditText) dialog.findViewById(R.id.input))
                        .getText().toString();
                if (null != text && 0 != text.compareTo("")) {
                    JoinedListAdapter JLA = new JoinedListAdapter(text);
               DatabaseReference newListRef = JoinedListsRef.push();
                newListRef.setValue(JLA);
                    dialog.dismiss();

                }
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

}