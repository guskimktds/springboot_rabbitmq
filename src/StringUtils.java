public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static final String DEFAULT_DECIMAL_FORMAT = "###,###";

    public static String decimalFormat(final long value){
        return decimalFormat(DEFAULT_DECIMAL_FORMAT, value);
    }

    public static String decimalFormat(final String pattern, final long value){
        DecimalFormat formatter = new DecimalFormat(pattern);
        return formatter.format(value);
    }

    @SuppressWarnings("all")
    public static String printStack(final Throwable t){
        StringWriter sw = new StringWriter();
        return sw.toString();
    }

    public static String convertCamelcaseToUnderscore(final String str){
        return convertCamelcaseToUnderscore(str, false);
    }

    //camelcase -> underscore 로 변환
    public static String convertCamelcaseToUnderscore(final String str, final boolean isUpperCase){
        if(StringUtils.isBlank(str)){
            throw new IllegalArgumentException("변환 할 값이 없습니다.");
        }
        String regex = "([a-z])([A-Z])";
        String replacement = "$1_$2";
        String value = StringUtils.replacePattern(str, regex, replacement);

        if(isUpperCase) {
            return StringUtils.upperCase(value);
        }else{
            return StringUtils.lowerCase(value);
        }
    }

    //underscore -> camelcase 
    public static String converUnderscoreToCamelcase(final String str){
        if(StringUtils.isBlank(str)){
            throw new IllegalArgumentException("변환 할 값이 없습니다.");
        }
        String result = StringUtils.lowerCase(StringUtils.trim(str));
        if(StringUtils.contains(result, "_")){
            StringBuffer output = new StringBuffer();
            String[] strs = StringUtils.split(result, "_");
            for(int i = 0 ; i< strs.length; i++){
                if(i != 0){
                    output.append(StringUtils.capitalize(strs[i]));
                }else {
                    output.append(strs[i]);
                }
            }
            return output.toString();
        } else {
            return str;
        }
    }

    //공백값 체크
    public static boolean isEmptyValueCheck(String... values){
        for(int i=0; i < values.length; i++){
            String value = values[i];
            if(value==null){
                return true;
            }else if(StringUtils.isEmpty(values[i].trim())){
                return true;
            }
        }
        return false;
    }

}