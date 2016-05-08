package com.euro16.Utils.Enums;

/**
 * Créé par Guillaume le 04/04/2016.
 */
public enum ETypeCommunaute {

    PUBLIQUE("pub", "Publique"),
    PRIVEE("priv", "Privée");

    private final String typeBase;
    private final String nomType;

    ETypeCommunaute(String typeBase, String nomType) {
        this.typeBase = typeBase;
        this.nomType = nomType;
    }

    public String getTypeBase() {
        return this.typeBase;
    }

    public String getNomType() { return this.nomType; }
}
