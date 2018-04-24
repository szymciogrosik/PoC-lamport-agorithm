package com.szymon.service;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@NoArgsConstructor
public class SaveToFileService {
    private final String fileName = "score.txt";

    public void writeToNewFile(String messageToWrite) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(messageToWrite);
        writer.close();
    }

    void writeToExistingFile(String messageToWrite) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
            out.println(messageToWrite);
            out.close();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }
}
