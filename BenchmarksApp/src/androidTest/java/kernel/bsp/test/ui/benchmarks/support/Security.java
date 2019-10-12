package kernel.bsp.test.ui.benchmarks.support;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Settings;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 23.05.17.
 */

public class Security extends Settings {
    public Security() {
        super("com.android.settings/android.settings.SECURITY_SETTINGS", "security|settings");
    }

    @Test
    public void setScreenLockToNone() throws Exception {
        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true));
        UiObject lock = mDevice.findObject(new UiSelector().textMatches("Screen lock"));
        scroll.scrollIntoView(lock);
        lock.clickAndWaitForNewWindow(DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        this.unlockDevice();

        // set first element
        UiTools.swipeUp();
        UiObject2 none = mDevice.findObject(By.text(Pattern.compile("none", Pattern.CASE_INSENSITIVE)));
        if (none != null){
            none.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
            printResults("Security set to None");
        }else {
            printResults("NOT FOUND");
        }
    }
}
