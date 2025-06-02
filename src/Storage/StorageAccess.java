package Storage;


import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
        // Files.write(path,data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         try (FileChannel channel = FileChannel.open(path, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND);
             FileLock lock = channel.lock()) {
            
            channel.write(ByteBuffer.wrap(data));
            channel.force(true); // Ensure data is flushed to disk
        }
    }

    public void write(byte[] data) throws Exception{
        Files.createDirectories(path.getParent());
        // Files.write(path,data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         try (FileChannel channel = FileChannel.open(path, 
                StandardOpenOption.CREATE, 
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
             FileLock lock = channel.lock()) {
            
            channel.write(ByteBuffer.wrap(data));
            channel.force(true); // Ensure data is flushed to disk
        }
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
