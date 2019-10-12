package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiCollection;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class ThreeDMark extends Benchmark{

    public ThreeDMark(){
        super("com.futuremark.dmandroid.application/com.futuremark.dmandroid.application.activity.MainActivity",
              "3dmark");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        Float[] demo_button = Extras.getFloatArray("demo_button");
        Float[] start_button = Extras.getFloatArray("start_button");
        Float[] letsgo_button = Extras.getFloatArray("letsgo_button");

        BySelector settings = By.clazz("android.widget.TextView")
                .res(Pattern.compile("com.futuremark.dmandroid.application:id/menuSettings"));
        UiTools.waitForExists(settings, DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT*2, true);
        mDevice.findObject(settings).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);

        // demo button
        UiObject2 demo = mDevice.wait(Until.findObject(By.clazz("android.view.View")
                .desc(Pattern.compile("yes|no", Pattern.CASE_INSENSITIVE))), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        if (demo != null){
            if (demo.getContentDescription().equalsIgnoreCase("yes"))
//                demo.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                demo.click();
        }else {
            mDevice.click((int)(demo_button[0]*UiTools.width()), (int)(demo_button[1]*UiTools.height()));
        }
        sleep(DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        mDevice.pressBack();

        // skip let's go button if exists
        UiObject2 letsgo = mDevice.wait(Until.findObject(By.clazz("android.view.View")
                .desc(Pattern.compile("Ok, let's go! ", Pattern.CASE_INSENSITIVE))), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        if (letsgo != null) {
            letsgo.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
//            letsgo.click();
        }else{
            mDevice.click((int)(letsgo_button[0]*UiTools.width()), (int)(letsgo_button[1]*UiTools.height()));
            sleep(DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        }

        // check if see run buttons
        if (mDevice.findObject(By.text(Pattern.compile("run", Pattern.CASE_INSENSITIVE)).focusable(true)) != null){
            UiTools.waitForExists(By.text(Pattern.compile("run", Pattern.CASE_INSENSITIVE))
                    .focusable(true).enabled(true), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT*3);
        }

        // launch button
        print("Running benchmark...");
        UiObject content = new UiCollection(new UiSelector().className("android.webkit.WebView").scrollable(true))
                .getChild(new UiSelector().className("android.view.View").index(0));
        if (content.exists()){
            int icestorm_ind = -3;
            for (int i=0; i<content.getChildCount(); i++){
                UiObject launch = mDevice.findObject(new UiSelector().className("android.view.View").index(i));
                if (launch.getContentDescription().equalsIgnoreCase("ice storm unlimited")) icestorm_ind = i;
                if (launch.getContentDescription().equalsIgnoreCase("run ") && i == (icestorm_ind+2)){
                    launch.click();
                    sleep(2000);
                    break;
                }
            }
        }
        mDevice.click((int)(start_button[0]*UiTools.width()), (int)(start_button[1]*UiTools.height()));

        print("Wait for results...");
        UiTools.waitForExists(By.clazz("android.widget.TextView")
                .text(Pattern.compile("RESULT DETAILS", Pattern.CASE_INSENSITIVE)), finish*1000);
        sleep(2000);
        this.results();
    }

    private void results() throws Exception {
        print("Collecting results...");
        String results = "[";
        // scroll down
        for (int i=0; i<5; i++) {
            List<UiObject2> list = mDevice.findObjects(By.clazz("android.widget.ListView"));
            for (UiObject2 obj : list) {
                if (obj.getChildCount() > 2) { // skip first list with total score only
                    List<UiObject2> values = obj.getChildren();
                    int last = values.size() % 2 == 0 ? values.size() : values.size()-1; // remove last values if size odd
                    for (int j = 0; j < last; j++) {
                        String context = values.get(j).getContentDescription();
                        String text = values.get(j).getText();
                        String tmp = (j % 2 == 0 ? "('" : "'") + (context == null ? text : context) + (j % 2 == 1 ? "')," : "',");
                        if (!results.contains(tmp))
                            results += tmp;
                    }
                }
            }
            UiTools.swipeDown(50);
        }
        this.printResults(results + "]");
    }
}