package com.example.pepe.gato;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    TicTacToe myTicTacToe;
    Button buttons [];

    //Server
    private ServerSocket serverSocket;
    Handler updateConversationHandler;
    Thread serverThread = null;
    public static final int SERVERPORT = 7000;

    //Client
    private Socket socket;
    private String SERVER_IP = "187.190.51.33";

    private boolean firstconnection = true;
    private boolean multiplayer = false;
    private boolean turno = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //UI
        TextView textIP = (TextView) findViewById(R.id.textViewIP);
        textIP.setText(this.getLocalIpAddress() + " [offline]");

        myTicTacToe = new TicTacToe(1, textIP.getText().toString(), "local");

        buttons = new Button[9];

        buttons[0] = (Button) findViewById(R.id.b00);
        buttons[1] = (Button) findViewById(R.id.b01);
        buttons[2] = (Button) findViewById(R.id.b02);
        buttons[3] = (Button) findViewById(R.id.b10);
        buttons[4] = (Button) findViewById(R.id.b11);
        buttons[5] = (Button) findViewById(R.id.b12);
        buttons[6] = (Button) findViewById(R.id.b20);
        buttons[7] = (Button) findViewById(R.id.b21);
        buttons[8] = (Button) findViewById(R.id.b22);

        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(0, 0, true);
            }
        });
        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(0, 1, true);
            }
        });
        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(0, 2, true);
            }
        });
        buttons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(1, 0, true);
            }
        });
        buttons[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(1, 1, true);
            }
        });
        buttons[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(1, 2, true);
            }
        });
        buttons[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(2, 0, true);
            }
        });
        buttons[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(2, 1, true);
            }
        });
        buttons[8].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move(2, 2, true);
            }
        });


        //Connection
        //Server
        updateConversationHandler = new Handler();

        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();

        //UI again
        update();

        ((TextView) findViewById(R.id.textViewStatus)).setText("Conectate a la IP de tu amigo para jugar");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.conectar) {
            showPopUpConnect();
            return true;
        }
        if (id == R.id.revancha) {
            if(!multiplayer) {
                myTicTacToe = new TicTacToe(1, "", "");
                turno = true;
                update();
                return true;
            }
            try {
                myTicTacToe = new TicTacToe(2, myTicTacToe.getEstado().get("jugador").toString(),myTicTacToe.getEstado().get("rival").toString() );
                String str = "IP " + getLocalIpAddress() + " revancha";
                PrintWriter out = new PrintWriter(new BufferedWriter(
                       new OutputStreamWriter(socket.getOutputStream())),
                       true);
                out.println(str);
                update();
            } catch (JSONException e) {
                Toast.makeText(this.getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPopUpConnect(){

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptView = layoutInflater.inflate(R.layout.popupconnect, null);

        final EditText inputIP = (EditText) promptView.findViewById(R.id.editTextIP);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };
        inputIP.setFilters(filters);
        AlertDialog.Builder popupConnectBuilder = new AlertDialog.Builder(this);

        popupConnectBuilder.setView(promptView);
        popupConnectBuilder.setTitle("Ingresa la IP");
        popupConnectBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                    // Connect
                        SERVER_IP = inputIP.getText().toString();

                        new Thread(new ClientThread()).start();
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog popupConnectDialog = popupConnectBuilder.create();
        popupConnectDialog.show();
    }

    public String getLocalIpAddress(){

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        if(wm.isWifiEnabled()){
            return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        }

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    public void move(int x, int y, boolean intern){
        if(!multiplayer){
            intern = turno;
            turno = !turno;
        }

        if(myTicTacToe.mueve(x,y,intern)) {
            update();
            if(intern) {
                try {
                    String str = x + " " + y;
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);
                    out.println(str);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(){
        try {
            JSONObject stats = myTicTacToe.getEstado();
            ((TextView) findViewById(R.id.textViewStatus)).setText(stats.get("estado").toString());
            String aux = stats.get("tablero").toString();
            String [] aux2 = aux.split(",");
            for(int i = 0; i < 9; i++){
                switch(aux2[i]){
                    case "0":
                        buttons[i].setText("");
                        break;
                    case "1":
                        buttons[i].setText("X");
                        break;
                    case "2":
                        buttons[i].setText("O");
                        break;
                    default:
                        Toast.makeText(this.getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            Toast.makeText(this.getApplicationContext(), "error", Toast.LENGTH_LONG).show();
        }
    }

    public void nuevo(int tipo, String Local, String Remote){
        myTicTacToe = new TicTacToe(tipo, Local, Remote);
    }

    public void revancha(int tipo, String Local, String Remote){
        myTicTacToe = new TicTacToe(tipo, Local, Remote);
    }

    /*********************************************************************************************
     *                                                                                           *
     *                                          Server                                           *
     *                                                                                           *
     *********************************************************************************************/

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    socket = serverSocket.accept();
                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();
                    updateConversationHandler.post(new updateUIThread(read));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            System.out.println(Arrays.toString(msg.split(" ")));
            if(msg.split(" ").length == 1){
                if(msg.split(" ")[0].replace(" ", "").compareTo("Conectado") == 0) {
                    ((TextView) findViewById(R.id.textViewIP)).setText(getLocalIpAddress());
                    nuevo(1, getLocalIpAddress(), msg.split(" ")[0]);
                    update();
                }
            }
            if(msg.split(" ").length == 2){
                move(Integer.parseInt(msg.split(" ")[0]), Integer.parseInt(msg.split(" ")[1]), false);
                return;
            }
            if(msg.split(" ").length == 3){
                if(msg.split(" ")[0].compareTo("IP") == 0){
                    Toast.makeText(context, "Jugando con: "+ msg.split(" ")[1], Toast.LENGTH_SHORT).show();
                    if(msg.split(" ")[2].replace(" ","").compareTo("nuevo") == 0){
                        ((TextView) findViewById(R.id.textViewIP)).setText(getLocalIpAddress());
                        nuevo(2, getLocalIpAddress(), msg.split(" ")[1]);

                        SERVER_IP = msg.split(" ")[1].replace(" ", "");
                        firstconnection = false;
                        new Thread(new ClientThread()).start();

                        update();
                    }
                    if(msg.split(" ")[2].replace(" ","").compareTo("revancha") == 0){
                        revancha(1, getLocalIpAddress(), msg.split(" ")[1]);
                        update();
                    }
                }
            }
        }
    }

    /*********************************************************************************************
     *                                                                                           *
     *                                          Client                                           *
     *                                                                                           *
     *********************************************************************************************/

    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);

                multiplayer = true;
                String str;
                if(!firstconnection)
                    str = "Conectado";
                else
                    str = "IP " + getLocalIpAddress() + " nuevo";
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);
                    out.println(str);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

}
