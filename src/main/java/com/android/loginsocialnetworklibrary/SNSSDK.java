package com.android.loginsocialnetworklibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.android.common.Constants;
import com.android.common.PreferenceUtil;
import com.android.listener.GetUserInfoListener;
import com.android.listener.LogOutListener;
import com.android.listener.LoginSNSListener;
import com.android.model.UserSNSInfo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SNSSDK implements SNSSDKInterface {

    private FragmentActivity mFragmentAcvitivy;

    // Use for facebook.
    private CallbackManager mCallbackManager;
    private LoginButton mFacebookLoginBtn;
    // End

    // Use for twitter.
//    private TwitterLoginButton mTwitterLoginBtn;
    // End

    // Use for Google sign in
//    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleLoginBtn;
    private static final int RC_SIGN_IN = 9001;
    private LoginSNSListener mGoogleLoginListener;
    // End

    // Use for yahoo
//    private String clientId;
//    private String customUriScheme;
//    public static final int RC_LOG_IN_YAHOO = 9002;
//    public static final int RC_LOG_IN_YAHOO_FAIL = 9003;
//    public static final int RC_GET_YAHOO_USER_INFO = 9004;
//    public static final int RC_GET_YAHOO_USER_INFO_FAIL = 9005;
//    private LoginSNSListener mYahooLoginListener;
//    private GetUserInfoListener mGetYahooUserInfoListener;
    //End

    /**
     * Constructor.
     */
    public SNSSDK(FragmentActivity fragmentActivity) {
        this.mFragmentAcvitivy = fragmentActivity;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handleActivityResult(requestCode, resultCode, data);
    }

    private void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (mCallbackManager != null) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
//        if (mTwitterLoginBtn != null) {
//            mTwitterLoginBtn.onActivityResult(requestCode, resultCode, data);
//        }
        if (mGoogleSignInClient != null) {
            if (requestCode == RC_SIGN_IN) {
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                handleSignInGoogleResult(result);

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInGoogleResult(task);
            }
        }
//        if (mYahooLoginListener != null) {
//            if (resultCode == RC_LOG_IN_YAHOO) {
//                UserSNSInfo user = (UserSNSInfo) data.getSerializableExtra(YConnectYahooWebviewActivity.USER_DATA);
//                mYahooLoginListener.onLoginSuccess(user);
//            } else if (resultCode == RC_LOG_IN_YAHOO_FAIL) {
//                mYahooLoginListener.onLoginError(data.getStringExtra(YConnectYahooWebviewActivity.EXCEPTION));
//            }
//        }
    }

    //region Facebook API.

    /**
     * Initialize the Facebook SDK before executing any other operations, especially, if you're using Facebook UI elements.
     * Initialize facebook login button.
     *
     * @param fbAppId
     */

    public void initFacebookSDK(String fbAppId) {
        doInitFbSDK(fbAppId);
    }

    private void doInitFbSDK(String fbAppId) {
        mCallbackManager = CallbackManager.Factory.create();
        FacebookSdk.setApplicationId(fbAppId);
        PreferenceUtil.writeBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_FACEBOOK_SDK, true);
        mFacebookLoginBtn = new LoginButton(mFragmentAcvitivy);
        mFacebookLoginBtn.setReadPermissions(Arrays.asList("public_profile", "email"));
    }

    /**
     * Handle on click button login to Facebook.
     *
     * @param loginSNSListener
     */
    private void loginFacebook(final LoginSNSListener loginSNSListener) {
        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_FACEBOOK_SDK, false)) {
            LoginManager.getInstance().logOut();

            mFacebookLoginBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_FACEBOOK);
                    final UserSNSInfo user = new UserSNSInfo();
                    user.setUserId(loginResult.getAccessToken().getUserId());
                    user.setToken(loginResult.getAccessToken().getToken());
                    user.setAvatarUrl(String.format(mFragmentAcvitivy.getString(R.string.fb_avatar_url),
                            loginResult.getAccessToken().getUserId()));

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            getFacebookData(object, user);
                            loginSNSListener.onLoginSuccess(user);
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name, email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    loginSNSListener.onLoginError("Cancel Facebook.");
                }

                @Override
                public void onError(FacebookException error) {
                    error.printStackTrace();
                    loginSNSListener.onLoginError(error.getMessage());
                }
            });
            mFacebookLoginBtn.performClick();
        } else {
            loginSNSListener.onLoginError("You are not init facebook sdk");
        }
    }

    /**
     * Do log out facebook.
     *
     * @param logOutListener
     */
    private void logOutFacebook(LogOutListener logOutListener) {
        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_FACEBOOK_SDK, false)) {
            LoginManager.getInstance().logOut();
            logOutListener.onLogOut();
            PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, com.android.common.Constants.TYPE_DEFAULT);
        }
    }

    /**
     * Get facebook account information from json object response.
     *
     * @param object
     * @param loginSNSResult
     */
    private void getFacebookData(JSONObject object, UserSNSInfo loginSNSResult) {
        try {
            if (object.has("name")) {
                String name = "";
                name = object.getString("name");
                loginSNSResult.setUserName(name);
            }
            if (object.has("id")) {
                String id = "";
                id = object.getString("id");
                loginSNSResult.setUserId(id);
                if (!TextUtils.isEmpty(id)) {
                    loginSNSResult.setAvatarUrl(String.format(mFragmentAcvitivy.getString(R.string.fb_avatar_url), id));
                }
            }
            if (object.has("email")) {
                String email = "";
                email = object.getString("email");
                loginSNSResult.setEmail(email);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get facebook account info from current access token.
     *
     * @param getUserInfoListener
     */
    private void getFacebookAccountInfo(final GetUserInfoListener getUserInfoListener) {
        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_FACEBOOK_SDK, false)) {
            final AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        UserSNSInfo user = new UserSNSInfo();
                        user.setToken(accessToken.getToken());
                        getFacebookData(object, user);
                        getUserInfoListener.onSuccess(user);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
            } else {
                getUserInfoListener.onFail("Facebook access token is null.");
                resetLoginInfo();
            }
        } else {
            getUserInfoListener.onFail("You are not init facebook sdk");
        }
    }
    //endregion

    //region Twitter API.
    /**
     * Initialize the Twitter SDK before executing any other operations, especially, if you're using Twitter UI elements.
     *
     * @param twitterKey
     * @param twitterSecret
     */
