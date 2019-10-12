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

public class Sunspider extends Chrome{

    @Test
    public void bench() throws Exception {
        String url = Extras.getString("url");
        int finish = Extras.getInt("finish");

        this.openPage(url);
        this.waitLoading(finish*1000);

        BySelector countdown = By.clazz("android.view.View").res(Pattern.compile("countdown|testframe"));
        sleep(1000);
        if (mDevice.findObject(countdown) != null){
            UiTools.waitUntilGone(countdown, finish*1000);
        }else{
            try {
                this.waitFinishing("Total", finish*1000);
            }catch (Exception e){
                Log.d(LOG_TAG, String.format("Sunspider Wait for finish error: %s", e.toString()));
            }
        }
        this.checkAlert();
        BySelector content = By.clazz("android.view.View").res("results");
        if (mDevice.findObject(content) == null)
            content = By.clazz("android.webkit.WebView");
        this.collectResults(content, 5);
    }
}