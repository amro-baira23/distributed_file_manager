package Storage;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class StorageAccess {

    private final Path path ;

    public StorageAccess(String path){
        this.path = Paths.get(path);
    }

    public String getPath(){
        return path.getParent().toString();
    }


    public void store(byte[] data) throws Exception{
        Files.createDirectories(path.getParent());
        Files.write(path,data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void write(byte[] data) throws Exception{
        Files.createDirectories(path.getParent());
        Files.write(path,data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public String getFileContent(){
        try{
            return Files.readString(path);

        } catch (Exception e){
            System.out.println(e.getMessage());
            return "";
        }
    }
    public List<String> getAll(){
        try {
            var lines = Files.readAllLines(path);
            List<String> data = new ArrayList<>();
            for(var line : lines){
                data.add(new String(line.getBytes()));
            }
            return data;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}
