package com.dm.material.dashboard.candybar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.SoftKeyboardHelper;
import com.danimahardhika.android.helpers.license.LicenseHelper;
import com.danimahardhika.android.helpers.permission.PermissionCode;
import com.dm.material.dashboard.candybar.activities.callbacks.ActivityCallback;
import com.dm.material.dashboard.candybar.activities.configurations.ActivityConfiguration;
import com.dm.material.dashboard.candybar.applications.CandyBarApplication;
import com.dm.material.dashboard.candybar.databases.Database;
import com.dm.material.dashboard.candybar.fragments.AboutFragment;
import com.dm.material.dashboard.candybar.fragments.dialog.IntentChooserFragment;
import com.dm.material.dashboard.candybar.helpers.ConfigurationHelper;
import com.dm.material.dashboard.candybar.helpers.LicenseCallbackHelper;
import com.dm.material.dashboard.candybar.helpers.LocaleHelper;
import com.dm.material.dashboard.candybar.helpers.NavigationViewHelper;
import com.dm.material.dashboard.candybar.helpers.TypefaceHelper;
import com.dm.material.dashboard.candybar.helpers.WallpaperHelper;
import com.dm.material.dashboard.candybar.items.Home;
import com.dm.material.dashboard.candybar.items.Icon;
import com.dm.material.dashboard.candybar.preferences.Preferences;
import com.dm.material.dashboard.candybar.receivers.CandyBarBroadcastReceiver;
import com.dm.material.dashboard.candybar.services.CandyBarService;
import com.dm.material.dashboard.candybar.services.CandyBarWallpapersService;
import com.dm.material.dashboard.candybar.tasks.IconRequestTask;
import com.dm.material.dashboard.candybar.tasks.IconsLoaderTask;
import com.dm.material.dashboard.candybar.utils.Extras;
import com.dm.material.dashboard.candybar.utils.InAppBillingProcessor;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.dm.material.dashboard.candybar.utils.listeners.SearchListener;
import com.dm.material.dashboard.candybar.utils.listeners.WallpapersListener;
import com.dm.material.dashboard.candybar.R;
import com.dm.material.dashboard.candybar.fragments.ApplyFragment;
import com.dm.material.dashboard.candybar.fragments.FAQsFragment;
import com.dm.material.dashboard.candybar.fragments.HomeFragment;
import com.dm.material.dashboard.candybar.fragments.IconsBaseFragment;
import com.dm.material.dashboard.candybar.fragments.RequestFragment;
import com.dm.material.dashboard.candybar.fragments.SettingsFragment;
import com.dm.material.dashboard.candybar.fragments.WallpapersFragment;
import com.dm.material.dashboard.candybar.fragments.dialog.ChangelogFragment;
import com.dm.material.dashboard.candybar.fragments.dialog.InAppBillingFragment;
import com.dm.material.dashboard.candybar.helpers.IntentHelper;
import com.dm.material.dashboard.candybar.helpers.RequestHelper;
import com.dm.material.dashboard.candybar.items.InAppBilling;
import com.dm.material.dashboard.candybar.items.Request;
import com.dm.material.dashboard.candybar.utils.ImageConfig;
import com.dm.material.dashboard.candybar.utils.listeners.InAppBillingListener;
import com.dm.material.dashboard.candybar.utils.listeners.RequestListener;
import com.dm.material.dashboard.candybar.utils.views.HeaderView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRLocation;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.onesignal.OneSignal;

