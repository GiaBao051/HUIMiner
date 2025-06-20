package HUIMiner;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<ArrayList<Element>> ULs = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();

        // ---------- Item: a ----------
        ArrayList<Element> aUL = new ArrayList<>();
        aUL.add(new Element(2, 4, 5));
        aUL.add(new Element(3, 4, 5));
        aUL.add(new Element(5, 5, 5));
        aUL.add(new Element(6, 3, 0));
        ULs.add(aUL);
        itemNames.add("a");

        // ---------- Item: b ----------
        ArrayList<Element> bUL = new ArrayList<>();
        bUL.add(new Element(1, 2, 5));
        bUL.add(new Element(2, 9, 2));
        bUL.add(new Element(5, 4, 10));
        bUL.add(new Element(6, 8, 3));
        ULs.add(bUL);
        itemNames.add("b");

        // ---------- Item: c ----------
        ArrayList<Element> cUL = new ArrayList<>();
        cUL.add(new Element(1, 2, 7));
        cUL.add(new Element(2, 3, 11));
        cUL.add(new Element(3, 2, 9));
        cUL.add(new Element(4, 2, 0));
        cUL.add(new Element(6, 1, 11));
        ULs.add(cUL);
        itemNames.add("c");

        // ---------- Item: d ----------
        ArrayList<Element> dUL = new ArrayList<>();
        dUL.add(new Element(1, 5, 0));
        dUL.add(new Element(2, 5, 0));
        dUL.add(new Element(3, 5, 0));
        dUL.add(new Element(5, 5, 0));
        dUL.add(new Element(7, 5, 0));
        ULs.add(dUL);
        itemNames.add("d");

        // ---------- Item: e ----------
        ArrayList<Element> eUL = new ArrayList<>();
        eUL.add(new Element(2, 4, 14));
        eUL.add(new Element(4, 4, 2));
        eUL.add(new Element(5, 8, 14));
        ULs.add(eUL);
        itemNames.add("e");

        System.out.print("Input minutil: ");
        int minUtil = sc.nextInt();
        ArrayList<String> prefix = new ArrayList<>();

        HUIMiner.huiMiner(new ArrayList<>(), ULs, itemNames, minUtil, prefix);
    }
}
