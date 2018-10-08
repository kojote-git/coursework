package com.jkojote.library.files;

import java.io.File;
import java.io.IOException;

public interface FileReader {

    byte[] readFile(File file) throws IOException;

    byte[] readFile(File file, long length) throws IOException;
}
