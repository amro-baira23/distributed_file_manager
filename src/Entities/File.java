package Entities;

import java.time.LocalDateTime;

public class File {
    private String name;
    private String department;
    private String created_at;
    private String updated_at;
    private String deleted_at;
    private String content;
    
    public File(String name, String department){
        this.name = name;
        this.department = department;
        this.created_at = LocalDateTime.now().toString(); 
        this.updated_at = LocalDateTime.MIN.toString(); 
        this.deleted_at = "null";
    }

    public String getPath(){
        return department + "/" + name;
    }

    public File(String data){
        String[] list = data.split(",", 5);
      
        this.name = list[0];
        this.department = list[1];
        this.created_at = list[2]; 
        this.updated_at = list[3]; 
        this.deleted_at = list[4];
    }
    public String getName(){
        return name;
    }
    public String getDepartment(){
        return department;
    }
    
    public void update(){
        this.updated_at = LocalDateTime.now().toString();
    }

    public void delete(){
        this.deleted_at = LocalDateTime.now().toString();
    }
    public String toString(){
        return (name+","+department+","+created_at+","+updated_at+","+deleted_at+"\n");
    }

    public byte[] bytes(){
        return (name+","+department+","+created_at+","+updated_at+","+deleted_at+"\n").getBytes();
    }
}
