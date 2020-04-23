package com.cscie599.gfn.service;

import java.io.IOException;

/**
 * An interface to support splitting of files and compressing them to reduce storage
 *
 * @author PulkitBhanot
 */
public interface FileService {

    void splitAndZipFiles() throws IOException;
}
