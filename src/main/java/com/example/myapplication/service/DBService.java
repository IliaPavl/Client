package com.example.myapplication.service;

import com.example.myapplication.SQL.DBHelper;
import com.example.myapplication.model.Client;

import java.util.ArrayList;

public class DBService {
    DBHelper dbHelper;

    public DBService(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void setClient(ArrayList<Client> clients) {
        dbHelper.setDataBase(clients);
    }

    public ArrayList<Client> searchDB(String textSearch) {
        return dbHelper.findOnDataBase(textSearch);
    }
}
