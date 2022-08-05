package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Client;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {

    private final ArrayList<Client> users;

    public Adapter(ArrayList<Client> users) {
        this.users = users;
    }

    //сетим ресайклер

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyHolder(view);
    }

    //заполнием ячейку для ресайклера

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String name = "Client Name: " + users.get(position).getClientName();
        String fullname = "Full name: " + users.get(position).getFullName();
        setHolder(holder, name, fullname, position);
    }

    private void setHolder(MyHolder holder, String name, String fullname, int position) {
        holder.name.setText(name);
        holder.fullName.setText(fullname);
        holder.active.setAlpha((users.get(position).isStatus() ? 1000 : 0));
        holder.frosen.setAlpha((users.get(position).isStatus() ? 0 : 1000));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    //холдер для ресайклера (константы)

    public static class MyHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView fullName;
        private final ImageView active;
        private final ImageView frosen;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userNameTextView);
            fullName = itemView.findViewById(R.id.userClientFullName);
            active = itemView.findViewById(R.id.active);
            frosen = itemView.findViewById(R.id.frosen);
        }
    }
}
