package Repositories;

import java.util.ArrayList;
import java.util.List;

import Entities.File;
import Storage.StorageAccess;

public class FileRepository {
      private StorageAccess storageAccess;

    public FileRepository(StorageAccess storageAccess){
        this.storageAccess = storageAccess;
    }

    public void create(String name, String department, String content){
        File file = new File(name,department);
        try{
            storageAccess.store(file.bytes());
            StorageAccess documentAccess = new StorageAccess(storageAccess.getPath() + "/" + file.getPath());
            documentAccess.write(content.getBytes());
        } catch (Exception e){
            System.out.println("File Repository Exception: " + e.getMessage());
        }
    }

    public String getFileContent(int index){
        File file = this.getAll().get(index);
        StorageAccess documentAccess = new StorageAccess(storageAccess.getPath() + "/" + file.getPath());
        return documentAccess.getFileContent();
    }

     public void setFileContent(int index, String newContent){
        List<File> files = this.getAll();
        File file = files.get(index);
        StorageAccess documentAccess = new StorageAccess(storageAccess.getPath() + "/" + file.getPath());
        try {
            documentAccess.write(newContent.getBytes());
            file.update();
            updateFilesBin(files);
        } catch (Exception e){
            System.out.println("File Repository Exception: " + e.getMessage());
        }
    }

    public void updateFilesBin(List<File> files) throws Exception{
        StringBuilder newFilesBinContent = new StringBuilder(); 
        for (File file : files){
            newFilesBinContent.append(file.toString());
        }
        byte[] data = newFilesBinContent.toString().getBytes();
        storageAccess.write(data);
    }

    public void delete(int index){
        List<File> files = this.getAll();
        File file = files.get(index);
        try {
            file.delete();
            updateFilesBin(files);
        } catch (Exception e){
            System.out.println("File Repository Exception: " + e.getMessage());
        }
    }

    public List<File> getAll(){
        List<String> rows = storageAccess.getAll();
        List<File> files = new ArrayList<>();
        for (String row: rows){
            try{
                files.add(new File(row));
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        return files;
    }
    public List<File> getAllByDepartment(String department){
        List<File> files = new ArrayList<>();
        for (File file : this.getAll()){
            if (file.getDepartment().equals(department)){
                files.add(file);
            }
        }
        return files;
    }

    
    public File findByName(String name, String department){
        List<File> files = getAll();
        for (File file : files){
            if (file.getName().equals(name) && file.getDepartment().equals(department)){
                return file;
            }
        }
        return null;
    }
}
