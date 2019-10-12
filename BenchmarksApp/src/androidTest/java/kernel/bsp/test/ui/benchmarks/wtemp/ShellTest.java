package kernel.bsp.test.ui.benchmarks.wtemp;

import android.os.ParcelFileDescriptor;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 11/2/2016.
 */

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 21)
public class ShellTest {

    private String shell(String command) throws IOException {
        StringBuilder output = new StringBuilder();

        ParcelFileDescriptor descriptor = InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command);

        final ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(descriptor);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // read lines
        String line;
        while ((line = reader.readLine())!= null) {
            output.append(line.concat("\n"));
        }
        return output.toString();
    }

    @Test
    public void test1(){
        try {
            Log.w("BSP.TEST.UI.BENCHMARKS", String.format("Test1: %s", this.shell("ls /data/local/tmp/")));
        }catch (IOException e) {
            Log.w("BSP.TEST.UI.BENCHMARKS", e.toString());
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        try {
            Log.w("BSP.TEST.UI.BENCHMARKS", String.format("Test2: %s", this.shell("ls /asdfjshadf")));
        } catch (IOException e) {
            Log.w("BSP.TEST.UI.BENCHMARKS", e.toString());
            e.printStackTrace();
        }
    }
}
