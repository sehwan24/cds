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
    private JButton cmStartStopButton;
    private JButton printFilesButton;
    private JButton modeButton;
    private CMServerStub cmServerStub;
    private CMWinServerEventHandler cmWinServerEventHandler;




    CMWinServer() {
        MyActionListener myActionListener = new MyActionListener();
        setTitle("CM Server");
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

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
        jPanel.setBackground(new Color(189, 151, 189));
        add(jPanel, BorderLayout.NORTH);

        cmStartStopButton = new JButton("Start Server CM");
        cmStartStopButton.addActionListener(myActionListener);
        cmStartStopButton.setEnabled(false);
        jPanel.add(cmStartStopButton);

        printFilesButton = new JButton("Print Files");
        printFilesButton.addActionListener(myActionListener);
        printFilesButton.setEnabled(false);
        jPanel.add(printFilesButton);

        modeButton = new JButton("No Collide Mode(4)");
        modeButton.addActionListener(myActionListener);
        modeButton.setEnabled(false);
        jPanel.add(modeButton);

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

        printMessage2("------------------------------------\n");



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
            else if (button.getText().equals("No Collide Mode(4)"))
            {
                NoCollideMode();
                button.setText("Collide Mode(3)");
            }
            else if (button.getText().equals("Collide Mode(3)"))
            {
                CollideMode();
                button.setText("No Collide Mode(4)");
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
            modeButton.setEnabled(true);
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


    String mode = "Push";
    public void NoCollideMode() {
        mode = "NoCollide";
    }

    public void CollideMode() {
        mode = "Push";
    }

    public String getMode() {
        return mode;
    }





    public static void main(String[] args) {
        CMWinServer cmWinServer = new CMWinServer();
        CMServerStub cmServerStub1 = cmWinServer.getCmServerStub();
        cmServerStub1.setAppEventHandler(cmWinServer.getCmWinServerEventHandler());
    }
}
