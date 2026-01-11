import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.net.*;
/**
 * The GUIClient class provides a graphical user interface for the Order Management System.
 * It allows users to input order details, validates the input locally,
 * and communicates with the CentralServer via TCP sockets.
 */
public class GUIClient extends JFrame {
    // רכיבי ממשק
    private JTextField nameField = new JTextField(20);
    private JTextField idField = new JTextField(20);
    private JTextField qtyField = new JTextField(20);
    private String[] items = {"Sunglasses (1)", "Belt (2)", "Scarf (3)"};
    private JComboBox<String> itemPicker = new JComboBox<>(items);

    private JButton sendButton = new JButton("Send");
    private JButton disconnectButton = new JButton("Disconnect");

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Constructs the GUIClient frame, initializes all UI components,
     * and sets up event listeners for the buttons.
     */
    public GUIClient() {

        setTitle("Order Management System");
        setSize(450, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);


        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 10, 10, 10));
        JLabel titleLabel = new JLabel("Order Form");
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 10, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        centerPanel.add(new JLabel("Business Name:"));
        centerPanel.add(nameField);
        centerPanel.add(new JLabel("Business ID (5 digits):"));
        centerPanel.add(idField);
        centerPanel.add(new JLabel("Select Item:"));
        centerPanel.add(itemPicker);
        centerPanel.add(new JLabel("Quantity:"));
        centerPanel.add(qtyField);
        add(centerPanel, BorderLayout.CENTER);


        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        southPanel.setBackground(Color.WHITE);

        // כפתור Send
        sendButton.setBackground(new Color(40, 167, 69));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(100, 35));
        sendButton.setFocusPainted(false);

        // כפתור Disconnect
        disconnectButton.setBackground(new Color(220, 53, 69));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setPreferredSize(new Dimension(100, 35));
        disconnectButton.setFocusPainted(false);
        disconnectButton.setVisible(false);

        southPanel.add(sendButton);
        southPanel.add(disconnectButton);
        add(southPanel, BorderLayout.SOUTH);

        // אירועים
        sendButton.addActionListener(e -> sendData());
        disconnectButton.addActionListener(e -> disconnectFromServer());

        setLocationRelativeTo(null);
        setVisible(true);
    }
    /**
     * Gathers data from the text fields, performs local validation,
     * and sends the order message to the server if validation passes.
     * Displays an error dialog if input is invalid.
     */
    private void sendData() {
        String name = nameField.getText().trim();
        String idStr = idField.getText().trim();
        String qtyStr = qtyField.getText().trim();

        //  בדיקה שהשדות לא ריקים
        if (name.isEmpty() || idStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error 200: All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  בדיקה שה-ID והכמות הם מספרים בלבד
        try {
            Integer.parseInt(idStr);
            Integer.parseInt(qtyStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Error 200: ID and Quantity must be numerical numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //  אם הכל תקין - שולחים לשרת
        try {
            if (socket == null || socket.isClosed()) {
                socket = new Socket("localhost", 9999);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }

            String message = name + "," + idStr + "," + (itemPicker.getSelectedIndex() + 1) + "," + qtyStr;
            out.println(message);

            String response = in.readLine();
            handleResponse(response);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Connection Error: " + ex.getMessage());
        }
    }

    /**
     * Processes the server's response code and shows appropriate message dialogs.
     * @param response The response code received from the server (e.g., 100, 200, 201, 202).
     */
    private void handleResponse(String response) {
        if (response == null) return;

        switch (response) {
            case "100":
                JOptionPane.showMessageDialog(this, "Success: Order Received!");
                disconnectButton.setVisible(true);
                break;
            case "200":
                JOptionPane.showMessageDialog(this, "Error 200: Missing or invalid data!", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "201":
                JOptionPane.showMessageDialog(this, "Error 201: Name does not match ID!", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            case "202":
                JOptionPane.showMessageDialog(this, "Error 202: Invalid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Server Response: " + response);
        }
    }

    /**
     * Sends a DISCONNECT command to the server, closes the network resources,
     * and exits the application.
     */
    private void disconnectFromServer() {
        try {
            if (out != null) out.println("DISCONNECT");
            if (socket != null) socket.close();
            dispose();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GUIClient();
    }
}