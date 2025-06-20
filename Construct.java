package HUIMiner;

import java.util.*;

class Construct{
    public static ArrayList<Element> construct(ArrayList<Element> pUL, ArrayList<Element> pxUL, ArrayList<Element> pyUL) {
        ArrayList<Element> pxyUL = new ArrayList<>();
        for(Element ex : pxUL){
            for(Element ey : pyUL){
                if(ex.tid == ey.tid){
                    Element exy;
                    if(!pUL.isEmpty()){   //Nếu pUL không rỗng
                        Element e = null;
                        for(Element pe : pUL){    //Tìm phần tử có tid trùng
                            if(pe.tid == ex.tid){
                                e = pe;
                                break;
                            }
                        }
                        if(e != null){    //Nếu tìm thấy
                            exy = new Element(ex.tid, ex.iutil + ey.iutil - e.iutil, ey.rutil);
                        }
                        else{
                            continue;       //Không tìm thấy thì bỏ qua
                        }
                    }
                    else{
                        exy = new Element(ex.tid, ex.iutil + ey.iutil, ey.rutil);
                    }
                    pxyUL.add(exy);
                    break; 
                }
            }
        }
        return pxyUL;
    }
}


