/*
 * Copyright (C) 2016-2018 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.stream.alpakka.hdfs

import akka.stream.alpakka.hdfs.HdfsWritingSettings._

final case class HdfsWritingSettings(
    overwrite: Boolean = true,
    newLine: Boolean = false,
    pathGenerator: FilePathGenerator = DefaultFilePathGenerator
) {
  def withOverwrite(overwrite: Boolean): HdfsWritingSettings = copy(overwrite = overwrite)
  def withNewLine(newLine: Boolean): HdfsWritingSettings = copy(newLine = newLine)
  def withPathGenerator(generator: FilePathGenerator): HdfsWritingSettings = copy(pathGenerator = generator)
}

object HdfsWritingSettings {

  private val DefaultFilePathGenerator: FilePathGenerator =
    FilePathGenerator.create((rc: Long, _: Long) => s"/tmp/alpakka/$rc")

  /**
   * Java API
   */
  def create(): HdfsWritingSettings = HdfsWritingSettings()
}

final case class WriteLog(path: String, rotation: Int)

private[hdfs] sealed case class FileUnit(byteCount: Long)

object FileUnit {
  val KB = FileUnit(Math.pow(2, 10).toLong)
  val MB = FileUnit(Math.pow(2, 20).toLong)
  val GB = FileUnit(Math.pow(2, 30).toLong)
  val TB = FileUnit(Math.pow(2, 40).toLong)
}

private[hdfs] trait Strategy {
  type S <: Strategy
  def should(): Boolean
  def reset(): S
  def update(offset: Long): S
}
