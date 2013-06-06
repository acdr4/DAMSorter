/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.yale.damsorter;

import com.artesia.asset.Asset;
import com.artesia.asset.AssetIdentifier;
import com.artesia.asset.metadata.services.AssetMetadataServices;
import com.artesia.asset.security.services.AssetSecurityServices;
import com.artesia.asset.services.AssetDataLoadRequest;
import com.artesia.asset.services.AssetServices;
import com.artesia.common.exception.BaseTeamsException;
import com.artesia.entity.TeamsIdentifier;
import com.artesia.entity.TeamsNumberIdentifier;
import com.artesia.metadata.MetadataField;
import com.artesia.security.SecurityPolicy;
import com.artesia.security.SecuritySession;
import com.artesia.security.services.SecurityPolicyServices;
import com.artesia.security.session.services.AuthenticationServices;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author acdr4
 */
public class DAMWrite {

    private static final String CDS_ACCESS_FIELD_ID = "YALE.FIELD.COMMON.CDS ACCESS";
    private static final String MEDIA_IS_PRIMARY_FIELD_ID = "YALE.FIELD.MEDIA.IS PRIMARY";
    private static final String MEDIA_RANK_FIELD_ID = "YALE.FIELD.MEDIA.RANK";

    public void writeData(SecuritySession session, SaveParams saveObj) {

        //System.out.println("Saving to DAM...");

        try {
            TeamsIdentifier cdsAccessLevelFieldId = new TeamsIdentifier(CDS_ACCESS_FIELD_ID);
            TeamsIdentifier mediaRankFieldId = new TeamsIdentifier(MEDIA_RANK_FIELD_ID);
            TeamsIdentifier isPrimaryFieldId = new TeamsIdentifier(MEDIA_IS_PRIMARY_FIELD_ID);

            MetadataField cdsAccessField = new MetadataField(cdsAccessLevelFieldId);
            MetadataField mediaRankField = new MetadataField(mediaRankFieldId);
            MetadataField isPrimaryField = new MetadataField(isPrimaryFieldId);
            MetadataField[] metadataFields = {cdsAccessField, mediaRankField, isPrimaryField};
            
            //now set metadata fields to the desired values (provided in the saveObj)
            cdsAccessField.setValue(saveObj.getCdsLevel());
            mediaRankField.setValue(saveObj.getRank());
            isPrimaryField.setValue(saveObj.getIsPrimary());

            //now write the changed metadata of the asset to DAM
            AssetIdentifier id = new AssetIdentifier(saveObj.getAssetId());
            AssetIdentifier[] assetIds = {id};
            //System.err.println("Writing to DAM...");
            AssetMetadataServices.getInstance().saveMetadataForAssets(assetIds, metadataFields, session);
            //System.out.println("Done writing!");
            
        } catch (Exception e) {
            System.out.println("Error saving data to DAM,  Message ID: " + e.getMessage());
        }
    }
}
