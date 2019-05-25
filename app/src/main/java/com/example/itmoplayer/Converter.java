package com.example.itmoplayer;


import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Converter {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        String file_encode = encoder("C:\\Users\\iiiki\\AndroidStudioProjects\\ITMOPlayer\\app\\src\\main\\java\\com\\example\\itmoplayer\\EdSheeran.jpg");
        try {
            FileWriter file = new FileWriter("C:\\Users\\iiiki\\AndroidStudioProjects\\ITMOPlayer\\app\\src\\main\\java\\com\\example\\itmoplayer\\my_java_file.txt");
            file.write(file_encode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public static void write_encoder(String python_file){
//        FileWriter file = null;
//        try {
//            file = new FileWriter("C:\\Users\\iiiki\\AndroidStudioProjects\\ITMOPlayer\\app\\src\\main\\java\\com\\example\\itmoplayer\\my_python_file.txt");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            file.write(python_file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encoder(String filePath) {
        String base64File = "";
        File file = new File(filePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            // Reading a file from file system
            byte fileData[] = new byte[(int) file.length()];
            imageInFile.read(fileData);
            base64File = Base64.getEncoder().encodeToString(fileData);
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the file " + ioe);
        }
        return base64File;
    }

}
