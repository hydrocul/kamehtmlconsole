package hydrocul.kameq;

import java.util.concurrent.TimeUnit;

class Task[R](p: =>R){

  protected def getRunnable: Runnable = new Runnable {
    def run(): Unit = p;
  }

  def submit(){
    Task.taskManager.submit(getRunnable);
  }

  def schedule(delay: Long, timeUnit: TimeUnit){
    Task.taskManager.schedule(getRunnable, delay, timeUnit);
  }

}

object Task {

  def apply[R](p: =>R) = new Task[R](p);

  val taskManager = new TaskManager();

}

class IOTask[R](p: =>R) extends Task[R](p) {

  override def submit(){
    Task.taskManager.submitIO(getRunnable);
  }

  override def schedule(delay: Long, timeUnit: TimeUnit){
    Task.taskManager.scheduleIO(getRunnable, delay, timeUnit);
  }

}

object IOTask {

  def apply[R](p: =>R) = new IOTask[R](p);

}

class SynchronizedTask[R](actor: Any, p: =>R) extends Task[R](p) {

  override def submit(){
    Task.taskManager.submit(getRunnable, actor);
  }

  override def schedule(delay: Long, timeUnit: TimeUnit){
    Task.taskManager.schedule(getRunnable, actor, delay, timeUnit);
  }

}

object SynchronizedTask {

  def apply[R](actor: Any)(p: =>R) = new SynchronizedTask[R](actor, p);

}

class TaskBuilder[A, R](p: A=>R){

  def createTask(a: A) = new Task[R](p(a));

}

object TaskBuilder {

  def apply[A, R](p: A=>R) = new TaskBuilder[A, R](p);

}

class IOTaskBuilder[A, R](p: A=>R) extends TaskBuilder[A, R](p){

  override def createTask(a: A) = new IOTask[R](p(a));

}

object IOTaskBuilder {

  def apply[A, R](p: A=>R) = new IOTaskBuilder[A, R](p);

}

class SynchronizedTaskBuilder[A, R](actor: Any, p: A=>R) extends TaskBuilder[A, R](p){

  override def createTask(a: A) = new SynchronizedTask(actor, p(a));

}

object SynchronizedTaskBuilder {

  def apply[A, R](actor: Any)(p: A=>R) = new SynchronizedTaskBuilder[A, R](actor, p);

}

