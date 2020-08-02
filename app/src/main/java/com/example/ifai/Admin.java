package com.example.ifai;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {

    TextView count;

    FirebaseFirestore firestore;

    private FirestoreRecyclerAdapter madapter;
    private LinearLayoutManager manager;

    RecyclerView accept_list;


    ArrayList<String> uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        count = findViewById(R.id.count);

        manager = new LinearLayoutManager(this);

        accept_list = findViewById(R.id.accept_list);
        accept_list.setHasFixedSize(true);
        accept_list.setLayoutManager(manager);
     //   uid = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        //code to diplay the total number of users
        //not included to view details of users yet
        firestore.collection("Users").get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty()){
                                Toast.makeText(Admin.this, "error in getting user count", Toast.LENGTH_SHORT).show();
                            }else{
                                String c = String.valueOf(queryDocumentSnapshots.size() - 1);
                                count.setText(c);
                            }
                    }
                });

        //calling a method to display the list of pending films to be accepted
        submitted();

    }

    //method to display the list of pending films to be accepted

    private void submitted() {
        //query to get data from firebase
        Query query = firestore.collection("films");
        //Recycler options
        FirestoreRecyclerOptions<FilmsModel> options = new FirestoreRecyclerOptions.Builder<FilmsModel>().
                setQuery(query, FilmsModel.class).build();

        madapter = new FirestoreRecyclerAdapter<FilmsModel,FilmViewHolder>(options) {

            @NonNull
            @Override
            public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mv_view_admin,parent,false);
                return new FilmViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FilmViewHolder holder, int position, @NonNull final FilmsModel model) {
                holder.setTitle(model.getFilm_title());
                holder.setPoster(Admin.this,model.getPoster_uri());
                holder.setDescription(model.getDescription());
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {    //on click listeners for items in recycler view
                        Intent intent = new Intent(Admin.this,Stream.class); //redirects to the stream activity where the video is played
                        intent.putExtra("title",model.getFilm_title());
                        intent.putExtra("film_url",model.getFilm_uri());
                        intent.putExtra("poster_uri",model.getPoster_uri());
//                        System.out.println(model.getPoster_uri() + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                        intent.putExtra("description",model.getDescription());
                        intent.putExtra("uid",model.getUid());
                        startActivity(intent);
                    }
                });
            }
        };
        accept_list.setAdapter(madapter);
        madapter.startListening();

}

    //view Holder here
    private class FilmViewHolder extends RecyclerView.ViewHolder {

        private View mview;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }


        public void setPoster(Admin upload, String film_poster) {
            ImageView poster = mview.findViewById(R.id.mv_pstr);
            Picasso.get().load(film_poster).into(poster);
            Picasso.get().setLoggingEnabled(true);
        }

        public void setTitle(String film_title) {
            TextView title = mview.findViewById(R.id.mv_ttl);
            title.setText(film_title);
        }
        public void setDescription(String description) {
            TextView desc = mview.findViewById(R.id.mv_desc);
            desc.setText(description);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Admin.this,MainActivity.class);
        startActivity(intent);
    }
}

