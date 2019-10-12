package com.feedme.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.feedme.app.Data.Contracts;
import com.feedme.app.FeedMeApp;
import com.feedme.app.MainScreen.Models.Category;
import com.feedme.app.MainScreen.Models.FeedMeArticle;
import com.feedme.app.MainScreen.Models.Site;
import com.feedme.app.MainScreen.Models.SuggestSite;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by BeTheChange on 7/29/2017.
 */

public class FirebaseUtils {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void serialBackup(ArrayList<Category> cats, final ArrayList<Site> sites){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
            return ;
        final DatabaseReference dataRef = database.getReference("users").child(auth.getCurrentUser().getUid());

        dataRef.child("categories").
                removeValue().isSuccessful();

        dataRef.child("categories").setValue(CollectionUtils.getHashMap(cats)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dataRef.child("sites").setValue(CollectionUtils.getHashMap(sites)).isSuccessful();
                }
            }
        });

    }
    public static int insertCategoryList(ArrayList<Category> cats) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
            return 0;
        int insertedItems=0;
        final DatabaseReference insertCategory = database.getReference("users").child(auth.getCurrentUser().getUid());

        insertCategory.child("categories").
                removeValue().isSuccessful();
        for (Category cat : cats) {
            boolean inserted=insertCategory.child("categories").child(cat.getTitle()+ "").setValue(cat).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }
    public static int insertSiteList(ArrayList<Site> sites) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int insertedItems=0;
        final DatabaseReference insertCategory = database.getReference("users").child(auth.getCurrentUser().getUid());
        if(auth.getCurrentUser()==null)
            return 0;
        insertCategory.child("sites").
                removeValue().isSuccessful();
        for (Site site : sites) {
            boolean inserted=insertCategory.child("sites").child(site.getID()+ "").setValue(site).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }
    public static int insertSuggestionsSites(ArrayList<Site> sites) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int insertedItems=0;
        if(auth.getCurrentUser()==null)
            return 0;
        final DatabaseReference insertCategory = database.getReference("sites");
        //insertCategory.set
        for (Site site : sites) {
            boolean inserted=insertCategory.child(site.getID()+"").setValue(new SuggestSite(site)).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }
    public static int insertSavings(ArrayList<FeedMeArticle>feeds){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
            return 0;
        int insertedItems=0;
        final DatabaseReference insertCategory = database.getReference("users");

        insertCategory.child(auth.getCurrentUser().getUid()).child("savings").
                removeValue().isSuccessful();
        for (FeedMeArticle feed : feeds) {
            boolean inserted=insertCategory.child(auth.getCurrentUser().getUid()).child("savings").
                    child(feed.getArticleID()+ "").setValue(feed).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }
    public static int insertBookmarks(ArrayList<FeedMeArticle>feeds){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null)
            return 0;
        int insertedItems=0;
        final DatabaseReference insertCategory = database.getReference("users");

        insertCategory.child(auth.getCurrentUser().getUid()).child("bookmarks").
                removeValue().isSuccessful();
        for (FeedMeArticle feed : feeds) {
            boolean inserted=insertCategory.child(auth.getCurrentUser().getUid()).child("bookmarks").
                    child(feed.getArticleID()+ "").setValue(feed).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }

    public static void checkUserExist(final FirebaseUserListener listener){
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference getUsers = database.getReference("users");
        if (auth.getCurrentUser()==null||!NetworkUtils.isNetworkAvailable()){
            listener.onUserChecked(false);
            return;
        }
        getUsers.addValueEventListener(new ValueEventListener() {

                    ArrayList<Site> sites=new ArrayList<>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        deliverToListener(dataSnapshot.child(auth.getCurrentUser().getUid()).exists());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deliverToListener(false);
                    }
                    private void deliverToListener(final boolean exist){
                        new Handler(FeedMeApp.getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onUserChecked(exist);

                            }
                        });
                        getUsers.removeEventListener(this);
                    }
                });

    }
    public static void getUserCategories(final FirebaseCategoriesListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser()==null||!NetworkUtils.isNetworkAvailable()){
            listener.onCategoriesFetched(null,true);
            return;
        }
        final DatabaseReference getCategory = database.getReference("users").child(auth.getCurrentUser().getUid()).child("categories");
        getCategory.addValueEventListener(new ValueEventListener() {

                    ArrayList<Category> cats=new ArrayList<>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                System.out.println(dataSnapshot);
                               // for(DataSnapshot snap:dataSnapshot.getChildren()){
                                    HashMap<String,Category> cat = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String,Category>>() {});
                                    cats.addAll(cat.values());
                               // }
                                deliverToListener(false);

                        }
                        else
                            deliverToListener(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deliverToListener(true);
                    }
                    private void deliverToListener(final boolean error){
                        new Handler(FeedMeApp.getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCategoriesFetched(cats, error);
                            }
                        });
                        getCategory.removeEventListener(this);
                    }
                });

    }
    public static void updateSuggestionsSites(final Context context) {
        final DatabaseReference getSites = database.getReference("sites");
        final ArrayList<Site> sites=new ArrayList<>();
        if (!NetworkUtils.isNetworkAvailable()){
            return;
        }
        getSites.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean error=true;
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    HashMap<String,Site>d=dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String,Site>>() {
                    });
                    sites.addAll(d.values());
                    error=false;

                }
                updateDB(error);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateDB(true);
            }
            private void updateDB(final boolean error){
                if(!error)
                    context.getContentResolver().bulkInsert(Contracts.SiteSuggestEntry.CONTENT_URI,DBUtils.suggestSitesToCV(sites));
                getSites.removeEventListener(this);
            }
        });

    }
    public static void getSuggestionsSites(final FirebaseSitesListener listener) {
        final DatabaseReference getSites = database.getReference("sites");
        final ArrayList<Site> sites=new ArrayList<>();
        if (!NetworkUtils.isNetworkAvailable()){
            listener.onSitesFetched(sites,true);
            return;
        }
        getSites.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean error=true;
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                           // for(DataSnapshot snap:dataSnapshot.getChildren()){
                                HashMap<String,Site>d=dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String,Site>>() {});
                                sites.addAll(d.values());
                            //}
                            error=false;

                        }
                        deliverToListener(error);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deliverToListener(true);
                    }
                    private void deliverToListener(final boolean error){
                        new Handler(FeedMeApp.getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSitesFetched(sites, error);
                            }
                        });
                        getSites.removeEventListener(this);
                    }
                });

    }
    public static void getUserSites(final FirebaseSitesListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference getSites = database.getReference("users");
        if (auth.getCurrentUser()==null||!NetworkUtils.isNetworkAvailable()){
            listener.onSitesFetched(null,true);
            return;
        }
        getSites.child(auth.getCurrentUser().getUid()).child("sites")
                .addValueEventListener(new ValueEventListener() {
                    ArrayList<Site> sites=new ArrayList<>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean error=true;
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                HashMap<String,Site> sit = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String,Site>>() {});
                                sites.addAll(sit.values());
                                error=false;
                        }

                        deliverToListener(error);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        deliverToListener(true);
                    }
                    private void deliverToListener(final boolean error){
                        new Handler(FeedMeApp.getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSitesFetched(sites, error);
                            }
                        });
                    }
                });

    }
    public interface FirebaseSitesListener{
        void onSitesFetched(ArrayList<Site>sites,boolean error);
    }
    public interface FirebaseCategoriesListener{
        void onCategoriesFetched(ArrayList<Category>categories,boolean error);
    }
    public interface FirebaseUserListener{
        void onUserChecked(boolean exist);
    }
    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        return status == ConnectionResult.SUCCESS;
    }
}

