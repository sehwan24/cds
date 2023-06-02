import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class CMWinClientEventHandler implements CMAppEventHandler {


    private long m_lStartTime;
    private CMWinClient m_client;
    private CMClientStub m_clientStub;
    private long startTime;
    private boolean m_bReqAttachedFile;

    public CMWinClientEventHandler(CMClientStub cmClientStub, CMWinClient cmWinClient)
    {
        m_client = cmWinClient;
        m_clientStub = cmClientStub;
        m_lStartTime = 0;
    }

    public void setReqAttachedFile(boolean bReq)
    {
        m_bReqAttachedFile = bReq;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    public long getStartTime() {
        return startTime;
    }


    @Override
    public void processEvent(CMEvent cmEvent) {
        switch (cmEvent.getType())
        {
            case CMInfo.CM_SESSION_EVENT:
                processSessionEvent(cmEvent);
                break;
        }
    }

    private void processSessionEvent(CMEvent cme) {
        long lDelay = 0;
        CMSessionEvent se = (CMSessionEvent) cme;
        switch (se.getID()) {
            case CMSessionEvent.LOGIN_ACK:
                lDelay = System.currentTimeMillis() - m_lStartTime;
                printMessage("LOGIN_ACK delay: " + lDelay + " ms.\n");
                if (se.isValidUser() == 0) {
                    printMessage("This client fails authentication by the default server!\n");
                } else if (se.isValidUser() == -1) {
                    printMessage("This client is already in the login-user list!\n");
                } else {
                    printMessage("This client successfully logs in to the default server.\n");
                    CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();

                    // Change the title of the client window
                    m_client.setTitle("CM Client [" + interInfo.getMyself().getName() + "]");

                    // Set the appearance of buttons in the client frame window
                    m_client.setButtonsAccordingToClientState();
                }
                break;
        }
    }

    private void printMessage(String s) {
        m_client.printMessage(s);

        return;
    }

}
