package com.example.jhc51.docusignfinal;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import java.util.*;

public class ItemDatabase {
    CollectionReference userRef = FirebaseFirestore.getInstance().collection("Users");
    CollectionReference itemRef = FirebaseFirestore.getInstance().collection("Items");
    Globals g = Globals.getInstance();
    List<DocumentSnapshot> users, itemsPerUser;
    DocumentReference docRef;
    static int autoinc = 1;


    public Task<QuerySnapshot> getDoc() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    users = task.getResult().getDocuments();
                    for (DocumentSnapshot user : users) {
                        final String email = user.getId();
                        user.getReference().collection("Items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                if (task1.isSuccessful()) {
                                    itemsPerUser = task1.getResult().getDocuments();
                                    for (DocumentSnapshot item : itemsPerUser) {
                                        Map<String, Object> data = item.getData();
                                        if (data != null) {
                                            data.put("id", autoinc);
                                            data.put("renter", false);
                                            data.put("loaner", false);
                                            itemRef.document(email + "," + item.get("name")).set(data);
                                            autoinc += 2;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        return itemRef.get();
    }
}
