package com.gino.grammify;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckGrammar extends AppCompatActivity {

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    private static Context context;
    public TextView status;
    public String sentence;

    public MainActivity ma;
    public SpellCheck sc;
    static POSTagger pst;

    public Typeface face;

    static Menu mm;
    ArrayList<ArrayList<Integer>> coloredText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grammar_checked);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        face= Typeface.createFromAsset(getAssets(), "font_chalk.ttf");
        context = getApplicationContext();
        status = (TextView) findViewById(R.id.textView);
        ma = new MainActivity();
        sentence = ma.GetText();
        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(sentence);

        textView3.setTypeface(face);

        final TextView textView2 = (TextView)findViewById(R.id.textView2);
        textView2.setTypeface(face);
        final TextView textView4 = (TextView)findViewById(R.id.textView4);
        textView4.setTypeface(face);
        final TextView textView5 = (TextView)findViewById(R.id.textView5);
        textView5.setTypeface(face);

        //TEMP
        final View topLevelLayout = findViewById(R.id.top_layout_second);
        topLevelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topLevelLayout.setVisibility(View.INVISIBLE);
            }
        });
        //TEMP



        try {
            //startDialog(sentence);
            SpellCheck sc = new SpellCheck(sentence, context);
            sentence = SpellCheck.getString();
            CorrectWords(sentence);
            sentence = SpellCheck.getString();
        } catch (Exception e) {
            Log.wtf("EXCEPTION: ", e.toString());
        }

        coloredText = new ArrayList<ArrayList<Integer>>();
    }

    public void SetSuggestion(final ArrayList<ArrayList<String>> suggst, final String paragraph) {
        final TextView textView2 = (TextView) findViewById(R.id.textView2);
        final ArrayList<String> sentence = new ArrayList<String>();
        int [] errors = {0,0,0,0,0,0};

        // Sentence Tokenization
        Pattern delimiter = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)"
                        + "[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
                Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher sentenceSplit = delimiter.matcher(paragraph);


        while (sentenceSplit.find()) {
            //Sentence
            sentence.add(sentenceSplit.group()
                    .replaceAll("[^a-zA-Z'\\s]", "")
                    .replaceAll("\\s+", " ") +
                    // punctuation
                    Character.toString(paragraph.charAt(sentenceSplit.end() - 1)));
        }

        Log.wtf("Complete Sentence", paragraph);
        Log.wtf("# of sentence", Integer.toString(sentence.size()));
        final SpannableString ss = new SpannableString(paragraph);
        for (int i = 0; i < sentence.size(); i++) {
            final int positionI = i;
            //FIND IF THERE IS SOMETHING TO HIGHLIGHT IN A SENTENCE
            for (int j = 0; j < suggst.size(); j++) {
                if (Integer.parseInt(suggst.get(j).get(0)) == i) {
                    Log.wtf("CurSentence", sentence.get(i));
                    Log.wtf("Color", suggst.get(j).get(1));
                    if (Integer.parseInt(suggst.get(j).get(1)) == 0) { // RUN ON
                        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)),
                                paragraph.indexOf(sentence.get(i)), //START
                                paragraph.indexOf(sentence.get(i)) + sentence.get(i).length(), //END
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        errors[0]++;
                    }
                    else if (Integer.parseInt(suggst.get(j).get(1)) == 1) { // FRAGMENTS
                        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)),
                                paragraph.indexOf(sentence.get(i)), //START
                                paragraph.indexOf(sentence.get(i)) + sentence.get(i).length(), //END
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        errors[1]++;
                    }
                    else if (Integer.parseInt(suggst.get(j).get(1)) == 2) { // S/V AGREEMENT
                        final int positionJ = j;
                        //FIND WORD
                        for (int k = 0; k < suggst.get(j).size(); k++) {
                            Log.wtf("SENTENCE", sentence.get(i));
                            Log.wtf("WORD", suggst.get(j).get(2));
                            if (sentence.get(i).contains(suggst.get(j).get(2))) {
                                final String markedWord = suggst.get(j).get(2);
                                Log.wtf("Marked word", markedWord);
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View textView) {
                                        PopupMenu popup = new PopupMenu(CheckGrammar.this, textView);
                                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                                        Log.wtf("# of Sugg", Integer.toString(suggst.get(positionJ).size()));
                                        for (int k = 3; k < ((ArrayList) suggst.get(positionJ)).size(); k++) {
                                            Log.wtf("Suggestion", suggst.get(positionJ).get(k));
                                            popup.getMenu().add(k - 2, R.id.slot1, k - 2, suggst.get(positionJ).get(k));
                                        }
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            public boolean onMenuItemClick(MenuItem item) {
                                                Log.wtf("Click", "clicked");
                                                //Toast.makeText(CheckGrammar.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                                Log.wtf("Marked", markedWord);
                                                for (int l = 0; l < suggst.size(); l++) {
                                                    Log.wtf("Click", suggst.get(l).get(2));
                                                    if (suggst.get(l).get(2).equals(markedWord))
                                                        suggst.remove(l);
                                                }
                                                String replacement = sentence.get(positionI).replace(markedWord, item.getTitle().toString());
                                                SetSuggestion(suggst, paragraph.replace(sentence.get(positionI), replacement));
                                                return true;
                                            }
                                        });
                                        popup.show();
                                    }
                                };
                                ss.setSpan(clickableSpan,
                                        paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(2)), //START
                                        paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(2)) + suggst.get(j).get(2).length(), //END
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        errors[4]++;
                    }
                    else if (Integer.parseInt(suggst.get(j).get(1)) == 3) { // VERB TENSE
                        /*
                        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink)),
                                paragraph.indexOf(sentence.get(i)), //START
                                paragraph.indexOf(sentence.get(i)) + sentence.get(i).length(), //END
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                */
                        for (int k = 2; k < suggst.get(j).size(); k++) {
                            Log.wtf("SENTENCE", sentence.get(i));
                            Log.wtf("PARAGRAPH", paragraph);
                            Log.wtf("START", Integer.toString(paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k))));
                            Log.wtf("END", Integer.toString(paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k)) + suggst.get(j).get(k).length()));

                            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink)),
                                    paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k)), //START
                                    paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k)) + suggst.get(j).get(k).length(), //END
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        errors[2]++;
                    }
                    else if (Integer.parseInt(suggst.get(j).get(1)) == 4) { // PLURAL AND POSSESIVE
                        /*Log.wtf("HIGHLIGHT", suggst.get(j).get(2));
                        Log.wtf("SENTENCE", sentence.get(i));
                        Log.wtf("PARAGRAPH", paragraph);
                        Log.wtf("START", Integer.toString(paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(2))));
                        Log.wtf("END", Integer.toString(paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(2)) + suggst.get(j).get(2).length()));
                        */
                        /*
                        for (int k = 2; k < suggst.get(j).size(); k++) {
                            Log.wtf("SENTENCE", sentence.get(i));
                            Log.wtf("PARAGRAPH", paragraph);
                            Log.wtf("START", Integer.toString(paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k))));
                            Log.wtf("END", Integer.toString(paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k)) + suggst.get(j).get(k).length()));

                            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.violet)),
                                    paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k)), //START
                                    paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k)) + suggst.get(j).get(k).length(), //END
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }*/
                        final int positionJ = j;

                        //FIND WORD
                        for (int k = 2; k < suggst.get(j).size(); k++) {
                            Log.wtf("SENTENCE pp", sentence.get(i));
                            Log.wtf("WORD pp", suggst.get(j).get(k));
                            if (Character.isDigit((suggst.get(j).get(k)).charAt(0))) {
                                Log.wtf("SentenceNum", Character.toString(suggst.get(j).get(k).charAt(0)));
                                if (sentence.get(i).contains(suggst.get(j).get(k+1))) {
                                    final String markedWord = suggst.get(j).get(k+1);
                                    final int holdK = k+1;
                                    Log.wtf("Marked word", markedWord);
                                    ClickableSpan clickableSpan = new ClickableSpan() {
                                        @Override
                                        public void onClick(View textView) {
                                            PopupMenu popup = new PopupMenu(CheckGrammar.this, textView);
                                            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                                            Log.wtf("# of Sugg", Integer.toString(suggst.get(positionJ).size()));
                                            for (int l = holdK+1; l < ((ArrayList) suggst.get(positionJ)).size(); l++) {
                                                if (Character.isDigit(suggst.get(positionJ).get(l).charAt(0)))
                                                    break;
                                                Log.wtf("Suggestion", suggst.get(positionJ).get(l));
                                                popup.getMenu().add(l - 1, R.id.slot1, l - 1, suggst.get(positionJ).get(l));
                                            }
                                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                                public boolean onMenuItemClick(MenuItem item) {
                                                    //Toast.makeText(CheckGrammar.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                                    for (int l = 0; l < suggst.size(); l++) {
                                                        if (suggst.get(l).get(holdK).equals(markedWord))
                                                            suggst.remove(l);
                                                    }
                                                    String replacement = sentence.get(positionI).replace(markedWord, item.getTitle().toString());
                                                    SetSuggestion(suggst, paragraph.replace(sentence.get(positionI), replacement));
                                                    return true;
                                                }
                                            });
                                            popup.show();
                                        }
                                    };
                                    ss.setSpan(clickableSpan,
                                            paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k+1)), //START
                                            paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(k+1)) + suggst.get(j).get(k+1).length(), //END
                                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }
                        errors[3]++;
                    }
                    else if (Integer.parseInt(suggst.get(j).get(1)) == 5) { // S/V AGREEMENT
                        final int positionJ = j;
                        //FIND WORD
                        for (int k = 0; k < suggst.get(j).size(); k++) {
                            Log.wtf("SENTENCE", sentence.get(i));
                            Log.wtf("WORD", suggst.get(j).get(2));
                            if (sentence.get(i).contains(suggst.get(j).get(2))) {
                                final String markedWord = suggst.get(j).get(2);
                                Log.wtf("Marked word", markedWord);
                                ClickableSpan clickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View textView) {
                                        PopupMenu popup = new PopupMenu(CheckGrammar.this, textView);
                                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                                        Log.wtf("# of Sugg", Integer.toString(suggst.get(positionJ).size()));
                                        for (int k = 3; k < ((ArrayList) suggst.get(positionJ)).size(); k++) {
                                            Log.wtf("Suggestion", suggst.get(positionJ).get(k));
                                            popup.getMenu().add(k - 2, R.id.slot1, k - 2, suggst.get(positionJ).get(k));
                                        }
                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            public boolean onMenuItemClick(MenuItem item) {
                                                //Toast.makeText(CheckGrammar.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                                for (int l = 0; l < suggst.size(); l++) {
                                                    if (suggst.get(l).get(2).equals(markedWord))
                                                        suggst.remove(l);
                                                }
                                                String replacement = sentence.get(positionI).replace(markedWord, item.getTitle().toString());
                                                SetSuggestion(suggst, paragraph.replace(sentence.get(positionI), replacement));
                                                return true;
                                            }
                                        });
                                        popup.show();
                                    }
                                };
                                ss.setSpan(clickableSpan,
                                        paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(2)), //START
                                        paragraph.indexOf(sentence.get(i)) + sentence.get(i).indexOf(suggst.get(j).get(2)) + suggst.get(j).get(2).length(), //END
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        errors[5]++;
                    }
                }
            }
        }
        textView2.setText(ss);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());
        if (errors[4] > 0)
            textView2.setLinkTextColor(Color.GREEN);
        if (errors[5] > 0)
            textView2.setLinkTextColor(Color.BLUE);
        if (errors[3] > 0)
            textView2.setLinkTextColor(getResources().getColor(R.color.violet));
        textView2.setHighlightColor(Color.TRANSPARENT);

        String appendMark = "";
        if (errors[0] > 0)
            appendMark += ("\n⚫ Run-on");
        if (errors[1] > 0)
            appendMark += ("\n⚫ Fragment");
        if (errors[2] > 0)
            appendMark += ("\n⚫ Verb Inconsistency");
        if (errors[3] > 0)
            appendMark += ("\n⚫ Plural and Possesive");
        if (errors[4] > 0)
            appendMark += ("\n⚫ Subject Verb Agreement");
        if (errors[5] > 0)
            appendMark += ("\n⚫ Pronoun Error");

        SpannableString markings = new SpannableString(appendMark);

        if (errors[0] > 0) {
            markings.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)),
                    appendMark.indexOf("⚫ Run-on"), //START
                    appendMark.indexOf("⚫ Run-on") + "⚫ Run-on".length(), //END
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (errors[1] > 0) {
            markings.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)),
                    appendMark.indexOf("⚫ Fragment"), //START
                    appendMark.indexOf("⚫ Fragment") + "⚫ Fragment".length(), //END
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (errors[2] > 0) {
            markings.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.pink)),
                    appendMark.indexOf("⚫ Verb Inconsistency"), //START
                    appendMark.indexOf("⚫ Verb Inconsistency") + "⚫ Verb Inconsistency".length(), //END
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (errors[3] > 0) {
            Log.wtf("START", Integer.toString(appendMark.indexOf("⚫ Plural and Possesive")));
            Log.wtf("END", Integer.toString(appendMark.indexOf("⚫ Plural and Possesive") + "⚫ Plural and Possesive".length()));

            markings.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.violet)),
                    appendMark.indexOf("⚫ Plural and Possesive"), //START
                    appendMark.indexOf("⚫ Plural and Possesive") + "⚫ Plural and Possesive".length(), //END
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (errors[4] > 0) {
            markings.setSpan(new ForegroundColorSpan(/*getResources().getColor(R.color.green)*/ Color.GREEN),
                    appendMark.indexOf("⚫ Subject Verb Agreement"), //START
                    appendMark.indexOf("⚫ Subject Verb Agreement") + "⚫ Subject Verb Agreement".length(), //END
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (errors[5] > 0) {
            markings.setSpan(new ForegroundColorSpan(/*getResources().getColor(R.color.green)*/ Color.BLUE),
                    appendMark.indexOf("⚫ Pronoun Error"), //START
                    appendMark.indexOf("⚫ Pronoun Error") + "⚫ Pronoun Error".length(), //END
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView2.append(markings);
    }

    /*public void SetSuggestion(final ArrayList<ArrayList<String>> suggst, final String sntnce) {
        final TextView textView2 = (TextView) findViewById(R.id.textView2);

        SpannableString ss = new SpannableString(sntnce);
        for (int i = 0; i < suggst.size(); i++) {
            if (sntnce.contains(suggst.get(i).get(0))) {
                final String markedWord = suggst.get(i).get(0);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        PopupMenu popup = new PopupMenu(CheckGrammar.this, textView);
                        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                        for (int j = 0; j < suggst.size(); j++) {
                            if (markedWord.equals(((ArrayList) suggst.get(j)).get(0))) {
                                for (int k = 1; k < ((ArrayList) suggst.get(j)).size(); k++)
                                    popup.getMenu().add(k, R.id.slot1, k, (String) ((ArrayList) suggst.get(j)).get(k));
                            }
                        }
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                //Toast.makeText(CheckGrammar.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                //Toast.makeText(CheckGrammar.this, sntnce.toString(), Toast.LENGTH_LONG).show();

                                //REMOVE SUGGESTION FROM LIST
                                for (int x = 0; x < suggst.size(); x++) {
                                    if (suggst.get(x).get(0) == markedWord) {
                                        suggst.get(x).get(0);
                                        suggst.remove(x);
                                    }
                                }

                                textView2.setText(sntnce.replace(markedWord, item.getTitle()));
                                Spannable wordtoSpan = new SpannableString(textView2.getText());
                                sentence = sntnce.replace(markedWord, item.getTitle());
                                ArrayList<Integer> mark = new ArrayList<Integer>();
                                mark.add(textView2.getText().toString().indexOf(item.getTitle().toString()));
                                mark.add(textView2.getText().toString().indexOf(item.getTitle().toString()) + item.getTitle().toString().length());
                                coloredText.add(mark);
                                SetSuggestion(suggst, textView2.getText().toString());
                                //final SpellCheck spc = new SpellCheck();
                                //spc.ReplaceWord(sentence, item.getTitle().toString());
                                return true;
                            }
                        });
                        popup.show();
                    }
                };
                ss.setSpan(clickableSpan,
                        sntnce.indexOf(suggst.get(i).get(0)),
                        sntnce.indexOf(suggst.get(i).get(0)) + suggst.get(i).get(0).length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (!coloredText.isEmpty()) {
            //TextView textView4 = (TextView)findViewById(R.id.textView4);
            //textView4.setText(Integer.toString(coloredText.size()));
            for (int x = 0; x < coloredText.size(); x++) {
                ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.cyan)),
                        coloredText.get(x).get(0),
                        coloredText.get(x).get(1),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }
        textView2.setText(ss);
        textView2.setMovementMethod(LinkMovementMethod.getInstance());
        textView2.setLinkTextColor(Color.GREEN);
        textView2.setHighlightColor(Color.TRANSPARENT);

    } */

    public void CorrectWords(String sentence) {

        final TextView textView3 = (TextView) findViewById(R.id.textView3);
        sc = new SpellCheck();
        final ArrayList<ArrayList<String>> corrections = SpellCheck.getCorrections();
        SpannableString ss = new SpannableString(SpellCheck.getString());
        if (sentence.contains("[")) {
            String[] words = sentence.split(" ");
            for (final String word : words) {
                if (word.startsWith("[")) {
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            //use word here to make a decision
                            final int end = (word.contains(".")) || word.contains("?") || word.contains("!") ?
                                    word.length() - 2 :
                                    word.length() - 1;
                            final String markedWord = word.substring(1, end);

                            //Creating the instance of PopupMenu
                            PopupMenu popup = new PopupMenu(CheckGrammar.this, textView);
                            //Inflating the Popup using xml file
                            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                            popup.getMenu().add(1, R.id.slot1, 1, (markedWord.substring(0, 1).toUpperCase() + markedWord.substring(1)));
                            for (int i = 0; i < corrections.size(); i++) {
                                if (markedWord.equals(((ArrayList) corrections.get(i)).get(0))) {
                                    for (int j = 1; j < ((ArrayList) corrections.get(i)).size(); j++)
                                        popup.getMenu().add(j + 1, R.id.slot1, j + 1, (String) ((ArrayList) corrections.get(i)).get(j));
                                }
                            }

                            //registering popup with OnMenuItemClickListener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                public boolean onMenuItemClick(MenuItem item) {
                                    //Toast.makeText(MainActivity.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                                    final SpellCheck spc = new SpellCheck();
                                    SpellCheck.ReplaceWord(markedWord, item.getTitle().toString());
                                    CorrectWords(SpellCheck.getString());
                                    return true;

                                }
                            });
                            //showing popup menu
                            popup.show();
                        }
                    };
                    ss.setSpan(clickableSpan, sentence.indexOf(word), sentence.indexOf(word) + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        textView3.setText(ss);
        textView3.setMovementMethod(LinkMovementMethod.getInstance());
        textView3.setLinkTextColor(Color.RED);
        textView3.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        // disables the back button
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.button_done, menu);
        mm = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.done:
                //getSupportActionBar().setDisplayShowCustomEnabled(false);
                if (!SpellCheck.getString().contains("[")) {
                    mm.getItem(0).setEnabled(false);
                    mm.getItem(0).setVisible(false);
                    new BackgroundTagger().execute();
                } else
                    Toast.makeText(this, "PLEASE CORRECT ALL WORDS", Toast.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Checking Grammar...");
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    //Background Task
    private class BackgroundTagger extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            SpellCheck spc = new SpellCheck();
            try {
                pst = new POSTagger(SpellCheck.getString());
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            dismissDialog(progress_bar_type);
            TextView textView2 = (TextView) findViewById(R.id.textView2);
            SpellCheck spellCheck = new SpellCheck();
            GrammarRules gr = new GrammarRules(pst.GetTags(), pst.getChunk());
            SetSuggestion(gr.GetSuggestion(), gr.GetSentence());
        }
    }
}


