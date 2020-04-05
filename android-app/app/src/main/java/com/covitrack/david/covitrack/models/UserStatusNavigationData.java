package com.covitrack.david.covitrack.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserStatusNavigationData implements Parcelable {
    private UserStatusType status;

    public UserStatusNavigationData(UserStatusType status) {
        this.setStatus(status);
    }

    protected UserStatusNavigationData(Parcel in) {
        int flag = in.readInt();
        UserStatusType status = UserStatusType.INFECTED;

        if (flag == UserStatusType.CONTACT.getValue()) {
            status = UserStatusType.CONTACT;
        }

        this.status = status;
    }

    public static final Creator<UserStatusNavigationData> CREATOR = new Creator<UserStatusNavigationData>() {
        @Override
        public UserStatusNavigationData createFromParcel(Parcel in) {
            return new UserStatusNavigationData(in);
        }

        @Override
        public UserStatusNavigationData[] newArray(int size) {
            return new UserStatusNavigationData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status.getValue());
    }

    public UserStatusType getStatus() {
        return status;
    }

    public void setStatus(UserStatusType status) {
        this.status = status;
    }
}
