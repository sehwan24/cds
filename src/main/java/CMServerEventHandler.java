import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMFileEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.manager.CMFileTransferManager;
import kr.ac.konkuk.ccslab.cm.manager.CMInteractionManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.io.*;


public class CMServerEventHandler implements CMAppEventHandler {
    private CMServerStub m_serverStub;

    public CMServerEventHandler(CMServerStub serverStub)
    {
        m_serverStub = serverStub;
    }

    @Override
    public void processEvent(CMEvent cme) {
        switch (cme.getType())
        {
            case CMInfo.CM_SESSION_EVENT:  //이벤트 타입확인해서 CM이벤트면 처리
                processSessionEvent(cme);
                break;
            case CMInfo.CM_FILE_EVENT:
                processFileEvent(cme);
                break;
            default:
                return;
        }
    }

    private void processSessionEvent(CMEvent cme)
    {
        CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
        CMSessionEvent se = (CMSessionEvent) cme; //이벤트확인
        switch (se.getID()) {     //login이벤트인 경우 유저네임과 함께 로그인 요청되었다는 문구 출력
            case CMSessionEvent.LOGIN:
                System.out.println("[" + se.getUserName() + "] requests login. ");
                if(confInfo.isLoginScheme())
                {
                    // user authentication...
                    // CM DB must be used in the following authentication..
                    boolean ret = CMDBManager.authenticateUser(se.getUserName(), se.getPassword(),
                            m_serverStub.getCMInfo());
                    if(!ret)
                    {
                        System.out.println("["+se.getUserName()+"] authentication fails!");
                        m_serverStub.replyEvent(se, 0);
                    }
                    else
                    {
                        System.out.println("["+se.getUserName()+"] authentication succeeded.");
                        m_serverStub.replyEvent(se, 1);
                    }
                }
                break;
            /*case CMSessionEvent.LOGOUT:
                System.out.println("["+se.getUserName()+"] logs out.");
                break;*/ //logout은 그냥 클라이언트 종료로

            default:
                return;
        }
    }

    private void processFileEvent(CMEvent cme)
    {
        CMFileEvent fe = (CMFileEvent) cme;
        switch(fe.getID())
        {
            /*case CMFileEvent.REQUEST_PERMIT_PULL_FILE:
                System.out.println("["+fe.getFileReceiver()+"] requests file("+fe.getFileName()+").");
                System.err.print("["+fe.getFileReceiver()+"] requests file("+fe.getFileName()+").\n");
                System.err.print("The pull-file request is not automatically permitted!\n");
                System.err.print("To change to automatically permit the pull-file request, \n");
                System.err.print("set the PERMIT_FILE_TRANSFER field to 1 in the cm-server.conf file\n");
                break;
            case CMFileEvent.REPLY_PERMIT_PULL_FILE:
                if(fe.getReturnCode() == -1)
                {
                    System.err.print("["+fe.getFileName()+"] does not exist in the owner!\n");
                }
                else if(fe.getReturnCode() == 0)
                {
                    System.err.print("["+fe.getFileSender()+"] rejects to send file("
                            +fe.getFileName()+").\n");
                }
                break;
            case CMFileEvent.REQUEST_PERMIT_PUSH_FILE:
                System.out.println("["+fe.getFileSender()+"] wants to send a file("+fe.getFilePath()+
                        ").");
                System.err.print("The push-file request is not automatically permitted!\n");
                System.err.print("To change to automatically permit the push-file request, \n");
                System.err.print("set the PERMIT_FILE_TRANSFER field to 1 in the cm-server.conf file\n");
                break;
            case CMFileEvent.REPLY_PERMIT_PUSH_FILE:
                if(fe.getReturnCode() == 0)
                {
                    System.err.print("["+fe.getFileReceiver()+"] rejected the push-file request!\n");
                    System.err.print("file path("+fe.getFilePath()+"), size("+fe.getFileSize()+").\n");
                }
                break;*/
            /*case CMFileEvent.START_FILE_TRANSFER:
            case CMFileEvent.START_FILE_TRANSFER_CHAN:
                System.out.println("["+fe.getFileSender()+"] is about to send file("+fe.getFileName()+").");
                break;*/
            case CMFileEvent.END_FILE_TRANSFER:
            case CMFileEvent.END_FILE_TRANSFER_CHAN:
                System.out.println("End file transfer!!!");
        }
        return;
    }

    private void processFile(String strSender, String strFile)
    {
        CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
        String strFullSrcFilePath = null;
        String strModifiedFile = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] fileBlock = new byte[CMInfo.FILE_BLOCK_LEN];

        long lStartTime = System.currentTimeMillis();

        // change the modified file name
        strModifiedFile = "m-"+strFile;
        strModifiedFile = confInfo.getTransferedFileHome().toString()+File.separator+strSender+
                File.separator+strModifiedFile;

        // stylize the file
        strFullSrcFilePath = confInfo.getTransferedFileHome().toString()+File.separator+strSender+
                File.separator+strFile;
        File srcFile = new File(strFullSrcFilePath);
        long lFileSize = srcFile.length();
        long lRemainBytes = lFileSize;
        int readBytes = 0;

        try {
            fis = new FileInputStream(strFullSrcFilePath);
            fos = new FileOutputStream(strModifiedFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        try {

            while( lRemainBytes > 0 )
            {
                if( lRemainBytes >= CMInfo.FILE_BLOCK_LEN )
                {
                    readBytes = fis.read(fileBlock);
                }
                else
                {
                    readBytes = fis.read(fileBlock, 0, (int)lRemainBytes);
                }

                fos.write(fileBlock, 0, readBytes);
                lRemainBytes -= readBytes;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // add some process delay here
        for(long i = 0; i < lFileSize/50; i++)
        {
            for(long j = 0; j < lFileSize/50; j++)
            {
                //
            }
        }

        long lEndTime = System.currentTimeMillis();
        System.out.println("processing delay: "+(lEndTime-lStartTime)+" ms");

        // send the modified file to the sender
        CMFileTransferManager.pushFile(strModifiedFile, strSender, m_serverStub.getCMInfo());

        return;
    }
}
