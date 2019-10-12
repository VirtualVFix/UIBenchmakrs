package kernel.bsp.test.ui.benchmarks.base;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.util.Log;

import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import kernel.bsp.test.ui.benchmarks.utils.Runner;
import kernel.bsp.test.ui.benchmarks.utils.Shell;

import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 10/7/2016.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // sort tests by name
public class Benchmark extends Runner {
    // base benchmark class
    protected Intent intent;

    public static final int LAUNCH_TIMEOUT = 140000;
    public static final int DEFAULT_WAIT_ELEMENT_TIMEOUT = 3000; // 3 seconds
    public static final int DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT = 10000; // 10 seconds
    protected final Shell shell = new Shell();
    // stop testing if one test failed
    protected static boolean stopTesting = false;
    protected String checkAlertWindow = null;

    public Benchmark(String activity, String searchRegex){
        super(activity, searchRegex);
    }

    @Rule // stop testing rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description){
            stopTesting = true;
            String testName = description.getMethodName();
            String className= description.getClassName();
            className = className.substring(className.lastIndexOf('.') + 1);
            Log.d(LOG_TAG, String.format("JUnit [%s#%s] test FAILED !", className, testName));

            try {
                takeUIDump(String.format("%s_%s", className, testName));
            } catch (Exception er) {
                Log.d(LOG_TAG, er.toString());
            }
        }

        @Override
        protected void finished(Description description){ // replace @After annotation
            try {
                tearDown();
            } catch (Exception e) {
                stopTesting = true;
                Log.d(LOG_TAG, e.toString());
            }
        }
    };

    @Before
    public void tearUp() throws Exception {
        // stop testing if one test failed
        try {
            Assume.assumeTrue("Testing was interrupted due to FAIL result of previous test !", !stopTesting);
            this.openApp();
            print(getClass().getSimpleName() + " test case launched...");
        }catch (Exception e){
            stopTesting = true;
            throw new Exception(e);
        }
    }

    public void tearDown() throws Exception {
        if (!stopTesting){
            checkAlert(checkAlertWindow);
            this.closeApp();
            print("Test case completed.");
        } else {
            this.closeApp();
        }
    }

    public void openApp() throws Exception {
        start(); // runner helper start
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        intent = context.getPackageManager().getLaunchIntentForPackage(BASIC_PACKAGE);

        if (intent==null)
            throw new Exception("Application cannot be launched !");

        intent = intent.addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
//        boolean isStart = mDevice.wait(Until.hasObject(By.pkg(BASIC_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

        // Check application launched
        BySelector pack = By.pkg(BASIC_PACKAGE); // apk package
        String[] permissions = Extras.getStringArray("permissions");
        BySelector allow = By.res(Pattern.compile(".*?:id/permission_allow.*")); // permission button
        long t = System.currentTimeMillis();
        boolean isStart = false;
        while (System.currentTimeMillis() - t < LAUNCH_TIMEOUT){
            if (mDevice.findObject(pack) != null){
                isStart = true;
                break;
            }else if (mDevice.findObject(allow) != null && permissions.length > 0){
                UiTools.allowPermissions(permissions);
            }
            sleep(500);
        }
        if (!isStart)
            throw new Exception("Application launch error: Timeout expired !");
    }

    public void closeApp() throws Exception {
        stop(); // runner helper stop
        boolean isClose = false;
        if (intent != null){
            Context context = InstrumentationRegistry.getInstrumentation().getContext();
            isClose = context.stopService(intent);
        }
        if (!isClose){
            Log.d(LOG_TAG, "Close application via shell command.");
            shell.stopApp(BASIC_PACKAGE); // try to stop via shell
        }
    }

    public void waitForLogcatMessage(String msg, int finish, int checktime) throws Exception {
        // wait for msg in logcat // logcat -d
        this.waitForLogcatMessage(null, msg, finish, checktime);
    }

    public String waitForLogcatMessage(String tag, String msg, int finish, int checktime) throws Exception {
        // wait for msg in logcat // logcat -d | grep tag
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < finish) {
            String res = shell.pureExecute("logcat -d " + (tag == null ? "" : (" | grep " + tag)), 30 * 1000);
            if (res.toLowerCase().contains(msg)) return res;
            sleep(checktime);
        }
        throw new Exception("Message: \"" + msg + "\" not found in logcat !");
    }
}
