package com.example.nbaplayermanager;


import java.util.ArrayList;

public interface IFirestoreCallback {

    void onDataReceived(ArrayList<Player> players);

}
