package com.gino.grammify;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    boolean doubleBackToExitPressedOnce = false;

    /**
     * File url to download
     * resources.bin
     */
    private static String file_url = "https://docs.google.com/uc?id=0B6wkFq-lyI4kT3VjcUhkNm1KaTA&export=download";
    private static String file_url2 = "https://docs.google.com/uc?id=0B6wkFq-lyI4kQ29SdHFOTXZhOGM&export=download";

    static String sentence;
    private EditText input;
    private TextView status;
    private ImageButton button;
    protected static final int RESULT_SPEECH = 1;

    View topLevelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        //MARSHMALLOW PERMISSION
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        button = (ImageButton) findViewById(R.id.imageButton);
        input = (EditText) findViewById(R.id.editText);
        status = (TextView) findViewById(R.id.textView);

        Typeface face= Typeface.createFromAsset(getAssets(), "font_chalk.ttf");

        input.setTypeface(face);

        //Check if directory exists
        String folder = Environment.getExternalStorageDirectory()
                + "/Android/data/com.thesis.grammify/resources";
        File dir = new File(folder + "/resources.bin");
        File dir2 = new File(folder + "/resources2.bin");

        if (!dir.exists() || !dir.exists()) {
            new File(folder).mkdirs();
            new DownloadFileFromURL().execute();

        }
        final EditText editText = (EditText)findViewById(R.id.editText);
        final TextView wordCount = (TextView) findViewById(R.id.textView6);

        //TEMP
        topLevelLayout = findViewById(R.id.top_layout);
        if (isFirstTime()) {
            topLevelLayout.setVisibility(View.INVISIBLE);
        }
        //TEMP
        /*
        final TextWatcher txwatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] arr = s.toString().split("\\s");
                    if (arr.length > 50) {
                        String text ="";
                        for (int x = 0; x < 50; x++)
                            text += text + " ";

                        editText.setText(text);
                    }
                    wordCount.setText("Words: " + arr.length);
                }
                else
                    wordCount.setText("Words: 0");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        editText.addTextChangedListener(txwatcher);
*/
    }

    //MARSHMALLOW PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                else {
                    Toast.makeText(MainActivity.this, "Permission deny to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /* DRAWER
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }}, 2000);
    }
    */

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    /* DRAWER
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();


        int id = item.getItemId();
        if (id == R.id.nav_menu) {
            fragment = new MyFragment1();
        } else if (id == R.id.nav_about) {
            fragment = new MyFragment2();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.drawer_layout, fragment)
                .commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class MyFragment1 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.content_main, container, false);
        }
    }

    public static class MyFragment2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.content_about, container, false);
        }
    }
*/

    public void VoiceInput(View view) {
        input.setEnabled(!input.isEnabled());
        ListeningText(true);
        //Voice Recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast("Oops! Your device doesn't support Speech to Text");
        }
    }

    public void CheckGrammar(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        sentence = input.getText().toString();
        if (sentence.length() == 0)
            Toast("Please enter sentence");
        else {
            startActivityForResult(new Intent(MainActivity.this, CheckGrammar.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION), 0);

        }
    }

    public void Toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void OpenKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    /**
     * Android Speech to Text
     */

    public void ListeningText(boolean en) {
        if (en) {
            button.setBackgroundResource(R.drawable.ic_microphone_grey600_24dp);
            status.setText("Listening");
            final Handler handler = new Handler();
            final Runnable updateTask = new Runnable() {
                @Override
                public void run() {
                    if (input.isEnabled())
                        Thread.currentThread().interrupt();
                    else {
                        status.setText(status.getText() + ".");
                        if (status.getText().toString().contains("...."))
                            status.setText("Listening");
                        handler.postDelayed(this, 1400);
                    }
                }
            };
            handler.postDelayed(updateTask, 1000);
        }
        if (!en) {
            input.setEnabled(!input.isEnabled());
            status.setText("");
            button.setBackgroundResource(R.drawable.ic_microphone_black_24dp);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    input.append(text.get(0).substring(0, 1).toUpperCase() + text.get(0).substring(1) + ". ");
                }
                ListeningText(false);
                break;
            }
        }
    }


    public String GetText() {
        return sentence;
    }


    /**
     * Downloading Dialog
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading offline resources. Please wait...");
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {

            try {
                downloadFile(file_url, "resources");
                downloadFile(file_url2, "resources2");
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

        }

        public void downloadFile(String fl_url, String file_name) throws Exception {
            int count;
            URL url = new URL(fl_url);
            URLConnection urlCon = url.openConnection();
            urlCon.connect();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(),
                    8192);
            OutputStream output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString()
                    + "/Android/data/com.thesis.grammify/resources/" + file_name + ".bin");

            byte data[] = new byte[1024];

            while ((count = input.read(data)) != -1) {
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();
        }

    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
            topLevelLayout.setVisibility(View.VISIBLE);
            topLevelLayout.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    topLevelLayout.setVisibility(View.INVISIBLE);
                    return false;
                }

            });


        }
        return ranBefore;

    }

}
