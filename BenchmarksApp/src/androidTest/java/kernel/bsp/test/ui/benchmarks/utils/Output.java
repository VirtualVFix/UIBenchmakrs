package kernel.bsp.test.ui.benchmarks.utils;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 10/14/2016.
 */

@SdkSuppress(minSdkVersion = 17)
public class Output {

    public static final String LOG_TAG = "BSP.TEST.UI.BENCHMARKS";
    protected UiDevice mDevice;
    private String TEMP_PATH = "/data/local/tmp/";

    public Output(){
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    public static void print(String msg){
        Bundle bundle = new Bundle();
        bundle.putString("PRINT", msg);
        InstrumentationRegistry.getInstrumentation().sendStatus(0, bundle);
        Log.d(LOG_TAG, msg);
    }

    private boolean generateScreenshot(String path) throws Exception {
        boolean result = false;
        try {
            if (!mDevice.takeScreenshot(new File(path))) {
                throw new Exception("Screenshot cannot be created.");
            }
            result = true;
        } catch (Exception e) {
            try {
                Log.d(LOG_TAG, "Trying to make screenshot via shell command...");
                new Shell().execute("screencap " + path);
                result = true;
                Log.d(LOG_TAG, "Done");
            } catch (Exception er) {
                Log.d(LOG_TAG, e.toString());
            }
        }
        return result;
    }

    // take screenshot, print results + add screnshot path to end of report
    protected void takeScreenshot(String name, String data) throws Exception{
        String screen_path = TEMP_PATH + name;
        if (this.generateScreenshot(screen_path))
            this.printResults(data + "#SCREENSHOT#" + screen_path);
        else
            this.printResults(data);
    }

    public void takeScreenshot(String name) throws Exception{
        this.takeScreenshot(name, "");
    }

    // get UI dump with screenshot if possible
    protected void takeUIDump(String name, String data) throws Exception {
        String path = TEMP_PATH + name;
        boolean hasdump = false;
        boolean hasscreen = generateScreenshot(path + ".png");
        File file = new File(path + ".uix");
        for (int i=0; i<2; i++) {
            try {
                if (!file.exists())
                    file.createNewFile();
                if (file.canWrite()) {
                    mDevice.dumpWindowHierarchy(file);
                    hasdump = true;
                }
            } catch (Exception er) {
                Log.d(LOG_TAG, er.toString());
                new Shell().execute("touch " + path + ".uix");
            }
        }

        if (hasdump & hasscreen)
            this.printResults(String.format("%s#UIDUMP#%s.uix;%s.png", data, path, path));
        else if (hasscreen)
            this.printResults(String.format("%s#SCREENSHOT#%s.png", data, path));
        else
            this.printResults(data);
    }

    public void takeUIDump(String name) throws Exception {
        this.takeUIDump(name, "");
    }
    // print benchmark results to console output
    public void printResults(String results) throws UnsupportedEncodingException {
        print(String.format("#RESULTS#%s#ENDRESULTS#", results.replaceAll("\n", " ")));
    }
}