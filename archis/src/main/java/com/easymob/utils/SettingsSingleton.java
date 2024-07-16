package com.easymob.utils;

import com.easymob.bdd.BddCrud;
import com.easymob.model.Settings;

import java.util.List;

public class SettingsSingleton {
    private static SettingsSingleton instance;
    private List<Settings> settings;

    private SettingsSingleton() {
        updateSettings();
    }

    public static SettingsSingleton getInstance() {
        if (instance == null) {
            instance = new SettingsSingleton();
        }
        return instance;
    }

    public List<Settings> getSettings() {
        return settings;
    }

    public void updateSettings() {
        this.settings = BddCrud.getSettings(); // Assuming getSettings returns a List<Settings>
    }
}
