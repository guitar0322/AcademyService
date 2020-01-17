package guitar.academyservice;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpClient    {
    public String request(String _url, ContentValues params){
        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();

        if(params == null) sbParams.append("");
        else{
            boolean isAnd = false;
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if(isAnd == true) sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                if(isAnd == false && params.size() > 1) isAnd = true;
            }
        }

        try{
            String strParams = sbParams.toString();
            URL url = new URL(_url + strParams);
            Log.d("http_test", "url = " + url.toString());
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setConnectTimeout(2000);
            urlConn.setRequestMethod("POST");
            urlConn.setDoInput(true);
            urlConn.setRequestProperty("Accept-Charset", "UTF-8");
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

//            OutputStream os = urlConn.getOutputStream();
            Log.d("http_test", "params = " + strParams);

//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

//            writer.write(strParams.getBytes().toString());
//            Log.d("http_test", "getBytes = " + strParams.getBytes().toString());
//            writer.flush();
//            writer.close();
//            os.write(strParams.getBytes("UTF-8"));
//            os.flush();
//            os.close();

            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.d("http_test", "getResponseFail");
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            String line;
            String page = "";

            while((line = reader.readLine()) != null){
                Log.d("http_test", "line = " + line);
                page += line;
            }
            return page;
        }
        catch(MalformedURLException e){
            e.printStackTrace();
            Log.d("http_test", "malformedException");
        }
        catch(IOException e){
            Log.d("http_test", "ioexception");
            e.printStackTrace();
        }
        finally {
            if(urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }
}
