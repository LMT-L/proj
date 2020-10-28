package com.proj.tookit.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReUtil {
    /**
     * 手机号正则(固话(含区号)、手机号)
     */
//    public static final String phoneValid = "^(1[3-9]\\d{9}|\\d{1,4}-\\d{7,8}|\\d{7})$";
    public static final String phoneValid = "^(((\\+\\d{2}-)?0\\d{2,3}-\\d{7,8})|((\\+\\d{2}-)?(\\d{2,3}-)?([1][3,4,5,7,8][0-9]\\d{8})))$";

    /**
     * 税号正则
     */
    public static final String einValid = "^[A-Za-z0-9]{1,30}$";
    /**
     * 银行账号正则
     */
    public static final String accountNoValid = "^[A-Za-z0-9]{5,30}$";
    /**
     * 人员姓名正则
     */
    public static final String personNameValid = "^[\\u4E00-\\u9FA5·0-9A-z]{1,20}$";
    /**
     * 合同税率正则
     */
    public static final String tax_rate = "^(100|[1-9]?\\d(\\.\\d\\d?\\d?)?)%$|0$";


    /*
    日期验证
     */
    public static final String DATE_VALID = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))$";

    /**
     * 定义异常处理
      */
    private static final Logger logger = LoggerFactory.getLogger(ReUtil.class);
    /**
     * 验证数字正则
     *
     * @param number
     * @return
     */
    public static boolean validNumber(String number) {
        String numPattern = "^[0-9]{1,9}([.][0-9]{1,2})?$";
        if ((number != null)) {
            Pattern pattern = Pattern.compile(numPattern);
            Matcher match = pattern.matcher(number);
                return match.matches();
        }
        return false;
    }

    /**
     * 验证时间数字正则
     *
     * @param number
     * @return
     */
    public static boolean validDateNumber(String number) {
        String numPattern = "^[0-9]{1,15}([.][0-9]{1,2})?$";
        if ((number != null)) {
            Pattern pattern = Pattern.compile(numPattern);
            Matcher match = pattern.matcher(number);
            return match.matches();
        }
        return false;
    }
    /**
     * 判断字符串是否为正整数
     * @param str
     * @return
     */
    public static boolean validIsPosNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");

        return pattern.matcher(str).matches();
    }
    /**
     * 验证整数正则
     *
     * @param number
     * @return
     */
    public static boolean validInteger(String number) {
        String numPattern = "^[0-9]*$";
        if ((number != null)) {
            Pattern pattern = Pattern.compile(numPattern);
            Matcher match = pattern.matcher(number);
            return match.matches();
        }
        return false;
    }

    /**
     * 数字字母正则
     *
     * @param param
     * @return
     */
    public static boolean numLetterRegex(String param) {
        String numPattern = "^[A-Za-z0-9]+$";
        if ((param != null)) {
            Pattern pattern = Pattern.compile(numPattern);
            Matcher match = pattern.matcher(param);
            return match.matches();
        }
        return false;
    }

    /**
     * 银行卡号正则
     *
     * @param param
     * @return
     */
    public static boolean bankNoRegex(String param) {
//        String numPattern = "^([1-9]{1})(\\d{15}|\\d{18})$";
        if ((param != null)) {
            Pattern pattern = Pattern.compile(phoneValid);
            Matcher match = pattern.matcher(param);
            return match.matches();
        }
        return false;
    }

    /**
     * 百分数校验正则
     *
     * @param percent
     * @return
     */
    public static boolean validPercent(String percent) {
        String percentPattern = "^(100|[1-9]?\\d(\\.\\d\\d?\\d?)?)%$|0$";
        if ((percent != null)) {
            Pattern pattern = Pattern.compile(percentPattern);
            Matcher match = pattern.matcher(percent);
            return match.matches();
        }
        return false;
    }

    /**
     * 百分数校验正则
     *
     * @param percent
     * @return
     */
    public static boolean validPercentNumber(String percent) {
        String percentPattern = "^((\\d|[123456789]\\d)(\\.\\d{1,2})?|100)$";
        if ((percent != null)) {
            Pattern pattern = Pattern.compile(percentPattern);
            Matcher match = pattern.matcher(percent);
            return match.matches();
        }
        return false;
    }


    /**
     * 身份证正则校验
     * @param IDNumber
     * @return
     */
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|accept))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|accept))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾


        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {

            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    return idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase());

                } catch (Exception e) {
                    logger.error(e.getMessage());
                    return false;
                }
            }

        }
        return matches;
    }

    /**
     * 邮箱校验
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        try {
            // 正常邮箱
            // /^\w+((-\w)|(\.\w))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/

            // 含有特殊 字符的 个人邮箱 和 正常邮箱
            // js: 个人邮箱
            // /^[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+@[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+(\.[\-!#\$%&'\*\+\\\.\/0-9=\?A-Z\^_`a-z{|}~]+)+$/

            // java：个人邮箱
            // [\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+@[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+\\.[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+

            // 范围 更广的 邮箱验证 “/^[^@]+@.+\\..+$/”
            final String pattern1 = "[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+@[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+\\.[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+";

            final Pattern pattern = Pattern.compile(pattern1);
            final Matcher mat = pattern.matcher(email);
            return mat.matches();
        } catch (Exception e) {
           logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * CJYFIXME搜集号段时间:2017-11-28(这个之后的请自行添加) 手机号:目前全国有27种手机号段。
     * 移动有19个号段：134（0-8）、135、136、137、138、139、147(147（数据卡）)、148(物联网)、150、151、152、
     * 157、158、159、178、182、183、184、187、188、198。
     * 联通有11种号段：130、131、132、--145(数据卡)--、146(物联网)、155、156、166、171、175、176、185、
     * 186。 电信有7个号段：133、--1349--、149、153、173、177、180、181、189、199。 虚拟运营商:
     * (1).移动:1703、1705、1706 (2).联通:1704、1707、1708、1709、171
     * (3).电信:1700、1701、1702 卫星通信:1349
     * <p>
     * 工业和信息化部公示了2017年第10批“电信网码号资源使用证书”颁发结果，批准同意部分单位提出的电信网码号资源有关申请，
     * 其中三大运营商均获得相关物联网号段。 移动: (1).198(0-9)号段(公众移动通信网号) (2).148(0-9)号段(物联网业务专用号段)
     * (3).1440(0-9)号段(物联网网号) (4).(460)13(移动网络识别码) 联通: (1).166(0-9)号段(公众移动通信网号)
     * (2).146(0-9)号段(物联网业务专用号段) 电信: (1).1740(0-5)号段(卫星移动通信业务号)、
     * (2).199(0-9)号段(公众移动通信网号)、 (3).1410(0-9)号段(物联网网号)、 (4).(460)59(移动网络识别码)
     * 由于物联网号段一般用在家用家具上，所以这里不考虑物联网号段,物联网号码的总位数是13或者14还没搞清楚
     * =========================================================================
     * ======================
     * 总结一下:虚拟运营商、数据卡、物联网、卫星通信、移动网络识别码都不作为正常使用的电话号码,所以需要验证的手机号如下:
     * 130、131、132、133、134(0-8)、135、136、137、138、139 149
     * 150、151、152、153、155、156、157、158、159 166、 173、175、176、177、178、
     * 180、181、182、183、184、185、186、187、188、189 198、199
     *
     * 2019-9-4 新增加191、193、147、172等四个号段
     */
    private static final String REGEX_MOBILE = "(^1\\d{10}$)";

    /**
     * 判断是否是手机号
     *
     * @param tel
     *            手机号
     * @return boolean true:是 false:否
     */
    public static boolean isMobile(String tel) {
        return Pattern.matches(REGEX_MOBILE, tel);
    }

    /**
     * 校验金额
     * @param amount 金额
     * @param min 最小值
     * @param max 最大值
     * @param place 最多保留place位小数
     */
    public static String validateAmount(Double amount, int min, int max, int place){
        // 判断非空
        if(amount == null){
            return null;
        }
        // 判断金额，如若不为空，应要求属于(min,max)
        if(amount <= min || amount >= max){
            return "请输入10位以内的数字，最多两位小数，不可为0";
        }
        // 校验小数部分(需转化为字符串类型)
        String str = amount.toString();
        if(!str.contains(".")){
            return null;
        }
        if(str.split("\\.")[1].length() > place){
            return "小数点后最多保留" + place + "位";
        }
        return null;
    }


}
