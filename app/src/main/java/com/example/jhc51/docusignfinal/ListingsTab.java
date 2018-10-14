package com.example.jhc51.docusignfinal;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class ListingsTab extends Fragment {

    ListView itemsList;
    String[] itemname;
    String[] rate;
    String[] urls;
    Boolean[] loaner;
    Boolean[] renter;
    String[] defaultName = {"Item1", "Item2", "Item3", "Item4", "Item5"};
    Integer[] imgid = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, R.drawable.ic_launcher_background};
    ItemDatabase itemDB = new ItemDatabase();
    CustomListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_one, container, false);

        itemDB.getDoc().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getBaseContext(), name[position], Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(itemsList.getContext(), itemDetails.class);
                            Bundle param = new Bundle();
                            param.putString("key", ls.get(position).getId());
                            param.putString("rate", ls.get(position).get("rate").toString());
                            param.putString("duration", ls.get(position).get("duration").toString());
                            param.putString("title", ls.get(position).get("name").toString());
                            param.putString("description", ls.get(position).get("description").toString());
                            param.putString("creationid", ls.get(position).get("id").toString());
                            if(ls.get(position).getData().get("url")!=null){
                                param.putString("url", ls.get(position).get("url").toString());}
                            intent.putExtras(param);
                            startActivity(intent);

                        }
                    });
                }
                else {
                }
            }
        });
        Log.d("Items", itemname+"");
        return view;
    }
}


