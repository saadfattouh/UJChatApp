package com.example.ujchatapp.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ujchatapp.GroupMessageModel;
import com.example.ujchatapp.MessageModel;
import com.example.ujchatapp.R;
import com.example.ujchatapp.Utils.Util;
import com.example.ujchatapp.Utils.Validation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {

    TextInputEditText mTitle, mSection, mCourseCode, mInstructor;
    ImageView mAddImageBtn;
    Button mCreateGroupBtn;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    Uri imageUri;
    String filePath = null;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mTitle = findViewById(R.id.title);
        mSection = findViewById(R.id.section);
        mCourseCode = findViewById(R.id.course_code);
        mInstructor = findViewById(R.id.instructor);
        mAddImageBtn = findViewById(R.id.upload_btn);
        mCreateGroupBtn = findViewById(R.id.create_btn);

        mAddImageBtn.setOnClickListener(v -> {
            requestRead();
        });

        mCreateGroupBtn.setOnClickListener(v -> {
            if(Validation.validateInput(this, mTitle, mSection, mCourseCode, mInstructor)){
                if(filePath != null){
                    createGroup();
                }else {
                    Toast.makeText(this, "select the1group image first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createGroup() {
        uploadImage(compressImage(filePath));
    }


    //..................Methods for File Chooser.................
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openFileChooser();
        }
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Uri picUri = imageUri;
            filePath = getPath(picUri);
            if (filePath != null) {
                bitmap = BitmapFactory.decodeFile(filePath);
                Log.d("filePath", String.valueOf(filePath));
            }
            else
            {
                Toast.makeText(this,"no image selected", Toast.LENGTH_LONG).show();
            }

            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(bitmap)
                    .into(mAddImageBtn);
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = this.getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    //..............................................................................

    private String compressImage(String fileName) {

        File file = new File(fileName);

        return SiliCompressor.with(this).compress(fileName, file, false);
    }


    private void uploadImage(String fileName) {

        String key = FirebaseDatabase.getInstance().getReference().child("Groups").push().getKey();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(key + "/Images/"  + System.currentTimeMillis());
        Uri uri = imageUri;
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnCompleteListener(uri -> {
                    if (uri.isSuccessful()) {
                        String url = uri.getResult().toString();

                        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("Groups").child(key).child("info");

                        HashMap newChatMap = new HashMap();
                        newChatMap.put("id", key);
                        newChatMap.put("title", mTitle.getText().toString());
                        newChatMap.put("section", mSection.getText().toString());
                        newChatMap.put("courseCode", mCourseCode.getText().toString());
                        newChatMap.put("instructorName", mInstructor.getText().toString());
                        newChatMap.put("image", url);
                        newChatMap.put("time", new Util().currentTime());
                        newChatMap.put("lastMessage", "");
                        newChatMap.put("lastMessageSender", "");
                        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), FirebaseAuth.getInstance().getUid());
                        chatInfoDb.updateChildren(newChatMap);

                        Toast.makeText(CreateGroupActivity.this, "created successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
            }
        });
    }
}