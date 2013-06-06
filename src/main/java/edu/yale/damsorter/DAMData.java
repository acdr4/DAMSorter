/*
 * Stores all metadata pulled from DAM to allow some manipulation
 * and to perform all the work in memory
 */
package edu.yale.damsorter;

public class DAMData {

    String assetId = "";
    String primary = "";
    String rank = "";
    String cdsLevel = "";
    String thumb = "";

    // --------------------------------------------------------------------
    // constructor 
    public DAMData() {
    }

    // --------------------------------------------------------------------
    // setter functions 	
    public void setAssetId(String assetIdData) {
        assetId = assetIdData;
    }

    public void setRank(String rankData) {
        rank = rankData;
    }

    public void setPrimary(String primaryData) {
        primary = primaryData;
    }

    public void setCdsLevel(String cdsLevelData) {
        cdsLevel = cdsLevelData;
    }

    public void setThumb(String thumbUrl) {
        thumb = thumbUrl;
    }

    // --------------------------------------------------------------------
    // getter functions 
    public String getAssetId() {
        return assetId;
    }

    public String getPrimary() {
        return primary;
    }

    public String getRank() {
        return rank;
    }

    public String getCdsLevel() {
        return cdsLevel;
    }
    
    public String getThumb() {
        return thumb;
    }
}