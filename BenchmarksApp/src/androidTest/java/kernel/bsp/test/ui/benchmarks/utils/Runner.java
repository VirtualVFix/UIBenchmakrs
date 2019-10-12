package kernel.bsp.test.ui.benchmarks.utils;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.UiWatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Runner extends Output{
    protected String ACTIVITY;
    protected String BASIC_PACKAGE;
    private Pattern SEARCH_REGEX;
    private String errorMsg = null; // keep error found in alert watcher
    protected static final String WATCHER_NAME = "ALERT_WATCHER";
    private int openAppAgainCounter = 0; // count how many times AlertWatcher click to "Open app again" button if exists
    private final int MAX_OPEN_AGAIN_TRIES = 2; // limit tries of click to "Open app again" button. Not raise error if 0 < openAppAgainCounter < MAX_OPEN_AGAIN_TRIES

    // by regex function looking for error message in alerts
    public Runner(String activity, String regex) {
        ACTIVITY = activity;
        BASIC_PACKAGE = activity.split("/")[0];
        SEARCH_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    private final UiWatcher alertWatcher = new UiWatcher() {

        @Override
        public boolean checkForCondition() {
            UiObject2 button = mDevice.findObject(By.text(Pattern.compile("ok|got it|close app|open app again",
                    Pattern.CASE_INSENSITIVE)));
            if (button != null) {
                try {
                    UiObject2 title = mDevice.findObject(By.res(Pattern.compile("android:id/alertTitle",
                            Pattern.CASE_INSENSITIVE)));
                    UiObject2 msg = mDevice.findObject(By.res(Pattern.compile("android:id/message",
                            Pattern.CASE_INSENSITIVE)));
                    String message = "" + (title != null ? title.getText() + ": " : "")
                                        + (msg != null ? msg.getText() : "");
                    if (!message.trim().equals("") && !message.toLowerCase().contains("airplane mode")) {
                        print(String.format("%s %s", WATCHER_NAME, message));
                        Matcher match = SEARCH_REGEX.matcher(message);
                        if (match.find() && errorMsg == null) {
                            errorMsg = message;
                        }
                    }
                    if (button.getText().toLowerCase().contains("open app again")) {
                        openAppAgainCounter++;
                    }
                    if (title != null || msg != null) { // click if it alert
                        button.click();
                    }
                }catch (StaleObjectException er) {
//                Log.d(LOG_TAG, er.toString());
                }
            }
            return false;
        }
    };

    public void setRegex(String regex) {
        SEARCH_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    protected void stopWatcher() {
        mDevice.removeWatcher(Runner.WATCHER_NAME);
    }

    protected void startWatcher() {
        this.errorMsg = null;
        this.openAppAgainCounter = 0;
        mDevice.registerWatcher(Runner.WATCHER_NAME, this.alertWatcher);
        mDevice.runWatchers();
    }

    protected void unlockDevice() throws Exception{
        /* wake up and Unlock device if required */

        // wake up
        mDevice.wakeUp();

        UiObject unlock = mDevice.findObject(new UiSelector().resourceId("com.android.systemui:id/lock_icon"));

        // wait screen on
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < 5000)
            if (mDevice.isScreenOn() || unlock.exists())
                break;
        // swipe unlock if need it
        if (unlock.exists()) {
            Rect bounds = unlock.getBounds();
            mDevice.swipe(bounds.centerX(), bounds.centerY(), bounds.centerX(), 100, 20);
        }
    }

    public void start() throws Exception {
        // alert watcher register
        this.startWatcher();
        // unlock device
        this.unlockDevice();
        errorMsg = null;
    }

    protected void stop() throws Exception {
        this.stopWatcher();
    }

    protected void checkAlert() throws Exception {
        this.checkAlert(true, null);
    }

    protected void checkAlert(String regexwindow) throws Exception {
        this.checkAlert(true, regexwindow);
    }

    protected void checkAlert(boolean window) throws Exception {
        this.checkAlert(window, null);
    }

    private void checkAlert(boolean window, String regex) throws Exception {
        /* Check popup alerts */
        try {
            if (!mDevice.isScreenOn()){
                this.unlockDevice();
            }
            alertWatcher.checkForCondition();
            if (errorMsg != null && (this.openAppAgainCounter == 0
                    || this.openAppAgainCounter >= this.MAX_OPEN_AGAIN_TRIES))
                throw new Exception(errorMsg);
            if (window) {
                String pack = mDevice.getCurrentPackageName();
                if (pack != null) {
                    if (regex != null) {
                        Pattern reg = Pattern.compile(BASIC_PACKAGE + "|" + regex, Pattern.CASE_INSENSITIVE);
                        if (!reg.matcher(pack).find())
                            throw new Exception("Current activity was changed !");
                    } else if (!pack.equals(BASIC_PACKAGE))
                        throw new Exception("Current activity was changed !");
                }
            }
        } catch (Exception ex) {
            this.stop();
            throw new Exception(ex);
        }
    }
}
