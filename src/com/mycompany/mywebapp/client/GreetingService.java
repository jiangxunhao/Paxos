package com.mycompany.mywebapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mycompany.mywebapp.shared.AcceptReq;
import com.mycompany.mywebapp.shared.AcknowledgeReq;
import com.mycompany.mywebapp.shared.PrepareReq;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
  int register();
  void sendPrepareReq(PrepareReq prepareReq);
  void sendAcknowledgeReq(AcknowledgeReq acknowledgeReq);
  void sendAcceptReq(AcceptReq acceptReq);
  void accept(AcceptReq acceptReq, int processId);

  PrepareReq receivePrepareReq(int processId);
  AcknowledgeReq receiveAcknowledgeReq(int processId);
  AcceptReq receiveAcceptReq(int processId);

  AcceptReq[] enquire();

}
