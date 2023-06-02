import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Paths;
import java.util.List;

public class CMWinServer extends JFrame {


    private JTextPane jTextPane;
    private JTextField jTextField;
    private JButton cmStartStopButton;
    private CMServerStub cmServerStub;
    private CMWinServerEventHandler cmWinServerEventHandler;


    CMWinServer() {
        //MyKeyListener myKeyListener = new MyKeyListener();
        MyActionListener myActionListener = new MyActionListener();
        setTitle("CM Server");
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        jTextPane = new JTextPane();
        jTextPane.setEditable(false);
        jTextPane.setPreferredSize(new Dimension(this.getWidth(), 120));

        StyledDocument styledDocument = jTextPane.getStyledDocument();
        add(jTextPane, BorderLayout.SOUTH);
        JScrollPane jScrollPane = new JScrollPane();
        add(jScrollPane);

        /*jTextField = new JTextField();
        jTextField.addKeyListener(myKeyListener);
        add(jTextField, BorderLayout.SOUTH);*/

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        add(jPanel, BorderLayout.NORTH);

        cmStartStopButton = new JButton("Start Server CM");
        cmStartStopButton.addActionListener(myActionListener);
        cmStartStopButton.setEnabled(false);
        jPanel.add(cmStartStopButton);

        setVisible(true);

        cmServerStub = new CMServerStub();
        cmWinServerEventHandler = new CMWinServerEventHandler(cmServerStub, this);

        startCM();

    }

    public CMServerStub getCmServerStub() {
        return cmServerStub;
    }

    public CMWinServerEventHandler getCmWinServerEventHandler() {
        return cmWinServerEventHandler;
    }



    /*public class MyKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if(key == KeyEvent.VK_ENTER) {
                JTextField input = (JTextField)e.getSource();
                String strText = input.getText();
                printMessage(strText+"\n");
                //CM시작
                processInput(strText);
                input.setText("");
                input.requestFocus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void keyTyped(KeyEvent e) {

        }
    }*/

    public class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            if(button.getText().equals("Start Server CM")) {
                //CM시작
                boolean bRet = cmServerStub.startCM();
                if(!bRet) {
                    printMessage("CM init error\n");
                }
                else {
                    printMessage("CM init success\n");
                    button.setText("Stop Server CM");
                }
                jTextField.requestFocus();
            } else if (button.getText().equals("Stop Server CM")) {
                cmServerStub.terminateCM();
                printMessage("Server CM terminates.\n");
                button.setText("Start Server CM");
            }
        }
    }

    public void startCM() {
        boolean bRet = false;

        String serverAddress = null;
        List<String> localAddressList = null;
        int serverPort = -1;


        cmServerStub.setConfigurationHome(Paths.get("."));
        cmServerStub.setTransferedFileHome(cmServerStub.getConfigurationHome().resolve("server-file-path"));

        localAddressList = CMCommManager.getLocalIPList();
        if(localAddressList == null) {
            System.err.println("Local address not found!");
            return;
        }

        serverAddress = cmServerStub.getServerAddress();
        serverPort = cmServerStub.getServerPort();

        bRet = cmServerStub.startCM();
        if(!bRet)
        {
            printMessage("CM initialization error!\n");
        }
        else
        {
            printMessage("Server CM starts.\n");
            //printMessage("Type \"0\" for menu.\n");
            // change button to "stop CM"
            cmStartStopButton.setEnabled(true);
            cmStartStopButton.setText("Stop Server CM");
        }

        //jTextField.requestFocus();



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


   /* public void processInput(String strInput) {
        int nCommand = -1;
        try {
            nCommand = Integer.parseInt(strInput);
        } catch (NumberFormatException e) {
            printMessage("NumberFormatException\n");
            return;
        }

        switch (nCommand){
            case 0:
                //printAllMenus();
                break;
            case 1:
                //
                break;
            default:
                printMessage("Unknown command.\n");
                break;
        }
    }*/

    /*public void setMenus() {
        MyMenuListener menuListener = new MyMenuListener();
        JMenuBar menuBar = new JMenuBar();
        JMenu
    }*/


    public class MyMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String strMenu = e.getActionCommand();
            switch (strMenu) {
                case "start CM":
                    startCM();
                    break;
                case "terminate CM":
                    //terminateCM();
                    break;
                case "connect to default server":
                    //connectToDefaultServer();
                    break;
            }
        }
    }

    public static void main(String[] args) {
        CMWinServer cmWinServer = new CMWinServer();
        CMServerStub cmServerStub1 = cmWinServer.getCmServerStub();
        cmServerStub1.setAppEventHandler(cmWinServer.getCmWinServerEventHandler());
    }
}
