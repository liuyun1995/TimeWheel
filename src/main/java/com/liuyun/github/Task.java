package com.liuyun.github;

import com.liuyun.github.utils.ThreadPoolRepo;
import com.liuyun.github.utils.TimeUtils;
import lombok.Data;

/**
 * @author: lewis
 * @create: 2020/1/13 下午4:31
 */
@Data
public class Task <T extends Runnable> {

    /** cron表达式 */
    private String cron;

    /** 下次执行时间, 单位毫秒 */
    private long nextExecTime;

    private T t;

    public Task(String cron, T t) {
        this.cron = cron;
        this.t = t;
    }

    public Task(Long nextExecTime, T t) {
        this.nextExecTime = nextExecTime;
        this.t = t;
    }

    /**
     * 获取下一次执行时间
     * @return
     */
    public long getNextExecTime() {
        return cron == null ? nextExecTime : TimeUtils.getNextExecTime(cron);
    }

    /**
     * 重置下一次执行时间
     */
    public void resetNextExecTime() {
        nextExecTime = TimeUtils.getNextExecTime(cron);
    }

    /**
     * 执行任务
     */
    public void execute() {
        ThreadPoolRepo.getThreadPool().execute(t);
        //执行完任务后重置下一次执行时间
        resetNextExecTime();
    }

}
