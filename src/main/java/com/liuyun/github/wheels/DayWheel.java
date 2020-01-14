package com.liuyun.github.wheels;

import com.liuyun.github.Slot;
import com.liuyun.github.Task;
import com.liuyun.github.utils.TimeUtils;
import com.liuyun.github.utils.WheelHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 天轮
 * @author: lewis
 * @create: 2020/1/13 下午4:21
 */
public class DayWheel extends Thread {

    /** 时轮 */
    private HourWheel hourWheel = WheelHolder.getHourWheel();

    private Slot[] daySlot = new Slot[365];

    private Integer index;

    @Override
    public void run() {
        while (true) {
            try {
                //休眠1天
                TimeUnit.DAYS.sleep(1);
            } catch (InterruptedException e) {
                //线程中断
            }
            Slot slot = daySlot[index++];
            //若任务队列为空
            if(slot.getTaskList().size() <= 0) {
                index = index % daySlot.length;
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
        Integer day = handleTask(task);
        if(day != null) {
            int p = (index + day) % daySlot.length;
            daySlot[p] = daySlot[p] == null ? new Slot() : daySlot[p];
            daySlot[p].getTaskList().add(task);
        }
    }

    private Integer handleTask(Task task) {
        Integer day = TimeUtils.toDay(task.getNextExecTime());
        //若下次执行时间小于1天, 将任务放入时轮
        if(day < 1) { hourWheel.addTask(task); return null; }
        return day;
    }

}
