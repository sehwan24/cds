import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class CMWinServerEventHandler implements CMAppEventHandler {


    private CMWinServer m_server;
    private CMServerStub m_serverStub;

    public CMWinServerEventHandler(CMServerStub cmServerStub, CMWinServer cmWinServer)
    {
        m_server = cmWinServer;
        m_serverStub = cmServerStub;
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

    private void processSessionEvent(CMEvent cmEvent) {
        CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
        CMSessionEvent se = (CMSessionEvent) cmEvent;
        switch(se.getID()) {
            case CMSessionEvent.LOGIN:
                //System.out.println("["+se.getUserName()+"] requests login.");
                printMessage("[" + se.getUserName() + "] requests login.\n");
                if (confInfo.isLoginScheme()) {
                    // user authentication...
                    // CM DB must be used in the following authentication..
                    boolean ret = CMDBManager.authenticateUser(se.getUserName(), se.getPassword(),
                            m_serverStub.getCMInfo());
                    if (!ret) {
                        printMessage("[" + se.getUserName() + "] authentication fails!\n");
                        m_serverStub.replyEvent(cmEvent, 0);
                    } else {
                        printMessage("[" + se.getUserName() + "] authentication succeeded.\n");
                        m_serverStub.replyEvent(cmEvent, 1);
                    }
                }
                break;
            case CMSessionEvent.LOGOUT:
                //System.out.println("["+se.getUserName()+"] logs out.");
                printMessage("[" + se.getUserName() + "] logs out.\n");
                break;
        }
    }


        private void printMessage(String s)
        {
            m_server.printMessage(s);
        }

}
