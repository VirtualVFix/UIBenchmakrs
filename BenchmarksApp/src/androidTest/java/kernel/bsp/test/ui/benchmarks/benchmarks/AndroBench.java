package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class AndroBench extends Benchmark{

    public AndroBench(){
        super("com.andromeda.androbench2/com.andromeda.androbench2.main", "androbench");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        print("AndroBench benchmark starting...");
        BySelector start = By.res(Pattern.compile(".*:id/btnStartingBenchmarking"));
        UiTools.waitForExists(start, DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        print("Running benchmark...");
        UiObject2 startbtn = mDevice.findObject(start);
        BySelector yesbtn = By.clazz("android.widget.Button").res(Pattern.compile(".*:id/button1"));
        // fix stuck issue on start button press
        mDevice.pressRecentApps();
        sleep(2000);
        mDevice.pressBack();
        // end fix
        startbtn.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        mDevice.findObject(yesbtn).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        print("Wait for results...");
        BySelector cancel = By.clazz("android.widget.Button").text(Pattern.compile("cancel", Pattern.CASE_INSENSITIVE));
        UiTools.waitForExists(cancel, finish*1000);
        mDevice.findObject(cancel).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        this.results();
    }

    private void results() throws Exception {
        print("Collecting results...");
        UiTools.waitForExists(By.scrollable(true).res(Pattern.compile(".*:id/TestingView")), DEFAULT_WAIT_ELEMENT_TIMEOUT, true);

        UiScrollable scroll = new UiScrollable(new UiSelector().resourceId("com.andromeda.androbench2:id/TestingView").scrollable(true));
        String results = "[";
        for (int i=0; i<3; i++){
            for (int j=0; j<scroll.getChildCount(); j++){
                UiObject frame = scroll.getChild(new UiSelector().className("android.widget.FrameLayout").index(j));

                UiObject test = frame.getChild(new UiSelector().className("android.widget.TextView").resourceId("com.andromeda.androbench2:id/row_testing_name"));
                UiObject status = frame.getChild(new UiSelector().className("android.widget.TextView").resourceId("com.andromeda.androbench2:id/row_testing_status"));

                if (test.exists() && status.exists() && !results.contains(test.getText())) results += "('" + test.getText() + "','" + status.getText() + "'),";
            }
            UiTools.swipeDown(50);
        }
        this.printResults(results + "]");
    }
}