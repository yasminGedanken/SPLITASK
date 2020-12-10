package Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Adapters.ListAdapter;


public class addNewList extends Activity implements OnClickListener,
        OnItemLongClickListener {

    private ArrayList<String> datasource;
    private MyAdapter adapter;
    private Dialog dialog;
    private EditText listName;
    private Button save;

    //Auto functions
    private FirebaseAuth firebaseAuth;

    //The data base itself
    private FirebaseDatabase firebaseDatabase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_list);

        //Connecting akk the buttons to the xml by id
        setupUI();

        datasource = new ArrayList<String>();
        adapter = new MyAdapter();


        //creating a list with Adapter
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(this);

        //Listener to the 'JOIN' button, open a dialog when clicked
        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(addNewList.this);
                dialog.setContentView(R.layout.dialog_layout);
                dialog.findViewById(R.id.button_cancel).setOnClickListener(
                        addNewList.this);
                dialog.findViewById(R.id.button_ok).setOnClickListener(
                        addNewList.this);
                dialog.show();
            }
        });

        //saving a list and add it to the database.
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListToDB(listName.getText().toString(), datasource);
                finish();
                startActivity(new Intent(addNewList.this, CreatedLists.class));
            }

        });
    }

    //this functions adds the new added list to the database,
    //under the person who created it.
    public void addListToDB(String name, ArrayList<String> addedList) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        int id = (int) (Math.random() * (10000 - 1)) + 1;
        ListAdapter newList = new ListAdapter(name, addedList, firebaseAuth.getUid(), id);
        DatabaseReference newListRef = databaseReference.child("Created lists").push();
        newListRef.setValue(newList);

    }

    //the dialog options, get the input from the user.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_cancel:
                dialog.dismiss();
                break;

            case R.id.button_ok:
                String text = ((EditText) dialog.findViewById(R.id.edit_box))
                        .getText().toString();
                if (null != text && 0 != text.compareTo("")) {
                    datasource.add(text);
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void setupUI() {
        listName = (EditText) findViewById(R.id.listname);
        save = (Button) findViewById(R.id.SaveButton);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> listView, View view,
                                   int position, long column) {
        datasource.remove(position);
        adapter.notifyDataSetChanged();
        return true;
    }
//Adapter for the list
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return datasource.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return datasource.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) convertView;
            if (null == view) {
                view = new TextView(addNewList.this);
                view.setPadding(10, 10, 10, 10);
            }
            view.setText(datasource.get(position));
            return view;
        }
    }
}





