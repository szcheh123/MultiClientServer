/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.multiclientserver;

import java.io.IOException;
import java.io.OutputStream;

public class NonClosingOutputStream extends OutputStream {
    private final OutputStream wrappedOutputStream;

    public NonClosingOutputStream(OutputStream outputStream) {
        this.wrappedOutputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        wrappedOutputStream.write(b);
    }

    // Override other methods from OutputStream as necessary

    @Override
    public void close() throws IOException {
        // Do not close the underlying stream
    }
}

