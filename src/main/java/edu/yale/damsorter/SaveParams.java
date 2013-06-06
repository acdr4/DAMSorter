/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.yale.damsorter;

/**
 *
 * @author acdr4
 */
public class SaveParams {
    
    private String assetId;
    private String rank;
    private String cdsLevel;
    private String isPrimary;
    
    /* setters */
    public void setAssetId(String id) {
        assetId = id;
    }
    
    public void setRank(String r) {
        rank = r;
    }
    
    public void setCdsLevel(String lvl) {
        cdsLevel = lvl;
    }
    
    public void setIsPrimary(String pri) {
        isPrimary = pri;
    }
    
    /* getters */
    public String getAssetId() {
        return this.assetId;
    }
    
    public String getRank() {
        return this.rank;
    }
    
    public String getCdsLevel() {
        return this.cdsLevel;
    }
    
    public String getIsPrimary() {
        return this.isPrimary;
    }
}
