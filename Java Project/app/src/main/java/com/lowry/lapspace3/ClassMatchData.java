package com.lowry.lapspace3;

import android.graphics.Bitmap;

/**
 * CLASS: OBJECT CONTAINING THE MATCH IMAGE AND THE RESULT OF  WHETHER IT WAS A MATCH OR NOT (VERIFY)
 */

public class ClassMatchData {
    private Bitmap matchImage;
    private String matchResultString;

    public ClassMatchData(Bitmap matchImage, String matchResultString) {
        this.matchImage = matchImage;
        this.matchResultString = matchResultString;
    }

    public String getMatchResultString() {
        return matchResultString;
    }
    public Bitmap getMatchImage() {
        return matchImage;
    }
}
