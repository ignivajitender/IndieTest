package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 19/10/16.
 */
public class UpdateMessagePojo {

  private DateModel date_updated;
    private String last_message_Id;
    private String last_message_by;
    private String last_message;

    public String getLast_message_Id() {
        return last_message_Id;
    }

    public void setLast_message_Id(String last_message_Id) {
        this.last_message_Id = last_message_Id;
    }

    public DateModel getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(DateModel date_updated) {
        this.date_updated = date_updated;
    }

    public String getLast_message_by() {
        return last_message_by;
    }

    public void setLast_message_by(String last_message_by) {
        this.last_message_by = last_message_by;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }



}
