package fileFinder;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
public final class DirIterator {
    private File dir;
    private boolean useMultipleThreads;
    private String excludedDirs;
    public DirIterator(File dir, String excludedDirs, boolean useMultipleThreads){
        this.dir = dir;
        this.useMultipleThreads = useMultipleThreads;
        this.excludedDirs = excludedDirs;
    }
    public void iterate(Consumer consumer){
        File[] files = dir.listFiles();
        for(int l = 0; l<files.length; l++){
            File file = files[l];
            if(file.isFile()){
                if(!this.useMultipleThreads) {
                    consumer.accept(file);
                } else {
                    Runnable task = ()-> {
                        consumer.accept(file);
                    };
                    FileFinder.executorService.submit(task);
                }
            }
            else if(file != null && file.isDirectory() && !(excludedDirs.contains(file.getName()))){
                DirIterator dirIterator = new DirIterator(file, this.excludedDirs, this.useMultipleThreads);
                dirIterator.iterate(consumer);
            }

        }
    }
    public void searchForFile(BiConsumer consumer, String fileName){
        File[] files = dir.listFiles();
        for(int l = 0; l<files.length; l++){
            File file = files[l];
            if(file.isFile()){
                if(!this.useMultipleThreads) {
                    consumer.accept(file, fileName);
                } else {
                    Runnable task = ()-> {
                        consumer.accept(file, fileName);
                    };
                    FileFinder.executorService.submit(task);
                }
            }
            else if(file != null && file.isDirectory() && !(excludedDirs.contains(file.getName()))){
                DirIterator dirIterator = new DirIterator(file, this.excludedDirs, this.useMultipleThreads);
                dirIterator.searchForFile(consumer, fileName);
            }

        }
    }
}
