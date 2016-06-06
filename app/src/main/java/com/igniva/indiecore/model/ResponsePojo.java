package com.igniva.indiecore.model;

/**
 * Wrapper class -  to wrap ant response from webservice
 */
public class ResponsePojo {

    private int status;
    private String description;
    private DataPojo data;
    private ErrorPojo error;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataPojo getData() {
        return data;
    }

    public void setData(DataPojo data) {
        this.data = data;
    }

    public ErrorPojo getError() {
        return error;
    }

    public void setError(ErrorPojo error) {
        this.error = error;
    }

}
