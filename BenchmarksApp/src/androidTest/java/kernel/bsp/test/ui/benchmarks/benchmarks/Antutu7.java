package kernel.bsp.test.ui.benchmarks.benchmarks;

import android.support.test.uiautomator.*;
import kernel.bsp.test.ui.benchmarks.base.Benchmark;
import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.UiTools;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Antutu7 extends Benchmark{
    public Antutu7(){
        super("com.antutu.ABenchMark/.ABenchMarkStart", "antutu");
    }

    @Test
    public void bench() throws Exception {
        int finish = Extras.getInt("finish");
        BySelector start = By.res((Pattern.compile(".*?:id/main_test_(start_title|finish_retest)")));
        UiObject2 start_button = UiTools.waitForExists(start, 20000);
        print("Running benchmark...");
        start_button.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);

        // check permissions
        UiObject2 allow_btn = mDevice.findObject(By.res(Pattern.compile(".*?:id/permission_allow.*")));
        if (allow_btn != null)
            allow_btn.clickAndWait(Until.newWindow(), DEFAULT_WAIT_ELEMENT_TIMEOUT);

        print("Wait for completion...");
        UiTools.waitForExists(By.res(Pattern.compile(".*id/result_details.*")), finish*1000);
        this.collectResults();
    }

    private void collectResults() throws Exception {
        print("Collecting results...");
        String results = "[('Score',";
        UiObject2 score = mDevice.findObject(By.res(Pattern.compile(".*:id/textViewTotalScore")));
        results += "'" + score.getText() + "'),";
        List<String> block_list = Arrays.asList("CPU", "GPU", "UX", "MEM");

        UiScrollable scroll = new UiScrollable(new UiSelector().resourceIdMatches(".*?:id/result_details_.*")
                .scrollable(true));
        for (String block : block_list){
            UiObject item = scroll.getChildByText(new UiSelector().className("android.widget.TextView"),
                    block, true);
            item.click();
            sleep(1000);

            for (UiObject2 grp : mDevice.findObject(By.res(Pattern.compile(".*?:id/result_details_.*"))).getChildren()){
                if (grp.getChildCount() >= 3){
                    UiObject2 name = grp.findObject(By.res(Pattern.compile(".*?:id/textViewScoreName"))
                            .clazz("android.widget.TextView"));
                    UiObject2 value = grp.findObject(By.res(Pattern.compile(".*?:id/textViewScore"))
                            .clazz("android.widget.TextView"));
                    if (name != null && value != null) {

                        String tmp = "('" + name.getText() + "','" + value.getText() + "'),";
                        if (block_list.contains(name.getText())
                                && block_list.indexOf(name.getText()) > block_list.indexOf(block))
                                break;

                        if (!results.contains(tmp))
                            results = results.concat(tmp);
                    }
                }
            }
        }
        printResults(results + "]");
    }
}