import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static java.lang.Thread.sleep;

public class CMWinClient extends JFrame {
    private JTextPane jTextPane;
    private JTextPane jTextPane2;
    private JButton startStopButton;
    private JButton loginLogoutButton;
    private JButton printFilesButton;
    private JButton fileTransferButton;
    private JButton fileUpdateButton;
    private CMClientStub cmClientStub;
    private CMWinClientEventHandler cmWinClientEventHandler;
    String[][] array = new String[50][2];



    CMWinClient() {
        MyActionListener myActionListener = new MyActionListener();
        setTitle("CM Client");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //setMenus();
        //setLayout(new BorderLayout());

        jTextPane2 = new JTextPane();
        jTextPane2.setBackground(new Color(143, 206, 156));
        jTextPane2.setEditable(false);
        jTextPane2.setPreferredSize(new Dimension(this.getWidth(), 400));
        add(jTextPane2, BorderLayout.CENTER);
        JScrollPane centerScroll = new JScrollPane (jTextPane2,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //add(centerScroll);
        getContentPane().add(centerScroll, BorderLayout.CENTER);

        jTextPane = new JTextPane();
        jTextPane.setBackground(new Color(140, 208, 208));
        //m_outTextPane.setForeground(Color.WHITE);
        jTextPane.setEditable(false);
        jTextPane.setPreferredSize(new Dimension(this.getWidth(), 120));

        StyledDocument doc = jTextPane.getStyledDocument();
        add(jTextPane, BorderLayout.SOUTH);
        JScrollPane centerScroll2 = new JScrollPane (jTextPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //add(centerScroll);
        getContentPane().add(centerScroll2, BorderLayout.SOUTH);

        /*jTextField = new JTextField();
        jTextField.addKeyListener(myKeyListener);
        add(jTextField, BorderLayout.SOUTH);*/

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setBackground(new Color(189, 151, 189));
        topButtonPanel.setLayout(new FlowLayout());
        add(topButtonPanel, BorderLayout.NORTH);

        startStopButton = new JButton("Start Client CM");
        //m_startStopButton.setBackground(Color.LIGHT_GRAY);	// not work on Mac
        startStopButton.addActionListener(myActionListener);
        startStopButton.setEnabled(false);
        //add(startStopButton, BorderLayout.NORTH);
        topButtonPanel.add(startStopButton);

        loginLogoutButton = new JButton("Login");
        loginLogoutButton.addActionListener(myActionListener);
        loginLogoutButton.setEnabled(false);
        topButtonPanel.add(loginLogoutButton);

        printFilesButton = new JButton("Print Files");
        printFilesButton.addActionListener(myActionListener);
        printFilesButton.setEnabled(false);
        topButtonPanel.add(printFilesButton);

        fileUpdateButton = new JButton("File Update");
        fileUpdateButton.addActionListener(myActionListener);
        fileUpdateButton.setEnabled(false);
        topButtonPanel.add(fileUpdateButton);

        fileTransferButton = new JButton("File Transfer");
        fileTransferButton.addActionListener(myActionListener);
        fileTransferButton.setEnabled(false);
        topButtonPanel.add(fileTransferButton);

        setVisible(true);

        cmClientStub = new CMClientStub();
        cmWinClientEventHandler = new CMWinClientEventHandler(cmClientStub, this);

        startCM();


        //jTextField.requestFocus();
    }


    private void startCM() {
        boolean bRet = false;

        String serverAddress = null;
        List<String> localAddressList = null;
        int serverPort = -1;

        //cmClientStub.setConfigurationHome(Paths.get("."));
        //cmClientStub.setTransferedFileHome(cmClientStub.getConfigurationHome());  //수정

        localAddressList = CMCommManager.getLocalIPList();
        if(localAddressList == null) {
            System.err.println("Local address not found!");
            return;
        }

        serverAddress = cmClientStub.getServerAddress();
        serverPort = cmClientStub.getServerPort();

        bRet = cmClientStub.startCM();
        if(!bRet)
        {
            printMessage("CM initialization error!\n");
        }
        else
        {
            startStopButton.setEnabled(true);
            loginLogoutButton.setEnabled(true);
            printMessage("Client CM starts.\n");
            // 버튼 활성화
            setButtonsAccordingToClientState();
        }



    }

    public void setButtonsAccordingToClientState() {
        int nClientState;
        nClientState = cmClientStub.getCMInfo().getInteractionInfo().getMyself().getState();
        System.out.println("nClientState = " + nClientState);

        switch (nClientState){
            case CMInfo.CM_INIT, CMInfo.CM_CONNECT:
                startStopButton.setText("Stop Client CM");
                loginLogoutButton.setText("Login");
                break;
            case CMInfo.CM_LOGIN, CMInfo.CM_SESSION_JOIN:
                startStopButton.setText("Stop Client CM");
                loginLogoutButton.setText("Logout");
                printFilesButton.setEnabled(true);
                fileUpdateButton.setEnabled(true);
                fileTransferButton.setEnabled(true);
                break;
            default:
                startStopButton.setText("Start Client CM");
                loginLogoutButton.setText("Login");
                break;
        }
        revalidate();
        repaint();
    }


    private CMClientStub getCmClientStub() {
        return cmClientStub;
    }

    private CMWinClientEventHandler getCmWinClientEventHandler() {
        return cmWinClientEventHandler;
    }



    public void printMessage(String strText) {
        StyledDocument styledDocument = jTextPane.getStyledDocument();

        try {
            styledDocument.insertString(styledDocument.getLength(), strText, null);
            jTextPane.setCaretPosition(jTextPane.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return;
    }

    public void printMessage2(String strText) {
        StyledDocument styledDocument = jTextPane2.getStyledDocument();

        try {
            styledDocument.insertString(styledDocument.getLength(), strText, null);
            jTextPane2.setCaretPosition(jTextPane2.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return;
    }


    public class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            if(button.getText().equals("Start Client CM"))
            {
                startCM();
            }
            else if(button.getText().equals("Stop Client CM"))
            {
                TerminateCM();
            }
            else if(button.getText().equals("Login"))
            {
                // login to the default cm server
                LoginDS();
            }
            else if(button.getText().equals("Logout"))
            {
                // logout from the default cm server
                LogoutDS();
            }
            else if(button.getText().equals("File Update"))
            {
                fileUpdate();
            }
            else if (button.getText().equals("Print Files"))
            {
                printFiles();
            }
            else if (button.getText().equals("File Transfer")) {
                fileTransfer();
            }

            //jTextField.requestFocus();
        }
    }

    private void LogoutDS() {
        boolean bRequestResult = false;
        printMessage("Logout DS\n");
        bRequestResult = cmClientStub.logoutCM();
        if(bRequestResult)
            printMessage("successfully sent the logout request.\n");
        else
            printMessage("failed the logout request.\n");

        setButtonsAccordingToClientState();
        setTitle("CM Client");
    }

    private void LoginDS() {
        String username = null;
        String password = null;
        boolean bRequestResult = false;

        printMessage("Login DS\n");
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "username:", usernameField,
                "password:", passwordField
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Login Input", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION)
        {
            username = usernameField.getText();
            password = new String(passwordField.getPassword());

            cmWinClientEventHandler.setStartTime(System.currentTimeMillis());
            bRequestResult = cmClientStub.loginCM(username, password);
            long lDelay = System.currentTimeMillis() - cmWinClientEventHandler.getStartTime();
            if(bRequestResult)
            {
                printMessage("successfully sent the login request.\n");
                printMessage("return delay: "+lDelay+" ms.\n");
                setButtonsAccordingToClientState();
            }
            else
            {
                printMessage("failed the login request!\n");
                cmWinClientEventHandler.setStartTime(0);
            }

        };

    }

    private void TerminateCM() {
        cmClientStub.terminateCM();
        printMessage("Client CM terminates.\n");
        initializeButtons();
        setTitle("CM Client");
    }



    int i = 0;
    int n, m;
    public void fileUpdate() {

        CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
        String s = interInfo.getMyself().getName();
/*
        Path path0 = Paths.get("C:\\CMProject\\client-file-path");
        Path path1 = path0

 */
        File file = new File("C:\\CMProject\\client-file-path-" + s);
        Path path1 = file.toPath();   //수정

        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            path1.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long start = System.currentTimeMillis();
        Path serverPath = Paths.get("C:\\CMProject\\server-file-path");
        Path path2 = serverPath.resolve(s);

        while(true) {
            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<WatchEvent<?>> list = key.pollEvents();
            for (WatchEvent<?> event : list) {
                WatchEvent.Kind<?> kind = event.kind();
                Path pth = (Path) event.context();
                Path path3 = path2.resolve(pth); //server
                Path path4 = path1.resolve(pth); //client
                if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                    //파일 생성 시
                    printMessage(pth.getFileName() + " 생성\n");
                    CMDummyEvent cmDummyEvent = new CMDummyEvent();
                    array[i][0] = String.valueOf(pth.getFileName());      //i는 클라이언트 파일배열
                    printMessage("클라이언트 파일 이름 : "+array[i][0]);
                    array[i][1] = String.valueOf(1);
                    printMessage("  로지컬 클락 : " + array[i][1]+ "\n");
                    cmDummyEvent.setDummyInfo("C" + "§" + String.valueOf(i) +"§"+ array[i][0] +"§" + array[i][1]+"§"+s);
                    cmClientStub.send(cmDummyEvent, cmClientStub.getDefaultServerName());
                    printMessage("파일 생성 동기화 요청\n");
                    i++;
                    return;
                } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    //파일 삭제 시
                    printMessage(pth.getFileName() + " 삭제\n");
                    CMDummyEvent cmDummyEvent = new CMDummyEvent();
                    int v1 = -1;
                    for(int k = 0; k < 50; k++) {  //몇 번 파일 삭제됐나 확인
                        if(String.valueOf(pth.getFileName()).equals(array[k][0])){    //클라이언트
                            array[k][1] = String.valueOf(Integer.valueOf(array[k][1])+1);  //로지컬 클락 업데이트
                            printMessage("클라이언트 파일 이름 : "+array[k][0]);
                            printMessage("  로지컬 클락 : " + array[k][1]+ "\n");
                            //printMessage("\n"+array[k][0]+"\n"+array[k][1]+"\n");
                            n = k;
                            v1 = 1;
                        }
                    }
                    if(v1 == 1) {
                        cmDummyEvent.setDummyInfo("D" + "§" + String.valueOf(n) + "§" + array[n][0] + "§" + array[n][1]);
                        cmClientStub.send(cmDummyEvent, cmClientStub.getDefaultServerName()); //서버와 통신
                        printMessage("파일 삭제 동기화 요청\n");
                        array[n][0] = null;
                        array[n][1] = null;  //삭제했으니 파일명, 로지컬 클락 배열 초기화
                        printMessage("클라이언트 파일 정보가 삭제되었습니다.\n");
                    }
                    else {
                        CMDummyEvent cmDummyEvent1 = new CMDummyEvent();
                        cmDummyEvent1.setDummyInfo("D2§"+pth.getFileName());
                        cmClientStub.send(cmDummyEvent1, "SERVER");
                    }
                    return;
                } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    //파일 수정 시
                    printMessage(pth.getFileName() + " 수정\n");
                    CMDummyEvent cmDummyEvent = new CMDummyEvent();
                    int v = -1;
                    for(int k = 0; k < 50; k++) {  //몇 번 파일 수정됐나 확인
                        if(String.valueOf(pth.getFileName()).equals(array[k][0])){    //클라이언트
                            array[k][1] = String.valueOf(Integer.valueOf(array[k][1])+1);  //로지컬 클락 업데이트
                            printMessage("클라이언트 파일 이름 : "+array[k][0]);
                            printMessage("  로지컬 클락 : " + array[k][1]+ "\n");
                            n = k;
                            v = 1;
                        }
                    }
                    if(v == 1) {
                        cmDummyEvent.setDummyInfo("M" + "§" + String.valueOf(n) + "§" + array[n][0] + "§" + array[n][1]);
                        printMessage("파일 수정 동기화 요청\n");
                    }
                    else {
                        cmDummyEvent.setDummyInfo("M2§"+pth.getFileName());
                        File file1 = new File("C:\\CMProject\\client-file-path-"+s+"\\"+pth.getFileName());
                        Path path = file1.toPath();
                        cmClientStub.pushFile(String.valueOf(path), "SERVER");
                    }
                    cmClientStub.send(cmDummyEvent, cmClientStub.getDefaultServerName());

                    return;
                }
            }
            if(!key.reset()) break;


        }
        try {
            watchService.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void ClockUpdate(String filename, int clock) {
        array[i][0] = filename;
        array[i][1] = String.valueOf(clock+1);

        printMessage("클라이언트 파일 이름 : "+array[i][0]);
        printMessage("  로지컬 클락 : " + array[i][1]+ "\n");

    }


    private void fileTransfer() {

       /* Path pth0 = Paths.get(".\\client-file-path");
        cmClientStub.setTransferedFileHome(pth0);*/

        cmClientStub.setTransferedFileHome(Path.of(".\\"));

        CMDummyEvent cmDummyEvent = new CMDummyEvent();
        cmDummyEvent.setDummyInfo("Transfer");
        cmClientStub.send(cmDummyEvent, cmClientStub.getDefaultServerName());

        String strusername = null;
        JTextField user = new JTextField();

        Object[] message = {
                "user:", user
        };
        int option = JOptionPane.showConfirmDialog(null, message, "User Input", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            strusername = user.getText();
        }

        /*CMDummyEvent cmDummyEvent2 = new CMDummyEvent();
        cmDummyEvent2.setDummyInfo("Transfer2§" + strusername);
        cmClientStub.send(cmDummyEvent2, cmClientStub.getDefaultServerName());*/


        /*if(String.valueOf(strusername).equals(String.valueOf(s))) {
            printMessage("다른 클라이언트를 입력하세요.\n");
        }*/


        CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
        String s = interInfo.getMyself().getName();

        Path transferHome = cmClientStub.getTransferedFileHome();
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(false);
        Path path = transferHome;
        File file0 = new File(".\\client-file-path-" + s);
        //Path path1 = path.resolve(s);
        fc.setCurrentDirectory(file0);
        int fcRet = fc.showOpenDialog(null);
        if(fcRet != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        //printMessage(file.getPath());

        File file1 = new File(".\\client-file-path-" + strusername);
        cmClientStub.setTransferedFileHome(file1.toPath());


        /*
        Path path3 = cmClientStub.getTransferedFileHome();
        printMessage(String.valueOf(path3));
        Path path4 = path3.resolve(String.valueOf(strusername));
        printMessage(String.valueOf(path4));
        cmClientStub.setTransferedFileHome(path4);

        while(!String.valueOf(path4).equals(String.valueOf(cmClientStub.getTransferedFileHome()))) {
            cmClientStub.setTransferedFileHome(path4);
        }*/

        CMInteractionInfo interInfo1 = cmClientStub.getCMInfo().getInteractionInfo();
        String s2 = interInfo.getMyself().getName();

        String s1 = file.getName();

        /*Path pth = Paths.get("C:\\CMProject\\client-file-path");
        Path pth1 = pth.resolve(s2);
        Path pth2 = pth1.resolve(s1);*/

        File file2 = new File("C:\\CMProject\\client-file-path-" + s2);
        Path pth2 = file2.toPath();
        Path pth3 = pth2.resolve(s1);



        File file3 = new File(".\\client-file-path-" + strusername);
        cmClientStub.setTransferedFileHome(file3.toPath());

        CMDummyEvent cmDummyEvent2 = new CMDummyEvent();
        cmDummyEvent2.setDummyInfo("Toclient§"+s1+"§"+strusername+"§"+pth3);
        boolean send1 = cmClientStub.send(cmDummyEvent2, strusername);
        //printMessage(String.valueOf(send1));

        cmClientStub.pushFile(String.valueOf(pth3), "SERVER");   //서버로 선택된 파일 전송



        CMDummyEvent cmDummyEvent1 = new CMDummyEvent();
        //printMessage(file.getName() + "\n!@!#!#\n");
        cmDummyEvent1.setDummyInfo("Share§" + String.valueOf(strusername) + "§" + String.valueOf(file.getName()));

        boolean send = cmClientStub.send(cmDummyEvent1, cmClientStub.getDefaultServerName());
        //printMessage(String.valueOf(send));


    }



    public void printFiles() {
        CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
        String s = interInfo.getMyself().getName();

        /*path path = Paths.get("C:\\CMProject\\client-file-path");
        Path path1 = path.resolve(s);
        File file = new File(String.valueOf(path1));*/
        File file = new File("C:\\CMProject\\client-file-path-"+s+"\\");
        File[] fileList = file.listFiles();


        if(fileList.length > 0) {
            for(int i = 0; i < fileList.length; i++) {
                printMessage2(String.valueOf(fileList[i])+"\n");
            }
        }

        printMessage2("------------------------------------------------\n");


    }

    private void initializeButtons() {
        startStopButton.setText("Start Client CM");
        loginLogoutButton.setText("Login");
        revalidate();
        repaint();
    }







    public static void main(String[] args) {
        CMWinClient cmWinClient = new CMWinClient();
        CMClientStub cmClientStub1 = cmWinClient.getCmClientStub();
        cmClientStub1.setAppEventHandler(cmWinClient.getCmWinClientEventHandler());

    }


}


