

import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateRevokedException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class CMServerApp {

    private CMServerStub m_serverStub;
    private CMServerEventHandler m_eventHandler;

    private Scanner m_scan = null;

    private boolean m_bRun;

    public CMServerApp() //서버 객체 생성
    {
        m_serverStub = new CMServerStub();
        m_eventHandler = new CMServerEventHandler(m_serverStub);
        m_bRun = true;
        //서버스텁, 이벤트핸들러 객체 생성
    }

    public CMServerStub getServerStub()
    {
        return m_serverStub; //서버스텁 반환하는 함수
    }

    public CMServerEventHandler getServerEventHandler()

    {
        return m_eventHandler; //이벤트핸들러 반환하는 함수
    }

    public void printLoginUsers()
    {
        System.out.println("========== print login users");
        CMMember loginUsers = m_serverStub.getLoginUsers();
        if(loginUsers == null)
        {
            System.err.println("The login users list is null!");
            return;
        }

        System.out.println("Currently ["+loginUsers.getMemberNum()+"] users are online.");
        Vector<CMUser> loginUserVector = loginUsers.getAllMembers();
        Iterator<CMUser> iter = loginUserVector.iterator();
        int nPrintCount = 0;
        while(iter.hasNext())
        {
            CMUser user = iter.next();
            System.out.print(user.getName()+" ");
            nPrintCount++;
            if((nPrintCount % 10) == 0)
            {
                System.out.println();
                nPrintCount = 0;
            }
        }
    }
    public void startCM()
    {
        boolean bRet = m_serverStub.startCM();
        if(!bRet)
        {
            System.err.println("CM initialization error!");
            return;
        }
        startCMApp();
    }



    public void startCMApp() {
        System.out.println("Server application starts.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        m_scan = new Scanner(System.in);
        String strInput = null;
        int nCommand = -1;
        while (m_bRun) {
            System.out.println("Type 1 for print login users");
            try {
                strInput = br.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                continue;
            }

            try {
                nCommand = Integer.parseInt(strInput);
            } catch (NumberFormatException e) {
                System.out.println("Incorrect command number!");
                continue;
            }

            switch (nCommand) {
                case 1:
                    printLoginUsers();
                    break;
                default:
                    System.err.println("Unknown command.");
                    break;
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_scan.close();
    }

    public static void main(String[] args) {
        CMServerApp server = new CMServerApp(); //서버생성
        CMServerStub cmStub = server.getServerStub(); //서버스텁 받아오기
        cmStub.setAppEventHandler(server.getServerEventHandler());
        server.startCM(); //CM시작
    }
}
