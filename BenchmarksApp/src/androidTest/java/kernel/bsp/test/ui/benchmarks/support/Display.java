package kernel.bsp.test.ui.benchmarks.support;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Settings;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Display extends Settings {
    public Display() {
        super("com.android.settings/android.settings.DISPLAY_SETTINGS", "display|settings");
    }

    @Test
    public void setMaxTimeout() throws Exception {
        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true));
        UiObject sleep = mDevice.findObject(new UiSelector().textMatches("Sleep"));
        scroll.scrollIntoView(sleep);
        sleep.clickAndWaitForNewWindow(DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        this.unlockDevice();

        // set last element
        UiTools.swipeDown();
        UiObject2 times = mDevice.findObject(By.clazz("android.widget.ListView").res("android:id/select_dialog_listview"));
        List<UiObject2> list = times.getChildren();
        UiObject2 last = list.get(list.size()-1);
        printResults(String.format("%s", last.getChildren().get(0).getText()));
        last.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
    }

    @Test
    public void setSpecifiedTimeout() throws Exception {
        // set display timeout to selected time in "timeout" option
        String timeout = Extras.getString("timeout");
        String unit = Extras.getString("unit");
        int swipes = 3; // max swipes in display timeouts menu
        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true));
        UiObject sleep = mDevice.findObject(new UiSelector().textMatches("Sleep"));
        scroll.scrollIntoView(sleep);
        sleep.clickAndWaitForNewWindow(DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        this.unlockDevice();

        // find specified timeout
        UiTools.swipeUp();
        for (int i=0; i<swipes; i++) {
            UiObject2 selected = mDevice.findObject(By.text(Pattern.compile("\\s*"+timeout+"\\s*"+unit+".*?", Pattern.CASE_INSENSITIVE)));
            if (selected != null){
                printResults(selected.getText());
                selected.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                return;
            }
            UiTools.swipeDown(50);
        }
        printResults("NOT FOUND");
    }
}
