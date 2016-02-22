package com.gino.grammify;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Gino on 2/22/2016.
 */
public class Contraction {

    /**
     * @param args the command line arguments
     */

    private static ArrayList<ArrayList<String>> contractions;

    public Contraction(){
        contractions = new ArrayList<ArrayList<String>>();
        Contraction.initialize();
    }

    protected static void initialize() {
        //BE
        Contraction.addContractions("^(i|i')m$", "i am");
        Contraction.addContractions("^(you|you')re$", "you are");
        Contraction.addContractions("^(he|he')s$", "he is");
        Contraction.addContractions("^(she|she')s$", "she is");
        Contraction.addContractions("^(it|it|'ti)s$", "it is");
        Contraction.addContractions("^we're$", "we are");
        Contraction.addContractions("^(they|they')re$", "they are");
        Contraction.addContractions("^(that|that')s$", "that is");
        Contraction.addContractions("^(who|who')s$", "who is");
        Contraction.addContractions("^(what|what')s$", "what is");
        Contraction.addContractions("^(what|what')re$", "what are");
        Contraction.addContractions("^(where|where')s$", "where is");
        Contraction.addContractions("^(when|when')s$", "when is");
        Contraction.addContractions("^(why|why')s$", "why is");
        Contraction.addContractions("^(how|how')s$", "how is");
        //WILL
        Contraction.addContractions("^i'll$", "i will");
        Contraction.addContractions("^(you|you')ll$", "you will");
        Contraction.addContractions("^he'll$", "he will");
        Contraction.addContractions("^she'll$", "she will");
        Contraction.addContractions("^(it|it')ll$", "it will");
        Contraction.addContractions("^(we|we')ll$", "we will");
        Contraction.addContractions("^(they|they')ll$", "they will");
        Contraction.addContractions("^(that|that')ll$", "that will");
        Contraction.addContractions("^(who|who')ll$", "who will");
        Contraction.addContractions("^(what|what')ll$", "what will");
        Contraction.addContractions("^(where|where')ll$", "that will");
        Contraction.addContractions("^(when|when')ll$", "when will");
        Contraction.addContractions("^(why|why')ll$", "why will");
        Contraction.addContractions("^(how|how')ll$", "how will");
        //WOULD
        Contraction.addContractions("^i'd$", "i would");
        Contraction.addContractions("^(you|you')d$", "you would");
        Contraction.addContractions("^he'd$", "he would");
        Contraction.addContractions("^she'd$", "she would");
        Contraction.addContractions("^(it|it')d$", "it would");
        Contraction.addContractions("^(we|we')d$", "we would");
        Contraction.addContractions("^(they|they')d$", "they would");
        Contraction.addContractions("^(that|that')d$", "that would");
        Contraction.addContractions("^(who|who')d$", "who would");
        Contraction.addContractions("^(what|what')d$", "what would");
        Contraction.addContractions("^(where|where')d$", "that would");
        Contraction.addContractions("^(when|when')d$", "when would");
        Contraction.addContractions("^(why|why')d$", "why would");
        Contraction.addContractions("^(how|how')d$", "how would");
        //HAVE
        Contraction.addContractions("^(i|i')ve$", "i have");
        Contraction.addContractions("^(you|you')ve$", "you have");
        //Contraction.addContractions("^(he|he')s$", "he has");
        //Contraction.addContractions("^(she|she')s$", "she has");
        //Contraction.addContractions("^(it|it')s$", "it has");
        Contraction.addContractions("^(we|we')ve$", "we have");
        Contraction.addContractions("^(they|they')ve$", "they have");
        //Contraction.addContractions("^(that|that')s$", "that has");
        //Contraction.addContractions("^(who|who')s$", "who has");
        //Contraction.addContractions("^(what|what')s$", "what has");
        //Contraction.addContractions("^(where|where')s$", "that has");
        //Contraction.addContractions("^(when|when')s$", "when has");
        //Contraction.addContractions("^(why|why')s$", "why has");
        //Contraction.addContractions("^(how|how')s$", "how has");
        //NEGATING A VERB
        Contraction.addContractions("^(isn|isn')t$", "is not");
        Contraction.addContractions("^(aren|aren')t$", "are not");
        Contraction.addContractions("^(was|was')t$", "was not");
        Contraction.addContractions("^(have|have')t$", "have not");
        Contraction.addContractions("^(hasn|hasn')t$", "has not");
        Contraction.addContractions("^(hadn|hadn')t$", "had not");
        Contraction.addContractions("^(won|won')t$", "will not");
        Contraction.addContractions("^(wouldn|wouldn')t$", "would not");
        Contraction.addContractions("^(don|don')t$", "do not");
        Contraction.addContractions("^(doesn|doesn')t$", "does not");
        Contraction.addContractions("^(didn|didn')t$", "did not");
        Contraction.addContractions("^(can|can')t$", "cannot");
        Contraction.addContractions("^(couldn|couldn')t$", "could not");
        Contraction.addContractions("^(shouldn|shouldn')t$", "should not");
        Contraction.addContractions("^(mightn|mightn')t$", "might not");
        Contraction.addContractions("^(mustn|mustn')t$", "must not");
        //WOULDA-SHOULDA-COULDA
        Contraction.addContractions("^(would|would')ve$", "would have");
        Contraction.addContractions("^(should|should')ve$", "should have");
        Contraction.addContractions("^(could|could')ve$", "could have");
        Contraction.addContractions("^(might|might')ve$", "might have");
        Contraction.addContractions("^(must|must')ve$", "must have");
    }

    static void addContractions(String rule, String replacement) {
        final ArrayList<String> contractionRules = new ArrayList<String>();
        contractionRules.add(rule);
        contractionRules.add(replacement);
        contractions.add(contractionRules);
    }

    static String contractionize(String word) {
        boolean upperCase = Character.isUpperCase(word.charAt(0));
        for (ArrayList<String> findRule: contractions)
        {
            Pattern regex = Pattern.compile(findRule.get(0), Pattern.CASE_INSENSITIVE);
            Matcher regexMatcher = regex.matcher(word);
            if (regexMatcher.find())  {
                if (upperCase)
                    return Contraction.toSentenceCase(apply(word, findRule));
                else
                    return Contraction.apply(word, findRule);
            }
        }
        return word;
    }

    static String apply(String word, ArrayList<String> contractionRule) {
        Pattern rule = Pattern.compile(contractionRule.get(0) ,Pattern.CASE_INSENSITIVE);
        Matcher matcher = rule.matcher(word);
        return matcher.replaceAll(contractionRule.get(1));
    }

    static String toSentenceCase(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
