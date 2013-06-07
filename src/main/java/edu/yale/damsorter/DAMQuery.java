/*
 * Pulls metadata from DAM, does some in memory manipulation 
 * of some of the fields, adds the DAMData object to vector list 
 * of DAMData objects and finally sends the vector list to 
 * MySQL class for insertion into DB. 
 * 
 * 
 */
package edu.yale.damsorter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.*; //importing some java classes
import java.lang.*;
import java.io.*;

// DAM
import com.artesia.asset.Asset;
import com.artesia.asset.services.AssetDataLoadRequest;
import com.artesia.asset.services.AssetServices;
import com.artesia.asset.services.RetrieveAssetsCriteria;
import com.artesia.common.MetadataFieldConstants;
import com.artesia.common.exception.BaseTeamsException;
import com.artesia.entity.TeamsIdentifier;
import com.artesia.search.Search;
import com.artesia.search.SearchConstants;
import com.artesia.search.SearchCondition;
import com.artesia.search.SearchMetadataCondition;
import com.artesia.security.SecuritySession;
import com.artesia.security.session.services.AuthenticationServices;

public class DAMQuery {
    // to increase the nuber of records requested from DAM
    // increase the memory given to this program, all this
    // is done in memory. 

    private int maxDAMrecords = 25000;

    protected ArrayList<DAMData> queryDAM(SecuritySession session, SearchParams searchObj) {

        System.out.println("Querying DAM...");
        ArrayList<DAMData> damArr = new ArrayList<DAMData>();

        try {
            
            long startTime = System.currentTimeMillis();
            
            AssetDataLoadRequest dataRequest = new AssetDataLoadRequest();
            dataRequest.setLoadMetadata(true);
            dataRequest.setLoadPreviewBytes(true);
            dataRequest.setLoadPreviewAndSynopsisInfo(true);
            dataRequest.setLoadThumbnailBytes(true);
            dataRequest.setLoadThumbnailAndKeywords(true, true);
            dataRequest.setLoadThumbnailAndKeywordInfo(true);
            dataRequest.setLoadSecurityPolicies(true);
            dataRequest.setLoadThumbnailUrl(true);

            TeamsIdentifier[] fieldIds = new TeamsIdentifier[]{ // -- DAM 6.8 --	-- MySQL --
                new TeamsIdentifier("ARTESIA.FIELD.ASSET ID"), // char (40)		varchar (45)
                new TeamsIdentifier("YALE.FIELD.COMMON.REPOSITORY CODE"), // char (25)		varchar (45)
                new TeamsIdentifier("YALE.FIELD.IPTC.XMP.FILENAME"), // char (80)		varchar (128)
                new TeamsIdentifier("YALE.FIELD.COMMON.OBJECT ID"), // char (50)		varchar (45)
                new TeamsIdentifier("YALE.FIELD.BIBLIO.BIB ID"), // char (25)		varchar ()
                new TeamsIdentifier("YALE.FIELD.MEDIA.IS PRIMARY"), // char (1)		varchar (45)
                new TeamsIdentifier("YALE.FIELD.MEDIA.RANK"), // integer (5)		int
                new TeamsIdentifier("YALE.FIELD.COMMON.CDS ACCESS")
            };

            dataRequest.setMetadataFieldsToRetrieve(fieldIds);

            //search by objectID or bibID
            SearchMetadataCondition objectIDlimit = null;
            if (searchObj.getCriterion().equals("bibid")) {
                objectIDlimit = new SearchMetadataCondition(
                        new TeamsIdentifier("YALE.FIELD.BIBLIO.BIB ID"),
                        SearchConstants.OPERATOR_ID__CHAR_IS, searchObj.getid());
            } else if (searchObj.getCriterion().equals("objectid")) {
                objectIDlimit = new SearchMetadataCondition(
                        new TeamsIdentifier("YALE.FIELD.COMMON.OBJECT ID"),
                        SearchConstants.OPERATOR_ID__CHAR_IS, searchObj.getid());
                //objectID = 1473
            } else {
                return null;
            }

            // repository code = YCBA
            SearchMetadataCondition repositoryCode = new SearchMetadataCondition(
                    new TeamsIdentifier("YALE.FIELD.COMMON.REPOSITORY CODE"),
                    SearchConstants.OPERATOR_ID__CHAR_IS, "YCBA");

            // filename  
            SearchMetadataCondition filenameNotNull = new SearchMetadataCondition(
                    new TeamsIdentifier("YALE.FIELD.IPTC.XMP.FILENAME"),
                    SearchConstants.OPERATOR_ID__CHAR_IS_NOT_EMPTY, null);

            // asset not deleted 
            SearchMetadataCondition notDeleted = new SearchMetadataCondition(
                    MetadataFieldConstants.METADATA_FIELD_ID__CONTENT_STATUS,
                    SearchConstants.OPERATOR_ID__CHAR_IS_NOT, "DELETED");


            objectIDlimit.setRelationalOperator(SearchConstants.OPERATOR_AND);
            repositoryCode.setRelationalOperator(SearchConstants.OPERATOR_AND);
            filenameNotNull.setRelationalOperator(SearchConstants.OPERATOR_AND);
            notDeleted.setRelationalOperator(SearchConstants.OPERATOR_AND);

            Search search = new Search();   // create search objects 
            search.addCondition(filenameNotNull);
            search.addCondition(objectIDlimit);
            search.addCondition(repositoryCode);
            search.addCondition(notDeleted);

            RetrieveAssetsCriteria criteria = new RetrieveAssetsCriteria();

            criteria.setSearchInfo(search, maxDAMrecords); // to increase the number of records, increase VM memory -Xms128m -Xmx512m

            System.out.println("  executing query for YCBA, filename != null, asset not deleted, " + searchObj.getCriterion() + " = " + searchObj.getid() + ".");
            Asset[] searchAssets = AssetServices.getInstance().retrieveAssets(criteria, dataRequest, session);
            System.out.println("  retrieved " + searchAssets.length + " records");

            DAMData data;

            for (Asset asset : searchAssets) {

                data = new DAMData();

                data.setAssetId(asset.getMetadata().getValueForField(new TeamsIdentifier("ARTESIA.FIELD.ASSET ID")).getStringValue());
                data.setPrimary(asset.getMetadata().getValueForField(new TeamsIdentifier("YALE.FIELD.MEDIA.IS PRIMARY")).getStringValue());
                data.setRank(asset.getMetadata().getValueForField(new TeamsIdentifier("YALE.FIELD.MEDIA.RANK")).getStringValue());
                if (asset.getMetadata().getValueForField(new TeamsIdentifier("YALE.FIELD.COMMON.CDS ACCESS")) != null) {
                    data.setCdsLevel(asset.getMetadata().getValueForField(new TeamsIdentifier("YALE.FIELD.COMMON.CDS ACCESS")).getStringValue());
                } else {
                    System.out.println("No YALE.FIELD.COMMON.CDS ACCESS for Asset # " + data.getAssetId());
                }

                if (asset.getRenditionContent().getThumbnailContent().getContentBytes() != null) {
                    //get thumbnail binary from DB and write it to images folder...image name = assetId
                    byte[] thumbnail = asset.getRenditionContent().getThumbnailContent().getContentBytes();
                    OutputStream out = new FileOutputStream(searchObj.getPath() + "images/" + data.getAssetId() + ".jpeg");
                    out.write(thumbnail);
                    out.close();
                    data.setThumb(searchObj.getUrl() + "/images/" + data.getAssetId() + ".jpeg");
                } else {
                    System.out.println("No thumbnail available for" + data.getAssetId());
                }

                //add the DAMData object to our arraylist that is to be returned
                damArr.add(data);
            } //end for loop 
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Query execution time = " + duration + "ms");
        } //end try
        catch (Exception e) {
            System.out.println("Error querying DAM,  Message ID: " + e.getMessage());
            //System.exit(0);
        }
        return damArr;
    }//end queryDAM
}//end class