package com.tss.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static Connection connection;

    private DBConnection(){

    }
    public static Connection connect(){
        try{
            if(connection == null){
                Properties prop = new Properties();

                InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
                prop.load(input);

                Class.forName(prop.getProperty("db.driver"));

                connection = DriverManager.getConnection(prop.getProperty("db.url"),
                        prop.getProperty("db.username"),
                        prop.getProperty("password"));

                System.out.println("Connection Established Successfully.");
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return connection;
    }
}
