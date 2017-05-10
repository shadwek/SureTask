package sa.com.sure.task;
/**
 * Created by HussainHajjar on 5/8/2017.
 */

public class Global {
    // TODO add all log TAGs here
    public static String getMethodName(){
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        System.out.println(methodName);
        return methodName;
    }
}
