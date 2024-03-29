package com.kraaiennest.opvang.base;

import android.app.Application;
import android.content.Context;

import com.kraaiennest.opvang.R;

import org.acra.ACRA;
import org.acra.BuildConfig;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;

import dagger.hilt.android.HiltAndroidApp;


@HiltAndroidApp
@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "websitetkraaiennest@gmail.com",
        resSubject = R.string.app_crach_subject,
        reportAsFile = true,
        reportFileName = "report.json")
public class BaseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
