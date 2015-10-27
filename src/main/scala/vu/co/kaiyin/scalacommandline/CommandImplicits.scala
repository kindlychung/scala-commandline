package vu.co.kaiyin.scalacommandline

import ScalaCommand._

/**
 * Created by IDEA on 26/10/15.
 */
object ScalaCommandImplicits {

  implicit class CommandString(commandString: String) {
    val cmdList = commandString.split( """\s+""").toList

    def run = Command(cmdList).run

    def isValidCmd: Boolean = {
      val res = "command -v %s".format(cmdList.head).run;
      if (res.status == 0) true else false
    }

    def |(cmd1: String): List[Command] = {
      List(
        Command(cmdList),
        Command(cmd1)
      )
    }

    def &&(cmd1: String): List[Command] = |(cmd1)

    def ||(cmd1: String): List[Command] = |(cmd1)

    def >>(cmd1: String): List[Command] = |(cmd1)
  }

  implicit class ListCommand(list: List[Command]) {
    def |(cmd: String) = {
      list ++ List(Command(cmd))
    }
    def ||(cmd: String) = |(cmd)
    def &&(cmd: String) = |(cmd)
    def >>(cmd: String) = |(cmd)

    private def runPipeIfP(p: Int => Boolean) = {
      list.foldLeft1(CommandResult(0, "", ""))((res, cmd) => {
        val res1 = cmd.run(res.output)
        res1 match {
          case CommandResult(x, _, _) if p(x) => (res1, true)
          case _ => (res1, false)
        }
      })
    }

    def rp = {
      runPipeIfP(_ != 0)
    }

    private def stopIfP(p: Int => Boolean) = {
      list.foldLeft1(CommandResult(0, "", ""))((res, cmd) => {
        val res1 = cmd.run
        res1 match {
          case CommandResult(x, _, _) if p(x) => (res1, true)
          case _ => (res1, false)
        }
      })
    }

    // run if ok, stop if err
    def rio = {
      stopIfP(_ != 0)
    }

    // run if err, stop if ok
    def rie = {
      stopIfP(_ == 0)
    }

    // run sequentially, never stop
    def rs = {
      stopIfP(_ => false)
    }
  }

  implicit class ListWithLazyFold[A](s: List[A]) {
    def foldLeft1[B](z: B)(f: (B, A) => (B, Boolean)): B = s match {
      case _ if s.isEmpty => z
      case h :: t => {
        val (z1, stop) = f(z, h.asInstanceOf[A])
        if (stop) z1
        else t.asInstanceOf[List[A]].foldLeft1(z1)(f)
      }
    }
  }
}
