package kz.topsecurity.client.helper;

public class TextValidator {
    public static boolean checkIIN(String IIN){
        if(!checkIfOnlyNumbers(IIN)){
            return false;
        }
        String first_six_numbers = IIN.substring(0,6);
        String year = first_six_numbers.substring(0,2);
        String mounth = first_six_numbers.substring(2,4);
        String day = first_six_numbers.substring(4,6);
        Character sex = IIN.charAt(6);
        if (Integer.parseInt(mounth)>12){
            return false;
        }
        if(Integer.parseInt(day)>31){
            return false;
        }
        if(Character.getNumericValue(sex)>6)
            return false;
        return true;
    }

    private static final String regex = "[0-9]+";

    private static boolean checkIfOnlyNumbers(String text) {
        return text.matches(regex);
    }
}
