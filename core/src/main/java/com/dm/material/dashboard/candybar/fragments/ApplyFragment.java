package com.dm.material.dashboard.candybar.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dm.material.dashboard.candybar.R;
import com.dm.material.dashboard.candybar.adapters.LauncherAdapter;
import com.dm.material.dashboard.candybar.applications.CandyBarApplication;
import com.dm.material.dashboard.candybar.items.Icon;
import com.dm.material.dashboard.candybar.preferences.Preferences;
import com.dm.material.dashboard.candybar.utils.AlphanumComparator;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.michaelflisar.gdprdialog.GDPR;
import com.michaelflisar.gdprdialog.GDPRConsentState;
import com.michaelflisar.gdprdialog.GDPRLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class ApplyFragment extends Fragment{
    private InterstitialAd mInterstitialAd;
    private RecyclerView mRecyclerView;
    private AsyncTask mAsyncTask;
    boolean allowsPersonalAds = true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);


        GDPRConsentState consentState = GDPR.getInstance().getConsentState();
        GDPRLocation location = consentState.getLocation(); // where has the given consent been given
        boolean canCollectPersonalInformation = GDPR.getInstance().canCollectPersonalInformation();

        if(location.name() == "UNDEFINED" || location.name() == "NOT_IN_EAA"){
            allowsPersonalAds = true;
        } else {

            if (canCollectPersonalInformation){
                allowsPersonalAds = true;
            } else {
                allowsPersonalAds = false;
            }
        }
        if(allowsPersonalAds){
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(getContext().getResources().getString(R.string.interstitialad_id));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    displayInterstitial();
                    mAsyncTask = new LaunchersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    mAsyncTask = new LaunchersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                @Override
                public void onAdOpened() {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    mAsyncTask = new LaunchersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        } else {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId(getContext().getResources().getString(R.string.interstitialad_id));
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    displayInterstitial();
                    mAsyncTask = new LaunchersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    mAsyncTask = new LaunchersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                @Override
                public void onAdOpened() {
                    view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    mAsyncTask = new LaunchersLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        }




        if (!Preferences.get(getActivity()).isToolbarShadowEnabled()) {
            View shadow = view.findViewById(R.id.shadow);
            if (shadow != null) shadow.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.apply_column_count)));

        if (CandyBarApplication.getConfiguration().getApplyGrid() == CandyBarApplication.GridStyle.FLAT) {
            int padding = getActivity().getResources().getDimensionPixelSize(R.dimen.card_margin);
            mRecyclerView.setPadding(padding, padding, 0, 0);
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resetSpanSizeLookUp();
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    private void resetSpanSizeLookUp() {
        int column = getActivity().getResources().getInteger(R.integer.apply_column_count);
        LauncherAdapter adapter = (LauncherAdapter) mRecyclerView.getAdapter();
        GridLayoutManager manager = (GridLayoutManager) mRecyclerView.getLayoutManager();

        try {
            manager.setSpanCount(column);

            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == adapter.getFirstHeaderPosition() || position == adapter.getLastHeaderPosition())
                        return column;
                    return 1;
                }
            });
        } catch (Exception ignored) {}
    }

    private boolean isPackageInstalled(String pkg) {
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(
                    pkg, PackageManager.GET_ACTIVITIES);
            return packageInfo != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLauncherInstalled(String pkg1, String pkg2, String pkg3) {
        return isPackageInstalled(pkg1) | isPackageInstalled(pkg2) | isPackageInstalled(pkg3);
    }

    private boolean isLauncherShouldBeAdded(String packageName) {
        if (("com.dlto.atom.launcher").equals(packageName)) {
            int id = getResources().getIdentifier("appmap", "xml", getActivity().getPackageName());
            if (id <= 0) return false;
        } else if (("com.lge.launcher2").equals(packageName) ||
                ("com.lge.launcher3").equals(packageName)) {
            int id = getResources().getIdentifier("theme_resources", "xml", getActivity().getPackageName());
            if (id <= 0) return false;
        }
        return true;
    }

    private class LaunchersLoader extends AsyncTask<Void, Void, Boolean> {

        private List<Icon> launchers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            launchers = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    String[] launcherNames = getResources().getStringArray(
                            R.array.launcher_names);
                    TypedArray launcherIcons = getResources().obtainTypedArray(
                            R.array.launcher_icons);
                    String[] launcherPackages1 = getResources().getStringArray(
                            R.array.launcher_packages_1);
                    String[] launcherPackages2 = getResources().getStringArray(
                            R.array.launcher_packages_2);
                    String[] launcherPackages3 = getResources().getStringArray(
                            R.array.launcher_packages_3);

                    List<Icon> installed = new ArrayList<>();
                    List<Icon> supported = new ArrayList<>();

                    for (int i = 0; i < launcherNames.length; i++) {
                        boolean isInstalled = isLauncherInstalled(
                                launcherPackages1[i],
                                launcherPackages2[i],
                                launcherPackages3[i]);

                        int icon = R.drawable.ic_app_default;
                        if (i < launcherIcons.length())
                            icon = launcherIcons.getResourceId(i, icon);

                        String launcherPackage = launcherPackages1[i];
                        if (launcherPackages1[i].equals("com.lge.launcher2")) {
                            boolean lghome3 = isPackageInstalled(launcherPackages2[i]);
                            if (lghome3) launcherPackage = launcherPackages2[i];
                        }

                        Icon launcher = new Icon(launcherNames[i], icon, launcherPackage);
                        if (isLauncherShouldBeAdded(launcherPackage)) {
                            if (isInstalled) installed.add(launcher);
                            else supported.add(launcher);
                        }
                    }

                    try {
                        Collections.sort(installed, new AlphanumComparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                String s1 = ((Icon) o1).getTitle();
                                String s2 = ((Icon) o2).getTitle();
                                return super.compare(s1, s2);
                            }
                        });
                    } catch (Exception ignored) {}

                    try {
                        Collections.sort(supported, new AlphanumComparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                String s1 = ((Icon) o1).getTitle();
                                String s2 = ((Icon) o2).getTitle();
                                return super.compare(s1, s2);
                            }
                        });
                    } catch (Exception ignored) {}

                    if (installed.size() > 0) {
                        launchers.add(new Icon(getResources().getString(
                                R.string.apply_installed), -1, null));
                    }

                    launchers.addAll(installed);
                    launchers.add(new Icon(getResources().getString(
                            R.string.apply_supported), -2, null));
                    launchers.addAll(supported);

                    launcherIcons.recycle();
                    return true;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
            if (aBoolean) {
                mRecyclerView.setAdapter(new LauncherAdapter(getActivity(), launchers));
                resetSpanSizeLookUp();
            }
        }

    }
    public void displayInterstitial() {
        //if (Preferences.get(getActivity()).isPremiumRequest()) {

        //} else {

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        //}
    }
}
