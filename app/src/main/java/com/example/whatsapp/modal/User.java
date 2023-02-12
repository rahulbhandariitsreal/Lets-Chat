package com.example.whatsapp.modal;

public class User {

    private String uid;
    private String name;
    private String email;
    private String imageURI;

    private String status;

    public User() {
    }

    public User(String uid, String name, String email, String imageURI,String status) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.imageURI = imageURI;
        this.status=status;
    }

    public String getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}
