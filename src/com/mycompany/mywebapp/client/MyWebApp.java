package com.mycompany.mywebapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.mycompany.mywebapp.shared.AcceptReq;
import com.mycompany.mywebapp.shared.AcknowledgeReq;
import com.mycompany.mywebapp.shared.PrepareReq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MyWebApp implements EntryPoint {
  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  private final int[] processIds = {101, 102, 103};
  private final int majority = 2;
  private int processId = 0;
  private String log = "";
  private int ballotNumber = 1;

  // local variable for proposer
  private HashMap<Integer, ArrayList<AcknowledgeReq>> quorums = new HashMap<>();
  private HashMap<Integer, String> proposedValue = new HashMap<>();

  // local variable for acceptor
  private int minIdToAccept = 0;
  private String lastAcceptedValue = "";
  private int lastAcceptedId = 0;

  String[] values = new String[3];

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    final Label processIdLabel = new Label();

    final Label acceptor1Value = new Label("");
    final Label acceptor2Value = new Label("");
    final Label acceptor3Value = new Label("");
    final Button receiveButton = new Button("start");

    final TextBox proposeValue = new TextBox();
    final Button sendButton = new Button("send");

    final HTML logLabel = new HTML();

    greetingService.register(new AsyncCallback<Integer>() {
      @Override
      public void onFailure(Throwable throwable) {
        //
      }

      @Override
      public void onSuccess(Integer integer) {
        processId = integer;
        processIdLabel.setText("Process id: " + processId);
        log += "<b>Process id is allocated</b>";
        logLabel.setHTML(log);
      }
    });

    RootPanel.get("processIdContainer").add(processIdLabel);

    RootPanel.get("acceptor1ValueContainer").add(acceptor1Value);
    RootPanel.get("acceptor2ValueContainer").add(acceptor2Value);
    RootPanel.get("acceptor3ValueContainer").add(acceptor3Value);
    RootPanel.get("receiveButtonContainer").add(receiveButton);


    RootPanel.get("proposeValueContainer").add(proposeValue);
    RootPanel.get("sendButtonContainer").add(sendButton);

    RootPanel.get("logLabelContainer").add(logLabel);

    sendButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        final int ballotId = processId * ballotNumber++;

        proposedValue.put(ballotId, proposeValue.getText());

        for(int i = 0; i < processIds.length; i++) {
          final int to = processIds[i];
          PrepareReq prepareReq = new PrepareReq();
          prepareReq.setBallotId(ballotId);
          prepareReq.setFrom(processId);
          prepareReq.setTo(to);

          greetingService.sendPrepareReq(prepareReq, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(Void unused) {
              log += ("<br><b>prepare " + ballotId + " sent to " + to + "</b>");
              logLabel.setHTML(log);
            }
          });
        }
      }
    });

    class receiveClickHandler implements ClickHandler {
      @Override
      public void onClick(ClickEvent clickEvent) {
        log += "<br><b>system starts to be listening</b>";
        logLabel.setHTML(log);

        Timer timer1 = new Timer() {
          @Override
          public void run() {
            greetingService.receivePrepareReq(processId, new AsyncCallback<PrepareReq>() {
              @Override
              public void onFailure(Throwable throwable) {
                //
              }

              @Override
              public void onSuccess(PrepareReq prepareReq) {
                final int ballotId = prepareReq.getBallotId();
                final int from = prepareReq.getFrom();
                final int to = prepareReq.getTo();

                log += "<br><b>received prepare " + ballotId + "(pid) from " + from + "</b>";
                logLabel.setHTML(log);

                AcknowledgeReq acknowledgeReq = new AcknowledgeReq();
                acknowledgeReq.setBallotId(ballotId);
                acknowledgeReq.setFrom(to);
                acknowledgeReq.setTo(from);

                if(minIdToAccept == 0) {
                  minIdToAccept = ballotId;

                  greetingService.sendAcknowledgeReq(acknowledgeReq, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                      //
                    }

                    @Override
                    public void onSuccess(Void unused) {
                      log += "<br><b>sent prepare ack " + ballotId + "(pid) to " + from + "</b>";
                      logLabel.setHTML(log);
                    }
                  });
                } else if(ballotId > minIdToAccept) {
                  minIdToAccept = ballotId;

                  acknowledgeReq.setPre(lastAcceptedId, lastAcceptedValue);

                  greetingService.sendAcknowledgeReq(acknowledgeReq, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                      //
                    }

                    @Override
                    public void onSuccess(Void unused) {
                      log += "<br><b>sent prepare ack " + ballotId + "(pid) to " + from + "</b>";
                      logLabel.setHTML(log);
                    }
                  });
                } else {
                  log += "<br><b>did not send prepare ack " + ballotId + "(pid): acceptor promise " + minIdToAccept + "</b>";
                  logLabel.setHTML(log);
                }
              }
            });

            greetingService.receiveAcknowledgeReq(processId, new AsyncCallback<AcknowledgeReq>() {
              @Override
              public void onFailure(Throwable throwable) {
                //
              }

              @Override
              public void onSuccess(AcknowledgeReq acknowledgeReq) {
                log += "<br><b>received ack from " + acknowledgeReq.getFrom() + "</b>";
                logLabel.setHTML(log);

                int ballotId = acknowledgeReq.getBallotId();

                ArrayList<AcknowledgeReq> quorum;
                if(quorums.containsKey(ballotId)) {
                  quorum = quorums.get(ballotId);
                } else {
                  quorum = new ArrayList<AcknowledgeReq>();
                }
                quorum.add(acknowledgeReq);
                quorums.put(ballotId, quorum);
              }
            });

            greetingService.receiveAcceptReq(processId, new AsyncCallback<AcceptReq>() {
              @Override
              public void onFailure(Throwable throwable) {
                //
              }

              @Override
              public void onSuccess(AcceptReq acceptReq) {

                String value = acceptReq.getValue();

                final int acceptId = acceptReq.getBallotId();
                final String acceptValue = acceptReq.getValue();

                log += "<br><b>received proposal <" + acceptId + ", " + acceptValue + "></b>";
                logLabel.setHTML(log);

                if(acceptId == minIdToAccept) {
                  lastAcceptedValue = value;
                  lastAcceptedId = acceptId;

                  greetingService.accept(acceptReq, processId, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(Void unused) {
                      if (processId - 100 == 1) {
                        acceptor1Value.setText("final:<" + acceptId + ", " + acceptValue + ">");
                      } else if (processId - 100 == 2) {
                        acceptor2Value.setText("final:<" + acceptId + ", " + acceptValue + ">");
                      } else {
                        acceptor3Value.setText("final:<" + acceptId + ", " + acceptValue + ">");
                      }
                      log += "<br><b>accepted proposal <" + acceptId + ", " + acceptValue + "></b>";
                      logLabel.setHTML(log);
                    }
                  });
                } else {
                  log += "<br><b>refused proposal <" + acceptId + ", " + acceptValue + "></b>";
                  logLabel.setHTML(log);
                }
              }
            });

            greetingService.enquire(new AsyncCallback<AcceptReq[]>() {
              @Override
              public void onFailure(Throwable throwable) {

              }

              @Override
              public void onSuccess(AcceptReq[] acceptReqs) {
                int[] ballotIds = new int[3];
                String[] values = new String[3];
                HashMap<Integer, Integer> count = new HashMap<>();
                for(int i = 0; i < acceptReqs.length; i++) {
                  ballotIds[i] = acceptReqs[i].getBallotId();
                  values[i] = acceptReqs[i].getValue();
                  count.put(i, count.getOrDefault(i, 1));
                }

                for(Map.Entry<Integer, Integer> entry : count.entrySet()) {
                  if(entry.getValue() >= majority) {
                    lastAcceptedId = ballotIds[entry.getKey()];
                    lastAcceptedValue = values[entry.getKey()];
                    log += "<br><b>learner received <" + lastAcceptedId + ", "
                            + lastAcceptedValue + "> from " + acceptReqs[entry.getKey()].getFrom() + "</b>";
                    log += "<br><b>majority achieved <" + lastAcceptedId + ", "
                            + lastAcceptedValue + "></b>";
                    logLabel.setHTML(log);
                  }
                  acceptor1Value.setText("final:<" + lastAcceptedId + ", " + lastAcceptedValue + ">");
                  acceptor2Value.setText("final:<" + lastAcceptedId + ", " + lastAcceptedValue + ">");
                  acceptor3Value.setText("final:<" + lastAcceptedId + ", " + lastAcceptedValue + ">");
                }
              }
            });

          }
        };

        timer1.scheduleRepeating(1000);

        Timer timer2 = new Timer() {
          @Override
          public void run() {
            for(Map.Entry<Integer, ArrayList<AcknowledgeReq>> entry : quorums.entrySet()) {
              int ballotId = entry.getKey();
              ArrayList<AcknowledgeReq> quorum = entry.getValue();
              if(quorum.size() >= majority) {
                log += "<br><b>received prepare consensus</b>";
                logLabel.setHTML(log);

                int maxBallotId = ballotId;
                String value = proposedValue.get(ballotId);

                for(AcknowledgeReq ack : quorum) {
                  if (ack.getPreBallotId() != 0 && ack.getBallotId() > maxBallotId) {
                    maxBallotId = ack.getBallotId();
                    value = ack.getPreAcceptedValue();
                  }
                }

                final int ballotID = ballotId;
                final String sendValue = value;

                AcceptReq acceptReq = new AcceptReq();
                acceptReq.set(ballotId, value);
                acceptReq.setFrom(processId);

                if(!value.equals(proposedValue.get(ballotId))) {
                  for(AcknowledgeReq ack : quorum) {
                    acceptReq.setTo(ack.getFrom());
                    final int to = ack.getFrom();
                    greetingService.sendAcceptReq(acceptReq, new AsyncCallback<Void>() {
                      @Override
                      public void onFailure(Throwable throwable) {
                        //
                      }

                      @Override
                      public void onSuccess(Void unused) {
                        log += "<br><b>sending <" + ballotID + ", " + sendValue + "> to " + to + "</b>";
                        logLabel.setHTML(log);

                        if (to - 100 == 1) {
                          acceptor1Value.setText("temp:<" + to + ", " + sendValue + ">");
                        } else if (to - 100 == 2) {
                          acceptor2Value.setText("temp:<" + to + ", " + sendValue + ">");
                        } else {
                          acceptor3Value.setText("temp:<" + to + ", " + sendValue + ">");
                        }

                      }
                    });
                  }
                } else {
                  for(int toId : processIds) {
                    acceptReq.setTo(toId);

                    log += "<br><b>sending <" + ballotID + ", " + sendValue + "> to all acceptors</b>";
                    logLabel.setHTML(log);

                    final int to = toId;
                    greetingService.sendAcceptReq(acceptReq, new AsyncCallback<Void>() {
                      @Override
                      public void onFailure(Throwable throwable) {
                        //
                      }

                      @Override
                      public void onSuccess(Void unused) {
                        acceptor1Value.setText("temp:<" + to + ", " + sendValue + ">");
                        acceptor2Value.setText("temp:<" + to + ", " + sendValue + ">");
                        acceptor3Value.setText("temp:<" + to + ", " + sendValue + ">");
                      }
                    });
                  }
                }
              }
              quorums.remove(ballotId);
            }
          }
        };
        timer2.scheduleRepeating(5000);
      }


    }

    receiveButton.addClickHandler(new receiveClickHandler());
  }

}
