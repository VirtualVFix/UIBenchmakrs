package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class CFBench extends Benchmark{

    public CFBench(){
        super("eu.chainfire.cfbench/.MainActivity", "cfbench");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        BySelector fullbench = By.clazz("android.widget.TextView").text(Pattern.compile("full benchmark", Pattern.CASE_INSENSITIVE));
        UiTools.waitForExists(fullbench, DEFAULT_WAIT_ELEMENT_TIMEOUT*2);
        print("Running benchmark...");
        mDevice.findObject(fullbench).clickAndWait(Until.newWindow(), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        print("Wait for results...");
        BySelector progress = By.clazz("android.widget.ProgressBar");
        UiTools.waitForExists(progress, DEFAULT_WAIT_ELEMENT_TIMEOUT*2);
        UiTools.waitUntilGone(progress, finish*1000);
        this.results();
    }

    private void results() throws Exception {
        print("Collecting results...");
        UiTools.waitForExists(By.scrollable(true), DEFAULT_WAIT_ELEMENT_TIMEOUT, true);

        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true));
        scroll.setAsVerticalList();
        scroll.getChildByText(new UiSelector().className("android.widget.TextView"), "Results");
        UiObject endscroll = mDevice.findObject(new UiSelector().className("android.widget.TextView").text("Comparison"));

        String results = "[";
        for (int i=0; i < 6; i++){
            for (int j=0; j<scroll.getChildCount(); j++){
                UiObject obj = scroll.getChild(new UiSelector().className("android.widget.LinearLayout").instance(j));
                if (obj.exists() && obj.getChildCount() == 2){
                    UiObject text = obj.getChild(new UiSelector().className("android.widget.TextView").instance(0));
                    UiObject value = obj.getChild(new UiSelector().className("android.widget.TextView").instance(1));
                    if (text.exists() && value.exists() && !results.contains(text.getText()))
                        results += "('" + text.getText() + "','" + value.getText() + "'),";
                }
            }
            if (endscroll.exists()) break;
            UiTools.swipeDown(50);
        }
        this.printResults(results + "]");
    }
}