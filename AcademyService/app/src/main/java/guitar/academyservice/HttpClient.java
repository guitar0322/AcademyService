package guitar.academyservice;
import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

            Log.d("http_test", "params = " + strParams);

            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK){
                Log.d("http_test", "getResponseFail");
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            String line;
            String page = "";

            while((line = reader.readLine()) != null){
                page += line;
            }
            return page;
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally {
            if(urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }
}
