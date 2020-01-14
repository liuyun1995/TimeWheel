package com.liuyun.github.wheels;

import com.liuyun.github.Slot;
import com.liuyun.github.Task;
import com.liuyun.github.utils.TimeUtils;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * 秒轮
 * @author: lewis
 * @create: 2020/1/13 下午4:19
 */
public class SecondWheel extends Thread {

    /** 分轮 */
    private MinuteWheel minuteWheel = new MinuteWheel();

    private Slot[] secondSlot = new Slot[60];

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
            Slot slot = secondSlot[index++];
            //若任务队列为空
            if(slot.getTaskList().size() <= 0) {
                index = index % secondSlot.length;
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
            int p = (index + minute) % secondSlot.length;
            secondSlot[p] = secondSlot[p] == null ? new Slot() : secondSlot[p];
            secondSlot[p].getTaskList().add(task);
        }
    }

    private Integer handleTask(Task task) {
        Integer second = TimeUtils.toSecond(task.getNextExecTime());
        //若下次执行时间小于1秒钟, 则执行任务
        if(second < 1) {
            task.execute();
            return null;
        }
        //若下次执行时间大于60秒钟, 将任务放入分轮
        if(second > 60) {
            minuteWheel.addTask(task);
            return null;
        }
        return second;
    }

}
