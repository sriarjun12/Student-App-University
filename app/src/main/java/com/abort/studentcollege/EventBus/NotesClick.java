package com.abort.studentcollege.EventBus;

import com.abort.studentcollege.Model.UserModel;

public class NotesClick {
    private boolean success;
    private UserModel userModel;

    public NotesClick(boolean success, UserModel userModel) {
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
