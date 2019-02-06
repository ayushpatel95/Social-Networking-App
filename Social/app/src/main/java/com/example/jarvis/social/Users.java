package com.example.jarvis.social;

import java.io.Serializable;

/**
 * Created by jarvis on 17/11/17.
 */

public class Users implements Serializable {
    String firstname;
    String uid;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
