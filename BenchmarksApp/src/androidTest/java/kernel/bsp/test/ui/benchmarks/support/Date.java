package kernel.bsp.test.ui.benchmarks.support;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Settings;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 23.05.17.
 */

public class Date extends Settings {
    public Date() {
        super("com.android.settings/android.settings.DATE_SETTINGS", "date|time|settings");
    }

    @Test
    public void setTimeFormat() throws Exception {
        boolean use24hour = Extras.getBoolean("use24", false);

        UiTools.swipeDown();
        UiTools.swipeDown();

        UiObject2 date = mDevice.findObject(By.text(Pattern.compile(".*?24.*?hour.*", Pattern.CASE_INSENSITIVE)));
        if (date != null){
            UiObject2 swt = date.getParent().getParent().findObject(By.clazz("android.widget.Switch"));
            if (swt != null){
                if (swt.isChecked() ^ use24hour){
                    swt.click();
                }
                printResults("Time set to " + (swt.isChecked() ? "24" : "12") + "-hour format");
                return;
            }
        }
        printResults("NOT FOUND");
    }
}
