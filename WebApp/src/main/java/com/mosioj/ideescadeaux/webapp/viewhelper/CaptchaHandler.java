package com.mosioj.ideescadeaux.webapp.viewhelper;

import com.mosioj.ideescadeaux.webapp.utils.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.MessageFormat;

public class CaptchaHandler {

    private static final Logger logger = LogManager.getLogger(CaptchaHandler.class);
    private static final String SECRET_KEY = ApplicationProperties.getProp().getProperty("googleCaptchaSecretKey");
    private static URL URL = null;

    /**
     * @param captchaResponse Response received from the client.
     * @return True if it passed the validation, false otherwise
     */
    public static boolean resolveIt(String captchaResponse) {

        if (URL == null) {
            return false;
        }

        HttpsURLConnection con;
        try {
            con = (HttpsURLConnection) URL.openConnection();
        } catch (IOException e) {
            logger.error("Error while opening the URL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // add request header
        try {
            con.setRequestMethod("POST");
            con.setDoOutput(true);
        } catch (ProtocolException e) {
            logger.error("Error while setting the protocol: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        String urlParameters = MessageFormat.format("secret={0}&response={1}", SECRET_KEY, captchaResponse);

        // Send post request
        try {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        } catch (IOException e) {
            logger.error("Error while writting parameters: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        try {
            con.getResponseCode();
        } catch (IOException e) {
            logger.error("Error while processing the request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(new InputStreamReader(con.getInputStream()));
            String isSuccess = response.get("success").toString();
            logger.debug("Success ? " + isSuccess);
            return Boolean.parseBoolean(isSuccess);
        } catch (IOException | ParseException e) {
            logger.error("Error while reading the anwers: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    static {
        try {
            URL = new URL("https://www.google.com/recaptcha/api/siteverify");
        } catch (MalformedURLException e) {
            logger.error("Error while resolving the creating Captcha URL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
