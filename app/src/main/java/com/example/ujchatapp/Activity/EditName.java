package com.example.ujchatapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.R;
import com.example.ujchatapp.Utils.Util;

import java.util.Objects;



public class EditName extends AppCompatActivity {


    Button doneBtn;
    EditText fNameEt, lNameEt;

    private String fName, lName;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        util = new Util();

        fNameEt = findViewById(R.id.edtFName);
        lNameEt = findViewById(R.id.edtLName);
        doneBtn = findViewById(R.id.btnEditName);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("name")) {
            String name = getIntent().getStringExtra("name");
            if (name.contains(" ")) {
                String[] split = name.split(" ");
                fNameEt.setText(split[0]);
                lNameEt.setText(split[1]);

            } else {
                fNameEt.setText(name);
                lNameEt.setText("");
            }
        }

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFName() & checkLName()) {
                    Intent intent = new Intent();
                    intent.putExtra("name", fName + " " + lName);
                    setResult(AllConstants.USERNAME_CODE, intent);
                    finish();
                }
            }
        });
    }

    private boolean checkFName() {
        fName = fNameEt.getText().toString().trim();
        if (fName.isEmpty()) {
            fNameEt.setError("Field is required");
            return false;
        } else {
            fNameEt.setError(null);
            return true;
        }
    }

    private boolean checkLName() {
        lName = lNameEt.getText().toString().trim();
        if (fName.isEmpty()) {
            lNameEt.setError("Field is required");
            return false;
        } else {
            lNameEt.setError(null);
            return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        util.updateOnlineStatus(getResources().getString(R.string.online));
        super.onResume();
    }

    @Override
    protected void onPause() {
        util.updateOnlineStatus(String.valueOf(System.currentTimeMillis()));
        super.onPause();
    }


}