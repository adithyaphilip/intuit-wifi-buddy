package com.aaphilip.tools.autowifilogin.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aaphilip.tools.autowifilogin.util.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Requester {

    private ResultHandler mResultHandler;

    public static abstract class ResultHandler extends Handler {
        private static final int RESULT_SUCCESS = 0;
        private static final int RESULT_ERROR = 1;
        private static final int RESULT_FAILURE = 2; // not IO error, but login failed
        @Override
        public final void handleMessage(Message msg) {
            int resultCode = msg.arg1;
            switch (resultCode) {
                case RESULT_SUCCESS:
                    onSuccess((String) msg.obj);
                    break;
                case RESULT_FAILURE:
                    onFailure((String) msg.obj);
                    break;
                case RESULT_ERROR:
                    onError((IOException) msg.obj);
                    break;
                default:
                    throw new RuntimeException("Received unknown result code " + resultCode);
            }
        }

        public abstract void onError(IOException e);
        public abstract void onSuccess(String response);
        public abstract void onFailure(String response);
    }

    public Requester(ResultHandler handler) {
        mResultHandler = handler;
    }

    public void makeAsyncRequest(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message msg = mResultHandler.obtainMessage();

                try {
                    String response = makeRequest(username, password);
                    msg.arg1 = response.contains("Login Successful") ?
                                ResultHandler.RESULT_SUCCESS : ResultHandler.RESULT_FAILURE;
                    msg.obj = response;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.arg1 = ResultHandler.RESULT_ERROR;
                    msg.obj = e;
                }

                msg.sendToTarget();
            }
        }).start();
    }

    private String makeRequest(String username, String password) throws IOException {
        Log.d("Requester", "Making request with " + username + ":" + password);

        URLConnection connection = new URL(Constants.LOGIN_URL).openConnection();
        connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
        connection.setDoOutput(true);
        PrintWriter pw = new PrintWriter(connection.getOutputStream());

        pw.print(
                "buttonClicked=4&err_flag=0&err_msg=&info_flag=0&info_msg=" +
                        "&redirect_url=http%3A%2F%2Fwww.gstatic.com%2Fgenerate_204" +
                        "&network_name=Guest+Network" +
                        String.format("&username=%s&password=%s", username, password));
        pw.flush();
        Scanner sc = new Scanner(connection.getInputStream());

        String response = "";
        while (sc.hasNextLine()) {
            response += sc.nextLine();
        }

        Log.d("Requester", response);

        return response;
    }
}
