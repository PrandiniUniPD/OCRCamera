package unipd.se18.barcodemodule;

public enum ErrorCode {
    BITMAP_NOT_FOUND(100, "Bitmap not found"),
    BARCODE_NOT_FOUND(101, "Barcode not found"),
    DECODING_ERROR(102, "Barcode decoding process failed");

    private int code;
    private String info;

    ErrorCode(int code, String info){
        this.code = code;
        this.info = info;
    }

    public int getCode(){
        return code;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "("+getCode()+")"+" "+getInfo();
    }
}


