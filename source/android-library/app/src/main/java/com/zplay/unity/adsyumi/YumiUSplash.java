package com.zplay.unity.adsyumi;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSplash;
import com.yumi.android.sdk.ads.publish.listener.IYumiSplashListener;

public class YumiUSplash {
    private final String TAG = "zplayPluginActivity";

    /**
     * The {@link YumiSplash}.
     */
    private YumiSplash mYumiSplash;
    /**
     * The {@code Activity} on which the splash will display.
     */
    private Activity activity;

    /**
     * A listener implemented in Unity via {@code AndroidJavaProxy} to receive ad events.
     */
    private YumiUSplashListener mUnityListener;

    private FrameLayout mSplashContainer;
    public YumiUSplash(Activity activity, YumiUSplashListener listener){
        this.activity = activity;
        this.mUnityListener = listener;
    }

    /**
     * Creates an view  to hold banner ads.
     */
    public void create(final String placementId, final String channelId, final String versionId, final int fetchTime, final double adBottomViewHeight) {
        Log.d(TAG, "create Splash");

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                createSplashContainer(activity, adBottomViewHeight);

                mYumiSplash = new YumiSplash(activity, mSplashContainer, placementId);
                mYumiSplash.setChannelID(channelId);
                mYumiSplash.setVersionName(versionId);
                mYumiSplash.setFetchTime(fetchTime);
                mYumiSplash.setSplashListener(new IYumiSplashListener() {
                    @Override
                    public void onSplashAdSuccessToShow() {
                        Log.d(TAG, "on splash ad show success");
                        if (mUnityListener != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mUnityListener != null) {
                                        mUnityListener.onAdSuccessToShow();
                                    }
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onSplashAdFailToShow(final AdError error) {
                        Log.d(TAG, "on splash ad show fail" +  error);
                        removeSplashView();
                        if (mUnityListener != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mUnityListener != null) {
                                        mUnityListener.onAdFailedToShow(error.toString());
                                    }
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onSplashAdClicked() {
                        Log.d(TAG, "on splash ad clicked");
                        if (mUnityListener != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mUnityListener != null) {
                                        mUnityListener.onAdClicked();
                                    }
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onSplashAdClosed() {
                        Log.d(TAG, "on splash ad closed");
                        removeSplashView();
                        if (mUnityListener != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (mUnityListener != null) {
                                        mUnityListener.onAdClosed();
                                    }
                                }
                            }).start();
                        }
                    }
                });
            }
        });
    }


    public void loadAdAndShow(){
       activity.runOnUiThread(new Runnable() {
               @Override
               public void run() {
               Log.d(TAG, "splash loadAdAndShow");
               if(mYumiSplash != null){
                   Log.d(TAG, "splash loadAdAndShow start");
                   mSplashContainer.setVisibility(ViewGroup.VISIBLE);
                   mSplashContainer.bringToFront();
                   mYumiSplash.loadAdAndShowInWindow();
               }
           }
       });
    }

    public void destroy(){
        Log.d(TAG, "splash destroy");
        if(mYumiSplash != null){
            mYumiSplash = null;
        }
    }


    private void createSplashContainer(Activity activity,final double adBottomViewHeight){
        try{
        mSplashContainer = new FrameLayout(activity);
        Log.d(TAG, "create splashContainer");

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.bottomMargin = (int)adBottomViewHeight;
        activity.getWindow().addContentView(mSplashContainer, params);
            mSplashContainer.bringToFront();
        }catch (Exception e){
            Log.e(TAG, "on createSplashContainer error : " + e);
        }
    }

    private void removeSplashView(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "remove splash view");
                if(mSplashContainer !=null){
                    mSplashContainer.setVisibility(ViewGroup.GONE);
                }
            }
        });

    }
}
