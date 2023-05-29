package com.itheima;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BlueCoveExample {
    public static StreamConnection connection = null;
    private static boolean isConnected = false;

    public static void main(String[] args) {
        try {
            // 搜索附近的蓝牙设备
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            DiscoveryAgent discoveryAgent = localDevice.getDiscoveryAgent();
            RemoteDevice[] remoteDevices = discoveryAgent.retrieveDevices(DiscoveryAgent.CACHED);

            // 打印设备名称
            for (RemoteDevice remoteDevice : remoteDevices) {
                String deviceName = remoteDevice.getFriendlyName(false);
                System.out.println("Device Address: " + remoteDevice.getBluetoothAddress());
                System.out.println("Device Name: " + deviceName);

                if (deviceName.equals("BOHB-WAX9")) {
                    // 连接名为 "apple" 的设备
                    isConnected = connectToDevice(remoteDevice);
                    if (isConnected) {
                        System.out.println("Connected to device: " + deviceName);
                        // 在这里可以进行进一步的操作
                        String filePath = "C:\\Users\\ASUS\\IdeaProjects\\StudyJava\\cqu_projects\\bluetoothTest\\src\\main\\java\\com\\itheima/example.txt";
                        sendFile(filePath);

                        //sendMessages();
                        receiveMessages();
                        //sleep for 100s
                        try {
                            Thread.sleep(100000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Failed to connect to device: " + deviceName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean connectToDevice(RemoteDevice remoteDevice) {
        try {
            // 创建一个连接
            UUID uuid = new UUID("0000110100001000800000805F9B34FB", false); // SPP UUID
            String deviceAddress = remoteDevice.getBluetoothAddress();
            String connectionString = "btspp://" + deviceAddress + ":1;authenticate=false;encrypt=false;master=false";
            connection = (StreamConnection) Connector.open(connectionString);

            // 连接成功
            // 在这里可以进行进一步的操作
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 连接失败
        }
    }

    private static void receiveMessages() {
        if (!isConnected) {
            System.out.println("Not connected to any device.");
            return;
        }

        try {

            // 在新线程中持续读取输入流的数据
            Thread thread = new Thread(() -> {
                try {
                    InputStream inputStream = connection.openInputStream();

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        String message = new String(buffer, 0, bytesRead);
                        System.out.println("Received message: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessages() {
        if (!isConnected) {
            System.out.println("Not connected to any device.");
            return;
        }

        try {
            OutputStream outputStream = connection.openOutputStream();
            String message = "Hello, World!";
            outputStream.write(message.getBytes());
            outputStream.flush();
            System.out.println("Message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void sendFile(String filePath) {
        if (!isConnected) {
            System.out.println("Not connected to any device.");
            return;
        }

        try {
            OutputStream outputStream = connection.openOutputStream();
            FileInputStream fileInputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            outputStream.flush();
            System.out.println("File sent: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