//    public void initTwitterSDK(String twitterKey, String twitterSecret) {
//        TwitterAuthConfig authConfig = new TwitterAuthConfig(twitterKey, twitterSecret);
//        Fabric.with(mFragmentAcvitivy, new Twitter(authConfig));
//        PreferenceUtil.writeBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_TWITTER_SDK, true);
//        mTwitterLoginBtn = new TwitterLoginButton(mFragmentAcvitivy);
//    }

    /**
     * Get twitter account info from active session.
     *
     * @param getUserInfoListener
     */
//    private void getTwitterAccountInfo(final GetUserInfoListener getUserInfoListener) {
//        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_TWITTER_SDK, false)) {
//            final TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
//            if (twitterSession != null) {
//                final UserSNSInfo userData = new UserSNSInfo();
//                if (twitterSession != null) {
//                    userData.setUserId("" + twitterSession.getUserId());
//                    userData.setUserName(twitterSession.getUserName());
//                    userData.setToken(twitterSession.getAuthToken().token);
//                    Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<com.twitter.sdk.android.core.models.UserSNSInfo>() {
//                        @Override
//                        public void success(Result<com.twitter.sdk.android.core.models.UserSNSInfo> result) {
//                            com.twitter.sdk.android.core.models.UserSNSInfo user = result.data;
//                            userData.setAvatarUrl(user.profileImageUrl.replace("_normal", ""));
//                            userData.setEmail(user.email);
//                            getUserInfoListener.onSuccess(userData);
//                        }
//
//                        @Override
//                        public void failure(TwitterException e) {
//                            try {
//                                getUserInfoListener.onSuccess(userData);
//                            } catch (Exception ex) {
//                                e.printStackTrace();
//                                ex.printStackTrace();
//                            }
//
//                        }
//                    });
//                }
//            } else {
//                getUserInfoListener.onFail("Twitter active session is null.");
//                resetLoginInfo();
//            }
//        } else {
//            getUserInfoListener.onFail("You are not init twitter sdk.");
//            resetLoginInfo();
//        }
//    }

    /**
     * Handle on click button login to Twitter.
     *
     * @param loginSNSListener
     */
