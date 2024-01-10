package com.example.chatter;

public class User {
    private String profilePic,mail,userName,pass,userID,status,currentStatus;

    private long lastSeen;

    public User(String userID, String userName, String mail, String pass, String profilePic, String status,String currentStatus, long lastSeen) {
        this.profilePic = profilePic;
        this.mail = mail;
        this.userName = userName;
        this.pass = pass;
        this.userID = userID;
        this.status = status;
        this.currentStatus = currentStatus;
        this.lastSeen = lastSeen;
    }
    public User(){

    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String activeStatus) {
        this.currentStatus = activeStatus;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