import java.lang.ref.WeakReference;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
 * CandyBar - Material Dashboard
 *
 * Copyright (c) 2014-2016 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public abstract class CandyBarMainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, RequestListener, InAppBillingListener,
        SearchListener, WallpapersListener, ActivityCallback {

    private TextView mToolbarTitle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private AdView mAdView;
    private String mFragmentTag;
    private int mPosition, mLastPosition;
    private CandyBarBroadcastReceiver mReceiver;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager mFragManager;
    private LicenseHelper mLicenseHelper;
    private boolean mIsMenuVisible = true;
    boolean allowsPersonalAds = true;
    public static List<Request> sMissedApps;
    public static List<Icon> sSections;
    public static Home sHomeIcon;
    public static int sInstalledAppsCount;
    public static int sIconsCount;
    private static WeakReference<InAppBillingProcessor> mInAppBilling;
    private ActivityConfiguration mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.setTheme(Preferences.get(this).isDarkTheme() ?
                R.style.AppThemeDark : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(4)
                .session(3)
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        ratingDialog.show();
        ColorHelper.setupStatusBarIconColor(this);
        ColorHelper.setNavigationBarColor(this, ContextCompat.getColor(this,
                Preferences.get(this).isDarkTheme() ?
                        R.color.navigationBarDark : R.color.navigationBar));
        registerBroadcastReceiver();
        startService(new Intent(this, CandyBarService.class));

        inicilisation();


        //Todo: wait until google fix the issue, then enable wallpaper crop again on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Preferences.get(this).setCropWallpaper(false);
        }

        mConfig = onInit();
        InAppBillingProcessor.get(this).init(mConfig.getLicenseKey());

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);

        toolbar.setPopupTheme(Preferences.get(this).isDarkTheme() ?
                R.style.AppThemeDark : R.style.AppTheme);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mFragManager = getSupportFragmentManager();

        initNavigationView(toolbar);
        initNavigationViewHeader();

        mPosition = mLastPosition = 0;
        if (savedInstanceState != null) {
            mPosition = mLastPosition = savedInstanceState.getInt(Extras.EXTRA_POSITION, 0);
            onSearchExpanded(false);
        }

        IntentHelper.sAction = IntentHelper.getAction(getIntent());
        if (IntentHelper.sAction == IntentHelper.ACTION_DEFAULT) {
            setFragment(getFragment(mPosition));
        } else {
            setFragment(getActionFragment(IntentHelper.sAction));
        }
        IconsLoaderTask.start(this);
        checkWallpapers();
        IconRequestTask.start(this, AsyncTask.THREAD_POOL_EXECUTOR);


        if (Preferences.get(this).isFirstRun() && mConfig.isLicenseCheckerEnabled()) {
            mLicenseHelper = new LicenseHelper(this);
            mLicenseHelper.run(mConfig.getLicenseKey(), mConfig.getRandomString(), new LicenseCallbackHelper(this));
            return;
        }

        if (Preferences.get(this).isNewVersion())
            ChangelogFragment.showChangelog(mFragManager);

        if (mConfig.isLicenseCheckerEnabled() && !Preferences.get(this).isLicensed()) {
            finish();
        }
    }

    public void inicilisation() {
        MobileAds.initialize(this, getResources().getString(R.string.app_id));

        GDPRConsentState consentState = GDPR.getInstance().getConsentState();
        GDPRLocation location = consentState.getLocation(); // where has the given consent been given
        boolean canCollectPersonalInformation = GDPR.getInstance().canCollectPersonalInformation();

        if(location.name() == "UNDEFINED" || location.name() == "NOT_IN_EAA"){
            //Если пользователь не из ЕС то подписываем на уведомления

            OneSignal.startInit(this)
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .unsubscribeWhenNotificationsAreDisabled(true)
                    .init();


            //if (mInAppBilling.get().id== "your.product.id")
            mAdView = findViewById(R.id.adView);
            Bundle extras = new Bundle();
            extras.putString("npa", "0");
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);

            allowsPersonalAds = true;
        } else {

            mAdView = findViewById(R.id.adView);
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mAdView.loadAd(adRequest);
            if (canCollectPersonalInformation){
                //Если пользователь из ЕС и он согласен на сбор данных то подписываем на уведомления

                OneSignal.startInit(this)
                        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                        .unsubscribeWhenNotificationsAreDisabled(true)
                        .init();

                allowsPersonalAds = true;
            } else {

                allowsPersonalAds = false;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setLocale(this);
        if (mIsMenuVisible) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleHelper.setLocale(newBase);
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        int action = IntentHelper.getAction(intent);
        if (action != IntentHelper.ACTION_DEFAULT)
            setFragment(getActionFragment(action));
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        RequestHelper.checkPiracyApp(this);
        IntentHelper.sAction = IntentHelper.getAction(getIntent());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        InAppBillingProcessor.get(this).destroy();

        if (mLicenseHelper != null) {
            mLicenseHelper.destroy();
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        CandyBarMainActivity.sMissedApps = null;
        CandyBarMainActivity.sHomeIcon = null;
        stopService(new Intent(this, CandyBarService.class));
        Database.get(this.getApplicationContext()).closeDatabase();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extras.EXTRA_POSITION, mPosition);
        Database.get(this.getApplicationContext()).closeDatabase();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mFragManager.getBackStackEntryCount() > 0) {
            clearBackStack();
            return;
        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (!mFragmentTag.equals(Extras.TAG_HOME)) {
            mPosition = mLastPosition = 0;
            setFragment(getFragment(mPosition));
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!InAppBillingProcessor.get(this).handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCode.STORAGE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
                return;
            }
            Toast.makeText(this, R.string.permission_storage_denied, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPiracyAppChecked(boolean isPiracyAppInstalled) {
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.navigation_view_request);
        if (menuItem != null) {
            menuItem.setVisible(getResources().getBoolean(
                    R.bool.enable_icon_request) || !isPiracyAppInstalled);
        }
    }

    @Override
    public void onRequestSelected(int count) {
        if (mFragmentTag.equals(Extras.TAG_REQUEST)) {
            String title = getResources().getString(R.string.navigation_view_request);
            if (count > 0) title += " ("+ count +")";
            mToolbarTitle.setText(title);
        }
    }

    @Override
    public void onBuyPremiumRequest() {
        if (Preferences.get(this).isPremiumRequest()) {
            RequestHelper.showPremiumRequestStillAvailable(this);
            return;
        }

        if (InAppBillingProcessor.get(this.getApplicationContext())
                .getProcessor().loadOwnedPurchasesFromGoogle()) {
            List<String> products = InAppBillingProcessor.get(this).getProcessor().listOwnedProducts();
            if (products != null) {
                boolean isProductIdExist = false;
                for (String product : products) {
                    for (String premiumRequestProductId : mConfig.getPremiumRequestProductsId()) {
                        if (premiumRequestProductId.equals(product)) {
                            isProductIdExist = true;
                            break;
                        }
                    }
                }

                if (isProductIdExist) {
                    RequestHelper.showPremiumRequestExist(this);
                    return;
                }
            }
        }

        InAppBillingFragment.showInAppBillingDialog(getSupportFragmentManager(),
                InAppBilling.PREMIUM_REQUEST,
                mConfig.getLicenseKey(),
                mConfig.getPremiumRequestProductsId(),
                mConfig.getPremiumRequestProductsCount());
    }

    @Override
    public void onPremiumRequestBought() {
        if (mFragmentTag.equals(Extras.TAG_REQUEST)) {
            RequestFragment fragment = (RequestFragment) mFragManager.findFragmentByTag(Extras.TAG_REQUEST);
            if (fragment != null) fragment.refreshIconRequest();
        }
    }

    @Override
    public void onRequestBuilt(Intent intent, int type) {
        if (intent == null) {
            Toast.makeText(this, "Icon Request: Intent is null", Toast.LENGTH_LONG).show();
            return;
        }

        if (type == IntentChooserFragment.ICON_REQUEST) {
            if (RequestFragment.sSelectedRequests == null)
                return;

            if (getResources().getBoolean(R.bool.enable_icon_request_limit)) {
                int used = Preferences.get(this).getRegularRequestUsed();
                Preferences.get(this).setRegularRequestUsed((used + RequestFragment.sSelectedRequests.size()));
            }

            if (Preferences.get(this).isPremiumRequest()) {
                int count = Preferences.get(this).getPremiumRequestCount() - RequestFragment.sSelectedRequests.size();
                Preferences.get(this).setPremiumRequestCount(count);
                if (count == 0) {
                    if (InAppBillingProcessor.get(this).getProcessor().consumePurchase(Preferences
                            .get(this).getPremiumRequestProductId())) {
                        Preferences.get(this).setPremiumRequest(false);
                        Preferences.get(this).setPremiumRequestProductId("");
                    } else {
                        RequestHelper.showPremiumRequestConsumeFailed(this);
                        return;
                    }
                }
            }

            if (mFragmentTag.equals(Extras.TAG_REQUEST)) {
                RequestFragment fragment = (RequestFragment) mFragManager.findFragmentByTag(Extras.TAG_REQUEST);
                if (fragment != null) fragment.refreshIconRequest();
            }
        }

        try {
            startActivity(intent);
        } catch (IllegalArgumentException e) {
            startActivity(Intent.createChooser(intent,
                    getResources().getString(R.string.email_client)));
        }
        CandyBarApplication.sRequestProperty = null;
        CandyBarApplication.sZipPath = null;
    }

    @Override
    public void onRestorePurchases() {
        if (InAppBillingProcessor.get(this).getProcessor().loadOwnedPurchasesFromGoogle()) {
            List<String> productsId = InAppBillingProcessor.get(this).getProcessor().listOwnedProducts();
            if (productsId != null) {
                SettingsFragment fragment = (SettingsFragment) mFragManager.findFragmentByTag(Extras.TAG_SETTINGS);
                if (fragment != null) fragment.restorePurchases(productsId,
                        mConfig.getPremiumRequestProductsId(), mConfig.getPremiumRequestProductsCount());
            }
        }
    }

    @Override
    public void onInAppBillingSelected(int type, InAppBilling product) {
        Preferences.get(this).setInAppBillingType(type);
        if (type == InAppBilling.PREMIUM_REQUEST) {
            Preferences.get(this).setPremiumRequestCount(product.getProductCount());
            Preferences.get(this).setPremiumRequestTotal(product.getProductCount());
        }

        InAppBillingProcessor.get(this).getProcessor().purchase(this, product.getProductId());
    }

    @Override
    public void onInAppBillingConsume(int type, String productId) {
        if (InAppBillingProcessor.get(this).getProcessor().consumePurchase(productId)) {
            if (type == InAppBilling.DONATE) {
                new MaterialDialog.Builder(this)
                        .typeface(TypefaceHelper.getMedium(this),
                                TypefaceHelper.getRegular(this))
                        .title(R.string.navigation_view_donate)
                        .content(R.string.donation_success)
                        .positiveText(R.string.close)
                        .show();
            }
        }
    }
    @Override
    public void onInAppBillingRequest() {
        if (mFragmentTag.equals(Extras.TAG_REQUEST)) {
            RequestFragment fragment = (RequestFragment) mFragManager.findFragmentByTag(Extras.TAG_REQUEST);
            if (fragment != null) fragment.prepareRequest();
        }
    }

    @Override
    public void onWallpapersChecked(@Nullable Intent intent) {
        if (intent != null) {
            String packageName = intent.getStringExtra("packageName");
            LogUtil.d("Broadcast received from service with packageName: " +packageName);

            if (packageName == null)
                return;

            if (!packageName.equals(getPackageName())) {
                LogUtil.d("Received broadcast from different packageName, expected: " +getPackageName());
                return;
            }

            int size = intent.getIntExtra(Extras.EXTRA_SIZE, 0);
            int offlineSize = Database.get(this).getWallpapersCount();
            Preferences.get(this).setAvailableWallpapersCount(size);

            if (size > offlineSize) {
                if (mFragmentTag.equals(Extras.TAG_HOME)) {
                    HomeFragment fragment = (HomeFragment) mFragManager.findFragmentByTag(Extras.TAG_HOME);
                    if (fragment != null) fragment.resetWallpapersCount();
                }

                int accent = ColorHelper.getAttributeColor(this, R.attr.colorAccent);
                LinearLayout container = (LinearLayout) mNavigationView.getMenu().getItem(4).getActionView();
                if (container != null) {
                    TextView counter = container.findViewById(R.id.counter);
                    if (counter == null) return;

                    ViewCompat.setBackground(counter, DrawableHelper.getTintedDrawable(this,
                            R.drawable.ic_toolbar_circle, accent));
                    counter.setTextColor(ColorHelper.getTitleTextColor(accent));
                    int newItem = (size - offlineSize);
                    counter.setText(String.valueOf(newItem > 99 ? "99+" : newItem));
                    container.setVisibility(View.VISIBLE);




                    int heightPixels = AdSize.SMART_BANNER.getHeightInPixels(this);

                    //setLayoutParams(new FrameLayout.LayoutParams(100,100);
                    return;
                }
            }
        }

        LinearLayout container = (LinearLayout) mNavigationView.getMenu().getItem(4).getActionView();
        if (container != null) container.setVisibility(View.GONE);
    }

    @Override
    public void onSearchExpanded(boolean expand) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mIsMenuVisible = !expand;

        if (expand) {
            int color = ColorHelper.getAttributeColor(this, R.attr.toolbar_icon);
            toolbar.setNavigationIcon(DrawableHelper.getTintedDrawable(
                    this, R.drawable.ic_toolbar_back, color));
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        } else {
            SoftKeyboardHelper.closeKeyboard(this);
            ColorHelper.setStatusBarColor(this, Color.TRANSPARENT, true);
            if (CandyBarApplication.getConfiguration().getNavigationIcon() == CandyBarApplication.NavigationIcon.DEFAULT) {
                mDrawerToggle.setDrawerArrowDrawable(new DrawerArrowDrawable(this));
            } else {
                toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(this,
                        CandyBarApplication.getConfiguration().getNavigationIcon()));
            }

            toolbar.setNavigationOnClickListener(view ->
                    mDrawerLayout.openDrawer(GravityCompat.START));
        }

        mDrawerLayout.setDrawerLockMode(expand ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED :
                DrawerLayout.LOCK_MODE_UNLOCKED);
        supportInvalidateOptionsMenu();
    }

    public void showSupportDevelopmentDialog() {
        InAppBillingFragment.showInAppBillingDialog(mFragManager,
                InAppBilling.DONATE,
                mConfig.getLicenseKey(),
                mConfig.getDonationProductsId(),
                null);
    }






    private void initNavigationView(Toolbar toolbar) {
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.txt_open, R.string.txt_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                SoftKeyboardHelper.closeKeyboard(CandyBarMainActivity.this);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                selectPosition(mPosition);
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(ConfigurationHelper.getNavigationIcon(this,
                CandyBarApplication.getConfiguration().getNavigationIcon()));
        toolbar.setNavigationOnClickListener(view ->
                mDrawerLayout.openDrawer(GravityCompat.START));

        if (CandyBarApplication.getConfiguration().getNavigationIcon() == CandyBarApplication.NavigationIcon.DEFAULT) {
            DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
            drawerArrowDrawable.setColor(ColorHelper.getAttributeColor(this, R.attr.toolbar_icon));
            drawerArrowDrawable.setSpinEnabled(true);
            mDrawerToggle.setDrawerArrowDrawable(drawerArrowDrawable);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationViewHelper.initApply(mNavigationView);
        NavigationViewHelper.initIconRequest(mNavigationView);
        NavigationViewHelper.initWallpapers(mNavigationView);

        ColorStateList itemStateList = ContextCompat.getColorStateList(this,
                Preferences.get(this).isDarkTheme() ?
                        R.color.navigation_view_item_highlight_dark :
                        R.color.navigation_view_item_highlight);
        mNavigationView.setItemTextColor(itemStateList);
        mNavigationView.setItemIconTintList(itemStateList);
        Drawable background = ContextCompat.getDrawable(this,
                Preferences.get(this).isDarkTheme() ?
                        R.drawable.navigation_view_item_background_dark :
                        R.drawable.navigation_view_item_background);
        mNavigationView.setItemBackground(background);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_view_home) mPosition = 0;
            else if (id == R.id.navigation_view_apply) mPosition = 1;
            else if (id == R.id.navigation_view_icons) mPosition = 2;
            else if (id == R.id.navigation_view_request) mPosition = 3;
            else if (id == R.id.navigation_view_wallpapers) mPosition = 4;
            else if (id == R.id.navigation_view_settings) mPosition = 5;
            else if (id == R.id.navigation_view_faqs) mPosition = 6;
            else if (id == R.id.navigation_view_about) mPosition = 7;

            else if (id == R.id.navigation_view_instagram) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://goo.gl/HVafCj"));
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                return false;
            }
