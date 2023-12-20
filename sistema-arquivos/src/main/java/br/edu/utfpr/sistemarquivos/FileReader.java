package br.edu.utfpr.sistemarquivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileReader {

    public void read(Path path,String fileName) {
        // TODO implementar a leitura dos arquivos do PATH aqui

        try{

            final InputStream input = new FileInputStream(path.toFile() + File.separator + fileName);

            int content;

            while((content = input.read()) != -1){
                System.out.print((char) content);
            }

        }catch(IOException exception){
            exception.printStackTrace();
        }
    }
}
