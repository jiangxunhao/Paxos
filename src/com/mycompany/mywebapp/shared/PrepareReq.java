package com.mycompany.mywebapp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PrepareReq extends Message implements IsSerializable {
    private int ballotId;

    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }

    public int getBallotId() {
        return ballotId;
    }
}
