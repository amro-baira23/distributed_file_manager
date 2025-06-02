package Servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import Entities.File;
import Repositories.FileRepository;
import Storage.StorageAccess;


public class Node implements NodeI{
    int port;
    String name;
    FileRepository fileRepository;

    public Node(int port){
        this.port = port;
        this.name = "node_" + port;
        fileRepository = new FileRepository(new StorageAccess(this.getNodePath() + "files.bin"));
        setSchedule();
    }

    private String getNodePath(){
        return "src/Data Sources/" + name + "/";
    }

    public List<File> listFiles(){
        return fileRepository.getAll();
    }
    public void createFile(String name,String department, String content){
        fileRepository.create(name, department, content);
    }

    private void setSchedule(){
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        
        if (midnight.before(Calendar.getInstance())) {
            midnight.add(Calendar.DATE, 1);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("lkdj");
            }
        }, midnight.getTime(), TimeUnit.HOURS.toMillis(24)); 
    }
    public String getFileContent(int index){
        return fileRepository.getFileContent(index);
    }

    public void setFileContent(int index, String content){
        fileRepository.setFileContent(1,content);
    }

     public void deleteFile(int index){
        fileRepository.delete(index);;
    }

    
    public  List<File> listFilesByDepartment(String deparment){
        return fileRepository.getAllByDepartment(deparment);
    }

    public void syncNodeData(NodeSyncManager syncManager){
        List<File> files = syncManager.getSyncedFilesList();
        try{
            fileRepository.updateFilesBin(files);
            for (int i=0;i<files.size();i++){
                setFileContent(i, files.get(i).getContent());;
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public List<String> getSyncingLines(NodeSyncManager syncManager){
        syncManager.feedFiles(fileRepository.getAllWithContent());
        List<File> files = syncManager.getSyncedFilesList();
        List<String> data = new ArrayList<>();
        for (File file: files){
            data.add(file.toStringWithContent());
        }
        return data;
    }


    public List<String> exchangeDataWithChannels(List<String> data){
        try{
            InetAddress group = InetAddress.getByName("239.0.0.1");
            int port = 1234;
            Scanner sc = new Scanner(System.in);
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(group);

            ReadThread rt = new ReadThread(socket, group, port, name);
            Thread t = new Thread(rt);
            t.start();

            System.out.println("Start sending messages...\n");
            System.out.println(data.size());
            for(String row: data){
                String message;
                message = row;
                message = name + ":" + message;
                byte[] buffer = message.getBytes();
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
                socket.send(datagram);
            }
            t.join();
            socket.leaveGroup(group);
            socket.close();
            return rt.geList();
            
        } catch(Exception e){
            System.out.println("problem");
            System.out.println(e);
            return new ArrayList<>();
        }
    }
    
    public void sync(){
        NodeSyncManager syncManager = new NodeSyncManager();
        List<String> data = getSyncingLines(syncManager);
        data.add("end");
        List<String> received = exchangeDataWithChannels(data);
        System.out.println(received.size());
        for(String line: received){
            System.out.println(line);
        }
        syncManager.feedFiles(fileRepository.stringsIntoFiles(received));
        syncNodeData(syncManager);
    }
    public void isAlive(){
        
    }
    public static void main(String[] args){
        int port;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter port number:");
        port = sc.nextInt();
        sc.close();
        Node node = new Node(port);
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            NodeI nodeStub = (NodeI) UnicastRemoteObject.exportObject(node, 0);
            registry.rebind("node",nodeStub);
            System.out.println("Node is running");
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
}

}

class ReadThread implements Runnable {

    private MulticastSocket socket;
    private InetAddress group;
    private int port;   
    private int nodes_connected = 2;
    private String node_name;
    private List<String> lines;


    ReadThread(MulticastSocket socket, InetAddress group, int port, String node_name) {
        this.socket = socket;
        this.group = group;
        this.port = port;
        this.node_name = node_name;
        this.lines = new ArrayList<>();
    }
    private String getMessageContent(String message){
        String[] list = message.split(":",2); 
        return list[1];
    }
    public List<String> geList(){
        return lines;
    }
    @Override
    public void run() {
        while (nodes_connected>0) {
            byte[] buffer = new byte[1000];
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
            String message;
            try {
                socket.receive(datagram);
                message = new String(buffer, 0, datagram.getLength(), "UTF-8");
                System.out.println(message);
                String content = getMessageContent(message);
                if (!message.startsWith(node_name)) {
                    if (content.startsWith("end")){
                        lines.add(content);
                        nodes_connected--;
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
                System.out.println("Socket closed!");
            }
        }
    }
}
