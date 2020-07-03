package com0.example.android.bouncingmessengertestapp;

/**
 * Created by AVI on 16-01-2018.
 */

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 */
public class GroupOwnerSocketHandler extends Thread {
    ArrayList al = new ArrayList();
    ArrayList users = new ArrayList();
    ServerSocket serverSocket = null;
    Socket s;
    private final static int PORT = 4545;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "GroupOwnerSocketHandler";

    public GroupOwnerSocketHandler(Handler handler) throws IOException {
        try {
            if(serverSocket==null){
                serverSocket = new ServerSocket(PORT);
            }
            this.handler = handler;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }
    }
    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
                s = serverSocket.accept();
                pool.execute(new GroupChatManager(s, handler,al, users));
                Log.d(TAG, "Launching the I/O handler");

            } catch (IOException e) {
                Log.d(TAG, "IO EXCEPTIONS DETECTED WHILE ACCEPTING REQUEST FROM CLIENT");
                try {
                    if (serverSocket != null && !serverSocket.isClosed()){
                        serverSocket.close();
                    }
                } catch (IOException ioe) {
                    Log.d(TAG, "Server socket not closed properly");
                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
            catch (SecurityException se){
                Log.d(TAG, "Security Exception while accepting connection");
                try{
                    if (serverSocket !=null && !serverSocket.isClosed())
                        serverSocket.close();
                }catch (IOException ioe){
                    Log.d(TAG, "Server socket not closed properly");
                }
            }
        }
    }
}
