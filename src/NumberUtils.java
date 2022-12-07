public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils{
    //문자열을 split 해서  숫자 배열로 변환한다.
    public static Integer[] split(String str, String seperator){
        String[] stringArray = StringUtils.split(str, separator);
        Integer[] intArray = new Integer[stringArray.length];
        for(int i=0; i<stringArray.length; i++){
            String numberAsString = stringArray[i];
            intArray[i] = Integer.parseInt(numberAsString);
        }
        return intArray;
    }

    //문자열을 split 해서  숫자 배열로 변환한다.
    public static Integer[] splitByWholeSeparator(String str, String seperator){
        String[] stringArray = StringUtils.splitByWholeSeparator(str, separator);
        Integer[] intArray = new Integer[stringArray.length];
        for(int i=0; i<stringArray.length; i++){
            String numberAsString = stringArray[i];
            intArray[i] = Integer.parseInt(numberAsString);
        }
        return intArray;
    }


}