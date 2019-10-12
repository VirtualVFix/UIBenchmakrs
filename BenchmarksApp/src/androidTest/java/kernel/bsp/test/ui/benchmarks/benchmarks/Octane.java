package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.util.Log;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Chrome;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Octane extends Chrome{

    @Test
    public void bench() throws Exception {
        String url = Extras.getString("url");
        int finish = Extras.getInt("finish");

        this.openPage(url);
        this.waitLoading(finish*1000);

        BySelector run = By.res(Pattern.compile("run-octane", Pattern.CASE_INSENSITIVE));
        UiTools.waitForExists(run, finish*1000);
        mDevice.findObject(run).click();

        sleep(1000);
        BySelector progressBar = By.res(Pattern.compile("progress-bar-container"));
        if (mDevice.findObject(progressBar) != null){
            print("Wait for completion...");
            UiTools.waitUntilGone(progressBar, finish*1000);
        }else {
            try {
                this.waitFinishing("final score", finish*1000);
            } catch (Exception e) {
                Log.d(LOG_TAG, String.format("Octane Wait for finish error: %s", e.toString()));
            }
        }
        this.checkAlert();
        // collect results
        this.collectResults(By.clazz("android.webkit.WebView"), 8);
    }
}