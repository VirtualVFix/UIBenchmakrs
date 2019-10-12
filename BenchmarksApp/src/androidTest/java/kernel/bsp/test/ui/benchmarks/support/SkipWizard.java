package kernel.bsp.test.ui.benchmarks.support;

import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.Runner;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
public class SkipWizard extends Runner {
    private final String LAUNGUAGE = "English";
    private final int MAX_LANGUAGE_SCROLL = 10;
    private static final int DEFAULT_WAIT_ELEMENT_TIMEOUT = 3000; // 3 seconds
    private static final int DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT = 60000; // 60 seconds
    private List<BySelector> SKIP_BUTTONS_SELECTORS = new ArrayList<>(); // skip buttons
    private List<BySelector> FINISH_SELECTORS = new ArrayList<>(); // finish objects
    private List<BySelector> WAITOR_SELECTORS = new ArrayList<>(); // wait until gone objects
    private List<BySelector> WIFI_SELECTORS = new ArrayList<>(); // wifi stop connection selectors
    private List<BySelector> ERROR_SELECTORS = new ArrayList<>(); // error selectors

    public SkipWizard() {
        super("com.android.settings/com.android.settings.Settings", "setting");
        /* ===== buttons selectors ===== */
        // get started btn
        SKIP_BUTTONS_SELECTORS.add(By.clazz("android.widget.Button").res("com.google.android.setupwizard:id/start"));
        // skip buttons by name: skip, use any network for setup, set up as new,
        SKIP_BUTTONS_SELECTORS.add(By.text(Pattern.compile("not now" +
                                                           "|set up as new" +
                                                           "|.*?use any network for setup" +
                                                           "|skip.*" +
                                                           "|no thanks" +
                                                           "|next" +
                                                           "|.*?continue" +
                                                           "|set up later" +
                                                           "|let.*?s go" +
                                                           "|all set" +
                                                           "|done" +
                                                           "|got it", Pattern.CASE_INSENSITIVE)));

        // next buttons
        SKIP_BUTTONS_SELECTORS.add(By.res(Pattern.compile(".*?(skip" +
                                                          "|skip_button" +
                                                          "|navbar_next" +
                                                          "|navbar_more" +
                                                          "|next_button.*" +
                                                          "|start_button" +
                                                          "|agree)", Pattern.CASE_INSENSITIVE)));

        /* ===== Finish selectors ===== */
        // unlock and home screen widgets by ID
        FINISH_SELECTORS.add(By.res(Pattern.compile(".*?(keyguard_header" +
                                                    "|lock_icon" +
                                                    "|default_search_widget" +
                                                    "|workspace)", Pattern.CASE_INSENSITIVE)));
        // home screen launcher package
        FINISH_SELECTORS.add(By.pkg(Pattern.compile(".*?launcher.*", Pattern.CASE_INSENSITIVE)));

        /* ===== wait until gone objects ===== */
        WAITOR_SELECTORS.add(By.res(Pattern.compile(".*?title", Pattern.CASE_INSENSITIVE))
                               .clazz("android.widget.TextView")
                               .text(Pattern.compile("(checking" +
                                                     "|connecting" +
                                                     "|software update" +
                                                     "|update" +
                                                     "|just a sec" +
                                                     "|adding finish" +
                                                     "|waiting).*",Pattern.CASE_INSENSITIVE)));

        /* ===== wifi stop connection selectors ===== */
        WIFI_SELECTORS.addAll(WAITOR_SELECTORS);
        WIFI_SELECTORS.add(By.res(Pattern.compile(".*?title", Pattern.CASE_INSENSITIVE))
                             .clazz("android.widget.TextView")
                             .text(Pattern.compile("(about your privacy).*",Pattern.CASE_INSENSITIVE)));

        /* ============ error selectors ============ */
        ERROR_SELECTORS.add(By.text(Pattern.compile("(verify your account"+
                                                    "|couldn.*?t sign in" +
                                                    "|there was a problem communicating with google services).*", Pattern.CASE_INSENSITIVE)));
    }

    @Before
    public void tearUp() throws Exception {
        start(); // wake up phone and run alert watcher
    }

