package org.example;

public class Userinfo {
    private String username;
    private String pswd;
    private String fullName;
    private String email;


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    public void setEmail (String email){
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPswd() {
        return pswd;
    }

    public String getFullName(){
        return fullName;
    }

    public String getEmail(){
        return email;
    }
}
