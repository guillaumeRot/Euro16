package com.euro16.API;

import com.euro16.Activity.FacebookConnexion;
import com.euro16.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Créé par Guillaume le 16/03/2016.
 */
public class RestClient {
    private static final String BASE_URL = Config.urlApi;
    private static final String CLE = getCle();

    private static AsyncHttpClient client = new AsyncHttpClient();

    /** GET METHOD **/

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, responseHandler);
    }

    public static void getUtilisateur(String idFacebook, AsyncHttpResponseHandler responseHandler) {
        get(getAbsoluteUrl("getUtilisateur") + "&id_facebook=" + idFacebook, responseHandler);
    }

    /** POST METHOD **/

    public static void post(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void creerUtilisateur(String nom, String prenom, String photo, String idFacebook, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("nom", nom);
            json.put("prenom", prenom);
            json.put("photo", photo);
            json.put("id_facebook", idFacebook);

            entity = new StringEntity(json.toString());

            post("creerUtilisateur", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /** PUT METHOD **/

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    /** DELETE METHOD **/

    public static void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl + "&cle=" + CLE + "&id=" + FacebookConnexion.profil.getId();
    }

    private static String getCle() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            if(FacebookConnexion.profil != null) {
                String mdp = FacebookConnexion.profil.getId() + Config.motCle;
                digest.update(mdp.getBytes());
                byte messageDigest[] = digest.digest();

                StringBuffer cle = new StringBuffer();
                for (int i=0; i<messageDigest.length; i++)
                    cle.append(Integer.toHexString(0xFF & messageDigest[i]));
                return cle.toString();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
