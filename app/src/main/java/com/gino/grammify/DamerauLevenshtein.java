package com.gino.grammify;

/**
 * Created by Gino on 2/25/2016.
 */
public class DamerauLevenshtein {
    private String compOne;
    private String compTwo;
    private int[][] matrix;
    private Boolean calculated = false;

    public DamerauLevenshtein(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        if ((a.length() > 0 || !a.isEmpty()) || (b.length() > 0 || !b.isEmpty())) {
            compOne = a;
            compTwo = b;
        }
    }

    public int getSimilarity() {
        if (!calculated) setupMatrix();

        return matrix[compOne.length()][compTwo.length()];
    }

    private void setupMatrix() {
        int cost = -1;
        int del, sub, ins;

        matrix = new int[compOne.length()+1][compTwo.length()+1];

        for (int i = 0; i <= compOne.length(); i++) {
            matrix[i][0] = i;
        }

        for (int i = 0; i <= compTwo.length(); i++) {
            matrix[0][i] = i;
        }

        for (int i = 1; i <= compOne.length(); i++) {
            for (int j = 1; j <= compTwo.length(); j++) {
                if (compOne.charAt(i-1) == compTwo.charAt(j-1))
                    cost = 0;
                else
                    cost = 1;
                del = matrix[i-1][j]+1;
                ins = matrix[i][j-1]+1;
                sub = matrix[i-1][j-1]+cost;

                matrix[i][j] = minimum(del,ins,sub);

                if ((i > 1) && (j > 1) && (compOne.charAt(i-1) == compTwo.charAt(j-2)) && (compOne.charAt(i-2) == compTwo.charAt(j-1))) {
                    matrix[i][j] = minimum(matrix[i][j], matrix[i-2][j-2]+cost);
                }
            }
        }
        calculated = true;
    }

    private int minimum(int d, int i, int s) {
        int m = Integer.MAX_VALUE;

        if (d < m) m = d;
        if (i < m) m = i;
        if (s < m) m = s;

        return m;
    }

    private int minimum(int d, int t) {
        int m = Integer.MAX_VALUE;

        if (d < m) m = d;
        if (t < m) m = t;

        return m;
    }
}
