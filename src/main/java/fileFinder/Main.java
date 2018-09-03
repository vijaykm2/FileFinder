package fileFinder;
public class Main {
    public static void main(String[] args){
        Long startTime = System.currentTimeMillis();
        FileFinder dirLister = new FileFinder();
        System.out.println("args length: "+args.length+ "\n Args are: ");
        String dirName = "";
        String excluded = "";
        int threadCount = -1;
        boolean countLines = false;
        boolean searchForFile = false;
        boolean useMultipleThreads = false;
        String fileName = null;
        for(String arg: args){
            System.out.println(arg);
            if (arg.startsWith("--location") || arg.startsWith("-l")){
                dirName = arg.split("=")[1];
                System.out.println("dirName = "+dirName);
            }
            if (arg.startsWith("--exclude") || arg.startsWith("-e")){
                excluded = arg.split("=")[1];
                System.out.println("excluded = "+excluded);
            }
            if (arg.startsWith("--useMultipleThreads") || arg.startsWith("-m")) {
                useMultipleThreads = true;
            }
            if(useMultipleThreads && (arg.startsWith("--threadCount") ||arg.startsWith("-tc"))){
                threadCount = Integer.parseInt(arg.split("=")[1]);
                if(threadCount == 1){
                    useMultipleThreads = false;
                }
            }
            if(arg.startsWith("--countLines") || arg.startsWith("-c")){
                countLines = true;
            }
            if(arg.startsWith("--searchForFile") || arg.startsWith("-sf")){
                searchForFile = true;
            }

            if(arg.startsWith("--name")){
                fileName = arg.split("=")[1];
                System.out.println("fileName = "+fileName);
            }


        }
        if(threadCount > 1 ) {
            if(countLines){
                dirLister.countLines(dirName, excluded, useMultipleThreads, threadCount);
            }
            if(searchForFile && fileName != null){
                dirLister.searchForFile(dirName, fileName, excluded, useMultipleThreads, threadCount);
            }
        } else {
            if(countLines){
                dirLister.countLines(dirName, excluded, useMultipleThreads);
            }

            if(searchForFile && fileName != null){
                dirLister.searchForFile(dirName, fileName, excluded, useMultipleThreads);
            }
        }
        System.out.println("Number of lines: "+ FileFinder.lineCount);
        Long endTime = System.currentTimeMillis();
        System.out.println("This program took : "+ (endTime - startTime)+ " milliseconds!!");
    }
}
