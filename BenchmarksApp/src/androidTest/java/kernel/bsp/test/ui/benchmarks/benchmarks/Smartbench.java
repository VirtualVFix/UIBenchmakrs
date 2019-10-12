package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Smartbench extends Benchmark{

    public Smartbench(){
        super("com.smartbench.twelve/.Smartbench2012", "smartbench");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish", 300);
        // wait run button
        UiObject2 run = UiTools.waitForExists(By.text("Run SmartBench"), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT, true);
        print("Running benchmark...");
        run.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        // wait for results
        print("Wait for results...");
        BySelector result = By.clazz("android.widget.TextView").textContains("Display Index Scores");
        UiTools.waitForExists(result, finish*1000);
        mDevice.findObject(result).click();
    }
}