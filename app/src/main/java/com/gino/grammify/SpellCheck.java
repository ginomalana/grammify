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

                        //store distance
                        ArrayList<String> w;
                        /*
                        //Store all distance from word
                        dist = new ArrayList<Integer>();

                        for (String wr: wordList) {
                            //Log.wtf("MATCH", wr + " WORD: " + word);
                            for (int x = 0; x < word.length(); x++) {
                                if (word.length() > x && wr.length() > x) {
                                    if (wr.charAt(x) == word.charAt(x)) {
                                        dist.add(distance(wr.toCharArray(), word.toCharArray()));
                                        break;
                                    }
                                }
                            }
                        }*/
                        //Sort distance from word
                        //Collections.sort(dist);
                        w = new ArrayList<String>();
                        w.add(word);


                        sb.append("[" + word + "]");

                        for (String wr: wordList) {
                            for (int x = 0; x < word.length(); x++) {
                                if (word.length() > x && wr.length() > x) {
                                    if (wr.charAt(x) == word.charAt(x)) {
                                        //Log.wtf("DISTANCE", Boolean.toString(distance(wr.toCharArray(), word.toCharArray()) == dist.get(0)));
                                        if (distance(wr.toCharArray(), word.toCharArray()) /*== dist.get(0)*/ <= 2 ) {
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
                                            w.add(sugg);
                                        }
                                        break;
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

    /*
    public static int distance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }*/

    public static int distance( char[] s1, char[] s2 ) {

        // memoize only previous line of distance matrix
        int[] prev = new int[ s2.length + 1 ];

        for( int j = 0; j < s2.length + 1; j++ ) {
            prev[ j ] = j;
        }

        for( int i = 1; i < s1.length + 1; i++ ) {

            // calculate current line of distance matrix
            int[] curr = new int[ s2.length + 1 ];
            curr[0] = i;

            for( int j = 1; j < s2.length + 1; j++ ) {
                int d1 = prev[ j ] + 1;
                int d2 = curr[ j - 1 ] + 1;
                int d3 = prev[ j - 1 ];
                if ( s1[ i - 1 ] != s2[ j - 1 ] ) {
                    d3 += 1;
                }
                curr[ j ] = Math.min( Math.min( d1, d2 ), d3 );
            }

            // define current line of distance matrix as previous
            prev = curr;
        }
        return prev[ s2.length ];
    }

    /*
    public static int distance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = Math.min(Math.min(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }
*/
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