//    private void loginTwitter(final LoginSNSListener loginSNSListener) {
//        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_TWITTER_SDK, false)) {
//            mTwitterLoginBtn.setCallback(new Callback<TwitterSession>() {
//                @Override
//                public void success(Result<TwitterSession> result) {
//                    PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_TWITTER);
//                    final UserSNSInfo userData = new UserSNSInfo();
//                    userData.setUserId("" + result.data.getUserId());
//                    userData.setUserName(result.data.getUserName());
//                    userData.setToken(result.data.getAuthToken().token);
//                    final TwitterSession session =
//                            Twitter.getSessionManager().getActiveSession();
//                    Twitter.getApiClient().getAccountService().verifyCredentials(true, false, new Callback<com.twitter.sdk.android.core.models.UserSNSInfo>() {
//                        @Override
//                        public void success(Result<com.twitter.sdk.android.core.models.UserSNSInfo> result) {
//                            com.twitter.sdk.android.core.models.UserSNSInfo user = result.data;
//                            userData.setAvatarUrl(user.profileImageUrl.replace("_normal", ""));
//                            loginSNSListener.onLoginSuccess(userData);
//                        }
//
//                        @Override
//                        public void failure(TwitterException e) {
//                            try {
//                                loginSNSListener.onLoginSuccess(userData);
//                            } catch (Exception ex) {
//                                e.printStackTrace();
//                                ex.printStackTrace();
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void failure(TwitterException e) {
//                    loginSNSListener.onLoginError(e.getMessage());
//                }
//            });
//            mTwitterLoginBtn.performClick();
//        } else {
//            loginSNSListener.onLoginError("You are not init twitter sdk.");
//        }
//    }

    /**
     * Do log out Twitter.
     *
     //     * @param logOutListener
     */
//    private void logOutTwitter(LogOutListener logOutListener) {
//        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_TWITTER_SDK, false)) {
//            TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
//            if (twitterSession != null) {
//                clearCookies(mFragmentAcvitivy);
//                Twitter.getSessionManager().clearActiveSession();
//                Twitter.logOut();
//                logOutListener.onLogOut();
//                PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, com.android.common.Constants.TYPE_DEFAULT);
//            }
//        }
//    }
//    private void clearCookies(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            CookieManager.getInstance().removeAllCookies(null);
//            CookieManager.getInstance().flush();
//        } else {
//            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
//            cookieSyncMngr.startSync();
//            CookieManager cookieManager = CookieManager.getInstance();
//            cookieManager.removeAllCookie();
//            cookieManager.removeSessionCookie();
//            cookieSyncMngr.stopSync();
//            cookieSyncMngr.sync();
//        }
//    }

    //endregion

    //region Google API.

    /**
     * Initialize the Google sign in before executing any other operations, especially, if you're using Google sign in UI elements.
     */
    public void initGoogleSignIn(String clientId) {
        doInitGG(clientId);
    }

    private void doInitGG(String clientId) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile().requestEmail().requestServerAuthCode(clientId)
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
//        mGoogleApiClient = new GoogleApiClient.Builder(mFragmentAcvitivy)
//                .enableAutoManage(mFragmentAcvitivy, new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//                    }
//                })
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mGoogleSignInClient = GoogleSignIn.getClient(mFragmentAcvitivy, gso);
        mGoogleLoginBtn = new SignInButton(mFragmentAcvitivy);
//        mGoogleLoginBtn.setScopes(gso.getScopeArray());

        PreferenceUtil.writeBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_GOOGLE_SDK, true);
    }

    /**
     * Handle on click button login to Google.
     *
     * @param loginSNSListener
     */
    private void loginGoogle(final LoginSNSListener loginSNSListener) {
        mGoogleLoginListener = loginSNSListener;
        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_GOOGLE_SDK, false)) {
//            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            mFragmentAcvitivy.startActivityForResult(signInIntent, RC_SIGN_IN);
            mGoogleLoginBtn.performClick();
        } else {
            mGoogleLoginListener.onLoginError("You are not init google sdk");
        }
    }

