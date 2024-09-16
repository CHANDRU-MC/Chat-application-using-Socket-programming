import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private String clientName;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient() {
        // Ask for client name
        clientName = JOptionPane.showInputDialog(this, "Enter your name for the chat:");

        // GUI Setup
        setTitle("Chat Client - " + clientName);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false); // Not editable
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        messageField = new JTextField();
        sendButton = new JButton("Send");

        panel.add(messageField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);
        add(panel, BorderLayout.SOUTH);

        // Event listener for sending messages
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    out.println(clientName + ": " + message); // Send message to the server
                    chatArea.append("Me: " + message + "\n");
                    messageField.setText("");
                }
            }
        });

        setVisible(true);

        // Connect to the server
        try {
            socket = new Socket("localhost", 1234); // Connect to the server
            chatArea.append("Connected to the server!\n");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Thread to handle incoming server messages
            new ServerHandler().start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread to handle receiving messages from the server
    private class ServerHandler extends Thread {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");  // Display server's message
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}