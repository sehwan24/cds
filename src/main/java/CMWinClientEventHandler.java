import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMFileEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import java.io.File;
import java.nio.file.*;

public class CMWinClientEventHandler implements CMAppEventHandler {


    private long lStartTime;
    private CMWinClient cmWinClient;
    private CMClientStub cmClientStub;
    private long startTime;

    public CMWinClientEventHandler(CMClientStub cmClientStub, CMWinClient cmWinClient)
    {
        this.cmWinClient = cmWinClient;
        this.cmClientStub = cmClientStub;
        lStartTime = 0;
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
            case CMInfo.CM_FILE_EVENT:
                processFileEvent(cmEvent);
                break;
            case CMInfo.CM_DUMMY_EVENT:
                processDummyEvent(cmEvent);
            default:
                break;
        }
    }

    private void processDummyEvent(CMEvent cmEvent)
    {
        CMDummyEvent due = (CMDummyEvent) cmEvent;
        //System.out.println("session("+due.getHandlerSession()+"), group("+due.getHandlerGroup()+")");
        printMessage("session("+due.getHandlerSession()+"), group("+due.getHandlerGroup()+")\n");
        //System.out.println("dummy msg: "+due.getDummyInfo());
        printMessage("["+due.getSender()+"] sent a dummy msg: "+due.getDummyInfo()+"\n");

        String s = due.getDummyInfo();

        if(String.valueOf(s).equals("NoCollide")) {
            return;
        }



        String[] strArray = s.split("§");

        if(String.valueOf(strArray[0]).equals("CanPush1")) {
            CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            File file = new File("C:\\CMProject\\client-file-path-" + s1 +"\\" + String.valueOf(strArray[1]));
            Path path = file.toPath();
            cmClientStub.pushFile(String.valueOf(path), "SERVER");
            printMessage("생성 동기화 성공\n");
        }

        if(String.valueOf(strArray[0]).equals("Path")) {
            CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            CMDummyEvent cmDummyEvent2 = new CMDummyEvent();
            cmDummyEvent2.setDummyInfo("ToCli§"+s1+"§"+String.valueOf(strArray[2]));   //클라이언트에게
            boolean send1 = cmClientStub.send(cmDummyEvent2, String.valueOf(strArray[1]));
            //printMessage(String.valueOf(send1));
            return;
        }

        if(String.valueOf(strArray[0]).equals("PathD")) {
            CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            CMDummyEvent cmDummyEvent2 = new CMDummyEvent();
            cmDummyEvent2.setDummyInfo("ToCliD§"+s1+"§"+String.valueOf(strArray[2]));   //클라이언트에게
            boolean send1 = cmClientStub.send(cmDummyEvent2, String.valueOf(strArray[1]));
            //printMessage(String.valueOf(send1));
            return;
        }

        if(String.valueOf(strArray[0]).equals("ToCli")) {
            CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            File file1 = new File(".\\client-file-path-" + s1);
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            cmClientStub.setTransferedFileHome(file1.toPath());
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            String s2 = String.valueOf(strArray[2]);
            File file = new File("C:\\CMProject\\client-file-path-" + String.valueOf(strArray[1])+"\\"+s2);
            Path path = file.toPath();
            cmClientStub.pushFile(String.valueOf(path), "SERVER");
            CMDummyEvent cmDummyEvent = new CMDummyEvent();
            cmDummyEvent.setDummyInfo("GoPush§"+s1+"§"+s2);
            cmClientStub.send(cmDummyEvent, "SERVER");

            return;
        }

        if(String.valueOf(strArray[0]).equals("ToCliD")) {
            CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            File file1 = new File(".\\client-file-path-" + s1);
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            cmClientStub.setTransferedFileHome(file1.toPath());
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            String s2 = String.valueOf(strArray[2]);
            File file = new File("C:\\CMProject\\client-file-path-" + s1+"\\"+s2);
            //printMessage(String.valueOf(file));
            boolean delete = file.delete();
            //printMessage(String.valueOf(delete));
            /*Path path = file.toPath();
            m_clientStub.pushFile(String.valueOf(path), "SERVER");*/
            CMDummyEvent cmDummyEvent = new CMDummyEvent();
            cmDummyEvent.setDummyInfo("GoPushD§"+s1+"§"+s2);
            cmClientStub.send(cmDummyEvent, "SERVER");

            return;
        }


        if(String.valueOf(strArray[0]).equals("Toclient")) {
            CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            File file1 = new File(".\\client-file-path-" + s1);
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            cmClientStub.setTransferedFileHome(file1.toPath());
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            String s2 = String.valueOf(strArray[3]);
            cmClientStub.pushFile(s2, "SERVER");

            return;
        }

        if(String.valueOf(strArray[0]).equals("Transfer")) {
            int length = strArray.length;
            printMessage("Online User List:\n");
            for(int h = 0; h < (length-2); h++) {
                printMessage(strArray[1 + h] + "  ");
            }
            printMessage("\n");
            return;

        }

        /*if(String.valueOf(strArray[0]).equals("Transfer2")) {
            if(String.valueOf(strArray[1]).equals(String.valueOf(1))) {
                printMessage("Choose file\n");
            }
            else {
                printMessage("No user online\n");
            }
        }*/

        if(String.valueOf(strArray[1]).equals("Y")){
            if(String.valueOf(strArray[0]).equals("M")) {
                //Path path = Paths.get("C:\\CMProject\\client-file-path");
                CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
                String s1 = interInfo.getMyself().getName();
                //Path path2 = path.resolve(s1);
                File file = new File("C:\\CMProject\\client-file-path-"+s1);
                Path path2 = file.toPath();
                Path path4 = path2.resolve(strArray[2]);
                //path4 print go
                cmClientStub.pushFile(String.valueOf(path4), "SERVER");
                printMessage("수정 동기화 성공\n");
            }
            else if (String.valueOf(strArray[0]).equals("D"))
            {
                printMessage("삭제 동기화 성공\n");
            }
            return;
        }
        else if(String.valueOf(strArray[1]).equals("N")){
            printMessage("동기화 실패\n");
            return;
        }




        if(String.valueOf(strArray[0]).equals("Push")) {

            //printMessage(strArray[1]+strArray[2]);
            int i = Integer.valueOf(strArray[2]);
            //printMessage(String.valueOf(i));
            cmWinClient.ClockUpdate(String.valueOf(strArray[1]), i);



            return;
            /*CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
            String s1 = interInfo.getMyself().getName();
            Path old = Paths.get("C:\\CMProject\\client-file-path\\" + strArray[1]);
            /*while(!Files.exists(old)) {

            }
            printMessage(String.valueOf(old) + "\n");
            Path se = Paths.get("C:\\CMProject\\client-file-path\\"+s1+"\\" + strArray[1]);
            printMessage(String.valueOf(se) + "\n");
            try {
                Path move = Files.move(old, se, StandardCopyOption.ATOMIC_MOVE);
                printMessage(String.valueOf(move));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/

        }





        return;
    }


    private void processFileEvent(CMEvent cmEvent)
    {
        CMFileEvent fe = (CMFileEvent) cmEvent;
        if(!fe.getFileReceiver().equals("SERVER")) {
            File file1 = new File(".\\client-file-path-" + fe.getFileReceiver());
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
            cmClientStub.setTransferedFileHome(file1.toPath());
            //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));
        }
        int nOption = -1;
        switch(fe.getID())
        {

            case CMFileEvent.END_FILE_TRANSFER_ACK:
                printMessage("End file transfer\n");
                //printMessage(String.valueOf(m_clientStub.getTransferedFileHome()));

                CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
                /*String s1 = interInfo.getMyself().getName();
                String str = fe.getFileName();
                Path old = Paths.get("C:\\CMProject\\client-file-path\\" + str);
                while(!Files.exists(old)) {

                }
                printMessage(String.valueOf(old) + "\n");
                Path se = Paths.get("C:\\CMProject\\client-file-path\\"+s1+"\\" + str);
                printMessage(String.valueOf(se) + "\n");
                try {
                    Path move = Files.move(old, se, StandardCopyOption.ATOMIC_MOVE);
                    printMessage(String.valueOf(move));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }*/

                break;

            case CMFileEvent.REQUEST_PERMIT_PULL_FILE:
                //printMessage("\npermit request\n");
                break;



        }
        return;
    }

    private void processSessionEvent(CMEvent cme) {
        long lDelay = 0;
        CMSessionEvent se = (CMSessionEvent) cme;
        switch (se.getID()) {
            case CMSessionEvent.LOGIN_ACK:
                lDelay = System.currentTimeMillis() - lStartTime;
                printMessage("LOGIN_ACK delay: " + lDelay + " ms.\n");
                if (se.isValidUser() == 0) {
                    printMessage("This client fails authentication by the default server!\n");
                } else if (se.isValidUser() == -1) {
                    printMessage("This client is already in the login-user list!\n");
                } else {
                    printMessage("This client successfully logs in to the default server.\n");
                    CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();

                    // Change the title of the client window
                    cmWinClient.setTitle("CM Client [" + interInfo.getMyself().getName() + "]");

                    // Set the appearance of buttons in the client frame window
                    cmWinClient.setButtonsAccordingToClientState();

                    String s = interInfo.getMyself().getName();

                    /*Path path = Paths.get("C:\\CMProject\\client-file-path");
                    Path path1 = path.resolve(s);*/
                    File file = new File("C:\\CMProject\\client-file-path-"+s);
                    //printMessage(String.valueOf(path1));


                    if(!file.exists()) {
                        if(file.mkdir() == true) {
                            printMessage("클라이언트 폴더 생성됨\n");
                        }
                        else {
                            printMessage("클라이언트 폴더 생성 실패\n");
                        }
                    }
                    else {
                        printMessage("클라이언트 폴더가 이미 존재합니다.\n");
                    }


                }
                break;
        }
    }

    private void printMessage(String s) {
        cmWinClient.printMessage(s);

        return;
    }





}
