/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.yale.damsorter;

import com.artesia.common.exception.BaseTeamsException;
import com.artesia.entity.TeamsIdentifier;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import javax.servlet.GenericServlet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import com.artesia.security.SecuritySession;
import java.io.File;

public class SearchQueryHandler {

    //stores the search type parameter specified i.e. bibid OR objectid
    private String search_by;
    //stores the search ID input by the user
    private String search_id;
    //stores the path where the project root directory is located
    public String path;
    //stores the home url of the project
    public String url;

    public SearchQueryHandler() {
        search_by = null;
        search_id = null;
    }

    /**
     * @param by the search_by to set
     */
    public void setsearch_by(String by) {
        this.search_by = by;
    }

    /**
     * @return the search_by
     */
    public String getsearch_by() {
        return search_by;
    }

    /**
     * @return the search_id
     */
    public String getsearch_id() {
        return search_id;
    }

    /**
     * @param id the search_id to set
     */
    public void setsearch_id(String id) {
        this.search_id = id;
    }

    /**
     * @return the JSON string containing data for the search parameters
     * specified
     */
    public String getJSON() throws org.json.JSONException, BaseTeamsException, IOException {

        // get DAM login credentials from ConfigParser
        String [] credentials = ConfigParser.getCredentials();
        String userid = credentials[0];
        String password = credentials[1];

        // this JVM variable has to be set for login to proceed!
        System.setProperty("TEAMS_HOME", System.getProperty("user.home"));
        
        /*Map<String, String> env = System.getenv();
         for (String envName : env.keySet()) {
         System.out.format("%s=%s%n", envName, env.get(envName));
         }*/

        System.out.println("Logging into DAM...");
        SecuritySession session = SessionHandler.login(userid, password);

        // create folder for download images
        //System.out.println("Url is: " + url);
        //System.out.println("Path is: " + path);
        String dirName = path + "/images";
        File imagesDir = new File(dirName);
        // clean the images directory if it pre-exists
        if(imagesDir.exists()) {
            FileUtils.deleteDirectory(imagesDir);
        }
        // create a new images directory
        System.out.println("creating directory: " + dirName);
        File imgDir = new File(dirName);
        boolean result = imgDir.mkdir();
        if (result) {
            System.out.println("DIR created");
        } else {
            System.err.println("ERROR: DIR creation failed!");
        }

        //set up Search parameters object
        SearchParams searchObj = new SearchParams(search_by, search_id, path, url);

        //Query DAM
        ArrayList<DAMData> queryResult;
        DAMQuery dq = new DAMQuery();
        queryResult = dq.queryDAM(session, searchObj);

        //sort records data based on rank
        DAMData[] damData = new DAMData[queryResult.size()];
        for (int i = 0; i < queryResult.size(); i++) {
            damData[i] = queryResult.get(i);
        }
        //damData = mergeSort(damData);
        /*for (int i = 0; i < damData.length; i++) {
            //reassign ranks as 0,1,2,3,...
            damData[i].setRank(i + "");
            //print for viewing results on console
            System.out.println(damData[i].getAssetId()
                    + " " + damData[i].getRank()
                    + " " + damData[i].getPrimary()
                    + " " + damData[i].getThumb()
                    + " " + damData[i].getCdsLevel());
        }*/

        // make a JSON object to be returned to client side
        JSONArray data = new JSONArray();
        int primaryIdx = -1;
        for (int i = 0; i < damData.length; i++) {
            JSONObject json = new JSONObject();
            json.put("id", damData[i].getAssetId());
            json.put("rank", damData[i].getRank());
            if (damData[i].getPrimary() != null && damData[i].getPrimary().equals("Y")) {
                primaryIdx = i;
            }
            json.put("thumb", damData[i].getThumb());

            if (damData[i].cdsLevel == null) {
                // default CDS level is 12 if it was null??
                json.put("cdsLevel", new Integer(12));
            } else {
                json.put("cdsLevel", damData[i].getCdsLevel());
            }
            data = data.put(json);
        }

        JSONObject finalJson = new JSONObject();
        finalJson.put("primaryIdx", primaryIdx);
        finalJson.put("search_by",search_by);
        finalJson.put("search_id",search_id);
        finalJson.put("recordsArr", data);

        // logout from DAM before returning
        SessionHandler.logout(session);
        System.out.println("Logged out of DAM");

        return finalJson.toString();
    }

    /* merge sort algorithm used to sort DAMData array on the rank attribute */
    public DAMData[] mergeSort(DAMData[] data) {
        int lenD = data.length;
        if (lenD <= 1) {
            return data;
        } else {
            DAMData[] sorted;
            int middle = lenD / 2;
            int rem = lenD - middle;
            DAMData[] L = new DAMData[middle];
            DAMData[] R = new DAMData[rem];
            System.arraycopy(data, 0, L, 0, middle);
            System.arraycopy(data, middle, R, 0, rem);
            L = this.mergeSort(L);
            R = this.mergeSort(R);
            sorted = merge(L, R);
            return sorted;
        }
    }

    public DAMData[] merge(DAMData[] L, DAMData[] R) {
        int lenL = L.length;
        int lenR = R.length;
        DAMData[] merged = new DAMData[lenL + lenR];
        int i = 0;
        int j = 0;
        while (i < lenL || j < lenR) {
            if (i < lenL & j < lenR) {
                if (Integer.parseInt(L[i].getRank()) <= Integer.parseInt(R[j].getRank())) {
                    merged[i + j] = L[i];
                    i++;
                } else {
                    merged[i + j] = R[j];
                    j++;
                }
            } else if (i < lenL) {
                merged[i + j] = L[i];
                i++;
            } else if (j < lenR) {
                merged[i + j] = R[j];
                j++;
            }
        }
        return merged;
    }
}
