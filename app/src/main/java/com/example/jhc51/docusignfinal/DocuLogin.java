package com.example.jhc51.docusignfinal;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.docusign.esign.client.*;
import com.docusign.esign.client.auth.AccessTokenListener;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.UserInfo;

import org.apache.oltu.oauth2.common.token.BasicOAuthToken;

import java.net.URI;

public class DocuLogin extends AppCompatActivity {

    WebView webview;
    String authCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docu_login);

        // Java setup and config
        final String IntegratorKey = "c0a74c69-a826-4ddc-a820-0824bcf170b9";

        // generate a client secret for the integrator key you supply above, again through sandbox admin menu
        final String ClientSecret = "dfa370e4-e83a-4350-a839-e4730ea1294f";

        // must match a redirect URI (case-sensitive) you configured on the key
        String RedirectURI = "https://10.0.2.2/callback";

        // use demo authentication server (remove -d for production)
        String AuthServerUrl = "https://account-d.docusign.com";

        // point to the demo (sandbox) environment. For production requests your account sub-domain
        // will vary, you should always use the base URI that is returned from authentication to
        // ensure your integration points to the correct endpoints (in both environments)
        String RestApiUrl = "https://demo.docusign.net/restapi";

        // instantiate the api client and point to auth server
        final ApiClient apiClient = new ApiClient(AuthServerUrl, "docusignAccessCode", IntegratorKey, ClientSecret);

        // set the base path for REST API requests
        apiClient.setBasePath(RestApiUrl);

        // configure the authorization flow on the api client
        apiClient.configureAuthorizationFlow(IntegratorKey, ClientSecret, RedirectURI);

        // set as default api client in your configuration
        Configuration.setDefaultApiClient(apiClient);

        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView wView, String url) {
                if(url.indexOf("https://10.0.2.2/") > -1) {
                    authCode = Uri.parse(url).getQueryParameter("code");
                    System.out.println("Checking " + authCode);
                    requestToken(apiClient, IntegratorKey, ClientSecret, authCode);
                    return true;
                }
                else {
                    webview.loadUrl(url);
                    return true;
                }
            }

        });
        webview.getSettings().setJavaScriptEnabled(true);



        try {
            // get DocuSign OAuth authorization url
            String oauthLoginUrl = apiClient.getAuthorizationUri();
            // open DocuSign OAuth login in the browser
            System.out.println(oauthLoginUrl);
            System.out.println("link"+URI.create(oauthLoginUrl).toString());
            webview.loadUrl(URI.create(oauthLoginUrl).toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }

    }

    public void requestToken(ApiClient apiClient, String IK, String CS, String code) {
        Runnable thread = new MyRunnable(apiClient, code);
        new Thread(thread).start();
    }

    public class MyRunnable implements Runnable {
        ApiClient apiClient;
        String code;
        public MyRunnable(ApiClient apiClient, String code) {
            this.apiClient = apiClient;
            this.code = code;
        }

        public void run() {
            try {
                apiClient.getTokenEndPoint().setCode(code);

                // optionally register to get notified when a new token arrives
                apiClient.registerAccessTokenListener(new AccessTokenListener() {
                    @Override
                    public void notify(BasicOAuthToken token) {
                        System.out.println("Got a fresh token: " + token.getAccessToken());
                        System.out.println("Got a token type: " + token.getTokenType());
                        System.out.println("Got a refresh token: " + token.getRefreshToken());
                        System.out.println("Expires in: " + token.getExpiresIn());
                    }
                });

                // following call exchanges the authorization code for an access code and updates
                // the `Authorization: bearer <token>` header on the api client
                apiClient.updateAccessToken();

                OAuth.UserInfo userInfo = apiClient.getUserInfo(apiClient.getAccessToken());

                // currently parsing the first account we find in the response
                apiClient.setBasePath(userInfo.getAccounts().get(0).getBaseUri() + "/restapi");

                Configuration.setDefaultApiClient(apiClient);
                System.out.println("AfterRunnable: " + userInfo.getAccounts().get(0).getAccountId());

                Globals.getInstance().setAccessToken(apiClient.getAccessToken());
                Globals.getInstance().setCreatorId(userInfo.getAccounts().get(0).getAccountId());

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        public ApiClient getClient() {
            return this.apiClient;
        }
    }
}
