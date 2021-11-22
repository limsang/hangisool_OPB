package com.hangisool.lcd_a_h.ftpmanager;

import com.hangisool.lcd_a_h.filepath.IpconfigPath;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.ini4j.Wini;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FtpContentsManager {
  //String remote = "59.11.245.146";    //ftp접속주소
  //String ftpId = "D33";    //ftp접속ID
  //String ftpPwd = "3544";    //ftp접속 비밀번호

  String remote = "192.168.11.22";    //ftp접속주소
  String ftpId = "LCD2_SERVER";    //ftp접속ID
  String ftpPwd = "3544";    //ftp접속 비밀번호

  String rDir;
  String lDir;

  public FTPClient client = null;

  public FtpContentsManager(String rDir, String ldir){
    IpconfigPath ipconfigPath = new IpconfigPath();
    try{
      Wini wini = new Wini(new File(ipconfigPath.getImgPath(), "serverIP.ini"));
      remote = wini.get("IP_ADDRESS", "IP");
      ftpId = wini.get("IP_ADDRESS", "ID");
      ftpPwd = wini.get("IP_ADDRESS", "PW");
    }catch (Exception e){
      e.printStackTrace();
    }
    this.rDir=rDir;
    this.lDir=ldir;
    client = new FTPClient();
    client.setControlEncoding("euc-kr");
  }

  public boolean connect(){
    try {
      client.connect(remote,21);

      if(!FTPReply.isPositiveCompletion(client.getReplyCode())){
        return false;
      }

      if(!client.login(ftpId,ftpPwd)){
        return false;
      }

      client.setFileType(FTP.BINARY_FILE_TYPE);
      client.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

      if(!client.changeWorkingDirectory(rDir)){
        return false;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  public void fileDownload(){
    File getFile;
    FileOutputStream outputstream;

    try {
      if (client.changeWorkingDirectory("/")) {
        getFile = new File(lDir + "/news.ini");
        outputstream = new FileOutputStream(getFile);
        client.retrieveFile("contents/news.ini", outputstream);
        outputstream.close();
      }

      if (client.changeWorkingDirectory("/")) {
        getFile = new File(lDir + "/stock_price.ini");
        outputstream = new FileOutputStream(getFile);
        client.retrieveFile("contents/stock_price.ini", outputstream);
        outputstream.close();
      }

      if (client.changeWorkingDirectory("/")) {
        getFile = new File(lDir + "/exchange.ini");
        outputstream = new FileOutputStream(getFile);
        client.retrieveFile("contents/exchange.ini", outputstream);
        outputstream.close();
      }

      if (client.changeWorkingDirectory("/")) {
        getFile = new File(lDir + "/movieRank.ini");
        outputstream = new FileOutputStream(getFile);
        client.retrieveFile("contents/movieRank.ini", outputstream);
        outputstream.close();
      }

      client.logout();
      client.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
