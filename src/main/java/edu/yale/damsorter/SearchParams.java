/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.yale.damsorter;

/** an immutable object that stores search criterion and ID
 *  input by the user on client side
 * @author acdr4
 */
public class SearchParams {
    
    private String search_criterion;
    private String search_id;
    private String images_path;
    private String home_url;
    
    public SearchParams(String s_by, String s_id, String i_path, String url) {
        search_criterion = s_by;
        search_id = s_id;
        images_path = i_path;
        home_url = url;
    }
    
    public String getCriterion() {
        return this.search_criterion;
    }
    
    public String getid() {
        return this.search_id;
    }
    
    public String getPath() {
        return this.images_path;
    }
    
    public String getUrl() {
        return this.home_url;
    }
    
}
