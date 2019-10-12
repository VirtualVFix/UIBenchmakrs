package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiSelector;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 *
 */

public class GFXBench4 extends Benchmark {
    public GFXBench4() {
        super("net.kishonti.gfxbench.gl.*.corporate/net.kishonti.app.MainActivity", "gfx");
        String version = Extras.getString("version");
        ACTIVITY = ACTIVITY.replace("*", version);
        BASIC_PACKAGE = BASIC_PACKAGE.replace("*", version);
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish", 20000);
        sleep(2000);
        // pass popup alert
        UiTools.passAlert(true, "Pushed data not found", null);
        UiTools.waitUntilGone(By.res(Pattern.compile(".*:id/init_progressbar")), finish * 1000 / 4);
        // click to home tab if button not found
        UiObject run = mDevice.findObject(new UiSelector().resourceIdMatches(".*:id/main_circleControl"));
        if (!run.exists()) {
            UiObject bar = mDevice.findObject(new UiSelector().resourceIdMatches(".*:id/tabbar_back")).getChild(new UiSelector().className("android.view.View").index(0));
            bar.click();
            sleep(1000);
        }

        print("Running benchmark...");
        run.clickAndWaitForNewWindow(DEFAULT_WAIT_ELEMENT_TIMEOUT);

        print("Wait for results...");
        UiObject res = mDevice.findObject(new UiSelector().className("android.widget.ListView").resourceIdMatches(".*:id/results_testList"));
        if (!res.waitForExists(finish * 1000))
            throw new Exception("Wait object exists Error: Timeout expired.");

        this.results();
    }

    private String scanHierarchy(UiObject2 obj) {
        String result = "";
        if (obj.getClassName().equals("android.widget.TextView")) {
            if (obj.getResourceName().contains("updated_result_item_group_title")) {
                result = "'" + obj.getText() + "','',";
            } else if (obj.getResourceName().contains("updated_result_item_name")) {
                result = "'" + obj.getText();
            } else if (obj.getResourceName().contains("updated_result_item_desc")) {
                result = " " + obj.getText().split("\\|")[0].trim() + "',";
            } else {
                result = "'" + obj.getText().trim().replaceAll("[,\\(\\)\\n]", "") + "',";
            }
        } else if (obj.getChildCount() != 0) {
            for (UiObject2 child : obj.getChildren()) {
                result += this.scanHierarchy(child);
            }
        }
        return result;
    }

    private void results() throws Exception {
        print("Collecting results...");
        String result = "[";

        BySelector hierarchy = By.res(Pattern.compile(".*id/results_testList", Pattern.CASE_INSENSITIVE));
        UiTools.waitForExists(hierarchy, DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT, true);

        int maxScrolls = 5;
        UiTools.swipeUp();
        for (int i = 0; i < maxScrolls; i++) {
            // scan results
            if (mDevice.findObject(hierarchy) != null) {
                for (UiObject2 obj : mDevice.findObject(hierarchy).getChildren()) {
                    String tmp = this.scanHierarchy(obj).trim().replaceAll("[^\\w\\s\\(\\)',.-]", "").replaceAll("[^\\p{ASCII}]", " ");
                    if (tmp.length() > 0)
                        tmp = tmp.substring(0, tmp.length() - 1); // cut last "," symbol
                    // skip single field, not full field and duplicates
                    if (tmp.split(",").length > 1 && !result.contains(tmp) && (tmp.length() - tmp.replace("'", "").length()) % 2 == 0) {
                        result += "(" + tmp + "),";
                    }
                }
            }
            UiTools.swipeDown(60);
        }
        this.printResults(result.substring(0, result.length() - 1) + "]");
    }
}