package Entities;


public class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String data) throws Exception{
        String[] list = data.split(",", 3);
        if (list.length != 3){
            throw new Exception("bad String entered as User row");
        }
        this.username = list[0];
        this.password = list[1];
        this.role = list[2]; 
    }
    public String getUsername(){
        return this.username;
    }
    public String getRole(){
        return this.role;
    }
    public String toString(){
        return ("User: ["+username+", "+role+"]\n");

    }
    public byte[] bytes(){
        return (username+","+password+","+role+"\n").getBytes();
    }

 
} 
