import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
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
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CMWinServer extends JFrame {


    private JTextPane jTextPane;
    private JTextPane jTextPane2;
    private JTextField jTextField;
    private JButton cmStartStopButton;
    private JButton printFilesButton;
    private CMServerStub cmServerStub;
    private CMWinServerEventHandler cmWinServerEventHandler;
    public static int serverlogicalclock;




    CMWinServer() {
        //MyKeyListener myKeyListener = new MyKeyListener();
        MyActionListener myActionListener = new MyActionListener();
        setTitle("CM Server");
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

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
        jTextPane.setBackground(new Color(132, 253, 1));
        jTextPane.setEditable(false);
        jTextPane.setPreferredSize(new Dimension(this.getWidth(), 120));

        add(jTextPane, BorderLayout.SOUTH);
        JScrollPane jScrollPane = new JScrollPane(jTextPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(jScrollPane, BorderLayout.SOUTH);

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

        printFilesButton = new JButton("Print Files");
        printFilesButton.addActionListener(myActionListener);
        printFilesButton.setEnabled(false);
        jPanel.add(printFilesButton);

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





    public void printFiles() {
        Path path = Paths.get("C:\\CMProject\\server-file-path");
        File file = new File(String.valueOf(path));
        File[] fileList = file.listFiles();

        if(fileList.length > 0) {
            for(int i = 0; i < fileList.length; i++) {
                printMessage2(String.valueOf(fileList[i])+"\n");
            }
        }

        printMessage2("--------------------------\nSL: "+ serverlogicalclock);
    }

    private void printMessage2(String strText) {
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
                //jTextField.requestFocus();
            }
            else if (button.getText().equals("Stop Server CM"))
            {
                cmServerStub.terminateCM();
                printFilesButton.setEnabled(false);
                printMessage("Server CM terminates.\n");
                button.setText("Start Server CM");
            }
            else if (button.getText().equals("Print Files"))
            {
                printFiles();
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
            printFilesButton.setEnabled(true);
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
