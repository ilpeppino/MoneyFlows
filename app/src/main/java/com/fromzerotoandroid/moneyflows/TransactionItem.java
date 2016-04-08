package com.fromzerotoandroid.moneyflows;

/**
 * Created by M06F806 on 7-4-2016.
 */
public class TransactionItem {

    String mCost,
            mDescription,
            mDate,
            mCategory,
            mTimestamp;

    // Constructors
    TransactionItem() {
        mCost = "";
        mDescription = "";
        mDate = "";
        mCategory = "";
        mTimestamp = "";
    }

    TransactionItem(String category, String cost, String description, String date, String timestamp) {

        mCategory = category;
        mCost = cost;
        mDescription = description;
        mDate = date;
        mTimestamp = timestamp;
    }

    // Getters and setters
    public String getmCost() {
        return mCost;
    }

    public void setmCost(String mCost) {
        this.mCost = mCost;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    @Override
    public String toString() {
        return mTimestamp + " - " + mCategory + " - " + mCost + " - " + mDescription + " - " + mDate;
    }
}