//    private void handleSignInGoogleResult(GoogleSignInResult result) {
//        if (mGoogleLoginListener != null) {
//            if (result.isSuccess()) {
//                // Signed in successfully.
//                GoogleSignInAccount acct = result.getSignInAccount();
//                PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_GOOGLE);
//                mGoogleLoginListener.onLoginSuccess(getGoogleData(acct));
//            } else {
//                // Signed out.
//            }
//        }
//    }

    private void handleSignInGoogleResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_GOOGLE);
            mGoogleLoginListener.onLoginSuccess(getGoogleData(account));
        } catch (ApiException e) {
            e.printStackTrace();
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            mGoogleLoginListener.onLoginError("signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * Get Google account information.
     *
     * @param googleSignInAccount
     * @return user
     */
    private UserSNSInfo getGoogleData(GoogleSignInAccount googleSignInAccount) {
        UserSNSInfo user = new UserSNSInfo();
        if (googleSignInAccount != null) {
            user.setUserId(googleSignInAccount.getId());
            user.setUserName(googleSignInAccount.getDisplayName());
            user.setEmail(googleSignInAccount.getEmail());
            user.setToken(googleSignInAccount.getIdToken());
            if (googleSignInAccount.getPhotoUrl() != null) {
                user.setAvatarUrl(googleSignInAccount.getPhotoUrl().toString());
            }
        }
        return user;
    }

    /**
     * Get Google account info from active session.
     *
     * @param getUserInfoListener
     */
    private void getGoogleAccountInfo(final GetUserInfoListener getUserInfoListener) {
        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_GOOGLE_SDK, false)) {
//            final OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//            if (opr.isDone()) {
//                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//                // and the GoogleSignInResult will be available instantly.
//                GoogleSignInResult result = opr.get();
//                GoogleSignInAccount acct = result.getSignInAccount();
//                getUserInfoListener.onSuccess(getGoogleData(acct));
//            } else {
//                //getUserInfoListener.onFail("You have not previously signed in on this device or the sign-in have expired.");
//                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                    @Override
//                    public void onResult(GoogleSignInResult googleSignInResult) {
//                        GoogleSignInResult result = opr.get();
//                        GoogleSignInAccount acct = result.getSignInAccount();
//                        getUserInfoListener.onSuccess(getGoogleData(acct));
//                    }
//                });
//            }
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mFragmentAcvitivy);
            //update the UI if user has already sign in with the google for this app
            getUserInfoListener.onSuccess(getGoogleData(account));
        } else {
            getUserInfoListener.onFail("You are not init google sdk.");
        }
    }

    /**
     * Do log out Google.
     *
     * @param logOutListener
     */
    private void logOutGoogle(final LogOutListener logOutListener) {
        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_GOOGLE_SDK, false)) {
            if (mGoogleSignInClient != null) {
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_DEFAULT);
//                        logOutListener.onLogOut();
//                    }
//                });
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(mFragmentAcvitivy, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_DEFAULT);
                                logOutListener.onLogOut();
                            }
                        }).addOnFailureListener(mFragmentAcvitivy, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        logOutListener.onLogOut();
                    }
                });
            }
        }
    }

    //endregion

    //region Yahoo API.
    /**
     * Initialize the Yahoo.
     */
//    public void initYahooSDK(String clientId, String customUriScheme) {
//        this.clientId = clientId;
//        this.customUriScheme = customUriScheme;
//        PreferenceUtil.writeBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_YAHOO_SDK, true);
//    }

    /**
     * Handle on click button login to Yahoo.
     *
     * @param loginSNSListener
     */
//    private void loginYahoo(final LoginSNSListener loginSNSListener) {
//        mYahooLoginListener = loginSNSListener;
//        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_YAHOO_SDK, false)) {
//            YConnectExplicit yconnect = YConnectExplicit.getInstance();
//            // Specify a random string for verification between the request and call back.
//            String state = "5LiW55WM44GMWeODkOOBhCEh";
//            // Specify a random string of replay attack measures
//            String nonce = "44Ki44Kk44OH44Oz44OG44Kj44OG44Kj44Gu5rW344Gv5bqD5aSn44Gg44KP";
//            String display = OIDCDisplay.SMART_PHONE;
//            String[] prompt = {OIDCPrompt.DEFAULT};
//            String[] scope = {OIDCScope.OPENID, OIDCScope.PROFILE,
//                    OIDCScope.EMAIL, OIDCScope.ADDRESS};
//
//            PreferenceUtil.writeString(mFragmentAcvitivy, PreferenceUtil.KEY_STATE, state);
//            PreferenceUtil.writeString(mFragmentAcvitivy, PreferenceUtil.KEY_NONCE, nonce);
//
//            yconnect.init(clientId, customUriScheme, state, display, prompt, scope, nonce);
//
//
//            Intent intent = new Intent(mFragmentAcvitivy, YConnectYahooWebviewActivity.class);
//            intent.putExtra(YConnectYahooWebviewActivity.YAHOO_CLIENT_ID, clientId);
//            intent.putExtra(YConnectYahooWebviewActivity.YAHOO_URI_SCHEME, customUriScheme);
//
//            mFragmentAcvitivy.startActivityForResult(intent, RC_LOG_IN_YAHOO);
//        } else {
//            loginSNSListener.onLoginError("You are not init yahoo sdk");
//        }
//    }

    /**
     * Get Yahoo account info from active session.
     *
     * @param getUserInfoListener
    //     */
