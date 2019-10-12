package kernel.bsp.test.ui.benchmarks.base;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Until;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

public class Settings extends Benchmark {
    private final String APK_PACKAGE;

    public Settings() {
        super("com.android.settings/android.settings.SETTINGS", "setting");
        APK_PACKAGE = ACTIVITY.split("/")[1];
    }

    public Settings(String activity, String searchRegex){
        super(activity, searchRegex);
        APK_PACKAGE = ACTIVITY.split("/")[1];
    }

    @Override
    public void tearUp() throws Exception {
        start();
        shell.execute("am start -a " + APK_PACKAGE);
        boolean isStart = mDevice.wait(Until.hasObject(By.pkg(BASIC_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

        if (!isStart)
            throw new Exception("Settings launch error: Timeout expired !");
    }

    @Override
    public void tearDown() throws Exception {
        checkAlert();
        this.closeApp();
    }
}