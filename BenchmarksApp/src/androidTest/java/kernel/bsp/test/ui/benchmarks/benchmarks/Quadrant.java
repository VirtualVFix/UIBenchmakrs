package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Quadrant extends Benchmark{

    public Quadrant(){
        super("com.aurorasoftworks.quadrant.ui.professional/.QuadrantProfessionalLauncherActivity", "quadrant");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        this.stopWatcher();

        if (mDevice.findObject(By.clazz("android.widget.TextView").res(Pattern.compile(".*:id/alertTitle"))) != null)
            mDevice.findObject(By.clazz("android.widget.Button").res(Pattern.compile(".*:id/button1"))).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT*2);
        print("Running benchmark...");
        BySelector run = By.clazz("android.widget.TextView").text(Pattern.compile("run full benchmark", Pattern.CASE_INSENSITIVE));

        UiTools.waitForExists(run, DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT, true);
        mDevice.findObject(run).clickAndWait(Until.newWindow(), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        print("Wait for results...");

        this.startWatcher();
        UiTools.waitForExists(By.res(Pattern.compile("com.aurorasoftworks.quadrant.ui.professional:id/chart")), finish*1000);
    }
}