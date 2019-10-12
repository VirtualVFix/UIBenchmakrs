package kernel.bsp.test.ui.benchmarks.support;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Settings;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class WiFi extends Settings {
    private final int MAX_POINTS_LIST_SCROLL_UP = 2;
    private final int MAX_POINTS_LIST_SCROLL_DOWN = 3;
    // selectors which displayed wifi is connected. Use in skip wizard
    protected List<BySelector> WIFI_CONNETED_SELECTORS = new ArrayList<>();

    public WiFi() {
        super("com.android.settings/android.net.wifi.PICK_WIFI_NETWORK", "wifi|wi.*?fi");
    }

    private UiObject2 getWiFiSwitchWidget() throws Exception {
        return mDevice.findObject(By.clazz("android.widget.Switch"));
    }

    private class WiFiPoint{
        public UiObject2 widget = null; // point widget
        public String name = null; // point name
        public String password = null; // point password
        public UiObject2 status = null; // point status
    }

    private WiFiPoint getWiFiPoint(String[] points){
        /* search WiFi point widget by specified names and return it with status */
        WiFiPoint point = new WiFiPoint();
        // search one of available points
        for (int k=0; k<MAX_POINTS_LIST_SCROLL_UP; k++){
            for (int i=0; i<MAX_POINTS_LIST_SCROLL_DOWN; i++){
                // scan all available points
                for (int j=0; j<points.length-1; j+=2){
                    String name = points[j];
                    if (name == null || name.equals("")) // skip empty names
                        continue;
//                print(String.format("Name: %s", name));
                    UiObject2 widget = mDevice.findObject(By.text(name));
                    if (widget != null) {
                        point.widget = widget;
                        point.name = name;
                        point.password = points[j+1];
                        UiObject2 status = null;
                        try {
                            status = widget.getParent().findObject(By.res(Pattern.compile(".*?summary",
                                                                          Pattern.CASE_INSENSITIVE)));
                        }catch (Exception er){
                            Log.d(LOG_TAG, er.toString());
                        }
                        point.status = status != null ? status : null;
                        return point;
                    }
                }
                UiTools.swipeDown(50);
            }
            UiTools.swipeUp();
        }
        return point;
    }

    // wait when wifi return status "connected"
    private boolean isWiFiConnected(WiFiPoint point) throws InterruptedException {
        int timeout = Extras.getInt("timeout", 60); //  timeout of waiting till "connected" status presents

        // update widget
        UiTools.swipeUp();
        point = this.getWiFiPoint(new String[] {point.name, point.password});
        Log.d(LOG_TAG, String.format("Update: Point \"%s\" Status: \"%s\" Password: \"%s\"",
                                     point.name, point.status != null ? point.status.getText(): "NULL", point.password));

        // wait "connected" status
        Pattern fail_pattern = Pattern.compile(".*?(problem|error).*", Pattern.CASE_INSENSITIVE);
        Pattern skip_pattern = Pattern.compile(".*?(save|no internet).*", Pattern.CASE_INSENSITIVE);
        boolean first_skip = true; // first skip status
        long t = System.currentTimeMillis();
        while (System.currentTimeMillis() - t < timeout*1000){
            try {
                mDevice.wakeUp();
                String msg = point.status != null ? point.status.getText().toLowerCase() : "NULL";
                Log.d(LOG_TAG, msg);
                // lost point widget
                if (point.widget == null){
                    Log.d(LOG_TAG, String.format("Point widget was lost !"));
                    break;
                }
                // wrong password
                if (mDevice.findObject(By.res(Pattern.compile(".*?:id/password_incorrect_text",
                        Pattern.CASE_INSENSITIVE))) != null){
                    mDevice.findObject(By.clazz("android.widget.Button")
                            .text(Pattern.compile("cancel", Pattern.CASE_INSENSITIVE))).click();
                    print(String.format("Point \"%s\": password is incorrect !", point.name));
                    break;
                }else if (msg.equals("connected")){ // connected status message
                    printResults(String.format("%s:%s", msg, point.name));
                    return true;
                }else if (WIFI_CONNETED_SELECTORS.size() > 0){ // connected selectors for external functions
                    for (BySelector selector : WIFI_CONNETED_SELECTORS) {
                        UiObject2 obj = mDevice.findObject(selector);
                        if (obj != null) {
                            printResults(String.format("Stop by selector:%s", obj.getText()));
                            return true;
                        }
                    }
                }else if (fail_pattern.matcher(msg).find()){ // fail pattern
                    break;
                }else if (skip_pattern.matcher(msg).find()){ // && auth){ // skip pattern
                    if (!first_skip) {
                        break;
                    }
                    mDevice.wait(Until.gone(By.text(skip_pattern)), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
                    first_skip = false;
                }else if (msg.equals("NULL")){ // update widget
                    point = this.getWiFiPoint(new String[] {point.name, point.password});
                }
            }catch (Exception e){ // update widget
                point = this.getWiFiPoint(new String[] {point.name, point.password});
                Log.d(LOG_TAG, e.toString());
            }
            sleep(1000);
        }
        try{
            // print last point status to log
            Log.d(LOG_TAG, String.format("Point \"%s\" Status: \"%s\"", point.name,
                                         point.status != null ? point.status.getText(): "NULL"));
        }catch (Exception e) { // update widget
            Log.d(LOG_TAG, e.toString());
        }
        return false;
    }

    @Test
    public void connectToWiFiPoint() throws Exception {
        /* connect to WiFi point */
        String[] points_array = Extras.getStringArray("points");
        if (points_array == null)
            throw new Exception("WiFi points is not specified !");
        if (points_array.length % 2 != 0)
            throw new Exception("All WiFi points should be specified with password or null password ! Ex: " +
                                "[point1, point1_pwd, point2, null, ...].");

        // enable WiFi
        this.enableWiFi();
        // wait until gone Wi-Fi searching
        mDevice.wait(Until.gone(By.text(Pattern.compile(".*?for Wi.*?Fi network.*", Pattern.CASE_INSENSITIVE))),
                     DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT*3);
        // wait until gone points with connection status
        mDevice.wait(Until.gone(By.text(Pattern.compile(".*?(connecting|authenticating|obtaining).*",
                                        Pattern.CASE_INSENSITIVE))), DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT*3);
        // wait list update
        sleep(DEFAULT_LONG_WAIT_ELEMENT_TIMEOUT);
        this.unlockDevice();

        // check already connected point
        UiObject2 obj = mDevice.findObject(By.res(Pattern.compile(".*?summary"))
                                             .text(Pattern.compile("connected", Pattern.CASE_INSENSITIVE)));
        if (obj != null){
            // print Point name and status
            printResults(String.format("%s:%s", obj.getText(), obj.getParent()
                    .findObject(By.res(Pattern.compile(".*?(title|name)"))).getText()));
            return;
        }

        UiTools.swipeUp();
        // connect to points from list
        while (points_array.length >= 2){
            // search point
            WiFiPoint point = this.getWiFiPoint(points_array);
            // connect to point
            if (point.widget != null) {
                if (point.status == null || !point.status.getText().toLowerCase().contains("connected")) {
                    Log.d(LOG_TAG, String.format("Trying connect to \"%s\" point", point.name));
                    boolean alert = point.widget.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                    // enter password if required and connect to point
                    if (alert) {
                        BySelector connectBtnSelector = By.clazz("android.widget.Button")
                                .text(Pattern.compile("connect", Pattern.CASE_INSENSITIVE));
                        UiObject2 connectButton = mDevice.findObject(connectBtnSelector);
                        UiObject2 pwd_edit = mDevice.findObject(By.res("com.android.settings:id/password"));
                        if (pwd_edit != null && pwd_edit.isEnabled()){ // password is required
                            pwd_edit.setText(point.password);
                            if (connectButton == null) {
                                mDevice.pressEnter();
                                mDevice.wait(Until.findObject(connectBtnSelector), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                                connectButton = mDevice.findObject(connectBtnSelector);
                                // check that connection button is enabled
                                if (connectButton == null || !connectButton.isEnabled()) {
                                    throw new Exception("Enter password error !");
                                }else {
                                    connectButton.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                                }
                            }else {
                                connectButton.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                            }
                        }else if (connectButton != null && connectButton.isEnabled()) { // connect button is active
                            connectButton.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);
                        } else {
                            Log.d(LOG_TAG, "Unsupported Wi-Fi popup window: No CONNECT button no password editor !");
                        }
                    }
                }

                // wait for wifi connection
                if (this.isWiFiConnected(point)) {
                    return;
                }

                // cur point list
                List<String> tmp = new ArrayList<>();
                int index = -1;
                for (int i=0; i<points_array.length; i++){
                    if (index == -1 && points_array[i].equals(point.name))
                        index = i+1;

                    if (i > index)
                        tmp.add(points_array[i]);
                }
                points_array = tmp.toArray(new String[]{});
            }else {
                throw new Exception("None of specified WiFi points were found or connected !");
            }
        }
        throw new Exception("None of specified WiFi points were connected !");
    }

    @Test
    public void isWiFiEnabled() throws Exception {
        /* get WiFi status */
        UiObject2 swt = this.getWiFiSwitchWidget();
        printResults(String.format("%b", swt.isChecked()));
    }

    @Test
    public void enableWiFi() throws Exception {
        /* enable WiFi */
        UiObject2 swt = this.getWiFiSwitchWidget();
        if (!swt.isChecked()) {
            swt.click();
            swt.wait(Until.checked(true), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        }
        printResults(String.format("%b", swt.isChecked()));
    }

    @Test
    public void disableWiFi() throws Exception {
        /* disable WiFi */
        UiObject2 swt = this.getWiFiSwitchWidget();
        if (swt.isChecked()){
            swt.click();
            swt.wait(Until.checked(false), DEFAULT_WAIT_ELEMENT_TIMEOUT);
        }
        printResults(String.format("%b", swt.isChecked()));
    }
}