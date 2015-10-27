import org.scalatest._
import vu.co.kaiyin.scalacommandline.CmdExceptions.InvalidCmdException
import vu.co.kaiyin.scalacommandline.{ScalaCommandImplicits, ScalaCommand}
import ScalaCommand._
import ScalaCommandImplicits._

import scala.collection.mutable.ArrayBuffer

class CommandSpec extends FlatSpec with Matchers {
  def rcmd = randomString.make(10)

  def prepareDir(nRandFiles: Int) = {
    val randDir = "/tmp/%s/".format(randomString.make(10))
    val lsDir = "ls %s".format(randDir)
    var tmp = "mkdir %s".format(randDir).run
    for(i <- 0 until nRandFiles) {
      tmp = "touch %s%s".format(randDir, randomString.make(10)).run
    }
    (randDir, lsDir)
  }

  "Random strings" should "be invalid commands" in {
    for (_ <- 1 to 10) {
      val cmd: String = randomString.make(10)
      cmd.isValidCmd should be(false)
      val res = cmd run;
      res.status should not be (0)
      res.error.contains("No such file or directory") should be(true)
    }
  }

  "Normal Unix commands" should "be valid" in {
    List("ls", "cp", "mv", "grep", "cd").foreach(
      x => {
        x.isValidCmd should be(true)
      }
    )
  }


  "mkdir, touch, grep, ls and pipes" should "work" in {
    val (randDir, lsDir) = prepareDir(0)
    val res1 = "touch %sscmd1 %sscmd2 %sscmd3".format(randDir, randDir, randDir).run
    res1.status should be(0)
    val res2 = lsDir run;
    res2.output.split( """\s+""").toList should be("scmd1 scmd2 scmd3".split( """\s+""").toList)
    val res3 = lsDir | "grep 3" rp;
    res3.output.trim should be("scmd3")
  }


  "Sequential runs" should "work" in {
    val (randDir, lsDir) = prepareDir(3)
    val res0 = lsDir && rcmd rio;
    res0.status should not be (0)
    val res1 = lsDir && rcmd && lsDir rio;
    res1.status should not be (0)
    val res2 = rcmd || lsDir rie;
    res2.status should be (0)
    val res3 = lsDir | rcmd rie;
    res3.status should be (0)
    val res4 = rcmd | lsDir | rcmd rie;
    res4.status should be (0)
    val res5 = rcmd | rcmd | lsDir | rcmd rie;
    res5.status should be (0)
  }

  "Early-termination foldLeft" should "work" in {
    val buffer = new ArrayBuffer[Int]()
    val x = List(1, 2, 3, 4, 5)
    // Are all ints in x less than 3?
    x.foldLeft1(true)((bool, int) => {
      buffer += 1
      val check = int < 3 && bool
      (check, !check)
    })
    buffer.length should be(3)
  }

}


