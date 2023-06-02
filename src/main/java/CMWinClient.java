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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CMWinClient extends JFrame {
    private JTextPane jTextPane;
    private JTextPane jTextPane2;
    private JTextField jTextField;
    private JButton m_startStopButton;
    private JButton m_loginLogoutButton;
    private JButton fileUpdateButton;
    private JButton fileTransferButton;
    private JButton m_composeSNSContentButton;
    private JButton m_readNewSNSContentButton;
    private JButton m_readNextSNSContentButton;
    private JButton m_readPreviousSNSContentButton;
    private JButton m_findUserButton;
    private JButton m_addFriendButton;
    private JButton m_removeFriendButton;
    private JButton m_friendsButton;
    private JButton m_friendRequestersButton;
    private JButton m_biFriendsButton;
    private MyMouseListener myMouseListener;
    private CMClientStub cmClientStub;
    private CMWinClientEventHandler cmWinClientEventHandler;

    CMWinClient() {
        MyKeyListener myKeyListener = new MyKeyListener();
        MyActionListener myActionListener = new MyActionListener();
        myMouseListener = new MyMouseListener();
        setTitle("CM Client");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //setMenus();
        //setLayout(new BorderLayout());

        jTextPane2 = new JTextPane();
        jTextPane2.setBackground(new Color(0, 53, 254));
        jTextPane2.setEditable(false);
        jTextPane2.setPreferredSize(new Dimension(this.getWidth(), 400));
        add(jTextPane2, BorderLayout.CENTER);
        JScrollPane centerScroll = new JScrollPane (jTextPane2,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //add(centerScroll);
        getContentPane().add(centerScroll, BorderLayout.CENTER);

        jTextPane = new JTextPane();
        jTextPane.setBackground(new Color(0, 253, 44));
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
        topButtonPanel.setBackground(new Color(253, 0, 127));
        topButtonPanel.setLayout(new FlowLayout());
        add(topButtonPanel, BorderLayout.NORTH);

        m_startStopButton = new JButton("Start Client CM");
        //m_startStopButton.setBackground(Color.LIGHT_GRAY);	// not work on Mac
        m_startStopButton.addActionListener(myActionListener);
        m_startStopButton.setEnabled(false);
        //add(startStopButton, BorderLayout.NORTH);
        topButtonPanel.add(m_startStopButton);

        m_loginLogoutButton = new JButton("Login");
        m_loginLogoutButton.addActionListener(myActionListener);
        m_loginLogoutButton.setEnabled(false);
        topButtonPanel.add(m_loginLogoutButton);

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

        cmClientStub.setConfigurationHome(Paths.get("."));
        cmClientStub.setTransferedFileHome(cmClientStub.getConfigurationHome().resolve("client-file-path"));

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
            m_startStopButton.setEnabled(true);
            m_loginLogoutButton.setEnabled(true);
            printMessage("Client CM starts.\n");
            // change the appearance of buttons in the client window frame
            setButtonsAccordingToClientState();
        }


    }

    public void setButtonsAccordingToClientState() {
        int nClientState;
        nClientState = cmClientStub.getCMInfo().getInteractionInfo().getMyself().getState();
        System.out.println("nClientState = " + nClientState);

        switch (nClientState){
            case CMInfo.CM_INIT, CMInfo.CM_CONNECT:
                m_startStopButton.setText("Stop Client CM");
                m_loginLogoutButton.setText("Login");
                break;
            case CMInfo.CM_LOGIN, CMInfo.CM_SESSION_JOIN:
                m_startStopButton.setText("Stop Client CM");
                m_loginLogoutButton.setText("Logout");
                fileUpdateButton.setEnabled(true);
                fileTransferButton.setEnabled(true);
                break;
            default:
                m_startStopButton.setText("Start Client CM");
                m_loginLogoutButton.setText("Login");
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


    public class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_ENTER)
            {
                JTextField input = (JTextField)e.getSource();
                String strText = input.getText();
                printMessage(strText+"\n");
                // parse and call CM API
                //processInput(strText);
                input.setText("");
                input.requestFocus();
            }
            else if(key == KeyEvent.VK_ALT)
            {

            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            
        }

        @Override
        public void keyTyped(KeyEvent e) {
            
        }
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
            else if (button.getText().equals("File Update"))
            {
                fileUpdate();
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
            password = new String(passwordField.getPassword()); // security problem?

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

    private void fileUpdate() {
        printFiles();
    }

    private void printFiles() {
        CMInteractionInfo interInfo = cmClientStub.getCMInfo().getInteractionInfo();
        String s = interInfo.getMyself().getName();

        Path path = Paths.get("C:\\CMProject\\client-file-path");
        Path path1 = path.resolve(s);
        File file = new File(String.valueOf(path1));
        File[] fileList = file.listFiles();


        if(fileList.length > 0) {
            for(int i = 0; i < fileList.length; i++) {
                printMessage2(String.valueOf(fileList[i])+"\n");
            }
        }
    }

    private void initializeButtons() {
        m_startStopButton.setText("Start Client CM");
        m_loginLogoutButton.setText("Login");
        revalidate();
        repaint();
    }




    public class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getSource() instanceof JLabel)
            {
                JLabel pathLabel = (JLabel)e.getSource();
                String strPath = pathLabel.getText();
                File fPath = new File(strPath);
                try {
                    int index = strPath.lastIndexOf(File.separator);
                    String strFileName = strPath.substring(index+1, strPath.length());
                    if(fPath.exists())
                    {
                        accessAttachedFile(strFileName);
                        Desktop.getDesktop().open(fPath);
                    }
                    else
                    {
                        requestAttachedFile(strFileName);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if(e.getSource() instanceof JLabel) {
                Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                setCursor(cursor);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(e.getSource() instanceof JLabel)
            {
                Cursor cursor = Cursor.getDefaultCursor();
                setCursor(cursor);
            }
        }
    }

    private void requestAttachedFile(String strFileName) {
        boolean bRet = cmClientStub.requestAttachedFileOfSNSContent(strFileName);
        if(bRet)
            cmWinClientEventHandler.setReqAttachedFile(true);
        else
            printMessage(strFileName+" not found in the downloaded content list!\n");

        return;
    }



    private void accessAttachedFile(String strFileName) {
        boolean bRet = cmClientStub.accessAttachedFileOfSNSContent(strFileName);
        if(bRet)
            printMessage(strFileName+" not found in the downloaded content list!\n");

        return;
    }


    public static void main(String[] args) {
        CMWinClient cmWinClient = new CMWinClient();
        CMClientStub cmClientStub1 = cmWinClient.getCmClientStub();
        cmClientStub1.setAppEventHandler(cmWinClient.getCmWinClientEventHandler());
    }


}


