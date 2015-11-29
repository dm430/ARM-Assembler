import lexer.Lexer;
import lexer.TokenStream;
import lexer.exceptions.UnknownSymbolException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by devin on 11/25/15.
 */
public class Driver {
    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer(new File("/home/devin/test.txt"));
            TokenStream tokenStream = lexer.tokenize();

            while (tokenStream.hasNext()) {
                System.out.println(tokenStream.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnknownSymbolException e) {
            e.printStackTrace();
        }
    }
}
