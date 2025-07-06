package HUIMiner;

import java.util.*;
public class Main {
    public static void main(String[] args) {
        HUIMiner algo = new HUIMiner();
        Scanner sc = new Scanner(System.in);
        String input = "E:\\NCKH\\3.Code\\HUIMiner\\input.txt";
        String output = "E:\\NCKH\\3.Code\\HUIMiner\\output.txt";
        System.out.print("Input minUtil: ");
        int minUtility = sc.nextInt();
        try {
            algo.runAlgorithm(input, output, minUtility);
            algo.printStats();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
