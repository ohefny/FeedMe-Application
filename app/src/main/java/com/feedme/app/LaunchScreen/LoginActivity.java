package com.feedme.app.LaunchScreen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.feedme.app.Data.ArticlesRepository;
import com.feedme.app.Data.Contracts;
import com.feedme.app.MainScreen.Models.Site;
import com.feedme.app.MainScreen.Views.MainScreenActivity;
import com.feedme.app.R;
import com.feedme.app.Utils.CollectionUtils;
import com.feedme.app.Utils.DBUtils;
import com.feedme.mvpframeworkedited.BasePresenterActivity;
import com.feedme.mvpframeworkedited.PresenterFactory;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

public class LoginActivity extends BasePresenterActivity<LaunchPresenter,LaunchContracts.View> implements
        GoogleApiClient.OnConnectionFailedListener,LaunchContracts.View {

    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN=101;
    private ProgressDialog progressDialog;
    private LaunchContracts.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prepareProgress();
        SignInButton signInBtn = (SignInButton) findViewById(R.id.sign_in_button);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_api_key))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @NonNull
    @Override
    protected String tag() {
        return null;
    }

    @NonNull
    @Override
    protected PresenterFactory getPresenterFactory() {
        return new LaunchPresenterFactory(ArticlesRepository.getInstance(this));
    }

    @Override
    protected void onPresenterPrepared(@NonNull LaunchPresenter presenter) {
        super.onPresenterPrepared(presenter);
        this.presenter=presenter;
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
            presenter.loginSuccessful();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArticlesRepository.getInstance(this);
    }

    @Override
    protected void onStop() {
        ArticlesRepository.destroyInstance(this);
        Log.d("OnStop Login","OnStop");
        super.onStop();
    }

    private void prepareProgress() {
        progressDialog = new ProgressDialog(this);//,R.style.MyProgressBar);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(getString(R.string.login));
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showAuthenticationError();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            progressDialog.show();
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(result.getSignInAccount());

        } else {
            showAuthenticationError();

        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), getString(R.string.firebase_api_key));
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()&&presenter!=null) {
                        presenter.loginSuccessful();
                }
                else{
                    System.out.println(task.getException().getMessage());
                    showAuthenticationError();
                }
            }
        });

    }

    private void showAuthenticationError() {
        progressDialog.cancel();
        Snackbar.make(findViewById(R.id.login_activity_parent),
                getString(R.string.auth_fail), Snackbar.LENGTH_LONG).show();
    }
    @Override
    public void updateProgressMsg(String str) {
        progressDialog.setMessage(str);
    }
    @Override
    public void openMainScreen() {
        progressDialog.dismiss();
        Intent intent = new Intent(this, MainScreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String str) {
        Snackbar.make(findViewById(R.id.login_activity_parent),str, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showSitesList() {
        progressDialog.cancel();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Cursor cur = getContentResolver().query(Contracts.SiteSuggestEntry.CONTENT_URI, null, null, null, null);
        final ArrayList<Site> sites=DBUtils.cursorToSuggestSites(cur);

        final boolean[] checks = new boolean[cur != null ? cur.getCount() : 0];
        final int[] count = {0};
        builder.setTitle(getString(R.string.pick_sites_title))
                .setMultiChoiceItems(CollectionUtils.objectsToStrings(sites),null,new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checks[which] = isChecked;
                        count[0] += isChecked ? 1 : -1;
                    }

                });
        builder.setPositiveButton(getString(R.string.add_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (count[0] > 0){
                    int removed=0;
                    for(int i=0;i<checks.length;i++){
                        if(!checks[i])
                            sites.remove(i-removed++);
                    }
                    presenter.onSitesFetched(sites, false);
                    progressDialog.show();
                }
                else
                    openMainScreen();
            }
        });
        if(sites.size()==0){
            openMainScreen();
        }
        else
         builder.create().show();


    }

}
