package Servers;

import java.util.List;
import java.util.Map;

import Entities.File;

public class NodeSyncManager {
    private Map<String,File> syncedFiles;

    public NodeSyncManager(){
    }
    
    public void feedFiles(List<File> files){
        for (File file: files){
            if(syncedFiles.containsKey(file.getPath())){
                handle(file);
            }
            syncedFiles.put(file.getPath(),file);
        }
    }

    private void handle(File file){
        
    }
}
