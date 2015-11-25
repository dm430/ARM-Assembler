package lexer;

import java.io.*;

/**
 * Created by devin on 11/25/15.
 */
public class InputReader {
    private PushbackReader fileReader;

    public InputReader(File file) throws FileNotFoundException {
        fileReader  = new PushbackReader(new FileReader(file));
    }

    public char peek() throws IOException {
        char charToPeekAt = (char) fileReader.read();
        fileReader.unread(charToPeekAt);

        return charToPeekAt;
    }

    public char read() throws IOException {
        return (char) fileReader.read();
    }
}
