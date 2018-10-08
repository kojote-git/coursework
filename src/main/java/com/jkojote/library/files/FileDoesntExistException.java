package com.jkojote.library.files;

import java.io.File;
import java.io.IOException;

public class FileDoesntExistException extends RuntimeException {

    private File file;

    public FileDoesntExistException(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
