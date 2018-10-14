package com.example.jhc51.docusignfinal;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class itemDetails extends AppCompatActivity {
    Bundle b;
    String id = "";
    TextView durationText, rateText, descriptionText;
    ImageView imageView;

    static String realname = "";
    static String phone = "";
    static String duration = "";
    static String rate = "";
    static String title="";
    static String description="";
    static int creationid = 1;
    static String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        b = getIntent().getExtras();
        if( b!= null){
            System.out.print("here");
            id = b.getString("key");
        }
        Log.d("CREATION", id);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        durationText = findViewById(R.id.duration);
        rateText = findViewById(R.id.rate);
        descriptionText = findViewById(R.id.description);
        imageView = (ImageView)findViewById(R.id.detailsImage);


        duration = b.getString("duration");
        rate = b.getString("rate");
        title = b.getString("title");
        creationid = Integer.parseInt(b.getString("creationid"));
        description = b.getString("description");
        url = b.getString("url");

        durationText.setText("Duration: "+ duration);
        rateText.setText("Daily rate " + rate);
        descriptionText.setText("Description: "+ description);

        if(!url.isEmpty())
            Picasso.get().load(url).into(imageView);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Button button = findViewById(R.id.rentbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFields();
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        /* Request user permissions in runtime */
        ActivityCompat.requestPermissions(itemDetails.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);
        /* Request user permissions in runtime */

        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        File folder = new File(extStorageDirectory, "pdf");
        folder.mkdir();
        final File file = new File(folder, "docusign_template.pdf");
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Runnable runnable = new Runnable() {
            @Override
           public void run() {
                Downloader.DownloadFile("https://ahn010sd.000webhostapp.com/EQUIPMENT%20RENTAL%20AGREEMENT.pdf", file);
            }
        };
        AsyncTask.execute(runnable);
    }


    public void Rent(View v) {

        addFields();
    }

    public void addFields() {
        //Runnable runnable = new Runnable() {
        //    @Override
        //    public void run() {
                try {
                    PdfReader reader = new PdfReader(Environment.getExternalStorageDirectory()+"/pdf/docusign_template.pdf");
                    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(Environment.getExternalStorageDirectory()+"/pdf/docusign_template_stamped.pdf"));
                    PdfContentByte cb = stamper.getOverContent(1);
                    ColumnText ct = new ColumnText(cb);

                    // Loaner Name
                    ct.setSimpleColumn(120f, 120f, 300f, 695f);
                    Font f = new Font();
                    Paragraph loanerName = new Paragraph(new Phrase(20, realname, f));
                    ct.addElement(loanerName);
                    ct.go();

                    // Loaner Street Address
                    ct.setSimpleColumn(120f, 120f, 300f, 665f);
                    Paragraph loanerStreetAddress = new Paragraph(new Phrase(20, "1234 Michael St", f));
                    ct.addElement(loanerStreetAddress);
                    ct.go();

                    // Loaner City/ZIP
                    ct.setSimpleColumn(120f, 120f, 300f, 645f);
                    Paragraph loanerCityAddress = new Paragraph(new Phrase(20, "City, ZIP", f));
                    ct.addElement(loanerCityAddress);
                    ct.go();

                    // Loaner Phone
                    ct.setSimpleColumn(150f, 150f, 300f, 620f);
                    Paragraph loanerPhone = new Paragraph(new Phrase(20, phone, f));
                    ct.addElement(loanerPhone);
                    ct.go();

                    // Renter Name
                    ct.setSimpleColumn(120f, 120f, 300f, 580f);
                    Paragraph renterName = new Paragraph(new Phrase(20, Globals.getInstance().getRealName(), f));
                    ct.addElement(renterName);
                    ct.go();

                    // Renter Street Address
                    ct.setSimpleColumn(120f, 120f, 300f, 550f);
                    Paragraph renterStreetAddress = new Paragraph(new Phrase(20, "1234 Sana Rd", f));
                    ct.addElement(renterStreetAddress);
                    ct.go();

                    // Renter City/ZIP
                    ct.setSimpleColumn(120f, 120f, 300f, 530f);
                    Paragraph renterCityAddress = new Paragraph(new Phrase(20, "City, ZIP", f));
                    ct.addElement(renterCityAddress);
                    ct.go();

                    // Renter Phone
                    ct.setSimpleColumn(150f, 150f, 300f, 505f);
                    Paragraph renterPhone = new Paragraph(new Phrase(20, Globals.getInstance().getPhone(), f));
                    ct.addElement(renterPhone);
                    ct.go();

                    // Place of rental
                    ct.setSimpleColumn(150f, 150f, 300f, 480f);
                    Paragraph rentalLocation = new Paragraph(new Phrase(20, "ZIP", f));
                    ct.addElement(rentalLocation);
                    ct.go();

                    // Equipment 1
                    ct.setSimpleColumn(100f, 100f, 300f, 400f);
                    Paragraph equipment1 = new Paragraph(new Phrase(20, getSupportActionBar().getTitle().toString(), f));
                    ct.addElement(equipment1);
                    ct.go();
                    // Equipment 2
                    ct.setSimpleColumn(100f, 100f, 300f, 375f);
                    Paragraph equipment2 = new Paragraph(new Phrase(20, " ", f));
                    ct.addElement(equipment2);
                    ct.go();
                    // Equipment 3
                    ct.setSimpleColumn(100f, 100f, 300f, 350f);
                    Paragraph equipment3 = new Paragraph(new Phrase(20, " ", f));
                    ct.addElement(equipment3);
                    ct.go();

                    // Rate
                    ct.setSimpleColumn(80f, 100f, 300f, 275f);
                    Paragraph rate = new Paragraph(new Phrase(20, rateText.getText().toString(), f));
                    ct.addElement(rate);
                    ct.go();

                    // Length
                    ct.setSimpleColumn(240f, 240f, 300f, 275f);
                    Paragraph length = new Paragraph(new Phrase(20, durationText.getText().toString(), f));
                    ct.addElement(length);
                    ct.go();

                    // PenaltyFee
                    ct.setSimpleColumn(80f, 100f, 300f, 255f);
                    Paragraph penaltyFee = new Paragraph(new Phrase(20, "PenaltyFee", f));
                    ct.addElement(penaltyFee);
                    ct.go();

                    stamper.close();
                    reader.close();
                    System.out.println("I wrote it");

                    createEnvelope();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
        //    }
       // };
       // AsyncTask.execute(runnable);
    }

    public void createEnvelope() {
        // create a byte array that will hold our document bytes
        byte[] fileBytes = null;
        String pathToDocument = Environment.getExternalStorageDirectory()+"/pdf/docusign_template_stamped.pdf";

        try
        {
            String currentDir = System.getProperty("user.dir");

            // read file from a local directory
            Path path = Paths.get(currentDir + pathToDocument);
            fileBytes = Files.readAllBytes(path);
        }
        catch (IOException ioExcp)
        {
            // TODO: handle error
            System.out.println("Exception: " + ioExcp);
            return;
        }

// create an envelope that will store the document(s), field(s), and recipient(s)
        final EnvelopeDefinition envDef = new EnvelopeDefinition();
        envDef.setEmailSubject("Please sign this document sent from Java SDK)");

// add a document to the envelope
        Document doc = new Document();
        String base64Doc = Base64.getEncoder().encodeToString(fileBytes);
        doc.setDocumentBase64(base64Doc);
        doc.setName("TestFile"); // can be different from actual file name
        doc.setFileExtension(".pdf"); // update if different extension!
        doc.setDocumentId("1");

        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);
        envDef.setDocuments(docs);

// add a recipient to sign the document, identified by name and email we used above
        Signer loaner = new Signer();
        loaner.setEmail(id.split(",", 3)[0]);
        System.out.println(id.split(",", 3)[0]);
        loaner.setName(id.split(",", 3)[0]);
        loaner.setRecipientId("2");
        loaner.setRoutingOrder("2");
        loaner.setClientUserId(String.valueOf(creationid));

        // Local phone holder
        Signer renter = new Signer();
        renter.setEmail(Globals.getInstance().getEmail());
        renter.setName(Globals.getInstance().getRealName());
        renter.setRecipientId("1");
        renter.setRoutingOrder("1");
        renter.setClientUserId(String.valueOf(creationid+1));


// create a |signHere| tab somewhere on the document for the signer to sign
// here we arbitrarily place it 100 pixels right, 150 pixels down from top
// left corner of first page of first envelope document
        SignHere signHereLoaner = new SignHere();
        signHereLoaner.setDocumentId("1");
        signHereLoaner.setPageNumber("2");
        signHereLoaner.setRecipientId("1");
        signHereLoaner.setXPosition("100");
        signHereLoaner.setYPosition("585");
        signHereLoaner.setOptional("true");

        SignHere signHereRenter = new SignHere();
        signHereRenter.setDocumentId("1");
        signHereRenter.setPageNumber("2");
        signHereRenter.setRecipientId("2");
        signHereRenter.setXPosition("100");
        signHereRenter.setYPosition("565");
        signHereRenter.setOptional("true");

// can have multiple tabs, so need to add to envelope as a single element list
        List<SignHere> signHereTabs = new ArrayList<SignHere>();
        signHereTabs.add(signHereLoaner);
        signHereTabs.add(signHereRenter);
        Tabs tabs = new Tabs();
        tabs.setSignHereTabs(signHereTabs);
        loaner.setTabs(tabs);
        renter.setTabs(tabs);



// add recipients (in this case a single signer) to the envelope
        envDef.setRecipients(new Recipients());
        envDef.getRecipients().setSigners(new ArrayList<Signer>());
        envDef.getRecipients().getSigners().add(loaner);
        envDef.getRecipients().getSigners().add(renter);

// send the envelope by setting |status| to "sent". To save as a draft set to "created" instead
        envDef.setStatus("sent");


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // instantiate a new EnvelopesApi object
                    EnvelopesApi envelopesApi = new EnvelopesApi();
                    // call the createEnvelope() API
                    EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(Globals.getInstance().getCreatorId(), envDef);
                    System.out.println("EnvelopeSummary: " + envelopeSummary);

                    //Comment out
                    Intent intent = new Intent(getApplicationContext(), SigningPage.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("envId", envelopeSummary.getEnvelopeId());
                    bundle.putString("creationid", String.valueOf(creationid+1));
                    bundle.putString("originalemail", id.split(",", 3)[0]);
                    bundle.putString("item", title);
                    bundle.putString("rate", rate);
                    bundle.putString("duration", duration);
                    bundle.putString("description", description);
                    bundle.putString("url", url);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } catch (com.docusign.esign.client.ApiException ex) {
                    System.out.println("Exception: " + ex);
                }
            }
        };
        AsyncTask.execute(runnable);
    }

}