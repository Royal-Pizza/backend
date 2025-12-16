# 🍕 Royal Pizza - Backend API

Backend Java/Spring Boot pour la plateforme de commande de pizzas Royal Pizza.

---

## 📋 Table des matières

1. [Architecture](#architecture)
2. [Structure de la Base de Données](#structure-de-la-base-de-données)
3. [Authentification et Tokens JWT](#authentification-et-tokens-jwt)
4. [Installation et Démarrage](#installation-et-démarrage)
5. [API Endpoints](#api-endpoints)

---

## 🏗️ Architecture

Le backend utilise une architecture **3-tiers** :
- **Controller** : Points d'entrée REST
- **Service** : Logique métier
- **Repository** : Accès aux données (JPA)
- **Entités JPA** : Mapping avec la base de données PostgreSQL

---

## 📊 Structure de la Base de Données

### Diagramme Entité-Association

![alt text](image.png)
![alt text](image-2.png)
![alt text](image-1.png)
---

## 🔐 Authentification et Tokens JWT

### Flux d'Authentification

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENT SE CONNECTE (POST /api/auth/login)                │
│    Envoie: { email, password }                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. BACKEND VÉRIFIE                                          │
│    - Email existe en DB                                     │
│    - Password correct (bcrypt)                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. GÉNÉRATION DU JWT TOKEN                                  │
│    Header: { alg: "HS256", typ: "JWT" }                     │
│    Payload: {                                               │
│      sub: "email@example.com",                              │
│      id: 123,                                               │
│      isAdmin: true,                                         │
│      iat: 1702699200,                                       │
│      exp: 1702785600 (24h par défaut)                       │
│    }                                                         │
│    Signature: HMAC-SHA256(secret_key)                       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. RETOUR AU CLIENT                                         │
│    {                                                         │
│      token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",     │
│      expiresIn: 86400,                                      │
│      customer: { id, firstName, lastName, email, isAdmin }  │
│    }                                                         │
└─────────────────────────────────────────────────────────────┘
```

### Utilisation du Token

Pour chaque requête protégée, le client envoie :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Le backend :
1. Extrait le token du header `Authorization`
2. Valide la signature avec la clé secrète
3. Vérifie que le token n'est pas expiré
4. Récupère les claims (email, id, rôle)
5. Accepte ou rejette la requête

### Configuration JWT

Les paramètres JWT sont configurables dans `application.properties` :
```properties
app.jwt.secret=votre_clé_secrète_très_longue
app.jwt.expiration=86400000  # 24 heures en millisecondes
app.jwt.refreshExpiration=604800000  # 7 jours
```

### Rôles et Autorisations

- **USER** : Consultation du catalogue, création de commandes, gestion du wallet
- **ADMIN** : Gestion des pizzas, des ingrédients, des prix, des utilisateurs

---

## 🚀 Installation et Démarrage

### Prérequis : Docker Compose (Recommandé)

Pour lancer le backend et la base de données avec Docker Compose :

```bash
git clone https://github.com/Royal-Pizza/docker.git
cd docker
docker compose -f docker-compose.yml up --build
```

Cette commande :
- Lance un conteneur PostgreSQL avec la base de données initiale
- Lance un conteneur du backend Spring Boot
- Initialise les tables à partir du schéma SQL
- Configure automatiquement les volumes de données

**En cas de problème :** Lire le README.md de ce repertoire git.