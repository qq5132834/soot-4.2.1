//HelloWorld.java
public class HelloWorld {

    public static void main(String[] args) {
        int i = 1;
        while (i <= 9) {
            int j = 1;
            while (j <= i) {
                int sum = i * j;
                System.out.print(j + "*" + i + "=" + sum + "  ");
                j++;
            }
            i++;
            System.out.println();
        }
    }

}

