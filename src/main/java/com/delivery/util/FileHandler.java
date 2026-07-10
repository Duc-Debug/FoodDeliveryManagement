package com.delivery.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.delivery.model.MenuItem;

/**
 * Generic file utility for text/csv persistence helpers.
 */
public class FileHandler {
    private FileHandler(){

    }
    public static <T> void writeToTextFile(Path path, List<T>data, Function<T, String> serializer)throws IOException{
        Path parent = path.getParent();
        if(parent != null && Files.notExists(parent)){
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();
        for(T item : data){
            lines.add(serializer.apply(item));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static <T> List<T> readFromTextFile(Path path,Function<String, T> parser) throws IOException{
        List<String> lines = Files.readAllLines(path,StandardCharsets.UTF_8);
        List<T> result = new ArrayList<>();
        for(String line : lines){
            result.add(parser.apply(line));
        }
        return result;
    }

    public static <T> void writeToCsv(Path path,List<T> data, Function<T, String> csvMapper) throws IOException{
        writeToTextFile(path, data, csvMapper);
    }
    public static <T> List<T> readToCsv(Path path, Function<String, T>csvParser) throws IOException{
      return  readFromTextFile(path, csvParser);
    }
    public static void writeToCsv(Path filePath, List<MenuItem> all, Object csvMapper) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeToCsv'");
    }
}
