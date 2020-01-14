package com.liuyun.github.wheels;

import com.liuyun.github.Slot;
import com.liuyun.github.Task;
import com.liuyun.github.utils.TimeUtils;
import com.liuyun.github.utils.WheelHolder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * 分轮
 * @author: lewis
 * @create: 2020/1/13 下午4:20
 */
public class MinuteWheel extends Thread {

    /** 时轮 */
    private HourWheel hourWheel = WheelHolder.getHourWheel();
    /** 秒轮 */
    private SecondWheel secondWheel = WheelHolder.getSecondWheel();

    private Slot[] minuteSlot = new Slot[60];

    private Integer index;

    @Override
    public void run() {
        while (true) {
            try {
                //休眠1分钟
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                //线程中断
            }
            Slot slot = minuteSlot[index++];
            //若任务队列为空
            if(slot.getTaskList().size() <= 0) {
                index = index % minuteSlot.length;
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
        Integer minute = handleTask(task);
        if(minute != null) {
            int p = (index + minute) % minuteSlot.length;
            minuteSlot[p] = minuteSlot[p] == null ? new Slot() : minuteSlot[p];
            minuteSlot[p].getTaskList().add(task);
        }
    }

    private Integer handleTask(Task task) {
        Integer minute = TimeUtils.toMinute(task.getNextExecTime());
        //若下次执行时间小于1分钟, 将任务放入秒轮
        if(minute < 1) { secondWheel.addTask(task); return null; }
        //若下次执行时间大于60分钟, 将任务放入时轮
        if(minute > 60) { hourWheel.addTask(task); return null; }
        return minute;
    }

}
