package obs1d1anc1ph3r.reverseshell.server;

import java.io.*;

public class ResponseHandler implements Runnable {
    private BufferedReader in;

    public ResponseHandler(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Shell> " + response);
                System.out.print("Command> ");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error receiving response: " + e.getMessage());
        }
    }
}
