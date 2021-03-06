/*
 * Copyright (C) 2016-2018 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.stream.alpakka.ftp
package impl

import org.apache.commons.net.ftp.{FTP, FTPClient}

import scala.util.Try

private[ftp] trait FtpOperations extends CommonFtpOperations { _: FtpLike[FTPClient, FtpSettings] =>

  def connect(connectionSettings: FtpSettings)(implicit ftpClient: FTPClient): Try[Handler] = Try {
    ftpClient.connect(connectionSettings.host, connectionSettings.port)

    connectionSettings.configureConnection(ftpClient)

    ftpClient.login(
      connectionSettings.credentials.username,
      connectionSettings.credentials.password
    )

    if (connectionSettings.binary) {
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
    }

    if (connectionSettings.passiveMode) {
      ftpClient.enterLocalPassiveMode()
    }

    ftpClient
  }

  def disconnect(handler: Handler)(implicit ftpClient: FTPClient): Unit =
    if (ftpClient.isConnected) {
      ftpClient.logout()
      ftpClient.disconnect()
    }
}
