package kernel.bsp.test.ui.benchmarks.utils;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */
public class UiTools{
    private static UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    
    public static int width(){
        return mDevice.getDisplayWidth();
    }

    public static int height(){
        return mDevice.getDisplayHeight();
    }

    public static void longClick(int x, int y, int time){
        mDevice.swipe(x, y, x, y, time==0 ? 60: time);
    }

    public static void longClick(int x, int y){
        UiTools.longClick(x, y, 0);
    }

    public static void clickCenter(){
        mDevice.click(UiTools.width()/2, UiTools.height()/2);
    }

    public static void waitUntilGone(BySelector selector, int timeout) throws Exception{
        boolean exists = mDevice.wait(Until.gone(selector), timeout);
        if (!exists) throw new Exception("Wait object gone Error: Timeout expired.");
    }

    public static UiObject2 waitForExists(BySelector selector, int timeout) throws Exception{
        return UiTools.waitForExists(selector, timeout, false);
    }

    public static UiObject2 waitForExists(BySelector selector, int timeout, boolean minimize) throws Exception{
        /* Wait for UI element and raise exception if timeout expired.
        minimize == true - allow to minimize application and reopen it if timeout expired.
        It can help fix issue when UIAutomator cannot find element. It's increase timeout on 10000ms */
        UiObject2 obj;
        try {
            obj = mDevice.wait(Until.findObject(selector), timeout);
        }catch (NullPointerException e){
            obj = null;
        }
        if (obj == null){
            if (minimize) {
                mDevice.pressRecentApps();
                sleep(2000);
                mDevice.pressBack();
                Log.d(Runner.LOG_TAG, "Minimize application.");
                return UiTools.waitForExists(selector, 10000, false);
            }else {
                throw new Exception("Wait object exists Error: Timeout expired.");
            }
        }
        return obj;
    }

    public static void swipeLeft(){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2, mDevice.getDisplayWidth(),
                mDevice.getDisplayHeight()/2, 10);
    }

    public static void swipeLeft(int steps){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2, mDevice.getDisplayWidth(),
                mDevice.getDisplayHeight()/2, steps);
    }

    public static void swipeRight(){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2, 0,
                mDevice.getDisplayHeight()/2, 10);
    }

    public static void swipeRight(int steps){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2, 0,
                mDevice.getDisplayHeight()/2, steps);
    }

    public static void swipeDown(){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2,
                mDevice.getDisplayWidth()/2, 0, 10);
    }

    public static void swipeDown(int steps){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2,
                mDevice.getDisplayWidth()/2, 0, steps);
    }

    public static void swipeUp(){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2,
                mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight(), 10);
    }

    public static void swipeUp(int steps){
        mDevice.swipe(mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight()/2,
                mDevice.getDisplayWidth()/2, mDevice.getDisplayHeight(), steps);
    }

    public static void passAlert(boolean clickok, String title, String name) throws Exception{
        if (title != null && !title.equals(""))
            if (!mDevice.findObject(new UiSelector().resourceId("android:id/alertTitle")
                    .textMatches(title)).exists())
                return;
        UiObject button = mDevice.findObject(new UiSelector().className("android.widget.Button")
                .resourceId("android:id/button" + (clickok ? "1" : "2"))
                .textMatches((name == null || name.equals("")) ? ".*" : name));
        if (button.exists()) button.clickAndWaitForNewWindow(5000);
    }

    public static void allowPermissions(String [] permissions) throws Exception{
        UiObject alert = mDevice.findObject(new UiSelector().resourceId("android:id/alertTitle")); // alerts title
        UiObject overAlert = mDevice.findObject(new UiSelector().resourceId("android:id/alertTitle")
                .textContains("Screen overlay detected")); // overlay alerts title
        UiObject msg = mDevice.findObject(new UiSelector()
                .className("android.widget.TextView").resourceId("com.android.packageinstaller:id/permission_message"));
        for (int i=0; i<5; i++){ // find allow permissions popup dialogs
            if (msg.exists()){
                boolean added = false;
                for (String perm : permissions){
                    if (msg.getText().contains(perm.toLowerCase())){
                        mDevice.findObject(new UiSelector().className("android.widget.Button")
                                .resourceId("com.android.packageinstaller:id/permission_allow_button"))
                                .clickAndWaitForNewWindow(5000);
                        added = true;
                        break;
                    }
                }
                if (!added) mDevice.findObject(new UiSelector().className("android.widget.Button")
                        .resourceId("com.android.packageinstaller:id/permission_deny_button"))
                        .clickAndWaitForNewWindow(5000);
            }else if (overAlert.exists()){
                mDevice.pressBack();
            }else if (alert.exists()){ // check popup alerts during allow permissions
                Log.d(Runner.LOG_TAG, String.format("AlertWatcher: %s", alert.getText()));
                UiObject btn = mDevice.findObject(new UiSelector().resourceId("android:id/button1"));
                if (btn.exists()) btn.clickAndWaitForNewWindow(2000);
            }else break;
            sleep(500);
        }
    }
}
