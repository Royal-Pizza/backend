package com.example.royalpizza.exception;

public final class ErrorMessages {

    private ErrorMessages() {

    }

    public static final String CUSTOMER_ALREADY_EXISTS = "Un client avec cet email existe déjà";
    public static final String CUSTOMER_NOT_FOUND = "Client introuvable pour l'identifiant fourni";
    public static final String INVALID_PASSWORD = "Mot de passe incorrect";
    public static final String DATABASE_ACCESS_ERROR = "Erreur d'accès à la base de données, veuillez réessayer plus tard";
    public static final String INTERNAL_SERVER_ERROR = "Erreur interne du serveur";
}
