package com.dm.material.dashboard.candybar.activities;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.dm.material.dashboard.candybar.R;
import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRDefinitions;
import com.michaelflisar.gdprdialog.GDPRSetup;
import com.michaelflisar.gdprdialog.helper.GDPRPreperationData;

public class MinimalGDPR extends AppCompatActivity implements View.OnClickListener, GDPR.IGDPRCallback {
    // minimal setup
    private GDPRSetup mSetup = new GDPRSetup(
            GDPRDefinitions.ADMOB,
            GDPRDefinitions.FIREBASE_ANALYTICS,
            GDPRDefinitions.ONESIGNAL,
            GDPRDefinitions.FABRIC_CRASHLYTICS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimal_gdpr);
        //mSetup.withCheckRequestLocation(true);
        mSetup.withBottomSheet(true);
        mSetup.withLoadAdMobNetworks("pub-8676713254153441");

        mSetup.withPrivacyPolicy("https://porting-team.ru/privacy/miux_app/");
        // show GDPR Dialog if necessary, the library takes care about if and how to show it
        showGDPRIfNecessary();
    }

    @Override
    public void onClick(View v) {
        GDPR.getInstance().resetConsent();
        // reshow dialog instantly
        showGDPRIfNecessary();
    }

    private void showGDPRIfNecessary() {
        GDPR.getInstance().checkIfNeedsToBeShown(this, mSetup);
    }

    // --------------------
    //  GDPR.IGDPRCallback
    // --------------------

    @Override
    public void onConsentNeedsToBeRequested(GDPRPreperationData data) {
        // forward the result and show the dialog
        GDPR.getInstance().showDialog(this, mSetup, data.getLocation());
    }

    @Override
    public void onConsentInfoUpdate(GDPRConsentState consentState, boolean isNewState) {
        // consent is known, handle this
        //Toast.makeText(this, String.format("ConsentState: %s", consentState.logString()), Toast.LENGTH_LONG).show();
        finish();
    }
}