package com.example.duckluck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class UpdatePassword extends AppCompatActivity {

    private EditText newPassword;
    private Button update;

    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        setupUI();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userPasswordNew = newPassword.getText().toString();
                if(userPasswordNew.length() < 6){
                    Toast.makeText(UpdatePassword.this, "Password length must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(UpdatePassword.this, myProfile.class));
                                Toast.makeText(UpdatePassword.this, "Password Changed!", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(UpdatePassword.this, "Password Update Failed!", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });

    }

    private void setupUI() {
    newPassword = (EditText) findViewById(R.id.etNewPass);
    update = (Button) findViewById(R.id.btnChangePassw);
    }
}