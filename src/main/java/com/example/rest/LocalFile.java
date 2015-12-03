package com.example.rest;

import org.apache.commons.io.FileUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.zip.CRC32;

@XmlRootElement(name = "file")
public class LocalFile {
    public long id;
    public String name;

    private String path;

    public LocalFile() {
    }

    public LocalFile(final File file) {
        this(getCRC(file), file.getAbsolutePath(), file.getName());
    }

    public LocalFile(final long id, final String path, final String name) {
        this.id = id;
        this.path = path;
        this.name = name;
    }

    public static long getCRC(final File file) {
        try  {
            return FileUtils.checksumCRC32(file);
        } catch (final IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(id);
        sb.append(" | ");
        sb.append(name);
        sb.append(" | ");
        sb.append(path);
        return sb.toString();
    }

    public long getId() {
        return id;
    }

    protected String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}