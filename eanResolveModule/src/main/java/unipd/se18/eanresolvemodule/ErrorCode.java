package unipd.se18.eanresolvemodule;

public enum ErrorCode {

    HTTP_ERROR(100, "Error reaching the server"),
    NO_PRODUCT_ERROR(101, "No product found"),
    JSON_ERROR(102, "Error decoding the response from the server"),
    INVALID_BARCODE(103, "Invalid barcode value");

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
