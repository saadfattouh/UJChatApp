package com.example.ujchatapp.Utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ujchatapp.R;
import com.google.android.material.textfield.TextInputEditText;


public class Validation {

    public static boolean validateInput(Context ctx, TextInputEditText...fields){
        for (EditText editText: fields) {
            if (editText.getText().toString().trim().isEmpty()){
                Toast.makeText(ctx, ctx.getResources().getString(R.string.missing_fields_message), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}