package com.gmailcymbidiumm.stayindoorsapp.Model;

public class User {

    private String id;
    private String fName;
    private String imageurl;
    private String email;

    public User(String id, String fName, String imageurl, String email) {
        this.id = id;
        this.fName = fName;
        this.imageurl = imageurl;
        this.email = email;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
