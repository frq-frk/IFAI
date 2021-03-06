package com.example.ifai;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.storage.OnProgressListener;
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
    private RecyclerView submitted,streaming;


    private Uri poster_path, video_path;
    private StorageReference mStorageRef;
    private FirebaseFirestore firestore;
    String poster_uri,film_uri;
    String UID;
    private FirestoreRecyclerAdapter madapter,sadapter;
    private LinearLayoutManager manager,smanager;

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
        bar.setVisibility(View.GONE);
        submitted = findViewById(R.id.submitted);
        streaming = findViewById(R.id.streaming);

        manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        submitted.setHasFixedSize(true);
        submitted.setLayoutManager(manager);

        smanager = new LinearLayoutManager(this);
        smanager.setOrientation(RecyclerView.HORIZONTAL);
        streaming.setHasFixedSize(true);
        streaming.setLayoutManager(smanager);





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
                //to prevent onBackpressed, Security concern
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);//similar to no cache header in jsp
                startActivity(intent);
            }
        });

        submitted();

        streaming();



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

                if(film_title.getText().toString().isEmpty()||description.getText().toString().isEmpty()||
                video_path == null||poster_path == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(upload.this,R.style.dialog);
                    builder.setTitle("Help!!!");
                    String msg = "1) No field must be empty\n\n2) Choose picture as the poster of your film by clicking on the gallery image on side of description" +
                            "\n\n3) click on chose file to select your completely edited final version of film to stream\n\n4) your film title must not be same as already existing one";
                    builder.setMessage(msg);
                    builder.setCancelable(false);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(layoutParams);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(upload.this, R.color.colorPrimary));
                } else {

                    bar.setVisibility(View.VISIBLE);
                    bar.setProgress(0);
                    final StorageReference poster = mStorageRef.child("potsers").child(UID + UUID.randomUUID().toString());   //at first poster is saved in storage of firebase
                    poster.putFile(poster_path).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {                                                  //when storing is successfull we get the uri of the poster we stored
                                poster.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        poster_uri = uri.toString();                          //after successfull storage of uri into a string variable
                                        final StorageReference film = mStorageRef.child("films").child(UID + UUID.randomUUID().toString());//we start to store the video file of the film in storage
                                        film.putFile(video_path).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                                bar.setProgress((int) progress);
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //on successful storing we get the uri of video
                                                if (task.isSuccessful()) {
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
            }
        });

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(upload.this,R.style.dialog);
                builder.setTitle("Confirmtion!!");
                builder.setMessage("Are you sure about logging out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(upload.this,MainActivity.class);//redirects to home instead of login
                        //to prevent onBackpressed, Security concern
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);//similar to no cache header in jsp
                        startActivity(intent);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            }
        });

    }


    //this function stores data into firestore
    //this is called from on submit click functionality after storing the image and video into storage reference
    //here along with the title and description we store the poster uri and video uri which are stored in string variable in on submit click function.
    private void SaveDatatoFirestore() {
        DocumentReference reference = firestore.collection("films").document(film_title.getText().toString());                                   //document(UID + film_title.getText().toString());
        Map<String,Object> film = new HashMap<>();
        film.put("film_title",film_title.getText().toString());
        film.put("description",description.getText().toString());
        film.put("poster_uri",poster_uri);
        film.put("film_uri",film_uri);
        film.put("uid",UID);
        reference.set(film).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialogentry();
            }
        });

    }

    private void dialogentry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog);
        builder.setTitle("Upload Successfull!!!");
        builder.setCancelable(false);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(upload.this,MainActivity.class);//after succesfull uploading we navigate to mainactivity coz I dont know how to refresh the same activity
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
        Query query = firestore.collection("films").whereEqualTo("uid",UID);
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

        //code to load the list of submitted files into recycler view

    private void streaming() {
        //Query
        Query query = firestore.collection("streaming").whereEqualTo("uid",UID);
        //recycler options
        FirestoreRecyclerOptions<Streaming> options = new FirestoreRecyclerOptions.Builder<Streaming>().
                setQuery(query,Streaming.class).build();
        sadapter = new FirestoreRecyclerAdapter<Streaming, FilmsViewHolder>(options) {

            @NonNull
            @Override
            public FilmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_poster,parent,false);
                return new FilmsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FilmsViewHolder holder, int position, @NonNull Streaming model) {
                holder.setTitle(model.getFilm_title());
                holder.setPoster(upload.this,model.getPoster_uri());
            }
        };

        streaming.setAdapter(sadapter);
        sadapter.startListening();
    }

}
