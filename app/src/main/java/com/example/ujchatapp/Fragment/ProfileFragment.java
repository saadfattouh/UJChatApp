package com.example.ujchatapp.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.example.ujchatapp.Activity.EditName;
import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.Permissions.Permissions;
import com.example.ujchatapp.R;
import com.example.ujchatapp.Utils.SharedPrefManager;
import com.example.ujchatapp.Utils.Util;
import com.example.ujchatapp.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private Uri imageUri;
    private Util util;
    private Permissions permissions;
    private AlertDialog alertDialog;
    private UserModel user;
    private SharedPreferences.Editor sharedPreferences;
    private DatabaseReference databaseReference;

    TextView profileName, fName, lName, statusTv, phoneTv;
    ImageView editStatusImg, pickProfileImg;
    CircleImageView profileImage;
    CardView nameCard;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.imgProfile);
        profileName = view.findViewById(R.id.txtProfileName);
        fName = view.findViewById(R.id.txtProfileFName);
        lName = view.findViewById(R.id.txtProfileLName);
        phoneTv = view.findViewById(R.id.txtProfileNumber);
        statusTv = view.findViewById(R.id.txtProfileStatus);
        editStatusImg = view.findViewById(R.id.imgEditStatus);
        pickProfileImg = view.findViewById(R.id.imgPickImage);
        nameCard = view.findViewById(R.id.cardName);

        util = new Util();
        sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE).edit();
        permissions = new Permissions();

        getUser();


        pickProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissions.isStorageOk(getContext()))
                    pickImage();
                else permissions.requestStorage(getActivity());
            }
        });

        editStatusImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_layout, null);
                builder.setView(view1);

                final EditText edtStatus = view1.findViewById(R.id.edtUserStatus);
                Button btnEditStatus = view1.findViewById(R.id.btnEditStatus);

                btnEditStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String status = edtStatus.getText().toString().trim();
                        if (!status.isEmpty()) {
                            editStatus(status);
                            alertDialog.dismiss();

                        }
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();

            }
        });

        nameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!user.getName().isEmpty()) {
                    Intent intent = new Intent(getContext(), EditName.class);
                    intent.putExtra("name", user.getName());
                    startActivityForResult(intent, AllConstants.USERNAME_CODE);
                } else {
                    Intent intent = new Intent(getContext(), EditName.class);
                    startActivityForResult(intent, AllConstants.USERNAME_CODE);
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                if (data != null) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == Activity.RESULT_OK) {
                        imageUri = result.getUri();
                        uploadImage(imageUri);
                    } else {
                        Log.d("image", "onActivityResult: " + result.getError());
                    }
                }
                break;
            case AllConstants.USERNAME_CODE:

                if (data != null) {

                    String name = data.getStringExtra("name");
                    editUsername(name);
                    if (name.contains(" ")) {
                        String[] split = name.split(" ");
                        fName.setText(split[0]);
                        lName.setText(split[1]);
                    } else {
                        fName.setText(name);
                        lName.setText("");
                    }
                    SharedPrefManager.getInstance(getContext()).setName(name);

                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AllConstants.STORAGE_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage();
                else
                    Toast.makeText(getContext(), "Storage Permission rejected.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void pickImage() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(getContext(), ProfileFragment.this);
    }

    private void uploadImage(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(util.getUID()).child(AllConstants.IMAGE_PATH);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String uri = task.getResult().toString();
                        editImage(uri);
                        sharedPreferences.putString("userImage", uri).apply();

                    }
                });
            }
        });
    }

    public void getUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(util.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    user = userModel;

                    SharedPrefManager.getInstance(getContext()).setName(user.getName());

                    statusTv.setText(user.getStatus());

                    String name = user.getName();
                    if (name.contains(" ")) {
                        String[] split = name.split(" ");
                        fName.setText(split[0]);
                        lName.setText(split[1]);
                    } else {
                        fName.setText(name);
                        lName.setText("");
                    }

                    phoneTv.setText(user.getNumber());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void editStatus(final String status) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.setStatus(status);
                    statusTv.setText(status);
                } else Log.d("status", "onComplete: " + task.getException());
            }
        });
    }

    public void editUsername(final String name) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.setName(name);
                    profileName.setText(name);

                    Log.d("name", "onComplete: Name Updated");
                } else Log.d("name", "onComplete:" + task.getException());
            }
        });
    }

    public void editImage(final String uri) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(util.getUID());
        Map<String, Object> map = new HashMap<>();
        map.put("image", uri);
        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.setImage(uri);
                    profileImage.setImageURI(Uri.parse(uri));
                    Log.e("image", "onComplete: Image updated");
                } else Log.e("image", "onComplete: " + task.getException());
            }
        });
    }
}