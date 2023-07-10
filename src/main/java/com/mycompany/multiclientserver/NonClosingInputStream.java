/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.multiclientserver;

import java.io.IOException;
import java.io.InputStream;

public class NonClosingInputStream extends InputStream {
    private final InputStream wrappedInputStream;

    public NonClosingInputStream(InputStream inputStream) {
        this.wrappedInputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return wrappedInputStream.read();
    }

    // Override other methods from InputStream as necessary

    @Override
    public void close() throws IOException {
        // Do not close the underlying stream
    }
}
