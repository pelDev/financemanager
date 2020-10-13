package com.example.financemanager;

import android.os.Parcel;
import android.os.Parcelable;

public final class ExpenditureInfo  {

    private String mExpenditureName;
    private String mExpenditureTimestamp;
    private String mExpenditureId;
    private int mExpenditureAmount;
    private int mId;

    public ExpenditureInfo(String expenditureName, String expenditureTimestamp, String expenditureId, int expenditureAmount, int id) {
        mExpenditureName = expenditureName;
        mExpenditureTimestamp = expenditureTimestamp;
        mExpenditureId = expenditureId;
        mExpenditureAmount = expenditureAmount;
        mId = id;
    }

//    private ExpenditureInfo(Parcel parcel){
//        mExpenditureName = parcel.readString();
//        mExpenditureTimestamp = parcel.readString();
//    }



    public String getExpenditureName() {
        return mExpenditureName;
    }

    public void setExpenditureName(String expenditureName) {
        mExpenditureName = expenditureName;
    }

    public String getExpenditureTimestamp() {
        return mExpenditureTimestamp;
    }

    public void setExpenditureTimestamp(String expenditureTimestamp) {
        mExpenditureTimestamp = expenditureTimestamp;
    }

//    private String getCompareKey() {
//        return mCourse.getCourseId() + "|" + mExpenditureName + "|" + mExpenditureTimestamp;
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ExpenditureInfo that = (ExpenditureInfo) o;
//
//        return getCompareKey().equals(that.getCompareKey());
//    }
//
//    @Override
//    public int hashCode() {
//        return getCompareKey().hashCode();
//    }
//
//    @Override
//    public String toString() {
//        return getCompareKey();
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }

//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeParcelable(mCourse, 0);
//        parcel.writeString(mExpenditureName);
//        parcel.writeString(mExpenditureTimestamp);
//    }

//    public  static  final Creator<NoteInfo> CREATOR =
//            new Creator<NoteInfo>() {
//                @Override
//                public NoteInfo createFromParcel(Parcel parcel) {
//                    return new NoteInfo(parcel);
//                }
//
//                @Override
//                public NoteInfo[] newArray(int size) {
//                    return new NoteInfo[size];
//                }
//            };

    public int getId() {
        return mId;
    }

    public int getExpenditureAmount() {
        return mExpenditureAmount;
    }

    public void setExpenditureAmount(int expenditureAmount) {
        mExpenditureAmount = expenditureAmount;
    }
}
