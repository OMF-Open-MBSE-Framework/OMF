# **Analyse des Classes MagicDraw SysML V2**

## **1. ElementRepresentation**
- **Mission :** Générer du texte représentant un élément SysML à partir de `RepresentationTextProviderService`.
- **Services :**
    - `getText(Element element)`: Génère une représentation textuelle avec ou sans couleur.
    - `getEssentialText(Element element)`: Génère un texte essentiel.
    - `getIcon(ModelElement element)`: Retourne une icône pour un élément.
- **Utilité pour notre besoin :** ✅ **Oui**, utile pour récupérer une représentation textuelle d'un élément SysML2.

---

## **2. ModelTextCreatorUtils**
- **Mission :** Manipuler du texte formaté pour la représentation d'éléments dans MagicDraw.
- **Services :**
    - Formatage des noms et des types en couleur.
    - Encapsulation de texte entre crochets, parenthèses.
    - Compression du texte.
- **Utilité pour notre besoin :** ⚠️ **Partiellement**, peut aider à structurer le DSL en format lisible.

---

## **3. TextualNotationBuilder**
- **Mission :** Transformer des expressions SysML en texte.
- **Services :**
    - `toText(Expression expression)`: Convertit une expression SysML en texte.
    - `toText(Namespace namespace)`: Transforme un namespace en texte (non supporté).
- **Utilité pour notre besoin :** ✅ **Oui**, pour transformer les expressions en DSL SysML v2.

---

## **4. ElementEditor<E extends Element>**
- **Mission :** Gérer l’édition d’un élément SysML dans un diagramme.
- **Services :**
    - `getPureTextForEditing()`: Récupère le texte brut d’un élément.
    - `parseText(...)`: Change le nom d’un élément.
    - `getModelElement()`: Retourne l’élément édité.
- **Utilité pour notre besoin :** ✅ **Oui**, permet d’obtenir l’élément dans l’éditeur DSL en live.

---

## **5. ElementSelectionRangeVisitor**
- **Mission :** Récupérer la plage de sélection d’un élément dans le DSL SysML v2.
- **Services :**
    - `getSelectionRange()`: Renvoie la plage de sélection.
    - `visitElement(Element element, SysMLVisitorContext context)`: Récupère la plage de sélection pour un élément.
- **Utilité pour notre besoin :** ✅ **Oui**, peut aider à retrouver quels éléments sont actuellement sélectionnés.

---

## **6. SysMLLanguageServices**
- **Mission :** Gestion des services liés au DSL SysML v2.
- **Services :**
    - `getSymbolsAsJsonString(...)`: Extrait les symboles d’un diagramme sous forme de JSON.
    - `getErrorsAsJsonString()`: Liste les erreurs dans le DSL.
    - `getDefinitionAsJsonString(offset)`: Récupère la définition d’un élément SysML.
    - `getReferences(offset)`: Trouve les références d’un élément dans le DSL.
- **Utilité pour notre besoin :** ✅ **Oui**, essentiel pour extraire les informations sur les éléments du DSL.

---

## **7. GeneratorUtil**
- **Mission :** Nettoyage et traitement du texte dans un DSL.
- **Services :**
    - `processCommentBody(String body)`: Nettoie les commentaires.
- **Utilité pour notre besoin :** ❌ **Non**, sauf pour la gestion des commentaires.

---

## **8. RuleNavigator**
- **Mission :** Naviguer dans la structure d’un arbre syntaxique (ANTLR).
- **Services :**
    - `child(int i)`: Accède à un enfant d’un nœud.
    - `parent()`: Remonte au parent.
    - `name(String iName)`: Vérifie si un nœud a un certain nom.
    - `getText()`: Récupère le texte d’un nœud.
- **Utilité pour notre besoin :** ✅ **Oui**, peut aider à naviguer dans la structure du DSL SysML v2.

---

## **9. sysml2textedita**
- **Mission :** Service de requêtes pour la gestion du texte SysML2 dans l’éditeur.
- **Services :**
    - `sysml2texteditK(Project var0)`: Récupère une instance du service.
    - `sysml2texteditx(sysml2texteditf var1)`: Ajoute une requête à traiter.
    - `sysml2texteditp()`: Exécute les requêtes en attente.
- **Utilité pour notre besoin :** ✅ **Oui**, facilite l’intégration avec l’éditeur DSL.

---

## **📌 Pistes de Solutions**

### **1. Extraction d’un élément et de son contexte**
- `ElementRepresentation` pour la représentation textuelle.
- `SysMLLanguageServices` pour récupérer les symboles et informations des éléments.
- `ElementEditor` pour éditer/récupérer les éléments visibles dans un diagramme.

### **2. Navigation sur les éléments proches**
- `ElementSelectionRangeVisitor` pour récupérer la plage de sélection.
- `RuleNavigator` pour explorer les relations dans le DSL.

### **3. Transformation en DSL SysML v2**
- `TextualNotationBuilder` pour convertir les expressions en texte.
- `SysMLLanguageServices.getSymbolsAsJsonString()` pour récupérer le texte du DSL existant.

### **4. Récupération des éléments présents et sélectionnés dans l’éditeur DSL**
- `sysml2textedita` pour interagir avec l’éditeur et gérer les requêtes.
- `SysMLLanguageServices.getDefinitionAsJsonString(offset)` pour retrouver les éléments à partir de l’éditeur.

---

Ces classes offrent des bases solides pour extraire, naviguer et convertir les éléments SysML en DSL SysML v2 dans MagicDraw. 📌 **Ce document sera mis à jour au fur et à mesure des découvertes.**

