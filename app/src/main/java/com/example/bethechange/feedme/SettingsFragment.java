package com.example.bethechange.feedme;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bethechange.feedme.Utils.PrefUtils;
import com.firebase.jobdispatcher.JobService;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private ListPreference mUpdateList;
    private ListPreference mCleanList;
    private ListPreference mBackupList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_data_sync);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= super.onCreateView(inflater, container, savedInstanceState);
        mBackupList=(ListPreference)findPreference(getResources().getString(R.string.backup_list_freq_key));
        mUpdateList=(ListPreference)findPreference(getResources().getString(R.string.sync_list_freq_key));
        mCleanList=(ListPreference)findPreference(getResources().getString(R.string.cleanup_list_freq_key));
        setUpdateListSummary();
        setBackupListSummary();
        //mCleanList.set(PrefUtils.getOutdate(getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUpdateList.setOnPreferenceChangeListener(this);
        mCleanList.setOnPreferenceChangeListener(this);
        mBackupList.setOnPreferenceChangeListener(this);

              //  .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUpdateList.setOnPreferenceChangeListener(null);
        mCleanList.setOnPreferenceChangeListener(null);
        mBackupList.setOnPreferenceChangeListener(null);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //mListPreference.set
       // preference.setDefaultValue(newValue.toString());
      /*  */
        if(!(preference instanceof ListPreference))
            return false;
        if(preference.equals(mBackupList)){
            PrefUtils.setBackupInterval(getActivity(),Integer.parseInt((String)newValue));
            setBackupListSummary();
            JobManger.scheduleBackupJob(getActivity());
            return true;
        }
        else if(preference.equals(mCleanList)){
            PrefUtils.setOutdateVal(getActivity(),Integer.parseInt((String)newValue));
            return true;
        }
        else if(preference.equals(mUpdateList)){
            PrefUtils.setUpdateInterval(getActivity(),Integer.parseInt((String)newValue));
            JobManger.scheduleRefreshJob(getActivity());
            setUpdateListSummary();
            return true;
        }
        return false;
    }

    private void setBackupListSummary() {
        int val= PrefUtils.getBackupInterval(getActivity());
        String sum=val>1?val+" Days":val+" Day";
        if(val>7)
            sum="Never";
        mBackupList.setSummary(sum);
    }

    private void setUpdateListSummary() {
        int val= PrefUtils.getUpdateInterval(getActivity());
        String sum=val>1?val+" Hours":val+" Hour";
        sum=val>24?(val/24)+" Days":sum;
        if(val/24>7)
            sum="Never";
        if(val==0)
            sum="On application open";
        mUpdateList.setSummary(sum);
    }
}



