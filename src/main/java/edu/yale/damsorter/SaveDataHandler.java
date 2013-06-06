/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.yale.damsorter;

import com.artesia.security.SecuritySession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author acdr4
 */
public class SaveDataHandler {

    //stores json data received from client side post as string
    private String json_data;

    public SaveDataHandler() {
        json_data = null;
    }

    /**
     * @param json the json data to set
     */
    public void setjson_data(String json) {
        this.json_data = json;
    }

    /**
     * @return the json_data
     */
    public String getjson_data() {
        return json_data;
    }

    public void saveJson() throws org.json.JSONException {

        String userid = "**Confidential**";
        String password = "**Confidential**";

        //create JSON object from json string received from client side
        JSONObject json = new JSONObject(json_data);
        //System.out.println(json.toString());
        String primaryIdx = json.getString("primaryIdx");
        JSONArray jsonArr = json.getJSONArray("recordsArr");

        // create a log in session of DAM
        System.out.println("Loggin in DAM...");
        SecuritySession session = SessionHandler.login(userid, password);

        System.out.println("Saving to DAM...");
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject record = jsonArr.getJSONObject(i);
            //make a SaveParams object for compactness of data to write to DAM
            SaveParams saveObj = new SaveParams();
            saveObj.setAssetId(record.getString("id"));
            saveObj.setRank(record.getString("rank"));
            saveObj.setCdsLevel(record.getString("cdsLevel"));
            if (i == Integer.parseInt(primaryIdx)) {
                saveObj.setIsPrimary("Y");
            } else {
                saveObj.setIsPrimary("N");
            }
            //invoke the class that writes data to DAM
            DAMWrite dw = new DAMWrite();
            dw.writeData(session, saveObj);
        }
        System.out.println("Saving data completed!");

        System.out.print("Primary index at " + primaryIdx);
        System.out.print(".. rank = ");
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject record = jsonArr.getJSONObject(i);
            System.out.print(record.getString("rank") + ", ");
        }
        System.out.print(".. cds level = ");
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject record = jsonArr.getJSONObject(i);
            System.out.print(record.getString("cdsLevel") + ", ");
        }
        System.out.println();

        // logout from DAM before returning
        SessionHandler.logout(session);
        System.out.println("Logged out of DAM");
    }
}
