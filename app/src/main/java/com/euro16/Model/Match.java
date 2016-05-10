package com.euro16.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.euro16.Utils.Enums.EDateFormat;
import com.euro16.Utils.Enums.EGroupeEuro;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Guillaume on 02/05/2016.
 */
public class Match implements Parcelable{

    private Equipe equipe1;
    private Equipe equipe2;
    private EGroupeEuro groupe;
    private int score1;
    private int score2;
    private Date dateMatch;

    public Match(Equipe equipe1, Equipe equipe2, int score1, int score2, Date dateMatch, EGroupeEuro groupe) {
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.groupe = groupe;
        this.score1 = score1;
        this.score2 = score2;
        this.dateMatch = dateMatch;
    }

    public Match(Equipe equipe1, Equipe equipe2, Date dateMatch) {
        this.equipe1 = equipe1;
        this.equipe2 = equipe2;
        this.dateMatch = dateMatch;
    }

    public Match(Parcel in) {
        in.readValue(Match.class.getClassLoader());
        in.readValue(Match.class.getClassLoader());
        in.readValue(Match.class.getClassLoader());
        in.readValue(Match.class.getClassLoader());
        in.readValue(Match.class.getClassLoader());
        in.readValue(Match.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        @Override
        public Match createFromParcel(Parcel source)
        {
            return new Match(source);
        }

        @Override
        public Match[] newArray(int size)
        {
            return new Match[size];
        }
    };

    public Equipe getEquipe1() {
        return equipe1;
    }

    public Equipe getEquipe2() {
        return equipe2;
    }

    public EGroupeEuro getGroupe() {
        return groupe;
    }

    public int getScore1() {
        return score1;
    }

    public int getScore2() {
        return score2;
    }

    public Date getDateMatch() {
        return dateMatch;
    }

    public void setEquipe1(Equipe equipe1) {
        this.equipe1 = equipe1;
    }

    public void setEquipe2(Equipe equipe2) {
        this.equipe2 = equipe2;
    }

    public void setGroupe(EGroupeEuro groupe) {
        this.groupe = groupe;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }

    public void setDateMatch(Date dateMatch) {
        this.dateMatch = dateMatch;
    }

    @Override
    // Non utilisée
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(equipe1);
        dest.writeValue(equipe2);
        dest.writeValue(groupe);
        dest.writeValue(score1);
        dest.writeValue(score2);
        dest.writeValue(dateMatch);
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EDateFormat.DATE_SIMPLE.getFormatDate());
        return "Match { " +
                "equipe1 = " + equipe1.getNom() +
                ", equipe2 = " + equipe2.getNom() +
                ", groupe = " + groupe +
                ", score1 = " + score1 +
                ", score2 = " + score2 +
                ", dateMatch = " + dateFormat.format(dateMatch) +
                '}';
    }
}
