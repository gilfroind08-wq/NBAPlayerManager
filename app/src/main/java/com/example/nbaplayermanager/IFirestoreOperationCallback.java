
package com.example.nbaplayermanager;

public interface IFirestoreOperationCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
