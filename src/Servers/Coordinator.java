package Servers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.StringContent;

import Entities.File;
import Entities.User;
import Repositories.UserRepository;
import Storage.StorageAccess;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Coordinator implements CoordinatorI{
    UserRepository userRepository;
    List<NodeI> nodes;
    Map<String,User> sessions;

    static int node_index = -1;

    public Coordinator() {
        StorageAccess storageAccess = new StorageAccess("src/Data Sources/users.bin");
        userRepository = new UserRepository(storageAccess);
        sessions = new HashMap<>();
        nodes = stubNodes();
    }

     private List<String> getNodesUrls(){
        return List.of(
            "rmi://localhost:5001/node",
            "rmi://localhost:5002/node",
            "rmi://localhost:5003/node"
        );
    }

    private List<NodeI> locateNodes() {
        List<NodeI> nodes = new ArrayList<>();
        for (String url : getNodesUrls()){
            try{
                nodes.add((NodeI) Naming.lookup(url));
            } catch(Exception e){
                System.out.println(e);
            }
        }
        return nodes;
    }
    private List<NodeI> stubNodes() {
        return List.of(
            new Node(5001),
            new Node(5002),
            new Node(5003)
        );
    }
    private NodeI selectNode() throws RemoteException{
        node_index++;
        if (nodes.size()==0){
            throw new RemoteException("no nodes alive");
        }
        NodeI node = nodes.get(node_index%nodes.size());
        try{
            node.isAlive();
        }  catch(Exception e){
            nodes.remove(node_index);
            return selectNode();
        }
        return node;
    }

    public String login(String username, String password) throws RemoteException{
        User user = userRepository.get(username);
        if (user==null){
            throw new RemoteException("couldn't find user's credentials");
        }
        String combined = username + ":" + password;
        String token =  Base64.getEncoder().encodeToString(combined.getBytes());
        sessions.put(token,user);
        return token;
    }

    private boolean authenticate(String token){
        return sessions.containsKey(token);
    }

    private boolean authorize(String token, String role) {
        System.out.println(token);
        return (authenticate(token) && sessions.get(token).getRole().equals(role));
    }

    public List<User> listUsers(String token) throws RemoteException{
        if (!this.authorize(token, "admin")){
            throw new RemoteException("not authorized");
        }
        return userRepository.getAll();
    }

    public void registerUser(String token, String username, String password, String role) throws  RemoteException{
        if (!this.authorize(token, "admin"))
            throw new RemoteException("not authorized action");
        userRepository.create(username, password, role);
    }
  public List<File> listFiles() throws RemoteException{
        NodeI node = selectNode();
        return node.listFiles();
    }
    public List<File> listFilesByDepartment(String deparment) throws RemoteException{
        NodeI node = selectNode();
        return node.listFilesByDepartment(deparment);
    }
    
    public void createFile(String token,String name,String department, String content) throws RemoteException{
        authorize(token, department);
        NodeI node = selectNode();
        node.createFile(name, department, content);
    }


    public String getFileContent(String token, int index) throws RemoteException{

        NodeI node = selectNode();
        return node.getFileContent(index);
    }

    public void setFileContent(String token ,int index,String content)throws RemoteException{
        NodeI node = selectNode();
        node.setFileContent(1,content);
    }



    public static void main(String[] args){
        try {
            Coordinator coordinator = new Coordinator();
            Registry registry = LocateRegistry.createRegistry(5000);
            CoordinatorI coordinatorStub = (CoordinatorI) UnicastRemoteObject.exportObject(coordinator,0);
            registry.rebind("coordinator",coordinatorStub);
            System.out.println("coordinator is running");
        } catch(Exception e){
            System.out.println(e);
        }
    }

  
    
}
