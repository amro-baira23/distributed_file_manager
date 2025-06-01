package Servers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

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




    public void sync(){
           try{
            InetAddress group = InetAddress.getByName("239.0.0.1");
            int port = 1234;
            Scanner sc = new Scanner(System.in);
            
            MulticastSocket socket = new MulticastSocket(port);

            socket.joinGroup(group);//socket.joinGroup("239.0.0.1");
            
            // Thread t = new Thread(new ReadThread(socket, group, port));
            t.start();

            System.out.println("Start typing messages...\n");
            while (true) {
                String message;
                message = sc.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    socket.leaveGroup(group);
                    socket.close();
                    break;
                }
                message = this.name + ":" + message;
                byte[] buffer = message.getBytes();
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length, group, port);
                socket.send(datagram);
            }
           } catch(Exception e){
                System.out.println(e);
           }
    }

    public static void main(String[] args){
        Node node = new Node(5000);
        node.sync();
        // int port;
        // Scanner sc = new Scanner(System.in);
        // System.out.println("Enter port number:");
        // port = sc.nextInt();
        // sc.close();
        // Node node = new Node(port);
        // try {
        //     Registry registry = LocateRegistry.createRegistry(port);
        //     NodeI nodeStub = (NodeI) UnicastRemoteObject.exportObject(node, 0);
        //     registry.rebind("node",nodeStub);
        //     System.out.println("Node is running");
        // } catch(Exception e){
        //     System.out.println(e.getMessage());
        // }
}

}