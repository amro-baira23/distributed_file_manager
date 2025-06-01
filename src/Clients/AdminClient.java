package Clients;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

import Entities.File;
import Entities.User;
import Servers.Coordinator;
import Servers.CoordinatorI;

public class AdminClient {
    private CoordinatorI coordinator;
    private String session;

    public AdminClient(){
        this.coordinator = new Coordinator();
            
    }

    public List<String> getCommands(){
        return List.of(
                "login with your username and password",
                "create new user",
                "list users",
                "list all files",
                "create new file",
                "see files in a specific department",
                "get file content",
                "edit file",
                "delete file",
                "logout"
            ); 
    }
    public CoordinatorI getCoordinator(){
        try {
            Registry registry = LocateRegistry.getRegistry(5000);
            CoordinatorI coordinator = (CoordinatorI) registry.lookup("coordinator");
            return coordinator;
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public void login(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = sc.next();
        System.out.println("Enter password:");
        String password = sc.next();
        try{
            this.session = coordinator.login(username, password);
        }catch (Exception e){
            System.out.println(e);
            return;
        }
        System.out.println("you've been logged in successfully");
    }

    public void createUser(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = sc.nextLine();
        System.out.println("Enter password:");
        String password = sc.next();
        System.out.println("Enter role:");
        String role = sc.next();
        try{
            coordinator.registerUser(this.session,username,password,role);

        } catch(Exception e){
            System.out.println(e);
        }
        System.out.println("User created successfully");
    }

    public void listUsers(){
        System.out.println("users:");
        try{
            for (User user: coordinator.listUsers(session)){
                System.out.println(user);
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void listFiles(){
        System.out.println("files:");
        try {
            for (File file: this.coordinator.listFiles()){
                System.out.println(file);
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }
    public void createNewFile(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file name");
        String name = sc.next();
        System.out.println("Enter file department");
        String department = sc.next();
        System.out.println("Enter file content");
        String content = sc.next();
        try{
            this.coordinator.createFile(this.session, name, department, content);;
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void listFilesByDepartment(){
        System.out.println("Enter deparment");
        Scanner sc = new Scanner(System.in);
        String department = sc.next();
        System.out.println("files:");
        try {
            for (File file: this.coordinator.listFilesByDepartment(department)){
                System.out.println(file);
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void getFileContent(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file index");
        int index = sc.nextInt();
        try{
            this.coordinator.getFileContent(session, index);
        } catch ( Exception e){
            System.out.println(e);
        }
    }

    public void setFileContent(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter file index");
        int index = sc.nextInt();
        System.out.println("Enter file content");
        String content = sc.next();
        try{
            this.coordinator.setFileContent(session, index,content);
        } catch ( Exception e){
            System.out.println(e);
        }
    }
    public void logout(){
        this.session = null;
    }
    public static void main(String[] args){
        AdminClient client = new AdminClient();
        List<String> commands = client.getCommands();
      
        Scanner sc = new Scanner(System.in);
        while(true){
            for (int i = 1;i<=commands.size();i++){
                System.out.println(i + "- "+commands.get(i-1));
            }        

            int command = sc.nextInt();
            switch (command) {
                case 1:
                    client.login();
                    break;
                case 2:
                    client.createUser();
                    break;
                case 3:
                    client.listUsers();
                    break;
                case 4:
                    client.listFiles();
                    break;
                case 5:
                    client.createNewFile();
                    break;
                case 6:
                    client.listFilesByDepartment();
                    break;
                case 7:
                    client.getFileContent();
                    break;
                case 8:
                    client.setFileContent();
                    break;
                case 9:
                    // client.deleteFile();
                    break;
                case 10:
                    client.logout();
                    break;
                default:
                    break;
            }
        }
      
    }
    
}
