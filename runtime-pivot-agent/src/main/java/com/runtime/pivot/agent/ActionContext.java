package com.runtime.pivot.agent;

import java.util.Date;

public class ActionContext {
    private String uid;
    private String action;
    private Date date;
    private String datePrintString;
    private String dateFileString;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDatePrintString() {
        return datePrintString;
    }

    public void setDatePrintString(String datePrintString) {
        this.datePrintString = datePrintString;
    }

    public String getDateFileString() {
        return dateFileString;
    }

    public void setDateFileString(String dateFileString) {
        this.dateFileString = dateFileString;
    }
}
