package kernel.bsp.test.ui.benchmarks.support;

import android.os.Build;
import android.support.test.uiautomator.*;

import org.junit.Test;

import kernel.bsp.test.ui.benchmarks.base.Settings;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 23.05.17.
 */

public class Debug extends Settings {
    private Settings deviceinfo;

    public Debug() {
        super();
        deviceinfo = new Settings("com.android.settings/android.settings.DEVICE_INFO_SETTINGS", "phone|info|settings");
    }

    // open debug on Android 8 and later
    private void openDebug26() throws Exception{
        UiObject2 sys = mDevice.findObject(By.clazz("android.widget.TextView")
                                             .text(Pattern.compile("system", Pattern.CASE_INSENSITIVE)));
        sys.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
    }

    private void openDebug() throws Exception{
        for (int i = 0; i < 7; i++) {
            UiTools.swipeDown();
        }
        // debug on android 26
        if (Build.VERSION.SDK_INT >= 26) {
            this.openDebug26();
        }

        UiObject dev = mDevice.findObject(new UiSelector().textMatches("Developer options"));

        if (!dev.exists()) {
            UiScrollable scroll;
            deviceinfo.tearUp(); // open device info
            scroll = new UiScrollable(new UiSelector().scrollable(true));
            UiObject build = mDevice.findObject(new UiSelector().textMatches("Build number"));
            scroll.scrollIntoView(build);
            for (int i = 0; i < 7; i++) {
                build.click();
            }
            mDevice.pressBack();
            sleep(DEFAULT_WAIT_ELEMENT_TIMEOUT);
            scroll = new UiScrollable(new UiSelector().scrollable(true));
            scroll.scrollIntoView(dev);
        }
        dev.clickAndWaitForNewWindow(DEFAULT_WAIT_ELEMENT_TIMEOUT);
    }

    @Test
    public void setStayAwake() throws Exception {
        boolean enable = Extras.getBoolean("enable", true);
        openDebug();

        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true));
        UiObject awake = mDevice.findObject(new UiSelector().textMatches("Stay awake"));
        scroll.scrollIntoView(awake);

        // set first element
        UiObject2 item = mDevice.findObject(By.text("Stay awake"));
        if (item != null){
            UiObject2 swt = item.getParent().getParent().findObject(By.clazz("android.widget.Switch"));
            if (swt != null){
                if (swt.isChecked()^enable){
                    swt.click();
                }
                printResults("Stay awake " + (swt.isChecked() ? "enabled" : "disabled"));
                return;
            }
        }
        printResults("NOT FOUND");
    }
}
