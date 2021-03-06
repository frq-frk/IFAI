package com.example.ifai;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private ImageButton upload;
    private RecyclerView mv_list;

    private FirebaseFirestore firestore;
    private FirestoreRecyclerAdapter madapter;
    private LinearLayoutManager manager;


    boolean doubleBackToExitPressedOnce = false, clicked = false;
    int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = findViewById(R.id.upload_btn);
        mv_list = findViewById(R.id.mv_list);

        manager = new LinearLayoutManager(this);

        mv_list.setHasFixedSize(true);
        mv_list.setLayoutManager(manager);

        firestore = FirebaseFirestore.getInstance();

        streaming();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });


    }

    //viewHolder class here

    private class MovieViewHolder extends RecyclerView.ViewHolder{

        private View mview;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setTitle(String film_title) {
            TextView title = mview.findViewById(R.id.mv_title);
            title.setText(film_title);
        }

        public void setPoster(MainActivity mainActivity, String poster_uri) {
            ImageView poster = mview.findViewById(R.id.mv_poster);
            Picasso.get().load(poster_uri).into(poster);
            Picasso.get().setLoggingEnabled(true);
        }

        public void setMaker(String uname) {
            TextView director = mview.findViewById(R.id.maker);
            director.setText(uname);
        }

        public void setDescription(String description) {
            TextView desc = mview.findViewById(R.id.description);
            desc.setText(description);
        }
    }

    private void streaming() {
        //Query to get documents from firestore
        Query query = firestore.collection("streaming");
        //RecyclerOptions
        FirestoreRecyclerOptions<Streaming> options = new FirestoreRecyclerOptions.Builder<Streaming>().
                setQuery(query,Streaming.class).build();

        madapter = new FirestoreRecyclerAdapter<Streaming, MovieViewHolder>(options){
            @NonNull
            @Override
            public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mv_view,parent,false);
                return new MovieViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MovieViewHolder holder, int position, @NonNull final Streaming model) {
                holder.setTitle(model.getFilm_title());
                holder.setPoster(MainActivity.this,model.getPoster_uri());
                holder.setDescription(model.getDescription());
                holder.setMaker(model.getUname());
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,user_stream.class);
                        intent.putExtra("title",model.getFilm_title());
                        intent.putExtra("description",model.getDescription());
                        intent.putExtra("email",model.getEmail());
                        intent.putExtra("uname",model.getUname());
                        intent.putExtra("film_uri",model.getFilm_uri());
                        intent.putExtra("poster_uri",model.getPoster_uri());
//                        System.out.println(model.getViews() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                            long views = model.getViews();
                            updateDatabase(model.getFilm_title(),model.getUid());
                            String v = String.valueOf(views);
                            intent.putExtra("views", v);
                            startActivity(intent);
                    }
                });
            }
        };
        mv_list.setAdapter(madapter);
        madapter.startListening();
    }

    private void updateDatabase(final String film_title, String uid) {

            firestore.collection("streaming").document(uid + film_title).update("views", FieldValue.increment(1));
                Toast.makeText(MainActivity.this, "Playing " + film_title, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //initialising the layout using id
    // assigning a click listener to the layout
    //so that on clicking 5 times it displayes a password dialogue.

    @SuppressLint("ResourceAsColor")
    public void admin_login(View view) {

        if(clicked) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                //todo the action here
                //i.e open dialog prompting password
                final EditText edittext = new EditText(view.getContext());
                edittext.setBackgroundResource(R.drawable.edittext_border);
                edittext.setHeight(80);
                edittext.setHint("Password");
                edittext.setFocusable(true);
                edittext.setGravity(Gravity.CENTER);
                edittext.setTextColor(R.color.white);


                final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext(),R.style.dialog);

                alert.setTitle("Welcome Admin");
                alert.setMessage("Enter the password");
                alert.setView(edittext);
                alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String pwd = edittext.getText().toString();
                        if (pwd.equals("Jormale 2020")) {
                            Intent intent = new Intent(MainActivity.this, Admin.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //kill the dialog
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(layoutParams);

                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }else {
                Toast.makeText(this, "you must be a user first to get admin access", Toast.LENGTH_SHORT).show();
            }
        }

            if (count == 4) {
                Toast.makeText(MainActivity.this, "you click one more time to reach admin dialog", Toast.LENGTH_SHORT).show();
                clicked = true;
                count = 0;
            }
            count += 1;
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    clicked = false;
                }
            }, 2000);


        }
}
