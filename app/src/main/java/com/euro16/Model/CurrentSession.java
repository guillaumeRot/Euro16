package com.euro16.Model;

import com.euro16.Utils.Enums.EGroupeEuro;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Guillaume on 09/04/2016.
 */
public class CurrentSession {

    public static Utilisateur utilisateur;
    public static Communaute communaute;
    public static Groupe groupe;
    public static HashMap<EGroupeEuro, ArrayList<Match>> groupeMatchs = new HashMap<>();
    public static HashMap<EGroupeEuro, ArrayList<Equipe>> groupeEquipes = new HashMap<>();
    public static LinkedList<Match> matchNonPronostiques = new LinkedList<>();

    public static ArrayList<Match> getMatchs(EGroupeEuro groupe) {
        return groupeMatchs.get(groupe);
    }

    public static ArrayList<Equipe> getEquipes(EGroupeEuro groupe) {
        return groupeEquipes.get(groupe);
    }

    public static Equipe getEquipeByNom(String nom) {
        for(ArrayList<Equipe> listEquipes : groupeEquipes.values()) {
            for(Equipe equipe : listEquipes) {
                if(equipe.getNom().equalsIgnoreCase(nom)) {
                    return equipe;
                }
            }
        }
        return null;
    }

    public static Match getMatch(String equipe1, String equipe2, EGroupeEuro groupe) {
        for(Match match : groupeMatchs.get(groupe)) {
            if(match.getEquipe1().getNom().equalsIgnoreCase(equipe1) && match.getEquipe2().getNom().equalsIgnoreCase(equipe2)) {
                return match;
            }
        }
        return null;
    }

    public static Match getMatch(String equipe1, String equipe2, Date dateMatch) {
        for(ArrayList<Match> matchs : groupeMatchs.values()) {
            for(Match match : matchs) {
                if(match.getEquipe1().getNom().equalsIgnoreCase(equipe1) && match.getEquipe2().getNom().equalsIgnoreCase(equipe2)
                        && match.getDateMatch().compareTo(dateMatch) == 0) {
                    return match;
                }
            }
        }
        return null;
    }

    public static Match getMatchNonProno(String equipe1, String equipe2, Date dateMatch) {
        for(Match match : matchNonPronostiques) {
            if(match.getEquipe1().getNom().equalsIgnoreCase(equipe1) && match.getEquipe2().getNom().equalsIgnoreCase(equipe2)
                    && match.getDateMatch().compareTo(dateMatch) == 0) {
                return match;
            }
        }
        return null;
    }
}
