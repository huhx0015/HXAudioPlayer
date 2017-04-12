package com.huhx0015.hxgselib;

import android.content.Context;

/**
 * Created by Michael Yoon Huh on 4/12/2017.
 */

public class HXGSE {

    private static HXGSE hxgse;
    private Context context;

    public static HXGSE with(Context context) {

        context = context.getApplicationContext();
        if (hxgse == null) {
            hxgse = new HXGSE();
        }

        return hxgse;
    }
}
