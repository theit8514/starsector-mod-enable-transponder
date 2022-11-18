package com.theit8514.enabletransponder;

import com.fs.starfarer.api.*;
import com.theit8514.enabletransponder.listeners.*;

public class EnableTransponderPlugin extends BaseModPlugin {
    @Override
    public void onGameLoad(boolean newGame) {
        addTransientListener(new LocationListener());
        Global.getSector().addListener(new CampaignListener());
    }

    private void addTransientListener(Object listener) {
        Global.getSector().getListenerManager().addListener(listener, true);
    }
}
