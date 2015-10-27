package vu.co.kaiyin.scalacommandline

import java.io.{OutputStreamWriter, BufferedWriter}

import vu.co.kaiyin.scalacommandline.CmdExceptions.InvalidCmdException

import scala.collection.JavaConversions._
import scala.collection.immutable.IndexedSeq
import scala.io.Source
import scala.language.postfixOps
import scala.util.Random
import ScalaCommandImplicits._

object ScalaCommand {

  case class CommandResult(status: Int, output: String, error: String)

  class Command(commandParts: List[String]) {
    def run(input: String): CommandResult = {
      try {
        val processBuilder = new ProcessBuilder(commandParts)
        val process = processBuilder.start()

        // pipe string into stdin of the cmd
        val writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))
        if(! input.isEmpty) {
          try {
            writer.write(input)
          } finally {
            writer.close()
          }
        }

        val status = process.waitFor()
        val outputAsString =
          Source.fromInputStream(process.getInputStream()).mkString("")
        val errorAsString =
          Source.fromInputStream(process.getErrorStream()).mkString("")
        CommandResult(status, outputAsString, errorAsString)
      } catch {
        case e: Exception => CommandResult(1, "", e.getMessage)
      }
    }
    def run: CommandResult = {
      run("")
    }
  }

  object Command {
    def apply(commandString: String) = new Command(commandString.split( """\s+""").toList)

    def apply(cmdList: List[String]) = new Command(cmdList)
  }


  object randomString {
    val rand = new Random()
    val alphaNumerics: IndexedSeq[Char] = ('a' to 'z') ++ ('0' to '9')

    def make(n: Int): String = {
      val chars = for (i <- 0 until n) yield {
        val index = rand.nextInt(alphaNumerics.length)
        alphaNumerics(index)
      }
      chars.mkString("")
    }
  }

}


