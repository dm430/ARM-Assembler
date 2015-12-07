import lexer.Lexer;
import lexer.TokenStream;
import lexer.exceptions.UnknownSymbolException;
import parser.Parser;
import parser.exceptions.EncodingException;
import parser.exceptions.SyntaxErrorException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by devin on 11/25/15.
 */
public class Driver {
    public static void main(String[] args) {
        try {
            Parser parser = new Parser();
            Lexer lexer = new Lexer(new File("/home/devin/test.txt"));
            TokenStream tokenStream = lexer.tokenize();
            byte[] program = parser.parse(tokenStream);
            writeFile("/home/devin/RPI/test.img", program);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownSymbolException e) {
            e.printStackTrace();
        } catch (SyntaxErrorException e) {
            e.printStackTrace();
        } catch (EncodingException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(String fileName, byte[] program) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(program);
            fos.flush();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
