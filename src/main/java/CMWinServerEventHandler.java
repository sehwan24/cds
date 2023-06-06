import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMFileEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

public class CMWinServerEventHandler implements CMAppEventHandler {


    private CMWinServer m_server;
    private CMServerStub m_serverStub;

    String[][] c_array = new String[50][2];

    String[][] array2 = new String[200][2];

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
            case CMInfo.CM_FILE_EVENT:
                processFileEvent(cmEvent);
                break;
            case CMInfo.CM_DUMMY_EVENT:
                processDummyEvent(cmEvent);
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

    int o;
    int i1;
    private void processDummyEvent(CMEvent cmEvent)
    {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        //System.out.println("session("+due.getHandlerSession()+"), group("+due.getHandlerGroup()+")");
        printMessage("session("+due.getHandlerSession()+"), group("+due.getHandlerGroup()+")\n");
        //System.out.println("dummy msg: "+due.getDummyInfo());
        printMessage("["+due.getSender()+"] sent a dummy msg: "+due.getDummyInfo()+"\n");
        String s = due.getDummyInfo();
        printMessage(s);

        if(String.valueOf(s).equals(String.valueOf("Transfer"))) {  //파일 전송 이벤트 받음
            CMMember loginUsers = m_serverStub.getLoginUsers();
            printMessage("0");
            if(loginUsers == null)
            {
                printMessage("The login users list is null!\n");
                return;
            }
            printMessage("1");
            Vector<CMUser> loginUserVector = loginUsers.getAllMembers();
            Iterator<CMUser> iter = loginUserVector.iterator();
            CMDummyEvent cmDummyEvent = new CMDummyEvent();
            String s1 = "Transfer§";
            int f = 0;
            printMessage("2");
            while(iter.hasNext())
            {
                CMUser user = iter.next();
                String s2 = String.valueOf(user.getName());
                s1 = s1 + s2 + "§";
                f++;
            }
            s1 = s1 + String.valueOf(f);
            cmDummyEvent.setDummyInfo(s1);
            m_serverStub.send(cmDummyEvent, due.getSender());
            printMessage("3");
            return;

        }



        String[] strArray = s.split("§");
        printMessage("!!!!!!!!"+String.valueOf(strArray[0])+" "+String.valueOf(strArray[1]));
        String v = String.valueOf(strArray[0]);

        a = 0;

        if((String.valueOf(v).equals("M"))||(String.valueOf(v).equals("D"))) {
            i1 = Integer.valueOf(strArray[1]);  //i
            printMessage(String.valueOf(i1) + "\n");
            c_array[i1][0] = strArray[2];  //파일이름
            printMessage(c_array[i1][0] + "\n");
            c_array[i1][1] = strArray[3];  //로지컬 클락
            printMessage(c_array[i1][1] + "\n");

            for (int u = 0; u < 200; u++) {
                if (String.valueOf(array2[u][0]).equals(String.valueOf(c_array[i1][0]))) { //서버에 파일이 존재하면 서버 업데이트
                    if (String.valueOf(v).equals(String.valueOf("M"))) {   //수정인 경우
                        if (Integer.valueOf(array2[u][1]) > Integer.valueOf(c_array[i1][1])) { //서버 로지컬 클락이 더 클 경우
                            CMDummyEvent cmDummyEvent = new CMDummyEvent();
                            cmDummyEvent.setDummyInfo("M§N");
                            m_serverStub.send(cmDummyEvent, due.getSender());
                        } else {
                            array2[u][1] = String.valueOf(Integer.valueOf(c_array[i1][1]) + 1); //서버 로지컬 클락 업데이트
                            printMessage("99\n");
                            CMDummyEvent cmDummyEvent = new CMDummyEvent();
                            cmDummyEvent.setDummyInfo("M§Y§" + c_array[i1][0]);
                            m_serverStub.send(cmDummyEvent, due.getSender());
                        }
                        a = 1;
                        printMessage(array2[u][0] + "\n" + array2[u][1] + "\n");
                    } else if (String.valueOf(v).equals(String.valueOf("D"))) {  //삭제인 경우
                        if (Integer.valueOf(array2[u][1]) > Integer.valueOf(c_array[i1][1])) { //서버 로지컬 클락이 더 클 경우
                            printMessage("동기화 실패\n");
                            CMDummyEvent cmDummyEvent = new CMDummyEvent();
                            cmDummyEvent.setDummyInfo("D§N");
                            m_serverStub.send(cmDummyEvent, due.getSender());
                        } else {
                            printMessage("삭제\n");
                            String str = String.valueOf(due.getSender());
                            Path path = Paths.get("C:\\CMProject\\server-file-path");
                            Path path1 = path.resolve(str);
                            Path path2 = path1.resolve(String.valueOf(c_array[i1][0]));
                            File file = new File(String.valueOf(path2));
                            boolean delete = file.delete();
                            if (delete == true) {
                                printMessage("삭제 성공\n");
                            }

                            array2[u][0] = null; //서버 파일,로지컬 클락 배열 초기화
                            array2[u][1] = null;
                            CMDummyEvent cmDummyEvent = new CMDummyEvent();
                            cmDummyEvent.setDummyInfo("D§Y§" + c_array[i1][0]);
                            m_serverStub.send(cmDummyEvent, due.getSender());
                        }
                    }
                }
            }
        }




        printMessage("9");


        /*if(String.valueOf(strArray[0]).equals(String.valueOf("Transfer2"))) {

        }*/


        if(String.valueOf(v).equals(String.valueOf("Share"))) {  //파일공유
            printMessage("Server get file\n");
            String a = String.valueOf(strArray[1]);
            String b = String.valueOf(strArray[2]);
            boolean b1 = m_serverStub.pushFile(b,a);
            if(b1) {
                printMessage("filepush success\n");
            }
            else {
                printMessage("filepush fail\n");
            }

        }

        printMessage("8");





        printMessage("7");

        if((a != 1) && String.valueOf(v).equals(String.valueOf("C"))) {
            //서버에 파일이 없으면 서버에 파일 업데이트
            printMessage(a +"  "+ i);

            i1 = Integer.valueOf(strArray[1]);  //i
            printMessage(String.valueOf(i1) + "\n");
            c_array[i1][0] = strArray[2];  //파일이름
            printMessage(c_array[i1][0] + "\n");
            c_array[i1][1] = strArray[3];  //로지컬 클락
            printMessage(c_array[i1][1] + "\n");

            array2[i][0] = c_array[i1][0];
            printMessage(array2[i][0]);
            array2[i][1] = String.valueOf(Integer.valueOf(c_array[i1][1])+1);  //서버 로지컬 클락 업데이트
            printMessage(array2[i][0]+"\n"+array2[i][1]+"\n");
        }

        printMessage("6");

        i++;
        return;
    }

    int i = 0;
    int a;
    int b;
    private void processFileEvent(CMEvent cmEvent) {
        CMFileEvent fe = (CMFileEvent) cmEvent;
        switch(fe.getID())
        {
            case CMFileEvent.END_FILE_TRANSFER_CHAN:




                printMessage(fe.getFileName()+"파일전송 완료\n");


                break;
        }
        return;
    }


    private void printMessage(String s)
    {
            m_server.printMessage(s);
    }

}
