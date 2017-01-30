package com.zype.android.webapi.model;

public class DataModel<T> extends BaseModel {

    private T data;

    public DataModel(T data) {
        setModelData(data);
    }

    public T getModelData() {
        return data;
    }

    public void setModelData(T d) {
        data = d;
    }
}
