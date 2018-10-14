package com.example.jhc51.docusignfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PostingActivity extends AppCompatActivity {
    private Button postBtn;
    private EditText title, duration, rate, description;
    private UserDatabase userDB = new UserDatabase();
    Globals g = Globals.getInstance();

    private ImageView imageView;

    private Uri filePath;
    Uri downloadUri;
    private final int PICK_IMAGE_REQUEST = 71;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    Button btnCamera, btnUpload;


    private HashMap<String, Object> createData(String title, double rate, int duration, String description, String url) {
        System.out.println(title + " " + rate + " " + duration + " " + description);
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", title);
        data.put("rate", rate);
        data.put("duration", duration);
        data.put("description", description);
        data.put("url", url);
        data.put("loaner", false);
        data.put("renter", false);
        data.put("owner", g.getRealName());
        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        imageView = (ImageView) findViewById(R.id.imageView);

        /*btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });*/

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filePath != null){
                    uploadImage();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Need to pick an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });*/


        title = (EditText)findViewById(R.id.postTitle);
        duration = (EditText)findViewById(R.id.postDuration);
        rate = (EditText)findViewById(R.id.postPrice);
        description = (EditText)findViewById(R.id.postDesciption);
        postBtn = (Button)findViewById(R.id.btn_post);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().isEmpty() || duration.getText().toString().isEmpty() || rate.getText().toString().isEmpty() || description.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Missing fields", Toast.LENGTH_SHORT).show();
                }
                else if(downloadUri.toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Missing image upload", Toast.LENGTH_SHORT).show();
                }
                else{
                String titleString = title.getText().toString();
                int durationTime = Integer.parseInt(duration.getText().toString());
                double ratePrice = Double.parseDouble(rate.getText().toString());
                String desc = description.getText().toString();
                HashMap<String, Object> data = createData(titleString, ratePrice, durationTime, desc, downloadUri.toString());
                userDB.addItem(g.getEmail(), titleString, data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("add", "add fail");
                        }
                        else {
                            startActivity(new Intent(PostingActivity.this, MainActivity.class));
                        }
                    }
                });}
            }
        });
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
        StorageReference ref = FirebaseStorage.getInstance().getReference();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] image = baos.toByteArray();

        UploadTask task = ref.pu
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        Toast.makeText(getApplicationContext(), "Uploading please wait", Toast.LENGTH_SHORT).show();
        final StorageReference ref = storageReference.child(g.getEmail());
        UploadTask uploadTask = ref.putFile(filePath);
        Task<Uri>urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    downloadUri = task.getResult();
                    Toast.makeText(getApplicationContext(), "Finish Upload Image", Toast.LENGTH_SHORT).show();
                }else{
                    //handle exception
                }
            }
        });
    }

}
