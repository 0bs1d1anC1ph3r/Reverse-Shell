package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;

public class ResponseHandler implements Runnable {

    private BufferedReader in;
    private PrintWriter out;

    public ResponseHandler(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("[*] Response received: " + response);
            }
        } catch (IOException e) {
            if (e instanceof java.net.SocketException) {
                System.err.println("[ERROR] Connection closed by server: " + e.getMessage());
            } else {
                System.err.println("[ERROR] Error reading response: " + e.getMessage());
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Error closing streams: " + e.getMessage());
            }
            System.out.println("[*] ResponseHandler thread exiting.");
        }
    }

}
