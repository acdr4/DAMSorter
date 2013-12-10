/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author acdr4
 */
package edu.yale.damsorter;

import java.io.*;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;

public class ConfigParser {

    static Logger log = Logger.getLogger(ConfigParser.class.getName());
    
    //DAMSorter.config file containing info like DAM username and password resides in home directory
    //private static String config_path = System.getProperty("user.home") + "/DAMSorter.config";
    //private static String config_path = System.getProperty("user.home") + "/DAMSorter.config";
    
    // fields to be used in DAMSorter.config
    private static String userid_field = "USERID";
    private static String password_field = "PASSWORD";
    private static String teams_home = "TEAMS_HOME";
    
    private static Properties configProp = new Properties();
    
    public void loadProps2() {
        //InputStream in = this.getClass().getClassLoader().getResourceAsStream("edu/yale/WEB-INF/config.properties");
 
        // If you are in a war, your classpath "current directory" is "WEB-INF/classes". To change go up level ../config.properties
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            log.info("Error: " + e.getMessage());
        }
    }
    
    public static String[] getConfig() {

        String[] conf = new String[3];

        ConfigParser sample = new ConfigParser();
        sample.loadProps2();
        
        conf[0] = configProp.getProperty("USERID");
        conf[1] = configProp.getProperty("PASSWORD");
        conf[2] = configProp.getProperty("TEAMS_HOME");
        //cr[0] = getFieldValue(userid_field);
        //cr[1] = getFieldValue(password_field);

        return conf;
    }

/*
    private static String getFieldValue(String field) {

        String value = "";

        try {
            // open DAMSorter.config to read userid and password
            FileInputStream fstream = new FileInputStream(config_path);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            //Read File Line By Line
            boolean found = false;
            while ((strLine = br.readLine()) != null && !found) {
                // use # for comments in the config file
                strLine = strLine.trim();
                if (strLine == null || strLine.equals("") || strLine.charAt(0) == '#') {
                    continue;
                }
                if (strLine.length() >= field.length() && strLine.substring(0, field.length()).equals(field)) {
                    // assuming there's always an = sign in the line!
                    int pos = strLine.indexOf("=");
                    value = strLine.substring(pos + 1);
                    found = true;
                }
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            //System.err.println("Error: " + e.getMessage());
            log.info("Error: " + e.getMessage());
        }
        return value.trim();
    }
*/
    
}
