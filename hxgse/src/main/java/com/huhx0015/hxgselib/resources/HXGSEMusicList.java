package com.huhx0015.hxgselib.resources;

import android.util.Log;
import com.huhx0015.hxgselib.R;
import com.huhx0015.hxgselib.model.HXGSESong;
import java.util.LinkedList;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEMusicList] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: This class is responsible for providing methods to return the list of songs
 *  available for playback in this application.
 *  -----------------------------------------------------------------------------------------------
 */

public class HXGSEMusicList {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private static final String TAG = HXGSEMusicList.class.getSimpleName(); // Used for logging output to logcat.

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

    // hxgseMusicList(): Creates and returns an LinkedList of HXGSESong objects.
    public static LinkedList<HXGSESong> hxgseMusicList() {

        LinkedList<HXGSESong> musicList = new LinkedList<>(); // Creates a new LinkedList<HXGSESong> object.

        // Adds the HXGSESong objects to the LinkedList.
        // TODO: Define your song name and resources here.
        musicList.add(new HXGSESong("SONG 1", R.raw.song_1_gamerstep_bass_triplets));
        musicList.add(new HXGSESong("SONG 2", R.raw.song_2_ts_drums));
        musicList.add(new HXGSESong("SONG 3", R.raw.song_3_ts_digi_lead_2));

        Log.d(TAG, "INITIALIZATION: List of songs has been constructed successfully.");

        return musicList;
    }
}
