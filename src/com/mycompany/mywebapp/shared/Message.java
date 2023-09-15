package com.mycompany.mywebapp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

abstract public class Message implements IsSerializable {
    private int from;
    private int to;

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
