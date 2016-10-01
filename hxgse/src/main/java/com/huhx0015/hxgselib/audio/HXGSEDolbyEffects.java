package com.huhx0015.hxgselib.audio;

import android.content.Context;
import android.util.Log;
import com.dolby.dap.DolbyAudioProcessing;
import com.dolby.dap.OnDolbyAudioProcessingEventListener;

/** -----------------------------------------------------------------------------------------------
 *  [HXGSEDolbyEffects] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: HXGSEDolbyEffects class is used to add Dolby audio processing effects for devices
 *  that support Dolby Audio.
 *  -----------------------------------------------------------------------------------------------
 */
public class HXGSEDolbyEffects implements OnDolbyAudioProcessingEventListener {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // INSTANCE VARIABLE
    private static HXGSEDolbyEffects hxgse_dolby; // The HXGSEDolbyEffects instance.

    // DOLBY VARIABLES:
    private DolbyAudioProcessing dolbyAudioProcessor; // DolbyAudioProcessing object for utilizing Dolby audio sound processing functionality.

    // SYSTEM VARIABLES:
    private static final String TAG = HXGSEMusicEngine.class.getSimpleName(); // Used for logging output to logcat.

    /** INSTANCE FUNCTIONALITY _________________________________________________________________ **/

    // getInstance(): Returns the hxgse_dolby instance.
    public static HXGSEDolbyEffects getInstance() {
        if (hxgse_dolby == null) {
            hxgse_dolby = new HXGSEDolbyEffects();
        }
        return hxgse_dolby;
    }

    /** INITIALIZATION FUNCTIONALITY ___________________________________________________________ **/

    // initializeDolby(): Initializes the HXGSEDolbyEffects class variables.
    public void initializeDolby(Context context) {

        Log.d(TAG, "INITIALIZING: Initializing Dolby audio processing effects.");

        dolbyAudioProcessor = DolbyAudioProcessing.getDolbyAudioProcessing(context, DolbyAudioProcessing.PROFILE.GAME, this);

        // If initialization of the Dolby Audio Processor has failed, an error logcat message is outputted.
        if (dolbyAudioProcessor == null){
            Log.e(TAG, "ERROR: Dolby audio processing is not available for this device.");
        }

        else {
            Log.d(TAG, "INITIALIZING: Dolby audio processing initialization complete.");
        }
    }

    /** DOLBY EFFECT FUNCTIONALITY _____________________________________________________________ **/

    // releaseDolbyEffects(): Releases all resources utilized by the dolby audio processing object.
    public void releaseDolbyEffects() {

        if (dolbyAudioProcessor != null) {
            dolbyAudioProcessor.release(); // Releases the DolbyAudioProcessing object.
            dolbyAudioProcessor = null;

            Log.d(TAG, "RELEASE: Dolby audio processing released.");
        }
    }

    /** DOLBY AUDIO PROCESSING FUNCTIONALITY ___________________________________________________ **/

    // onDolbyAudioProcessingClientConnected(): Invoked when the Dolby audio processing object is
    // connected.
    @Override
    public void onDolbyAudioProcessingClientConnected() {

        Log.d(TAG, "DOLBY: Dolby audio processing client connected.");

        dolbyAudioProcessor.setEnabled(true); // Enables the DolbyAudioProcessing object.
    }

    // onDolbyAudioProcessingClientDisconnected(): Invoked when the Dolby audio processing object is
    // disconnected.
    @Override
    public void onDolbyAudioProcessingClientDisconnected() {

        Log.d(TAG, "DOLBY: Dolby audio processing client disconnected.");

        dolbyAudioProcessor.setEnabled(false); // Disables the DolbyAudioProcessing object.
    }

    // onDolbyAudioProcessingEnabled(): Invoked when DolbyAudioProcessing is enabled.
    @Override
    public void onDolbyAudioProcessingEnabled(boolean b) {
        Log.d(TAG, "DOLBY: Dolby audio processing enabled.");
    }

    // onDolbyAudioProcessingProfileSelected(): Invoked when a DolbyAudioProcessing profile is set.
    @Override
    public void onDolbyAudioProcessingProfileSelected(DolbyAudioProcessing.PROFILE profile) {
        Log.d(TAG, "DOLBY: Dolby audio processing profile selected.");
    }
}