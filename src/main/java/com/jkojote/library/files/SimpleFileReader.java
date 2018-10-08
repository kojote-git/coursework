package com.jkojote.library.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class SimpleFileReader implements FileReader {

    public byte[] readFile(File file) throws IOException {
        return readFile(file, file.length());
    }

    public byte[] readFile(File file, long length) throws IOException {
        if (length <= 0 || length > file.length())
            throw new IllegalArgumentException();
        return readFile(file, 4096, length);
    }


    private byte[] readFile(File file, final int bufferSize, final long length)
    throws IOException {
        if (!file.exists())
            throw new FileDoesntExistException(file);
        byte[] buffer = new byte[bufferSize];
        long totalRead = 0;
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             FileInputStream fout = new FileInputStream(file)) {
            int read;
            while (totalRead <= length && (read = fout.read(buffer)) > 0) {
                bout.write(buffer, 0, read);
                totalRead += read;
            }
            return bout.toByteArray();
        }
    }
}
