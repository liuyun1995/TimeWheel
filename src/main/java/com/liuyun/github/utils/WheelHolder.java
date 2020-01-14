package com.liuyun.github.utils;

import com.liuyun.github.wheels.DayWheel;
import com.liuyun.github.wheels.HourWheel;
import com.liuyun.github.wheels.MinuteWheel;
import com.liuyun.github.wheels.SecondWheel;

/**
 * @author: lewis
 * @create: 2020/1/14 下午2:08
 */
public class WheelHolder {

    private static DayWheel dayWheel = new DayWheel();

    private static HourWheel hourWheel = new HourWheel();

    private static MinuteWheel minuteWheel = new MinuteWheel();

    private static SecondWheel secondWheel = new SecondWheel();

    public static DayWheel getDayWheel() {
        return dayWheel;
    }

    public static HourWheel getHourWheel() {
        return hourWheel;
    }

    public static MinuteWheel getMinuteWheel() {
        return minuteWheel;
    }

    public static SecondWheel getSecondWheel() {
        return secondWheel;
    }

}
