package com.tss.Utils;

import java.util.Scanner;

public class EchoScanner extends Scanner {

    public EchoScanner(java.io.InputStream source) {
        super(source);
    }

    @Override
    public String nextLine() {
        String input = super.nextLine();
        System.out.println("Echo: " + input);
        return input;
    }
}