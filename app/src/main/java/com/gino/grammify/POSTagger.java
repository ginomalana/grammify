package com.gino.grammify;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

/**
 * Created by Gino on 1/19/2016.
 */
public class POSTagger {

    static POSModel model;
    static ChunkerModel model2;

    static StringBuilder posTag;
    static ArrayList<ArrayList<String>> chunk;

    public POSTagger() {
            new LoadModel().execute();
    }

    public POSTagger(String sentence) {
        posTag = new StringBuilder("");
        chunk = new ArrayList<ArrayList<String>>();
        // TODO code application logic here
        try{
            chunk(sentence);
        }
        catch(Exception e) {
            Log.wtf("EXCEPTION ERROR ", e.toString());
            posTag.append(e.toString());
        }
    }

    public void chunk(String paragraph) throws IOException {

        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        ChunkerME chunkerME = new ChunkerME(model2);
        ArrayList<ArrayList<String>>  splittedSentence = new ArrayList<ArrayList<String>>();

        //Sentenze Tokenizer
        Pattern delimiter = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)"
                        + "[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
                        Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher sentenceSplit = delimiter.matcher(paragraph);


        while (sentenceSplit.find()) {
            ArrayList<String> puncAndWord = new ArrayList<String>();
            //Sentence
            puncAndWord.add(sentenceSplit.group()
                    .replaceAll("[^a-zA-Z'\\s]", "")
                    .replaceAll("\\s+", " "));
            //Punctuation
            puncAndWord.add(Character.toString(paragraph.charAt(sentenceSplit.end() - 1)));
            splittedSentence.add(puncAndWord);
        }
        perfMon.start();
        for (int i = 0; i < splittedSentence.size(); i++) {
            ArrayList<String> chunkPerSentence = new ArrayList<String>();
            ObjectStream<String> lineStream = new PlainTextByLineStream(
                    new StringReader(splittedSentence.get(i).get(0)));

            String line;
            String whitespaceTokenizerLine[] = null;

            String[] tags = null;
            while ((line = lineStream.read()) != null) {
                whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE.tokenize(line);
                tags = tagger.tag(whitespaceTokenizerLine);

                POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
                posTag.append(sample.toString() + (splittedSentence.get(i).get(1)) + " ");
                perfMon.incrementCounter();
            }
            perfMon.stopAndPrintFinalResult();

            // chunker
            String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);

            for (String s : result)
                chunkPerSentence.add(s);

            Span[] span = chunkerME.chunkAsSpans(whitespaceTokenizerLine, tags);
            for (Span s : span)
                chunkPerSentence.add(s.toString());

            chunk.add(chunkPerSentence);
        }
    }

    public String GetTags(){
        return posTag.toString();
    }

    public ArrayList<ArrayList<String>> getChunk() {
        return chunk;
    }

    private class LoadModel extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String path = Environment.getExternalStorageDirectory()
                        + "/Android/data/com.thesis.grammify/resources/resources.bin";
                Model1 m1 = new Model1(path);

                String path2 = Environment.getExternalStorageDirectory()
                        + "/Android/data/com.thesis.grammify/resources/resources2.bin";
                Model2 m2 = new Model2((path2));

                m1.start();
                m2.start();

                m1.join();
                m2.join();

                model = m1.getValue();
                model2 = m2.getValue();

                Log.wtf("TAGGERS", "Taggers Loaded!");
            }
            catch (Exception e) {
                Log.wtf("EXCEPTION ERROR Tagger", e.toString());
            }
            return null;
        }
    }

}

class Model1 extends Thread {
    public Model1(String str) {
        super(str);
    }
    private volatile POSModel model;

    public void run() {
        Log.wtf("Loading Tagger", getName());
        try {
            InputStream modelIn = new BufferedInputStream(new FileInputStream(getName())/*, 131072*/);
            model = new POSModel(modelIn);
        } catch (Exception e) {
            Log.wtf("Exception: ", e.toString());
        }
    }
    public POSModel getValue() {
        return model;
    }
}

class Model2 extends Thread {
    public Model2(String str) {
        super(str);
    }
    private volatile ChunkerModel model2;

    public void run() {
        Log.wtf("Loading Tagger", getName());
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(getName())/*, 131072*/);
            model2 = new ChunkerModel(is);
        } catch (Exception e) {
            Log.wtf("Exception: ", e.toString());
        }

    }
    public ChunkerModel getValue() {
        return model2;
    }
}
