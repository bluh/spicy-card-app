package steelhacks.joe.trevor.vdeck;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable{
    boolean running;
    BluetoothServerSocket serverSock;
    ArrayList<SocketListener> listeners;
    Map<BluetoothSocket, String> queue;
    Context context;
    Thread thread;

    public ServerThread(BluetoothServerSocket serverSock, Context context){
        this.serverSock = serverSock;
        this.context = context;
        this.listeners = new ArrayList<>();
        this.queue = Collections.synchronizedMap(new HashMap<BluetoothSocket, String>());
        this.running = true;
        this.thread = new Thread(this);
        thread.start();
    }

    public void run() {
        System.out.println("Starting server thread...");
        while(running){
            BluetoothSocket sock;
            try {
                System.out.println("Waiting for communication...");
                sock = serverSock.accept();
                System.out.println("GOT: " + sock.toString());
                for(SocketListener listener: listeners){
                    listener.SocketConnect(sock);
                }
                BufferedReader read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                BufferedWriter write = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                System.out.println("WRITE THREAD");
                new Thread(new Runnable() { //thread to write messages
                    BluetoothSocket sock;
                    BufferedWriter write;

                    public void run() {
                        try {
                            while (sock.isConnected()) {
                                if(queue.containsKey(sock)){
                                    String msg = queue.get(sock);
                                    System.out.println("OUT: " + msg);
                                    queue.remove(sock);
                                    write.write(msg + "\n");
                                    write.flush();
                                }

                            }
                        }catch(Exception e){
                            ErrorMessageAlert.create(context,e.toString());
                        }
                        //socket disconnected or we errored
                        for(SocketListener listen: listeners){
                            listen.SocketDisconnect(sock);
                        }
                        running = false;
                    }

                    public Runnable setSocket(BluetoothSocket sock, BufferedWriter write){
                        this.sock = sock;
                        this.write = write;
                        return this;
                    }
                }.setSocket(sock, write)).start();
                System.out.println("READ THREAD");
                new Thread(new Runnable(){ //thread to read messages
                    BluetoothSocket sock;
                    BufferedReader read;

                    public void run() {
                        String line;
                        try {
                            while((sock.isConnected()) && (line = read.readLine()) != null){
                                System.out.println("IN: " + line);
                                for(SocketListener listen: listeners){
                                    listen.SocketMessage(sock, line);
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        //socket disconnected or we errored
                        for(SocketListener listen: listeners){
                            listen.SocketDisconnect(sock);
                        }
                        running = false;
                    }

                    public Runnable setSocket(BluetoothSocket sock, BufferedReader read) {
                        this.sock = sock;
                        this.read = read;
                        return this;
                    }
                }.setSocket(sock, read)).start();
            } catch (IOException e) {
                ErrorMessageAlert.create(context,e.toString());
                return;
            }
            System.out.println("Threads created");
        }
        System.out.println("Loop over...");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void send(BluetoothSocket sock, String message){
        queue.put(sock, message);
    }

    public void addListener(SocketListener l){
        listeners.add(l);
    }
}
