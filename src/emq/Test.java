package emq;

public class Test {
    public static void main(String args[]) {
        DimenConverter dimenConverter = new DimenConverter(1.15f);
        System.out.println(dimenConverter.convertValue("100dp"));
        System.out.println(dimenConverter.convertValue("-20dp"));
    }
}
