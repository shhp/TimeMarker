package com.shhp.timemarker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shhp.timemarker.log.AndroidLogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TimeMarker.mark("onCreate_start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimeMarker.mark("onCreate_setContentView");
        doSomethingOnCreate();
        TimeMarker.mark("onCreate_finish");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeMarker.report(new AndroidLogUtil());
    }

    @Override
    protected void onResume() {
        TimeMarker.mark("onResume_start");
        super.onResume();

        doSomethingOnResume();

        TimeMarker.mark("onResume_finish");
    }

    private void doSomethingOnCreate() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doSomethingOnResume() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
