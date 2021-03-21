package com.abort.studentcollege.EventBus;

import com.abort.studentcollege.Model.UserModel;

public class TimeTableClick {
    private boolean success;
    private UserModel userModel;

    public TimeTableClick(boolean success, UserModel userModel) {
        this.success = success;
        this.userModel = userModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
