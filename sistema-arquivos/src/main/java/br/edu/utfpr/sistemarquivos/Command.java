package br.edu.utfpr.sistemarquivos;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import br.edu.utfpr.sistemarquivos.FileReader;
import static br.edu.utfpr.sistemarquivos.Application.ROOT;

public enum Command {

    LIST() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("LIST") || commands[0].startsWith("list");
        }

        @Override
        Path execute(Path path) throws IOException {

            // TODO implementar conforme enunciado
            try(DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
                stream.forEach(file ->{
                    System.out.println(file.getFileName());
                });
            }catch(IOException | DirectoryIteratorException x){
                System.err.println(x);
            }
            return path;
        }
    },

    SHOW() {
        private String[] parameters = new String[]{};
        private String file = "";

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            file = commands.length > 1 ? commands[1]: "";

            return commands.length > 0 && commands[0].startsWith("SHOW") || commands[0].startsWith("show");
        }

        @Override
        Path execute(Path path) {

            // TODO implementar conforme enunciado
            String fullFilePath = path.toString() + File.separator + file;

            String fileExtension = "";
                    if(file.contains(".")){
                        fileExtension = file.substring(file.lastIndexOf(".") + 1);
                        if((fileExtension.equals("txt")) || (fileExtension.equals("TXT"))){

                            FileReader f = new FileReader();
                            f.read(path,file);
                        }else
                            throw new UnsupportedOperationException("Extension not supported");
                    }else
                        throw new UnsupportedOperationException("This command should be used with files only");
            return path;
        }
    },
    BACK() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("BACK") || commands[0].startsWith("back");
        }

        @Override
        Path execute(Path path) {

            // TODO implementar conforme enunciado
            File file = new File(path.toString());
            String strParentDirectory = file.getParent();

            if(!Objects.equals(path.toString(), ROOT)){
                path = Paths.get(strParentDirectory);
            }

            return path;
        }
    },
    OPEN() {
        private String[] parameters = new String[]{};
        private String folderName = "";

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            folderName = commands.length > 1 ? commands[1]: "";

            var hasFileOrFolder = commands.length > 1 ? true: false;
            if(!hasFileOrFolder){
                throw new UnsupportedOperationException("Type a directory...");
            }

            return commands.length > 0 && commands[0].startsWith("OPEN") || commands[0].startsWith("open");
        }

        @Override
        Path execute(Path path) {
            // TODO implementar conforme enunciado
            if(!folderName.contains(".")){
                try{
                    String absolutePath = path + File.separator + folderName;
                    path = Paths.get(absolutePath);

                }catch(UnsupportedOperationException e){
                    e.printStackTrace();
                }
            }else
                throw new UnsupportedOperationException("This command should be used with folders only");

            return path;
        }
    },
    DETAIL() {
        private String[] parameters = new String[]{};

        @Override
        void setParameters(String[] parameters) {
            this.parameters = parameters;
        }

        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("DETAIL") || commands[0].startsWith("detail");
        }

        @Override
        Path execute(Path path) {

            // TODO implementar conforme enunciado
            try{
                BasicFileAttributeView attributeView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
                BasicFileAttributes attribute = attributeView.readAttributes();
                System.out.println("Size: " + attribute.size());
                System.out.println("Created on: " + attribute.creationTime());
                System.out.println("Last access:" + attribute.lastAccessTime());

            }catch(IOException e){
                System.err.println(e);
            }
            return path;
        }
    },
    EXIT() {
        @Override
        boolean accept(String command) {
            final var commands = command.split(" ");
            return commands.length > 0 && commands[0].startsWith("EXIT") || commands[0].startsWith("exit");
        }

        @Override
        Path execute(Path path) {
            System.out.print("Saindo...");
            return path;
        }

        @Override
        boolean shouldStop() {
            return true;
        }
    };

    abstract Path execute(Path path) throws IOException;

    abstract boolean accept(String command);

    void setParameters(String[] parameters) {
    }

    boolean shouldStop() {
        return false;
    }

    public static Command parseCommand(String commandToParse) {

        if (commandToParse.isBlank()) {
            throw new UnsupportedOperationException("Type something...");
        }

        final var possibleCommands = values();

        for (Command possibleCommand : possibleCommands) {
            if (possibleCommand.accept(commandToParse)) {
                possibleCommand.setParameters(commandToParse.split(" "));
                return possibleCommand;
            }
        }
        throw new UnsupportedOperationException("Can't parse command [%s]".formatted(commandToParse));
    }
}
