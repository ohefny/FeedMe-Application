package com.example.bethechange.feedme.Utils;

import android.os.Handler;

import com.example.bethechange.feedme.FeedMeApp;
import com.example.bethechange.feedme.MainScreen.Models.Category;
import com.example.bethechange.feedme.MainScreen.Models.FeedMeArticle;
import com.example.bethechange.feedme.MainScreen.Models.Site;
import com.example.bethechange.feedme.MainScreen.Models.SuggestSite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BeTheChange on 7/29/2017.
 */

public class FirebaseUtils {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static int insertCategoryList(ArrayList<Category> cats) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int insertedItems=0;
        final DatabaseReference insertCategory = database.getReference("users");

        insertCategory.child(auth.getCurrentUser().getUid()).child("categories").
                removeValue().isSuccessful();
        for (Category cat : cats) {
            boolean inserted=insertCategory.child(auth.getCurrentUser().getUid()).child("categories").child(cat.getId()+ "").setValue(cat).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }
    public static int insertSiteList(ArrayList<Site> sites) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int insertedItems=0;
        final DatabaseReference insertCategory = database.getReference("users");

        insertCategory.child(auth.getCurrentUser().getUid()).child("sites").
                removeValue().isSuccessful();
        for (Site site : sites) {
            boolean inserted=insertCategory.child(auth.getCurrentUser().getUid()).child("sites").child(site.getID()+ "").setValue(site).isSuccessful();
            if(inserted)
                insertedItems++;
        }
        return insertedItems;
    }
    public static int insertSuggestionsSites(ArrayList<Site> sites) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        int insertedItems=0;
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
                    }
                });

    }
    public static void getUserCategories(final FirebaseCategoriesListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference getCategory = database.getReference("users");
        if (auth.getCurrentUser()==null||!NetworkUtils.isNetworkAvailable()){
            listener.onCategoriesFetched(null,true);
            return;
        }
        getCategory.child(auth.getCurrentUser().getUid()).child("categories")
                .addValueEventListener(new ValueEventListener() {

                    ArrayList<Category> cats=new ArrayList<>();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                HashMap<String,Category> cat = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String,Category>>() {});
                                cats.addAll(cat.values());
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
                            HashMap<String,Site>d=dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String,Site>>() {
                            });
                            sites.addAll(d.values());
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
}

