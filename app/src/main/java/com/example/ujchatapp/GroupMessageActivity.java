package com.example.ujchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devlomi.record_view.OnRecordListener;
import com.example.ujchatapp.Activity.GroupInfo;
import com.example.ujchatapp.Activity.UserInfo;
import com.example.ujchatapp.Constants.AllConstants;
import com.example.ujchatapp.Permissions.Permissions;
import com.example.ujchatapp.Utils.SharedPrefManager;
import com.example.ujchatapp.Utils.Util;
import com.example.ujchatapp.databinding.ActivityGroupMessageBinding;
import com.example.ujchatapp.databinding.ActivityMessageBinding;
import com.example.ujchatapp.databinding.LeftAudioGroupLayoutBinding;
import com.example.ujchatapp.databinding.LeftAudioItemLayoutBinding;
import com.example.ujchatapp.databinding.LeftGroupLayoutBinding;
import com.example.ujchatapp.databinding.LeftItemLayoutBinding;
import com.example.ujchatapp.databinding.RightAudioGroupLayoutBinding;
import com.example.ujchatapp.databinding.RightAudioItemLayoutBinding;
import com.example.ujchatapp.databinding.RightGroupLayoutBinding;
import com.example.ujchatapp.databinding.RightItemLayoutBinding;
import com.example.ujchatapp.services.SendMediaService;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class GroupMessageActivity extends AppCompatActivity {

    private ActivityGroupMessageBinding binding;
    private String groupImage, myID, chatID = null, myImage, myName, audioPath;
    private Util util;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<GroupMessageModel, GroupMessageActivity.ViewHolder> firebaseRecyclerAdapter;
    private SharedPreferences sharedPreferences;
    private Permissions permissions;
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_group_message, null, false);
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        myImage = sharedPreferences.getString("userImage", "");
        myName = sharedPreferences.getString("username", "");
        util = new Util();
        myID = util.getUID();
        permissions = new Permissions();

        if (getIntent().hasExtra("chat_id")) {
            chatID = getIntent().getStringExtra("chat_id");
            groupImage = getIntent().getStringExtra("group_image");
            readMessages(chatID);
        }


        binding.setImage(groupImage);
        binding.setActivity(this);

        binding.btnSend.setOnClickListener(v -> {

            String message = binding.msgText.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(GroupMessageActivity.this, "Enter Message...", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(message);
//                getToken(message, hisID, myImage, chatID);
            }

            binding.msgText.setText("");
            util.hideKeyBoard(GroupMessageActivity.this);
        });

        binding.msgText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {

                    binding.btnSend.setVisibility(View.GONE);
                    binding.recordButton.setVisibility(View.VISIBLE);

                } else {
                    binding.recordButton.setVisibility(View.GONE);
                    binding.btnSend.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnDataSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (binding.dataLayout.getVisibility() == View.INVISIBLE)
//                    showLayout();
//                else
//                    hideLayout();

                getGalleryImage();
            }
        });

