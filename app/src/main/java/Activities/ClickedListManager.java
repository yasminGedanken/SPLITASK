package Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Adapters.ListAdapter;

public class ClickedListManager extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid()).child("Created lists");
    private TextView listName;


    //This Activity is a dynamic activity.
    //It contains a List, and fill it by the current list that have been clicked in the previous page.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_list_manager);
        setupUI();

        String key = getIntent().getExtras().getString("listKey");
        key.replaceAll("\\s", "");
        int keyNumber = Integer.parseInt(key);

        final ListView list = findViewById(R.id.listClicked);
        ArrayList<String> clickedList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, clickedList);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) list.getItemAtPosition(position);

            }
        });

        //Looking for a match up between a list and the key.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot uniqueUserSnapshot : dataSnapshot.getChildren()) {
                    ListAdapter LA = uniqueUserSnapshot.getValue(ListAdapter.class);
                    if (LA.getId() == keyNumber) {
                        listName.setText(LA.getName());
                        for (int j = 0; j < LA.getList().size(); j++) {
                            clickedList.add((LA.getList().get(j)));
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupUI() {
        listName = (TextView) findViewById(R.id.freindListName);
    }
}