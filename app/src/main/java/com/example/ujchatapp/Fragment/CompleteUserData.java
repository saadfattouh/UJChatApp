package com.example.ujchatapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ujchatapp.Activity.DashBoard;
import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.Permissions.Permissions;
import com.example.ujchatapp.R;
import com.example.ujchatapp.UserModel;
import com.example.ujchatapp.Utils.SharedPrefManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;



public class CompleteUserData extends Fragment {

    CircleImageView profileImage;
    ImageView selectNewImageBtn;
    Button doneBtn;
    EditText nameEt, statusEt, profileEt, emailEt, studentIdEt;



    private String storagePath, name, status, email, profileName, studentID;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;

    private Permissions permissions;
    private SharedPreferences sharedPreferences;

    UserModel user;



    public CompleteUserData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_complete_user_data, container, false);


        profileImage = view.findViewById(R.id.imgUser);
        selectNewImageBtn = view.findViewById(R.id.imgPickImage);
        doneBtn = view.findViewById(R.id.btnDataDone);
        nameEt = view.findViewById(R.id.edtUserName);
        statusEt = view.findViewById(R.id.edtUserStatus);
        profileEt = view.findViewById(R.id.edtUserProfileName);
        emailEt = view.findViewById(R.id.edtUserEmail);
        studentIdEt = view.findViewById(R.id.edtUserStudentId);

        user = (UserModel) getArguments().getSerializable("user");


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        storagePath = firebaseAuth.getUid() + AllConstants.IMAGE_PATH;
        permissions = new Permissions();
        sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);

        selectNewImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (permissions.isStorageOk(getContext()))
                    pickImage();
                else
                    permissions.requestStorage(getActivity());
            }
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStatus() & checkImage() & checkName() & checkEmail() & checkProfileName() & checkStudentID())
                    uploadData();
            }
        });


        return view;
    }

    private void uploadData() {

        Toast.makeText(getContext(), "Uploading", Toast.LENGTH_SHORT).show();
        storageReference.child(storagePath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String url = task.getResult().toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("status", status);
                        map.put("image", url);
                        map.put("studentID", studentID);
                        map.put("email", email);
                        map.put("profileName", profileName);
                        map.put("token", user.getToken());
                        map.put("uID", user.getuID());
                        map.put("typing", user.getTyping());
                        map.put("number", user.getNumber());
                        map.put("online", user.getOnline());
                        map.put("type", user.getType());
                        SharedPrefManager.getInstance(getContext()).setName(name);
                        databaseReference.child(firebaseAuth.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("userImage", url).apply();
                                    editor.putString("username", name).apply();
                                    Intent intent = new Intent(getContext(), DashBoard.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else
                                    Toast.makeText(getContext(), "Fail to upload", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage();
                else
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void pickImage() {

        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imageUri = result.getUri();
                    profileImage.setImageURI(imageUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

    private boolean checkName() {
        name = nameEt.getText().toString().trim();
        if (name.isEmpty()) {
            nameEt.setError("Filed is required");
            return false;
        } else {
            nameEt.setError(null);
            return true;
        }
    }

    private boolean checkStatus() {
        status = statusEt.getText().toString();
        if (status.isEmpty()) {
            statusEt.setError("Filed is required");
            return false;
        } else {
            statusEt.setError(null);
            return true;
        }
    }

    private boolean checkProfileName() {
        profileName = profileEt.getText().toString();
        if (profileName.isEmpty()) {
            profileEt.setError("Filed is required");
            return false;
        } else {
            profileEt.setError(null);
            return true;
        }
    }
    private boolean checkEmail() {
        email = emailEt.getText().toString();
        if (email.isEmpty()) {
            emailEt.setError("Filed is required");
            return false;
        } else {
            emailEt.setError(null);
            return true;
        }
    }

    private boolean checkStudentID() {
        studentID = studentIdEt.getText().toString();
        if (studentID.isEmpty()) {
            studentIdEt.setError("Filed is required");
            return false;
        } else {
            studentIdEt.setError(null);
            return true;
        }
    }


    private boolean checkImage() {
        if (imageUri == null) {
            Toast.makeText(getContext(), "Image is required", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }
}
