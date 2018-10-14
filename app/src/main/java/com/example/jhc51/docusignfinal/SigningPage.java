package com.example.jhc51.docusignfinal;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.ViewUrl;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SigningPage extends AppCompatActivity {

    private String envId;
    private String creationId;
    private String originalemail;
    private String item;
    private String rate;
    private String duration;
    private String description;
    private String URL;
    WebView webview;
    Globals g = Globals.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing_page);

        envId = getIntent().getExtras().getString("envId");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        creationId = getIntent().getExtras().getString("creationid");
        originalemail = getIntent().getExtras().getString("originalemail");
        item = getIntent().getExtras().getString("item");
        rate = getIntent().getExtras().getString("rate");
        duration = getIntent().getExtras().getString("duration");
        description = getIntent().getExtras().getString("description");
        URL = getIntent().getExtras().getString("url");

    }

    @Override
    public void onStart() {
        super.onStart();
        sign(envId);
    }

    public void sign(final String envId) {
        System.out.println(envId);
        // instantiate a new EnvelopesApi object
        EnvelopesApi envelopesApi = new EnvelopesApi();

        // set the url where you want the recipient to go once they are done signing
        RecipientViewRequest view = new RecipientViewRequest();
        view.setReturnUrl("https://10.0.2.2/");
        view.setAuthenticationMethod("email");

        // recipient information must match embedded recipient info we provided in step #2
        view.setEmail(Globals.getInstance().getEmail());
        view.setUserName(Globals.getInstance().getRealName());
        view.setRecipientId("2");
        view.setClientUserId(creationId);

        // call the CreateRecipientView API
        try {

            ViewUrl recipientView = envelopesApi.createRecipientView(Globals.getInstance().getCreatorId(), envId, view);
            System.out.println("Signing URL = " + recipientView.getUrl());
            webview = (WebView) findViewById(R.id.webview);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView wView, String url) {
                    if (url.indexOf("https://10.0.2.2/") > -1) {
                        CollectionReference ref = FirebaseFirestore.getInstance().collection("Items");
                        final CollectionReference users = FirebaseFirestore.getInstance().collection("Users");
                        ref.document(originalemail + "," + item).update("renter", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                HashMap<String, Object> m = new HashMap<>();
                                m.put("name", item);
                                m.put("duration", duration);
                                m.put("rate", rate);
                                m.put("description", description);
                                m.put("url", URL);
                                m.put("envelopeid", envId);
                                m.put("loaner", false);
                                m.put("renter", true);
                                users.document(originalemail).collection("Pending").document(item).set(m);

                            }
                        });

                        finish();
                        return true;
                    } else {
                        webview.loadUrl(url);
                        return true;
                    }
                }

            });
            webview.getSettings().setJavaScriptEnabled(true);

            webview.loadUrl(recipientView.getUrl());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
