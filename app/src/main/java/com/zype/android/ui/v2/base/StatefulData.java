package com.zype.android.ui.v2.base;


public class StatefulData<T> {
    public T data;
    public String errorMessage;
    public DataState state;

    public StatefulData(T data, String errorMessage, DataState state) {
        this.data = data;
        this.errorMessage = errorMessage;
        this.state = state;
    }

    public StatefulData(T data) {
        new StatefulData(data, null, DataState.READY);
    }

    public StatefulData(String errorMessage) {
        new StatefulData(null, errorMessage, DataState.ERROR);
    }
}
