package com.colwanymab.timelynews;

public class SetterProfile {

    public SetterProfile() {
    }

    private String uid, email, name, surname, twitter, facebook, link, number;

    public SetterProfile(String uid, String email, String name, String surname,
                         String twitter, String facebook, String link, String number) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.twitter = twitter;
        this.facebook = facebook;
        this.link = link;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
}
