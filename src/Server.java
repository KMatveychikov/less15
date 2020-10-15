import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    private static final int PORT = 8189;

    public static void main(String[] args) throws IOException {
        int port = PORT;
        new Server().start(port);
    }

    public void start(int port) throws IOException {
        ServerSocket socket = null;
        Socket clientSocket = null;
        Thread inputThread = null;

        try {
            socket = new ServerSocket(port);
            System.out.println("Server started");
            clientSocket = socket.accept();
            System.out.println("Client connected");
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            inputThread = runInputThread(in);
            runOutputLoop(out);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(inputThread !=null) inputThread.interrupt();
            if(clientSocket != null) clientSocket.close();
        }

        }

    private Thread runInputThread(DataInputStream in){
        Thread thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String message = in.readUTF();
                    System.out.println("From client: " + message);
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
    private void runOutputLoop(DataOutputStream out) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.next();
            out.writeUTF(message);
            if(message.equals("/end")) break;
        }
    }
}
