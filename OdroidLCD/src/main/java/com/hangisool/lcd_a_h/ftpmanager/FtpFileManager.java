package com.hangisool.lcd_a_h.ftpmanager;

import android.util.Log;

import com.hangisool.lcd_a_h.filepath.ApkPath;
import com.hangisool.lcd_a_h.filepath.IpconfigPath;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.ini4j.Wini;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FtpFileManager {

  String remote = "192.168.0.2";    //ftp접속주소
  String ftpId = "hangisool";    //ftp접속ID
  String ftpPwd = "han4143";    //ftp접속 비밀번호
  /*
      String remote = "192.168.0.50";    //ftp접속주소
      String ftpId = "LCD3_SERVER";    //ftp접속ID
      String ftpPwd = "han4143";    //ftp접속 비밀번호
  */
  String rDir;
  String lDir;

  FTPClient client;

  public FtpFileManager(String rDir, String ldir){
    IpconfigPath ipconfigPath = new IpconfigPath();
    try{
      Wini wini = new Wini(new File(ipconfigPath.getImgPath(), "serverIP.ini"));
      remote = wini.get("IP_ADDRESS", "IP");
      ftpId = wini.get("IP_ADDRESS", "ID");
      ftpPwd = wini.get("IP_ADDRESS", "PW");
    }catch (Exception e){

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

  public boolean apkDownload(){
    File getFile;
    FileOutputStream outputStream;
    ApkPath apkPath = new ApkPath();

    try{
      getFile = new File(lDir, "app-debug.apk");
      outputStream = new FileOutputStream(getFile);
      client.retrieveFile(rDir + "/app-debug.apk", outputStream);

      Log.d("LP:", getFile.getPath());
      outputStream.close();
      client.logout();
      client.disconnect();
    }catch (IOException e ){
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean playScriptDownload(){
    File getFile;
    FileOutputStream outputStream;

    try{
      getFile = new File(lDir, "playList.txt");
      outputStream = new FileOutputStream(getFile);
      client.retrieveFile(rDir + "/playList.txt", outputStream);

      Log.d("LP:", getFile.getPath());
      outputStream.close();
      client.logout();
      client.disconnect();
    }catch (IOException e ){
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public void fileDownload(ArrayList<String> downList){
    File getFile;
    FileOutputStream outputstream;
    try {
      for(int i =0; i< downList.size(); i++){
        getFile = new File(lDir,downList.get(i));
        outputstream = new FileOutputStream(getFile);
        client.retrieveFile(new File(rDir+ "/" + downList.get(i)).getName(), outputstream);
        outputstream.close();
      }
      client.logout();
      client.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void imagFileUpload(){
    File uploadFile = new File(lDir);
    FileInputStream fis = null;
    try {
      client.changeWorkingDirectory(rDir);
      fis = new FileInputStream(uploadFile);
      client.storeFile(uploadFile.getName(),fis);

      client.logout();
      client.disconnect();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
