package com.mycompany.mywebapp.server;

import com.mycompany.mywebapp.client.GreetingService;
import com.mycompany.mywebapp.shared.AcceptReq;
import com.mycompany.mywebapp.shared.AcknowledgeReq;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mycompany.mywebapp.shared.PrepareReq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
    GreetingService {

  int nextProcessId = 100;
  AcceptReq[] values = new AcceptReq[3];

  List<PrepareReq> pool1 = new ArrayList<>();
  List<PrepareReq> prepareReqPool = Collections.synchronizedList(this.pool1);
  List<AcknowledgeReq> pool2 = new ArrayList<>();
  List<AcknowledgeReq> acknowledgeReqPool = Collections.synchronizedList(this.pool2);
  List<AcceptReq> pool3 = new ArrayList<>();
  List<AcceptReq> acceptReqPool = Collections.synchronizedList(this.pool3);

  public int register() {
    this.nextProcessId++;
    return this.nextProcessId;
  }

  public void sendPrepareReq(PrepareReq prepareReq) {
    synchronized (this.prepareReqPool) {
      prepareReqPool.add(prepareReq);
    }
  }

  public void sendAcknowledgeReq(AcknowledgeReq acknowledgeReq) {
    synchronized (this.acknowledgeReqPool) {
      acknowledgeReqPool.add(acknowledgeReq);
    }
  }

  public void sendAcceptReq(AcceptReq acceptReq) {
    synchronized (this.acceptReqPool) {
      acceptReqPool.add(acceptReq);
    }
  }

  public void accept(AcceptReq acceptReq, int processId) {
    values[processId-101] = acceptReq;
  }

  public PrepareReq receivePrepareReq(int processId) {
    synchronized (this.prepareReqPool) {
      for(PrepareReq prepareReq : prepareReqPool) {
        if(prepareReq.getTo() == processId) {
          prepareReqPool.remove(prepareReq);
          return prepareReq;
        }
      }
      return null;
    }
  }

  public AcknowledgeReq receiveAcknowledgeReq(int processId) {
    synchronized (this.acknowledgeReqPool) {
      for(AcknowledgeReq acknowledgeReq : acknowledgeReqPool) {
        if(acknowledgeReq.getTo() == processId) {
          acknowledgeReqPool.remove(acknowledgeReq);
          return acknowledgeReq;
        }
      }
    }
    return null;
  }

  public AcceptReq receiveAcceptReq(int processId) {
    synchronized (this.acceptReqPool) {
      for(AcceptReq acceptReq : acceptReqPool) {
        if(acceptReq.getTo() == processId) {
          acceptReqPool.remove(acceptReq);
          return acceptReq;
        }
      }
    }
    return null;
  }

  public AcceptReq[] enquire() {
    return values;
  }

}
