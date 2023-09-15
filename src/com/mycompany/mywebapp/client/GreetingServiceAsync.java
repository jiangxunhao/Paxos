package com.mycompany.mywebapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.mywebapp.shared.AcceptReq;
import com.mycompany.mywebapp.shared.AcknowledgeReq;
import com.mycompany.mywebapp.shared.PrepareReq;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
  void register(AsyncCallback<Integer> callback);

  void sendPrepareReq(PrepareReq prepareReq, AsyncCallback<Void> callback);
  void sendAcknowledgeReq(AcknowledgeReq acknowledgeReq, AsyncCallback<Void> callback);
  void sendAcceptReq(AcceptReq acceptReq, AsyncCallback<Void> callback);
  void accept(AcceptReq acceptReq, int processId, AsyncCallback<Void> callback);

  void receivePrepareReq(int processId, AsyncCallback<PrepareReq> callback);
  void receiveAcknowledgeReq(int processId, AsyncCallback<AcknowledgeReq> callback);
  void receiveAcceptReq(int processId, AsyncCallback<AcceptReq> callback);

  void enquire(AsyncCallback<AcceptReq[]> callback);
}
