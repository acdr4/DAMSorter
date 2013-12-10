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
import org.apache.log4j.Logger;

public class SearchQueryHandler {

    static Logger log = Logger.getLogger(SearchQueryHandler.class.getName());
    
    //stores the search type parameter specified i.e. bibid OR objectid
    private String search_by;
    //stores the search ID input by the user
    private String search_id;
    //stores the search restriction for filename containing "pub"
    private String pub_only;
    //stores the path where the project root directory is located
    public String path;
    //stores the home url of the project
    public String url;

    public SearchQueryHandler() {
        search_by = null;
        search_id = null;
        pub_only = null; // default true, search only for object with filename containing "pub"
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
        if (id != null)
            this.search_id = id;
        else
            this.search_id = "";
    }
    
    public String getpub_only() {
        return pub_only;
    }
    
    public void setpub_only(String pub)
    {
        this.pub_only = pub;
    }
    /**
     * @return the JSON string containing data for the search parameters
     * specified
     */
    public String getJSON() throws org.json.JSONException, BaseTeamsException, IOException {

        // get DAM login credentials from ConfigParser
        String [] credentials = ConfigParser.getConfig();
        String userid = credentials[0];
        String password = credentials[1];
        String teams_home = credentials[2];

        // this JVM variable has to be set for login to proceed!
        //System.setProperty("TEAMS_HOME", System.getProperty("user.home"));
        //System.setProperty("TEAMS_HOME", ".");
        System.setProperty("TEAMS_HOME", teams_home);
        
        /*Map<String, String> env = System.getenv();
         for (String envName : env.keySet()) {
         System.out.format("%s=%s%n", envName, env.get(envName));
         }*/

        //System.out.println("Logging into DAM...");
        log.info("Logging into DAM...");
        
        SecuritySession session = SessionHandler.login(userid, password);

        // create folder for download images
        //System.out.println("Url is: " + url);
        //System.out.println("Path is: " + path);
        String dirName = path + "/images";
        //File imagesDir = new File(dirName);
        // clean the images directory if it pre-exists -- better way is to just remove files older then 3 days
        //if(imagesDir.exists()) {
        //    FileUtils.deleteDirectory(imagesDir);
        //}
        // create a new images directory
        // directory is part of the project - need to only make sure we empty it nightly
        /*
        System.out.println("creating directory: " + dirName);
        File imgDir = new File(dirName);
        boolean result = imgDir.mkdir();
        if (result) {
            System.out.println("DIR created");
        } else {
            System.err.println("ERROR: DIR creation failed!");
        }
        */
        
        //set up Search parameters object
        SearchParams searchObj = new SearchParams(search_by, search_id, pub_only, path, url);

        //Query DAM
        ArrayList<DAMData> queryResult;
        DAMQuery dq = new DAMQuery();
        queryResult = dq.queryDAM(session, searchObj);

        //sort records data based on rank
        DAMData[] damData = new DAMData[queryResult.size()];
        for (int i = 0; i < queryResult.size(); i++) {
            damData[i] = queryResult.get(i);
            if(damData[i].rank == null) {
                damData[i].rank = i+"";
            }
        }
        damData = mergeSort(damData);
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
        //int primaryIdx = -1;
        for (int i = 0; i < damData.length; i++) {
            JSONObject json = new JSONObject();
            json.put("id", damData[i].getAssetId());
            json.put("filename", damData[i].getFilename());
            if (damData[i].getKeywords() != null)
                json.put("keywords", damData[i].getKeywords());
            else
                json.put("keywords", "");
            if (damData[i].getDescCaption() != null)
                json.put("descCaption", damData[i].getDescCaption());
            else
                json.put("descCaption", "");
            if(damData[i].rank == null) {
                json.put("rank", "" + damData.length);
            } else {
                json.put("rank", damData[i].getRank());
            }
            if(damData[i].getPrimary() == null) {
                json.put("primary", "N");
            } else {
                json.put("primary", damData[i].getPrimary());
            }
            //if (damData[i].getPrimary() != null && damData[i].getPrimary().equals("Y")) {
            //    primaryIdx = i;
            //}
            json.put("thumb", damData[i].getThumb());

            /*
            if (damData[i].cdsLevel == null) {
                // default CDS level is 0 if it was null??
                json.put("cdsLevel", new Integer(0));
            } else {
                json.put("cdsLevel", damData[i].getCdsLevel());
            }
            */
            //System.out.println(data);
            data = data.put(json);
        }
        
        JSONObject finalJson = new JSONObject();
        //finalJson.put("primaryIdx", primaryIdx);
        finalJson.put("search_by",search_by);
        finalJson.put("search_id",search_id);
        finalJson.put("pub_only",pub_only);
        finalJson.put("recordsArr", data);
        //System.out.println(finalJson.toString());

        // logout from DAM before returning
        SessionHandler.logout(session);
        //System.out.println("Logged out of DAM");
        log.info("Logged out of DAM");
        
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
