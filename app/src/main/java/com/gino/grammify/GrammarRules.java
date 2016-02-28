package com.gino.grammify;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gino on 1/26/2016.
 */
public class GrammarRules extends AppCompatActivity {


    public static String output;
    public static String sntnc;
    public static int errorOccur;
    public boolean isQuestion = false;
    public static ArrayList<ArrayList<String>> suggestions;
    //public static ArrayList<String> suggestions;

    public GrammarRules(String taggedWords, ArrayList<ArrayList<String>> chunk) {
        int hold = 0;
        suggestions = new ArrayList<ArrayList<String>>();
        StringBuffer sb = new StringBuffer("");
        Log.wtf("TAGGED MESSAGE: ", taggedWords);
        errorOccur = 0;
        //Sentence Tokenization
        ArrayList<String> sentence = new ArrayList<String>();
        Pattern delimiter = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*" +
                "[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher tokenize = delimiter.matcher(taggedWords);
        while (tokenize.find()) {
            //remove punctuation
            sentence.add(tokenize.group().substring(0, tokenize.group().length() - 1));
        }
        //output
        for (int i = 0; i < sentence.size(); i++) {
            Log.wtf("WORD AND TAG", sentence.get(i));
        }


        for (int x = 0; x < sentence.size(); x++) {
            //Split word and tags
            String[] splitSentence = sentence.get(x).split(" ");
            String[][] words = new String[splitSentence.length][2];
            /*
            * FIRST INDEX WORD
            * SECOND INDEX TAG
             */
            for (int i = 0; i < splitSentence.length; i++) {
                String[] wordAndTag = splitSentence[i].split("_");
                words[i][0] = wordAndTag[0];
                words[i][1] = wordAndTag[1];
                if(words[i][0].equalsIgnoreCase("am")){
                    words[i][1] = "VBP";
                }
                if(words[i][0].equalsIgnoreCase("was")){
                    words[i][1] = "VBDZ";
                }
                if(words[i][0].equalsIgnoreCase("were")){
                    words[i][1] = "VBDP";
                }
                //words[i][1] = wordAndTag[1].equals("were")? "VBP": "VBZ";
                Log.wtf("Word", words[i][0] + "\tTag: " + words[i][1]);
            }


            // SAMPLE SENTENCE WITH TAGS
            //The_DT scissors_NNS are_VBP missing._VBG
            //String words[][] =  {{"The","DT"},{"pantaloons","NNS"},{"is","VBZ"},{"trendy.","VBG"}};

            String temp = "";
            //Capitalization
            temp = words[0][0].substring(0, 1).toUpperCase() + words[0][0].substring(1);
            words[0][0] = temp;
            for (int i = 1; i < words.length - 1; i++) {
                /*if (words[i][0].contains(".") || words[i][0].contains("!") || words[i][0].contains("?")) {
                    if (i + 1 < words.length - 1) {
                        temp = words[i + 1][0].substring(0, 1).toUpperCase() + words[i + 1][0].substring(1);
                        words[i + 1][0] = temp;
                    }
                }*/
                if (words[i][0].equals("i")) {
                    words[i][0] = words[i][0].substring(0, 1).toUpperCase();
                }
            }
            //Punctuation
            //!!
        /*if(words[0][1].contains("W") || words[0][0].equals("Am") || (words[0][1].contains("VB") && words[1][1].contains("P")))
            words[words.length-1][0]+="?";
        else if((!words[words.length-1][0].contains(".") || words[words.length-1][0].contains("!") || words[words.length-1][0].contains("?")))
            words[words.length-1][0]+=".";
*/
            for (int i = 0; i < words.length - 1; i++) {
                sb.append(words[i][0] + " ");
            }

            //append punctuation
            sb.append(words[words.length - 1][0] + SetPunctuation(words) + " ");

            sntnc = sb.toString();
            //Log.wtf("SNTC CONCAT", sntnc);
            int ctr = 0;
            //output

            //Verb-Change
            int ruleOne = RuleOne(words);
            int ruleTwo = RuleTwo(words, isQuestion);
            int ruleFour = RuleFour(words, isQuestion);
            int ruleFive = RuleFive(words, isQuestion);
            int ruleSix = RuleSix(words);
            int ruleSeven = RuleSeven(words);
            int ruleEight = RuleEight(words);
            int ruleNine = RuleNine(words);
            int ruleTen = RuleTen(words);
            int basicyou = BasicRuleYou(words);
            int basicyour = BasicRuleYour(words);
            int prosingular = BasicRulePronounSingular(words);
            int proplural = BasicRulePronounPlural(words);
            int basicsingular = BasicRuleSingular(words);
            int basicplural = BasicRulePlural(words);
            int arti = IndefiniteArticle(words);
            int frag = Fragments(words, chunk.get(x), isQuestion);
            int basicI = BasicI(words);
            int runOn = RunOn(chunk.get(x),words);

            //int runOnFrag = RunOnFragments(chunk, words);
            int comp = Compound(words);
            int ha = Have(words);
            //int frag = 2;
            if(frag == 1){
                //FRAGMENT
                ArrayList<String> fragmentList = new ArrayList<String>();
                fragmentList.add(Integer.toString(x));
                fragmentList.add("1"); ////COLOR
                suggestions.add(fragmentList);
                //sb.append("\nFragment\n");
            }
            else if(runOn>0){
                ArrayList<String> runOnList = new ArrayList<String>();
                runOnList.add(Integer.toString(x));
                runOnList.add("0");
                suggestions.add(runOnList);
            }
            /*if (runOnFrag == 1) {
                sb.append("\nError: Run on.\n");
            }
            else if (runOnFrag == 2) {
                sb.append("\nError: Fragment\n");
            } */
            //else if (runOnFrag == 3) {
            else{
                //Log.wtf("RULE FOUR VALUEE", " "+ruleFour);
                if (ruleOne > 0) {
                    //sb.append("\nRule #1 Error at word/s: " + words[RuleOne(words)][0]);
               /* sb = new StringBuffer(sentence.replace(" " + words[ruleOne][0] + " ",
                        " " + new Inflector().pluralize(words[ruleOne][0]) + " "));*/
                    ArrayList<String> ruleOneList = new ArrayList<String>();
                    ruleOneList.add(Integer.toString(x));
                    ruleOneList.add("2"); ////COLOR
                    ruleOneList.add(words[ruleOne][0]);
                    ruleOneList.add(new Inflector().pluralize(words[ruleOne][0]));
                    suggestions.add(ruleOneList);
                    hold++;
                    //sb.append("\nRule 1");
                    ctr++;

                    Log.wtf("RULE 1", "true");
                }
                else if (ruleTwo > 0) {

                    //sb.append("\nRule #2 Error at word/s: "  + words[RuleTwo(words)][0]);
                    // final String suggst[] = {words[ruleTwo][0],
                    //        (new Inflector().pluralize(words[ruleTwo][0]) + new Inflector().singularize(words[ruleTwo - 1][0])),
                    //        (new Inflector().singularize(words[ruleTwo][0]) + new Inflector().pluralize(words[ruleTwo - 1][0]))
                    //};
                    ArrayList<String> ruleTwoList = new ArrayList<String>();
                    ruleTwoList.add(Integer.toString(x));
                    ruleTwoList.add("2"); ////COLOR
                    ruleTwoList.add(words[ruleTwo - 1][0] + " " + words[ruleTwo][0]);
                    ruleTwoList.add(new Inflector().singularize(words[ruleTwo - 1][0]) + " " + new Inflector().pluralize(words[ruleTwo][0]));
                    ruleTwoList.add(new Inflector().pluralize(words[ruleTwo - 1][0]) + " " + new Inflector().singularize(words[ruleTwo][0]));
                    suggestions.add(ruleTwoList);
                    //sb.append("\nRule 2");
                    ctr++;

                    Log.wtf("RULE 2", "true");
                }
                else if (RuleThree(words, getSentenceInput(words)) > -1) {
                    Log.wtf("RULE 3", "true");
                    //sb.append("\nRule #3 Error at word/s: " + words[RuleThree(words, getSentenceInput(words))][0]);
                    if (getSentenceInput(words).toLowerCase().contains("the number")) {
                        //sb = new StringBuffer(sentence.replace(" " + words[RuleThree(words, getSentenceInput(words))][0] + " ",
                        //      " " + new Inflector().pluralize(words[RuleThree(words, getSentenceInput(words))][0]) + " "));
                        ArrayList<String> ruleList = new ArrayList<String>();
                        ruleList.add(Integer.toString(x));
                        ruleList.add("2"); ////COLOR
                        ruleList.add(words[RuleThree(words, getSentenceInput(words))][0]);
                        ruleList.add(new Inflector().pluralize(words[RuleThree(words, getSentenceInput(words))][0]));
                        suggestions.add(ruleList);
                    }
                    if (getSentenceInput(words).toLowerCase().contains("a number")) {
                        // sb = new StringBuffer(sentence.replace(" " + words[RuleThree(words, getSentenceInput(words))][0] + " ",
                        //       " " + new Inflector().singularize(words[RuleThree(words, getSentenceInput(words))][0]) + " "));
                        ArrayList<String> ruleList = new ArrayList<String>();
                        ruleList.add(Integer.toString(x));
                        ruleList.add("2"); ////COLOR
                        ruleList.add(words[RuleThree(words, getSentenceInput(words))][0]);
                        ruleList.add(new Inflector().singularize(words[RuleThree(words, getSentenceInput(words))][0]));
                        suggestions.add(ruleList);
                    }
                    //sb.append("\nRule 3");

                    ctr++;
                }
                else if (ruleFour > -1 && errorOccur == 0) {
                    //sb.append("\nRule #4 Error at word/s: " + words[RuleFour(words)][0]);
                    //sb = new StringBuffer(sentence.replace(" " + words[ruleFour][0] + " ",
                    //      " " + new Inflector().singularize(words[ruleFour][0]) + " "));
                    Log.wtf("RULE 4", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ruleFour][0]);
                    ruleList.add(new Inflector().singularize(words[ruleFour][0]));
                    suggestions.add(ruleList);
                    //sb.append("\nRule 4");
                    ctr++;
                }
                else if (ruleFive > -1) {
                    Log.wtf("RULE 5", "true");
                    //sb.append("\nRule #5 Error at word/s: " + words[RuleFive(words)][0]);
                    //sb = new StringBuffer(sentence.replace(" " + words[ruleFive][0] + " ",
                    //      " " + new Inflector().pluralize(words[ruleFive][0]) + " "));
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ruleFive][0]);
                    ruleList.add(new Inflector().pluralize(words[ruleFive][0]));
                    suggestions.add(ruleList);
                    //sb.append("\nRule 5");

                    ctr++;
                }
                else if (ruleSix > -1) {
                    Log.wtf("RULE 6", "true");
                    //sb.append("\nRule #6 Error at word/s: " + words[RuleSix(words)][0]);
                    //sb = new StringBuffer(sentence.replace(" " + words[ruleSix][0] + " ",
                    //      " " + new Inflector().singularize(words[ruleSix][0]) + " "));
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ruleSix][0]);
                    ruleList.add(new Inflector().singularize(words[ruleSix][0]));
                    suggestions.add(ruleList);
                    //sb.append("\nRule 6");

                    ctr++;
                }
                else if (ruleSeven > 0) {
                    Log.wtf("RULE 7", "true");
                    //sb.append("\nRule #7 Error at word/s: " + words[RuleSeven(words)][0]); suggest
                    // final String suggst[] = {words[ruleSeven][0],
                    //        (new Inflector().pluralize(words[ruleSeven - 1][0]) + new Inflector().singularize(words[ruleSeven][0])),
                    //        (new Inflector().singularize(words[ruleSeven - 1][0]) + new Inflector().pluralize(words[ruleSeven][0]))
                    //};
                    ArrayList<String> ruleSevenList = new ArrayList<String>();
                    ruleSevenList.add(Integer.toString(x));
                    ruleSevenList.add("2"); ////COLOR
                    ruleSevenList.add(words[ruleSeven - 1][0] + " " + words[ruleSeven][0]);
                    ruleSevenList.add(new Inflector().pluralize(words[ruleSeven - 1][0]) + " " + new Inflector().singularize(words[ruleSeven][0]));
                    ruleSevenList.add(new Inflector().singularize(words[ruleSeven - 1][0]) + " " + new Inflector().pluralize(words[ruleSeven][0]));
                    suggestions.add(ruleSevenList);
                    ctr++;
                    //sb.append("\nRule 7");
                }
                else if (ruleEight > -1) {
                    Log.wtf("RULE 8", "true");
                    //sb.append("\nRule #8 Error at word/s: " + words[RuleEight(words)][0]);
                    //  sb = new StringBuffer(sentence.replace(" " + words[RuleEight(words)][0] + " ",
                    //        " " + new Inflector().pluralize(words[RuleEight(words)][0]) + " "));
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ruleEight][0]);
                    ruleList.add(new Inflector().pluralize(words[ruleEight][0]));
                    //RuleEight(words) = ruleEight
                    suggestions.add(ruleList);
                    //sb.append("\nRule 8");

                    ctr++;
                }
                else if (ruleNine > 0) {
                    Log.wtf("RULE 9", "true");
                    //sb.append("\nRule #9 Error at word/s: " + words[RuleNine(words)][0]);
                    //  sb = new StringBuffer(sentence.replace(" " + words[ruleNine][0] + " ",
                    //        " " + new Inflector().pluralize(words[leNine][0]) + " "));
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ruleNine][0]);
                    ruleList.add(new Inflector().pluralize(words[ruleNine][0]));
                    suggestions.add(ruleList);
                    //sb.append("\nRule 9");

                    ctr++;
                }
                else if (ruleTen > 0) {
                    Log.wtf("RULE 10", "true");
                    //sb.append("\nRule #10 Error at word/s: " + words[RuleTen(words)][0]);
                    //sb = new StringBuffer(sentence.replace(" " + words[ruleTen][0] + " ",
                    //" " + new Inflector().singularize(words[ruleTen][0]) + " "));
                    //sb.append("\nRule 10");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ruleTen][0]);
                    ruleList.add(new Inflector().singularize(words[ruleTen][0]));
                    suggestions.add(ruleList);
                    ctr++;
                    //sb.append("\nRule 10");
                }
                if (comp > 0 && errorOccur == 0) {
                    Log.wtf("RULE comp", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[comp][0]);
                    ruleList.add(new Inflector().singularize(words[comp][0]));
                    suggestions.add(ruleList);
                    errorOccur++;
                }
                if (ha > 0 && errorOccur == 0) {
                    Log.wtf("RULE ha", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[ha][0]);
                    if (words[ha][0].equalsIgnoreCase("has")) {
                        ruleList.add("have");
                    }
                    else if (words[ha][0].equalsIgnoreCase("have")) {
                        ruleList.add("has");
                    }

                    suggestions.add(ruleList);
                    errorOccur++;
                }
                if (basicyou > 0 && errorOccur == 0) {
                    Log.wtf("RULE basicyou", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[basicyou][0]);
                    ruleList.add("you?");
                    suggestions.add(ruleList);
                    errorOccur++;
                }
                if (basicyour > 0 && errorOccur == 0) {
                    Log.wtf("RULE basicyour", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("5"); ////COLOR
                    ruleList.add(words[basicyou][0]);
                    ruleList.add("your");
                    suggestions.add(ruleList);
                    errorOccur++;
                }
                if (proplural > 0 && errorOccur == 0) {
                    Log.wtf("RULE proplural", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("5"); ////COLOR
                    ruleList.add(words[proplural][0]);
                    if(words[prosingular][0].equalsIgnoreCase("you")){
                        ruleList.add("your");
                    }
                    else if(words[prosingular][0].equalsIgnoreCase("your")){
                        ruleList.add("you");
                    }
                    else {
                        ruleList.add("they");
                        ruleList.add("those");
                        ruleList.add("these");
                        ruleList.add("you");
                    }
                    suggestions.add(ruleList);
                    errorOccur++;
                }
                if (prosingular > 0 && errorOccur == 0) {
                    Log.wtf("RULE prosing", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("5"); ////COLOR
                    ruleList.add(words[prosingular][0]);
                    if(words[prosingular][0].equalsIgnoreCase("you")){
                        ruleList.add("your");
                    }
                    else if(words[prosingular][0].equalsIgnoreCase("your")){
                        ruleList.add("you");
                    }
                    else {
                        ruleList.add("that");
                        ruleList.add("this");
                        ruleList.add("he");
                        ruleList.add("she");
                        ruleList.add("it");
                    }
                    suggestions.add(ruleList);
                    errorOccur++;
                }
                if(basicI > -1 && errorOccur == 0){
                    Log.wtf("RULE basicI", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("5"); ////COLOR
                    ruleList.add(words[basicI][0]);
                    if(words[basicI][0].equalsIgnoreCase("is"))
                        ruleList.add("am");
                    else if(words[basicI][0].equalsIgnoreCase("are"))
                        ruleList.add("am");
                    else if(words[basicI][0].equalsIgnoreCase("were"))
                        ruleList.add("was");
                    else if(words[basicI][0].equalsIgnoreCase("has"))
                        ruleList.add("have");
                    else if(words[basicI][1].equals("VBZ"))
                        ruleList.add(new Inflector().singularize(words[basicI][0]));
                    suggestions.add(ruleList);
                    errorOccur++;
                }
                else if (basicplural > 0 && errorOccur == 0) {
                    Log.wtf("RULE basicplural", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[basicplural][0]);
                    /*if(words[basicplural][0].equalsIgnoreCase("is") && words[basicplural - 1][0].equalsIgnoreCase("i")) {
                        ruleList.add("am");
                        // Log.wtf("BASIC PLURAL", "I AM");
                    }
                    else {*/
                    if(words[basicplural][0].equalsIgnoreCase("am")){
                        ruleList.add("are");
                    }
                    else {
                        ruleList.add(new Inflector().singularize(words[basicplural][0]));
                    }
                    //Log.wtf("BASIC PLURAL", "I PLURAL");
                    //}
                    suggestions.add(ruleList);
                    Log.wtf("BASIC WARNING", "basicplural");
                    errorOccur++;
                }


                else if (basicsingular > 0 && errorOccur == 0) {
                    Log.wtf("RULE basicsing", "true");
                    ArrayList<String> ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("2"); ////COLOR
                    ruleList.add(words[basicsingular][0]);
                    if(words[basicsingular][0].equalsIgnoreCase("am")){
                        ruleList.add("is");
                    }
                    else {
                        ruleList.add(new Inflector().pluralize(words[basicsingular][0]));
                    }
                    suggestions.add(ruleList);
                    errorOccur++;
                }

                if (arti > 0) {
                    for (int i = 0; i < words.length; i++) {
                        if (words[i][0].equalsIgnoreCase("an") && i < words.length - 1) {
                       /* ArrayList<String> ruleList = new ArrayList<String>();
                        ruleList.add(" a ");
                        ruleList.add(" an ");
                        suggestions.add(ruleList);*/
                            sb = new StringBuffer(sb.toString().replace(" an ", " a "));
                        }
                        else if (words[i][0].equalsIgnoreCase("a") && i < words.length - 1) {
                       /* ArrayList<String> ruleList = new ArrayList<String>();
                        ruleList.add(" a ");
                        ruleList.add(" an ");
                        suggestions.add(ruleList);*/
                            sb = new StringBuffer(sb.toString().replace(" a ", " an "));
                        }
                    }
                }

                // int error[] = new int[words.length];
                //  error = PluralPos(words);
                //  boolean plurPos = false;
                //  for (int i = 0; i < error.length; i++) {
                //      if (!(error[i] == -1)) {
                //          ArrayList<String> ruleList = new ArrayList<String>();
                //          ruleList.add(words[error[i]][0]);
                //          //ruleList.add(new Inflector().pluralize(words[ruleFive][0]));
                //          suggestions.add(ruleList);
                //          Log.wtf("warning", error[i] + " " + words[error[i]][0]);
                //          plurPos = true;
                //      }
                //  }
                //  if(plurPos)
                //      sb.append("\nPlural & Possessive Rule Error");
                if(!PluralPos(words,x).isEmpty()) {
                    for (ArrayList<String> ruleList: PluralPos(words, x))
                        suggestions.add(ruleList);
                }
                if (!VerbTense(words).isEmpty()) {
                    ArrayList ruleList = new ArrayList<String>();
                    ruleList.add(Integer.toString(x));
                    ruleList.add("3"); ////COLOR

                    ArrayList<String> highlight = VerbTense(words);
                    ruleList.addAll(highlight);
                    suggestions.add(ruleList);

                }


            }
            //ArrayList<String> ruleList = new ArrayList<String>();
            //ruleList.add(words[0][0]);
            //ruleList.add("Pronoun Singular: " + prosingular);
            //ruleList.add("Pronound Plural: " + proplural);
            //ruleList.add("You: " + basicyou);
            //ruleList.add("Your: " + basicyour);
            //ruleList.add("Singular: " + basicsingular);
            //ruleList.add("Plural: " + basicplural);
            //suggestions.add(ruleList);
            //output = sb.toString();

        /*sb.append("1: " + ruleOne +
                "\n2: " + ruleTwo +
                "\n3: " + RuleThree(words, getSentenceInput(words)) +
                "\n4: " + ruleFour +
                "\n5: " + ruleFive +
                "\n6: " + ruleSix +
                "\n7: " + ruleSeven +
                "\n8: " + ruleEight +
                "\n9: " + ruleNine +
                "\n10: " + ruleTen +
                "\nbasic: " + basic +
                "\nrunOnFrag: " + runOnFrag +
                "\nCtr: " + ctr);
*/
            //output = sb.toStrin
            //
            // g();
            sntnc = sb.toString();
        }
        for (int i = 0; i < suggestions.size(); i++){
            Log.wtf("Output", suggestions.get(i).get(0) + " sentence");
            Log.wtf("Output", suggestions.get(i).get(1) + " color");
            for (int j = 2; j < suggestions.get(i).size(); j++) {
                Log.wtf("Output", suggestions.get(i).get(j));
            }
        }
    }

/*    public static int RunOnFragments(ArrayList<String> words, String words1[][]) {

        int runOn = 0;
        String[] storePhrase = new String[words.size()];
        int stringPos;
        int valPhrase = 0;
        int startPos;
        for (int i = 0; i < words.size(); i++) {
            if(words.get(i).contains("[") == true && words.get(i).contains(")") == true){
                stringPos = words.get(i).length();
                startPos = words.get(i).indexOf(")");
                storePhrase[valPhrase] = words.get(i).substring(startPos + 2, stringPos);
                valPhrase++;
            }
        }
        //identifying run on and fragments


        for (int i = 0; i < valPhrase; i++) {
            if(storePhrase[i].equalsIgnoreCase("NP")){
                for (int j = i; j < valPhrase; j++) {
                    if(storePhrase[j].equalsIgnoreCase("VP")){
                        runOn++;
                        i = j;
                        break;
                    }
                }
            }
        }
        if(runOn == 1) {// No Error
            return 3;
        }
        else if(runOn > 1){// Run On
            for (int i = 0; i < words1.length; i++) {
                if (words1[i][1].equalsIgnoreCase("CC")) {
                    return 3;
                }
            }
            return 1;
        }else{//Frag
            return 2;
        }
    } */

    //Each/Every
    public static int RuleOne(String words[][]){
        if(getSentenceInput(words).toLowerCase().contains("each") || getSentenceInput(words).toLowerCase().contains("every")){
            if(getTagsInput(words).contains("CC")){
                errorOccur++;
                for (int j = 0; j < words.length; j++) {
                    if(words[j][1].contains("VB") && words[j][1].contains("P"))
                        return j;
                }
            }
        }
        return 0;
    }
    //Singular Indefinite Pronoun
/*    another, anybody, anyone, anything, each, either, everybody,
**            everyone, everything, little, much, neither, nobody, no one,
**                    nothing, one, other, somebody, someone, something
* */
    public static int RuleFive(String words[][], boolean isQuest) {
        if (!isQuest){
            for (int i = 0; i < words.length; i++) {
                if (words[i][0].equalsIgnoreCase("another") || words[i][0].equalsIgnoreCase("anybody") ||
                        words[i][0].equalsIgnoreCase("anyone") || words[i][0].equalsIgnoreCase("anything") ||
                        words[i][0].equalsIgnoreCase("each") || words[i][0].equalsIgnoreCase("either") ||
                        words[i][0].equalsIgnoreCase("everybody") || words[i][0].equalsIgnoreCase("everyone") ||
                        words[i][0].equalsIgnoreCase("little") || words[i][0].equalsIgnoreCase("much") ||
                        words[i][0].equalsIgnoreCase("neither") || words[i][0].equalsIgnoreCase("nobody") ||
                        words[i][0].equalsIgnoreCase("nothing") || /*words[i][0].equalsIgnoreCase("one") ||*/
                        words[i][0].equalsIgnoreCase("other") || words[i][0].equalsIgnoreCase("somebody") ||
                        words[i][0].equalsIgnoreCase("someone") || words[i][0].equalsIgnoreCase("something") || words[i][0].equalsIgnoreCase("everything")) {
                    if (!getTagsInput(words).contains("CC")) {
                        errorOccur++;
                        for (int j = i; j < words.length; j++) {
                            if ((words[j][1].contains("VB") && words[j][1].contains("P")))
                                return j;
                        }
                    }
                }
            }
        }
        else{
            if(getSentenceInput(words).toLowerCase().contains("another") || getSentenceInput(words).toLowerCase().contains("anybody") ||
                    getSentenceInput(words).toLowerCase().contains("anyone") || getSentenceInput(words).toLowerCase().contains("anything") ||
                    getSentenceInput(words).toLowerCase().contains("each") || getSentenceInput(words).toLowerCase().contains("either") ||
                    getSentenceInput(words).toLowerCase().contains("everybody") || getSentenceInput(words).toLowerCase().contains("everyone") ||
                    getSentenceInput(words).toLowerCase().contains("little") ||getSentenceInput(words).toLowerCase().contains("much")||
                    getSentenceInput(words).toLowerCase().contains("neither") || getSentenceInput(words).toLowerCase().contains("nobody") ||
                    getSentenceInput(words).toLowerCase().contains("nothing") || /*getSentenceInput(words).toLowerCase().contains("one") ||*/
                    getSentenceInput(words).toLowerCase().contains("other") || getSentenceInput(words).toLowerCase().contains("somebody") ||
                    getSentenceInput(words).toLowerCase().contains("someone") ||getSentenceInput(words).toLowerCase().contains("something") || getSentenceInput(words).toLowerCase().contains("everything")){
                if(!getTagsInput(words).contains("CC")) {
                    errorOccur++;
                    for (int j = 0; j < words.length; j++) {
                        if ((words[j][1].contains("VB") && words[j][1].contains("P")))
                            return j;
                    }
                }
            }
        }
        return -1;
    }
    //Fractions
    public static int RuleSeven(String words[][]){
        if(getTagsInput(words).contains("CD") && getSentenceInput(words).contains("of the")) {
            errorOccur++;
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i][1].equals("CD")) {
                    if (!(i == words.length - 2)) {
                        if (words[i + 2][0].equals("of")) {
                            for (int j = i+2; j < words.length; j++) {
                                if (words[j][1].equals("NN") || words[j][1].equals("NNP")) {
                                    for (int k = j; k < words.length; k++) {
                                        if ((words[j][1].contains("VB") && words[j][1].contains("P")))
                                            return k;
                                    }
                                } else if(words[j][1].equals("NNS") || words[j][1].equals("NNPS")){
                                    for (int k = j; k < words.length; k++) {
                                        if ((words[j][1].contains("VB") && words[j][1].contains("Z")))
                                            return k;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
    //plural in form singular in meaning
    public static int RuleNine(String words[][]){
//        aerobics
//athletics billiards binoculars blues (type of music) civics crossroads darts dominoes
//economics eyeglasses gymnastics headquarters mathematics measles mumps news pants
//Philippines politics scissors series shingles shorts tongs trousers tweezers
        int numpos = 0;
        int wordspos = 0;
        for (int i = 0; i < words.length; i++) {

            if(words[i][1].equals("CD")){
                numpos = i;
            }
            if(words[i][0].equalsIgnoreCase("aerobics") || words[i][0].equalsIgnoreCase("athletics") ||
                    words[i][0].equalsIgnoreCase("billiards") || /*words[i][0].equalsIgnoreCase("binoculars") ||*/
                    words[i][0].equalsIgnoreCase("blues") || words[i][0].equalsIgnoreCase("civics") ||
                    words[i][0].equalsIgnoreCase("crossroads") || words[i][0].equalsIgnoreCase("darts") ||
                    words[i][0].equalsIgnoreCase("economics") ||
                    words[i][0].equalsIgnoreCase("gymnastics") ||
                    words[i][0].equalsIgnoreCase("headquarters") || words[i][0].equalsIgnoreCase("mathematics") ||
                    words[i][0].equalsIgnoreCase("measles") || words[i][0].equalsIgnoreCase("mumps") ||
                    words[i][0].equalsIgnoreCase("news") ||
                    words[i][0].equalsIgnoreCase("Philippines") || words[i][0].equalsIgnoreCase("politics") ||
                    words[i][0].equalsIgnoreCase("series") ||
                    words[i][0].equalsIgnoreCase("shingles")
                    ){
                wordspos = (i+1);

            }

        }
        if(wordspos > numpos || words[numpos][0].equalsIgnoreCase("one") && wordspos - numpos == 1){
            errorOccur++;
            for (int j = wordspos; j < words.length; j++) {
                if((words[j][1].contains("VB") && words[j][1].contains("P")))
                    return j;
            }
        }
        return 0;
    }
    //Nouns that are plural
    public static int RuleTen(String words[][]){

        int numpos = 0;
        int wordspos = 0;
        int pairpos = 0;

        for (int i = 0; i < words.length; i++) {

            if(words[i][1].equals("CD")){
                numpos = i;
            }
            if(words[i][0].equalsIgnoreCase("leggings") || words[i][0].equalsIgnoreCase("eyeglasses") ||
                    words[i][0].equalsIgnoreCase("jeans") || words[i][0].equalsIgnoreCase("binoculars") ||
                    words[i][0].equalsIgnoreCase("flares") || words[i][0].equalsIgnoreCase("spectacles") ||
                    words[i][0].equalsIgnoreCase("tights") || words[i][0].equalsIgnoreCase("goggles") ||
                    words[i][0].equalsIgnoreCase("pants") || words[i][0].equalsIgnoreCase("clothes") ||
                    words[i][0].equalsIgnoreCase("shorts") || words[i][0].equalsIgnoreCase("marginalia") ||
                    words[i][0].equalsIgnoreCase("overalls") || words[i][0].equalsIgnoreCase("paraphernalia") ||
                    words[i][0].equalsIgnoreCase("dungarees") || words[i][0].equalsIgnoreCase("regalia") ||
                    words[i][0].equalsIgnoreCase("pliers") || words[i][0].equalsIgnoreCase("folk") ||
                    words[i][0].equalsIgnoreCase("forceps") || words[i][0].equalsIgnoreCase("shenanigans") ||
                    words[i][0].equalsIgnoreCase("shears") || words[i][0].equalsIgnoreCase("loggerheads") ||
                    words[i][0].equalsIgnoreCase("tweezers") || words[i][0].equalsIgnoreCase("cahoots") ||
                    words[i][0].equalsIgnoreCase("tongs") ||
                    words[i][0].equalsIgnoreCase("glasses") || words[i][0].equalsIgnoreCase("amends")||
                    words[i][0].equalsIgnoreCase("smithereens") || words[i][0].equalsIgnoreCase("thanks")
                    || words[i][0].equalsIgnoreCase("pantaloons") ||  words[i][0].equalsIgnoreCase("glasses")
                    || words[i][0].equalsIgnoreCase("earnings") ||  words[i][0].equalsIgnoreCase("remains")
                    ||  words[i][0].equalsIgnoreCase("riches")
                    ||  words[i][0].equalsIgnoreCase("leftovers")
                    ||  words[i][0].equalsIgnoreCase("ruins") ||  words[i][0].equalsIgnoreCase("scissors")
                    ){
                errorOccur++;
                wordspos = i + 1;

            }
            if(words[i][0].equalsIgnoreCase("pair") || words[i][0].equalsIgnoreCase("pairs")){
                pairpos = i;
            }

        }
        //Pair of Scissors is... // Two scissors are... // Two pairs of scissors are..
        //Pair of scissors.
        if(words[pairpos][0].equalsIgnoreCase("pair") && pairpos < wordspos ){
            for (int j = 0; j < words.length; j++) {
                if((words[j][1].contains("VB") && words[j][1].contains("P")))
                    return j;
            }
        }
        //
        else if(wordspos - numpos == 1 || words[pairpos][0].equalsIgnoreCase("pairs") && numpos < pairpos && pairpos < wordspos
                ) {
            for (int j = 0; j < words.length; j++) {
                if ((words[j][1].contains("VB") && words[j][1].contains("Z")))
                    return j;
            }
        }
        else if(wordspos > 0 && numpos == 0 && pairpos == 0){
            for (int j = 0; j < words.length; j++) {
                if ((words[j][1].contains("VB") && words[j][1].contains("Z")))
                    return j;
            }
        }
        return 0;
    }
    //A number; The number
    public static int RuleThree(String sentence[][], String ref){
        boolean aNumber = false, theNumber = false; //, error = false;
        //int wordError = -1;

        //string search
        if(ref.toLowerCase().contains("a number"))
            aNumber = true;
        else if(ref.toLowerCase().contains("the number"))
            theNumber = true;
        //else
        //error = false;

        //validate
        if(aNumber){
            errorOccur++;
            for(int i = 0; i<sentence.length; i++){
                if(sentence[i][1].contains("VB")){
                    if(sentence[i][1].contains("Z")){
                        return i;
                    }
                }
            }
        }
        else if(theNumber){
            errorOccur++;
            for(int i = 0; i<sentence.length; i++){
                if(sentence[i][1].contains("VB")){
                    if(sentence[i][1].contains("P")){
                        return i;
                    }
                }
            }
        }
        //else
        //error = false;

        return -1;
    }

    public static String getSentenceInput(String words[][]){
        String ref = "";
        for(int i = 0; i<words.length; i++){
            ref = ref.concat(words[i][0] + " ");
        }
        return ref;
    }
    public static String getTagsInput(String words[][]){
        String ref = "";
        for(int i = 0; i<words.length; i++){
            ref = ref.concat(words[i][1] + " ");
        }
        return ref;
    }

    //(or, nor, either ...or, neither ...nor)
    public static int RuleTwo(String words[][], boolean isQuest){
        int start = 0;
        if(getSentenceInput(words).contains(" or ") || getSentenceInput(words).contains(" nor ")){
            errorOccur++;
            if(!isQuest){
                for(int i = 0; i<words.length; i++){
                    if(words[i][0].equals("or") || words[i][0].equals("nor"))
                        start = i+1;
                }
                for(; start<words.length-1; start++){
                    if(words[start][1].contains("NN") && words[start+1][1].contains("VB")){
                        if(((words[start][1].equals("NN") || words[start][1].equals("NNP")) && (words[start+1][1].contains("VB") && words[start+1][1].contains("Z"))) || ((words[start][1].equals("NNS") || words[start][1].equals("NNPS")) && (words[start+1][1].equals("VB") && words[start+1][1].equals("P"))))
                            return 0;
                        else
                            return start+1;
                    }
                }
            }
            else{
                boolean plural = false;
                if(words[0][1].contains("VB") && words[0][1].contains("P"))
                    plural = true;
                for (int i = 0; i < words.length-1; i++) {
                    if(words[i][1].contains("NN")){
                        if(plural){
                            if(words[i][1].equals("NN") || words[i][1].equals("NNP"))
                                return i;
                        }
                        else{
                            if(words[i][1].equals("NNS") || words[i][1].equals("NNPS"))
                                return i;
                        }
                        break;
                    }

                }
            }

        }
        return 0;
    }



    //Adjectives used as nouns are considered plural
    //May flaws pa sa tags ****
    public static int RuleFour(String words[][], boolean isQuest){
        for(int i = 0; i < words.length - 1; i++){
            if(!isQuest){
                if(words[i][1].equals("JJ") && words[i+1][1].contains("VB")){
                    Log.wtf("dsgdfgfgfgdf", "RULE FOUUUUUUUUUUR");
                    errorOccur++;
                    if(words[i+1][1].contains("VB") && words[i+1][1].contains("Z")) {
                        Log.wtf("RULE FOUR", "not question");
                        return i + 1;
                    }
                }
            }
            else{
                if( i != words.length - 2 && (words[i][1].contains("VB") && words[i+1][1].equals("DT") && words[i+2][1].equals("JJ"))){
                    errorOccur++;
                    if(words[i][1].contains("VB") && words[i][1].contains("Z")) {
                        Log.wtf("RULE FOUR", " " + i);
                        return i;
                    }
                }
            }
        }
        return -1;
    }


    //Plural indefinite pronoun = plural verb
    public static int RuleSix(String words[][]){
        //HOW TO IDENTIFY PLURAL INDEFINITE PRONOUNS!!!!
        //several, many, others, few and both

        if(getSentenceInput(words).toLowerCase().contains("several") || getSentenceInput(words).toLowerCase().contains("many") ||
                getSentenceInput(words).toLowerCase().contains("others") || getSentenceInput(words).toLowerCase().contains("few") ||
                getSentenceInput(words).toLowerCase().contains("both") || getSentenceInput(words).toLowerCase().contains("all")){
            errorOccur++;
            for (int j = 0; j < words.length; j++) {
                if(words[j][1].contains("VB") && words[j][1].contains("Z"))
                    return j;
            }
        }
        return -1;
    }

    public static int RuleEight(String words[][]){
        for (int i = 0; i < words.length; i++) {
            //if(isNumeric(words[i][0]) || getTagsInput(words).contains("CD") && !getSentenceInput(words).contains("of the")){
            if(isNumeric(words[i][0]) || words[i][1].equals("CD")){
                errorOccur++;
                for (int j = 0; j < words.length; j++) {
                    if(words[j][1].contains("VB") && words[j][1].contains("P"))
                        return j;
                }
            }
        }

        return -1;
    }

    //basic rule(Nouns that are plural take plural verb, nouns that are singular take singular verb
    //public static int BasicRule(String words[][]){
    //    for (int i = 0; i<words.length; i ++){
    //        if(words[i][1].equals("NN") || words[i][1].equals("NNP")){
    //            for (int j = 0; j < words.length; j++) {
    //                if(words[j][1].equals("VBP"))
    //                {
    //                    return j;
    //                }
    //            }
    //        }
    //        if(words[i][1].equals("NNS") || words[i][1].equals("NNPS")){
    //            for (int j = 0; j < words.length; j++) {
    //                if(words[j][1].equals("VB"))
    //                {
    //                    return j;
    //                }
    //            }
    //        }
    //    }
    //
    //     return 0;
    // }

    //basic rule(Nouns that are plural take plural verb, nouns that are singular take singular verb
    public static int BasicRulePlural(String words[][]){
        for (int i = 0; i<words.length; i ++) {
            //roses [is] beautiful
            if(words[i][1].contains("VB") && words[i][1].contains("Z")){
                for (int j = i; j >0; j--){
                    if(words[j][1].equalsIgnoreCase("NNS") || words[j][1].equalsIgnoreCase("NNPS")){
                        return i;
                    }
                    else if(words[j][1].equalsIgnoreCase("NN") || words[j][1].equalsIgnoreCase("NNP")){
                        return 0;
                    }
                }
            }
            //you [is], [deserves], [plays]
            if ((words[i][1].equalsIgnoreCase("PRP") && (words[i][0].equalsIgnoreCase("they")
                    || words[i][0].equalsIgnoreCase("you") || words[i][0].equalsIgnoreCase("we")) || words[i][0].equalsIgnoreCase("those")
                    || words[i][0].equalsIgnoreCase("these") )) {

                for (int j = i; j < words.length; j++) {
                    if ((words[j][1].contains("VB") && words[j][1].contains("Z")) || words[j][0].equalsIgnoreCase("am") || words[j][0].equalsIgnoreCase("is")) {
                        return j;
                    }
                }
            }
            if(words[0][1].equals("VBZ") && words[0][0].equalsIgnoreCase("is")){
                for (int j = 0; j < words.length; j++) {
                    // is your [dogs]
                    if(words[j][1].contains("NNS")){
                        return j;
                    }
                    //[is] you tired, [is] you okay
                    if(words[i][0].equalsIgnoreCase("you")){
                        for (int k = j; k < words.length; k ++)
                            if(!words[k][1].equals("NN")
                                    || !words[k][1].equals("NNS")
                                    || !words[k][1].equals("NNP")
                                    || !words[k][1].equals("NNPS")){

                                if(words[0][0].equalsIgnoreCase("is")){
                                    return k;
                                }
                            }

                    }
                }
            }
            // starts with wh-question
            if(words[0][1].equals("WRB") || words[0][1].equals("WP")){
                //wh question is
                if(i + 1 < words.length -1) {
                    if (words[i + 1][0].equals("is")) {
                        for (int j = 0; j < words.length; j++) {
                            // error on how is your [dogs]
                            if (words[j][1].equals("PRP") && words[j][0].equalsIgnoreCase("your")) {
                                for (int k = j; k < words.length; k++) {
                                    if (words[k][1].equals("NNS")) {
                                        return k;
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }
        return 0;
    }
    public static int BasicRuleYou(String words[][]){
        for (int i = 0; i < words.length; i++) {
            // starts with wh-question
            if (words[i][1].equals("WRB") || words[i][1].equals("WP")) {
                // wh question are
                for (int j = i; j < words.length; j++) {
                    if (words[j][0].equals("are")) {
                        for (int k = j; k < words.length; k++) {
                            // how are you..
                            if (words[k][1].equals("PRP$") || words[k][0].equalsIgnoreCase("your?")
                                    || words[k][0].equals("your")) {
                                return k;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
    public static int BasicRuleYour(String words[][]){
        for (int i = 0; i< words.length; i ++) {

            if (words[i][1].equals("VBZ") && words[i][0].equalsIgnoreCase("is")) {
                for (int j = 0; j < words.length; j++) {
                    // is she [you] dog
                    if (words[j][0].equalsIgnoreCase("she") || words[j][0].equalsIgnoreCase("he")) {
                        for (int k = j; k < words.length; k++) {
                            if (words[k][0].equalsIgnoreCase("you")) {
                                return k;
                            }
                        }
                    }
                }
            }
            // starts with are
            if(words[i][1].equals("VBP") && words[i][0].equalsIgnoreCase("are")) {
                for (int j = 0; j < words.length; j++) {
                    if (words[j][0].equalsIgnoreCase("they")) {
                        // are they [you] dogs
                        for (int k = j; k < words.length; k++) {
                            if (words[k][0].equalsIgnoreCase("you")) {
                                return k;
                            }
                        }
                    }
                }
            }
            // starts with wh-question
            if(words[i][1].equals("WRB") || words[i][1].equals("WP")) {
                // wh question are
                if (words[i + 1][0].equals("are")) {
                    for (int j = 0; j < words.length; j++) {
                        //error on how are [you] dogs
                        if (words[j][1].equals("PRP")) {
                            return j;
                        }
                    }
                }
            }
        }
        return 0;
    }
    public static int BasicRuleSingular(String words[][]){
        for (int i = 0; i<words.length; i ++) {
            // love [are] blind
            if ((words[i][1].equals("NN") || words[i][1].equals("NNP")) && !words[i][0].contains("'")) {
                for (int j = 0; j < words.length; j++) {
                    if (words[j][1].contains("VB") && words[j][1].contains("P")) {
                        Log.wtf("Basic 1", "true");
                        return j;
                    }
                }
            }
            // he [are], [deserve], [play]
            if ((words[i][1].equalsIgnoreCase("PRP") && (words[i][0].equalsIgnoreCase("he")
                    || words[i][0].equalsIgnoreCase("she") || words[i][0].equalsIgnoreCase("it"))
                    || words[i][0].equalsIgnoreCase("this") || words[i][0].equalsIgnoreCase("that"))) {

                for (int j = i; j < words.length; j++) {
                    if (words[j][1].contains("VB") && words[j][1].contains("P")) {
                        Log.wtf("Basic 2", "true");
                        return j;
                    }
                }
            }
            // starts with are
            if(words[0][1].equals("VBP") && words[0][0].equalsIgnoreCase("are")){
                for (int j = 0; j < words.length; j++) {
                    // are your [dog]
                    if(words[j][1].contains("NN")){
                        Log.wtf("Basic 3", "true");
                        return j;
                    }
                }
            }
            // starts with wh-question
            if(words[0][1].equals("WRB") || words[0][1].equals("WP")) {
                // wh question are
                if(i + 1 < words.length-1) {
                    if (words[i + 1][0].equals("are")) {
                        // error on how are your [dog]
                        for (int j = 0; j < words.length; j++) {
                            if (words[j][1].equals("NN")) {
                                Log.wtf("Basic 4", "true");
                                return j;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }
    public static int BasicRulePronounSingular(String words[][]){
        for (int i = 0; i < words.length; i++) {
            // starts with is
            if (words[0][1].equals("VBZ") && words[0][0].equalsIgnoreCase("is")) {
                for (int j = 0; j < words.length; j++) {
                    // is [they], is [those], is[these], is [you]
                    if(words[j][1].equals("PRP") && (words[j][0].equals("they") || words[j][0].equals("you"))

                            ||words[j][1].equals("DT") &&
                            (words[j][0].equalsIgnoreCase("those")
                                    || words[j][0].equalsIgnoreCase("these"))  ){

                        for (int k = j; k < words.length; k++) {
                            if (words[k][1].equalsIgnoreCase("NN")
                                    || words[k][1].equalsIgnoreCase("NNP")
                                    ){

                                return j;

                            }
                        }

                    }
                }
            }
            //wh question is
            if(words[i][0].equals("is")){
                for (int j = 0; j < words.length; j++) {
                    // error on is [those]
                    if((words[j][0].equalsIgnoreCase("those") || words[j][0].equalsIgnoreCase("these"))
                            || (words[j][0].equalsIgnoreCase("those?") || words[j][0].equalsIgnoreCase("these?"))){
                        return j;
                    }

                }
            }
        }
        return 0;
    }
    public static int BasicRulePronounPlural(String words[][]){
        for (int i = 0; i < words.length; i ++) {
            // starts with are
            if (words[0][1].equals("VBP") && words[0][0].equalsIgnoreCase("are")) {
                for (int j = 0; j < words.length; j++) {
                    // are [that], are [this], are [he], are [she]
                    if (words[j][1].equals("PRP") && (words[j][0].equals("he") || words[j][0].equals("she") && words[j][0].equals("it"))
                            || words[j][1].equals("DT") && (words[j][0].equalsIgnoreCase("that")
                            || words[j][0].equalsIgnoreCase("this"))) {
                        return j;
                    }
                }
            }
            // starts with wh-question
            if (words[0][1].equals("WRB") || words[0][1].equals("WP")) {
                // wh question are
                if(i+1 < words.length - 1) {
                    if (words[i + 1][0].equals("are")) {

                        for (int j = 0; j < words.length; j++) {
                            //are [that]
                            if ((words[j][0].equalsIgnoreCase("that") || words[j][0].equalsIgnoreCase("this"))
                                    || (words[j][0].equalsIgnoreCase("that?") || words[j][0].equalsIgnoreCase("this?"))) {
                                return j;
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static int BasicI(String[][] words){
        for(int i = 0; i < words.length; i++) {
            if(words[i][0].equalsIgnoreCase("i")){
                for(int j = 0; j < words.length; j++) {
                    if(words[j][0].equalsIgnoreCase("is"))
                        return j;
                    if(words[j][0].equalsIgnoreCase("are"))
                        return j;
                    if(words[j][0].equalsIgnoreCase("were"))
                        return j;
                    if(words[j][0].equalsIgnoreCase("has"))
                        return j;
                    if(words[j][1].equals("VBZ"))
                        return j;
                }

            }
        }
        return -1;
    }

    ///what if 2 errors in 1 sentence
    public static ArrayList<ArrayList<String>> PluralPos(String words[][], int x){
        ArrayList<ArrayList<String>> sugg = new ArrayList<ArrayList<String>>();
        int num = words.length;
        int errors[] = new int[num];
        int temp = 0, ctr = 0;
        ArrayList<String> wordErrors;
        int k = 0;
        //if(getSentenceInput(words).contains("'")){
        for (int i = 0; i < words.length-1; i++) {
            if(words[i][0].contains("'")){
                if( !(words[i+1][1].contains("NN")) || words[i][0].contains("s's")) {
                    Log.wtf("warning", "panget gino " + i);
                    temp++;
                    ctr++;
                    //wordErrors.add(Integer.toString(ctr));
                    wordErrors = new ArrayList<String>();
                    wordErrors.add(Integer.toString(x));
                    wordErrors.add("4"); ////COLOR
                    wordErrors.add(words[i][0]);
                    if(!(words[i+1][1].contains("NN")) && !words[i][1].contains("P")) {
                        wordErrors.add(new Inflector().pluralize(words[i][0].substring(0, words[i][0].length() - 2)));
                    }
                    else if(words[i][0].contains("s's") && words[i][1].contains("P") && !(words[i+1][1].contains("NN")))
                        wordErrors.add(words[i][0].substring(0,words[i][0].length()-2));
                    else
                        wordErrors.add(words[i][0].substring(0,words[i][0].length()-1));
                    errors[k] = i;
                    sugg.add(wordErrors);
                }
            }
            else {
                Log.wtf("Plurpos", "no apos");
                if ((words[i][1].equals("NNS") || words[i][1].equals("NNPS") || words[i][1].equals("NNP")) && words[i + 1][1].contains("NN")) {
                    temp++;
                    ctr++;
                    wordErrors = new ArrayList<String>();
                    wordErrors.add(Integer.toString(x));
                    wordErrors.add("4");
                    wordErrors.add(words[i][0]);
                    if(words[i][0].charAt(words[i][0].length() - 1) == 's')
                        wordErrors.add(words[i][0] + "\'");
                    else
                        wordErrors.add(words[i][0] + "\'s");
                    errors[k] = i;
                    sugg.add(wordErrors);
                }
            }
            k++;
        }
        //}
        if(temp == 0) {
            //wordErrors.clear();
            //Log.wtf("PLURPOS3", "NO ERROR");
            for (int i = 0; i < num; i++)
                errors[i] = -1;
        }
        return sugg;
    }

    public static ArrayList<String> VerbTense(String words[][]){
        ArrayList<String> verbErrors = new ArrayList<String>();
        int present = 0, past = 0, future = 0, pastdid = 0, advpast = 0;
        boolean error = false;
        if(getSentenceInput(words).toLowerCase().contains("at the moment") || getSentenceInput(words).toLowerCase().contains("for a little while")
                || getSentenceInput(words).toLowerCase().contains("as we speak")){ //MORE THAN ONE_WORD HELPPPPPPPPPPPP
            present++;
        }
        for (int i = 0; i < words.length; i++) {
            if(words[i][1].equals("MD")) {
                future++;
                for (int j = i + 1; j < words.length - 1; j++) {
                    if (words[j][1].contains("VB")) {
                        if (!words[j][1].equals("VB")) {
                            error = true;
                            verbErrors.add(words[i][0]);
                            verbErrors.add(words[j][0]);
                        }

                    }
                }
            }
            if((words[i][1].contains("VBD") && !words[i][0].equalsIgnoreCase("did")) || words[i][1].equalsIgnoreCase("VBN")){
                verbErrors.add(words[i][0]);
                past++;
            }
            if(words[i][1].equalsIgnoreCase("VBG") || words[i][1].equalsIgnoreCase("VBP") || words[i][1].equalsIgnoreCase("VBZ")/* || words[i][1].equalsIgnoreCase("VB")*/){
                verbErrors.add(words[i][0]);
                present++;
            }
            if(words[i][0].equalsIgnoreCase("today") || words[i][0].equalsIgnoreCase("tonight")
                    || words[i][0].equalsIgnoreCase("always") || (words[i][0].contains("every") && (words[i][1].equals("DT")))
                    || words[i][0].equalsIgnoreCase("usually") || words[i][0].equalsIgnoreCase("often")
                    || words[i][0].equalsIgnoreCase("everyday")
                    || words[i][0].equalsIgnoreCase("sometimes") || words[i][0].equalsIgnoreCase("rarely")
                    || words[i][0].equalsIgnoreCase("never") || words[i][0].equalsIgnoreCase("now")
                    || words[i][0].equalsIgnoreCase("currently") || words[i][0].equalsIgnoreCase("presently")){
                verbErrors.add(words[i][0]);
                present++;
            }
            if(words[i][0].equalsIgnoreCase("ago") || words[i][0].equalsIgnoreCase("last")
                    || words[i][0].equalsIgnoreCase("yesterday") || words[i][0].equalsIgnoreCase("before")){
                verbErrors.add(words[i][0]);
                advpast++;
            }
            if(words[i][0].equalsIgnoreCase("next") || words[i][0].equalsIgnoreCase("tomorrow")){
                verbErrors.add(words[i][0]);
                future++;
            }
            if(words[i][0].equalsIgnoreCase("did")){
                for(int j = i + 1; j < words.length - 1; j++){
                    if(words[j][1].contains("VB")){
                        if(!words[j][1].equals("VB")) {
                            verbErrors.add(words[i][0]);
                            verbErrors.add(words[j][0]);
                            Log.wtf("ENTER", "PAST DID " + words[j][1]);
                            pastdid++;
                        }
                    }
                }
            }

        }
        Log.wtf("VERB TENSE", "past " + past + " present " + present + " future " + future + " error " + error);
        Log.wtf("VERB TENSE", "1 " + (future == 0 && present == 0 && (past > 0 || advpast > 0)) + " 2 " + (present > 0 && (past == 0  || advpast == 0) && future == 0) + " 3 " + (future > 0 && (past == 0  && advpast == 0) && present == 0) + " 4 " + error + " past did " + pastdid + " " + (pastdid == 0));

        Log.wtf("VERB TENSE WARNINGGGG", "RESULT : " + (((future == 0 && present == 0 && (past > 0 && advpast > 0)) || (present > 0 && (past == 0  && advpast == 0) && future == 0) || (future > 0 && (past == 0  && advpast == 0) && present == 0) || !error ) && (pastdid == 0)));
        if(((future == 0 && present == 0 && (past > 0 && advpast > 0)) || (present > 0 && (past == 0  && advpast == 0) && future == 0) || (future > 0 && (past == 0  && advpast == 0) && present == 0) || !error ) && (pastdid == 0)){
            Log.wtf("NO ERROR ", "verb tense");
            verbErrors.clear();
        }
        return verbErrors;
    }

    public static int Fragments(String words[][], ArrayList<String> words1, boolean isQuest){
        int nverb = 0;
        boolean frag = true;
        String[] storePhrase = new String[words1.size()];
        int stringPos;
        int valPhrase = 0;
        int startPos;
        for (int i = 0; i < words1.size(); i++) {
            if(words1.get(i).contains("[") == true && words1.get(i).contains(")") == true){
                stringPos = words1.get(i).length();
                startPos = words1.get(i).indexOf(")");
                storePhrase[valPhrase] = words1.get(i).substring(startPos + 2, stringPos);
                valPhrase++;
            }
        }
        for (int i = 0; i < words.length; i++) {
            //no subject Ex. Running down the streets.
            if(words[i][1].equalsIgnoreCase("VB") ||
                    words[i][1].contains("VBD") ||
                    words[i][1].equalsIgnoreCase("VBG") ||
                    words[i][1].equalsIgnoreCase("VBN") ||
                    words[i][1].equalsIgnoreCase("VBP") ||
                    words[i][1].equalsIgnoreCase("VBZ") ||
                    words[i][1].equalsIgnoreCase("IN")){
                for (int j = i; j >= 0; j--) {
                    if(words[j][1].equalsIgnoreCase("NN") ||
                            words[j][1].equalsIgnoreCase("NNS") ||
                            words[j][1].equalsIgnoreCase("NNP") ||
                            words[j][1].equalsIgnoreCase("NNPS") ||
                            words[j][1].equalsIgnoreCase("PRP") ||
                            words[j][1].equalsIgnoreCase("JJ") ||
                            words[j][0].equalsIgnoreCase("this") ||
                            words[j][0].equalsIgnoreCase("that") ||
                            words[j][0].equalsIgnoreCase("these") ||
                            words[j][0].equalsIgnoreCase("those")){
                        frag = false;
                    }
                }
                if(words[i][1].equalsIgnoreCase("VBG")){
                    if( i < words.length - 1 && (!(words[i+1][1].equalsIgnoreCase("MD")))){
                        for (int j = 0; j < words.length; j++) {
                            if(j < words.length - 1 && words[j][1].equalsIgnoreCase("MD")){
                                if(words[j+1][1].equalsIgnoreCase("VB")){
                                    return 0;
                                }
                            }
                        }
                    }
                }
            }
            if(words[0][1].equalsIgnoreCase("VBD") ||
                    words[0][1].equalsIgnoreCase("VBG")){
                return 1;
            }
        }
        if(isQuest){
            return 0;
        }
        if(frag == true){

            return 1;
        }
        for (int i = 0; i < words.length; i++) {
            //no verb Ex. A time of wonder and amazement.
            //            Clothes and shoes scattered around the room.

            if(words[i][1].equalsIgnoreCase("VB") ||
                    words[i][1].equalsIgnoreCase("VBD") ||
                    words[i][1].equalsIgnoreCase("VBG") ||
                    words[i][1].equalsIgnoreCase("VBN") ||
                    words[i][1].equalsIgnoreCase("VBP") ||
                    words[i][1].equalsIgnoreCase("VBZ") ||
                    words[i][1].equalsIgnoreCase("VBDZ") ||
                    words[i][1].equalsIgnoreCase("VBDP") ||
                    words[i][1].equalsIgnoreCase("IN")){
                ++nverb;

            }
            if(words[i][1].equalsIgnoreCase("VBN") ||
                    words[i][1].equalsIgnoreCase("VBG")){
                if((words[i-1][1].equalsIgnoreCase("VB") ||
                        words[i-1][1].equalsIgnoreCase("VBZ") ||
                        words[i-1][1].equalsIgnoreCase("VBP") ||
                        words[i-1][1].equalsIgnoreCase("VBD") ||
                        words[i-1][1].equalsIgnoreCase("JJ") ||
                        words[i-1][1].equalsIgnoreCase("RB") ||
                        words[i-1][1].equalsIgnoreCase("PRP$") ||
                        words[i-1][1].equalsIgnoreCase("IN") ||
                        words[i-1][1].equalsIgnoreCase("PRP") ||
                        words[i-1][1].equalsIgnoreCase("DT")) ){
                    return 0;
                }
                else{
                    for (int j = 0; j < valPhrase ; j++) {
                        if(words1.get(j).contains("SBAR")){
                            return 0;
                        }
                    }

                    return 1;
                }
            }
            if(i == words.length - 1 && nverb > 0){
                return 0;
            }

        }

        return 1;


    }

    public static int RunOn(ArrayList<String> words, String words1[][]){
        int runOn = 0;
        String[] storePhrase = new String[words.size()];
        int stringPos;
        int valPhrase = 0;
        int startPos;
        for (int i = 0; i < words.size(); i++) {
            if(words.get(i).contains("[") == true && words.get(i).contains(")") == true){
                stringPos = words.get(i).length();
                startPos = words.get(i).indexOf(")");
                storePhrase[valPhrase] = words.get(i).substring(startPos + 2, stringPos);
                valPhrase++;
            }
        }
        //identifying run on and fragments


        for (int i = 0; i < valPhrase; i++) {
            if(storePhrase[i].equalsIgnoreCase("NP")){
                for (int j = i; j < valPhrase; j++) {
                    if(storePhrase[j].equalsIgnoreCase("VP")){
                        runOn++;
                        i = j;
                        break;
                    }
                }
            }
        }
        if(runOn > 1){// Run On
            for (int i = 0; i < words1.length; i++) {
                if (words1[i][1].equalsIgnoreCase("CC")) {
                    return 0;
                }
            }
            for (int i = 0; i < valPhrase; i++) {
                if(storePhrase[i].equalsIgnoreCase("SBAR")){
                    return 0;
                }
            }
            return 1;
        }
        return 0;
    }

    public static int IndefiniteArticle(String words[][]){
        String IndefArtiWord;
        for (int i = 0; i < words.length; i++) {
            if(words[i][0].equalsIgnoreCase("an") && i < words.length - 1){
                IndefArtiWord = words[i+1][0].toLowerCase(Locale.FRENCH);
                if(!(IndefArtiWord.startsWith("a", 0) || IndefArtiWord.startsWith("e", 0)
                        || IndefArtiWord.startsWith("i", 0)|| IndefArtiWord.startsWith("o", 0)
                        || IndefArtiWord.startsWith("0", 0))){
                    return i; // return if the words does not starts with a vowel and the previous article is an
                }
            }
            else if (words[i][0].equalsIgnoreCase("a") && i < words.length - 1){
                IndefArtiWord = words[i+1][0].toLowerCase(Locale.FRENCH);
                if(IndefArtiWord.startsWith("a", 0) || IndefArtiWord.startsWith("e", 0)
                        || IndefArtiWord.startsWith("i", 0)|| IndefArtiWord.startsWith("o", 0)
                        || IndefArtiWord.startsWith("0", 0)){
                    return i; // return if the words starts with a vowel and the previous article is a
                }
            }
        }
        return 0;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public char SetPunctuation(String[][] words) {
        String pattern = ".!?";
        String intrrgtvs  = " who what where when why how which "
                + "wherefore whatever whom whose where with "
                + "whither whence is are am was ";
        String lastWord = (words[words.length-1][0]);
        if (!pattern.contains(Character.toString(lastWord.charAt(lastWord.length() - 1)))) {
            if (intrrgtvs.contains(" " + words[0][0].toLowerCase() + " ")) {
                isQuestion = true;
                return '?';
            }
            else
                return '.';
        }
        return '.';
    }

    public static int Compound(String words[][]){

        for (int i = 0; i < words.length; i++) {
            if(words[i][0].equalsIgnoreCase("and")){
                for (int j = i; j < words.length; j++) {
                    if(words[j][1].equalsIgnoreCase("VBZ") || words[j][0].equalsIgnoreCase("was")){
                        return j;
                    }
                }

            }
        }
        return 0;
    }

    public static int Have(String words[][]){
        for (int i = 0; i < words.length; i++) {
            if(words[i][0].equalsIgnoreCase("I") || words[i][0].equalsIgnoreCase("you")
                    || words[i][0].equalsIgnoreCase("we") || words[i][0].equalsIgnoreCase("they")
                    || words[i][1].equalsIgnoreCase("NNS") || words[i][1].equalsIgnoreCase("NNPS")){
                for (int j = i; j < words.length; j++) {
                    if(words[j][0].equalsIgnoreCase("has")){
                        return j;
                    }
                }

            }
            else if(words[i][0].equalsIgnoreCase("he") || words[i][0].equalsIgnoreCase("she")
                    || words[i][0].equalsIgnoreCase("it") || words[i][1].equalsIgnoreCase("NN")
                    || words[i][0].equalsIgnoreCase("NNP") ){
                for (int j = i; j < words.length; j++) {
                    if(words[j][0].equalsIgnoreCase("have")){
                        return j;
                    }
                }

            }
        }
        return 0;
    }

    public String GetResult(){
        return output;
    }

    public ArrayList<ArrayList<String>> GetSuggestion(){
        return suggestions;
    }

    public String GetSentence(){
        return sntnc;
    }
}
