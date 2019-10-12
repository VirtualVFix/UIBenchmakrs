package kernel.bsp.test.ui.benchmarks.support;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;

import android.util.Log;
import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Settings;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Sound extends Settings {

    public Sound() {
        super("com.android.settings/android.settings.SOUND_SETTINGS", "sound|settings");
    }

    @Test
    public void muteMediaVolume() throws Exception {
        UiTools.swipeUp(5);
        UiObject2 media_text = mDevice.findObject(By.clazz("android.widget.TextView")
                .text(Pattern.compile("media\\s+volume", Pattern.CASE_INSENSITIVE)));

        if (media_text != null){
            UiObject2 parent = media_text.getParent().getParent();
            if (parent.getChildCount() == 2){
              try{
                    Rect bounds = parent.getChildren().get(1).getVisibleBounds();
                    mDevice.click(bounds.left + 10, bounds.top + bounds.height() / 2);
                    return;
                }catch (Exception e){
                    Log.w(LOG_TAG, e.toString());
                }
            }
        }
        throw new Exception("Media volume SeekBar is not found !");
    }
}