package com.liuyun.github;

import com.liuyun.github.utils.TimeUtils;
import com.liuyun.github.utils.WheelHolder;
import com.liuyun.github.wheels.DayWheel;
import com.liuyun.github.wheels.HourWheel;
import com.liuyun.github.wheels.MinuteWheel;
import com.liuyun.github.wheels.SecondWheel;

import java.util.concurrent.TimeUnit;

/**
 * @author: lewis
 * @create: 2020/1/13 下午5:44
 */
public class TimeWheel {

    private DayWheel dayWheel = WheelHolder.getDayWheel();
    private HourWheel hourWheel = WheelHolder.getHourWheel();
    private MinuteWheel minuteWheel = WheelHolder.getMinuteWheel();
    private SecondWheel secondWheel = WheelHolder.getSecondWheel();

    private static boolean hasInit = false;

    private void init() {
        if(!hasInit) { return; }
        dayWheel.start();
        hourWheel.start();
        minuteWheel.start();
        secondWheel.start();
        hasInit = true;
    }

    public <T extends Runnable> void addTask(String cron, T t) {
        addTask(new Task(cron, t));
    }

    public <T extends Runnable> void addTask(Long nextExecTime, T t) {
        addTask(new Task(nextExecTime, t));
    }

    private void addTask(Task task) {
        //首先初始化线程
        init();
        long nextExecTime = task.getNextExecTime();
        if(TimeUtils.toDay(nextExecTime) > 0) {
            dayWheel.addTask(task);
            return;
        }
        if(TimeUtils.toHour(nextExecTime) > 0) {
            hourWheel.addTask(task);
            return;
        }
        if(TimeUtils.toMinute(nextExecTime) > 0) {
            minuteWheel.addTask(task);
            return;
        }
        if(TimeUtils.toSecond(nextExecTime) > 0) {
            secondWheel.addTask(task);
            return;
        }
    }

}
