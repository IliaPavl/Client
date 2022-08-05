package com.example.myapplication.SQL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.model.Client;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "client_list";
    public static final String CLIENT_TABLE = "сlients";
    public static final String CLIENT_NAME = "client_name";
    public static final String FULL_NAME = "full_name";
    public static final String SATUS = "status";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE VIRTUAL TABLE " + CLIENT_TABLE +
                " USING fts4 ( " + CLIENT_NAME + "  , "
                + FULL_NAME + ", " + SATUS + " )");
    }

    public void setDataBase(ArrayList<Client> clients) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Client client : clients) {
            String name = client.getClientName();
            String fullName = client.getFullName();
            String status = client.getStatus();
            db.execSQL("INSERT INTO " + CLIENT_TABLE + "( " +
                    CLIENT_NAME + ", " + FULL_NAME + ", " + SATUS + " ) " +
                    " VALUES ( '" + name + "', '" + fullName + "', '" + status + "')");
        }
    }

    public ArrayList<Client> findOnDataBase(String string) {
        SQLiteDatabase db = this.getReadableDatabase();
        string = "%" + string + "%";
        Cursor cursor = db.rawQuery("select distinct * from " + CLIENT_TABLE +
                " where " + CLIENT_NAME + " LIKE ? " +
                "or " + FULL_NAME + " LIKE ?" +
                "or " + SATUS + " LIKE ?", new String[]{string, string, string});
        return parseCursor(cursor);
    }

    @SuppressLint("Range")
    private ArrayList<Client> parseCursor(Cursor cursor) {
        ArrayList<Client> clients = new ArrayList<>();
        while (cursor.moveToNext()) {
            Client client = new Client();
            client.setClientName(cursor.getString(cursor.getColumnIndex(CLIENT_NAME)));
            client.setFullName(cursor.getString(cursor.getColumnIndex(FULL_NAME)));
            client.setStatus(cursor.getString(cursor.getColumnIndex(SATUS)));
            clients.add(client);
        }
        return clients;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CLIENT_TABLE);
        onCreate(db);
    }
}
