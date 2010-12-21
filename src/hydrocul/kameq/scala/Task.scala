package hydrocul.kameq.scala;

import java.util.concurrent.TimeUnit;

import hydrocul.kameq.TaskManager;

class Task(p: =>Unit) {

  protected def getRunnable: Runnable = new Runnable {
    def run() = p;
  }

  def submit(){
    Task.taskManager.submit(getRunnable);
  }

  def schedule(delay: Long, timeUnit: TimeUnit){
    Task.taskManager.schedule(getRunnable, delay, timeUnit);
  }

}

object Task {

  def apply(p: =>Unit) = new Task(p);

  val taskManager = new TaskManager();

}

class IOTask(p: =>Unit) extends Task(p) {

  override def submit(){
    Task.taskManager.submitIO(getRunnable);
  }

  override def schedule(delay: Long, timeUnit: TimeUnit){
    Task.taskManager.scheduleIO(getRunnable, delay, timeUnit);
  }

}

object IOTask {

  def apply(p: =>Unit) = new IOTask(p);

}

class SynchronizedTask(actor: Any, p: =>Unit) extends Task(p) {

  override def submit(){
    Task.taskManager.submit(getRunnable, actor);
  }

  override def schedule(delay: Long, timeUnit: TimeUnit){
    Task.taskManager.schedule(getRunnable, actor, delay, timeUnit);
  }

}

object SynchronizedTask {

  def apply(actor: Any, p: =>Unit) = new SynchronizedTask(actor, p);

}
