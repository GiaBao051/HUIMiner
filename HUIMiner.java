package HUIMiner;

import java.io.*;
import java.util.*;

public class HUIMiner {
    public long startTimestamp = 0;
    public long endTimestamp = 0;
    public int huiCount = 0;
    public int joinCount = 0;
    private BufferedWriter writer = null;
    private Map<Integer, Integer> mapItemToTWU = new HashMap<>();
    private final int BUFFERS_SIZE = 200;
    private int[] itemsetBuffer = null;

    class Pair {
        int item = 0;
        int utility = 0;
    }

    public HUIMiner() {
    }

    public void runAlgorithm(String input, String output, int minUtility) throws IOException {
        // Đặt lại giá trị bộ nhớ tối đa đã sử dụng
        MemoryLogger.getInstance().reset();
        // Khởi tạo bộ đệm để lưu tập mục hiện tại
        itemsetBuffer = new int[BUFFERS_SIZE];
        // Ghi lại thời điểm hiện tại
        startTimestamp = System.currentTimeMillis();
        writer = new BufferedWriter(new FileWriter(output));
        // Tạo map để lưu TWU của từng mục
        mapItemToTWU = new HashMap<Integer, Integer>();
        // Duyệt cơ sở dữ liệu lần đầu để tính TWU của từng mục
        BufferedReader myInput = null;
        String thisLine;
        try {
            // Đọc file
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            // Duyệt từng dòng
            while ((thisLine = myInput.readLine()) != null) {
                if (thisLine.isEmpty() || thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                // Tách giao dịch theo dấu ':'
                String split[] = thisLine.split(":");
                // Phần đầu là danh sách mục
                String items[] = split[0].split(" ");
                // Phần thứ hai là tổng utility của giao dịch
                int transactionUtility = Integer.parseInt(split[1]);
                // Tính TWU
                for (int i = 0; i < items.length; i++) {
                    Integer item = Integer.parseInt(items[i]);
                    Integer twu = mapItemToTWU.get(item);
                    twu = (twu == null) ? transactionUtility : twu + transactionUtility;
                    mapItemToTWU.put(item, twu);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                myInput.close();
            }
        }

        // Tạo danh sách chứa utility list của các mục có TWU >= minUtility
        List<UtilityList> listOfUtilityLists = new ArrayList<UtilityList>();
        // Tạo map chứa utility list ứng với từng mục
        Map<Integer, UtilityList> mapItemToUtilityList = new HashMap<Integer, UtilityList>();

        // Nếu TWU >= minUtility, thêm vào danh sách
        for (Integer item : mapItemToTWU.keySet()) {
            if (mapItemToTWU.get(item) >= minUtility) {
                UtilityList uList = new UtilityList(item);
                mapItemToUtilityList.put(item, uList);
                listOfUtilityLists.add(uList);
            }
        }
        // Sắp xếp danh sách theo TWU tăng dần
        Collections.sort(listOfUtilityLists, new Comparator<UtilityList>() {
            public int compare(UtilityList o1, UtilityList o2) {
                return compareItems(o1.item, o2.item);
            }
        });

        // Xây dựng UtilityList
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))));
            int tid = 0;
            while ((thisLine = myInput.readLine()) != null) {
                if (thisLine.isEmpty() || thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%' || thisLine.charAt(0) == '@') {
                    continue;
                }
                String split[] = thisLine.split(":");
                String items[] = split[0].split(" ");
                String utilityValues[] = split[2].split(" ");
                int remainingUtility = 0;
                List<Pair> revisedTransaction = new ArrayList<Pair>();
                for (int i = 0; i < items.length; i++) {
                    Pair pair = new Pair();
                    pair.item = Integer.parseInt(items[i]);
                    pair.utility = Integer.parseInt(utilityValues[i]);
                    if (mapItemToTWU.get(pair.item) >= minUtility) {
                        revisedTransaction.add(pair);
                        remainingUtility += pair.utility;
                    }
                }

                Collections.sort(revisedTransaction, new Comparator<Pair>() {
                    public int compare(Pair o1, Pair o2) {
                        return compareItems(o1.item, o2.item);
                    }
                });

                for (Pair pair : revisedTransaction) {
                    remainingUtility = remainingUtility - pair.utility;
                    UtilityList utilityListOfItem = mapItemToUtilityList.get(pair.item);
                    Element element = new Element(tid, pair.utility, remainingUtility);
                    utilityListOfItem.addElement(element);
                }
                tid++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (myInput != null) {
                myInput.close();
            }
        }

        // Kiểm tra bộ nhớ sử dụng
        MemoryLogger.getInstance().checkMemory();
        // Bắt đầu khai phá đệ quy
        huiMiner(itemsetBuffer, 0, null, listOfUtilityLists, minUtility);
        // Kiểm tra bộ nhớ sau khai phá và đóng file
        MemoryLogger.getInstance().checkMemory();
        writer.close();
        endTimestamp = System.currentTimeMillis();
    }

    private int compareItems(int item1, int item2) {
        int compare = mapItemToTWU.get(item1) - mapItemToTWU.get(item2);
        return (compare == 0) ? item1 - item2 : compare;
    }

    // Đệ quy tìm tất cả tập mục có lợi ích cao
    private void huiMiner(int[] prefix, int prefixLength, UtilityList pUL, List<UtilityList> ULs, int minUtility) throws IOException {
        for (int i = 0; i < ULs.size(); i++) {
            UtilityList X = ULs.get(i);
            if (X.sumIutils >= minUtility) {
                writeOut(prefix, prefixLength, X.item, X.sumIutils);
            }
            // Kiểm tra còn mở rộng được không?
            if (X.sumIutils + X.sumRutils >= minUtility) {
                List<UtilityList> exULs = new ArrayList<UtilityList>();
                for (int j = i + 1; j < ULs.size(); j++) {
                    UtilityList Y = ULs.get(j);
                    exULs.add(construct(pUL, X, Y));
                    joinCount++;
                }
                itemsetBuffer[prefixLength] = X.item;
                huiMiner(itemsetBuffer, prefixLength + 1, X, exULs, minUtility);
            }
        }
    }

    //Hàm tạo utility list cho tập mục mở rộng
    private UtilityList construct(UtilityList P, UtilityList px, UtilityList py) {
        UtilityList pxyUL = new UtilityList(py.item);
        for (Element ex : px.elements) {
            Element ey = findElementWithTID(py, ex.tid);
            if (ey == null)
                continue;
            if (P == null) {
                Element eXY = new Element(ex.tid, ex.iutils + ey.iutils, ey.rutils);
                pxyUL.addElement(eXY);
            } else {
                Element e = findElementWithTID(P, ex.tid);
                if (e != null) {
                    Element eXY = new Element(ex.tid, ex.iutils + ey.iutils - e.iutils, ey.rutils);
                    pxyUL.addElement(eXY);
                }
            }
        }
        return pxyUL;
    }

    //Tìm phần tử trong utility list theo tid bằng tìm kiếm nhị phân
    private Element findElementWithTID(UtilityList ulist, int tid) {
        List<Element> list = ulist.elements;
        int first = 0;
        int last = list.size() - 1;
        while (first <= last) {
            int middle = (first + last) >>> 1;
            if (list.get(middle).tid < tid) {
                first = middle + 1;
            } else if (list.get(middle).tid > tid) {
                last = middle - 1;
            } else {
                return list.get(middle);
            }
        }
        return null;
    }

    //Ghi tập mục có lợi ích cao ra file kết quả
    private void writeOut(int[] prefix, int prefixLength, int item, long utility) throws IOException {
        huiCount++;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < prefixLength; i++) {
            buffer.append(prefix[i]);
            buffer.append(' ');
        }
        buffer.append(item);
        buffer.append(" #UTIL: ");
        buffer.append(utility);
        writer.write(buffer.toString());
        writer.newLine();
    }

    //In thống kê thực thi thuật toán ra màn hình
    public void printStats() {
        System.out.println("=============  HUI-MINER ALGORITHM - STATS =============");
        System.out.println(" Total time ~ " + (endTimestamp - startTimestamp) + " ms");
        System.out.println(" Memory ~ " + MemoryLogger.getInstance().getMaxMemory() + " MB");
        System.out.println(" High-utility itemsets count: " + huiCount);
        System.out.println(" Join count: " + joinCount);
        System.out.println("===================================================");
    }
}
