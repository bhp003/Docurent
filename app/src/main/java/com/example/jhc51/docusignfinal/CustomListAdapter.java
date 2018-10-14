package com.example.jhc51.docusignfinal;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final String[] rate;
    private final String[] urls;
    private final Boolean[] loaner;
    private final Boolean[] renter;
    CollectionReference itemRef = FirebaseFirestore.getInstance().collection("Items");


    public CustomListAdapter(Activity context, String[] itemname, String[] rate, String[] urls, Boolean[] loaner, Boolean[] renter) {
        super(context, R.layout.item_inlist, itemname);
// TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.rate=rate;
        this.urls=urls;
        this.loaner = loaner;
        this.renter = renter;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.item_inlist, null,true);

        final TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        final TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        final TextView status = (TextView) rowView.findViewById(R.id.status);

        if(loaner[position]!=null && renter[position]!= null){
        if(loaner[position].toString().equals("true") && renter[position].toString().equals("true")){
            status.setText("2 Signature-Done");
        }
        else if(loaner[position].toString().equals("false") && renter[position].toString().equals("false")){
            status.setText("0 Signature");
        }
        else {
            status.setText("1 Signature-Pending");
        }}


        txtTitle.setText(itemname[position]);
        if(urls[position]!=null)
            Picasso.get().load(urls[position]).into(imageView);
         extratxt.setText("Rate per day: " + "$" + rate[position]);
        return rowView;

    }
}