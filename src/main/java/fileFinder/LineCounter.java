package fileFinder;

import java.io.*;
import java.util.stream.Stream;

public final class LineCounter {
    private BufferedReader bufferedReader;
    private File file;
    public LineCounter(File file) throws FileNotFoundException {
        this.file = file;
        bufferedReader = new BufferedReader(new FileReader(file));
    }
    public Long countLines() throws IOException {
        String name = this.file.getName();
        // System.out.println("Counting lines for file: " + name);
        Stream stream = bufferedReader.lines();
        Long noOfLines= stream.count();
        FileFinder.lineCount.addAndGet(noOfLines);

        bufferedReader.close();
        // System.out.println("Counted lines for file: " + name);
        return noOfLines;
    }
}
