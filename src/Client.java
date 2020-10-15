
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {


    public static void main(String[] args) throws IOException {
        new Client().start("localhost",8189);
    }
    public void start(String host, int port) throws IOException {
        Socket socket = null;
        Thread inputThread = null;
        try {
            socket = new Socket(host, port);
            System.out.println("Client started");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            inputThread = runInputThread(in);
            runOutputLoop(out);
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputThread !=null) inputThread.interrupt();
            if(socket != null) socket.close();
        }
    }

    private void runOutputLoop(DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.next();
            out.writeUTF(message);
            if(message.equals("/end")) break;
        }

}
    private Thread runInputThread(DataInputStream in){
        Thread thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String message = in.readUTF();
                    System.out.println("From server: " + message);
                    if(message.equals("/end")) System.exit(0);
                }catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Connection closed");
                    break;
                }
            }
        });
        thread.start();
        return thread;

    }
}
