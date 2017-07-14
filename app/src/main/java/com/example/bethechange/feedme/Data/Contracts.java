package com.example.bethechange.feedme.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by BeTheChange on 7/10/2017.
 */

public class Contracts {

        public static  final String AUTHORITY="com.example.bethechange.feedme";
        public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+AUTHORITY);
        public static final String ARTICLES_PATH="articles";
        public static final String SITES_PATH="sites";
        public static final String CATEGORY_PATH="categories";
        public static final class ArticleEntry implements BaseColumns {
            public  static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(ARTICLES_PATH).build();
            public static final String TABLE_NAME = "Article_Table";
            public static final String COLUMN_SOURCE="source";
            public static final String COLUMN_IMAGE= "image";
            public static final String COLUMN_TITLE = "title";
            public static final String COLUMN_DATE = "date";
            public static final String COLUMN_DESCRIPTION = "description";
            public static final String COLUMN_COMMENTS = "comments";
            public static final String COLUMN_AUTHOR = "author";
            public static final String COLUMN_TAGS = "tags";
            public static final String COLUMN_SITE = "site";
            public static final String COLUMN_CONTENT= "content";

            public static final String COLUMN_FAVORITE ="favorite" ;
            public static final String COLUMN_SAVED="saved";
        }

        public static final class SiteEntry implements BaseColumns {
            public  static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(SITES_PATH).build();
            public static final String TABLE_NAME = "Site_Table";
            public static final String COLUMN_TITLE = "title";
            public static final String COLUMN_URL = "domain";
            public static final String COLUMN_RSS_URL = "rss_link";
            public static final String COLUMN_CATEGORY = "site_category";
            public static final String COLUMN_BLOCKED = "blocked";



        }
        public static final class CategoryEntry implements BaseColumns {
            public  static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(CATEGORY_PATH).build();
            public static final String TABLE_NAME = "Category_Table";
            public static final String COLUMN_TITLE = "category_title";
            public static final String COLUMN_SHARED = "shared";




        }
}