    @After
    public void tearDown() throws Exception {
        stop(); // remove alert watcher
    }

    /*  check if setup wizard not skipped */
    private boolean isSetupWizardPresent(){
        Log.d(LOG_TAG, String.format("SkipWizard: Current package: %s", mDevice.getCurrentPackageName()));
        if (mDevice.getCurrentPackageName().matches(".*?(setupwizard|setup|gms).*")) {
            return true;
        } else {
            for (BySelector selector: FINISH_SELECTORS){
                UiObject2 obj = mDevice.findObject(selector);
                if (obj != null) {
                    Log.d(LOG_TAG, String.format("SkipWizard: Finish object found: <%s> %s", obj.getResourceName(), obj.getClassName()));
                    return false;
                }
            }
            return true;
        }
    }
    /* select language */
    private boolean choseLanguage() throws Exception{
        // language picker button
        UiObject2 lang_picker = mDevice.findObject(By.clazz("android.widget.Button").res("com.google.android.setupwizard:id/language_picker"));
        // Verizon and OEM builds
        if (lang_picker != null){
            Log.d(LOG_TAG, String.format("SkipWizard: Selected language: %s",lang_picker.getText()));
            if (lang_picker.getText().toLowerCase().contains(LAUNGUAGE.toLowerCase()))
                return true;
            lang_picker.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
            // search required language
            for (int i=0; i<MAX_LANGUAGE_SCROLL; i++){
                List<UiObject2> list = mDevice.findObjects(By.clazz("android.widget.TextView"));
                for (int j=0; j<list.size(); j++){
                    Log.d(LOG_TAG, String.format("SkipWizard: Selected language: %s",list.get(j).getText()));
                    if (list.get(j).getText().toLowerCase().contains(LAUNGUAGE.toLowerCase())) {
                        list.get(j).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                        return true;
                    }
                }
                UiTools.swipeDown(80);
            }
            throw new Exception(String.format("\"%s\" language cannot be selected !", LAUNGUAGE));
        }else if (mDevice.findObject(By.res(Pattern.compile(".*?languageFragment"))) != null){ // China build
            UiObject2 lang = mDevice.findObject(By.text(Pattern.compile(LAUNGUAGE + ".*", Pattern.CASE_INSENSITIVE)));
            if (lang != null){
                lang.click();
                sleep(DEFAULT_WAIT_ELEMENT_TIMEOUT);
                return true;
            }
        }
        return false;
    }

    private boolean skipButtons(){
        for (BySelector selector: SKIP_BUTTONS_SELECTORS){
            UiObject2 btn = mDevice.findObject(selector);
            if (btn != null && btn.isEnabled()) {
                Log.d(LOG_TAG, String.format("SkipWizard: Press button: <%s> %s %s", btn.getResourceName(), btn.getText(), selector));
                btn.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                return true;
            }
        }
        return false;
    }

