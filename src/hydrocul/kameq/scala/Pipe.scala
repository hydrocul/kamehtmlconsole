package hydrocul.kameq.scala;

import java.util.concurrent.TimeUnit;

trait Pipe[+R] {

  import Pipe._;

  def | [S](g: R=>S): Pipe[S];

  def | [S](next: (R=>S, TaskBuilder)): Pipe[S];

}

object Pipe {

  trait TaskBuilder {

    def create(process: ()=>Unit): Task;

  }

  val TaskBuilder: TaskBuilder = new TaskBuilder {

    def create(process: ()=>Unit) = new Task({ process() });

  }

  val IOTaskBuilder: TaskBuilder = new TaskBuilder {

    def create(process: ()=>Unit) = new IOTask({ process() });

  }

  def SynchronizedTaskBuilder(actor: Any): TaskBuilder = new TaskBuilder {

    def create(process: ()=>Unit) = new SynchronizedTask(actor, { process() });

  }

  private[scala] class Receiver[R] {

    private var _result: Option[R] = None;
    private var _next: Option[R=>Unit] = None;

    def put(result: R){
      synchronized {
        _next match {
          case None => _result = Some(result);
          case Some(n) => executeNext(result, n);
        }
      }
    }

    def setNext(process: R=>Unit){
      synchronized {
        _result match {
          case None => _next = Some(process);
          case Some(r) => executeNext(r, process);
        }
      }
    }

    private def executeNext(result: R, process: R=>Unit){
      process(result);
    }

  }

  private[scala] class PipeImpl[+R](receiver: Pipe.Receiver[R]) extends Pipe[R] {

    private def vertical[S](next: R=>S, taskBuilder: TaskBuilder): Pipe[S] = {
      val nextReceiver = new Receiver[S];
      receiver.setNext(
        (r: R) => taskBuilder.create(
          () => nextReceiver.put(next(r))
        ).submit()
      );
      new PipeImpl[S](nextReceiver);
    }

    def | [S](g: R=>S): Pipe[S] = vertical(g, TaskBuilder);

    def | [S](next: (R=>S, TaskBuilder)) = vertical(next._1, next._2);

  }

  def exec[R](g: =>R): Pipe[R] = {
    val receiver = new Receiver[Unit];
    receiver.put(());
    (new PipeImpl[Unit](receiver)) | ((u:Unit) => g);
  }

  def ioexec[R](g: =>R): Pipe[R] = {
    val receiver = new Receiver[Unit];
    receiver.put(());
    (new PipeImpl[Unit](receiver)) | ((u:Unit) => g, IOTaskBuilder);
  }

  def synexec[R](actor: Any)(g: =>R): Pipe[R] = {
    val receiver = new Receiver[Unit];
    receiver.put(());
    (new PipeImpl[Unit](receiver)) | ((u:Unit) => g, SynchronizedTaskBuilder(actor));
  }

  def iopipe[R, S](process: R=>S): (R=>S, TaskBuilder) =
    (process, IOTaskBuilder);

  def synpipe[R, S](actor: Any)(process: R=>S): (R=>S, TaskBuilder) =
    (process, SynchronizedTaskBuilder(actor));

}
