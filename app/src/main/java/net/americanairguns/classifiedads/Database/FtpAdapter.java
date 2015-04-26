package net.americanairguns.classifiedads.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FtpAdapter extends AsyncTask<Object, Void, Boolean> {

    private Context context;
    private String remoteFilePath;
    private File localFile;

    @Override
    protected Boolean doInBackground(Object... params) {
        return connectFTP((String)params[0], (String)params[1], (String)params[2], (Boolean)params[3]);
    }

    public FtpAdapter(Context context, String remoteFilePath, File localFile) {
        this.context = context;
        this.remoteFilePath = remoteFilePath;
        this.localFile = localFile;
    }

    public FtpAdapter(Context context, String remoteFilePath, String localFilePath) {
        this(context, remoteFilePath, new File(context.getFilesDir(), localFilePath));
    }

    public FtpAdapter(Context context, String remoteFilePath) {
        this(context, remoteFilePath, new File(context.getFilesDir(), "ftpDownload_" + String.valueOf(System.currentTimeMillis())));
    }

    protected void onPostExecute(Boolean result) {
        Toast.makeText(context.getApplicationContext(), (result ? "FTP Test Successful" : "FTP Test Failed"), Toast.LENGTH_LONG).show();
        Log.i("FTP_TEST", (result ? "FTP Test Successful" : "FTP Test Failed"));
    }

    public boolean connectFTP(String ip, String userName, String pass, Boolean downloadUpload) {
        boolean status;
        try {
            FTPClient mFtpClient = new FTPClient();
            mFtpClient.setConnectTimeout(10 * 1000);
            mFtpClient.connect(InetAddress.getByName(ip));
            status = mFtpClient.login(userName, pass);
            Log.i("FTP_CONNECTION_STATUS", String.valueOf(status));
            if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                mFtpClient.enterLocalPassiveMode();
                FTPFile[] mFileArray = mFtpClient.listFiles();
                Log.i("FTP_LIST", String.valueOf(mFileArray.length));
                return (downloadUpload ? downloadSingleFile(mFtpClient, remoteFilePath, localFile) : uploadFile(mFtpClient, remoteFilePath, localFile));
            }
            return false;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean downloadSingleFile(FTPClient ftpClient,
                                      String remoteFilePath, File downloadFile) {
        if (ftpClient == null) return false;
        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists())
            parentDir.mkdir();
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    downloadFile));
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean uploadFile(FTPClient ftpClient, String remoteFilePath, File downloadFile) {
        try {
            FileInputStream srcFileStream = new FileInputStream(downloadFile);
            boolean status = ftpClient.storeFile(remoteFilePath, srcFileStream);
            Log.e("Status", String.valueOf(status));
            srcFileStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
