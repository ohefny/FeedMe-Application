package com.feedme.app.Utils;

import android.util.SparseArray;

import com.feedme.app.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by BeTheChange on 7/27/2017.
 */

public class CollectionUtils {
    public static <T> ArrayList<T> sparseToArray(SparseArray<T>sparseArray){
        ArrayList<T>arrayList=new ArrayList<>();
        for(int i=0;i<sparseArray.size();i++)
            arrayList.add(sparseArray.valueAt(i));

        return arrayList;
    }
    public static <T> String[] objectsToStrings(ArrayList<T>objects){
        String[]arrayList=new String[objects.size()];
        for(int i=0;i<objects.size();i++)
           arrayList[i]=(objects.get(i).toString());

        return arrayList;
    }

    public static  <T extends Identifiable> HashMap<String,T> getHashMap(ArrayList<T> list) {
        HashMap<String,T>map=new HashMap<>();
        for(T item:list)
            map.put(item.getObjectKey(),item);
        return map;

    }
    public static  <T extends Identifiable> SparseArray<T> arrayListToSparse(ArrayList<T> list) {
        SparseArray<T> s=new SparseArray<>();
        for(T item:list)
            s.put(item.getIntObjectKey(),item);
        return s;

    }
}
