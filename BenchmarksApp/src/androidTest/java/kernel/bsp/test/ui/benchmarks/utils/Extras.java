package kernel.bsp.test.ui.benchmarks.utils;

import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 10/10/2016.
 */

public class Extras {
    /* get command line arguments */

    // convert unicode in string if available
    private static String convertUncode(String source){
        if (source.contains("\\u"))
            return source;

        String result = "";
        int index = source.indexOf("\\u");
        while (index != -1){
            if (index != 0) {
                result += source.substring(0, index);
            }
            String unicode = source.substring(index+2, index+6);
            result += (char)Integer.parseInt(unicode, 16);
            source = source.substring(index+6);
            index = source.indexOf("\\u");
        }
        return result+source;
    }

    private static String getExtras(String name){
        Bundle extras = InstrumentationRegistry.getArguments();
        if (extras.containsKey(name)) {
            String line = null;
            try {
                line = convertUncode(extras.getString(name).trim()).replaceAll("\\\\", "");
            } catch (Exception er) {
                Log.d(Output.LOG_TAG, er.toString());
            }
            return line;
        }
        return null;
    }

    public static String getString(String name) {
        return Extras.getString(name, null);
    }

    public static int getInt(String name) {
        return Extras.getInt(name, -1);
    }

    public static float getFloat(String name){
        return Extras.getFloat(name, -1f);
    }

    public static boolean getBoolean(String name){
        return Extras.getBoolean(name, false);
    }

    public static String getString(String name, String def){
        /* get String argument */
        String extras = Extras.getExtras(name);

        if (extras != null)
            return extras;
        return def;
    }

    public static int getInt(String name, int def){
        /* get int argument */
        String extras = Extras.getExtras(name);
        if (extras != null)
            return Integer.parseInt(extras);
        return def;
    }

    public static float getFloat(String name, float def){
        /* get float argument */
        String extras = Extras.getExtras(name);
        if (extras != null)
            return Float.parseFloat(extras);
        return def;
    }

    public static boolean getBoolean(String name, boolean def){
        /* get float argument */
        String extras = Extras.getExtras(name);
        if (extras != null)
            return Boolean.parseBoolean(extras);
        return def;
    }

    public static Float[] getFloatArray(String name) throws ParseException {
        return Extras.getArray(name, new Float[]{});
    }

    public static Integer[] getIntArray(String name) throws ParseException {
        return Extras.getArray(name, new Integer[]{});
    }

    public static String[] getStringArray(String name) throws ParseException {
        return Extras.getArray(name, new String[]{});
    }

    private static <T> T[] getArray(String name, T[] def) throws ParseException {
        /* Get array from arguments */
        String array = Extras.getExtras(name);
        if (array != null) {
            String[] str = array.replace("[", "").replace("]", "").split(",");
            if (def instanceof String[]){ // string array
                // clear string array
                List<String> newstr = new ArrayList<>();
                for (int i=0; i<str.length; i++){
                    // get string with convert unicode symbols
                    String line = str[i].trim();
//                    String line = StringEscapeUtils.unescapeJava(str[i].trim());
                    if (!line.equals("")){
                        if (line.toLowerCase().equals("none") || line.toLowerCase().equals("null")){
                            newstr.add(null);
                        }else{
                            newstr.add(line);
                        }
                    }
                }
                return newstr.toArray(def);
            }else if (def instanceof Integer[]) { // int array
                Integer[] res = new Integer[str.length];
                for (int i = 0; i < str.length; i++)
                    res[i] = Integer.parseInt(str[i]);
                return (T[]) res;
            }else if (def instanceof Float[]) { // float array
                Float[] res = new Float[str.length];
                for (int i = 0; i < str.length; i++)
                    res[i] = Float.parseFloat(str[i]);
                return (T[]) res;
            }
        }
        return def;
    }
}