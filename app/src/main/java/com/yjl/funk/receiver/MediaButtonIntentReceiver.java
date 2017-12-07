

package com.yjl.funk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;
import com.yjl.funk.player.Constants;
import com.yjl.funk.service.FunkMusicService;

/**
 * 耳机线控
 */
public class MediaButtonIntentReceiver extends BroadcastReceiver {

    private static void sendBroadCast(Context context, String command) {
        final Intent i = new Intent(context, FunkMusicService.class);
        i.setAction(Constants.SERVICECMD);
        i.putExtra(Constants.CMDNAME, command);
        context.startService(i);
    }
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final String intentAction = intent.getAction();
        //拔耳机
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intentAction)) {
            sendBroadCast(context, Constants.CMDPAUSE);
        } else if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }
            final int keycode = event.getKeyCode();

            String command = null;
            switch (keycode) {
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    command = Constants.CMDSTOP;
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    command = Constants.CMDNEXT;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    command = Constants.CMDPREVIOUS;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    command = Constants.CMDPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    command = Constants.CMDTOGGLEPAUSE;
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    command = Constants.CMDPLAY;
                    break;
            }
            abortBroadcast();
            sendBroadCast(context, command);

        }
    }
}

