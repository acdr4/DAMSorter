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
    private String keywords;
    private String descCaption;
    
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
    
    public void setKeywords(String kwords) {
        keywords = kwords;
    }
        
    public void setDescCaption(String descC) {
        descCaption = descC;
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
    
    public String getKeywords() {
        return this.keywords;
    }
        
    public String getDescCaption() {
        return this.descCaption;
    }
}
