import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CMWinServer extends JFrame {


    private JTextPane jTextPane;
    private JTextField jTextField;
    private JButton cmStartButton;
    private CMServerStub cmServerStub;
    private CMWinServerEventHandler cmWinServerEventHandler;


    CMWinServer() {
        MyKeyListener myKeyListener = new MyKeyListener();
        MyActionListener myActionListener = new MyActionListener();
        setTitle("CM Server");
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        jTextPane = new JTextPane();
        jTextPane.setEditable(false);

        StyledDocument styledDocument = jTextPane.getStyledDocument();
        add(jTextPane, BorderLayout.CENTER);
        JScrollPane jScrollPane = new JScrollPane();
        add(jScrollPane);

        jTextField = new JTextField();
        jTextField.addKeyListener(myKeyListener);
        add(jTextField, BorderLayout.SOUTH);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        add(jPanel, BorderLayout.NORTH);

        cmStartButton = new JButton("Start Server CM");
        cmStartButton.addActionListener(myActionListener);
        cmStartButton.setEnabled(false);
        jPanel.add(cmStartButton);

        setVisible(true);

        cmServerStub = new CMServerStub();
        cmWinServerEventHandler = new CMWinServerEventHandler();

        startCM();

    }

    public CMServerStub getCmServerStub() {
        return cmServerStub;
    }

    public CMWinServerEventHandler getCmWinServerEventHandler() {
        return cmWinServerEventHandler;
    }



    public class MyKeyListener implements KeyListener {
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
                    printMessage("Type \"0\" for menu.\n");
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


    public void processInput(String strInput) {
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
    }

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
