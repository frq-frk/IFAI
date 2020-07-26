package com.example.ifai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class upload extends AppCompatActivity {

    private long mLastClickTime = 0;


    private ImageView poster;
    private EditText description,film_title;
    private Button chose_file,submit,logout;
    private TextView file_path,username;
    private ImageButton back;
    private ProgressBar bar;
    private RecyclerView submitted;


    private Uri poster_path, video_path;
    private StorageReference mStorageRef;
    private FirebaseFirestore firestore;
    String poster_uri,film_uri;
    String UID;
    private FirestoreRecyclerAdapter madapter;
    private LinearLayoutManager manager;

//    String title,desc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        file_path=findViewById(R.id.file_path);
        description=findViewById(R.id.desc);
        film_title = findViewById(R.id.film_name);
        username = findViewById(R.id.username);
        bar = findViewById(R.id.loading);
        submitted = findViewById(R.id.submitted);

        manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);

        submitted.setHasFixedSize(true);

        submitted.setLayoutManager(manager);



//        title = film_title.getText().toString();
//        desc = description.getText().toString();

         UID = getIntent().getStringExtra("UID");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();


        //this code loads the username from the firestore and displays it on the top of the screen
        firestore.collection("Users").document(UID).get()   //captures the whole snapshot of the document
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){   //to check if the document of given path exists
                            String uname = documentSnapshot.getString("uname"); //if so copy a value from the snapshot to string using a key
                            username.setText(uname); //set the value to the UI field
                        }else {
                            Toast.makeText(upload.this, "User details doesn't exist contact developer", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //back Button
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(upload.this,MainActivity.class);
                startActivity(intent);
            }
        });

        submitted();



        //code to choose image button
        //gets related to OnActivityResult method with request code 1;
        poster = findViewById(R.id.poster);
        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image as poster"),1);
            }
        });



        //code to chose the video file to upload
        //gets relaated to OnActivityResult Method with request code 2;
        chose_file=findViewById(R.id.chose_file);
        chose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"chose your film"),2);
            }
        });

        //function to submit the entry of film
        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

              bar.setVisibility(View.VISIBLE);
             final StorageReference poster = mStorageRef.child("potsers").child(UID + UUID.randomUUID().toString());   //at first poster is saved in storage of firebase
             poster.putFile(poster_path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                  if(task.isSuccessful()){                                                  //when storing is successfull we get the uri of the poster we stored
                      poster.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {
                              poster_uri = uri.toString();                          //after successfull storage of uri into a string variable
                              final StorageReference film = mStorageRef.child("films").child(UID + UUID.randomUUID().toString());//we start to store the video file of the film in storage
                              film.putFile(video_path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //on successful storing we get the uri of video
                                          if(task.isSuccessful()){
                                              film.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                  @Override
                                                  public void onSuccess(Uri uri) {
                                                     film_uri = uri.toString();             //after storing the uri to string variable
                                                     SaveDatatoFirestore();                 //we call a function which starts storing data into firestore
                                                  }
                                              });
                                          }
                                  }
                              });
                          }
                      });
                  }
                 }
             });


            }
        });

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(upload.this,MainActivity.class);//redirects to home instead of login
                //to prevent onBackpressed, Security concern
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| intent.FLAG_ACTIVITY_CLEAR_TASK);//similar to no cache header in jsp
                startActivity(intent);
            }
        });

    }


    //this function stores data into firestore
    //this is called from on submit click functionality after storing the image and video into storage reference
    //here along with the title and description we store the poster uri and video uri which are stored in string variable in on submit click function.
    private void SaveDatatoFirestore() {
        DocumentReference reference = firestore.collection("films").document(UID).collection("submitted").document(film_title.getText().toString());                                   //document(UID + film_title.getText().toString());
        Map<String,Object> film = new HashMap<>();
        film.put("film_title",film_title.getText().toString());
        film.put("description",description.getText().toString());
        film.put("poster_uri",poster_uri);
        film.put("film_uri",film_uri);
        reference.set(film).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(upload.this, "upload successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(upload.this,MainActivity.class);//after succesfull uploading we navigate to mainactivity coz I dont know how to refresh the same activity
                startActivity(intent);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK
            && data!=null && data.getData()!=null){
            poster_path=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),poster_path);
                poster.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode==2 && resultCode==RESULT_OK
            && data!=null && data.getData()!=null){
            video_path=data.getData();
            file_path.setText(video_path.toString());
        }

    }

    //view Holder here
    private class FilmsViewHolder extends RecyclerView.ViewHolder {

        private View mview;

        public FilmsViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setPoster(upload upload, String film_poster) {
            ImageView poster = mview.findViewById(R.id.poster_view);
            Picasso.get().load(film_poster).into(poster);
            Picasso.get().setLoggingEnabled(true);
        }

        public void setTitle(String film_title) {
            TextView title = mview.findViewById(R.id.film_title);
            title.setText(film_title);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        madapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        madapter.stopListening();
//    }


    //code to load the list of submitted films into recycles view
    private void submitted(){
        //Query to get documents from firestore
        Query query = firestore.collection("films").document(UID).collection("submitted");
        //RecyclerOptions
        FirestoreRecyclerOptions<FilmsModel> options = new FirestoreRecyclerOptions.Builder<FilmsModel>().
                setQuery(query,FilmsModel.class).build();

        madapter = new FirestoreRecyclerAdapter<FilmsModel, FilmsViewHolder>(options) {
            @NonNull
            @Override
            public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_poster,parent,false);
                return new FilmsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FilmsViewHolder holder, int position, @NonNull FilmsModel model) {
//                System.out.println("aaaaaaaaaaa"+ model.getFilm_title());  //for debugging purpose
                holder.setTitle(model.getFilm_title());
                holder.setPoster(upload.this,model.getPoster_uri());
            }
        };
        submitted.setAdapter(madapter);
        madapter.startListening();
    }
}
