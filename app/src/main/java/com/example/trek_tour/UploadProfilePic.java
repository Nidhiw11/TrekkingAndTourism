 package com.example.trek_tour;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

 public class UploadProfilePic extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("Upload Profile Pic");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Button buttonUploadPicChoose=findViewById(R.id.upload_Button);
        Button buttonUploadPic=findViewById(R.id.upload_pic_button);
        progressBar=findViewById(R.id.progress3);
        imageViewUploadPic=findViewById(R.id.imageView_Profile_dp);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri=firebaseUser.getPhotoUrl();

        //Set User's current DP in imageview(If uploaded already, picasso imageViewer)
        //Regular URI's
        Picasso.with(UploadProfilePic.this).load(uri).into(imageViewUploadPic);
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });

        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(view.VISIBLE);
                UploadPic();
            }
        });
    }
    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
             uriImage=data.getData();
             imageViewUploadPic.setImageURI(uriImage);

         }

     }

     private void UploadPic(){
        if(uriImage!=null){
            //set the image with uid of the currently logged User
            StorageReference filereference=storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));
            //upload image to storage
            filereference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           Uri downloadUri=uri;
                           firebaseUser=authProfile.getCurrentUser();
                           //finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                }
            });
        }
     }
     //obtain File Extension
     private String getFileExtension(Uri uri){
         ContentResolver cR=getContentResolver();
         MimeTypeMap mime=MimeTypeMap.getSingleton();
         return mime.getExtensionFromMimeType(cR.getType(uri));
     }

     //Creating ActionBAr Menu
     public boolean onCreateOptionsMenu(Menu menu) {
         //Inflate menu items
         getMenuInflater().inflate(R.menu.common_menu,menu);
         return super.onCreateOptionsMenu(menu);
     }

     //When any menu item is selected
     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         int id= item.getItemId();
         if(id==android.R.id.home){
             NavUtils.navigateUpFromSameTask(UploadProfilePic.this);
         }else if(id==R.id.menu_refresh){
             //Refresh Activity
             startActivity(getIntent());
             finish();
             overridePendingTransition(0,0);
         }  else if (id == R.id.menu_user_profile) {
             Intent intent=new Intent(UploadProfilePic.this,UserProfile.class);
             startActivity(intent);
         } else if (id == R.id.menu_update_profile) {
             Intent  intent=new Intent(UploadProfilePic.this,UpdateP.class);
             startActivity(intent);
         }else if(id==R.id.menu_settings){
             Intent  intent=new Intent(UploadProfilePic.this,feedback.class);
             startActivity(intent);
        }else if(id==R.id.menu_Logout){
             authProfile.signOut();
             Toast.makeText(UploadProfilePic.this, "Logged Out", Toast.LENGTH_SHORT).show();
             Intent intent=new Intent(UploadProfilePic.this,MainActivity.class);

             //Clear stack to prevent user coming back to DashBoard on back button after Logging out
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(intent);
             finish();//close DashBoard
         }else{
             Toast.makeText(UploadProfilePic.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
         }
         return super.onOptionsItemSelected(item);
     }
}