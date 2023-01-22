import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

// CLient Class
public class Client extends JFrame{

    Socket socket;

    BufferedReader br;
    PrintWriter out;
    final int portNumber = 1001;

    // Component decleration..
    JLabel setTitle = new JLabel("CLIENT APPLICATION.");
    JTextArea textArea = new JTextArea();
    JTextArea messageArea = new JTextArea();
    JButton button = new JButton("Send");
    // JButton terminate = new JButton("Terminate");
    Font font = new Font("Helvetica", Font.PLAIN, 20);

    // Constructor
    public Client() {
        try {
            System.out.println("Sending request to server");
            String serverSideIPAddress = "127.0.0.1";
            socket = new Socket(serverSideIPAddress, portNumber);
            System.out.println("connection established.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            startReading();
            startWriting();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI()   {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }
        setTitle("Client Messenger");

        // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // int w = screenSize.width*8/10;
        // int h = screenSize.height*8/10;
        // setLocationRelativeTo(null);
        setSize(800,700);
        setLocationByPlatform(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //TITLE--
        setTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        // setTitle.setBackground();
        // setTitle.setForeground();
        setTitle.setHorizontalAlignment(JLabel.CENTER);
        // setTitle.setBorder(BorderFactory.createMatteBorder(20,20,20,20,Color.LIGHT_GRAY));
        setTitle.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY));
        // setTitle.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

            //MESSEGE SHOWN--
        messageArea.setFont(font);
        messageArea.setBackground(Color.GRAY);
        messageArea.setForeground(Color.BLACK);
        messageArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(messageArea);
        DefaultCaret caret = (DefaultCaret)messageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        messageArea.requestFocus();

            //MESSAGE TYPE AREA--
        textArea.setFont(new Font("Times", Font.PLAIN, 20));
        textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "");
        DefaultCaret caret1 = (DefaultCaret)textArea.getCaret();
        caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.requestFocus();
        
            // SEND BUTTON--
        button.setFont(font);
        button.setBackground(new Color(0x2dce98));
        button.setForeground(Color.white);
        JPanel j = new JPanel();
        j.setLayout(new BorderLayout());
        j.add(button, BorderLayout.SOUTH);
        j.setBorder(null);

        // ADDING ALL ELEMENTS TO THE JFRAME
        setLayout(new BorderLayout());
        add(j, BorderLayout.EAST);
        add(setTitle, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(textArea, BorderLayout.SOUTH);
        
        
        setVisible(true);


    }

    synchronized public void startReading() {
        Thread t1 = new Thread(new Runnable(){
        public void run() {
            System.out.println("Reader started...");

            while(!socket.isClosed()) {
                try {
                    String msg = br.readLine();
                    if("End".equals(msg)) {
                        socket.close();
                        System.out.println("Server terminated the chat.");
                        System.out.println("Reader stoped...");
                        break;
                    }
                    messageArea.append("Server: "+msg+"\n");
                    System.out.println("Server: "+msg);
                    
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }});
        t1.start();  
    }

    synchronized public void startWriting() {
        Thread t2 = new Thread(new Runnable(){
        public void run() {
            System.out.println("Writer started...");
            while(!socket.isClosed()) {
                try {
                    button.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String content = textArea.getText();
                            messageArea.append("Me: "+content+"\n");
                            out.println(content);
                            out.flush();
                            textArea.setText("");
                            textArea.requestFocus();

                            if("End".equals(content)) {
                                messageArea.append("Thanks😎...");
                                System.exit(0);
                                try {
                                    socket.close();
                                } catch (Exception e1) { 
                                }
                            }
                        }
                        
                    });
                        BufferedReader br1  = new BufferedReader(new InputStreamReader(System.in));
                        String content = br1.readLine();

                        out.println(content);
                        out.flush();

                        if("End".equals(content)) {
                            socket.close();
                            System.out.println("Writer stoped...");
                            break;
                        }
                        
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
        }});
        t2.start();  
    }

    public static void main(String[] args) {
        System.out.println("This is client...");
        new Client();

    }

}
