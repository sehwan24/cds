import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class CMWinClient extends JFrame {
    private JTextPane jTextPane;
    private JTextField jTextField;
    private JButton m_startStopButton;
    private JButton m_loginLogoutButton;
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
        setLayout(new BorderLayout());

        jTextPane = new JTextPane();
        jTextPane.setBackground(new Color(245,245,245));
        //m_outTextPane.setForeground(Color.WHITE);
        jTextPane.setEditable(false);

        StyledDocument doc = jTextPane.getStyledDocument();
        add(jTextPane, BorderLayout.CENTER);
        JScrollPane centerScroll = new JScrollPane (jTextPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //add(centerScroll);
        getContentPane().add(centerScroll, BorderLayout.CENTER);

        jTextField = new JTextField();
        jTextField.addKeyListener(myKeyListener);
        add(jTextField, BorderLayout.SOUTH);

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setBackground(new Color(220,220,220));
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

        setVisible(true);

        cmClientStub = new CMClientStub();
        cmWinClientEventHandler = new CMWinClientEventHandler();

        startCM();

        jTextField.requestFocus();
    }


    private void startCM() {
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

    private void printMessage(String strText) {
        StyledDocument styledDocument = jTextPane.getStyledDocument();

        try {
            styledDocument.insertString(styledDocument.getLength(), strText, null);
            jTextPane.setCaretPosition(jTextPane.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        return;
    }


    public class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            /*if(button.getText().equals("Start Client CM"))
            {
                testStartCM();
            }
            else if(button.getText().equals("Stop Client CM"))
            {
                testTerminateCM();
            }
            else if(button.getText().equals("Login"))
            {
                // login to the default cm server
                testLoginDS();
            }
            else if(button.getText().equals("Logout"))
            {
                // logout from the default cm server
                testLogoutDS();
            }
            else if(button.equals(m_composeSNSContentButton))
            {
                testSNSContentUpload();
            }
            else if(button.equals(m_readNewSNSContentButton))
            {
                testDownloadNewSNSContent();
            }
            else if(button.equals(m_readNextSNSContentButton))
            {
                testDownloadNextSNSContent();
            }
            else if(button.equals(m_readPreviousSNSContentButton))
            {
                testDownloadPreviousSNSContent();
            }
            else if(button.equals(m_findUserButton))
            {
                testFindRegisteredUser();
            }
            else if(button.equals(m_addFriendButton))
            {
                testAddNewFriend();
            }
            else if(button.equals(m_removeFriendButton))
            {
                testRemoveFriend();
            }
            else if(button.equals(m_friendsButton))
            {
                testRequestFriendsList();
            }
            else if(button.equals(m_friendRequestersButton))
            {
                testRequestFriendRequestersList();
            }
            else if(button.equals(m_biFriendsButton))
            {
                testRequestBiFriendsList();
            }*/

            jTextField.requestFocus();
        }
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
        /*if(bRet)
            cmWinClientEventHandler.setReqAttachedFile(true);
        else
            printMessage(strFileName+" not found in the downloaded content list!\n");*/

        return;
    }



    private void accessAttachedFile(String strFileName) {
        boolean bRet = cmClientStub.accessAttachedFileOfSNSContent(strFileName);
        if(bRet)
            printMessage(strFileName+" not found in the downloaded content list!\n");

        return;
    }


}


