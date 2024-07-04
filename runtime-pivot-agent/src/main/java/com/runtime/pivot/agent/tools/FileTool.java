package com.runtime.pivot.agent.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTool {
    public static void writeFromStream(InputStream in, File dest) throws IOException {
        // Ensure the destination file exists
        if (dest == null || dest.isDirectory()) {
            throw new IllegalArgumentException("Destination file is invalid");
        }

        // Use try-with-resources to ensure streams are closed properly
        try (InputStream inputStream = in;
             FileOutputStream outputStream = new FileOutputStream(dest)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            // Read from the input stream and write to the output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void main(String[] args) {
        // Example usage
        InputStream inputStream = null;
        try {
            inputStream = new java.net.URL("https://example.com").openStream();
            File destFile = new File("output.txt");
            writeFromStream(inputStream, destFile);
            System.out.println("File written successfully");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // InputStream will be closed automatically by try-with-resources
        }
    }
}
