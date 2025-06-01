package Servers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Entities.File;
import Entities.User;

public interface CoordinatorI extends Remote {
 

    public String login(String username, String password) throws RemoteException;
    
    
    public List<User> listUsers(String token) throws RemoteException;

    public void registerUser(String token, String username, String password, String role) throws RemoteException;

    public List<File> listFiles() throws RemoteException;

       public List<File> listFilesByDepartment(String deparment) throws RemoteException;
    
    public void createFile(String token,String name,String department, String content) throws RemoteException;

    public String getFileContent(String token, int index) throws RemoteException;


    public void setFileContent(String token, int index,String content) throws RemoteException;

    
    
}
