import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatServer extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private String serverName;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatServer() {
        // Ask for server name
        serverName = JOptionPane.showInputDialog(this, "Enter your name for the chat:");

        // GUI Setup
        setTitle("Chat Server - " + serverName);
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

        setVisible(true);

        // Event listener for sending messages
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    out.println(serverName + ": " + message); // Send message to the client
                    chatArea.append("Me: " + message + "\n");
                    messageField.setText("");
                }
            }
        });

        // Start the server
        try {
            serverSocket = new ServerSocket(1234);
            chatArea.append("Server started, waiting for a client...\n");

            clientSocket = serverSocket.accept(); // Wait for a connection
            chatArea.append("Client connected!\n");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Thread to handle client messages
            new ClientHandler().start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread to handle receiving messages from the client
    private class ClientHandler extends Thread {
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");  // Display client's message
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }
}