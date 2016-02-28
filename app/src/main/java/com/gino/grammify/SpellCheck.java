package com.gino.grammify;

/**
 * Created by Gino on 11/10/2015.
 */
import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SpellCheck {

    public static StringBuffer sb;
    public static BufferedReader in;
    public static ArrayList<ArrayList<String>> corrections;
    public static ArrayList<String> wordList;
    public final String WHITESPACE = " ";

    static int ctr;

    public SpellCheck() {

    }

    public SpellCheck (String source, Context context) {
        Log.wtf("Words", "Loading");
        //Store words to arraylist
        try {
            in = new BufferedReader(new InputStreamReader(context.getAssets().open(source), "UTF-8"));
            wordList = new ArrayList<String>();
            String line;
            while ((line = in.readLine()) != null)
                wordList.add(line.toString());
        } catch (Exception e) {
            Log.wtf("Exception Words", e.toString());
        }
        Log.wtf("Words", "Loaded");
    }

    public SpellCheck(String message) {

        corrections = new ArrayList<ArrayList<String>>();
        sb = new StringBuffer("");
        Log.wtf("INPUT", message);
        String words[] = message.split(WHITESPACE);
        message = "";
        for (String ws: words) {
            message += new Contraction().contractionize(ws) + " ";
        }
        Log.wtf("After Contraction", message);
        words = message.split(WHITESPACE);

        //CHECK RUNNING TIME
        double start = System.currentTimeMillis();

        ctr = 0;
        CheckWord [] cw = new CheckWord[words.length];
        for (int i = 0; i < cw.length; i++) {
            cw[i] = new CheckWord(words[i]);
            cw[i].start();
        }

        //Merge threads
        for (int i = 0; i < cw.length; i++) {
            try {
                cw[i].join();
            } catch (Exception e) {
                Log.wtf("SpellCheck Join", e.toString());
            }

            sb.append(cw[i].getWord() + " ");

            if (!cw[i].getSuggestions().isEmpty())
                corrections.add(cw[i].getSuggestions());
        }

        //CHECK RUNNING TIME
        Log.wtf("Running time", Double.toString((System.currentTimeMillis() - start)/1000));
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

    public void IncrementCounter() {
        ctr++;
    }

    public int GetCounter(){
        return ctr;
    }

    public void ResetCounter() {
        ctr = 0;
    }

    public ArrayList<String> GetWordList() {
        return wordList;
    }
}

class CheckWord extends Thread {
    ArrayList<String> w = new ArrayList<String>();;
    String word;
    StringBuffer sb;
    public CheckWord(String s){
        super(s);
    }

    public void run() {
        double startWord = System.currentTimeMillis();
        word = getName();
        Log.wtf("Word", word);
        Log.wtf("Run", "start");
        sb = new StringBuffer("");
        new SpellCheck().IncrementCounter();
        char punctuation = ' ';
        boolean hasApos = false;
        String pattern = ",.!?";
        String apostrophe = "";

        if (!Character.isDigit(word.charAt(0))) {
            //Check for word case
            boolean wordCase = Character.isUpperCase(word.charAt(0));
            hasApos = word.contains("'");
            String wrd ="";
            if (hasApos) {
                boolean yes = false;
                for (Character s: word.toCharArray()) {
                    if (s.equals('\'')) {
                        apostrophe  += s;
                        yes = true;
                    }
                    else {
                        if (yes)
                            apostrophe  += s;
                        else
                            wrd += s;
                    }
                }
                Log.wtf("WORD", wrd);
                word = wrd;
            }

            if (wordCase && new SpellCheck().GetCounter() > 1) {
                if (hasApos)
                    sb.append(word + apostrophe );
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
                    new SpellCheck().ResetCounter();
                }

                ArrayList<String> wordList = new SpellCheck().GetWordList();
                //find word
                for (String wr: wordList) {
                    if (wr.equals(word)) {
                        if (wordCase && hasApos)
                            sb.append(Character.toUpperCase(word.charAt(0)) + (word.substring(1)) + apostrophe );
                        else if (wordCase)
                            sb.append(Character.toUpperCase(word.charAt(0)) + (word.substring(1)));
                        else if (hasApos)
                            sb.append(word + apostrophe );
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

                    if (wordCase) {
                        sb.append("[" + Character.toUpperCase(word.charAt(0)) + word.substring(1) + "]");
                        w.add(Character.toUpperCase(word.charAt(0)) + word.substring(1));
                    } else {
                        sb.append("[" + word + "]");
                        w.add(word);
                    }

                    if (punctuation != ' ')
                        sb.append(punctuation);

                    for (String wr: wordList) {
                        for (int x = 0; x < word.length(); x++) {
                            if (word.length() > x && wr.length() > x) {
                                if (wr.charAt(x) == word.charAt(x)) {

                                    //if (distance(wr.toCharArray(), word.toCharArray()) /*== dist.get(0)*/ <= 1 ) {
                                    if (new DamerauLevenshtein(wr,word).getSimilarity() <= 1) {
                                        String sugg;
                                        if (wordCase && hasApos)
                                            sugg = Character.toUpperCase(wr.charAt(0)) + wr.substring(1) + apostrophe ;
                                        else if (wordCase)
                                            sugg = Character.toUpperCase(wr.charAt(0)) + wr.substring(1);
                                        else if (hasApos)
                                            sugg = wr + apostrophe ;
                                        else
                                            sugg = wr;
                                        w.add(sugg);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else
            sb.append(word);
        Log.wtf("Running time/word", Double.toString((System.currentTimeMillis() - startWord)/1000));
    }

    public String getWord() {
        return sb.toString();
    }

    public ArrayList<String> getSuggestions() {
        return w;
    }
}