package com.theit8514.enabletransponder;

import java.util.List;

import com.fs.starfarer.api.*;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.theit8514.enabletransponder.listeners.*;

public class EnableTransponderPlugin extends BaseModPlugin {
    @Override
    public void onGameLoad(boolean newGame) {
        if (EnableTransponderConstants.DISABLED) {
            disablePlugin();
        } else {
            enablePlugin();
        }
    }

    private void enablePlugin() {
        SectorAPI sector = Global.getSector();
        ListenerManagerAPI listenerManager = sector.getListenerManager();
        if (!listenerManager.hasListenerOfClass(LocationListener.class)) {
            listenerManager.addListener(new LocationListener(), true);
        }

        sector.addTransientListener(new CampaignListener());
    }

    private void disablePlugin() {
        SectorAPI sector = Global.getSector();
        sector.getListenerManager().removeListenerOfClass(LocationListener.class);
        List<CampaignEventListener> listeners = sector.getAllListeners();
        for (CampaignEventListener listener : listeners) {
            if (listener instanceof CampaignListener) {
                sector.removeListener(listener);
            }
        }
    }
}
