package com.gino.grammify;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Gino on 1/19/2016.
 */
public class LaunchScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if resources.bin & resources2.bin exists in /sdcard
        CheckFile();

        //Load words
        new SpellCheck(getApplicationContext());

        //Exit splash screen
        new BackgroundTask().execute();

        //Load resources.bin & resources2.bin
        new POSTagger();
    }

    public void CheckFile() {
        //Check if file exists
        String folder = Environment.getExternalStorageDirectory()
                + "/Android/data/com.thesis.grammify/resources";
        String [] files = {"/resources.bin", "/resources.bin"};
        for (String f: files) {
            if (!new File(folder + f).exists()) {
                Log.wtf("File not found", f);
                new File(folder).mkdirs();
                new CopyFromAssets().execute();
                break;
            }
        }
    }

    private class CopyFromAssets extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            try {
                AssetManager assetManager = getAssets();
                String[] files = {"resources.bin", "resources2.bin"};
                for(String filename : files) {
                    Log.wtf("Filename", filename);
                    try {
                        InputStream in = assetManager.open(filename);
                        OutputStream out = new FileOutputStream(Environment
                                .getExternalStorageDirectory().toString()
                                + "/Android/data/com.thesis.grammify/resources/" + filename);
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    } catch(IOException e) {
                        Log.e("Error", "Failed to copy asset file: " + filename, e);
                    }
                }
                Log.wtf("Copied", "Resources copied!");
            }
            catch (Exception e) {
                Log.wtf("EXCEPTION ERROR Copy", e.toString());
            }
            return null;
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
        }
    }

    private class BackgroundTask extends AsyncTask {
        Intent intent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            intent = new Intent(LaunchScreenActivity.this, MainActivity.class);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            startActivity(intent);
            finish();
        }
    }
}