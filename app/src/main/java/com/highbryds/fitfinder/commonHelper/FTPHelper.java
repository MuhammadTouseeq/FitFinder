package com.highbryds.fitfinder.commonHelper;

import android.os.AsyncTask;
import android.util.Log;

import com.highbryds.fitfinder.callbacks.FTPCallback;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.InetAddress;

public class FTPHelper {
    FTPClient ftpClient;
    boolean result = false;
    FTPCallback ftpCallback;
    String fileName;


    public void init(FTPCallback ftpCallback){
        this.ftpCallback = ftpCallback;
    }

    public class AsyncTaskExample extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                fileName = System.currentTimeMillis()+strings[1];
                ftpClient = new FTPClient();
                ftpClient.connect(InetAddress.getByName("maarkaz.co.uk"));
                ftpClient.login("highbryds@maarkaz.co.uk", "!@#$%^&*()");
                ftpClient.changeWorkingDirectory("/fitfinder/stories/");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                BufferedInputStream buffIn = null;
                buffIn = new BufferedInputStream(new FileInputStream(strings[0]));
                ftpClient.enterLocalPassiveMode();
                ProgressInputStream progressInput = new ProgressInputStream(
                        buffIn);
                ftpClient.storeFile(fileName, buffIn);
                 result = ftpClient.storeFile("test2.mp4",
                        progressInput);

                Log.d("DResult", String.valueOf(result));
                buffIn.close();
                ftpClient.logout();
                ftpClient.disconnect();

            } catch (Exception e) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (Exception i) {
                    e.printStackTrace();
                }
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            Log.d("D", "D");
            if (result){
                ftpCallback.isFTPUpload(true ,fileName);
            }else{
                ftpCallback.isFTPUpload(false,fileName);
            }

        }
    }
}
