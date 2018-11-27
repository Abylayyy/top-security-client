package kz.topsecurity.client.helper;

public class PhoneHelper {
    public static String getFormattedPhone(String phone){
        if(phone!=null && phone.length()==11 && !checkForSymbols(phone)) {
            return phone.replaceFirst("(\\d{1})(\\d{3})(\\d{3})(\\d{2})(\\d{2})", "+$1($2)$3-$4-$5");
        }
        return phone;
    }

    private static final String[] SymbolList =  {"+","(",")","-"};
    private static boolean checkForSymbols(String phone) {
        for (String aSymbolList : SymbolList) {
            if (phone.contains(aSymbolList))
                return true;
        }
        return false;
    }
}
