import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import javax.swing.*;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class CMClientApp {
    private CMClientStub m_clientStub;
    private CMClientEventHandler m_eventHandler;
    private boolean m_bRun;
    private Scanner m_scan = null;
    public CMClientApp()//클라이언트 스텁, 이벤트핸들러 객체 생성
    {
        m_clientStub = new CMClientStub();
        m_eventHandler = new CMClientEventHandler(m_clientStub);
        m_bRun = true;
    }

    public CMClientStub getClientStub() //클라이언트 스텁 반환
    {
        return m_clientStub;
    }

    public CMClientEventHandler getClientEventHandler()  //클라이언트 이벤트핸들러 반환
    {
        return m_eventHandler;
    }

    public void ConnectionDS()
    {
        System.out.println("====== connect to default server");
        m_clientStub.connectToServer();
        System.out.println("======");
    }


    public void SyncLoginDS()
    {
        String strUserName = null;
        String strPassword = null;
        CMSessionEvent loginAckEvent = null;
        Console console = System.console();

        System.out.println("====== login to default server");
        System.out.print("user name: ");
        BufferedReader br4 = new BufferedReader(new InputStreamReader(System.in));
        try {
            strUserName = br4.readLine();
            if(console == null)
            {
                System.out.print("password: ");
                strPassword = br4.readLine();
            }
            else
                strPassword = new String(console.readPassword("password: "));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        loginAckEvent = m_clientStub.syncLoginCM(strUserName, strPassword);
        if(loginAckEvent != null)
        {
            // print login result
            if(loginAckEvent.isValidUser() == 0)
            {
                System.err.println("This client fails authentication by the default server!");
            }
            else if(loginAckEvent.isValidUser() == -1)
            {
                System.err.println("This client is already in the login-user list!");
            }
            else
            {
                System.out.println("This client successfully logs in to the default server.");
            }
        }
        else
        {
            System.err.println("failed the login request!");
        }

        System.out.println("======");
    }

    public void SeveralPushFile()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("====== select files to send: ");
        Path transferHome = m_clientStub.getTransferedFileHome();
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(transferHome.toFile());
        int fcRet = fc.showOpenDialog(null);
        if(fcRet != JFileChooser.APPROVE_OPTION) return;
        File[] files = fc.getSelectedFiles();

        for(File file : files)
            System.out.println("selected file = " + file);
        if(files.length < 1) {
            System.err.println("No file selected!");
            return;
        }

        System.out.println("Receiver of files: ");
        System.out.println("Type \"SERVER\" for the server. ");
        String receiver = scanner.nextLine().trim();

        for(File file : files)
            m_clientStub.pushFile(file.getPath(), receiver);
    }


    public void startCM() {
        System.out.println("client application starts.");
        BufferedReader br3 = new BufferedReader(new InputStreamReader(System.in));
        m_scan = new Scanner(System.in);
        String strInput = null;
        int nCommand = -1;
        while(m_bRun) {
            System.out.println("type number > 1 : Connect DS   2 : Login   3 : Transfer File");
            try {
                strInput = br3.readLine();
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
                    ConnectionDS();
                    break;
                case 2:
                    SyncLoginDS();
                    break;
                case 3:
                    SeveralPushFile();
                    break;
                default:
                    System.err.println("Unknown command.");
                    break;

            }
        }

        try {
            br3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_scan.close();
    }









    public static void main(String[] args) {
        //Scanner scanner = new Scanner(System.in);

        CMClientApp client = new CMClientApp();//클라이언트 객체 생성
        CMClientStub cmStub = client.getClientStub(); //클라이언트 스텁받아오기
        cmStub.setAppEventHandler(client.getClientEventHandler()); //

        client.startCM();

    }
}
