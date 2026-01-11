import java.io.*;
import java.net.*;

/**
 * The ClientHandler class is responsible for managing the communication
 * with a single connected client.
 * It implements Runnable to allow the server to handle multiple clients
 * concurrently using threads.
 */
class ClientHandler implements Runnable {
    private Socket socket;
    /**
     * Constructs a new ClientHandler.
     * * @param socket The client socket accepted by the ServerSocket.
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    /**
     * The main execution loop of the thread.
     * It handles reading input from the client, parsing the order protocol,
     * validating data, and returning response codes until the client disconnects.
     * * <p>Protocol format: Name,ID,ItemType,Quantity</p>
     * <p>Special command: "DISCONNECT" to close the session.</p>
     */
    @Override
    public void run() {
        // פתיחת ערוצי תקשורת
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String inputLine;
            // לולאה שקוראת הודעות עד שהלקוח מתנתק
            while ((inputLine = in.readLine()) != null) {

                // בדיקה אם הלקוח ביקש להתנתק
                if (inputLine.equalsIgnoreCase("DISCONNECT")) {
                    System.out.println("Client requested disconnect.");
                    break;
                }

                // פירוק ההודעה לפי הפרוטוקול: שם,ID,סוג פריט,כמות
                String[] data = inputLine.split(",");

                // בדיקה האם הגיעו כל השדות הנדרשים
                if (data.length < 4) {
                    out.println("200"); // שגיאה: נתונים חסרים
                    continue;
                }
                try {
                    String name = data[0].trim();
                    int id = Integer.parseInt(data[1].trim());
                    int itemType = Integer.parseInt(data[2].trim());
                    int quantity = Integer.parseInt(data[3].trim());

                    // בדיקה: וידוא שמספר העסק הוא בן 5 ספרות
                    if (id < 10000 || id > 99999) {
                        out.println("200"); // נתייחס לזה כנתונים חסרים/לא תקינים
                        continue;
                    }

                    // בדיקה : כמות שלילית או אפס
                    if (quantity <= 0) {
                        out.println("202");
                        continue;
                    }

                    // שליחה לעיבוד במחלקה המרכזית (שם נמצאת הנעילה)
                    int result = CentralServer.processOrder(name, id, itemType, quantity);
                    // החזרת התשובה ללקוח
                    out.println(result);

                } catch (NumberFormatException e) {
                    // אם המספרים לא נשלחו בפורמט תקין
                    out.println("200");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}