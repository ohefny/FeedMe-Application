package com.feedme.app;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by BeTheChange on 7/29/2017.
 */

public class UserManger {
    private static UserManger sManger;

    public  GoogleSignInAccount getAcc() {
        return sAcc;
    }

    private static GoogleSignInAccount sAcc ;
    private UserManger(GoogleSignInAccount acc){
        sAcc=acc;
    }
    public static UserManger getInstance(){
        return sManger;
    }
    public static UserManger createInstance(GoogleSignInAccount result){
        sManger=new UserManger(result);
        return sManger;
    }

}
