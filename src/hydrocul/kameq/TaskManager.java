package hydrocul.kameq;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {

    private final ScheduledExecutorService scheduler;
    private final LinkedBlockingQueue<Runnable> queue;
    private final AtomicInteger currThreadCount;
    private final AtomicInteger leftThreadCount;

    private final ConcurrentHashMap<Object, SynchronizedTask> synchronizedTaskQueue;

    public TaskManager(){
        scheduler = Executors.newScheduledThreadPool(5);
        queue = new LinkedBlockingQueue<Runnable>();
        currThreadCount = new AtomicInteger(0);
        leftThreadCount = new AtomicInteger(
            Runtime.getRuntime().availableProcessors() * 2);
        synchronizedTaskQueue = new ConcurrentHashMap<Object, SynchronizedTask>();
    }

    public void submit(Runnable task){
        try {
            queue.put(task);
        } catch(InterruptedException e){
            throw new AssertionError(e);
        }
        start();
    }

    public void schedule(final Runnable task, long delay, TimeUnit timeUnit){
        scheduler.schedule(new Runnable(){
            public void run(){
                submit(task);
            }
        }, delay, timeUnit);
    }

    public void submit(Runnable task, Object actor){
        submitSynchronizedTask(task, actor);
    }

    public void schedule(final Runnable task, final Object actor, long delay, TimeUnit timeUnit){
        schedule(new Runnable(){
            public void run(){
                submit(task, actor);
            }
        }, delay, timeUnit);
    }

    public void submitIO(Runnable task){
        submitIOTask(task, 0, null);
    }

    public void scheduleIO(Runnable task, long delay, TimeUnit timeUnit){
        submitIOTask(task, delay, timeUnit);
    }

    private void start(){
        if(leftThreadCount.get() <= 0){
            return;
        }
        synchronized(this){
            if(queue.isEmpty()){
                return;
            }
            if(!incrementThreadCount()){
                return;
            }
        }
        Runnable runnable = new Runnable(){
            public void run(){
                while(true){
                    try {
                        doQueue(queue, 10, TimeUnit.SECONDS);
                    } finally {
                        synchronized(TaskManager.this){
                            decrementThreadCount();
                        }
                    }
                    if(Thread.currentThread().interrupted()){
                        break;
                    }
                    synchronized(TaskManager.this){
                        if(queue.isEmpty()){
                            break;
                        }
                        if(!incrementThreadCount()){
                            break;
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private boolean incrementThreadCount(){
        if(leftThreadCount.decrementAndGet() < 0){
            leftThreadCount.incrementAndGet();
            return false;
        }
        currThreadCount.incrementAndGet();
        return true;
    }

    private void decrementThreadCount(){
        currThreadCount.decrementAndGet();
        leftThreadCount.incrementAndGet();
    }

    private void doQueue(LinkedBlockingQueue<Runnable> queue,
            long delay, TimeUnit timeUnit){
        while(true){
            Runnable task;
            try {
                task = queue.poll(delay, timeUnit);
            } catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
            if(task==null){
                break;
            }
            try {
                task.run();
            } catch(Throwable e){
                handleException(e);
            }
            Thread.currentThread().interrupted();
        }
    }

    private void handleException(Throwable e){
        e.printStackTrace();
    }

    private void submitSynchronizedTask(Runnable task, final Object actor){
        SynchronizedTask q = synchronizedTaskQueue.putIfAbsent(actor,
            new SynchronizedTask());
        if(q==null){
            q = synchronizedTaskQueue.get(actor);
        }
        try {
            q.queue.put(task);
        } catch(InterruptedException e){
            throw new AssertionError(e);
        }
        Runnable task2 = new Runnable(){
            public void run(){
                while(true){
                    SynchronizedTask q = synchronizedTaskQueue.get(actor);
                    if(!q.executing.compareAndSet(false, true)){
                        break;
                    }
                    try {
                        doQueue(q.queue, 10, TimeUnit.MILLISECONDS);
                    } finally {
                        q.executing.set(false);
                    }
                    if(Thread.currentThread().interrupted()){
                        break;
                    }
                    if(q.queue.isEmpty()){
                        break;
                    }
                }
            }
        };
        if(q.executing.get()){
            return;
        }
        submit(task2);
    }

    private static class SynchronizedTask {

        SynchronizedTask(){
            executing = new AtomicBoolean(false);
            queue = new LinkedBlockingQueue<Runnable>();
        }

        final AtomicBoolean executing;
        final LinkedBlockingQueue<Runnable> queue;

    }

    private void submitIOTask(final Runnable task, long delay, TimeUnit timeUnit){
        Runnable task2 = new Runnable(){
            public void run(){
                leftThreadCount.incrementAndGet();
                try {
                    task.run();
                } catch(Throwable e){
                    handleException(e);
                } finally {
                    leftThreadCount.decrementAndGet();
                }
            }
        };
        if(delay==0){
            submit(task2);
        } else {
            schedule(task2, delay, timeUnit);
        }
    }

}
