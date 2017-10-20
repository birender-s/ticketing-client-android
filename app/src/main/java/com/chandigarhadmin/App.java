package com.chandigarhadmin;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.chandigarhadmin.controller.ApiController;
import com.chandigarhadmin.interfaces.RetrofitApiInterface;
import com.chandigarhadmin.retrofit.RetrofitApiClient;

public class App extends Application {

    private static RetrofitApiInterface apiInterface;
    private static ApiController apiController;

    //creating and returning instance of Retrofit Api interface
    public static RetrofitApiInterface getInterface() {
        if (null == apiInterface) {
            apiInterface = RetrofitApiClient.getClient().create(RetrofitApiInterface.class);
        }
        return apiInterface;
    }

    //creating and returning instance of Retrofit Api interface
    public static ApiController getApiController() {
        if (null == apiController) {
            apiController = new ApiController();
        }
        return apiController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //using multidex to avoid 65536 method use error
        MultiDex.install(this);
    }
}
