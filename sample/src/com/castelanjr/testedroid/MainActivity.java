package com.castelanjr.testedroid;

import net.nyvra.bakerdroid.BakerDroidView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	BakerDroidView mBaker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBaker = (BakerDroidView) findViewById(R.id.baker);
        mBaker.loadDocument("book", 0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
