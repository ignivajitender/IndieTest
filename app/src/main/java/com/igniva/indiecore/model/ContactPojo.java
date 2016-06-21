package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 17/6/16.
 */
public class ContactPojo {

    private String contactName;
    private String contactNumber;
    private String contactIcon;
    private boolean isSelected;


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactIcon() {
        return contactIcon;
    }

    public void setContactIcon(String contactIcon) {
        this.contactIcon = contactIcon;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

}
