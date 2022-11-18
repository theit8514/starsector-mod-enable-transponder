package com.theit8514.enabletransponder.utils;

import java.util.*;
import java.util.regex.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.theit8514.enabletransponder.EnableTransponderConstants;

public class TransponderHelper {
    private static final Map<String, Matcher> MatcherCache = new HashMap<String, Matcher>();

    /**
     * Gets the fleet patrol names from the data store. These may contain regex
     * names.
     * 
     * @return a set of string values which may contain a regex
     */
    private static Set<String> getFleetPatrolNames() {
        Set<String> allowedList = CsvHelper.getCSVSetFromMemory(EnableTransponderConstants.FLEET_PATROL_LIST_FILE);
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();

        for (String key : memory.getKeys()) {
            if (key.contains(EnableTransponderConstants.FLEET_PATROL_LIST_PREFIX))
                allowedList.add(memory.getString(key));
        }

        return allowedList;
    }

    /**
     * Get the regex pattern matchers for matching the fleet patrol names.
     * Caller must use matcher.reset() to change the input before checking.
     * 
     * @return a set of cached matchers
     */
    public static Set<Matcher> getFleetPatrolMatchers() {
        Set<String> names = getFleetPatrolNames();
        Set<Matcher> matchers = new HashSet<Matcher>();

        for (String name : names) {
            Matcher matcher = MatcherCache.get(name);
            if (matcher == null) {
                matcher = Pattern.compile(name, Pattern.CASE_INSENSITIVE).matcher("");
                MatcherCache.put(name, matcher);
            }

            matchers.add(MatcherCache.get(name));
        }

        return matchers;
    }
}
