package com.example.royalpizza.exception;

public final class ErrorMessages {

    private ErrorMessages() {

    }

    public static final String CUSTOMER_ALREADY_EXISTS = "Un client avec cet email existe déjà";
    public static final String CUSTOMER_NOT_FOUND = "Client introuvable pour l'identifiant fourni";
    public static final String INVALID_PASSWORD = "Mot de passe incorrect";
    public static final String INVALID_TOKEN = "Token invalide";
    public static final String INSUFFICIENT_BALANCE = "Fonds insuffisants pour effectuer cet achat. Veuillez recharger votre compte.";
    public static final String EXPIRED_TOKEN = "Le token a expiré, veuillez vous reconnecter";
    public static final String PRICE_NOT_FOUND = "Prix introuvable pour la pizza fournie";
    public static final String PIZZA_ALREADY_EXISTS = "Une pizza avec ce nom existe déjà";
    public static final String PIZZA_UNAVAILABLE = "La pizza indiquée n'est pas disponible";
    public static final String PIZZA_NOT_FOUND = "Pizza introuvable pour l'identifiant fourni";
    public static final String INGREDIENT_NOT_FOUND = "Ingrédient introuvable pour l'identifiant fourni";
    public static final String INGREDIENT_ALREADY_EXISTS = "Un ingrédient avec ce nom existe déjà";
    public static final String INTERNAL_SERVER_ERROR = "Erreur interne du serveur";
}
