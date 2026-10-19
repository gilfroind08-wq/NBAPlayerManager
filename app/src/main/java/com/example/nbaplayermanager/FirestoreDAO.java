package com.example.nbaplayermanager;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class FirestoreDAO {

    private static final String COLLECTION_NAME = "players";
    private ListenerRegistration listenerRegistration;
    // CREATE - Add a player
    public void createDocument(String name, String id,
                               String team, double points) {

        Map<String, Object> playerRecord = new HashMap<>();

        playerRecord.put("name", name);
        playerRecord.put("id", id);
        playerRecord.put("team", team);
        playerRecord.put("points", points);

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .document(id);

        docRef.set(playerRecord)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DAO_FIREBASE", "SUCCESSFUL WRITE!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DAO_FIREBASE", "WRITE FAILED", e);
                    }
                });
    }

    // READ - Find one player by ID
    public void readDocumentById(String docID,
                                 IFirestoreCallback callback) {

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .document(docID);

        docRef.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        ArrayList<Player> players = new ArrayList<>();

                        if (documentSnapshot.exists()) {
                            Player player =
                                    documentSnapshot.toObject(Player.class);

                            if (player != null) {
                                players.add(player);
                            }
                        }

                        callback.onDataReceived(players);
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DAO_FIREBASE", "READ FAILED", e);
            }
        });
    }

    // READ - Get all players
    public void readAllDocuments(IFirestoreCallback callback) {

        FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {

                                ArrayList<Player> players = new ArrayList<>();

                                for (DocumentSnapshot document :
                                        querySnapshot.getDocuments()) {

                                    Player player =
                                            document.toObject(Player.class);

                                    if (player != null) {
                                        players.add(player);
                                    }
                                }

                                callback.onDataReceived(players);
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DAO_FIREBASE", "READ ALL FAILED", e);
                    }
                });
    }



    public void updateDocument(String documentID, double newPoints) {

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME).document(documentID);

        docRef.update("points", newPoints)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DAO_FIREBASE", "SUCCESSFUL UPDATE!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DAO_FIREBASE", "UPDATE FAILED", e);
                    }
                });
    }

    public void deleteDocument(String documentID) {

        DocumentReference docRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME).document(documentID);

        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("DAO_FIREBASE", "SUCCESSFUL DELETE!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DAO_FIREBASE", "DELETE FAILED", e);
                    }
                });
    }

    public void listenToAllDocumentsChange() {

        CollectionReference colRef = FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME);

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = colRef.addSnapshotListener(
                new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.e("DAO_MULTIPLE_CHANGES", "Listener error", error);
                            return;
                        }

                        if (value == null)
                            return;

                        for (DocumentChange doc : value.getDocumentChanges()) {

                            Player player = doc.getDocument().toObject(Player.class);

                            if (player != null) {
                                Log.d("DAO_MULTIPLE_CHANGES",
                                        doc.getType() + ": " + player.toString());
                            }
                        }
                    }
                });
    }

    public void stopListening() {

        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

}
