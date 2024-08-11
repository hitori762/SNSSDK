package com.android.loginsocialnetworklibrary;

import android.content.Intent;

import com.android.listener.GetUserInfoListener;
import com.android.listener.LogOutListener;
import com.android.listener.LoginSNSListener;

interface SNSSDKInterface {

    public void initFacebookSDK(String fbAppId);

//    public void initTwitterSDK(String twitterKey, String twitterSecret);

    public void initGoogleSignIn(String clientId);

//    public void initYahooSDK(String clientId, String customUriScheme);

    public void onActivityResult(int requestCode, int resultCode, Intent data);

    public void loginSNS(int loginType, LoginSNSListener loginSNSListener);

    public void logoutSNS(LogOutListener logOutListener);

    public void getUserInfoData(GetUserInfoListener getUserInfoListener);
}