    private boolean waiting(){
        boolean status = false;
        for (BySelector selector : WAITOR_SELECTORS) {
            UiObject2 obj = mDevice.findObject(selector);
            if (obj != null) {
                Log.d(LOG_TAG, String.format("SkipWizard: waiting until gone: %s", obj.getText()));
                mDevice.wait(Until.gone(selector), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
                status = true;
            }
        }
        return status;
    }

    @Test
    public void skipWizard() throws Exception {
        int iterations = Extras.getInt("iterations", 30); //  max iterations before report fail
        for (int i = 0; i < iterations; i++) {
            try{
                boolean one_used = false; // one of elements used for skipp wizard
                Log.d(LOG_TAG, String.format("SkipWizard: iteration: %d/%d", i+1, iterations));
                mDevice.wakeUp();

                // exit if wizard skipped
                if (!this.isSetupWizardPresent()) {
                    UiObject2 gotit = mDevice.findObject(By.clazz("android.widget.Button").res("com.google.android.googlequicksearchbox:id/cling_dismiss_longpress_info"));
                    if (gotit != null)
                        gotit.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                    // wizard skipped
                    printResults(String.format("SetupWizard skipped after %d iterations. Current package: %s", i + 1, mDevice.getCurrentPackageName()));
                    // take screenshot by pass
//                    takeScreenshot("skip_wizard_pass.png", String.format("SetupWizard skipped after %d iterations. Current package: %s", i + 1, mDevice.getCurrentPackageName()));
                    return;
                }
                // language
                one_used |= this.choseLanguage();

                // device data collection on TMO builds
                if (mDevice.findObject(By.text(Pattern.compile("device data collection", Pattern.CASE_INSENSITIVE)).res(Pattern.compile(".*?text_header"))) != null){
                    // agree check box
                    UiObject2 agree = mDevice.findObject(By.clazz("android.widget.CheckBox"));
                    if (agree != null && agree.isChecked()){
                        agree.click();
                        one_used = true;
                    }
                }

                // skip buttons
                one_used |= this.skipButtons();

                // display all WiFi points
                if (mDevice.findObject(By.res(Pattern.compile(".*?(network_description|wifi_item)", Pattern.CASE_INSENSITIVE))) != null) {
                    Log.d(LOG_TAG, "SkipWizard: Network connection...");
                    UiTools.swipeDown();

                    // press don't connect if available
                    one_used |= this.skipButtons();
                    UiObject2 dont_connect = mDevice.findObject(By.res(Pattern.compile(".*network_dont_connect")));
                    if (dont_connect != null){
                        Log.d(LOG_TAG, "SkipWizard: Don't use network...");
                        dont_connect.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                        one_used = true;
                    }

                    // all networks
                    UiObject2 all_netw = mDevice.findObject(By.text(Pattern.compile(".*?all wi(.*?)+fi networks", Pattern.CASE_INSENSITIVE)));
                    if (all_netw != null) {
                        Log.d(LOG_TAG, "SkipWizard: All Network points...");
                        all_netw.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                        one_used = true;
                    }
                }

                // connect to WiFi point
                if (mDevice.findObject(By.text(Pattern.compile("select\\s+(wi.*?fi).*", Pattern.CASE_INSENSITIVE))) != null) {
                    Log.d(LOG_TAG, "SkipWizard: Connecting to WiFi point...");
                    // connect to point without enable wifi in settings
                    WiFi wifi = new WiFi() {
                        public void enableWiFi() {
                        }
                    };
                    wifi.WIFI_CONNETED_SELECTORS = WIFI_SELECTORS;
                    try { // try connect to WIFI
                        wifi.connectToWiFiPoint();
                    }catch (Exception e){
                        Log.d(LOG_TAG, e.toString());
                    }

                    // touch point if it was connected manually before
                    UiObject2 point = mDevice.findObject(By.res(Pattern.compile(".*?summary", Pattern.CASE_INSENSITIVE))
                            .text(Pattern.compile("connected", Pattern.CASE_INSENSITIVE)));
                    if (point != null){
                        point.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                        mDevice.findObject(By.text(Pattern.compile("connect", Pattern.CASE_INSENSITIVE))).clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                    }
                    one_used = true;
                }

                // waiting until objects gone
                one_used |= this.waiting();

                // error selectors
                for (BySelector selector : ERROR_SELECTORS) {
                    UiObject2 error = mDevice.findObject(selector);
                    if (error != null) {
                        Log.d(LOG_TAG, String.format("SkipWizard: error selector found: %s", error.getText()));
                        takeUIDump("skip_wizard_fail.png",
                                   String.format("\"%s\" window cannot be skipped.", error.getText()));
                        return;
                    }
                }

                if (!one_used) {
                    UiTools.swipeDown();
                    UiTools.swipeDown();
                    mDevice.sleep();
                    sleep(DEFAULT_WAIT_ELEMENT_TIMEOUT);
                }
            }catch (StaleObjectException er) {
//                Log.d(LOG_TAG, er.toString());
            }
        }
        // take screenshot by fail
        takeUIDump("skip_wizard_fail.png",
                   String.format("SkipSetupWizard procedure failed after %d iterations.", iterations));
//        throw new Exception(String.format("SkipSetupWizard procedure failed after %d iterations.", iterations));
    }
}