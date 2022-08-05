package com.example.myapplication.model;

public class Client {
    private String clientName;
    private String fullName;
    private boolean status;

    public Client(String clientName, String fullName, String status) {
        this.clientName = clientName;
        this.fullName = fullName;
        setStatus(status);
    }

    public Client() {
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status.equals("Активен");
    }

    public String getStatus() {
        return (this.status) ? "Активен" : "Приоставновлен";
    }
}
