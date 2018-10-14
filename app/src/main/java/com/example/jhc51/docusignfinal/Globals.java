package com.example.jhc51.docusignfinal;

public class Globals {
    private static Globals instance;
    private String email;
    private String accessToken;
    private String creatorId;
    private String realName;
    private String phone;

    public Globals() {}

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setCreatorId(String id) {
        this.creatorId = id;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setRealName(String name) {
        this.realName = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public static synchronized Globals getInstance(){
        if (instance == null)
            instance = new Globals();
        return instance;
    }
}

