package com.android.listener;

import com.android.model.UserSNSInfo;

public interface LoginSNSListener {
    public void onLoginSuccess(UserSNSInfo user);

    public void onLoginError(String error);
}
