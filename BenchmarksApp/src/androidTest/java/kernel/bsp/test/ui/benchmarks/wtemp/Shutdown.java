package kernel.bsp.test.ui.benchmarks.wtemp;

/**
 * Created by "VirtualV <https://github.com/virtualvfix>" on 22.07.16.
 */

//@RunWith(AndroidJUnit4.class)
//public class Shutdown extends Output {
//
////    @Rule
////    public ActivityTestRule<kernel.bsp.test.ui.benchmarks.MainActivity> activityRule = new ActivityTestRule<>(
////      kernel.bsp.test.ui.benchmarks.MainActivity.class,
////      true,     // initialTouchMode
////      false);   // launchActivity. False so we can customize the intent per test method
//
//    @Test
////    public void shutdown() throws Exception {
////
////        Context context = InstrumentationRegistry.getInstrumentation().getContext();
////        Intent intent = context.getPackageManager().getLaunchIntentForPackage("kernel.bsp.test.ui.benchmarks");
////
////        if (intent==null)
////            throw new Exception("Application cannot be launched !");
////
////        intent = intent.addCategory(Intent.CATEGORY_HOME)
////                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
////                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        context.startActivity(intent);
//
////        Context context = InstrumentationRegistry.getInstrumentation().getContext();
////        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN"); //context.getPackageManager().getLaunchIntentForPackage(BASIC_PACKAGE);
////        intent.putExtra("android.intent.extra.KEY_CONFIRM", true);
////        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        context.startActivity(intent);
////
////        if (intent==null)
////            throw new Exception("Application cannot be launched !");
//
////        intent = intent.addCategory(Intent.CATEGORY_HOME)
////                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
////                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        context.startActivity(intent);
//
////        Intent int = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
////        i.putExtra("android.intent.extra.KEY_CONFIRM", true);
////        startActivity(i);
//    }
//}