package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class AndEBenchPro extends Benchmark{

    public AndEBenchPro(){
        super("com.eembc.andebench/com.eembc.andebench.DrawerMain", "andebench");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        print("AndEBenchPro benchmark starting...");
        BySelector content = By.pkg(BASIC_PACKAGE).res("android:id/content");
        UiTools.waitForExists(content, DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);

        UiObject2 license = mDevice.findObject(By.res("android:id/button1").text(Pattern.compile("i agree", Pattern.CASE_INSENSITIVE)));
        if (license != null){
            license.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        }

        if (mDevice.findObject(By.res("com.eembc.andebench:id/res_expandableListView1")) != null)
            mDevice.pressBack();

        BySelector start = By.res("com.eembc.andebench:id/s1_runall");
        UiTools.waitForExists(start, DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT*10, true);

        print("Running benchmark...");
        mDevice.findObject(start).click();
        print("Wait for results...");

        BySelector results = By.res("com.eembc.andebench:id/res_expandableListView1");
        UiTools.waitForExists(results, finish*1000);
        this.results();
    }

    private void results() throws Exception {
        print("Collecting results...");
        String results = "[";
        results += "('Score','" + mDevice.findObject(By.res("com.eembc.andebench:id/res_text_intro")).getText() + "'),";
        for (int i=0; i<2; i++) { // scroll for small screen
            for (UiObject2 list : mDevice.findObject(By.res("com.eembc.andebench:id/res_expandableListView1")).getChildren()) {
                List<UiObject2> inner = list.getChildren();
                if (!results.contains(inner.get(1).getText()))
                    results += "('" + inner.get(1).getText() + "','" + inner.get(2).getText() + "'),";
            }
            UiTools.swipeDown(50);
        }
        this.printResults(results + "]");
    }
}