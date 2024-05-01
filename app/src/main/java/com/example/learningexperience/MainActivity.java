package com.example.learningexperience;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore fStore;

    Button logoutButton, goToQuizButton;

    ImageView profileImage;
    TextView welcomeText, textView;
    StorageReference storageReference;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        goToQuizButton = findViewById(R.id.goToQuiz);
        logoutButton = findViewById(R.id.logoutButton);
        profileImage = findViewById(R.id.profileImage);

        user = auth.getCurrentUser();

        welcomeText = findViewById(R.id.welcomeMessage);
        textView = findViewById(R.id.textView);

        listView = findViewById(R.id.listView);


        //get profile image from storage
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+user.getUid()+"/profile.jpeg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        //get username
        CollectionReference usersCollection = fStore.collection("users");

        usersCollection.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        String username = document.getString("username");
                                        String text = ("Hello, " + username);
                                        welcomeText.setText(text);
                                    }
                                }
                            }
                        });



        //log out user when logout button is clicked
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        goToQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //get List of selected interests
        String userId = auth.getCurrentUser().getUid();
        DocumentReference docRef = fStore.collection("users").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){

                        //add interests to a list and display
                        List<String> interestsList = (List<String>) document.get("interests");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item_layout, interestsList);
                        listView.setAdapter(adapter);


                        //set onclick listener for each list item which takes user to quiz based on that interest
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String selectedItem = interestsList.get(position);

                                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                                intent.putExtra("interest", selectedItem);
                                startActivity(intent);
                            }
                        });


                    }else{
                        return;
                    }
                }else{
                    return;
                }
            }
        });

    }
}