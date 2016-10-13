package com.igniva.indiecore.model;

/**
 * Created by igniva-andriod-05 on 12/10/16.
 */
public class CollectionPojo {

        private String msg;
    private String collection;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public FieldsPojo getFields() {
        return fields;
    }

    public void setFields(FieldsPojo fields) {
        this.fields = fields;
    }

    private String id;
    FieldsPojo fields;

}
