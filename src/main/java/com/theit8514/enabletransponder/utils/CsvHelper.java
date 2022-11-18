package com.theit8514.enabletransponder.utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.*;

import com.fs.starfarer.api.Global;
import com.theit8514.enabletransponder.EnableTransponderConstants;

// Original code from IndEvo_IndustryHelper
public class CsvHelper {
    public static final Logger log = Global.getLogger(CsvHelper.class);

    @SuppressWarnings("unchecked")
    public static Set<String> getCSVSetFromMemory(String path) {
        String idString = "$" + path;
        TransientMemoryCache transientMemory = TransientMemoryCache.getInstance();

        if (transientMemory.contains(idString)) {
            return (Set<String>) transientMemory.getSet(idString);
        } else {
            Set<String> csvSet = new HashSet<>();

            try {

                JSONArray config = Global.getSettings().getMergedSpreadsheetDataForMod("id", path,
                        EnableTransponderConstants.MOD_ID);
                for (int i = 0; i < config.length(); i++) {

                    JSONObject row = config.getJSONObject(i);
                    String id = row.getString("id");

                    csvSet.add(id);
                }
            } catch (IOException | JSONException ex) {
                log.error(ex);
            }

            transientMemory.set(idString, csvSet);
            return csvSet;
        }
    }

}
