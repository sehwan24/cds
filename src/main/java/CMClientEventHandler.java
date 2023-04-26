import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.*;
import kr.ac.konkuk.ccslab.cm.event.filesync.CMFileSyncEvent;
import kr.ac.konkuk.ccslab.cm.event.filesync.CMFileSyncEventCompleteNewFile;
import kr.ac.konkuk.ccslab.cm.event.filesync.CMFileSyncEventCompleteUpdateFile;
import kr.ac.konkuk.ccslab.cm.event.filesync.CMFileSyncEventSkipUpdateFile;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMFileTransferManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

public class CMClientEventHandler implements CMAppEventHandler {
    private CMClientStub m_clientStub;
    private boolean m_bDistFileProc;
    private long m_lDelaySum;	// for forwarding simulation
    private long m_lStartTime;	// for delay of SNS content downloading, distributed file processing
    private int m_nEstDelaySum;	// for SNS downloading simulation
    private int m_nSimNum;		// for simulation of multiple sns content downloading
    private FileOutputStream m_fos;	// for storing downloading delay of multiple SNS content
    private PrintWriter m_pw;		//
    private int m_nCurrentServerNum;	// for distributed file processing
    private int m_nRecvPieceNum;
    private String m_strExt;			// for distributed file processing
    private String[] m_filePieces;		// for distributed file processing

    public CMClientEventHandler(CMClientStub stub)
    {
        m_clientStub = stub;
        m_bDistFileProc = false;
        m_lDelaySum = 0;
        m_lStartTime = 0;
        m_nEstDelaySum = 0;
        m_nSimNum = 0;
        m_fos = null;
        m_pw = null;
        m_nCurrentServerNum = 0;
        m_nRecvPieceNum = 0;
        m_strExt = null;
        m_filePieces = null;
        //클라이언트 스텁 뽑아내기
    }

    @Override
    public void processEvent(CMEvent cme) {
        switch (cme.getType())
        {
            case CMInfo.CM_SESSION_EVENT: //cm이벤트면 처리
                processSessionEvent(cme);
                break;
            case CMInfo.CM_FILE_EVENT:
                processFileEvent(cme);
                break;
            default:
                break;
        }
    }

    private void processSessionEvent(CMEvent cme)
    {
        CMSessionEvent se = (CMSessionEvent) cme;
        switch (se.getID())
        {
            case CMSessionEvent.LOGIN_ACK:  //로그인 결과
                if(se.isValidUser() == 0) //authentication failed
                {
                    System.err.println("This client fails authentication by the default server!");
                }
                else if(se.isValidUser() == -1) //이미 로그인함
                {
                    System.err.println("This client is already in the login-user list!");
                }
                else  //로그인성공
                {
                    System.out.println("This client successfully logs in to the default server.");
                }
                break;

            default:
                return;
        }
    }



    private void processFileEvent(CMEvent cme)
    {
        CMFileEvent fe = (CMFileEvent) cme;
        int nOption = -1;
        switch(fe.getID())
        {

            /*case CMFileEvent.REQUEST_PERMIT_PUSH_FILE:
                StringBuffer strReqBuf = new StringBuffer();
                strReqBuf.append("["+fe.getFileSender()+"] wants to send a file.\n");
                strReqBuf.append("file path: "+fe.getFilePath()+"\n");
                strReqBuf.append("file size: "+fe.getFileSize()+"\n");
                System.out.print(strReqBuf.toString());
                nOption = JOptionPane.showConfirmDialog(null, strReqBuf.toString(),
                        "Push File", JOptionPane.YES_NO_OPTION);
                if(nOption == JOptionPane.YES_OPTION)
                {
                    m_clientStub.replyEvent(fe, 1);
                }
                else
                {
                    m_clientStub.replyEvent(fe, 1);
                }
                break;
            case CMFileEvent.REPLY_PERMIT_PUSH_FILE:
                if(fe.getReturnCode() == 0)
                {
                    System.err.print("["+fe.getFileReceiver()+"] rejected the push-file request!\n");
                    System.err.print("file path("+fe.getFilePath()+"), size("+fe.getFileSize()+").\n");
                }
                break;
            case CMFileEvent.START_FILE_TRANSFER:
                System.out.println("!!!");
            case CMFileEvent.START_FILE_TRANSFER_CHAN:
                System.out.println("["+fe.getFileSender()+"] is about to send file("+fe.getFileName()+").");
                break;
            case CMFileEvent.END_FILE_TRANSFER:
            case CMFileEvent.END_FILE_TRANSFER_CHAN:
                System.out.println("["+fe.getFileSender()+"] completes to send file("+fe.getFileName()+", "
                        +fe.getFileSize()+" Bytes).");
                if(m_bDistFileProc)
                    processFile(fe.getFileName());
                break;*/
            case CMFileEvent.END_FILE_TRANSFER_ACK:
                System.out.println("End file transfer!!!");
                break;

        }
        return;
    }

    /*private void processFile(String strFile)
    {
        CMConfigurationInfo confInfo = m_clientStub.getCMInfo().getConfigurationInfo();
        String strMergeName = null;

        // add file name to list and increase index
        if(m_nCurrentServerNum == 1)
        {
            m_filePieces[m_nRecvPieceNum++] = confInfo.getTransferedFileHome().toString()+File.separator+strFile;
        }
        else
        {
            // Be careful to put a file into an appropriate array member (file piece order)
            // extract piece number from file name ('filename'-'number'.split )
            int nStartIndex = strFile.lastIndexOf("-")+1;
            int nEndIndex = strFile.lastIndexOf(".");
            int nPieceIndex = Integer.parseInt(strFile.substring(nStartIndex, nEndIndex))-1;

            m_filePieces[nPieceIndex] = confInfo.getTransferedFileHome().toString()+ File.separator+strFile;
            m_nRecvPieceNum++;
        }


        // if the index is the same as the number of servers, merge the split file
        if( m_nRecvPieceNum == m_nCurrentServerNum )
        {
            if(m_nRecvPieceNum > 1)
            {
                // set the merged file name m-'file name'.'ext'
                int index = strFile.lastIndexOf("-");
                strMergeName = confInfo.getTransferedFileHome().toString()+File.separator+
                        strFile.substring(0, index)+"."+m_strExt;

                // merge split pieces
                CMFileTransferManager.mergeFiles(m_filePieces, m_nCurrentServerNum, strMergeName);
            }

            // calculate the total delay
            long lRecvTime = System.currentTimeMillis();
            System.out.println("total delay for ("+m_nRecvPieceNum+") files: "
                    +(lRecvTime-m_lStartTime)+" ms");

            // reset m_bDistSendRecv, m_nRecvFilePieceNum
            m_bDistFileProc = false;
            m_nRecvPieceNum = 0;
        }

        return;
    }*/





}
