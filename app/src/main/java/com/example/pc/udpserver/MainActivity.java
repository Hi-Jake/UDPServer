package com.example.pc.udpserver;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button start;
    TextView ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button)findViewById(R.id.start);
        ip = (TextView)findViewById(R.id.ip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initialNetwork();
        } else {
            displayIPAddress();
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Server server = new Server();
                    server.start();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initialNetwork() {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        NetworkRequest request = builder.build();
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                cm.unregisterNetworkCallback(this);

                LinkProperties lp = cm.getLinkProperties(network);
                List<LinkAddress> link = lp.getLinkAddresses();

                for (LinkAddress i : link) {
                    InetAddress address = i.getAddress();
                    if (address.isLoopbackAddress()) continue;

                    if (address instanceof Inet4Address) {
                        ip.setText(address.getHostAddress());
//                        Log.d("ServerIP", address.getHostAddress());
                    }
                }
            }
        };

        cm.registerNetworkCallback(request, callback);
        cm.requestNetwork(request, callback);
    }

    public void displayIPAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                if (ni.isLoopback() || ni.isPointToPoint()) continue;
                if (!ni.isUp()) continue;

                List<InterfaceAddress> list = ni.getInterfaceAddresses();
                InetAddress address;
                for (InterfaceAddress ia : list) {
                    address = ia.getAddress();
                    if (address instanceof Inet4Address) {
                        ip.setText(address.getHostAddress());
//                        Log.d("ServerIP", address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("Display","예외가 발생하였습니다11." + e);
        }
    }
}
