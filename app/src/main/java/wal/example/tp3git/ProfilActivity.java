package wal.example.tp3git;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wal.example.tp3git.service.GitRepoServiceAPI;

public class ProfilActivity extends AppCompatActivity {
    public List<String> data = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        setTitle("Profil");
        Intent intent = getIntent();
        String username = intent.getStringExtra(MainActivity.USERNAME_PARAM);
        TextView textViewProfilUsername = findViewById(R.id.textViewProfilUsername);
        ListView listViewRepos = findViewById(R.id.listViewRepos);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        listViewRepos.setAdapter(stringArrayAdapter);

        textViewProfilUsername.setText(username);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitRepoServiceAPI service = retrofit.create(GitRepoServiceAPI.class);

        Call<List<GitRepo>> repoCall = service.userRepos(username);
        repoCall.enqueue(
                new Callback<List<GitRepo>>() {
                    @Override
                    public void onResponse(Call<List<GitRepo>> call, Response<List<GitRepo>> response) {
                        if (!response.isSuccessful()){
                            Log.e("Error", String.valueOf(response.code()));
                            return;
                        }
                        List<GitRepo> gitRepos = (List<GitRepo>) response.body();
                        for (GitRepo gitRepo: gitRepos){
                            String context = "";
                            context += "id: " + gitRepo.id + "\n";
                            context += "name: " + gitRepo.name + "\n";
                            context += "language: " + gitRepo.language + "\n";

                            data.add(context);
                        }
                        stringArrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<GitRepo>> call, Throwable t) {
                        Log.i("error", "Error onFailure");
                    }
                }
        );
    }
}
