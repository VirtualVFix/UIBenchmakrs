package kernel.bsp.test.ui.benchmarks.support;

import android.os.RemoteException;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import kernel.bsp.test.ui.benchmarks.utils.Extras;
import kernel.bsp.test.ui.benchmarks.utils.Output;
import kernel.bsp.test.ui.benchmarks.utils.Shell;

import static java.lang.Thread.sleep;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
public class Install extends Output {

    private final Shell shell = new Shell();
    private final class AlertWatcher implements Runnable {
        private boolean shutdown = false;

        // shutdown install thread
        public void shutDown(){
            this.shutdown = true;
        }

        @Override
        public void run() {
            UiObject2 button = mDevice.findObject(By.text(Pattern.compile("accept|ok", Pattern.CASE_INSENSITIVE)));
            long t = System.currentTimeMillis();
            while (System.currentTimeMillis()-t < 60*1000 && !this.shutdown) {
                if(button != null){
                    button.click();
                    Log.w(LOG_TAG, "Accept alert pressed.");
                }
                try {
                    sleep(1000);
                    mDevice.wakeUp();
                } catch (InterruptedException|RemoteException e) {
                    Log.e(LOG_TAG, e.toString());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Test
    public void install() throws Exception{
        boolean replace = Extras.getBoolean("replace");
        boolean downgrade = Extras.getBoolean("downgrade");
        int timeout = Extras.getInt("timeout", 600);
        String apk = Extras.getString("apk");

        AlertWatcher alertWatcher = new AlertWatcher();
        Thread thr = new Thread(alertWatcher);
        try{
            thr.setDaemon(true);
            thr.start();
            String out = shell.execute("pm install -t " + (replace ? " -r " : "") + (downgrade ? " -d ": "")
                                               + "/data/local/tmp/" + apk, timeout*1000);
            this.printResults(out);
        } catch (Exception e){
            throw new Exception(e);
        } finally{
            if (thr.isAlive()){
                alertWatcher.shutDown();
                thr.join();
            }
        }
    }
}
