/**
 * Created by Administrator on 2017/5/15.
 */
public class testMain {
    public static void main(String[] args) {
        String originalFilename = "1.png";
        int i = originalFilename.lastIndexOf(".");
        System.out.println(i);
        String fileExtendName = originalFilename.substring(i);
        System.out.println(fileExtendName);
    }
}
