package com.mycompany.mywebapp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AcknowledgeReq extends Message implements IsSerializable {
    private int ballotId;
    private int preBallotId;
    private String preAcceptedValue;

    public void setBallotId(int ballotId) {
        this.ballotId = ballotId;
    }

    public void setPre(int preBallotId, String preAcceptedValue) {
        this.preBallotId = preBallotId;
        this.preAcceptedValue = preAcceptedValue;
    }

    public int getBallotId() {
        return ballotId;
    }

    public int getPreBallotId() {
        return preBallotId;
    }

    public String getPreAcceptedValue() {
        return preAcceptedValue;
    }
}