/*
            else if (id == R.id.navigation_view_vk) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://goo.gl/qdPmfU"));
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
                return false;
            }
*/

            item.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        });

        NavigationViewHelper.hideScrollBar(mNavigationView);
    }

    private void initNavigationViewHeader() {
        if (CandyBarApplication.getConfiguration().getNavigationViewHeader() == CandyBarApplication.NavigationViewHeader.NONE) {
            mNavigationView.removeHeaderView(mNavigationView.getHeaderView(0));
            return;
        }

        String imageUrl = getResources().getString(R.string.navigation_view_header);
        String titleText = getResources().getString(R.string.navigation_view_header_title);
        View header = mNavigationView.getHeaderView(0);
        HeaderView image = header.findViewById(R.id.header_image);
        LinearLayout container = header.findViewById(R.id.header_title_container);
        TextView title = header.findViewById(R.id.header_title);
        TextView version = header.findViewById(R.id.header_version);

        if (CandyBarApplication.getConfiguration().getNavigationViewHeader() == CandyBarApplication.NavigationViewHeader.MINI) {
            image.setRatio(16, 9);
        }

        if (titleText.length() == 0) {
            container.setVisibility(View.GONE);
        } else {
            title.setText(titleText);
            try {
                String versionText = "v" + getPackageManager()
                        .getPackageInfo(getPackageName(), 0).versionName;
                version.setText(versionText);
            } catch (Exception ignored) {}
        }

        if (ColorHelper.isValidColor(imageUrl)) {
            image.setBackgroundColor(Color.parseColor(imageUrl));
            return;
        }

        if (!URLUtil.isValidUrl(imageUrl)) {
            imageUrl = "drawable://" + DrawableHelper.getResourceId(this, imageUrl);
        }

        ImageLoader.getInstance().displayImage(imageUrl, new ImageViewAware(image),
                ImageConfig.getDefaultImageOptions(true), new ImageSize(720, 720), null, null);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(CandyBarBroadcastReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mReceiver = new CandyBarBroadcastReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void checkWallpapers() {
        if (Preferences.get(this).isConnectedToNetwork()) {
            Intent intent = new Intent(this, CandyBarWallpapersService.class);
            startService(intent);
            return;
        }

        int size = Preferences.get(this).getAvailableWallpapersCount();
        if (size > 0) {
            onWallpapersChecked(new Intent()
                    .putExtra("size", size)
                    .putExtra("packageName", getPackageName()));
        }
    }

    private void clearBackStack() {
        if (mFragManager.getBackStackEntryCount() > 0) {
            mFragManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            onSearchExpanded(false);
        }
    }

    public void selectPosition(int position) {
        if (position == 3) {
            if (!getResources().getBoolean(R.bool.enable_icon_request) &&
                    getResources().getBoolean(R.bool.enable_premium_request)) {
                if (!Preferences.get(this).isPremiumRequestEnabled())
                    return;

                if (!Preferences.get(this).isPremiumRequest()) {
                    mPosition = mLastPosition;
                    mNavigationView.getMenu().getItem(mPosition).setChecked(true);
                    onBuyPremiumRequest();
                    return;
                }
            }
        }

        if (position == 4) {
            if (WallpaperHelper.getWallpaperType(this)
                    == WallpaperHelper.EXTERNAL_APP) {
                mPosition = mLastPosition;
                mNavigationView.getMenu().getItem(mPosition).setChecked(true);
                WallpaperHelper.launchExternalApp(CandyBarMainActivity.this);
                return;
            }
        }

        if (position != mLastPosition) {
            mLastPosition = mPosition = position;
            setFragment(getFragment(position));
        }
    }

    private void setFragment(Fragment fragment) {
        clearBackStack();

        FragmentTransaction ft = mFragManager.beginTransaction()
                .replace(R.id.container, fragment, mFragmentTag);
        try {
            ft.commit();
        } catch (Exception e) {
            ft.commitAllowingStateLoss();
        }

        Menu menu = mNavigationView.getMenu();
        menu.getItem(mPosition).setChecked(true);
        mToolbarTitle.setText(menu.getItem(mPosition).getTitle());
    }

    private Fragment getFragment(int position) {
        mFragmentTag = Extras.TAG_HOME;
        if (position == 0) {
            mFragmentTag = Extras.TAG_HOME;
            return new HomeFragment();
        } else if (position == 1) {
            mFragmentTag = Extras.TAG_APPLY;
            return new ApplyFragment();
        } else if (position == 2) {
            mFragmentTag = Extras.TAG_ICONS;
            return new IconsBaseFragment();
        } else if (position == 3) {
            mFragmentTag = Extras.TAG_REQUEST;
            return new RequestFragment();
        } else if (position == 4) {
            mFragmentTag = Extras.TAG_WALLPAPERS;
            return new WallpapersFragment();
        } else if (position == 5) {
            mFragmentTag = Extras.TAG_SETTINGS;
            return new SettingsFragment();
        } else if (position == 6) {
            mFragmentTag = Extras.TAG_FAQS;
            return new FAQsFragment();
        } else if (position == 7) {
            mFragmentTag = Extras.TAG_ABOUT;
            return new AboutFragment();
        }
        return new HomeFragment();
    }

    private Fragment getActionFragment(int action) {
        switch (action) {
            case IntentHelper.ICON_PICKER :
            case IntentHelper.IMAGE_PICKER :
                mPosition = mLastPosition = 2;
                mFragmentTag = Extras.TAG_ICONS;
                return new IconsBaseFragment();
            case IntentHelper.WALLPAPER_PICKER :
                if (WallpaperHelper.getWallpaperType(this) == WallpaperHelper.CLOUD_WALLPAPERS) {
                    mPosition = mLastPosition = 4;
                    mFragmentTag = Extras.TAG_WALLPAPERS;
                    return new WallpapersFragment();
                }
            default :
                mPosition = mLastPosition = 0;
                mFragmentTag = Extras.TAG_HOME;
                return new HomeFragment();
        }
    }
}
