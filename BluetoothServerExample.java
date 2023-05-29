package com.itheima;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BluetoothServerExample {

    public static void main(String[] args) {
        try {
            // 创建一个服务器连接
            UUID uuid = new UUID("0000110100001000800000805F9B34FB", false); // SPP UUID
            String connectionString = "btspp://localhost:" + uuid + ";name=BluetoothServer";
            StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector.open(connectionString);

            // 获取本地蓝牙适配器
            LocalDevice localDevice = LocalDevice.getLocalDevice();

            // 设置设备可被其他设备检测到
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);

            System.out.println("Waiting for client connection...");

            // 等待客户端连接
            StreamConnection connection = notifier.acceptAndOpen();

            System.out.println("Client connected!");

            // 打开输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.openInputStream()));

            // 持续接收消息并打印
            while (true) {
                String message = reader.readLine();
                if (message != null) {
                    System.out.println("Received message: " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
