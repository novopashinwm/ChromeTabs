package ru.startandroid.develop.chrometabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.startandroid.develop.chrometabs.model.GitHubUser;
import ru.startandroid.develop.chrometabs.model.GithubRepository;
import ru.startandroid.develop.chrometabs.model.Permissions;

public class CreateRepoActivity extends AppCompatActivity {
    public static final String BASE_URL = "https://api.github.com/";
    Button btnCreateRepo;
    EditText etRepoName;
    private Bundle bundle;
    String accessToken ="";
    GitHubUser gitHubUser = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repo);
        etRepoName = findViewById(R.id.edRepoName);
        btnCreateRepo = findViewById(R.id.btnRepoCreate);
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
        accessToken = preferences.getString("accessToken", null);

        showUserName();

        btnCreateRepo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permissions permissions = new Permissions();
                permissions.setAdmin(true);
                permissions.setPull(true);
                GithubRepository repository = new GithubRepository();
                repository.setName(etRepoName.getText().toString());
                repository.setPrivate(false);
                repository.setPermissions(permissions);
                //ApiUtils.createRepository(repository,token);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL) // Note that the base url is different. See documentation
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                RepositoryInterface service = retrofit.create(RepositoryInterface.class);
                service.createRepo(repository,"token " + accessToken
                        ,"application/vnd.github.v3+json"
                ,"application/json").enqueue(new Callback<GithubRepository>() {


                    @Override
                    public void onResponse(@Nullable Call<GithubRepository> call, @Nullable Response<GithubRepository> response) {

                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Repository create succ", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Repository create error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GithubRepository> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void showUserName() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/") // Note that the base url is different. See documentation
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RepositoryInterface service = retrofit.create(RepositoryInterface.class);

        service.getUserInfo("token " + accessToken).enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(Call<GitHubUser> call, Response<GitHubUser> response) {
                if (response.isSuccessful()) {
                    gitHubUser = response.body();
                    if (gitHubUser.getLogin() != null) {
                        setTitle(gitHubUser.getLogin());
                    }
                }
            }

            @Override
            public void onFailure(Call<GitHubUser> call, Throwable t) {

            }
        });

    }

}
