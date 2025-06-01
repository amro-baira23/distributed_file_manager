package Clients;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Servers.Node;

public class UserClient {
    public static void main(String[] args) {
        int port;
        port = 5001;
        Node node = new Node(port);
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            Node nodeStub = (Node) UnicastRemoteObject.exportObject(node, 0);
            registry.rebind("node",nodeStub);
            System.out.println("Node is running");
        } catch(Exception e){
            System.out.println("exception");
            System.out.println(e);
        }
    }   
}
