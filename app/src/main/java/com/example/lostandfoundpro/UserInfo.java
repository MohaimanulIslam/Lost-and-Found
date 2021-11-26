package com.example.lostandfoundpro;

public class UserInfo {
    public String namee,eamaill,phonee;

    public UserInfo(){


    }

    public UserInfo(String namee, String eamaill, String phonee) {
        this.namee = namee;
        this.eamaill = eamaill;
        this.phonee = phonee;
    }

    public String getNamee() {
        return namee;
    }

    public void setNamee(String namee) {
        this.namee = namee;
    }

    public String getEamaill() {
        return eamaill;
    }

    public void setEamaill(String eamaill) {
        this.eamaill = eamaill;
    }

    public String getPhonee() {
        return phonee;
    }

    public void setPhonee(String phonee) {
        this.phonee = phonee;
    }
}
