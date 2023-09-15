package com.mycompany.mywebapp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AcceptReq extends Message implements IsSerializable {
    private int ballotId;
    private String value;

    public void set(int ballotId, String value) {
        this.ballotId = ballotId;
        this.value = value;
    }

    public int getBallotId() {
        return ballotId;
    }

    public String getValue() {
        return value;
    }
}
