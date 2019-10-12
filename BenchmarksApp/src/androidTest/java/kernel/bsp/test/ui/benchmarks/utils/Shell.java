package kernel.bsp.test.ui.benchmarks.utils;

import android.os.ParcelFileDescriptor;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by "VirtualV <https://github.com/virtualvfix>"
 */

public class Shell {

    public String pureExecute(String command) throws IOException, InterruptedException{
        return this.pureExecute(command, 0);
    }

    public String pureExecute(String command, int timeout) throws IOException, InterruptedException{
        StringBuilder output = new StringBuilder();

        ParcelFileDescriptor descriptor;
        if (!command.equals(""))
            descriptor = InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(command);
        else throw new InterruptedException("Shell command not found !");

        final ParcelFileDescriptor.AutoCloseInputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(descriptor);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // timeout timer
        final int[] exitcode = {0};
        Timer timer = new Timer();
        if (timeout > 0){
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        exitcode[0] = 1;
                        inputStream.close();
                    } catch (IOException e) {
                        Log.d(Output.LOG_TAG, String.format("Close stream error: %s", e));
                        e.printStackTrace();
                    }
                }
            }, timeout);
        }

        String line;
        while (exitcode[0] == 0 && (line = reader.readLine())!= null) {
            output.append(line.concat("\n"));
        }

        timer.cancel();
        if (exitcode[0] > 0) throw new InterruptedException("Timeout expired !");

        return output.toString();
    }

    public String execute(String command, int timeout) throws Exception{
        String out = this.pureExecute(command, timeout);
        Pattern r = Pattern.compile(".*?(error|exception)+.*?", Pattern.CASE_INSENSITIVE);
        Matcher m = r.matcher(out);
        if (m.find()) throw new Exception(String.format("Error during command \"%s\" execution: %s", command, out));
        return out;
    }

    public String execute(String command) throws Exception{
        return execute(command, 0);
    }


    public String startApp(String activity) throws Exception{
        return this.execute("am start -W " + activity);
    }

    public String stopApp(String _package) throws Exception{
        return this.execute("am force-stop " + _package);
    }
}
