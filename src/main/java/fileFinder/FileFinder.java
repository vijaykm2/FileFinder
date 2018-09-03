package fileFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class FileFinder {

    private HashSet<String> dirs = new HashSet<String>();
    public static Set<String> files;
    public static ExecutorService executorService;
    public static final AtomicLong lineCount = new AtomicLong(0l);
    public static final Timer timer = new Timer("line count content", true);
    /*static {
        lineCountTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(" Current linecount: "+ lineCount);
                System.out.println(" Current file Count: "+ files.size());
            }
        }, 5000l, 5000l);
    }
    public static final Timer searchFileTimer = new Timer("Search file timer", true);
    static {
        lineCountTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(" Current file Count: "+ files.size());
                for(String file: files){
                    System.out.println(file);
                }
            }
        }, 5000l, 5000l);
    }*/

    private static void shutdownExecutorService(boolean useMultipleThreads) {
        if(useMultipleThreads && !executorService.isShutdown()){
            try {
                System.out.println("executor service shutdown called!!");
                Long shutDownStartTime = System.currentTimeMillis();
                executorService.shutdown();
                System.out.println("executor service awaitTermination called!!");
                executorService.awaitTermination(10000, TimeUnit.SECONDS);
                Long shutDownEndTime = System.currentTimeMillis();
                System.out.println("Shut down took "+ (shutDownEndTime - shutDownStartTime) + " milliseconds");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void initialize(boolean useMultipleThreads, int threadCount) {
        if(useMultipleThreads){
            int noOfThreads = threadCount;
            System.out.println("No of threads : "+noOfThreads);
            executorService = Executors.newFixedThreadPool(noOfThreads);
            files = ConcurrentHashMap.newKeySet();
        }else {
            files = new HashSet<>();
        }
    }

    private Consumer<File> computeLinesForFile = (File file) -> {
        String absPath = file.getAbsolutePath();
        FileFinder.files.add(absPath);
        boolean isTextFile =!(absPath.endsWith(".jar") ||absPath.endsWith(".zip") || absPath.endsWith(".gz") ||
                absPath.endsWith(".jpeg") ||  absPath.endsWith(".jpg") || absPath.endsWith(".png") ||
                absPath.endsWith(".vmdk") || absPath.endsWith(".app") || absPath.endsWith(".db") ||
                absPath.endsWith(".exe") || absPath.endsWith(".iso") ||absPath.endsWith(".mov") ||
                absPath.endsWith(".avi") || absPath.endsWith(".mp3") || absPath.endsWith(".mp4") ||
                absPath.endsWith(".itc") || absPath.endsWith(".pdf") || absPath.endsWith(".doc") ||
                absPath.endsWith(".docx"));
        if(isTextFile){
            LineCounter lineCounter = null;
            try {
                lineCounter = new LineCounter(file);
                try {
                    lineCounter.countLines();
                } catch (IOException e) {
                    System.out.println("Exception caused by file : "+absPath);
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
    private BiConsumer<File, String> getFileByName = (File file, String fileName) -> {
        if(file.getName().contains(fileName)){
            System.out.println("Found file: "+ file.getAbsolutePath());
            files.add(file.getAbsolutePath());
        }
    };

    public void countLines(String dirName, String excludedDirs, boolean useMultipleThreads) {
        countLines(dirName, excludedDirs, useMultipleThreads, Runtime.getRuntime().availableProcessors());
    }
    public void countLines(String dirName, String excludedDirs, boolean useMultipleThreads, int threadCount) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(" Current linecount: "+ lineCount);
                System.out.println(" Current file Count: "+ files.size());
            }
        }, 5000l, 5000l);
        initialize(useMultipleThreads, threadCount);
        try {

            File file = new File(dirName);
            if(file.isDirectory()){
                //this.createFileSet(file, excludedDirs, useMultipleThreads);
                DirIterator iterator = new DirIterator(file, excludedDirs, useMultipleThreads);
                iterator.iterate(this.computeLinesForFile);
            } else if( file.isFile()){
                LineCounter counter = new LineCounter(file);
                counter.countLines();
            }
            // Thread.sleep(30000);
        }catch (Exception e){
            e.printStackTrace();
        }

        shutdownExecutorService(useMultipleThreads);
        timer.cancel();
        System.out.println("Number of files: "+ files.size());
    }

    public void searchForFile(String currentDirName, String fileName, String excludedDirs, boolean useMultipleThreads, int threadCount){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(" Current file Count: "+ files.size());
                for(String file: files){
                    System.out.println(file);
                }
            }
        }, 5000l, 5000l);
        initialize(useMultipleThreads, threadCount);
        File file = new File(currentDirName);
        if(file.isDirectory()){
            this.getFileByName.accept(file, fileName);
            /*if(useMultipleThreads){
                executorService.submit(() -> {
                    DirIterator iterator = new DirIterator(file, excludedDirs, useMultipleThreads);
                    iterator.searchForFile(this.getFileByName, fileName);
                });
            } else {
                DirIterator iterator = new DirIterator(file, excludedDirs, useMultipleThreads);
                iterator.searchForFile(this.getFileByName, fileName);
            }*/

            DirIterator iterator = new DirIterator(file, excludedDirs, useMultipleThreads);
            iterator.searchForFile(this.getFileByName, fileName);
        }
        shutdownExecutorService(useMultipleThreads);
        timer.cancel();
        System.out.println("Number of files: "+ files.size());/*
        for (String name: files){
            System.out.println(name);
        }*/
    }

    public void searchForFile(String dirName, String fileName,  String excludedDirs, boolean useMultipleThreads){
        this.searchForFile(dirName,fileName, excludedDirs, useMultipleThreads, Runtime.getRuntime().availableProcessors());
    }
}
