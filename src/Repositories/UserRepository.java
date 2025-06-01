package Repositories;

import java.util.ArrayList;
import java.util.List;

import Entities.User;
import Storage.StorageAccess;

public class UserRepository {

    private StorageAccess storageAccess;

    public UserRepository(StorageAccess storageAccess){
        this.storageAccess = storageAccess;
    }

    public void create(String username, String password, String role){
        User user = new User(username, password, role);
        try{
            storageAccess.store(user.bytes());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public User get(String username){
        List<User> users = getAll();
        for (User user: users){
            if (user.getUsername().equals(username))
            return user;
        }
        return null;
    }
    public List<User> getAll(){
        List<String> rows = storageAccess.getAll();
        List<User> users = new ArrayList<>();
        for (String row: rows){
            try{
                users.add(new User(row));
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return users;
    }
}
