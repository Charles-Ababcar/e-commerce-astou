# Guide de Test pour l'Application Backend

Ce guide décrit la méthodologie et les outils utilisés pour tester l'application backend. L'objectif est d'assurer la fiabilité, la robustesse et la maintenabilité du code.

## 1. Philosophie de Test

Nous utilisons principalement des **tests d'intégration au niveau du contrôleur**. L'objectif n'est pas de tester les couches de service ou de repository de manière isolée (ce qui relèverait des tests unitaires purs), mais de tester le comportement des endpoints de l'API dans un environnement contrôlé.

Cette approche consiste à :
- **Lancer le contexte Spring** pour une partie de l'application (la couche web).
- **Envoyer de véritables requêtes HTTP** à nos contrôleurs.
- **Mocker les dépendances externes** (comme les services) pour isoler le contrôleur et maîtriser le scénario de test.

## 2. Outils Utilisés

- **JUnit 5** : Le framework de test standard pour les applications Java.
- **Spring Boot Test & MockMvc** : Outils fournis par Spring Boot pour tester les contrôleurs MVC. `MockMvc` nous permet de simuler des appels HTTP sans avoir besoin de lancer un véritable serveur.
- **Mockito** : Un framework de mocking puissant pour créer des substituts (mocks) de nos dépendances.
- **Hamcrest & JsonPath** : Pour écrire des assertions claires et précises sur les réponses JSON retournées par l'API.

## 3. Structure d'un Test de Contrôleur

Prenons un exemple concret tiré du projet, comme `OrderControllerTest`.

### a. Annotations de la Classe

```java
@WebMvcTest(OrderController.class)
@WithMockUser
public class OrderControllerTest {
    // ...
}
```
- `@WebMvcTest(OrderController.class)`: C'est une annotation "slice" de Spring Boot. Elle ne charge que la couche web (le `OrderController` dans ce cas) et les beans nécessaires à son fonctionnement (comme `Filter`s, `JsonSerializer`, etc.). C'est beaucoup plus rapide que de charger l'application entière.
- `@WithMockUser`: Puisque nos endpoints sont sécurisés, cette annotation simule la présence d'un utilisateur authentifié dans le contexte de sécurité de Spring. Cela nous permet de passer les filtres de sécurité sans avoir à gérer manuellement la création d'un token JWT.

### b. Déclaration des Mocks et Beans

```java
@Autowired
private MockMvc mockMvc;

@MockBean
private OrderService orderService;

@MockBean
private JwtUtil jwtUtil; // Mocké car c'est une dépendance du filtre de sécurité

@Autowired
private ObjectMapper objectMapper;
```
- `MockMvc`: L'objet principal pour exécuter nos requêtes de test. Il est injecté par Spring grâce à `@WebMvcTest`.
- `@MockBean`: Cette annotation crée une version "mock" (factice) du `OrderService`. Au lieu d'exécuter la vraie logique du service, nous pourrons lui dicter son comportement pour chaque test (par exemple, "quand la méthode `getAllOrders` est appelée, retourne cette liste de commandes").
- `ObjectMapper`: Utile pour sérialiser nos objets de requête en JSON.

### c. Anatomie d'une Méthode de Test

Analysons un test qui récupère la liste des commandes.

```java
@Test
public void testGetAllOrders() throws Exception {
    // 1. Préparation (Given)
    Order order = new Order();
    order.setId(UUID.randomUUID().toString());
    Page<Order> orderPage = new PageImpl<>(Collections.singletonList(order));

    given(orderService.getAllOrders(any(PageRequest.class))).willReturn(
        new ApiResponse<>(true, "Orders retrieved successfully", orderPage)
    );

    // 2. Action (When) & 3. Assertion (Then)
    mockMvc.perform(get("/api/orders?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[0].id").value(order.getId()));
}
```

#### Étape 1: Préparation (Given)

C'est ici que nous définissons le scénario.
- Nous créons les objets de données attendus (une page contenant une commande).
- Nous utilisons `given(...).willReturn(...)` de Mockito pour configurer notre mock `orderService`. Nous lui disons : "Lorsque ta méthode `getAllOrders` est appelée avec n'importe quel `PageRequest`, tu dois retourner cet `ApiResponse` que j'ai préparé".

#### Étape 2: Action (When)

C'est l'exécution de la requête.
- `mockMvc.perform(...)` envoie la requête.
- `get("/api/orders?page=0&size=10")` simule un `GET` sur l'endpoint avec des paramètres de pagination.

#### Étape 3: Assertion (Then)

C'est la vérification des résultats.
- `.andExpect(status().isOk())`: Nous vérifions que le code de statut HTTP de la réponse est bien 200 (OK).
- `.andExpect(jsonPath("$.success").value(true))`: Nous utilisons `jsonPath` pour inspecter le corps de la réponse JSON. Ici, nous vérifions que le champ `success` à la racine (`$`) du JSON est à `true`.
- `.andExpect(jsonPath("$.data.content[0].id").value(order.getId()))`: Nous allons plus loin dans le JSON pour vérifier que l'ID de la première commande dans la liste correspond bien à celui que nous avions préparé.

## 4. Comment Lancer les Tests ?

Vous pouvez lancer tous les tests du projet en exécutant la commande Maven suivante à la racine du projet :

```bash
mvn test
```

Maven trouvera automatiquement toutes les classes de test, les exécutera et produira un rapport. Si un test échoue, la construction échouera, ce qui est un filet de sécurité essentiel dans notre processus d'intégration continue.
