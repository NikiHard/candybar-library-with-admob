package com.material.dashboard.candybar.sample.applications;

import androidx.annotation.NonNull;

import com.dm.material.dashboard.candybar.applications.CandyBarApplication;
import com.michaelflisar.gdprdialog.GDPR;

public class CandyBar extends CandyBarApplication {
    
    @NonNull
    @Override
    public Configuration onInit() {
        //Sample configuration
        GDPR.getInstance().init(this);
        Configuration configuration = new Configuration();

        configuration.setGenerateAppFilter(true);
        configuration.setGenerateAppMap(true);
        configuration.setGenerateThemeResources(true);
        
        return configuration;
    }
}
