package com.example.myapplication.service;

import com.example.myapplication.model.Client;

import java.util.ArrayList;

public class ClientService {
    private ArrayList<Client> clientList = new ArrayList<>();

    public void setAll(ArrayList<Client> clientList) {
        this.clientList = clientList;
    }

    public void findClient(DBService db, String searchValue) {
        clientList = db.searchDB(searchValue);
    }

    public ArrayList<Client> getClientList() {
        return clientList;
    }

}
