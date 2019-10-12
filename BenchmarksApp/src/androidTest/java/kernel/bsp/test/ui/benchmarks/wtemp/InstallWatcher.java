package kernel.bsp.test.ui.benchmarks.wtemp;

import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.Output;
import kernel.bsp.test.ui.benchmarks.utils.Shell;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
public class InstallWatcher extends Output {

    private final Shell shell = new Shell();
    private final String WATCHERNAME = "InstallAlertWatcher";

    private final UiWatcher alertWatcher = new UiWatcher() {
        private final UiObject tit = mDevice.findObject(new UiSelector().className("android.widget.TextView").resourceId("android:id/alertTitle"));
        private final UiObject msg = mDevice.findObject(new UiSelector().className("android.widget.TextView").resourceId("android:id/message"));
        private final UiObject btn = mDevice.findObject(new UiSelector().className("android.widget.Button").textMatches("Accept|accept|ACCEPT|ok|Ok|OK"));

        @Override
        public boolean checkForCondition() {
            try {
                print("CHECK");
                if (btn.exists()) {
                    String message = "" + (tit.exists() ? tit.getText() + ": " : "") + (msg.exists() ? msg.getText() : "");
                    if (!message.trim().equals("")) {
                        print(String.format("AlertWatcher: %s", message));
                    }
                    btn.click();
                }
            } catch (UiObjectNotFoundException e) {
                Log.e(LOG_TAG, "AcceptAlertError: " + e.toString());
            }
            return false;
        }
    };

    @Test
    public void install() throws Exception{
        boolean replace = Extras.getBoolean("replace");
        boolean downgrade = Extras.getBoolean("downgrade");
        // alert watcher register
        mDevice.registerWatcher(WATCHERNAME, alertWatcher);
        mDevice.runWatchers();
        String apk = Extras.getString("apk");
        try{
            String out = shell.pureExecute("pm install " + (replace ? " -r " : "") + (downgrade ? " -d ": "") + "/data/local/tmp/" + apk);
            print(out);
            this.printResults(out);
        } catch (Exception e) {
            throw new Exception(e);
        } finally{
            mDevice.removeWatcher(WATCHERNAME);
        }
    }
}


/*
public class Installer extends UiAutomatorTestCase{




}
 */
