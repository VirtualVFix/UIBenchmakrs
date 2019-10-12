package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.base.Browser;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Vellamo3 extends Benchmark{

    public Vellamo3(){
        super("com.quicinc.vellamo/com.quicinc.vellamo.main.MainActivity", "vellamo");
    }

    private void runBenchmark(Rect bounds, int finish) throws Exception{
        UiObject start = null;
        for (int i=0; i<3; i++){
            start = mDevice.findObject(new UiSelector().resourceId("com.quicinc.vellamo:id/card_launcher_run_button").instance(i));
            if (start.getBounds().top == bounds.top) break;
        }

        if (start == null) throw new Exception("Benchmark can not be started.");
        start.clickAndWaitForNewWindow(DEFAULT_WAIT_ELEMENT_TIMEOUT);

        UiObject2 butt = mDevice.findObject(By.res(Pattern.compile(".*:id/main_toolbar_operation_button")).text(Pattern.compile("got it", Pattern.CASE_INSENSITIVE)));
        if (butt != null)
            butt.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);

        print("Wait for results...");
        BySelector goback = By.clazz("android.widget.ImageButton").res(Pattern.compile(".*id/main_toolbar_goback_button"));
        UiTools.waitForExists(goback, finish*1000);
        mDevice.findObject(goback).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);

        UiObject topbutt = mDevice.findObject(new UiSelector().className("android.widget.ImageButton").resourceId("com.quicinc.vellamo:id/score_pane_topbar_button"));
        if(topbutt.exists()) topbutt.clickAndWaitForNewWindow(DEFAULT_WAIT_ELEMENT_TIMEOUT);
        if(topbutt.exists()) topbutt.clickAndWaitForNewWindow(DEFAULT_WAIT_ELEMENT_TIMEOUT);
        UiTools.swipeLeft();
    }

    @Override
    public void tearUp() throws Exception {
        Browser browser = new Browser();
        print("Checking Browser...");
        browser.openBrowser();
        browser.skipDialogIfExists();
        browser.closeApp();
        super.tearUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        Browser browser = new Browser();
        browser.closeApp();
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");

        print("Running benchmarks...");
        if (mDevice.findObject(new UiSelector().className("android.widget.TextView").textContains("User License")).exists()){
            UiObject accept = mDevice.findObject(new UiSelector().className("android.widget.Button").textMatches("Accept|ACCEPT"));
            accept.clickAndWaitForNewWindow(DEFAULT_WAIT_ELEMENT_TIMEOUT);
        }

        UiTools.swipeLeft();

        UiScrollable page = new UiScrollable(new UiSelector().className("android.support.v4.view.ViewPager").resourceId("com.quicinc.vellamo:id/viewpager"));
        UiObject test = mDevice.findObject(new UiSelector().resourceId("com.quicinc.vellamo:id/card_topbar_text").textContains("Browser"));
        if (!page.exists())
            UiTools.swipeLeft();

        UiTools.waitForExists(By.clazz("android.support.v4.view.ViewPager").res(Pattern.compile(".*:id/viewpager")), DEFAULT_WAIT_ELEMENT_TIMEOUT*2, true);
        page.scrollIntoView(test);

        UiCollection list = new UiCollection(new UiSelector().className("android.widget.LinearLayout").resourceIdMatches("(.*)?:id/(flavor_list|list)"));
        page.scrollIntoView(list);
        for (int i=0; i<2; i++){
            UiObject obj = list.getChild(new UiSelector().className("android.widget.ImageButton").instance(i));
            if (obj.exists() && !obj.isSelected()) obj.click();
        }

        // browser benchmark
        print("Running Browser benchmark...");
        this.runBenchmark(test.getBounds(), finish);

        //METAL benchmark
        test = mDevice.findObject(new UiSelector().resourceId("com.quicinc.vellamo:id/card_topbar_text").textContains("Metal"));
        if (!page.exists()) UiTools.swipeLeft();
        page.scrollIntoView(test);
        print("Running METAL benchmark...");
        this.runBenchmark(test.getBounds(), finish);

        // multicore benchmark
        test = mDevice.findObject(new UiSelector().resourceId("com.quicinc.vellamo:id/card_topbar_text").textContains("Multicore"));
        if (!page.exists()) UiTools.swipeLeft();
        page.scrollIntoView(test);
        print("Running Multicore benchmark...");
        this.runBenchmark(test.getBounds(), finish);
    }
}