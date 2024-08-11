package com.android.listener;

import com.android.model.UserSNSInfo;

public interface GetUserInfoListener {
    public void onSuccess(UserSNSInfo user);

    public void onFail(String error);
}
