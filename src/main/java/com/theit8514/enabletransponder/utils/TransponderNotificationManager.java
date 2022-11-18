package com.theit8514.enabletransponder.utils;

import java.util.HashSet;
import java.util.Set;

import com.theit8514.enabletransponder.EnableTransponderConstants;

public class TransponderNotificationManager {
    /**
     * Clear the list of notified systems.
     */
    public static void clear() {
        Set<String> cache = getCache();
        cache.clear();
    }

    /**
     * Checks if the location has already been notified.
     * 
     * @param locationId
     * @return
     */
    public static boolean alreadyNotified(String locationId) {
        Set<String> cache = getCache();
        boolean exists = cache.contains(locationId);
        cache.add(locationId);
        return exists;
    }

    /**
     * Checks if the location has been notified or not. Does not update notification
     * status.
     * 
     * @param locationId
     * @return
     */
    public static boolean hasNotified(String locationId) {
        Set<String> cache = getCache();
        return cache.contains(locationId);
    }

    @SuppressWarnings("unchecked")
    private static Set<String> getCache() {
        Set<String> set = null;
        TransientMemoryCache cache = TransientMemoryCache.getInstance();
        if (!cache.contains(EnableTransponderConstants.NOTIFICATION_CACHE_KEY)) {
            cache.set(EnableTransponderConstants.NOTIFICATION_CACHE_KEY, set = new HashSet<String>());
        } else {
            set = (Set<String>) cache.getSet(EnableTransponderConstants.NOTIFICATION_CACHE_KEY);
        }

        return set;
    }
}
