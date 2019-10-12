package kernel.bsp.test.ui.benchmarks.base;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;

import org.junit.Assume;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;
import static junit.framework.Assert.assertTrue;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 10/7/2016.
 */

public class Browser extends Benchmark {
    // base browser benchmarks class

    public Browser(){
        super("com.android.chrome/com.google.android.apps.chrome.Main", "chrome");
    }

    public void skipDialogIfExists() throws Exception{
        if (ACTIVITY.startsWith("com.android")){
            for (int i=0; i<5; i++){
                this.checkAlert();
                sleep(1000);
                UiObject2 skipbytext = mDevice.findObject(By.clazz("android.widget.Button").text(Pattern.compile("no thanks|next|skip", Pattern.CASE_INSENSITIVE)));
                if (skipbytext != null) skipbytext.click();
                UiObject2 skip = mDevice.findObject(By.clazz("android.widget.Button").res(Pattern.compile("(.*)?(accept|negative|skip|close)(.*)?", Pattern.CASE_INSENSITIVE)));
                if (skip != null) skip.click();
                else break;
            }
        }
    }

    @Override
    public void tearUp() throws Exception {
        Assume.assumeTrue("Testing was interrupted due to FAIL result of previous test !", !stopTesting);
        this.start();
        this.openBrowser();
        this.skipDialogIfExists();
        print(getClass().getSimpleName() + " test case launched...");
    }

    @Override
    public void tearDown() throws Exception{
        UiObject2 errors = mDevice.findObject(By.desc(Pattern.compile(".*you are offline|error|problem.*", Pattern.CASE_INSENSITIVE)));
        if (errors != null)
            throw new Exception(errors.getContentDescription());
        super.tearDown();
    }

    public void openBrowser() throws Exception {
        /* Open available browser
         * Correct work with Chrome alternative browser is not guaranteed */
        try {
            this.openApp();
        } catch (Exception e) {
            try{
                ACTIVITY = "com.swe.atego.browser/com.swe.atego.browser.BrowserActivity";
                BASIC_PACKAGE = ACTIVITY.split("/")[0];
                this.setRegex("swe browser");
                this.openApp();
                // Try SWE browser
                print("     Chrome browser not found. Using SWE browser !");
            } catch (Exception ex) {
                try{
                    // Try standart browser
                    ACTIVITY = "com.android.browser/com.android.browser.BrowserActivity";
                    BASIC_PACKAGE = ACTIVITY.split("/")[0];
                    this.setRegex("browser");
                    this.openApp();
                    print("     Chrome browser not found. Using alternative browser !");
                } catch (Exception exx) {
                    try {
                        // try China browser
                        ACTIVITY = "com.lenovo.browser/com.lenovo.browser.LeMainActivity";
                        BASIC_PACKAGE = ACTIVITY.split("/")[0];
                        this.setRegex("browser");
                        this.openApp();
                        print("     Chrome browser not found. Using Lenovo browser !");
                    } catch (Exception exxx) {
                        throw new Exception(e);
                    }
                }
            }
        }
    }

    public void openPage(String url) throws Exception {
        assertTrue("NULL URL ASSERT", url != null);
        UiObject2 urlBar;
        // Lenovo browser
        if (ACTIVITY.startsWith("com.lenovo.browser")){
            sleep(2000);
            mDevice.click(UiTools.width()/2, (int)(UiTools.height()*0.1));
            urlBar = mDevice.findObject(By.clazz("android.widget.EditText"));
        }else{
            // Chrome browser
            if (ACTIVITY.startsWith("com.android.chrome")){
                UiObject2 searchBar = mDevice.findObject(By.res(Pattern.compile("com.android.chrome:id/search_box_text")));
                if (searchBar != null)
                    searchBar.click();
            }
            urlBar = mDevice.findObject(By.clazz("android.widget.EditText").res(Pattern.compile(".*:id/url[_bar]*", Pattern.CASE_INSENSITIVE)));
        }
        urlBar.click();
        urlBar.clear();
        urlBar.setText(url);
        mDevice.pressEnter();
    }

    public void waitLoading(int time) throws Exception {
        /* Wait for loading page time in milliseconds */
        print("Wait for loading...");
        sleep(1000);
        UiTools.waitUntilGone(By.res(Pattern.compile(".*:id/progress")), time);
    }

    public void waitFinishing(int time) throws InterruptedException {
        /* Wait for finishing. Time in milliseconds */
        print("Wait for completion...");
        sleep(time);
    }
}
