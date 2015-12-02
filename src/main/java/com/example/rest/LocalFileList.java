package com.example.rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class LocalFileList {
    private static final CopyOnWriteArrayList<LocalFile> cList = new CopyOnWriteArrayList<>();

    static {
        try {
             Files.walk(Paths.get("."))
                    .filter(Files::isRegularFile)
                    .forEach(f -> cList.add(new LocalFile(f.toFile())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalFileList() {
    }

    public static CopyOnWriteArrayList<LocalFile> getInstance() {
        return cList;
    }

    public static void testList() {
        CopyOnWriteArrayList<LocalFile> list = LocalFileList.getInstance();
        list.stream()
                .forEach(i -> System.out.println(i));
        String cString =
                list.stream()
                        .map(c -> c.toString())
                        .collect(Collectors.joining("\n"));
        System.out.println(cString);
    }

    public static void main(String[] args) {
        LocalFileList.testList();
    }

}
