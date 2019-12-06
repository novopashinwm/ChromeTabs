package ru.startandroid.develop.chrometabs;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.startandroid.develop.chrometabs.model.GitHubUser;
import ru.startandroid.develop.chrometabs.model.GithubRepository;

public interface RepositoryInterface {

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code
    );


    @GET("login/oauth/authorize")
    Call<String> getCode(@Field("client_id") String clientId);

    @Headers("Accept: application/json")
    @GET("user")
    Call<GitHubUser> getUserInfo(@Header("Authorization") String token);

    @POST("user/repos")
    Call<GithubRepository> createRepo(@Body GithubRepository repo,
                                      @Header("Authorization") String accessToken,
                                      @Header("Accept") String apiVersionSpec,
                                      @Header("Content-Type") String contentType);


}
