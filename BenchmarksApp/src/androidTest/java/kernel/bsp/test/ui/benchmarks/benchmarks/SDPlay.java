package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class SDPlay extends Benchmark{

    public SDPlay(){
        super("com.elena.sdplay/com.elena.sdplay.MainActivity", "sdplay|sd play|files sizes");
        this.checkAlertWindow = "android";
    }

    @Override
    public void tearUp() throws Exception {
        String storage = Extras.getString("storage");
        this.closeApp();
        this.start();
        shell.execute("am start -a android.intent.action.VIEW -c android.intent.category.DEFAULT -e storage "
                + storage + " -n com.elena.sdplay/com.elena.sdplay.MainActivity");
    }

    @Test
    public void bench() throws Exception {
        String storage = Extras.getString("storage");
        String test = Extras.getString("test", "full");
        String filesize = Extras.getString("bigfilesize", "512MB");
        int threads = Extras.getInt("threads", 0);
        int finish = Extras.getInt("finish");

        UiTools.waitForExists(By.clazz("android.widget.Button").res(Pattern.compile("com.elena.sdplay:id/buttonStart")),
                DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT*2);
        print(String.format("Starting \"%s\" test for \"%s\" storage.", test, storage));
        shell.execute("am start -a android.intent.action.VIEW -c android.intent.category.DEFAULT -e test "
                        + test + (threads > 0 ? (" -e threads "+Integer.toString(threads)): "")
                        + " -n com.elena.sdplay/com.elena.sdplay.BenchStart");

        // check popup alerts
        sleep(2000);
        UiObject2 alert = mDevice.findObject(By.res(Pattern.compile(".*:id/alertTitle", Pattern.CASE_INSENSITIVE)));
        if (alert != null){
            // change big file size if not enough space
            UiObject2 msg = mDevice.findObject(By.res(Pattern.compile(".*:id/message", Pattern.CASE_INSENSITIVE)));
            if (msg != null && msg.getText().contains("no enough space")) {
                this.stopWatcher();
                print(String.format("%s %s", WATCHER_NAME, alert.getText() + ": " + msg.getText()));
                UiObject button = mDevice.findObject(new UiSelector().className("android.widget.Button").textMatches("OK|ok|Ok"));
                button.click();

                UiScrollable list = new UiScrollable(new UiSelector().className("android.widget.ListView").scrollable(true));
                UiObject fsize = mDevice.findObject(new UiSelector().className("android.widget.TextView").textContains("Large file size"));
                list.scrollIntoView(fsize);
                fsize.click();

                UiObject2 edit = mDevice.findObject(By.clazz("android.widget.EditText"));
                edit.clear();
                edit.setText(filesize);

                button.click();
                print(String.format("%s Large file size was changed to %s...", WATCHER_NAME, filesize));
                mDevice.pressBack();

                // check if not alert again
                sleep(2000);
                alert = mDevice.findObject(By.res(Pattern.compile(".*:id/alertTitle", Pattern.CASE_INSENSITIVE)));
                if (alert != null){
                    msg = mDevice.findObject(By.res(Pattern.compile(".*:id/message", Pattern.CASE_INSENSITIVE)));
                    throw new Exception("SDPlay launch error: " + alert.getText() + " " + msg.getText());
                }
                this.startWatcher();
            } else {
                throw new Exception("SDPlay launch error: " + alert.getText() + (msg != null ? (" " + msg.getText()) : ""));
            }
        }
        print("Wait for completion...");
        UiTools.waitForExists(By.clazz("android.widget.RelativeLayout").res(Pattern.compile("com.elena.sdplay:id/viewSummary")), finish*1000);
        print("SDPlay benchmark starting...");
    }
}