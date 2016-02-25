package com.gino.grammify;

/**
 * Created by Gino on 11/10/2015.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellCheck {

    private static Context cntx;
    //public static List<Integer> dist;
    public String pattern = ",.!?";
    public static StringBuffer sb;
    public static InputStream inputStrm;
    public static BufferedReader in;
    public static ArrayList<ArrayList<String>> corrections;
    public static ArrayList<String> wordList;
    public final String WHITESPACE = " ";
    ArrayList<String> w;

    static boolean hasApos;

    public SpellCheck() {
    }

    public SpellCheck(Context context) {
        cntx = context;
        new LoadModel().execute();
    }

    public SpellCheck(String message, Context context) throws IOException {

        corrections = new ArrayList<ArrayList<String>>();
        cntx = context;
        sb = new StringBuffer("");
        Log.wtf("INPUT", message);
        String words[] = message.split(WHITESPACE);
        message = "";
        for (String ws: words) {
            message += new Contraction().contractionize(ws) + " ";
        }
        Log.wtf("After Contraction", message);
        words = message.split(WHITESPACE);
        /*String tempWord = "";
        for (char wrd: words[words.length-1].toCharArray()){
            Log.wtf("WORD: ", Character.toString(wrd));
            if (Character.isLetter(wrd))
                tempWord += wrd;
            Log.wtf("TEMP WORD??", tempWord);
        }
        Log.wtf("TEMP WORD??", tempWord);
        words[words.length-1] = tempWord;*/


        int ctr = 0;
        for (String word : words) {
            ctr++;
            char punctuation = ' ';
            hasApos = false;
            String apos = "";
            if (!Character.isDigit(word.charAt(0))) {
                //Check for word case
                boolean wordCase = Character.isUpperCase(word.charAt(0));
                hasApos = word.contains("'");
                String wrd ="";
                if (hasApos) {
                    boolean yes = false;
                    for (Character s: word.toCharArray()) {
                        if (s.equals('\'')) {
                            apos += s;
                            yes = true;
                        }
                        else {
                            if (yes)
                                apos += s;
                            else
                                wrd += s;
                        }
                    }
                    Log.wtf("WORD", wrd);
                    word = wrd;
                }

                if (wordCase && ctr > 1) {
                    if (hasApos)
                        sb.append(word + apos);
                    else
                        sb.append(word);
                } else {
                    //lowercase
                    word = word.toLowerCase();
                    //Check if word is found in the list of words
                    boolean wordFound = false;
                    //Initialize for punctuations

                    if (pattern.contains(Character.toString(word.charAt(word.length() - 1)))) {
                        punctuation = word.charAt(word.length() - 1);
                        word = word.substring(0, word.length() - 1);
                    }

                    //find word
                    for (String wr: wordList) {
                        if (wr.equals(word)) {
                            if (wordCase && hasApos)
                                sb.append(Character.toUpperCase(word.charAt(0)) + (word.substring(1)) + apos);
                            else if (wordCase)
                                sb.append(Character.toUpperCase(word.charAt(0)) + (word.substring(1)));
                            else if (hasApos)
                                sb.append(word + apos);
                            else
                                sb.append(word);

                            if (punctuation != ' ')
                                sb.append(punctuation);
                            wordFound = true;

                            break;
                        } else
                            wordFound = false;
                    }

                    //Find correction
                    if (!wordFound) {

                        w = new ArrayList<String>();


                        if (wordCase) {
                            sb.append("[" + Character.toUpperCase(word.charAt(0)) + word.substring(1) + "]");
                            w.add(Character.toUpperCase(word.charAt(0)) + word.substring(1));
                        }
                        else {
                            sb.append("[" + word + "]");
                            w.add(word);
                        }

                        for (String wr: wordList) {
                            for (int x = 0; x < word.length(); x++) {

                                if (word.length() > x && wr.length() > x) {
                                    if (wr.charAt(x) == word.charAt(x)) {
                                        //if (distance(wr.toCharArray(), word.toCharArray()) /*== dist.get(0)*/ <= 1 ) {
                                        if (new DamerauLevenshtein(wr,word).getSimilarity() <= 1) {
                                            String sugg;
                                            if (wordCase && hasApos)
                                                sugg = Character.toUpperCase(wr.charAt(0)) + wr.substring(1) + apos;
                                            else if (wordCase)
                                                sugg = Character.toUpperCase(wr.charAt(0)) + wr.substring(1);
                                            else if (hasApos)
                                                sugg = wr + apos;
                                            else
                                                sugg = wr;

                                            if (punctuation != ' ')
                                                sugg += punctuation;
                                            Log.wtf("SUGG", sugg);
                                            w.add(sugg);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        corrections.add(w);
                    }
                }
            }
            else
                sb.append(word);
            if (ctr == words.length) {
                if (punctuation == ' ') {
                    sb.append(".");
                }
            } else {
                sb.append(" ");
            }
        }
    }

    private class LoadModel extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Log.wtf("Load", "Dictionary");
            try {
                inputStrm = cntx.getAssets().open("enable1.txt");
                in = new BufferedReader(new InputStreamReader(inputStrm, "UTF-8"));
                wordList = new ArrayList<String>();
                String line;
                while ((line = in.readLine()) != null) {
                    // Log.wtf("WORD", line);
                    wordList.add(line.toString());
                }
            } catch (Exception e) {
                Log.wtf("EXCEPTION ERROR Dictionary", e.toString());
            }
            Log.wtf("Loaded", "Dictionary");
            return null;
        }
    }

    public static String getString()
    {
        return sb.toString();
    }

    public static String ReplaceWord(String error, String replace) {
        sb = new StringBuffer(sb.toString().replace("[" + error + "]", replace));
        return sb.toString();
    }

    public static  ArrayList<ArrayList<String>> getCorrections()
    {
        return corrections;
    }
}