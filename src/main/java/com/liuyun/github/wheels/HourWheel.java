package com.liuyun.github.wheels;

import com.liuyun.github.Slot;
import com.liuyun.github.Task;
import com.liuyun.github.utils.TimeUtils;
import com.liuyun.github.utils.WheelHolder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * 时轮
 * @author: lewis
 * @create: 2020/1/13 下午4:20
 */
public class HourWheel extends Thread {

    /** 天轮 */
    private DayWheel dayWheel = WheelHolder.getDayWheel();
    /** 分轮 */
    private MinuteWheel minuteWheel = WheelHolder.getMinuteWheel();

    private Slot[] hourSlot = new Slot[24];

    private int index = 0;

    @Override
    public void run() {
        while (true) {
            try {
                //休眠1分钟
                TimeUnit.HOURS.sleep(1);
            } catch (InterruptedException e) {
                //线程中断
            }
            Slot slot = hourSlot[index++];
            //若任务队列为空
            if(slot.getTaskList().size() <= 0) {
                index = index % hourSlot.length;
                continue;
            }
            //遍历所有任务
            Iterator<Task> iterator = slot.getTaskList().iterator();
            while (iterator.hasNext()) {
                if(handleTask(iterator.next()) == null) {
                    //任务已移动, 将其移出本列表
                    iterator.remove();
                }
            }
        }
    }

    public void addTask(Task task) {
        Integer hour = handleTask(task);
        if(hour != null) {
            int p = (index + hour) % hourSlot.length;
            hourSlot[p] = hourSlot[p] == null ? new Slot() : hourSlot[p];
            hourSlot[p].getTaskList().add(task);
        }
    }

    private Integer handleTask(Task task) {
        Integer hour = TimeUtils.toHour(task.getNextExecTime());
        //若下次执行时间小于1小时, 将任务放入分轮
        if(hour < 1) { minuteWheel.addTask(task); return null; }
        //若下次执行时间大于24小时, 将任务放入天轮
        if(hour > 24) { dayWheel.addTask(task); return null; }
        return hour;
    }

}
