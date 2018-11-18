package com.jkojote.library.files;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

public class StandardFileInstance implements FileInstance {

    private static final FileReader FILE_READER = new SimpleFileReader();

    public static final StandardFileInstance EMPTY = new StandardFileInstance();

    private File path;

    private byte[] file;

    private StandardFileInstance() {
        file = new byte[1];
    }

    public StandardFileInstance(File file) {
        if (file == null)
            throw new IllegalArgumentException("file cannot be null");
        if (!file.exists())
            throw new FileDoesntExistException(file);
        this.path = file;
    }

    public StandardFileInstance(String path) {
        this(new File(path));
    }

    @Override
    public byte[] asBytes() {
        try {
            if (file == null) {
                file = FILE_READER.readFile(path);
            }
            return Arrays.copyOf(file, file.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] asBytes(int length) {
        try {
            if (file == null) {
                file = FILE_READER.readFile(path);
            }
            return Arrays.copyOf(file, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int length() {
        try {
            if (file == null) {
                file = FILE_READER.readFile(path);
            }
            return file.length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Blob asBlob() {
        try {
            if (file == null) {
                file = FILE_READER.readFile(path);
            }
            return new SerialBlob(file);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
