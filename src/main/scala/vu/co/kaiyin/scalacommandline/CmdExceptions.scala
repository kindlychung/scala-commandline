package vu.co.kaiyin.scalacommandline

/**
 * Created by IDEA on 27/10/15.
 */
object CmdExceptions {
  class InvalidCmdException(msg: String) extends Exception(msg)
}
