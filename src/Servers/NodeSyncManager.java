package Servers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Entities.File;

public class NodeSyncManager {
    private Map<String,File> syncedFiles;

    public NodeSyncManager(){
        syncedFiles = new HashMap<>();
    }
    
    public void feedFiles(List<File> files){
        for (File file: files){
            if(syncedFiles.containsKey(file.getPath())){
                handle(file);
            }
            syncedFiles.put(file.getPath(),file);
        }
    }

    public List<File> getSyncedFilesList(){
        List<File> files = new ArrayList<>();
        for (File file: this.syncedFiles.values()){
            files.add(file);
        }
        return files;
    }

    private void handle(File file){
        File current = syncedFiles.get(file.getPath());
        if (LocalDateTime.parse(file.getUpdatedAt()).isAfter(LocalDateTime.parse(current.getUpdatedAt())) ){
            syncedFiles.put(file.getPath(), file);
        }
    }
}
