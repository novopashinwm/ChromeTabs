package ru.startandroid.develop.chrometabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "TOKENOID";

    public static String OAUTH_URL = "https://github.com/login/oauth/authorize";
    public static String OAUTH_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String REDIRECT_URI = "https://www.google.ru/";
    private static final String STATE_PARAM = "state";
    private static final String RESPONSE_TYPE_VALUE ="code";
    public static String CLIENT_ID = "2bcac5550c4eea92c841";
    public static String SECRET_KEY = "49587e6f9e4e6de51685d75631ba7b7d8e49b8ef";
    WebView webView;

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String SECRET_KEY_PARAM = "client_secret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the webView from the layout
        webView =  findViewById(R.id.webView);

        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                if(authorizationUrl.startsWith(REDIRECT_URI)){
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if(authorizationToken==null){
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: "+authorizationToken);
                    getTokenRetrofit(authorizationToken);

                }else{
                    //Default behaviour
                    Log.i("Authorize","Redirecting to: "+authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }

        });
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize","Loading Auth Url: "+authUrl);
        //Load the authorization URL into the webView
        webView.loadUrl(authUrl);

    }

    void getTokenRetrofit(String code) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://github.com/") // Note that the base url is different. See documentation
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RepositoryInterface service = retrofit.create(RepositoryInterface.class);

        service.getAccessToken(
                CLIENT_ID,
                SECRET_KEY, code).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    AccessToken accessToken = response.body();
                    if (accessToken != null) {
                        Log.i(TAG, "onResponse: Token: " + accessToken.getAccessToken() + " - type: " + accessToken.getTokenType());

                        SharedPreferences preferences = getSharedPreferences("user_info", 0);
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("accessToken", accessToken.getAccessToken());
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), CreateRepoActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Log.i(TAG, "onResponse: Error code: " + response.code() + " - error message: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: Failed :(, error message: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to connect to the server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String getAuthorizationUrl(){
        return OAUTH_URL
                +QUESTION_MARK+CLIENT_ID_PARAM+EQUALS+CLIENT_ID;
    }

}
