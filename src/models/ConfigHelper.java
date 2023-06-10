package models;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigHelper {
    public static String getProperty(String name){
        Properties prop = new Properties();
        try {
            FileInputStream file = new FileInputStream("config.properties");
            prop.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(name);
    }
}
