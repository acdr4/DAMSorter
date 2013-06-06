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

public class ConfigParser {

    //DAMSorter.config file containing info like DAM username and password resides in home directory
    private static String config_path = System.getProperty("user.home") + "/DAMSorter.config";
    
    // fields to be used in DAMSorter.config
    private static String userid_field = "USERID";
    private static String password_field = "PASSWORD";

    public static String[] getCredentials() {

        String[] cr = new String[2];

        cr[0] = getFieldValue(userid_field);
        cr[1] = getFieldValue(password_field);

        return cr;
    }

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
            System.err.println("Error: " + e.getMessage());
        }
        return value.trim();
    }
}
