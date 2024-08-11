package com.android.loginsocialnetworklibrary;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.android.listener.GetUserInfoListener;
import com.android.listener.LogOutListener;
import com.android.listener.LoginSNSListener;

public class SNSSDKManager implements SNSSDKInterface {

    private static SNSSDK mSNSSDK;

    public SNSSDKManager(FragmentActivity fragmentActivity) {
        getInstance(fragmentActivity);
    }

    /**
     * Singleton SNSSDK.
     *
     * @param fragmentActivity
     * @return
     */
    public static SNSSDK getInstance(FragmentActivity fragmentActivity) {
        if (mSNSSDK == null) {
            {
                synchronized (SNSSDK.class) {
                    SNSSDK inst = mSNSSDK;
                    if (inst == null) {
                        synchronized (SNSSDK.class) {
                            mSNSSDK = new SNSSDK(fragmentActivity);
                            return mSNSSDK;
                        }
                    }
                }
            }
        }
        return mSNSSDK;
    }


    /**
     * Release singleton object.
     */
    public void releaseLoginSocialNetwork() {
        mSNSSDK = null;
    }

    /**
     * Initialize the Facebook SDK before executing any other operations, especially, if you're using Facebook UI elements.
     * Initialize facebook login button.
     *
     * @param fbAppId
     */
    @Override
    public void initFacebookSDK(String fbAppId) {
        mSNSSDK.initFacebookSDK(fbAppId);
    }

    /**
     * Initialize the Twitter SDK before executing any other operations, especially, if you're using Twitter UI elements.
     *
     * @param twitterKey
     * @param twitterSecret
     */
//    @Override
//    public void initTwitterSDK(String twitterKey, String twitterSecret) {
//        mSNSSDK.initTwitterSDK(twitterKey, twitterSecret);
//    }

    /**
     * Initialize the Google sign in before executing any other operations, especially, if you're using Google sign in UI elements.
     */
    @Override
    public void initGoogleSignIn(String clientId) {
        mSNSSDK.initGoogleSignIn(clientId);
    }

//    @Override
//    public void initYahooSDK(String clientId, String customUriScheme) {
//        mSNSSDK.initYahooSDK(clientId, customUriScheme);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSNSSDK.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Log in to social network.
     * loginType: type of social network.
     * loginSNSListener: callback after do log in.
     *
     * @param loginType
     * @param loginSNSListener
     */
    @Override
    public void loginSNS(int loginType, LoginSNSListener loginSNSListener) {
        mSNSSDK.loginSNS(loginType, loginSNSListener);
    }

    /**
     * Log out social network.
     * logOutListener: callback after do log out.
     *
     * @param logOutListener
     */
    @Override
    public void logoutSNS(LogOutListener logOutListener) {
        mSNSSDK.logoutSNS(logOutListener);
    }

    /**
     * Get user information from social network.
     * getUserInfoListener: callback after get user information data.
     *
     * @param getUserInfoListener
     */
    @Override
    public void getUserInfoData(GetUserInfoListener getUserInfoListener) {
        mSNSSDK.getUserInfoData(getUserInfoListener);
    }
}
