# 🍕 Royal Pizza - Backend API

**[EN](#en) | [FR](#fr)**

## <a id="en"></a>English

Java/Spring Boot backend for the Royal Pizza pizza ordering platform.

---

### 📋 Table of Contents

1. [Architecture](#architecture-en)
2. [Database Structure](#database-structure-en)
3. [Authentication and JWT Tokens](#authentication-and-jwt-tokens-en)
4. [Installation and Startup](#installation-and-startup-en)
5. [API Endpoints](#api-endpoints-en)

---

### <a id="architecture-en"></a>🏗️ Architecture

The backend uses a **3-tier architecture** :
- **Controller** : REST entry points
- **Service** : Business logic
- **Repository** : Data access (JPA)
- **JPA Entities** : Mapping with PostgreSQL database

---

### <a id="database-structure-en"></a>📊 Database Structure

### Entity-Relationship Diagram

![alt text](image.png)
![alt text](image-2.png)
![alt text](image-1.png)

---

### <a id="authentication-and-jwt-tokens-en"></a>🔐 Authentication and JWT Tokens

#### Authentication Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENT LOGS IN                                           │
│    POST /api-backend/customers/login                        │
│    Sends: { email, password }                               │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. BACKEND VERIFIES                                         │
│    - Email exists in DB                                     │
│    - Password correct (bcrypt)                              │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. JWT TOKEN GENERATION                                     │
│    Header: { alg: "HS256", typ: "JWT" }                     │
│    Payload: {                                               │
│      sub: "email@example.com",                              │
│      id: 123,                                               │
│      isAdmin: true,                                         │
│      iat: 1702699200,                                       │
│      exp: 1702785600 (24h by default)                       │
│    }                                                         │
│    Signature: HMAC-SHA256(secret_key)                       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. RETURN TO CLIENT                                         │
│    {                                                         │
│      token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",     │
│      basket: { /* saved basket */ }                         │
│    }                                                         │
└─────────────────────────────────────────────────────────────┘
```

#### Login Response - JSON Structure

Here is the JSON structure returned upon successful authentication:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "basket": {
    "items": [
      {
        "pizzaId": 1,
        "name": "Margherita",
        "quantity": 2,
        "price": 12.50
      }
    ]
  }
}
```

#### Using the Token

For each protected request, the client sends :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Protected endpoints :**
- `GET /api-backend/customers/basket` - Get the basket
- `POST /api-backend/customers/saveBasket` - Save the basket
- `POST /api-backend/customers/update` - Update customer info
- `POST /api-backend/customers/updatePassword` - Change password
- `POST /api-backend/customers/walletRecharge` - Recharge wallet
- `POST /api-backend/customers/deleteAccount` - Delete account
- `GET /api-backend/customers/checkToken` - Verify token validity

The backend :
1. Extracts the token from the `Authorization` header
2. Validates the signature with the secret key
3. Verifies that the token is not expired
4. Retrieves the customer ID from claims
5. Accepts or rejects the request

#### JWT Configuration

JWT parameters are configurable in `application.properties` :
```properties
jwt.expiration=86400000  # 24 hours in milliseconds
```

The JWT secret is defined in the `JwtTokenManager` class.

#### Roles and Authorizations

- **USER** : Catalog viewing, order creation, wallet management
- **ADMIN** : Pizza management, ingredients, prices, users

---

### <a id="installation-and-startup-en"></a>🚀 Installation and Startup

#### Prerequisites : Docker Compose (Recommended)

To launch the backend and database with Docker Compose :

```bash
# 1. Clone the backend repository (if not already done)
git clone https://github.com/Royal-Pizza/backend.git

# 2. Clone the docker repository
git clone https://github.com/Royal-Pizza/docker.git
cd docker

# 3. Start the containers
docker compose -f docker-compose.yml up --build
```

This command :
- Launches a PostgreSQL container with initial database
- Launches a Spring Boot backend container
- Initializes tables from SQL schema
- Automatically configures data volumes

**If you encounter issues :** Consult the [docker repository README.md](https://github.com/Royal-Pizza/docker).

---

## <a id="fr"></a>Français

Backend Java/Spring Boot pour la plateforme de commande de pizzas Royal Pizza.

---

### 📋 Table des matières

1. [Architecture](#architecture-fr)
2. [Structure de la Base de Données](#structure-de-la-base-de-données-fr)
3. [Authentification et Tokens JWT](#authentification-et-tokens-jwt-fr)
4. [Installation et Démarrage](#installation-et-démarrage-fr)
5. [API Endpoints](#api-endpoints-fr)

---

### <a id="architecture-fr"></a>🏗️ Architecture

Le backend utilise une architecture **3-tiers** :
- **Controller** : Points d'entrée REST
- **Service** : Logique métier
- **Repository** : Accès aux données (JPA)
- **Entités JPA** : Mapping avec la base de données PostgreSQL

---

### <a id="structure-de-la-base-de-données-fr"></a>📊 Structure de la Base de Données

### Diagramme Entité-Association

![alt text](image.png)
![alt text](image-2.png)
![alt text](image-1.png)

---

### <a id="authentification-et-tokens-jwt-fr"></a>🔐 Authentification et Tokens JWT

#### Flux d'Authentification

```
┌─────────────────────────────────────────────────────────────┐
│ 1. CLIENT SE CONNECTE                                       │
│    POST /api-backend/customers/login                        │
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
│      basket: { /* panier sauvegardé */ }                    │
│    }                                                         │
└─────────────────────────────────────────────────────────────┘
```

#### Réponse de Login - Structure JSON

Voici la structure JSON retournée lors d'une authentification réussie :

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "basket": {
    "items": [
      {
        "pizzaId": 1,
        "name": "Margherita",
        "quantity": 2,
        "price": 12.50
      }
    ]
  }
}
```

#### Utilisation du Token

Pour chaque requête protégée, le client envoie :
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Endpoints protégés :**
- `GET /api-backend/customers/basket` - Récupérer le panier
- `POST /api-backend/customers/saveBasket` - Sauvegarder le panier
- `POST /api-backend/customers/update` - Mettre à jour les infos client
- `POST /api-backend/customers/updatePassword` - Changer le mot de passe
- `POST /api-backend/customers/walletRecharge` - Recharger le wallet
- `POST /api-backend/customers/deleteAccount` - Supprimer le compte
- `GET /api-backend/customers/checkToken` - Vérifier la validité du token

Le backend :
1. Extrait le token du header `Authorization`
2. Valide la signature avec la clé secrète
3. Vérifie que le token n'est pas expiré
4. Récupère l'ID du client depuis les claims
5. Accepte ou rejette la requête

#### Configuration JWT

Les paramètres JWT sont configurables dans `application.properties` :
```properties
jwt.expiration=86400000  # 24 heures en millisecondes
```

Le secret JWT est défini dans la classe `JwtTokenManager`.

#### Rôles et Autorisations

- **USER** : Consultation du catalogue, création de commandes, gestion du wallet
- **ADMIN** : Gestion des pizzas, des ingrédients, des prix, des utilisateurs

---

### <a id="installation-et-démarrage-fr"></a>🚀 Installation et Démarrage

#### Prérequis : Docker Compose (Recommandé)

Pour lancer le backend et la base de données avec Docker Compose :

```bash
# 1. Cloner le dépôt du backend (si ce n'est pas déjà fait)
git clone https://github.com/Royal-Pizza/backend.git

# 2. Cloner le dépôt docker
git clone https://github.com/Royal-Pizza/docker.git
cd docker

# 3. Lancer les conteneurs
docker compose -f docker-compose.yml up --build
```

Cette commande :
- Lance un conteneur PostgreSQL avec la base de données initiale
- Lance un conteneur du backend Spring Boot
- Initialise les tables à partir du schéma SQL
- Configure automatiquement les volumes de données

**En cas de problème :** Consulter le [README.md du dépôt docker](https://github.com/Royal-Pizza/docker).

