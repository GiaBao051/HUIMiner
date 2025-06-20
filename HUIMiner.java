package HUIMiner;

import java.util.*;

class HUIMiner {
    public static void huiMiner(
        ArrayList<Element> pUL,
        ArrayList<ArrayList<Element>> ULs,
        ArrayList<String> itemNames,
        int minUtil,
        ArrayList<String> currentPrefix
    ){
        for (int i = 0; i < ULs.size(); i++) {
            ArrayList<Element> xUL = ULs.get(i);
            String itemName = itemNames.get(i);
            int sumIutil = 0;
            int sumRutil = 0;
            for(Element e : xUL){
                sumIutil += e.iutil;
                sumRutil += e.rutil;
            }
            //Nếu là tập mục có lợi ích cao thì in ra
            if(sumIutil >= minUtil){
                ArrayList<String> pattern = new ArrayList<>(currentPrefix);
                pattern.add(itemName);
                System.out.println("HUI: " + String.join("", pattern) + " = " + sumIutil);
            }

            //Nếu còn khả năng mở rộng thì tiếp tục đệ quy
            if(sumIutil + sumRutil >= minUtil){
                ArrayList<ArrayList<Element>> exULs = new ArrayList<>();
                ArrayList<String> exNames = new ArrayList<>();

                for(int j = i + 1; j < ULs.size(); j++){
                    ArrayList<Element> yUL = ULs.get(j);
                    ArrayList<Element> pxyUL = Construct.construct(pUL, xUL, yUL);
                    if(!pxyUL.isEmpty()){
                        exULs.add(pxyUL);
                        exNames.add(itemNames.get(j));
                    }
                }
                //Mở rộng tiền tố với item hiện tại
                ArrayList<String> newPrefix = new ArrayList<>(currentPrefix);
                newPrefix.add(itemName);

                //Đệ quy tiếp tục tìm các tập con có lợi ích cao hơn
                huiMiner(xUL, exULs, exNames, minUtil, newPrefix);
            }
        }
    }
}
