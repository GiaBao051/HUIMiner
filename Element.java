package HUIMiner;

// Định nghĩa cấu trúc UtilityList: lưu tid, iutil, rutil
class Element{
    int tid;       // Transaction ID
    int iutil;     // Internal utility
    int rutil;     // Remaining utility

    public Element(int tid, int iutil, int rutil){
        this.tid = tid;
        this.iutil = iutil;
        this.rutil = rutil;
    }
}