//        binding.imgGallery.setOnClickListener(view -> {
//            getGalleryImage();
//        });

        initView();

    }


    private void sendMessage(String msg) {

        String date = util.currentData();
        GroupMessageModel messageModel = new GroupMessageModel(myID, msg, date, "text");
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(chatID).child("messages");
        databaseReference.push().setValue(messageModel);

        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(chatID).child("info");
        HashMap<String, Object> last = new HashMap<>();
        last.put("lastMessage", msg);
        last.put("lastMessageSender", SharedPrefManager.getInstance(this).getName() +": ");
        last.put("time", util.currentTime());
        databaseReference.updateChildren(last);
    }

    public void groupInfo() {
        Intent intent = new Intent(this, GroupInfo.class);
        intent.putExtra("chat_id", chatID);
        startActivity(intent);
    }

    private void readMessages(String chatID) {

        Query query = FirebaseDatabase
                .getInstance().getReference("Groups")
                .child(chatID).child("messages");
        FirebaseRecyclerOptions<GroupMessageModel> options = new FirebaseRecyclerOptions.Builder<GroupMessageModel>()
                .setQuery(query, GroupMessageModel.class).build();
        query.keepSynced(true);


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<GroupMessageModel, GroupMessageActivity.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int position, @NonNull GroupMessageModel messageModel) {
                switch (getItemViewType(position)) {
                    case 0:

                        viewHolder.viewDataBinding.setVariable(BR.messageImage, myImage);
                        viewHolder.viewDataBinding.setVariable(BR.message, messageModel);
                        break;

                    case 100:
                        viewHolder.viewDataBinding.setVariable(BR.messageImage, myImage);
                        viewHolder.voicePlayerView.setAudio(messageModel.getMessage());
                        break;
                    case 1:
                        viewHolder.viewDataBinding.setVariable(BR.messageImage, null);
                        viewHolder.viewDataBinding.setVariable(BR.message, messageModel);
                        break;
                    case 200:
                        viewHolder.viewDataBinding.setVariable(BR.messageImage, null);
                        viewHolder.voicePlayerView.setAudio(messageModel.getMessage());

                        break;
                }


            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ViewDataBinding viewDataBinding = null;
                switch (viewType) {
                    case 0:
                        viewDataBinding = RightGroupLayoutBinding.inflate(
                                LayoutInflater.from(getBaseContext()), parent, false);
                        break;
                    case 100:
                        viewDataBinding = RightAudioGroupLayoutBinding.inflate(
                                LayoutInflater.from(parent.getContext()), parent, false);
                        break;
                    case 1:
                        viewDataBinding = LeftGroupLayoutBinding.inflate(
                                LayoutInflater.from(getBaseContext()), parent, false);
                        break;
                    case 200:

                        viewDataBinding = LeftAudioGroupLayoutBinding.inflate(
                                LayoutInflater.from(parent.getContext()), parent, false);
                        break;
                }
                return new ViewHolder(viewDataBinding);

            }

            @Override
            public int getItemViewType(int position) {
                GroupMessageModel messageModel = getItem(position);
                if (myID.equals(messageModel.getSender())) {

                    if (messageModel.getType().equals("recording"))
                        return 100;
                    else return 0;

                } else {
                    if (messageModel.getType().equals("recording"))
                        return 200;
                    else return 1;
                }
            }
        };


        binding.recyclerViewMessage.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewMessage.setHasFixedSize(false);
        binding.recyclerViewMessage.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding viewDataBinding;
        private VoicePlayerView voicePlayerView;

        public ViewHolder(@NonNull ViewDataBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            this.viewDataBinding = viewDataBinding;

            if (viewDataBinding instanceof RightAudioGroupLayoutBinding) {
                voicePlayerView = ((RightAudioGroupLayoutBinding) viewDataBinding).voicePlayerView;

            }

            if (viewDataBinding instanceof LeftAudioGroupLayoutBinding) {
                voicePlayerView = ((LeftAudioGroupLayoutBinding) viewDataBinding).voicePlayerView;
            }

        }
    }


    private void showLayout() {
        RelativeLayout view = binding.dataLayout;
        float radius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getLeft(), view.getTop(), 0, radius * 2);
        animator.setDuration(800);
        view.setVisibility(View.VISIBLE);
        animator.start();

    }

    private void hideLayout() {

        RelativeLayout view = binding.dataLayout;
        float radius = Math.max(view.getWidth(), view.getHeight());
        Animator animator = ViewAnimationUtils.createCircularReveal(view, view.getLeft(), view.getTop(), radius * 2, 0);
        animator.setDuration(800);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public void onBackPressed() {

        if (binding.dataLayout.getVisibility() == View.VISIBLE)
            hideLayout();
        else
            super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 300) {
            if (data != null) {
                ArrayList<String> selectedImages = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                if (chatID == null)
                    Toast.makeText(this, "Send simple message first", Toast.LENGTH_SHORT).show();
                else {

                    Intent intent = new Intent(GroupMessageActivity.this, SendMediaService.class);
                    intent.putExtra("chatID", chatID);
                    intent.putStringArrayListExtra("media", selectedImages);

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                        startForegroundService(intent);
                    else startService(intent);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getGalleryImage();
                } else {
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }

                break;

            case AllConstants.RECORDING_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (this.permissions.isStorageOk(GroupMessageActivity.this))
                        binding.recordButton.setListenForRecord(true);
                    else this.permissions.requestStorage(GroupMessageActivity.this);

                } else
                    Toast.makeText(this, "Recording permission denied", Toast.LENGTH_SHORT).show();
                break;
            case AllConstants.STORAGE_REQUEST_CODE:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    binding.recordButton.setListenForRecord(true);
                else
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    private void getGalleryImage() {

        Options options = Options.init()
                .setRequestCode(300)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setExcludeVideos(true)                                       //Option to exclude videos
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/UJChatApp/Media");                                       //Custom Path For media Storage


        Pix.start(this, options);
    }

    private void initView() {

        binding.recordButton.setRecordView(binding.recordView);
        binding.recordButton.setListenForRecord(false);

        binding.recordButton.setOnClickListener(view -> {

            if (permissions.isRecordingOk(GroupMessageActivity.this))
                if (permissions.isStorageOk(GroupMessageActivity.this))
                    binding.recordButton.setListenForRecord(true);
                else permissions.requestStorage(GroupMessageActivity.this);
            else permissions.requestRecording(GroupMessageActivity.this);
        });

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                Log.d("RecordView", "onStart");

                setUpRecording();

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                binding.messageLayout.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");

                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();

                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);


            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                Log.d("RecordView", "onFinish");

                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                binding.recordView.setVisibility(View.GONE);
                binding.messageLayout.setVisibility(View.VISIBLE);

                sendRecodingMessage(audioPath);


            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if (file.exists())
                    file.delete();



                binding.recordView.setVisibility(View.GONE);
                binding.recyclerViewMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpRecording() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "UJChatApp/Media/Recording");

        if (!file.exists())
            file.mkdirs();
        audioPath = file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".3gp";

        mediaRecorder.setOutputFile(audioPath);
    }

    private void sendRecodingMessage(String audioPath) {
        if (chatID == null)
            Toast.makeText(this, "Send simple message first", Toast.LENGTH_SHORT).show();
        else {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(chatID + "/Media/Recording/" + myID + "/" + System.currentTimeMillis());
            Uri audioFile = Uri.fromFile(new File(audioPath));
            storageReference.putFile(audioFile).addOnSuccessListener(success -> {
                Task<Uri> audioUrl = success.getStorage().getDownloadUrl();

                audioUrl.addOnCompleteListener(path -> {
                    if (path.isSuccessful()) {

                        String url = path.getResult().toString();
                        if (chatID == null) {
                            Toast.makeText(this, "an error has occured, please try again later...", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(chatID).child("messages");
                            GroupMessageModel messageModel = new GroupMessageModel(myID, url, util.currentData(), "recording");
                            databaseReference.push().setValue(messageModel);
                        }
                    }
                });
            });
        }
    }

}