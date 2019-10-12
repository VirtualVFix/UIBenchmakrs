package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.graphics.Rect;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Antutu6 extends Benchmark{
    public Antutu6(){
        super("com.antutu.ABenchMark/.ABenchMarkStart", "antutu");
    }

    private void allowPermissions(String[] permissions) throws Exception {
        List<String> allow = new ArrayList<>(); // allowed permissions

        // pass permission alert
        UiTools.passAlert(true, null, null);
        sleep(2000);
        // close background description
        UiObject actionbar = mDevice.findObject(new UiSelector().resourceIdMatches(".*id/action_bar"));
        if (!actionbar.exists()) return;
        Rect bounds = actionbar.getBounds();
        mDevice.click(bounds.right - (int)(UiTools.width()*0.06), bounds.bottom + (int)(bounds.bottom*0.4));
        // find permissions
        UiScrollable scroll = new UiScrollable(new UiSelector().scrollable(true));
        UiObject setting = scroll.getChildByText(new UiSelector().className("android.widget.TextView"), "Permissions", true);
        if (setting.exists()){
            setting.clickAndWaitForNewWindow(3000);
            // get permissions list
            UiObject list = mDevice.findObject(new UiSelector().className("android.widget.ListView").resourceIdMatches(".*:id/list"));
            for (int i=0; i<list.getChildCount(); i++){
                UiObject grp = list.getChild(new UiSelector().className("android.widget.LinearLayout").index(i));
                // search for required permission
                UiObject obj = grp.getChild(new UiSelector().className("android.widget.TextView").resourceIdMatches(".*:id/title")); // title
                UiObject sw = grp.getChild(new UiSelector().className("android.widget.Switch").resourceIdMatches(".*:id/switch_widget")); // switch
                for (String ch: permissions){
                    if (ch.toLowerCase().equals(obj.getText().toLowerCase())){
                        if (!allow.contains(ch)) allow.add(ch); // add permission to allow list
                        if (!sw.isChecked()) obj.click();
                    }
                }
            }
            // check if all permissions found
            if (permissions.length != allow.size())
                for (String ch: permissions)
                    if (!allow.contains(ch)) print(String.format("WARN: \"%s\" permission was not found !", ch));

            mDevice.pressBack();
            mDevice.pressBack();
        }
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        String[] permissions = Extras.getStringArray("permissions");
        // mute media volume
        this.stopWatcher();
        UiTools.waitForExists(By.res(Pattern.compile(".*:id/title_text")), 20000, true);

        UiObject start_button = mDevice.findObject(new UiSelector().className("android.widget.TextView")
                .resourceId("com.antutu.ABenchMark:id/start_test_text"));
        UiObject test_again = mDevice.findObject(new UiSelector().className("android.widget.Button")
                .resourceId("com.antutu.ABenchMark:id/tv_test_again"));
        print("Running benchmark...");
        // launch test and allow permissions
        while (start_button.exists() || test_again.exists()){
            if (start_button.exists()) start_button.clickAndWaitForNewWindow(5000); // launch tests
            else test_again.clickAndWaitForNewWindow(5000); // launch again button
            // check no permission popup alert and Screen overlay detected dialog
            this.allowPermissions(permissions);
        }
        this.startWatcher();
        // got it button
        UiObject2 gotit = mDevice.findObject(By.res("android:id/ok"));
        if (gotit != null) gotit.click();
        print("Wait for completion...");
        UiTools.waitForExists(By.res(Pattern.compile(".*:id/tv_score")), finish*1000);
        this.collectResults();
    }

    private void collectResults() throws Exception {
        print("Collecting results...");
        String results = "[('Score',";
        UiObject2 score = mDevice.findObject(By.res(Pattern.compile(".*:id/tv_score")));
        results += "'" + score.getText() + "'),";
        String[] res_name_list = new String[]{"3D", "UX", "CPU", "RAM"};
        // open results
        score.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);

        UiScrollable scroll = new UiScrollable(new UiSelector().resourceIdMatches(".*id/elv_detail_score"));
        for (String res_name : res_name_list){
            UiObject item = scroll.getChildByText(new UiSelector().className("android.widget.TextView"), res_name, true);
            item.click();
            for (int j=0; j<1; j++){ // scroll for horisontal screen
                for (int i=0; i<scroll.getChildCount(); i++){
                    // skip first group in first iteration
                    if (i==0 && res_name.equals(res_name_list[0])) continue;

                    // group of results
                    UiObject grp = scroll.getChild(new UiSelector().className("android.widget.LinearLayout").index(i))
                            .getChild(new UiSelector().className("android.widget.LinearLayout"));
                    UiObject name = grp.getChild(new UiSelector().className("android.widget.TextView").resourceId("com.antutu.ABenchMark:id/tv_title")); // result name
                    UiObject value = grp.getChild(new UiSelector().className("android.widget.TextView").resourceId("com.antutu.ABenchMark:id/tv_score")); // result value

                    // check selectable result
                    if (name.exists() && value.exists() && name.getText().contains(res_name)){
                        String tm = "('" + name.getText() + "','" + value.getText() + "'),";
                        if (!results.contains(tm)) results += tm;
                    }
                }
                UiTools.swipeDown(100);
            }
        }
        printResults(results + "]");
    }
}