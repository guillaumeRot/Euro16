package com.euro16.API;

import com.euro16.Config;
import com.euro16.Model.CurrentSession;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

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

    public static void getCommunautes(String idFacebook, AsyncHttpResponseHandler responseHandler) {
        get(getAbsoluteUrl("getCommunautes") + "&id_facebook=" + idFacebook, responseHandler);
    }

    public static void getCommunautesUtilisateur(String idFacebook, AsyncHttpResponseHandler responseHandler) {
        get(getAbsoluteUrl("getCommunautesUtilisateur") + "&id_facebook=" + idFacebook, responseHandler);
    }

    public static void getGroupesUtilisateur(String idFacebook, AsyncHttpResponseHandler responseHandler) {
        get(getAbsoluteUrl("getGroupesUtilisateur") + "&id_facebook=" + idFacebook, responseHandler);
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

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            post("creerUtilisateur", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void creerCommunaute(String nom, String admin, String photo, String type, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("nom", nom);
            json.put("admin", admin);
            json.put("photo", photo);
            json.put("type", type);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            post("creerCommunaute", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void creerGroupe(String nom, String admin, String photo, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("nom", nom);
            json.put("admin", admin);
            json.put("photo", photo);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            post("creerGroupe", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void ajouterUtilisateurCommunaute(String idFacebook, String communaute, int statut, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("id_facebook", idFacebook);
            json.put("communaute", communaute);
            json.put("statut", statut);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            post("ajouterUtilisateurCommunaute", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /** PUT METHOD **/

    public static void put(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.put(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void updateStatutUtilisateurGroupe(String nomGroupe, String idFacebook, int statut, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("nom_groupe", nomGroupe);
            json.put("id_facebook", idFacebook);
            json.put("new_statut", statut);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            put("updateStatutUtilisateurGroupe", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStatutUtilisateurCommunaute(String nomCommunaute, String idFacebook, int statut, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("nom_communaute", nomCommunaute);
            json.put("id_facebook", idFacebook);
            json.put("new_statut", statut);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            put("updateStatutUtilisateurCommunaute", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /** DELETE METHOD **/

    public static void delete(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.delete(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void deleteUtilisateurGroupe(String groupe, String idFacebook, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("groupe", groupe);
            json.put("id_facebook", idFacebook);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            delete("deleteUtilisateurGroupe", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteUtilisateurCommunaute(String communaute, String idFacebook, AsyncHttpResponseHandler httpResponseHandler) {
        JSONObject json = new JSONObject();
        StringEntity entity;
        try {
            json.put("communaute", communaute);
            json.put("id_facebook", idFacebook);

            entity = new StringEntity(json.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");

            delete("deleteUtilisateurCommunaute", entity, httpResponseHandler);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**********************************/

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl + "&cle=" + CLE + "&id=" + CurrentSession.utilisateur.getId();
    }

    private static String getCle() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            if(CurrentSession.utilisateur != null) {
                String mdp = CurrentSession.utilisateur.getId() + Config.motCle;
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
