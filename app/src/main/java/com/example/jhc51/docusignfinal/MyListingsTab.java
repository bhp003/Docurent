package com.example.jhc51.docusignfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.docusign.esign.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MyListingsTab extends Fragment {

    ListView itemsList;
    String[] itemname;
    String[] rate;
    String[] urls;
    Boolean[] loaner;
    Boolean[] renter;
    String[] defaultName = {"Item1", "Item2", "Item3", "Item4", "Item5"};
    Integer[] imgid = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
    CollectionReference userRef = FirebaseFirestore.getInstance().collection("Users");
    CustomListAdapter adapter;
    Globals g = Globals.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_one, container, false);
        //getItemsList();
        final String email = g.getEmail();
        userRef.document(email).collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final List<DocumentSnapshot> ls = task.getResult().getDocuments();
                    itemname = new String[ls.size()];
                    rate = new String[ls.size()];
                    urls = new String[ls.size()];
                    loaner = new Boolean[ls.size()];
                    renter = new Boolean[ls.size()];
                    for (int i = 0; i < ls.size(); i++){
                        Log.d("Items: ", ls.get(i).getData().get("name").toString());
                        itemname[i] = (ls.get(i).getData().get("name").toString());
                        itemname[i] = (ls.get(i).getData().get("name").toString());
                        rate[i] = (ls.get(i).getData().get("rate").toString());
                        if(ls.get(i).getData().get("url")!=null){
                            urls[i] = (ls.get(i).getData().get("url").toString());}
                        if(ls.get(i).getData().get("loaner")== null || ls.get(i).getData().get("loaner").toString().equals("false")){
                            loaner[i] = false; } else{ loaner[i] = true; }
                        if(ls.get(i).getData().get("renter") == null || ls.get(i).getData().get("renter").toString().equals("false")){
                            renter[i] = false; } else{ renter[i] = true; }
                    }
                    Log.d("itemslist", itemname + "");
                    adapter = new CustomListAdapter(getActivity(), itemname, rate, urls, loaner, renter);
                    itemsList = (ListView) view.findViewById(R.id.items_list);
                    itemsList.setAdapter(adapter);
                }
                else {
                }
            }
        });
        Log.d("Items", itemname+"");
        return view;
    }
}
