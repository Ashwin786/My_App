package com.rk.myApp.sync_toggle;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rk.myApp.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private TextView text;
    private EditText input;
    private Button send;
    private Socket socket;
    private DataOutputStream outputStream;
    private BufferedReader inputStream;
    private String DeviceName = "Device";

    private boolean searchNetwork() {
        log("Connecting");
        String range = "192.168.1.";
        for (int i = 1; i <= 5; i++) {
            String ip = range + i;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, 8080), 50);
                outputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                DeviceName += "1";
                Log.i("Server", DeviceName);
                log("Connected");
                return true;
            } catch (Exception e) {
            }
        }
        return false;

    }

    private void runNewChatServer() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
            log("Waiting for client...");
            socket = serverSocket.accept();
            DeviceName += "2";
            log("a new client Connected");
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        text = (TextView) findViewById(R.id.text);
        input = (EditText) findViewById(R.id.input);
        send = (Button) findViewById(R.id.send);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!searchNetwork()) {
//                        runNewChatServer();
                    }

                    outputStream = new DataOutputStream(
                            socket.getOutputStream());
                    inputStream = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    while (true) {

                        String Message = inputStream.readLine();
                        if (Message != null) {
                            log(Message);
                        }
                    }
                } catch (IOException e) {
                    log("Error: IO Exception");
                    e.printStackTrace();
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (outputStream == null) {
                    return;
                }
                try {
                    String Message = input.getText().toString() + "\n";
                    outputStream.write(Message.getBytes());
                    log2(input.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                input.setText("");
            }
        });
        thread.start();

    }

    private void log(final String message) {
        handler.post(new Runnable() {
            String DeviceName2 = "";

            @Override
            public void run() {
                if (DeviceName.equals("Device1")) {
                    DeviceName2 = "Device2";
                } else if (DeviceName.equals("Device2")) {
                    DeviceName2 = "Device1";
                } else {
                    DeviceName2 = "UnknowDevice";
                }

                text.setText(text.getText() + "\n" + DeviceName2 + " :"
                        + message);

            }
        });
    }

    private void log2(final String message) {
        handler.post(new Runnable() {

            @Override
            public void run() {


                text.setText(text.getText() + "\n" + "you" + " :"
                        + message);

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
