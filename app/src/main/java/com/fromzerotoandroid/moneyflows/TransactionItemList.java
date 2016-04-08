package com.fromzerotoandroid.moneyflows;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M06F806 on 7-4-2016.
 */
public class TransactionItemList {

    List<TransactionItem> mTransactionItemList;

    TransactionItemList() {
        mTransactionItemList = new ArrayList<TransactionItem>();
    }

    public List<TransactionItem> getmTransactionItemList() {
        return mTransactionItemList;
    }

    public void setmTransactionItemList(List<TransactionItem> mTransactionItemList) {
        this.mTransactionItemList = mTransactionItemList;
    }

    public TransactionItem getmTransactionItemFromList(int position) {
        return mTransactionItemList.get(position);
    }

    @Override
    public String toString() {
        String str = new String();
        for (int i = 0; i < mTransactionItemList.size(); i++) {
            str = mTransactionItemList.get(i).toString() + "\r\n";
        }
        return str;
    }

    public void addTransactionItemToList(TransactionItem transactionItem) {
        mTransactionItemList.add(transactionItem);
    }

    public void updateTransactionItemInList(TransactionItem oldTransactionItem, TransactionItem newTransactionItem) {
        mTransactionItemList.remove(oldTransactionItem);
        mTransactionItemList.add(newTransactionItem);
    }

    public void removeTransactionItemFromList(TransactionItem transactionItem) {
        mTransactionItemList.remove(transactionItem);
    }

    public void removeTransactionItemFromList(int position) {
        mTransactionItemList.remove(position);
    }
}
