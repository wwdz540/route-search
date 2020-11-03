package com.shangqiao56.tms.rms.route.util;

import java.util.Calendar;

public interface DayOfWeekTranslate {

    default int translateDaysString(String s){
        int onDday = 0;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            int dayOfWeek = (Integer.parseInt(c+"") % 7 ) + 1; //@todo 可能以后修改
            onDday |= (1 << dayOfWeek);

        }
        return  onDday;
    }

    default String translateToString(int onDays){
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<=7;i++){
            if(((onDays >> i ) & 0x01)>0){
                sb.append((i+5)%7+1);    //@todo 以后查找更好的公式
            }
        }
        return  sb.toString();
    }


    default boolean isTheDay(int dayOfWeek, int onDays){
        return (onDays & (0x01 << dayOfWeek))==1;
    }

    default int todayMask(){
        Calendar calendar = Calendar.getInstance();
        int shift  = calendar.get(Calendar.DAY_OF_WEEK);
        return 1 << shift;
    }

    static DayOfWeekTranslate getInstance(){
        return new DayOfWeekTranslate() {
        };
    }
}