//    private void getYahooAccountInfo(final GetUserInfoListener getUserInfoListener) {
//        mGetYahooUserInfoListener = getUserInfoListener;
//        if (PreferenceUtil.getBoolean(mFragmentAcvitivy, PreferenceUtil.KEY_INIT_YAHOO_SDK, false)) {
//            GetYahooUserInfoAsyncTask asyncTask = new GetYahooUserInfoAsyncTask(mFragmentAcvitivy, false, getUserInfoListener, clientId, customUriScheme);
//            asyncTask.execute();
//        } else {
//            getUserInfoListener.onFail("You are not init yahoo sdk.");
//        }
//    }

    /**
     * Do log out Yahoo.
     *
     * @param logOutListener
     */
//    private void logOutYahoo(LogOutListener logOutListener) {
//        PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_DEFAULT);
//        PreferenceUtil.writeString(mFragmentAcvitivy, PreferenceUtil.KEY_YAHOO_ACCESS_TOKEN, null);
//        PreferenceUtil.writeString(mFragmentAcvitivy, PreferenceUtil.KEY_YAHOO_URL_CALLBACK, null);
//        PreferenceUtil.writeString(mFragmentAcvitivy, PreferenceUtil.KEY_NONCE, null);
//        PreferenceUtil.writeString(mFragmentAcvitivy, PreferenceUtil.KEY_STATE, null);
//
//        logOutListener.onLogOut();
//    }
    //endregion

    /**
     * Log in to social network.
     * loginType: type of social network.
     * loginSNSListener: callback after do log in.
     *
     * @param loginType
     * @param loginSNSListener
     */
    public void loginSNS(int loginType, LoginSNSListener loginSNSListener) {
        switch (loginType) {
            case Constants.TYPE_FACEBOOK:
                loginFacebook(loginSNSListener);
                break;
//            case Constants.TYPE_TWITTER:
//                loginTwitter(loginSNSListener);
//                break;
            case Constants.TYPE_GOOGLE:
                loginGoogle(loginSNSListener);
                break;
//            case Constants.TYPE_YAHOO:
//                loginYahoo(loginSNSListener);
//                break;
            default:
                break;
        }
    }

    /**
     * Log out social network.
     * logOutListener: callback after do log out.
     *
     * @param logOutListener
     */
    public void logoutSNS(LogOutListener logOutListener) {
        int loginType = PreferenceUtil.getInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_DEFAULT);
        switch (loginType) {
            case Constants.TYPE_FACEBOOK:
                logOutFacebook(logOutListener);
                break;
//            case Constants.TYPE_TWITTER:
//                logOutTwitter(logOutListener);
//                break;
            case Constants.TYPE_GOOGLE:
                logOutGoogle(logOutListener);
                break;
//            case Constants.TYPE_YAHOO:
//                logOutYahoo(logOutListener);
//                break;
            default:
                break;
        }
    }

    /**
     * Get user information from social network.
     * getUserInfoListener: callback after get user information data.
     *
     * @param getUserInfoListener
     */
    public void getUserInfoData(GetUserInfoListener getUserInfoListener) {
        int loginType = PreferenceUtil.getInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_DEFAULT);
        switch (loginType) {
            case Constants.TYPE_FACEBOOK:
                getFacebookAccountInfo(getUserInfoListener);
                break;
//            case Constants.TYPE_TWITTER:
//                getTwitterAccountInfo(getUserInfoListener);
//                break;
            case Constants.TYPE_GOOGLE:
                getGoogleAccountInfo(getUserInfoListener);
                break;
//            case Constants.TYPE_YAHOO:
//                getYahooAccountInfo(getUserInfoListener);
//                break;
            default:
                break;
        }
    }

    private void resetLoginInfo() {
        PreferenceUtil.writeInt(mFragmentAcvitivy, PreferenceUtil.KEY_LOGIN_TYPE, Constants.TYPE_DEFAULT);
    }
}
