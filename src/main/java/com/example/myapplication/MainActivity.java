package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.SQL.DBHelper;
import com.example.myapplication.adapters.Adapter;
import com.example.myapplication.http.HttpAction;
import com.example.myapplication.model.Client;
import com.example.myapplication.service.ClientService;
import com.example.myapplication.service.DBService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private ClientService clientService;
    private DBService dbService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Adapter adapter;
    private boolean setedTheme = true;
    private SharedPreferences sPref;
    private static final String SAVED_THEME = "saved_theme";
    private static final String LANGUAGE = "language";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRecycle();
        setToolbar();
        setRefresh();
        setUserInfo();
        setAdapter();
        setDB();
        setTheme();
        setStartLanguage();
    }

    private void setStartLanguage() {

        if (loadLocal(LANGUAGE).equals("en")) {
            setLanguage("en");
            saveLocal("en", LANGUAGE);
        } else if (loadLocal(LANGUAGE).equals("ru")) {
            setLanguage("ru");
            saveLocal("ru", LANGUAGE);
        } else {
            setLanguage("en");
            saveLocal("en", LANGUAGE);
        }
    }

    private void switchLanguage() {
        if (loadLocal(LANGUAGE).equals("en")) {
            setLanguage("ru");
            saveLocal("ru", LANGUAGE);
        } else if (loadLocal(LANGUAGE).equals("ru")) {
            setLanguage("en");
            saveLocal("en", LANGUAGE);
        }
    }

    private void setLanguage(String whatLaggueage) {
        Locale locale = new Locale(whatLaggueage);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        // выводим текст на локали устройства
        setTitle(R.string.app_name);
    }

    private void setTheme() {
        if (loadLocal(SAVED_THEME).equals("Light")) {
            setTheme(R.style.Theme_MyApplicationLight);
            saveLocal("Light", SAVED_THEME);
            setedTheme = false;
        } else if (loadLocal(SAVED_THEME).equals("Dark")) {
            setTheme(R.style.Theme_MyApplicationBlack);
            saveLocal("Dark", SAVED_THEME);
        } else {
            setTheme(R.style.Theme_MyApplicationLight);
            saveLocal("Light", SAVED_THEME);

        }
        setMyTheme();
    }

    private void saveLocal(String nameTheme, String where) {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(where, nameTheme);
        ed.apply();
    }

    private String loadLocal(String where) {
        sPref = getPreferences(MODE_PRIVATE);
        return sPref.getString(where, "");
    }

    //сетеры

    private void setMyTheme() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();

        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true);
        getWindow().getDecorView().setBackgroundColor(typedValue.data);

        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(typedValue.data));

        theme.resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(typedValue.data);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    private void setRefresh() {
        swipeRefreshLayout = findViewById(R.id.swiperClientList);
        swipeRefreshLayout.setColorSchemeColors(Color.GREEN, Color.CYAN, Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setDB() {
        dbService = new DBService(new DBHelper(this));
        dbService.setClient(clientService.getClientList());
    }

    @SuppressLint("WrongViewCast")
    private void setRecycle() {
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycleA);
    }

    private void setAdapter() {
        adapter = new Adapter(clientService.getClientList());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("Range")
    private void setUserInfo() {
        swipeRefreshLayout.setRefreshing(true);
        clientService = new ClientService();
        new FetchClientList().execute();
    }

    //обновление листа клиентов

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new FetchClientList().execute();
        adapter.notifyDataSetChanged();
    }

    //создание меню и обработка действий в нем

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //enable menu list
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        //enable search
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                clientService.findClient(dbService, newText);
                setAdapter();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_ping: {
                swipeRefreshLayout.setRefreshing(true);
                new FetchPing().execute();
                break;
            }
            case R.id.app_bar_help: {
                swipeRefreshLayout.setRefreshing(true);
                new FetchHelpServer().execute();
                break;
            }
            case R.id.app_bar_switch_Language: {
                switchLanguage();
                break;
            }
            case R.id.app_bar_switch: {
                if (setedTheme) {
                    setedTheme = false;
                    setTheme(R.style.Theme_MyApplicationLight);
                    saveLocal("Light", SAVED_THEME);
                    setMyTheme();
                } else {
                    setedTheme = true;
                    setTheme(R.style.Theme_MyApplicationBlack);
                    saveLocal("Darck", SAVED_THEME);
                    setMyTheme();
                    break;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Паралельные вызовы

    @SuppressLint("StaticFieldLeak")
    private class FetchPing extends AsyncTask<Void, Void, Spanned> {

        @Override
        protected Spanned doInBackground(Void... voids) {
            return new HttpAction().pingServer();
        }

        @Override
        protected void onPostExecute(Spanned message) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG);
            toast.show();
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(message);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchHelpServer extends AsyncTask<Void, Void, Spanned> {

        @Override
        protected Spanned doInBackground(Void... voids) {
            return new HttpAction().helpServer();
        }

        @Override
        protected void onPostExecute(Spanned message) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    message, Toast.LENGTH_LONG);
            toast.show();
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(message);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchClientList extends AsyncTask<Void, Void, ArrayList<Client>> {

        @Override
        protected ArrayList<Client> doInBackground(Void... voids) {
            return new HttpAction().fetchClients();
        }

        @Override
        protected void onPostExecute(ArrayList<Client> clients) {
            if (clients.isEmpty()) {
                swipeRefreshLayout.setRefreshing(true);
                new FetchPing().execute();
            } else {
                swipeRefreshLayout.setRefreshing(false);
                clientService.setAll(clients);
                dbService.setClient(clientService.getClientList());
                super.onPostExecute(clients);
                setAdapter();
            }
        }
    }

}