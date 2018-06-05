package com.tapc.platform.model.common;

/**
 * Created by Administrator on 2018/1/19.
 */

public class UserManageModel {
    private static UserManageModel sUserManageModel;
    private boolean isLogined = false;
    private User mScanCodeUser;

    private UserManageModel() {
    }

    public static class User {

    }

    public static UserManageModel getInstance() {
        if (sUserManageModel == null) {
            synchronized (UserManageModel.class) {
                if (sUserManageModel == null) {
                    sUserManageModel = new UserManageModel();
                }
            }
        }
        return sUserManageModel;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public void login() {
        isLogined = true;
    }

    public void logout() {
        isLogined = false;
        mScanCodeUser = null;
    }

    public User getScanCodeUser() {
        return mScanCodeUser;
    }

    public void setScanCodeUser(User scanCodeUser) {
        this.mScanCodeUser = scanCodeUser;
    }
}
