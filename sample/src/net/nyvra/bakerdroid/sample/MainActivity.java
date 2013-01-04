package net.nyvra.bakerdroid.sample;

import net.nyvra.bakerdroid.BakerDroidView;
import net.nyvra.bakerdroid.BakerDroidView.StorageMode;
import net.nyvra.bakerdroid.sample.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	private BakerDroidView mBaker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mBaker = (BakerDroidView) findViewById(R.id.baker);
        mBaker.setStorageMode(StorageMode.STORAGE_ASSETS_FOLDER);
        mBaker.loadDocument("book", 0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
