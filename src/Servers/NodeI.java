package Servers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Entities.File;

public interface NodeI extends Remote {

    public List<File> listFiles() throws RemoteException;

    public void createFile(String name,String department, String content) throws RemoteException;


    public String getFileContent(int index) throws RemoteException;

    public void setFileContent(int index, String content) throws RemoteException;

    public  List<File> listFilesByDepartment(String deparment) throws RemoteException;

}