package com.jkojote.library.files;

import java.sql.Blob;

public interface FileInstance {

    byte[] asBytes();

    byte[] asBytes(int length);

    int length();

    Blob asBlob();

}
