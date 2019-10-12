package kernel.bsp.test.ui.benchmarks.base;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiObject2;

import java.util.ArrayList;
import java.util.List;

import kernel.bsp.test.ui.benchmarks.utils.UiTools;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 10/7/2016.
 */

public class Chrome extends Browser {
    /* Chrome browser tests */

    private void waitDescription(String desc, int time) throws Exception{
        /* Wait for description. Time in milliseconds */
        UiTools.waitForExists(By.clazz("android.view.View").descContains(desc), time);
    }

    public void waitLoading(String desc, int time) throws Exception{
        /* wait for loading by object description */
        this.waitLoading(time);
        this.waitDescription(desc, time);
    }

    public void waitFinishing(String desc, int time) throws Exception{
        /* wait for finishing by object description */
        print("Wait for completion...");
        this.waitDescription(desc, time);
    }

    public void collectResults(UiObject2 root) throws Exception {
        this.collectResults(root, "", null);
    }

    public void collectResults(UiObject2 root, String results) throws Exception {
        this.collectResults(root, results, null);
    }

    public void collectResults(UiObject2 root, String results, String screenshot) throws Exception {
        /* collect browser results and take screenshot if screenshot variable is not null */
        print("Collecting results...");
        UiObject2 network = mDevice.findObject(By.descContains("Wi-Fi and mobile data are unavailable"));
        if (network != null) throw new Exception(network.getContentDescription());

        List<UiObject2> childrenList = root.getChildren();
        results += root.getContentDescription().equals("") ? (root.getContentDescription() + "\n") : "";

        results += root.getContentDescription() + "\n";
        for (UiObject2 obj : childrenList) {
            results += obj.getContentDescription() + "\n";
        }

        if (screenshot == null) {
            this.printResults(results);
        }else {
            this.takeScreenshot(screenshot, results);
        }

        if (results.replaceAll("\n"," ").trim().equals(""))
            throw new Exception("Results are not found.");
    }

    private List<String> scanHierarchy(UiObject2 obj, List<String> results) throws Exception {
        if (obj.getChildCount() == 0){
            String text = obj.getContentDescription() == null ? obj.getText() : obj.getContentDescription();
            if (text != null && results.indexOf(text) == -1) {
                results.add(text);
                results.add("\n");
            }
        }else{
            for (UiObject2 chobj: obj.getChildren()) {
                results = this.scanHierarchy(chobj, results);
            }
        }
       return results;
    }

    public void collectResults(BySelector selector, int scroll) throws Exception {
        /* Collect results with scroll and check sub elements */
        print("Collecting results...");
        List<String> results = new ArrayList<>();
        for (int i=0; i<scroll; i++){
            UiObject2 root = mDevice.findObject(selector);
            if (root == null)
                break;
            // scan hierarchy
            results = scanHierarchy(root, results);
            UiTools.swipeDown(50);
        }

        String summary = String.join("", results);
        if (summary.replaceAll("\n"," ").trim().equals(""))
            throw new Exception("Results are not found.");
        printResults(summary);
    }
}
