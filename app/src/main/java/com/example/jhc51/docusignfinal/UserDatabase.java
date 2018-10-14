package com.example.jhc51.docusignfinal;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import java.util.*;

public class UserDatabase {
    private CollectionReference userRef = FirebaseFirestore.getInstance().collection("Users");
    Globals g = Globals.getInstance();

    public void addUser(String email, String name, String phone) {
        HashMap<String, String> m = new HashMap<>();
        m.put("email", email);
        m.put("name", name);
        m.put("phone", phone);
        userRef.document(email).set(m);
        //g.setEmail(email);
    }

    public Task<Void> addItem(String email, String name, Map<String, Object> data) {
        return userRef.document(email).collection("Items").document(name).set(data);
    }

    public Task<Void> deleteItem(String email, String name) {
        return userRef.document(email).collection("Items").document(name).delete();
    }

    public Task<Void> updateItem(String email, String name, Map<String, Object> data) {
        return userRef.document(email).collection("Items").document(name).update(data);
    }

    public Task<QuerySnapshot> getDoc() {
        return userRef.get();
    }
}

