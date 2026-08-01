# Architecture du système d'annotation — WereWolfAPI / WereWolfPlugin

Ce document décrit comment fonctionne le **système d'annotation** qui permet d'enregistrer les éléments constitutifs du
jeu : rôles, scénarios, timers, configurations, amoureux (lovers), événements aléatoires (random events), commandes,
modules/addons et événements de statistiques.

Il couvre les deux projets :

- **WereWolfAPI** (`fr.ph1lou.werewolfapi`, v1.12) — module *API pur* qui définit les **contrats** : annotations,
  interfaces de registre, classes abstraites de base. Ne contient **aucune** classe annotée concrète (à l'exception de
  ses propres events de statistiques).
- **WereWolfPlugin** (`fr.ph1lou.werewolfplugin`, v1.13.1) — plugin Minecraft (Spigot/Paper 1.21.4, Java 8) qui
  **implémente** l'API : il fournit toutes les classes annotées concrètes (rôles, scénarios, etc.), le **scanning**
  du classpath et l'implémentation du registre.

> Le plugin ne définit **aucune** annotation : il **consomme** uniquement celles
> de l'API. Le système d'annotation vit donc dans l'API ; son exécution (scan,
> registre, instanciation) vit dans le plugin.

---

## 1. Vue d'ensemble — les 3 couches

Le système repose sur trois couches distinctes :

```
┌─────────────────────────────────────────────────────────────────────────┐
│  COUCHE 1 — DÉCLARATION (WereWolfAPI)                                   │
│  fr.ph1lou.werewolfapi.annotations                                      │
│  22 annotations @Retention(RUNTIME) : @Role, @Scenario, @Timer,         │
│  @Configuration, @ConfigurationBasic, @IntValue, @Lover, @RandomEvent,  │
│  @AdminCommand, @PlayerCommand, @RoleCommand, @ModuleWerewolf, ...      │
└─────────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ posées sur des classes concrètes
                                   ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  COUCHE 2 — ENREGISTREMENT (WereWolfPlugin)                             │
│  Register.java  +  ReflectionUtils.java                                 │
│  Au démarrage (Main.onEnable) : scan de tous les jars Bukkit annotés    │
│  @ModuleWerewolf, lecture des .class via JarFile, introspection,        │
│  stockage dans Set<Wrapper<Classe, Annotation>> indexés par type.       │
└─────────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ accès via GetWereWolfAPI.getRegisterManager()
                                   ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  COUCHE 3 — RUNTIME (WereWolfPlugin)                                    │
│  Instanciation + lecture réflexive au moment voulu :                    │
│   • ListenersManager   → scenarios / timers / configs / random events    │
│   • RoleDuration       → rôles (assignation aux joueurs)                 │
│   • Admin / Command    → commandes admin / joueur / rôle                 │
│   • GameTask           → décrément des timers + fire des events onZero   │
│   • StatisticsEvents   → lecture des @Statistics* sur les events reçus   │
│   • StorageConfiguration → valeurs par défaut des timers/configs/IntVals │
└─────────────────────────────────────────────────────────────────────────┘
```

L'usage intensif de la réflexion est masqué derrière le conteneur
`Wrapper<T, U>` et l'interface `IRegisterManager`, ce qui permet à un **addon tiers** de définir ses propres
rôles/scénarios/etc. simplement en annotant ses classes dans un plugin Bukkit décoré de `@ModuleWerewolf`.

---

## 2. Le flux d'enregistrement au démarrage

### 2.1 Point d'entrée

`WereWolfPlugin/src/main/java/fr/ph1lou/werewolfplugin/Main.java` est la classe principale. Elle est elle-même annotée
`@ModuleWerewolf` (donc le plugin principal se scanne lui-même comme un module) :

```java

@ModuleWerewolf(key = Main.KEY,
        loreKeys = "werewolf.description_plugin",
        item = UniversalMaterial.ANVIL,
        defaultLanguage = "fr_FR",
        authors = @Author(uuid = "056be797-2a0b-4807-9af5-37faf5384396", name = "Ph1Lou"))
public class Main extends JavaPlugin implements GetWereWolfAPI { ...
}
```

Dans `onEnable()` (`Main.java:64-89`), un `scheduleSyncDelayedTask` construit le registre :

```java
Bukkit.getScheduler().

scheduleSyncDelayedTask(this,() ->{
        this.registerManager =new

Register(this);   // scan complet du classpath
    ...
            });
```

Le plugin publie ensuite l'instance d'API dans le `ServicesManager` Bukkit (`Main.java:66-70`), ce qui permet aux addons
tiers de la récupérer :

```java
Bukkit.getServicesManager().

register(GetWereWolfAPI .class, this,this,ServicePriority.Normal);
```

### 2.2 Le scan — `Register.java`

`WereWolfPlugin/src/main/java/fr/ph1lou/werewolfplugin/Register.java` (~470 lignes)
est la classe centrale de l'enregistrement. Elle implémente l'interface
`IRegisterManager` de l'API.

**Étape 1 — `scanPlugins(Main)` (L59-97)** : itère sur **tous les plugins Bukkit chargés**
(`Bukkit.getPluginManager().getPlugins()`), ne retient que ceux annotés `@ModuleWerewolf`. Vérifie que la `key` ne
commence pas par
`werewolf.` (préfixe réservé au plugin officiel) et que le préfixe avant le `.`
est unique entre modules.

**Étape 2 — `register(packageName, moduleWerewolf, plugin, prefix)` (L103-295)** :
appelle `ReflectionUtils.findAllClasses(plugin, packageName)` puis, pour chaque classe, applique cet enchaînement
(`if/else if`) :

```java
if(clazz.getAnnotation(DisableAutoLoad .class) !=null){return;}      // opt-out

        if(clazz.

getAnnotation(Role .class) !=null&&IRole .class.

isAssignableFrom(clazz))
        this.roles.

add(new Wrapper<>((Class<IRole>) clazz,role,addon.

key()));
        else if(clazz.

getAnnotation(Scenario .class) !=null&&ListenerWerewolf .class.

isAssignableFrom(clazz))
        this.scenarios.

add(...);
else if(clazz.

getAnnotation(RandomEvent .class) !=null)...
        else if(clazz.

getAnnotation(Configuration .class) !=null)...
        else if(clazz.

getAnnotation(Timer .class) !=null)...
        else if(clazz.

getAnnotation(PlayerCommand .class) !=null)...
        else if(clazz.

getAnnotation(RoleCommand .class) !=null)...
        else if(clazz.

getAnnotation(AdminCommand .class) !=null)...
        else if(clazz.

getAnnotation(Lover .class) !=null)...
        else if(clazz.

getAnnotation(StatisticsEvent .class) !=null)...
```

**Deux conditions obligatoires** pour qu'une classe soit enregistrée :

1. la `key` (ou `config().key()`) commence par le **préfixe du module** (sinon warning + skip) ;
2. la classe **implémente/étend le type attendu** (ex. `IRole` pour `@Role`,
   `ListenerWerewolf` pour `@Scenario`, `Event` pour `@StatisticsEvent`). Sinon : warning + skip.

Le résultat est stocké dans dix `Set<Wrapper<T, Annotation>>` (un par type, déclarés L41-52) puis exposés via
`IRegisterManager`.

> Particularité : pour le plugin principal, le package `fr.ph1lou.werewolfapi`
> est **aussi** scanné en plus du package du plugin (L89-94), afin que l'API
> puisse enregistrer ses propres events de statistiques.

### 2.3 Le scan de jar — `ReflectionUtils.java`

`WereWolfPlugin/src/main/java/fr/ph1lou/werewolfplugin/utils/ReflectionUtils.java`

**Aucune librairie tierce** (pas de Reflections, ClassGraph, ASM…). Le scan lit directement le jar via
`java.util.jar.JarFile` :

```java
URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
JarURLConnection jarConnection = (JarURLConnection)
        new URL("jar", "", "file:" + jarUrl.getPath() + "!/").openConnection();
try(
JarFile jarFile = jarConnection.getJarFile()){
        for(
JarEntry entry :Collections.

list(jarFile.entries())){
        if(name.

endsWith(".class") &&name.

startsWith(path)){
        classes.

add(Class.forName(className, false,plugin.getClass().

getClassLoader()));
        }
        }
        }
```

`Class.forName(..., /*initialize=*/ false, ...)` : l'initialisation statique des classes scannées est **différée**
jusqu'à leur instanciation effective.

---

## 3. Le registre — `IRegisterManager` & `Wrapper`

### 3.1 Le conteneur `Wrapper<T, U>`

`WereWolfAPI/src/main/java/fr/ph1lou/werewolfapi/utils/Wrapper.java`

Paire immutable `(Class<T> clazz, U annotation, String addonKey)` :

| Getter           | Renvoie                                                              |
|------------------|----------------------------------------------------------------------|
| `getClazz()`     | la classe concrète annotée (à instancier par réflexion)              |
| `getMetaDatas()` | l'annotation (métadonnées déclarées : key, valeurs par défaut, etc.) |
| `getAddonKey()`  | la clé du module/addon propriétaire                                  |

### 3.2 L'interface `IRegisterManager`

`WereWolfAPI/src/main/java/fr/ph1lou/werewolfapi/registers/IRegisterManager.java`

C'est le **contrat central** du registre. Chaque genre d'élément expose un
`Set<Wrapper<TypeConcret, TypeAnnotation>>` :

| Méthode                       | Type retourné                                 | Annotation         |
|-------------------------------|-----------------------------------------------|--------------------|
| `getRolesRegister()`          | `Set<Wrapper<IRole, Role>>`                   | `@Role`            |
| `getLoversRegister()`         | `Set<Wrapper<ILover, Lover>>`                 | `@Lover`           |
| `getScenariosRegister()`      | `Set<Wrapper<ListenerWerewolf, Scenario>>`    | `@Scenario`        |
| `getConfigsRegister()`        | `Set<Wrapper<?, Configuration>>`              | `@Configuration`   |
| `getTimersRegister()`         | `Set<Wrapper<?, Timer>>`                      | `@Timer`           |
| `getPlayerCommandsRegister()` | `Set<Wrapper<ICommand, PlayerCommand>>`       | `@PlayerCommand`   |
| `getRoleCommandsRegister()`   | `Set<Wrapper<ICommandRole, RoleCommand>>`     | `@RoleCommand`     |
| `getAdminCommandsRegister()`  | `Set<Wrapper<ICommand, AdminCommand>>`        | `@AdminCommand`    |
| `getModulesRegister()`        | `Set<Wrapper<JavaPlugin, ModuleWerewolf>>`    | `@ModuleWerewolf`  |
| `getRandomEventsRegister()`   | `Set<Wrapper<ListenerWerewolf, RandomEvent>>` | `@RandomEvent`     |
| `getEventsClass()`            | `Set<Wrapper<Event, StatisticsEvent>>`        | `@StatisticsEvent` |

### 3.3 Accès au registre

Depuis n'importe quel addon tiers :

```java
GetWereWolfAPI api = Bukkit.getServer().getServicesManager().load(GetWereWolfAPI.class);
IRegisterManager registry = api.getRegisterManager();
Set<Wrapper<IRole, Role>> roles = registry.getRolesRegister();
```

`GetWereWolfAPI` (`WereWolfAPI/.../GetWereWolfAPI.java:16`) est l'interface implémentée par `Main`, obtenue via
`BukkitUtils.loadAPI()`
(`WereWolfAPI/.../utils/BukkitUtils.java:22`).

---

## 4. Les annotations — référence exhaustive

Toutes définies dans `WereWolfAPI/src/main/java/fr/ph1lou/werewolfapi/annotations/`
(sous-package `statistics/` pour les 6 dernières). Sauf mention contraire :
`@Retention(RUNTIME)` et `@Target(TYPE)`.

### 4.1 Annotations d'enregistrement d'éléments de jeu

#### `@Role` — `annotations/Role.java:14`

```java
public @interface Role {
    String key();                              // obligatoire (clé i18n)

    String[] loreKey() default {};

    Category category();                       // WEREWOLF | VILLAGER | NEUTRAL

    RoleAttribute attribute();                 // VILLAGER | MINOR_INFORMATION | INFORMATION | WEREWOLF | HYBRID | NEUTRAL

    float weight() default 1;

    boolean requireDouble() default false;

    String[] requireRoles() default {};

    String[] incompatibleRoles() default {};

    IntValue[] configValues() default {};      // configs entières propres au rôle

    Configuration[] configurations() default {}; // configs booléennes propres au rôle

    Timer[] timers() default {};               // timers propres au rôle

    String auraDescriptionSpecialUseCase() default "";

    Aura defaultAura() default Aura.LIGHT;     // LIGHT | NEUTRAL | DARK

    int powerModifier() default 0;

    int sharpnessIronModifier() default 0;

    int sharpnessDiamondModifier() default 0;

    int protectionIronModifier() default 0;

    int protectionDiamondModifier() default 0;
}
```

> `@Role` peut **imbriquer** ses propres `@IntValue`/`@Configuration`/`@Timer` :
> un rôle est donc *self-contained*.

#### `@Scenario` — `annotations/Scenario.java:10`

```java
public @interface Scenario {
    String key();

    String[] loreKey() default {};

    boolean defaultValue() default false;

    boolean meetUpValue() default false;

    String[] incompatibleScenarios() default {};

    IntValue[] configValues() default {};

    Configuration[] configurations() default {};

    Timer[] timers() default {};
}
```

#### `@Timer` — `annotations/Timer.java:12`

```java
public @interface Timer {
    String key();

    int defaultValue();

    int meetUpValue();

    String[] loreKey() default {};

    boolean decrementAfterRole() default false;

    String decrementAfterTimer() default "";

    boolean decrement() default false;

    int step() default 5;

    Class<? extends Event> onZero() default Event.class;   // event Bukkit fire à 0
}
```

> `@Timer` sert à la fois d'annotation top-level (sur une classe) et de
> **membre imbriqué** dans `@Role` / `@Scenario` / `@Configuration` /
> `@RandomEvent` / `@Lover`.

#### `@Configuration` + `@ConfigurationBasic` — `annotations/Configuration.java:10` & `ConfigurationBasic.java:10`

```java
public @interface Configuration {
    ConfigurationBasic config();              // obligatoire

    IntValue[] configValues() default {};

    Timer[] timers() default {};

    ConfigurationBasic[] configurations() default {};   // sous-configs
}

public @interface ConfigurationBasic {
    String key();

    String[] loreKey() default {};

    boolean defaultValue() default false;

    boolean meetUpValue() default false;

    boolean appearInMenu() default true;

    boolean appearInConfigurationList() default true;

    String[] incompatibleConfigs() default {};
}
```

#### `@IntValue` — `annotations/IntValue.java:12`

```java
public @interface IntValue {
    String key();

    int defaultValue();

    int meetUpValue();

    int step();

    UniversalMaterial item();                  // icône de menu
}
```

#### `@Lover` — `annotations/Lover.java:10`

```java
public @interface Lover {
    String key();

    String color();

    String[] loreKey() default {};

    String[] requireRoles() default {};

    String[] incompatibleRoles() default {};

    IntValue[] configValues() default {};

    Configuration[] configurations() default {};

    Timer[] timers() default {};
}
```

#### `@RandomEvent` — `annotations/RandomEvent.java:10`

```java
public @interface RandomEvent {
    String key();

    String[] loreKey() default {};

    int defaultValue() default 1;

    int meetUpValue() default 1;

    IntValue[] configValues() default {};

    Timer[] timers() default {};

    Configuration[] configurations() default {};
}
```

#### `@ModuleWerewolf` — `annotations/ModuleWerewolf.java:12`

Déclare un plugin Bukkit comme **module / addon** werewolf (c'est la racine nécessaire pour qu'un plugin soit scanné).

```java
public @interface ModuleWerewolf {
    String key();

    String[] loreKeys();

    UniversalMaterial item();

    String defaultLanguage();

    Author[] authors();

    IntValue[] configValues();
}
```

#### `@Author` — `annotations/Author.java`

Membre imbriqué de `@ModuleWerewolf` : `String uuid()`, `String name()`. (Pas de `@Retention`/`@Target` explicites.)

#### `@DisableAutoLoad` — `annotations/DisableAutoLoad.java:10`

`boolean isDisable() default true`. Opt-out : la classe annotée ne sera **pas**
scannée/enregistrée.

### 4.2 Annotations de commandes

| Annotation       | Fichier                 | Membres principaux                                                                                                                                       |
|------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@AdminCommand`  | `AdminCommand.java:12`  | `key`, `descriptionKey`, `statesGame`, `argNumbers`, `moderatorAccess`, `hostAccess`, `autoCompletion`                                                   |
| `@PlayerCommand` | `PlayerCommand.java:13` | `key`, `descriptionKey`, `statesPlayer`, `statesGame`, `argNumbers`, `autoCompletion`                                                                    |
| `@RoleCommand`   | `RoleCommand.java:13`   | `key`, `roleKeys`, `statesPlayer`, `statesGame`, `argNumbers`, `autoCompletion`, `requiredPower`, `requiredAbilityEnabled`, `requiredLimitedUse`, `args` |

### 4.3 Marqueurs d'events (annotations vides)

| Annotation            | Fichier                      | Rôle                                                                       |
|-----------------------|------------------------------|----------------------------------------------------------------------------|
| `@SpyEvent`           | `SpyEvent.java:11`           | Marque un event éligible au replay du rôle Spy → déclenche `SpyInfoEvent`  |
| `@TellableStoryEvent` | `TellableStoryEvent.java:11` | Marque un event éligible au StoryTeller → déclenche `ActionableStoryEvent` |

### 4.4 Annotations de statistiques (package `annotations/statistics/`)

Toutes `@Retention(RUNTIME)`, cibles `METHOD` (sauf `@StatisticsEvent` qui vise `TYPE`). Posées sur des **méthodes** de
classes d'event Bukkit.

| Annotation             | Cible  | Rôle                                                                         |
|------------------------|--------|------------------------------------------------------------------------------|
| `@StatisticsEvent`     | TYPE   | `String key()` — clé de l'event pour les stats                               |
| `@StatisticsPlayer`    | METHOD | getter renvoyant l'`IPlayerWW` acteur (1 max/classe)                         |
| `@StatisticsTarget`    | METHOD | getter renvoyant un `IPlayerWW` cible unique                                 |
| `@StatisticsTargets`   | METHOD | getter renvoyant un `Set<IPlayerWW>` de cibles (exclusif avec la précédente) |
| `@StatisticsExtraInfo` | METHOD | getter renvoyant un `String` complémentaire                                  |
| `@StatisticsExtraInt`  | METHOD | getter renvoyant un `int` complémentaire                                     |

---

## 5. Détail par type d'élément enregistré

### 5.1 Rôles — `@Role`

**Hiérarchie** (côté API) : `IRole` → `RoleImpl` (abstraite) →
`RoleWereWolf` / `RoleNeutral` (abstraites) → **classe concrète du plugin**.

**Localisation** :
`WereWolfPlugin/src/main/java/fr/ph1lou/werewolfplugin/roles/` avec sous-dossiers
`werewolfs/`, `villagers/`, `neutrals/`, `lovers/`, `specials/`.

**Exemple simple** — `roles/werewolfs/WereWolf.java:15` :

```java

@Role(key = RoleBase.WEREWOLF,
        defaultAura = Aura.DARK,
        category = Category.WEREWOLF,
        attribute = RoleAttribute.WEREWOLF)
public class WereWolf extends RoleWereWolf { ...
}
```

**Exemple avec configs imbriquées** — `roles/villagers/Seer.java:30` :

```java

@Role(key = RoleBase.SEER,
        category = Category.VILLAGER,
        attribute = RoleAttribute.INFORMATION,
        configurations = {
                @Configuration(config = @ConfigurationBasic(key = ConfigBase.SEER_EVERY_OTHER_DAY)),
                @Configuration(config = @ConfigurationBasic(key = ConfigBase.CHATTY_SEER))
        })
public class Seer extends RoleWithLimitedSelectionDuration implements IAffectedPlayers { ...
}
```

**Exemple avec timer + IntValue imbriqués** — `roles/villagers/Poacher.java:40` :

```java

@Role(key = RoleBase.POACHER, category = Category.VILLAGER,
        defaultAura = Aura.NEUTRAL,
        attribute = RoleAttribute.VILLAGER,
        timers = {@Timer(key = TimerBase.POACHER_PROGRESS, defaultValue = 10, meetUpValue = 10)},
        configValues = {
                @IntValue(key = IntValueBase.POACHER_DISTANCE, defaultValue = 10,
                        meetUpValue = 10, step = 1, item = UniversalMaterial.ARROW)
        })
public class Poacher extends ...{...}
```

**Lecture réflexive (côté API)** — `RoleImpl` relit l'annotation sur
`this.getClass()` :

- `getKey()` (`role/impl/RoleImpl.java:87`) :
  ```java
  Role role = this.getClass().getAnnotation(Role.class);
  if (role == null) {
      Bukkit.getLogger().severe("The class " + this.getClass().getName()
          + " has not been annotated by the role annotation");
      return this.getClass().getName();
  }
  return role.key();
  ```
- `getDefaultAura()` (`role/impl/RoleImpl.java:263`) : `role.defaultAura()`.
- `DescriptionBuilder` (`role/utils/DescriptionBuilder.java:72`) lit
  `powerModifier`, `sharpnessIron/DiamondModifier`, `protectionIron/DiamondModifier`.

**Instanciation (côté plugin)** — `timers/RoleDuration.java:99` (assignation des rôles aux joueurs au début de partie) :
constructeur réflexif
`(WereWolfAPI, IPlayerWW)` :

```java
role =roleRegister.

getClazz()
        .

getConstructor(WereWolfAPI .class, IPlayerWW .class)
        .

newInstance(game, playerWW);
BukkitUtils.

registerListener(role);
playerWW.

setRole(role);
```

> ⚠️ **Convention de constructeur** : toute classe de rôle doit exposer un
> constructeur public `(WereWolfAPI game, IPlayerWW playerWW)`.

#### Hiérarchie & classes de base

```
IRole (interface) ──extends──> RoleImpl (abstraite)
                                  ├── RoleWereWolf        (isWereWolf() = true)
                                  ├── RoleNeutral         (isNeutral() selon isWereWolf/transformedToVillager)
                                  └── RoleWithLimitedSelectionDuration (implémente IPower + SelectionEndEvent)
```

| Classe de base                     | Quand l'étendre                             | Particularité                                                 |
|------------------------------------|---------------------------------------------|---------------------------------------------------------------|
| `RoleImpl`                         | Rôle villageois « pur » sans transformation | `isWereWolf()` = `infected` (faux par défaut)                 |
| `RoleWereWolf`                     | Rôle loup-garou natif                       | `isWereWolf()` = `true` (hardcodé)                            |
| `RoleNeutral`                      | Rôle neutre (victoire solo)                 | `isNeutral()` = `!isWereWolf() && !isTransformedToVillager()` |
| `RoleWithLimitedSelectionDuration` | Pouvoir unique avec durée de sélection      | Gère déjà `IPower` + intercepte `SelectionEndEvent`           |

#### Interfaces-marqueurs (à implémenter sur la classe concrète)

Interfaces **vides ou quasi-vides** qui signalent au framework (et aux commandes)
une capacité du rôle. Détectées par `instanceof` partout dans le cœur ; le simple fait d'implémenter suffit, **aucun
enregistrement**.

| Interface          | Méthodes                                                 | Effet framework                                                                                          |
|--------------------|----------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| `IPower`           | `hasPower()` / `setPower(boolean)`                       | Permet `@RoleCommand(requiredPower = true)` (le dispatcher refuse la commande si `hasPower()` est faux). |
| `IAffectedPlayers` | `add/remove/clearAffectedPlayer`, `getAffectedPlayers()` | Active `@PlayerArg(rejectIfAlreadyAffected = true)` (§10.4) et expose les cibles mémorisées.             |
| `ILimitedUse`      | `getUse()` / `setUse(int)`                               | Compteur d'utilisations ; `@RoleCommand(requiredLimitedUse = N)` couvre les limites fixes.               |
| `IProgress`        | `getProgress()` / `setProgress(float)`                   | Progression 0..1 (ex. Braconnier).                                                                       |
| `ITransformed`     | `isTransformed()` / `setTransformed(boolean)`            | Ajoute la ligne « Transformé : oui/non » dans la description (`DescriptionBuilder`).                     |
| `IInvisible`       | `isInvisible()` / `setInvisible(boolean)`                | Masque le joueur des rôles à infos (BearTrainer, Fox, …).                                                |
| `IGuesser`         | `canGuess`, `resolveGuess`, `getAvailableCategories`     | Rôle « devineur » (Corbeau/Sœur).                                                                        |

#### Cycle de vie d'un rôle

```
RoleDuration.onRepartition (RepartitionEvent)        ← RoleDuration.java:101
   │  role = clazz.getConstructor(WereWolfAPI, IPlayerWW).newInstance(game, playerWW)
   │  BukkitUtils.registerListener(role)             ← le rôle écoute ses propres @EventHandler
   │  playerWW.setRole(role)
   │  Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(playerWW))
   ▼
roleAnnouncement(playerWW)                           ← RoleDuration.java:50
   │  playerWW.sendMessage( role.getDescription() )
   │  role.recoverPotionEffects()                    ← RoleDuration.java:58 (force/night vision pour loups)
   │  role.recoverPower()                             ← RoleDuration.java:59 (init du pouvoir, bonus PV, ...)
   ▼
runtime : les @EventHandler du rôle réagissent aux events Bukkit
   │   (garder `isAbilityEnabled()` / `isState(ALIVE)` en tête de chaque handler)
   └─ role.disableAbilities() / enableAbilities()    ← isAbilityEnabled() coupe l'effet des handlers
```

`recoverPower()` / `recoverPotionEffects()` sont aussi rappelés après un changement de rôle : Thief (`:190`), Scammer
(`:169`), Imitator (`:177`), DevotedServant (`:193`), Swap (`:82-85`).

#### Camp, `isWereWolf()` et couche d'affichage

- `getCamp()` (`RoleImpl.java:111`) est **dérivé** : `NEUTRAL` si `isNeutral()`, sinon `WEREWOLF` si `isWereWolf()`,
  sinon `VILLAGER`. **Ne pas l'override** ; overrider plutôt `isWereWolf()` / `isNeutral()`.
- `isWereWolf()` (`RoleImpl.java:144`) vaut `this.infected` par défaut.
  `RoleWereWolf` le force à `true`. Un rôle peut l'override pour une transformation conditionnelle (ex.
  `WhiteWereWolf.java:43`,
  `AmnesicWerewolf.java:148`, `RomulusRemus.java:129`).
- **Affichage menteur** : `getDisplayCamp()` / `getDisplayRole()`
  (`RoleImpl.java:226` / `:239`) permettent de mentir à un rôle à info **sans**
  changer le camp réel.
- **Liste des loups** : l'event `AppearInWereWolfListEvent`
  (`events/werewolf/AppearInWereWolfListEvent.java:8`) décide si un joueur **apparaît** dans la liste des loups (vue
  `/ww list` des loups, tab). Le handler par défaut `WerewolfListener.onAppearInWereWolfList`
  (`WerewolfListener.java:82`, priorité `LOWEST`) met `appear = isWereWolf()`. Un rôle qui veut faire apparaître un
  **faux loup** surcharge l'event en priorité `NORMAL` (donc **après**) via `event.setAppear(true)` — c'est exactement
  la technique de l'Illusionniste (§8.6).

#### Rôle d'affichage vs rôle de mort

« Mentir » sur le rôle d'un joueur se fait sur **deux couches indépendantes**, qu'il faut distinguer :

| Couche               | API                                                                                                 | Vue par                                                     | Défaut                                                                       |
|----------------------|-----------------------------------------------------------------------------------------------------|-------------------------------------------------------------|------------------------------------------------------------------------------|
| **Affichage vivant** | `getDisplayRole()` / `setDisplayRole(key)`, `getDisplayCamp()` / `setDisplayCamp(key)` (`IDisplay`) | rôles à info (Voyante, Renifleur, …), `getDescription`, tab | `getKey()` / `getCamp().getKey()` si non surchargé                           |
| **Rôle de mort**     | `getDeathRole()` / `addDeathRole(key)` / `removeDeathRole(key)` (`IPlayerWW`)                       | message de mort (`AnnouncementDeathEvent`)                  | `role.getKey()` si la liste `deathRoles` est vide, sinon `deathRoles.get(0)` |

> ⚠️ `setDisplayRole(...)` **ne change pas** le rôle affiché à la mort :
> `AnnouncementDeathEvent` lit `getDeathRole()`, pas `getDisplayRole()`. Pour
> falsifier le message de mort, il faut soit `addDeathRole(facade)` (la façade
> devient `getDeathRole()`), soit intercepter `AnnouncementDeathEvent` et faire
> `event.setRole(facade)`.

**`AnnouncementDeathEvent`** (`events/game/life_cycle/AnnouncementDeathEvent.java`)
est fire **une fois par spectateur** par `DeathListener.onFinalDeath`
(`DeathListener.java:198`) avec un champ `role` (défaut = `getDeathRole()`), modifiable via `setRole(String)`. Des
configs/rôles le modifient en `HIGH` :
`Honor.onAnnouncementDeathEvent` (`Honor.java:130`) et `Priestess`
(`Priestess.java:137`) remplacent aléatoirement le rôle par « magic ». Pour qu'une falsification **prime** sur ces
masques aléatoires, intercepter en
`EventPriority.HIGHEST` (après `HIGH`) — technique du Faussaire.

**Save/restore de l'affichage** : `getDisplayRole()` renvoie `getKey()` quand le champ interne est `null` (non
distinguable d'une valeur fixée à `getKey()`). Pour restaurer fidèlement un joueur dont on a temporairement changé
l'affichage, on sauvegarde la valeur **effective** (`getDisplayRole()`) et on la réinjecte via
`setDisplayRole(...)` — le résultat observable est identique à l'état `null`
d'origine. `clearDisplay()` remet les deux champs à `null` (retour au réel).

#### Modificateurs d'aura nommés

`getAura()` (`RoleImpl.java:252`) renvoie l'aura du modificateur de **plus haute priorité**, sinon `getDefaultAura()`.
Les modificateurs se gèrent par **nom** :

```java
role.addAuraModifier(new AuraModifier("mon_role_facade", Aura.DARK, 1,false));  // priorité 1
        role.

removeAuraModifier("mon_role_facade");                                       // retrait par nom
```

`addAuraModifier` retire d'abord tout modificateur de même nom (donc l'ajout est idempotent). `removeTemporaryAuras()`
ne retire que ceux marqués `temporary =
true`. Un rôle qui modifie temporairement l'aura d'un **autre** joueur utilise un nom de modificateur qui lui est propre
(ex. `"counterfeiter_werewolf_facade"`)
pour ne pas interférer avec les autres auras de la cible.

#### `isWereWolf()` vs death role — asymétries clés

`isWereWolf()` peut valoir `true` alors que le **rôle affiché à la mort**
(`getDeathRole()` / `AnnouncementDeathEvent.getRole()`) reste le rôle **original**
(non-loup). Seuls `addDeathRole(key)` (façade persistante) ou
`AnnouncementDeathEvent.setRole(key)` (override par-message) changent vraiment le rôle de mort ; les drapeaux
`transformed` / `infected` **ne le font pas**.

| Rôle                                    | `isWereWolf()`          | `getDeathRole()` (message de mort) | `addDeathRole` ? |
|-----------------------------------------|-------------------------|------------------------------------|------------------|
| WereWolf natif (`RoleWereWolf`)         | `true` (hardcodé)       | clé réelle (ex. `WEREWOLF`)        | non              |
| **Enfant Sauvage** (transformé)         | `true` (`transformed`)  | `wild_child` (**Enfant Sauvage**)  | non              |
| **Joueur infecté** (non-loup d'origine) | `true` (`infected`)     | rôle **original** (non-loup)       | non              |
| **Loup-Garou Grimeur**                  | `true`                  | `grimy_werewolf` (sa propre clé)   | non              |
| Chien-Loup (`WolfDog`)                  | `true` si `transformed` | `wolf_dog` (Chien-Loup)            | non              |

> ⚠️ Conséquence : un rôle qui veut réagir aux morts « apparaissant comme
> Loup-Garou » doit lire **`AnnouncementDeathEvent.getRole()`** (le rôle annoncé),
> **pas** `isWereWolf()` — sinon il inclurait à tort l'Enfant Sauvage transformé et
> le joueur infecté, et manquerait la victime grimée (voir ci-dessous).

#### Grime du Loup-Garou Grimeur — swap d'affichage de mort

Quand le Grimeur **tue** un joueur, il reçoit un **message cliquable** à la
`SecondDeathEvent` (`onSecondDeathEvent`, `HIGH`) — comme l'Infect. S'il clique dans les 7 s (`CommandGrimy`,
`/ww grimer <uuid>`, `requiredPower = true`,
`byUuid = true`, `states = { JUDGEMENT }`), il « grimme » sa victime via
`grime(victim)` (le grime est donc **optionnel**). Au moment du grime, le Grimeur tire **un Loup-Garou aléatoire encore
en vie** (exclu lui-même) et stocke sa clé de rôle dans `grimedRoleKey` (fallback `RoleBase.WEREWOLF` si aucun loup).
Effets :

- **Victime grimée** : son message de mort affiche `grimedRoleKey` (override via
  `event.setRole(...)` à `HIGH` dans `onDeathAnnounce`) — donc un loup **spécifique aléatoire**, pas le `WEREWOLF` de
  base. Sa `getDeathRole()` persistante reste inchangée — seul le message est maquillé. Le grime étant posé pendant la
  fenêtre de mort (≤ 7 s), il est effectif **avant** la `FinalDeathEvent` (14 s) qui fire l'annonce.
- **Grimeur lui-même** : son propre message de mort est réécrit à `HIGHEST`
  (`onSelfDeathAnnounce`) — **après** `NotAllWereWolfs` (`HIGH`) qui l'aurait réduit à `WEREWOLF`. S'il a grimé un
  joueur → affiche le **rôle réel de la victime** (`affectedPlayer.get(0).getRole().getKey()`) ; s'il n'a grimé
  personne → affiche sa propre clé (`grimy_werewolf`). Le « swap » complet (victime → loup aléatoire, Grimeur → rôle de
  la victime) masque l'identité du Grimeur et incrimine un loup aléatoire.
- Le **compteur de composition** suit le même swap (`removeOneRole(WEREWOLF)` au grime, report du retrait du rôle réel
  de la victime à la mort du Grimeur).

> ⚠️ Comme le Grimeur réécrit son propre rôle de mort à `HIGHEST`, un rôle qui lit
> le death role à `HIGHEST` (ex. Braconnier) peut le lire **avant ou après**
> l'override (ordre non garanti entre handlers de même priorité). C'est une
> limitation inhérente — pour une lecture fiable, le Grimeur serait à `MONITOR`
> (interdit ici par convention).

#### Échelle de priorités de `AnnouncementDeathEvent`

`AnnouncementDeathEvent` est fire par-spectateur dans `DeathListener.onFinalDeath`
(`FinalDeathEvent` `NORMAL`). Les writers de `setRole(...)` / `setCancelled(...)`
se répartissent ainsi :

| Priorité  | Action                                                                                      | Exemples                                                                    |
|-----------|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `LOW`     | `setRole(camp.getKey())` (config « show category »)                                         | `ShowDeathCategoryRole`                                                     |
| `HIGH`    | `setRole(RoleBase.WEREWOLF)` (loups par défaut **et** victime grimée)                       | `NotAllWereWolfs`, `GrimyWereWolf`                                          |
| `HIGH`    | `setRole("...magic")` (masque aléatoire)                                                    | `Honor`, `Priestess`, `HiddenRoles`                                         |
| `HIGHEST` | `setRole(real getKey())` (vérité) ou `setRole(falsifié)` ou `setCancelled(true)` (deferral) | `Priestess`, `Shaman`, `CounterfeiterWereWolf`, `BloodMoon`, `Thiercelieux` |
| `MONITOR` | **aucun writer** (convention Bukkit : lecture seule)                                        | —                                                                           |

**`NotAllWereWolfs`** (`random_events/NotAllWereWolfs.java`) réécrit à `HIGH` le rôle de mort de **tout rôle de
catégorie `WEREWOLF`** (via `isDefaultWereWolf` =
`category == WEREWOLF || key == WHITE_WEREWOLF`) vers `RoleBase.WEREWOLF` — mais **uniquement si l'event aléatoire est
actif**. Sinon, chaque loup garde sa clé spécifique dans le message de mort.

> **Pour lire le rôle de mort « final »** : `HIGHEST` suffit dans la plupart des
> cas (les writers `HIGH` — Grimeur, NotAllWereWolfs — ont déjà tourné, et les
> writers `HIGHEST` qui « disent la vérité » — Priestess, Shaman — ne s'appliquent
> qu'au spectateur qui les incarne, donc n'affectent pas un autre spectateur).
> `MONITOR` est réservé à l' **observation** (jamais de `setRole`/`setCancelled`),
> conformément à la convention Bukkit ; ne pas l'utiliser pour de la logique de
> jeu. **Attention** à `BloodMoon`/`Thiercelieux` (`HIGHEST`,
> `ignoreCancelled=true`) qui `setCancelled(true)` pour **déporter** l'annonce au
> jour — sans `ignoreCancelled`, un handler `HIGHEST` s'exécute quand même sur
> l'event annulé (le rôle est déjà final, posé à `HIGH`), ce qui permet de suivre
> ces morts différées sans attendre la ré-émission (qui n'a pas lieu : `BloodMoon`
> renvoie le message directement, sans re-fire l'event).

#### Accéder au registre depuis un rôle — ne pas utiliser `Register.get()`

`Register.get()` est un accès **statique côté plugin**
(`fr.ph1lou.werewolfplugin.Register`) ; l'appeler dans un rôle couple le rôle au plugin et l'empêche d'être déplacé dans
un addon. `WereWolfAPI` (l'objet `game`
passé au rôle) **n'expose pas** `getRegisterManager()` — c'est `GetWereWolfAPI`
(l'API publiée via le `ServicesManager`) qui le fait. Voie API :

```java
GetWereWolfAPI api = Bukkit.getServer().getServicesManager().load(GetWereWolfAPI.class);
IRegisterManager registry = api.getRegisterManager();   // getCategory(key), getRolesRegister(), ...
```

Le `IRegisterManager` peut être mis en cache dans un champ du rôle (le
`ServicesManager` est peuplé dès `onEnable`, donc toujours disponible pendant la partie).

> Cas d'usage du Braconnier : pour savoir si un cadavre donne une fourrure, le
> rôle résout la **catégorie du rôle annoncé à la mort** (`event.getRole()`) via
> `registry.getCategory(deathRole)`. Donne fourrure si `category == WEREWOLF` ou
> si `deathRole` est `RoleBase.WOLF_DOG` (le Chien-Loup est catégorie `VILLAGER`)
> ou `RoleBase.WHITE_WEREWOLF` (le Loup-Garou Blanc est catégorie `NEUTRAL`) —
> d'où ces deux cas spéciaux en plus de la catégorie. Cette lookup registre est
> **scopée à la mort uniquement** — le reste du rôle n'y touche pas. Le
> `event.getRole()` lu à `HIGHEST` reflète déjà les maquillages posés à `HIGH`
> (Grimeur sur sa victime, `NotAllWereWolfs` sur les loups par défaut).

#### Time-tracking : `WereWolfAPI.getTimer()` & `IPlayerWW.getDeathTime()`

`game.getTimer()` (`WereWolfAPI.java:229`) renvoie le temps de jeu courant (int, secondes). `IPlayerWW.getDeathTime()`
renvoie le timer au moment de la mort. Pour une mécanique « fenêtre de X minutes après la mort » (ex. particules pendant
15 min), on peut soit compter en décrémentant dans `second()` (mis en pause tant que le rôle est mort, car `second()`
retourne tôt), soit calculer
`game.getTimer() - playerWW.getDeathTime()` (temps réel, indépendant de l'état du rôle).

#### Coloration d'écran via l'action bar (vignette Lunar Client)

L'action bar colorée (`ActionBarEvent`) est rendue par **Lunar Client** comme une **vignette** (dégradé coloré sur les
bords de l'écran), pas seulement comme du texte. Un rôle qui veut « colorer l'écran en rouge » insère du texte `§c...`
dans l'action bar (ex. `§c========` alterné avec `§6========`) ; le joueur Lunar voit alors un liseré rouge. Sans Lunar,
l'effet se limite au texte de l'action bar. Le réglage Lunar requis : `Video Settings > Details > Vignette > Fancy`.
Technique utilisée par le Braconnier (détection de cadavre à courte distance).

#### Suivi des épisodes — `DayEvent.getNumber()` / `NightEvent.getNumber()`

`WereWolfAPI` **n'expose pas** le numéro d'épisode courant (jour). `getTimer()`
renvoie le temps de jeu en secondes, pas le n° de jour. Les events
`DayEvent` / `NightEvent` (`events/game/day_cycle/`) portent un `int number`
(démarré à 1, incrémenté à chaque cycle dans `CycleListener`). Un rôle qui gâte par épisode capture ce numéro dans un
champ via `@EventHandler onDay(DayEvent)` :

```java

@EventHandler
public void onDay(DayEvent event) {
    if (event.getNumber() < FIRST_EPISODE) return;   // gate « à partir de l'épisode N »
    dayNumber = event.getNumber();
    setPower(true);                                  // réarmement quotidien
}
```

Ne **pas** maintenir un compteur incrémenté à la main — lire `event.getNumber()`.

#### Pouvoir récurrent « une fois par épisode » + pouvoir perdable

Le pattern canonique (Voyante, Corbeau, Salvateur, Gardien, Détective) :
`RoleWithLimitedSelectionDuration` (fournit `IPower` + fenêtre `POWER_DURATION`)

+ `setPower(false)` au constructeur + `setPower(true)` dans `onDay` + commande en
  `requiredPower = true` qui `setPower(false)` après usage. Pour un pouvoir qui peut être **perdu définitivement**
  (mauvaise devinette), ajouter un booléen séparé (`powerLost`) qui court-circuite le réarmement
  (`if (powerLost) return;` avant
  `setPower(true)`), à l'image de `Guard.powerFinal` (`Guard.java:39,119`).

#### Pouvoir à cooldown temporel (ex. 1x/10min)

`game.getTimer()` (secondes) sert d'horloge. Stocker `lastUseTimer` (init à
`-COOLDOWN` pour autoriser le 1er usage) et vérifier
`game.getTimer() - lastUseTimer >= COOLDOWN`. Aucun `@Timer` ni `second()`
nécessaire. Contrairement au pouvoir « une fois par épisode », celui-ci n'est PAS un `IPower` (pas de `requiredPower`) —
la commande vérifie le cooldown manuellement dans `execute`.

#### Tracking des actions ciblantes — `SpyInfoEvent` & `ActionableStoryEvent`

Il n'existe **pas** d'event unique « un joueur a été ciblé par une commande ». Chaque event d'abilité (87 sous-classes
de `SelectionEvent`) a son **propre**
`HandlerList` — on ne peut pas écouter la classe de base pour toutes les attraper. En revanche, `StatisticsEvents` fire
deux events **dérivés centraux**
pour les events annotés :

| Event dérivé                             | Annotation source     | Porte                    | `getPlayer()` =                  |
|------------------------------------------|-----------------------|--------------------------|----------------------------------|
| `ActionableStoryEvent(player, eventKey)` | `@TellableStoryEvent` | l'**acteur** + la clé    | acteur                           |
| `SpyInfoEvent(actor, target, eventKey)`  | `@SpyEvent`           | acteur **+ cible** + clé | acteur (`getTargetWW()` = cible) |

Pour compter « combien de fois un joueur a été ciblé par une ability » (et la nature de la dernière), écouter **
`SpyInfoEvent`** et indexer par
`event.getTargetWW()` (couvre les abilities `@SpyEvent` : Voyante, Renifleur, Grognement, etc.). C'est la base utilisée
par le Spy (`Spy.onActionableStory`
compte par acteur) et par `Honor.onSpyInfo` (avertit le neutre ciblé). La « nature » = `event.getEvent()` (la clé
`@StatisticsEvent`, à `game.translate(...)`).

#### Système d'Honneur

L'honneur est un attribut **par joueur** (`IPlayerWW.getHonor()` / `modifyHonor(int)`), **additif uniquement** (pas de
`setHonor`), **clampé à [-3, 3]**, initialisé aléatoirement dans `{-1, 0, 1}` (`PlayerWW` constructeur). Le config
`HONOR`
(`configs/Honor.java`, `@Configuration`) active le listener qui fait varier l'honneur (kill d'un loup +1, neutre +2,
villageois -1 ; être voté -2 ; voter +1 ; abstention -1) et applique les effets (scaling Force des loups, masquage de
l'annonce de mort, poids de vote, ±2 PV à ±3, etc.). Un rôle qui veut un honneur de départ fixe calcule le delta dans
`recoverPower()` :
`modifyHonor(target - getHonor())` (avec un flag `initialized` car `recoverPower`
est aussi rappelé après un changement de rôle). L'honneur existe comme champ indépendamment du config `HONOR` — un rôle
peut le lire/consommer même si le config est inactif (mais il ne variera plus alors).

#### Camp réel vs camp affiché — `getCamp()` vs `getDisplayCamp()`

`IRole.getCamp()` renvoie le `Camp` **dérivé réel** (`NEUTRAL` si `isNeutral()`, sinon `WEREWOLF` si `isWereWolf()`,
sinon `VILLAGER`). `getDisplayCamp()` renvoie le camp **affiché** aux rôles à information (par défaut =
`getCamp().getKey()`, mais surchargeable via `setDisplayCamp(...)` ou override — ex. Braconnier avec fourrure,
Chien-Loup en forme chien). Pour un rôle à information qui doit respecter les déguisements, utiliser **
`getDisplayCamp()`** (et
`getDisplayRole()` pour le rôle). `Camp` est l'énumération
`WEREWOLF` / `NEUTRAL` / `VILLAGER` (clés `werewolf.categories.*`).

#### Paramètres de commande multi-types — `@Params`

`@Params` combine librement plusieurs types d'arguments résolus par index :
`players` (`@PlayerArg`), `integers` (`@IntArg`), `bukkitPlayers`, `strings`,
`enums`, `choices`. Ex. `/ww boheme [Pseudo] [1-2-3]` :

```java
@RoleCommand(key = "...", roleKeys = RoleBase.BOHEMIAN, argNumbers = 2,
        args = @Params(
                players = @PlayerArg(index = 0, states = {StatePlayer.ALIVE}, excludeSelf = true),
                integers = @IntArg(index = 1, min = 1, max = 3)))
```

Récupération : `ctx.player(0)` / `ctx.intValue(1)`. Le résolveur valide bornes, états, excludeSelf avant `execute`.
`ChoiceArg` (ex. `choices = {"bleu","jaune"}`)
résout en `ctx.string(index)` (comparaison case-insensitive).

#### Hurlement — feature générique du camp des Loups-Garous

Le hurlement est une **mécanique transverse au camp loup** (pas un pouvoir role-specific). C'est une `@Configuration`
(`ConfigBase.WEREWOLF_HOWLING`,
`configs/WerewolfHowling.java`) + une commande générique (`commands/.../werewolf/CommandWereWolfHowling.java`,
`roleKeys = {}` → disponible à tous, mot `hurler`).

**Flux** : le loup tape `/ww hurler` → fire `WereWolfCanHowlingEvent` (peut-il hurler ?) → si oui, fire un
`WereWolfHowlingEvent(player, target, location)` par joueur vivant → le loup reçoit le nombre de loups dans un rayon de
50 blocs.

**Gating** (`WerewolfHowling.onWerewolfRequestHowling`, `LOWEST`) :
`isWereWolf()` + `|honor| > 1` + pas déjà hurlé (`playerWhoHaveHowling`) + config
`WEREWOLF_HOWLING` active (vérifiée à `HIGHEST`). Le once-per-game est tracé par un `Set<IPlayerWW>`.

**Effets** (`WerewolfHowling.onWerewolfHowling`, `HIGHEST`) : pour chaque cible **loup** (le `WereWolfHowlingEvent` est
annulé à `LOWEST` si la cible n'est pas loup) → `Sound.WOLF_HOWL` + message warn + stockage de la position du hurleur
pour afficher une **flèche directionnelle** dans l'action bar pendant 1 minute (`event.getDuration()` = 1200 ticks).

**Cas spéciaux** (via `WereWolfCanHowlingEvent` / `WereWolfHowlingEvent`) :

| Rôle                    | Comportement                                                          | Mécanisme                                                                                                                                                                                                                                                                                |
|-------------------------|-----------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Chaque loup             | 1 hurlement/partie                                                    | `playerWhoHaveHowling`                                                                                                                                                                                                                                                                   |
| **Loup-Garou Hurleur**  | 3 hurlements                                                          | `HowlingWerewolf.onRequestHowling` (default) : `setCanHowling(true)` + compteur `power++` tant que `< 3` (bypass le once-per-game). Réagit aussi aux hurlements d'autrui : Regeneration II 7s + apprend le pseudo du hurleur.                                                            |
| **Loup-Garou Craintif** | ne peut **pas** hurler (mais entend)                                  | `FearFulWerewolf.onRequestHowling` (default) : `setCanHowling(false)`. Reçoit quand même les hurlements (c'est un loup).                                                                                                                                                                 |
| **Petite Fille**        | entend + voit la direction, **quelques secondes** et **si invisible** | `LittleGirl.onWerewolfHowling` (default) : le `WereWolfHowlingEvent` est annulé pour elle (non-loup) à `LOWEST`, mais elle le **décancelle** (`setCancelled(false)`) si `isInvisible()`, avec une durée réduite `20*(5+3*compteur)` ticks (croît avec le nombre de hurlements entendus). |

> ⚠️ Certains rôles (AmnesicWerewolf, CounterfeiterWereWolf) ont leur **propre**
> commande `/ww hurler` role-specific (`@RoleCommand(requiredPower = true)` +
> `howl()` + `setPower(false)`), indépendante du système générique (non gated par
> le config ni l'honneur). Ne pas dupliquer ce pattern pour un nouveau rôle loup
> — utiliser le hurlement générique (gratuit via `RoleWereWolf` + config
> `WEREWOLF_HOWLING`).

#### Commandes cliquables en fenêtre de mort — pattern Infect / Grimeur / Assassin

Plusieurs rôles réagissent à la mort d'un joueur **tué par eux** via un message cliquable envoyé pendant la fenêtre de
mort (`SecondDeathEvent`, ~1 tick après le décès, avant la `FinalDeathEvent` à 14 s). Le clic exécute une commande role
avec UUID :

```java
// Rôle : onSecondDeathEvent (HIGH, ignoreCancelled=true)
if(!hasPower())return;                              // pouvoir dispo
        if(!killer.

equals(this.getPlayerWW()))return;       // le tueur est ce rôle
TextComponent msg = VersionUtils.getVersionUtils().createClickableText(
        game.translate(Prefix.YELLOW, "...", Formatter.player(victim.getName())),
        String.format("/ww %s %s", game.translate("...command"), victim.getUUID()),
        ClickEvent.Action.RUN_COMMAND);
this.

getPlayerWW().

sendMessage(msg);

// Commande : @RoleCommand(requiredPower=true, autoCompletion=false, argNumbers=1,
//   args=@Params(players=@PlayerArg(index=0, byUuid=true, excludeSelf=true,
//       states={StatePlayer.JUDGEMENT}, stateMessageKey="werewolf.check.not_in_judgement")))
if(game.

getTimer() -victim.

getDeathTime() >7){...}   // fenêtre 7 s
```

La fenêtre de 7 s (`getTimer() - getDeathTime() > 7`) garantit que l'action est pendant la `SecondDeathEvent` (avant
`FinalDeathEvent` à 14 s). L'effet (infecter / grimer / supprimer) est posé pendant cette fenêtre, donc **avant**
l'annonce de mort (`AnnouncementDeathEvent` à `FinalDeathEvent`). Utilisé par : Infect (résurrection + infection),
Grimeur (grime du rôle de mort), Assassin (suppression de l'annonce + révélation du rôle).

#### Mort silencieuse — pattern Ermite / Craintif

Un rôle dont la mort ne doit **rien révéler** (pas de message, pas de composition, pas de résurrection possible) combine
plusieurs handlers :

| Event                             | Action                                                                          | Effet                                                                                                                                                              |
|-----------------------------------|---------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `FirstDeathEvent` (LOWEST)        | `setCancelled(true)` + `scheduleSyncDelayedTask(14s, () -> game.death(player))` | Skip les fenêtres de mort (Second/ThirdDeath) → la Sorcière ne peut pas ressusciter, l'Infect ne peut pas infecter (pas de SecondDeathEvent). Mort directe à 14 s. |
| `AnnouncementDeathEvent`          | `setCancelled(true)`                                                            | Pas de message de mort.                                                                                                                                            |
| `AnnouncementLoverDeathEvent`     | `setCancelled(true)`                                                            | Pas de message de mort lié (couple).                                                                                                                               |
| `UpdateCompositionEvent` (DEATH)  | `setCancelled(true)` si `key == role`                                           | La composition ne décrémente pas (le rôle "disparaît" sans trace).                                                                                                 |
| `UpdatePlayerNameTagEvent` (HIGH) | `setTabVisibility(false)`                                                       | Masqué du tab quand mort.                                                                                                                                          |
| `VoteEvent`                       | `setCancelled(true)`                                                            | Vote blanc uniquement (ne peut pas voter).                                                                                                                         |

#### PotionModifier — effets par identifiant

Les effets de potion se gèrent par **identifiant** (clé string) via
`IPlayerWW.addPotionModifier(PotionModifier)` :

```java
// Ajouter (durée infinie, amp 0) — ex. Speed la nuit
playerWW.addPotionModifier(PotionModifier.add(UniversalPotionEffectType.SPEED, roleKey));
// Ajouter (durée limitée, amp custom)
        playerWW.

addPotionModifier(PotionModifier.add(TYPE, durationTicks, amplifier, roleKey));
// Retirer (par identifiant)
        playerWW.

addPotionModifier(PotionModifier.remove(TYPE, roleKey, 0));
```

L'identifiant (généralement `this.getKey()`) permet d'ajouter/retirer idempotemment. **Force** et **Résistance** sont
spéciales : l'amplificateur est forcé à -1 (binaire), le % réel vient du taux global (`getStrengthRate()` /
`getResistanceRate()`) via `PatchPotions` + `RequestStrengthRateEvent`. Pour une Force/Résistance **fractionnaire** (ex.
"0.5"), il faut un handler custom sur
`EntityDamageByEntityEvent` (cf. Braconnier) — l'effet vanilla ne peut pas représenter un niveau fractionnaire.

#### Cycle des effets — `recoverPotionEffect()` / `disableAbilitiesRole()`

- **`recoverPotionEffect()`** : rappelé au démarrage (RoleDuration) **et** après chaque changement de rôle (Voleur,
  Escroc, Imitateur, etc.). C'est ici qu'on ré-applique les effets persistants (Vision Nocturne, Speed la nuit, etc.).
  Pour un effet conditionnel (ex. Speed seulement la nuit), vérifier `game.isDay(Day.NIGHT)`.
- **`disableAbilitiesRole()`** : appelé quand les abilities du rôle sont désactivées (`disableAbilities()`). Retirer ici
  les effets actifs (Speed, Invisibility, etc.).
- **`recoverPower()`** : init du pouvoir (bonus PV, randomisation, etc.). Attention : rappelé après un changement de
  rôle — utiliser un flag `initialized`
  pour les inits à usage unique (ex. honneur de départ, tirage aléatoire).

#### Résurrection / mort directe

- **`game.resurrection(playerWW)`** — ressuscite un joueur (Infect, Garde). Le joueur revient à la vie avec son rôle.
- **`game.death(playerWW)`** — tue directement un joueur (Ermite/Craintif : mort silencieuse différée). Déclenche le
  cycle de mort normal.

#### `game.isDay(Day)` / `game.getRandom()` / autres utilitaires `WereWolfAPI`

| Méthode                             | Rôle                                                                       |
|-------------------------------------|----------------------------------------------------------------------------|
| `game.isDay(Day.DAY)` / `Day.NIGHT` | Vérifie le moment du cycle (jour/nuit)                                     |
| `game.getRandom()`                  | `Random` du jeu (tirages aléatoires : couleur, loup aléatoire, etc.)       |
| `game.getAlivePlayersWW()`          | `Collection<? extends IPlayerWW>` des joueurs vivants                      |
| `game.getPlayersWW()`               | Tous les joueurs (vivants + morts)                                         |
| `game.getPlayerWW(uuid)`            | `Optional<IPlayerWW>` par UUID                                             |
| `game.getTimer()`                   | Temps de jeu en secondes (horloge pour cooldowns)                          |
| `game.checkVictory()`               | Vérifie les conditions de victoire (à appeler après un changement de camp) |
| `game.translate(key, Formatter...)` | Traduit une clé i18n avec formateurs                                       |

#### `IPlayerWW.distance(IPlayerWW)` — attention aux mondes

`distance()` appelle `getLocation().distance(other.getLocation())` qui **lève une exception** si les joueurs sont dans
des mondes différents. Toujours vérifier
`getLocation().getWorld() == other.getLocation().getWorld()` (identité, pas
`equals`) avant d'appeler `distance()`.

#### Messages cliquables — `VersionUtils.createClickableText`

```java
TextComponent msg = VersionUtils.getVersionUtils().createClickableText(
        text,                            // texte affiché
        "/ww " + commandWord + " " + uuid, // commande exécutée au clic
        ClickEvent.Action.RUN_COMMAND);
playerWW.

sendMessage(msg);
```

Utilisé pour les actions en fenêtre de mort (Infect, Grimeur, Assassin). Le joueur clique → la commande s'exécute. La
commande doit être `@RoleCommand` avec
`autoCompletion = false` (pas d'autocomplétion pour une commande UUID) et
`byUuid = true` sur le `@PlayerArg`.

#### Stuff rôle — `stuffRole.yml`

Les objets de départ d'un rôle sont définis dans
`WereWolfPlugin/src/main/resources/stuffRole.yml`, keyée par
`werewolf.roles.<role>.display`. Format sérialisation Bukkit `ItemStack` :

```yaml
werewolf.roles.assassin.display:
  0:
    ==: org.bukkit.inventory.ItemStack
    type: ENCHANTED_BOOK
    meta:
      ==: ItemMeta
      meta-type: ENCHANTED
      stored-enchants:
        DAMAGE_ALL: 3           # Sharpness III
```

Les slots 0-35 = inventaire, 36-39 = armor. Le stuff est donné au démarrage (StuffManager). La traduction `items` décrit
le contenu pour la description.

#### Modificateurs d'enchantement `@Role` — `sharpness/protection/powerModifier` + `weight`

L'annotation `@Role` accepte des modificateurs d'enchantement qui **relèvent les caps** d'enchantement pour le rôle (lus
par le système d'enchantement + affichés par `DescriptionBuilder`) :

| Membre                                                 | Effet                                                 |
|--------------------------------------------------------|-------------------------------------------------------|
| `sharpnessDiamondModifier` / `sharpnessIronModifier`   | +N au cap Sharpness (épée diamant/fer)                |
| `protectionDiamondModifier` / `protectionIronModifier` | +N au cap Protection (armure)                         |
| `powerModifier`                                        | +N au cap Power (arc)                                 |
| `weight`                                               | Poids du rôle dans la répartition (ex. Infect `1.5f`) |

Ex. l'Assassin (`sharpnessDiamondModifier=1`, `protectionDiamondModifier=1`) peut appliquer Sharpness IV (cap III + 1)
et Protection III sur diamant (cap II + 1) — le seul rôle à pouvoir appliquer Protection III sur diamant.

#### `Formatter.format(pattern, value)` — formateur custom

En plus des formateurs standards (`Formatter.player`, `.timer`, `.number`,
`.role`), `Formatter.format("&custom&", value)` crée un formateur avec un pattern arbitraire. Utile pour les
placeholders non-standards (ex. `&role1&`,
`&role2&`, `&text&`, `&camp&`, `&nature&`, `&code&`).

#### `auraDescriptionSpecialUseCase` — description d'aura spéciale

Le membre `@Role(auraDescriptionSpecialUseCase = "...")` fournit une clé de traduction affichée dans la description
quand l'aura du rôle a un comportement spécial non-évident (ex. Garde : "être protégé donne une aura Lumineuse" ;
Chien-Loup : "forme chien = Lumineuse, forme loup = Obscure"). Si absent, aucune ligne d'aura spéciale n'est affichée.

### 5.2 Scénarios — `@Scenario`

**Localisation** : `WereWolfPlugin/.../werewolfplugin/scenarios/` (~30 classes). Une classe de scénario étend
`ListenerWerewolf`.

**Exemple simple** — `scenarios/CutClean.java:21` :

```java

@Scenario(key = ScenarioBase.CUT_CLEAN, defaultValue = true, meetUpValue = true)
public class CutClean extends ListenerWerewolf { ...
}
```

**Avec IntValue imbriquée** — `scenarios/DiamondLimit.java:23` :

```java

@Scenario(key = ScenarioBase.DIAMOND_LIMIT, defaultValue = true,
        configValues = @IntValue(key = IntValueBase.DIAMOND_LIMIT, defaultValue = 17,
                meetUpValue = 0, step = 1, item = UniversalMaterial.DIAMOND))
public class DiamondLimit extends ListenerWerewolf { ...
}
```

### 5.3 Timers — `@Timer`

**Localisation** : `WereWolfPlugin/.../werewolfplugin/timers/` (10 classes :
`Pvp`, `Invulnerability`, `DayDuration`, `RoleDuration`, `BorderBegin`,
`DiggingEnd`, `WerewolfList`, `AutoStartDuration`, `PowerDuration`, `LoverDuration`).

**Exemple** — `timers/Pvp.java:13` :

```java

@Timer(key = TimerBase.PVP,
        defaultValue = 25 * 60,
        meetUpValue = 3 * 60,
        decrement = true,
        onZero = PVPEvent.class)
public class Pvp extends ListenerWerewolf { ...
}
```

Le membre `onZero` désigne la classe d'`Event` Bukkit qui sera **instanciée par réflexion et déclenchée** quand le timer
atteint 0 (voir §6.4).

### 5.4 Configurations — `@Configuration` / `@ConfigurationBasic` / `@IntValue`

**Localisation** : `WereWolfPlugin/.../werewolfplugin/configs/` (~21 classes).

Trois usages distincts :

- **Config booléenne top-level** (classe souvent vide) — `configs/HideComposition.java:7` :
  ```java
  @Configuration(config = @ConfigurationBasic(key = ConfigBase.HIDE_COMPOSITION))
  public class HideComposition { }
  ```
- **Config booléenne avec listener** — `configs/LoneWolf.java:24` :
  ```java
  @Configuration(config = @ConfigurationBasic(key = ConfigBase.LONE_WOLF))
  public class LoneWolf extends ListenerWerewolf { ... }
  ```
- **Config booléenne avec timer imbriqué** — `configs/WerewolfChat.java:30` :
  ```java
  @Configuration(config = @ConfigurationBasic(key = ConfigBase.WEREWOLF_CHAT,
          defaultValue = true, meetUpValue = true),
          timers = @Timer(key = TimerBase.WEREWOLF_CHAT_DURATION,
                          defaultValue = 30, meetUpValue = 30))
  public class WerewolfChat extends ListenerWerewolf { ... }
  ```
- **Valeur entière** (`@IntValue`) : généralement **imbriquée** dans un `@Role`,
  `@Scenario`, etc. plutôt que top-level (voir Poacher/DiamondLimit ci-dessus).

### 5.5 Amoureux (lovers) — `@Lover`

**Localisation** : `WereWolfPlugin/.../werewolfplugin/roles/lovers/`.

```java

@Lover(key = LoverBase.LOVER, color = LoverImpl.COLOR)
public class LoverImpl extends AbstractLover { ...
}
```

Lecture réflexive (côté API) dans `lovers/impl/LoverBaseImpl.java` :
`getKey()` (L14) et `getColor()` (L27) utilisent
`getClass().getAnnotation(Lover.class)`.

#### Hiérarchie & classes

```
ILoverBase (interface, extends Listener)   ← API
   └── ILover (interface)                  ← getLovers(), isAlive(), swap(), second(), dissolve()
         ↑
LoverBaseImpl (abstraite, API)             ← getKey()/getColor() réflexifs
   └── AbstractLover (abstraite, plugin)   ← champs lovers/death/game, action bar, dissolve()
         ├── LoverImpl        (couple classique, @Lover LOVER)
         ├── FakeLover / FakeLoverCharmer  (couples « factices »)
         └── (CursedLover, AmnesiacLover étendent LoverBaseImpl directement, pas AbstractLover)
```

> ⚠️ `AmnesiacLover` et `CursedLover` **n'étendent pas** `AbstractLover` : elles
> étendent `LoverBaseImpl` directement et portent leurs propres champs
> (`death`, `amnesiacLover1/2`, `find`). Toute nouvelle méthode de couple à
> implémenter doit donc l'être **dans chaque classe concrète** (ou via une
> `default` no-op sur `ILover` surchargée dans chaque implé).

#### Cycle de vie & enregistrement

- Au `RepartitionEvent`, les couples sont créés et enregistrés via
  `ILoverManager.addLover(ILover)` ; chaque membre est lié côté joueur par
  `IPlayerWW.addLover(ILover)` (cf. `AbstractLover` constructeur).
- Accès : `game.getLoversManager().getLovers()`, `playerWW.getLovers()`.
- `second()` est appelé chaque seconde par `GameTask` (comme les rôles) — utilisé par `AmnesiacLover` pour révéler le
  couple à la proximité.

#### Mort liée (suicide du couple)

Mécanique centrale : **quand un membre meurt, l'autre meurt aussi**. Chaque couple concret écoute `FinalDeathEvent` en
`HIGH` :

- `LoverImpl.onFinalDeath` (`LoverImpl.java:36`) : si le mort est un membre et
  `!death` → `death=true`, annonce, puis `game.death(survivant)` (suicide) +
  `LoverDeathEvent`.
- `AmnesiacLover.onFinalDeath` (`AmnesiacLover.java:73`) : idem (suicide seulement si le couple s'est « trouvé » via
  `find`), fire `AmnesiacLoverDeathEvent`.

Le gardien est le champ `death` (testé **en tête** du handler) : une fois `death=true`, le handler retourne
immédiatement — **pas de suicide, pas d'annonce**.

#### Manipuler un couple : `swap` vs `dissolve`

| Méthode          | Effet                                                                                                                                    | Annonce ?                                               | Usage                                                       |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|-------------------------------------------------------------|
| `swap(old, new)` | remplace `old` par `new` dans le couple                                                                                                  | **oui** (`announceLovers()` → révèle le nouveau membre) | remplacer un amoureux mort par un vivant (ex. ancien Rival) |
| `dissolve()`     | **dissout** le couple : `death=true` + `HandlerList.unregisterAll(this)` + `removeLover` sur chaque membre + `manager.removeLover(this)` | **non** (le handler HIGH retourne avant l'annonce)      | briser un couple sans tuer le survivant, sans fuite de nom  |

`dissolve()` est une `default` no-op sur `ILover`, implémentée dans `AbstractLover`
(couvre `LoverImpl`/`FakeLover`) et `AmnesiacLover`. Elle s'appuie sur :

- `death=true` → fiable pour **l'event en cours** (le handler HIGH teste `death` en premier, donc le suicide est annulé
  même si l'event est déjà en propagation) ;
- `HandlerList.unregisterAll(this)` → désactive **pour le futur** tous les handlers du couple (action bar, message de
  fin, suicide) ;
- `IPlayerWW.removeLover(ILover)` côté membre + `ILoverManager.removeLover(ILover)`
  côté registre → le survivant joue alors **solo**.

> `ILoverManager.removeLover(ILover)` ne fait que retirer de la liste — il ne
> désenregistre **pas** le listener. Pour neutraliser un couple vivant, il faut
> donc `dissolve()` (ou `HandlerList.unregisterAll` + `death=true` manuels), pas
> seulement `removeLover`.

#### Don de vie entre amoureux — `DonEvent`

Le `/ww don` (commande `CommandLovers`) permet d'envoyer un % de vie à son partenaire. Fire
`DonEvent(playerWW, receiverWW, don)` (`events/lovers/DonEvent.java`), **cancellable** : un rôle peut bloquer les dons
d'un amoureux en annulant l'event si l'expéditeur **ou** le destinataire est sa cible (technique utilisée par le Rival
`isoler`).

#### Événements clés (package `events/lovers/`)

| Event                                                           | Rôle                                                                     |
|-----------------------------------------------------------------|--------------------------------------------------------------------------|
| `RevealLoversEvent` / `RevealNormal/Cursed/AmnesiacLoversEvent` | révélation des couples (le Rival y cible le sien)                        |
| `LoverDeathEvent`                                               | fin du couple classique (après suicide) — déclenche la pénalité du Rival |
| `AmnesiacLoverDeathEvent`                                       | fin du couple amnésique                                                  |
| `AnnouncementLoverDeathEvent`                                   | annonce de mort liée (cancellable)                                       |
| `AroundLoverEvent`                                              | détection de proximité autour d'un amoureux                              |
| `DonEvent`                                                      | don de vie (cancellable)                                                 |

### 5.6 Événements aléatoires — `@RandomEvent`

**Localisation** : `WereWolfPlugin/.../werewolfplugin/random_events/` (`GodMiracle`,
`Putrefaction`, `BearingRitual`, `Rumors`, `Swap`, `Triple`, `WolfFleas`,
`Infection`, `Exposed`, `Discord`, `NotAllWereWolfs`).

```java

@RandomEvent(key = EventBase.GOD_MIRACLE,
        loreKey = "werewolf.random_events.god_miracle.description")
public class GodMiracle extends ListenerWerewolf { ...
}
```

### 5.7 Commandes — `@AdminCommand` / `@PlayerCommand` / `@RoleCommand`

| Type   | Dossier                                     | Interface      | Exemple               |
|--------|---------------------------------------------|----------------|-----------------------|
| Admin  | `commands/admin/ingame/`, `commands/admin/` | `ICommand`     | `CommandRevive` (L19) |
| Joueur | `commands/utilities/`                       | `ICommand`     | `CommandRank` (L14)   |
| Rôle   | `commands/roles/<camp>/<rôle>/`             | `ICommandRole` | `CommandHunter` (L18) |

```java
// Admin
@AdminCommand(key = "werewolf.commands.admin.revive.command",
        descriptionKey = "werewolf.commands.admin.revive.description",
        statesGame = StateGame.GAME, argNumbers = 1)
public class CommandRevive implements ICommand { ...
}

// Joueur
@PlayerCommand(key = "werewolf.commands.player.rank.command",
        descriptionKey = "werewolf.commands.player.rank.description",
        statesGame = StateGame.LOBBY, argNumbers = 0)
public class CommandRank implements ICommand { ...
}

// Rôle
@RoleCommand(key = "werewolf.roles.hunter.command",
        roleKeys = RoleBase.HUNTER, requiredPower = true,
        statesPlayer = StatePlayer.DEATH, argNumbers = 1)
public class CommandHunter implements ICommandRole { ...
}
```

### 5.8 Modules / addons — `@ModuleWerewolf`

Annotation **racine obligatoire** sur la classe principale (`JavaPlugin`) de tout module. Voir §2.1 pour le plugin
officiel ; un addon tiers suit exactement la même convention (voir §8).

### 5.9 Événements de statistiques — `@StatisticsEvent` + marqueurs

**Localisation** : côté API, `WereWolfAPI/.../werewolfapi/events/**`.

```java

@StatisticsEvent(key = "werewolf.designed_lover")
public class CupidLoversEvent extends Event {
    @StatisticsPlayer
    public IPlayerWW getPlayerWW() {
        return playerWW;
    }

    @StatisticsTargets
    public Set<IPlayerWW> getPlayerWWS() {
        return playerWWS;
    }
}
```

Marqueurs simples : `@SpyEvent` (ex. `events/roles/bear_trainer/GrowlEvent.java:16`)
et `@TellableStoryEvent` (ex. `events/roles/angel/RegenerationEvent.java:11`).

---

## 6. Couche runtime — instanciation & lecture réflexive

### 6.1 `ListenersManager` — scenarios / timers / configs / random events

`WereWolfPlugin/src/main/java/fr/ph1lou/werewolfplugin/game/ListenersManager.java`

Le constructeur (L44-86) instancie par réflexion (constructeur `(WereWolfAPI)`)
toutes les classes des registres timers / random events / scenarios / configs et les indexe par clé dans des maps. Les
**timers** sont enregistrés d'office (`register(true)`), les autres sont activés plus tard selon la configuration.

`instantiate(Class<?>)` (L120-130) :

```java
return Optional.of((ListenerWerewolf)
        clazz.

getConstructor(WereWolfAPI .class).

newInstance(game));
```

> ⚠️ **Convention de constructeur** : toute classe de scenario / timer / config /
> random event doit exposer un constructeur public `(WereWolfAPI game)`.

**Activation dynamique** — `updateListeners()` (L153-173) active/désactive les listeners selon l'état courant lu dans
`StorageConfiguration` :

```java
listenerWerewolf.register(game.getConfig().

isScenarioActive(
        scenarioRegister.getMetaDatas().

key()));
```

C'est ce qui fait qu'un scenario désactivé ne reçoit plus les events Bukkit.
`ListenerWerewolf.register(boolean)`
(`WereWolfAPI/.../listeners/impl/ListenerWerewolf.java:29`)
wrappe `BukkitUtils.registerListener(this)` / `HandlerList.unregisterAll(this)`.

### 6.2 `RoleDuration` — rôles

Voir §5.1. Constructeur réflexif `(WereWolfAPI, IPlayerWW)`.

### 6.3 `Admin` / `Command` — commandes

- `commands/Admin.java` (L37-58) : instancie chaque `@AdminCommand` par **constructeur par défaut** et les range dans
  une `Map<String, ICommand>`.
- `commands/Command.java` (L41-67) : idem pour `@PlayerCommand` et
  `@RoleCommand`. Dispatch dans `execute(...)`.

> ⚠️ **Convention de constructeur** : toute classe de commande doit exposer un
> constructeur public **sans argument**.

### 6.4 `GameTask` — décrément des timers & events `onZero`

`WereWolfPlugin/.../tasks/GameTask.java` (L139-176)

À chaque tick (1 tick = 1 seconde de jeu), tous les timers connus sont rassemblés — top-level **et imbriqués** dans
`@Configuration`, `@Role`,
`@RandomEvent`, `@Scenario`. Pour chaque timer dont `decrement()` /
`decrementAfterRole()` / `decrementAfterTimer()` est actif :

- si la valeur atteint 0 → instanciation par réflexion de l'event déclaré dans
  `onZero` puis `Bukkit.getPluginManager().callEvent(...)` :
  ```java
  timerRegister.onZero().getConstructor().newInstance()   // event Bukkit fire
  ```
- décrément via `((Configuration) game.getConfig()).decreaseTimer(...)`.

Exemple : à la fin du timer `PVP`, `PVPEvent` est déclenchée, ce qui active le PVP dans le monde via le listener
associé.

### 6.5 `StatisticsEvents` — lecture des `@Statistics*`

`WereWolfPlugin/.../statistiks/StatisticsEvents.java` (L48-128)

Enregistre dynamiquement un listener Bukkit pour chaque classe annotée
`@StatisticsEvent`. Pour chaque event reçu, lit par réflexion les méthodes marquées `@StatisticsPlayer`,
`@StatisticsTargets`/`@StatisticsTarget`,
`@StatisticsExtraInfo`, `@StatisticsExtraInt`, et détecte les marqueurs
`@TellableStoryEvent` / `@SpyEvent` (L98-99) pour déclencher les events dérivés (`ActionableStoryEvent`,
`SpyInfoEvent`). Le résultat est sérialisé via
`RegisteredAction` (`WereWolfAPI/.../statistics/impl/RegisteredAction.java`).

### 6.6 `StorageConfiguration` — valeurs par défaut des timers/configs/IntValues

`WereWolfPlugin/.../game/StorageConfiguration.java` (implémente
`IStorageConfiguration`).

Holder de maps (`timerValues`, `configValues`, `scenarioValues`, `values`,
`randomEventsValues`, `roleCount`, `loverCount`).

**Comportement clé** : quand une clé n'est pas dans la map (l'hôte n'a rien personnalisé), `StorageConfiguration`
interroge `Register.get()` pour récupérer la **valeur par défaut déclarée dans l'annotation**, en respectant le mode
`meetUp` (L317-322) :

```java
private <T> T getDefaultValue(T value, T meetupValue) {
    if (game.getConfig().isMeetUp()) return meetupValue;
    return value;
}
```

Les recherches balaient **tous les registres**, y compris les valeurs **imbriquées** :

- `getTimerValue(key)` (L151-195) → `timersRegister` puis `.timers()` de
  `rolesRegister`, `randomEventsRegister`, `loversRegister`, `configsRegister`,
  `scenariosRegister`.
- `isConfigActive(key)` (L198-252) → `.configurations()` de tous les registres.
- `getValue(key)` (L266-311) → `.configValues()` de tous les registres, y compris `modulesRegister`.

> Conséquence : un `@IntValue` imbriqué dans un `@Role` est automatiquement
> accessible via `game.getConfig().getValue(IntValueBase.POACHER_DISTANCE)` —
> **aucun enregistrement supplémentaire n'est nécessaire**, le simple fait de
> déclarer l'annotation suffit.

### 6.7 Cycle des events de mort

La mort n'est pas instantanée : elle traverse une chaîne d'events Bukkit orchestrés par `DeathListener`
(`DeathListener.java`). Connaître cet enchaînement (et les délais) est indispensable pour réagir au bon moment.

```
PlayerDeathEvent                     (DeathListener.onPlayerDeath:64)
  │  état → JUDGEMENT, setDeathLocation, addKiller, PlayerWWKillEvent
  │  scheduleSyncDelayedTask(20L) ↓
  ▼
FirstDeathEvent  (L120)             ← « frappeurs » récents (getLastMinutesDamagedPlayer)
  │  onFirstDeath (L127, HIGHEST) → si JUDGEMENT ↓
  ▼
SecondDeathEvent  (L136)            ← fenêtre de résurrection (1 tick)
  │  onSecondDeathEvent (L140, HIGHEST) → scheduleSyncDelayedTask(7*20L) ↓
  ▼
ThirdDeathEvent  (L148)             ← 2e fenêtre de résurrection (7 s)
  │  onThirdDeath (L154, HIGHEST) → scheduleSyncDelayedTask(7*20L) ↓
  ▼
FinalDeathEvent  (L161)             ← mort définitive (14 s après le décès)
  │  DeathListener.onFinalDeath (L167) :
  │    état → DEATH, setDeathTime, ...
  │    └─ pour chaque spectateur : AnnouncementDeathEvent (L198)  ← message de mort
  │       UpdateCompositionEvent (L229)
```

**Points clés pour un rôle** :

- `FinalDeathEvent` est l'event « le joueur est bel et bien mort » — c'est lui qu'on écoute pour les effets de mort
  (perte de PV, transfert de rôle, etc.).
  `event.getPlayerWW()` = le mort ; `event.getPlayerWW().getLastKiller()` = tueur éventuel (`Optional<IPlayerWW>`).
- `AnnouncementDeathEvent` est fire **à l'intérieur** du handler
  `DeathListener.onFinalDeath` (donc pendant la propagation de `FinalDeathEvent`). Un rôle qui consomme un état dans
  `onFinalDeath` et qui falsifie le message via
  `onAnnouncementDeath` doit veiller à **ne pas** consommer cet état avant l'annonce (cf. §5.1 « Rôle d'affichage vs
  rôle de mort »).
- Pour détecter un kill par le rôle : `FinalDeathEvent` + `event.getPlayerWW()
  .getLastKiller()` filtré sur `this.getPlayerWW()`. Les loups reçoivent déjà automatiquement Speed I + Absorption I
  (2❤) pendant 60 s sur kill via
  `WerewolfListener` (`WerewolfListener.java:65-66`) — inutile de le recoder pour un rôle loup.

### 6.8 Système de Force/Résistance custom — `RequestStrengthRateEvent`

Le plugin **remplace le comportement vanilla de Force/Résistance** par un **scaling de dégâts en pourcentage** (au lieu
des +3 dégâts fixes par niveau de Minecraft). `RequestStrengthRateEvent` est l'event **par attaque** qui permet à
d'autres mécaniques d'ajuster le taux pour un attaquant précis.

**Émetteur** — `PatchPotions.onPatchPotion` (`PatchPotions.java:35`), déclenché sur **chaque coup PvP** où l'attaquant
porte l'effet Force :

```java
RequestStrengthRateEvent e =
        new RequestStrengthRateEvent(damagerWW, game.getConfig().getStrengthRate());
Bukkit.

getPluginManager().

callEvent(e);
event.

setDamage(event.getDamage() /2.3f*(1+e.

getStrengthRate() /100f));  // amp 0
```

Le taux initial = la valeur globale de config `strengthRate` (défaut **30** = +30 %, ajustable par l'hôte via
`AdvancedSettingsGUI`).

**Listeners** — mutent le taux pour **cette** attaque via
`event.setStrengthRate(...)` :

| Listener                                 | Condition      | Effet                                                                                |
|------------------------------------------|----------------|--------------------------------------------------------------------------------------|
| `Honor.onPatchPotion` (`Honor.java:307`) | attaquant loup | honneur 1→×11/12, 2→×5/6, 3→×3/4 (les loups à fort honneur frappent plus faiblement) |

**Formules** (`PatchPotions.java:54-65`) :

| Effet                     | Formule                                                                                                                                                       |
|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Force amp 0 (niveau I)    | `dégâts / 2.3 × (1 + taux/100)` — la division par 2.3 neutralise le bonus vanilla Force I (~×2.3 déjà appliqué par Bukkit), puis on reapplique le taux custom |
| Force amp ≥1 (niveau II+) | `dégâts × (1 + taux/100)`                                                                                                                                     |
| Résistance (victime)      | `dégâts × (100 − resistanceRate)/80` ; si `resistanceRate ≥ 100` → event annulé (immunité)                                                                    |

**Conséquences clés** :

1. **L'amplificateur de Force est forcé à -1** dans `PotionModifier`
   (`PotionModifier.java:21`) → l'amplificateur affiché est **sans signification**. La Force est **binaire**
   (présente/absente) ; le % réel vient du **taux**, pas du niveau. C'est pourquoi « Strength 0.5 » vs « Strength I » ne
   se distingue **pas**
   par potion : pour différencier deux niveaux de Force, il faut jouer sur le **taux** en écoutant
   `RequestStrengthRateEvent` (ex. `setStrengthRate(rate/2)`
   pour un « demi-Force »).
2. Le taux est **par attaque et par attaquant** (l'event porte le `playerWW`).
3. Un rôle qui veut un Force **personnalisé** écoute l'event, filtre
   `event.getPlayerWW().equals(this.getPlayerWW())`, et ajuste le taux. Le sort de l'effet lui-même (présent/absent)
   reste géré par `PotionModifier.add(STRENGTH, key)`.
4. `Auramancer` (`Auramancer.java:236,250`) lit le taux **directement**
   (`game.getConfig().getStrengthRate()`) **sans** fire l'event → il ignore les ajustements d'Honneur (×1 pour aura
   Ténébreuse attaquant le Lumineux, ×1/2 pour l'aura Lumineuse renvoyant le dégât).

> ⚠️ Ne pas confondre : `PotionModifier.add(STRENGTH, key)` **active** l'effet Force
> (binaire, amp -1) ; `RequestStrengthRateEvent` **ajuste le %** de cet effet pour
> une attaque donnée. Les deux sont orthogonaux.

---

## 7. Imbrication des annotations

La structure est hiérarchique : les annotations `@Role`, `@Scenario`,
`@RandomEvent`, `@Lover` et `@Configuration` acceptent toutes les membres
`IntValue[] configValues()`, `Timer[] timers()`, `Configuration[] configurations()`
(`@Configuration` ayant en plus un membre obligatoire `config()` vers une
`@ConfigurationBasic`). `@Timer` référence une `Class<? extends Event>` via
`onZero`.

```
@Role / @Scenario / @RandomEvent / @Lover
   ├── IntValue[]      configValues      (valeur entière, ex. distance, taux)
   ├── Timer[]         timers            (timer propre à l'élément)
   └── Configuration[] configurations    (config booléenne)
                          └── @ConfigurationBasic

@Configuration
   ├── ConfigurationBasic config         (obligatoire)
   ├── IntValue[]      configValues
   ├── Timer[]         timers
   └── ConfigurationBasic[] configurations

@Timer
   └── Class<? extends Event> onZero      (event fire à 0)
```

Cette imbrication est **entièrement résolue à l'exécution** par
`StorageConfiguration` et `GameTask` (cf. §6.4 et §6.6).

---

## 8. Créer un nouvel élément — guides pratiques

### 8.1 Ajouter un rôle

**Check-list rapide**

1. Créer une classe dans `roles/<camp>/` qui étend `RoleWereWolf`, `RoleNeutral`,
   `RoleWithLimitedSelectionDuration` ou directement `RoleImpl` (cf. §5.1 pour le choix de la classe de base).
2. Exposer un **constructeur public `(WereWolfAPI, IPlayerWW)`**.
3. Annoter avec `@Role(key = RoleBase.VOTRE_ROLE, category = ..., attribute = ...)`.
4. Ajouter la constante de clé i18n dans `RoleBase` et la traduction dans les fichiers de langue (`fr_FR.json`,
   `en_EN.json`, …).
5. Les configs/timers/IntValues propres au rôle peuvent être déclarés directement dans l'annotation (imbrication) —
   aucune autre inscription.
6. **Ne pas appeler `Register.get()`** (couplage plugin) ; si une lookup registre est nécessaire (ex. catégorie d'une
   clé de rôle), passer par le
   `ServicesManager` (cf. §5.1 « Accéder au registre depuis un rôle ») et la scoper au strict minimum.

**Choix de la classe de base & interfaces**

- Loup-garou natif → `RoleWereWolf`.
- Neutre (victoire solo) → `RoleNeutral`.
- Villageois avec pouvoir unique à durée de sélection → `RoleWithLimitedSelectionDuration` (gère déjà `IPower` +
  `SelectionEndEvent`).
- Villageois « pur » → `RoleImpl` direct ; implémenter `IPower` si pouvoir unique.
- Mémoriser des cibles → implémenter `IAffectedPlayers` (active aussi `rejectIfAlreadyAffected` côté commande, §10.4).
- Limite d'utilisations → `ILimitedUse` (+ `@RoleCommand(requiredLimitedUse = N)` pour les limites fixes).

**Pattern événementiel (recommandé)**

Pour un pouvoir non-trivial, déclarer **vos propres events** dans
`WereWolfAPI/.../events/roles/<votre_role>/` :

- Hériter de `SelectionEvent` (joueur acteur + cible) si pertinent, sinon `Event`.
- Annoter `@StatisticsEvent(key = "...")` pour le suivi stats.
- Ajouter `@TellableStoryEvent` si l'action doit être racontable par le StoryTeller,
  `@SpyEvent` si elle doit être visible par le Spy.
- Rendre l'event `Cancellable` quand une autre mécanique peut le bloquer.

Le rôle fire l'event (`Bukkit.getPluginManager().callEvent(...)`) **avant**
d'appliquer l'effet, vérifie `isCancelled()`, puis applique. D'autres rôles/configs peuvent réagir ou bloquer.

**Côté commande**

Si le pouvoir s'active par commande :

- Créer une classe dans `commands/roles/<camp>/<rôle>/` annotée `@RoleCommand`.
- `roleKeys = RoleBase.VOTRE_ROLE` lie la commande au rôle.
- `requiredPower = true` exige `IPower.hasPower()` (consommer via `setPower(false)` dans `execute`).
- Préférer `IParameterizedCommandRole` + `@Params` (§10) : cibles/poids résolus par le dispatcher ; signature
  `execute(WereWolfAPI, IPlayerWW, CommandContext)`.
- Constructeur public **sans argument** (§6.3).

**Conventions i18n**

Clés d'un rôle `werewolf.roles.<rôle_key_stem>.*` :

| Clé                             | Rôle                                                                                        |
|---------------------------------|---------------------------------------------------------------------------------------------|
| `display`                       | Nom affiché. **Cette clé est la `key` du `@Role`** et la valeur de la constante `RoleBase`. |
| `description`                   | Texte de description (passé à `DescriptionBuilder.setDescription`).                         |
| `power`                         | Description du pouvoir (`setPower`).                                                        |
| `command`                       | **Nom de la sous-commande** tapée après `/ww` (ex. `activate`).                             |
| `activate` / `already_activate` | Aide à la commande selon `hasPower()`.                                                      |
| `perform` / `reveal` / …        | Messages métier.                                                                            |

**Vérification** : au démarrage, un warning dans la console signale toute classe annotée skipée (mauvais préfixe de clé,
type attendu non respecté). Vérifier que le rôle apparaît dans le menu de composition. Un exemple complet commenté est
donné en §8.6.

### 8.2 Ajouter un scénario

1. Créer une classe dans `scenarios/` qui étend `ListenerWerewolf`.
2. Exposer un **constructeur public `(WereWolfAPI)`**.
3. Annoter avec `@Scenario(key = ScenarioBase.VOTRE_SCENARIO, defaultValue = ...)`.
4. Implémenter les handlers d'events Bukkit voulus.

### 8.3 Ajouter un timer

1. Créer une classe dans `timers/` qui étend `ListenerWerewolf`.
2. Exposer un **constructeur public `(WereWolfAPI)`**.
3. Annoter avec `@Timer(key = ..., defaultValue = ..., meetUpValue = ...,
   decrement = true, onZero = VotreEvent.class)`.
4. Créer la classe d'`Event` Bukkit référencée par `onZero` (déclenchée à 0).

### 8.4 Ajouter une configuration

- **Booléenne** : classe annotée `@Configuration(config = @ConfigurationBasic(key = ...))`. Peut être une classe vide si
  elle ne porte aucun listener, sinon étendre
  `ListenerWerewolf` avec un constructeur `(WereWolfAPI)`.
- **Entière** : imbriquer un `@IntValue(...)` dans l'annotation de l'élément auquel elle se rattache (rôle, scénario,
  config). Pas de classe dédiée.

### 8.5 Créer un addon tiers (nouveau module)

1. Créer un plugin Bukkit dépendant de `werewolfapi` (v1.12).
2. Annoter la classe principale avec `@ModuleWerewolf(key = "votreprefixe....",
   item = ..., defaultLanguage = ..., authors = @Author(...))`.
3. La `key` **ne doit pas** commencer par `werewolf.` (réservé) et son préfixe (avant le premier `.`) doit être
   **unique** parmi les modules chargés.
4. Déposer les classes annotées dans le package du plugin ; au démarrage, le
   `Register` du plugin officiel scannera automatiquement le jar de l'addon et enregistrera ses rôles/scénarios/etc. au
   même titre que ceux du cœur.
5. Récupérer l'API si besoin via
   `Bukkit.getServicesManager().load(GetWereWolfAPI.class)`.

> Opt-out : annoter une classe avec `@DisableAutoLoad` pour qu'elle ne soit pas
> chargée réflexivement par le module.

### 8.6 Exemple complet — l'Illusionniste (pouvoir unique + faux loup)

Cet exemple retrace l'implémentation **réelle** de l'Illusionniste (camp villageois) pour illustrer tous les points de
la §8.1 : choix de la classe de base, interfaces `IPower` + `IAffectedPlayers`, events dédiés, commande paramétrée, et
interception de `AppearInWereWolfListEvent` (§5.1).

**Mécanique** : une fois dans la partie, l'Illusionniste active son pouvoir (`/ww activate`). À la prochaine mort, un
joueur vivant **non-loup** aléatoire est ajouté à la liste des loups — les loups le voient comme l'un des leurs, mais il
garde son rôle et **gagne avec son camp d'origine**. 1 minute plus tard, la cible est notifiée et l'Illusionniste reçoit
une liste de 3 pseudos contenant la cible.

#### Étape 1 — Clé i18n & constante

`WereWolfAPI/.../basekeys/RoleBase.java:80` :

```java
public final static String ILLUSIONIST = "werewolf.roles.illusionist.display";
```

`WereWolfPlugin/src/main/resources/fr_FR.json` (extrait) :

```json
"illusionist": {
  "display": "Illusionniste",
  "description": "À la première mort suivant l'activation de votre pouvoir, le pseudo d'un joueur aléatoire sera ajouté à la liste des Loups",
  "power": "Une minute plus tard, le joueur le saura et vous recevrez une liste de trois pseudos contenant celui-ci.",
  "activate": "Pour activer votre pouvoir §b/ww activate",
  "already_activate": "Votre pouvoir est §aactivé",
  "reveal": "L'§bIllusioniste§f vous a ajouté à la liste des Loups Garous",
  "reveal_pseudos": "Le pseudo du joueur que vous avez ajouté à la liste des Loups est parmi : §b&names&",
  "command": "activate",
  "perform": "Votre pouvoir a été activé"
}
```

#### Étape 2 — Events dédiés (`WereWolfAPI/.../events/roles/illusionist/`)

Trois events, tous `@StatisticsEvent` pour les stats :

| Event                                     | Rôle                                | Marqueurs                                       |
|-------------------------------------------|-------------------------------------|-------------------------------------------------|
| `IllusionistActivatePowerEvent`           | activation, `Cancellable`           | `@StatisticsPlayer`                             |
| `IllusionistAddPlayerOnWerewolfListEvent` | ajout de la cible                   | `@TellableStoryEvent`, extends `SelectionEvent` |
| `IllusionistGetNamesEvent`                | remise des 3 pseudos, `Cancellable` | `@StatisticsPlayer` + `@StatisticsTargets`      |

```java

@StatisticsEvent(key = "werewolf.illusionist_add_player_event")
@TellableStoryEvent
public class IllusionistAddPlayerOnWerewolfListEvent extends SelectionEvent {
    public IllusionistAddPlayerOnWerewolfListEvent(IPlayerWW playerWW, IPlayerWW target) {
        super(playerWW, target);
    }
    // HandlerList standard...
}
```

#### Étape 3 — La classe de rôle

`WereWolfPlugin/.../roles/villagers/Illusionist.java:39` :

```java

@Role(key = RoleBase.ILLUSIONIST,
        category = Category.VILLAGER,
        attribute = RoleAttribute.VILLAGER)
public class Illusionist extends RoleImpl implements IPower, IAffectedPlayers {

    private boolean power = true;   // IPower
    private boolean wait = false;   // pouvoir armé, en attente de la prochaine mort
    private IPlayerWW playerWW;     // IAffectedPlayers : la cible « faux loup »

    public Illusionist(WereWolfAPI game, IPlayerWW playerWW) {
        super(game, playerWW);      // constructeur canonique
    }
    // ...
}
```

`RoleImpl` (villageois) + `IPower` (pouvoir unique → `requiredPower`) +
`IAffectedPlayers` (mémorise la cible). Trois responsabilités :

**R1 — déclenchement à la prochaine mort** (`Illusionist.java:60`) : le handler
`onFinalDeathEvent` garde-fous `isAbilityEnabled()` / `ALIVE` / `isWait()`, tire un non-loup aléatoire, fire
`IllusionistAddPlayerOnWerewolfListEvent`
(cancellable), puis `addAffectedPlayer(cible)` + `UpdateNameTagEvent` + message
`new_werewolf` et `WOLF_HOWL` aux loups.

**R2 — révélation différée 60 s** (`Illusionist.java:111`) :
`BukkitUtils.scheduleSyncDelayedTask(game, runnable, 20*60L)` notifie la cible (`illusionist.reveal`) et envoie à
l'Illusionniste une liste mélangée de 3 pseudos (cible + 2 leurres) via `illusionist.reveal_pseudos`.

**R3 — faux loup** (`Illusionist.java:152`) :

```java

@EventHandler
public void onWerewolfListRequest(AppearInWereWolfListEvent event) {
    if (event.getTargetWW().equals(this.playerWW)) {
        event.setAppear(true);   // surcharge le isWereWolf() par défaut
    }
}
```

Priorité `NORMAL` (défaut) → s'exécute **après** `WerewolfListener` (`LOWEST`)
qui avait mis `appear = isWereWolf()`. La cible apparaît loup **sans** l'être.

#### Étape 4 — La commande

`WereWolfPlugin/.../commands/roles/villager/illusionist/CommandIllusionist.java:14` :

```java

@RoleCommand(key = "werewolf.roles.illusionist.command",
        roleKeys = RoleBase.ILLUSIONIST,
        argNumbers = 0,
        requiredPower = true)
public class CommandIllusionist implements IParameterizedCommandRole {

    @Override
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        Illusionist illusionist = (Illusionist) playerWW.getRole();
        illusionist.setPower(false);                          // consomme IPower

        IllusionistActivatePowerEvent event = new IllusionistActivatePowerEvent(playerWW);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED, "werewolf.check.cancel");
            return;
        }
        illusionist.setWait(true);                            // armé pour la prochaine mort
        playerWW.sendMessageWithKey(Prefix.YELLOW, "werewolf.roles.illusionist.perform");
    }
}
```

`requiredPower = true` → le dispatcher refuse la commande si `hasPower()` est faux (message automatique). La commande ne
fait qu' **armer** ; l'effet réel se produit au prochain `FinalDeathEvent`.

#### Étape 5 — Connexion des pièces au runtime

```
Joueur tape /ww activate
   └─ dispatcher vérifie roleKeys + requiredPower (hasPower) → CommandIllusionist.execute
        ├─ setPower(false)              (IPower consommé)
        ├─ fire IllusionistActivatePowerEvent
        └─ setWait(true)

Prochaine FinalDeathEvent
   └─ Illusionist.onFinalDeathEvent (wait==true)
        ├─ tire cible non-loup aléatoire
        ├─ fire IllusionistAddPlayerOnWerewolfListEvent (cancellable, @TellableStoryEvent)
        ├─ addAffectedPlayer(cible) + UpdateNameTagEvent
        ├─ message new_werewolf + WOLF_HOWL aux loups
        └─ scheduleSyncDelayedTask(60s)
             ├─ cible ← illusionist.reveal
             └─ Illusionniste ← illusionist.reveal_pseudos (3 pseudos mélangés)

Un loup consulte sa liste (/ww list)
   └─ CommandWereWolf → AppearInWereWolfListEvent pour chaque joueur
        ├─ WerewolfListener (LOWEST) : appear = isWereWolf()
        └─ Illusionist.onWerewolfListRequest (NORMAL) : appear = true pour la cible
   → la cible s'affiche comme loup (mais getCamp()/isWereWolf() inchangés → gagne village)
```

**Leçons clés** :

- Un rôle = une classe `@Role` + constructeur canonique + (option) interfaces-marqueurs
    + (option) events dédiés + (option) commande `@RoleCommand`.
- « Faire apparaître quelqu'un comme loup » ne nécessite **pas** de changer son rôle : intercepter
  `AppearInWereWolfListEvent` suffit (couche d'affichage, §5.1).
- Les effets différés passent par `BukkitUtils.scheduleSyncDelayedTask(game, runnable, ticks)`.
- Toujours fire un event `Cancellable` avant l'effet (pour laisser les autres mécaniques s'y opposer) et un event
  `@StatisticsEvent` (pour les stats).

---

## 9. Conventions & pièges

| Règle                                | Détail                                                                                                                                       |
|--------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| **Préfixe de clé**                   | Toute `key` doit commencer par le préfixe du module propriétaire ; `werewolf.` est réservé au plugin officiel.                               |
| **Unicité du préfixe**               | Le préfixe (avant le premier `.`) d'un `@ModuleWerewolf` doit être unique entre modules.                                                     |
| **Constructeur attendu**             | Rôles : `(WereWolfAPI, IPlayerWW)` — Scenario/Timer/Config/RandomEvent : `(WereWolfAPI)` — Commandes : sans argument.                        |
| **Type attendu**                     | La classe annotée doit implémenter/étendre le bon type (`IRole`, `ListenerWerewolf`, `Event`, `ICommand`…), sinon skip silencieux + warning. |
| **Rétention**                        | Toutes les annotations sont `RUNTIME` (sauf `@Author` sans rétention explicite).                                                             |
| **Initialisation statique différée** | Les classes sont chargées via `Class.forName(..., false, ...)` ; les blocs `static` ne tournent qu'à l'instanciation.                        |
| **Aucune lib de scan**               | Le scanning repose uniquement sur `java.util.jar.JarFile` (maison).                                                                          |
| **Events : `IPlayerWW` > `UUID`**    | Dans les events Bukkit, passer des `IPlayerWW` (et `Map<IPlayerWW, …>`) plutôt que des `UUID`. Les listeners n'ont alors pas à appeler `game.getPlayerWW(uuid).orElse(null)` — l'objet est directement disponible. Un event doit porter toute l'info nécessaire aux listeners, sans qu'ils aient à la recharger depuis le jeu. |

---

## 10. Système d'annotation de paramètres de commandes

Le dispatcher valide déjà au niveau **classe** (avant `execute`) les contraintes de haut niveau : `statesGame`,
`argNumbers`, `statesPlayer` (joueur exécutant), permissions, `roleKeys`, `requiredPower`, `requiredAbilityEnabled` (cf.
§2.2 et §6.3). Mais la **résolution des arguments d'entrée** (joueur cible, entier…) était refaite manuellement dans
chaque `execute()` — un bloc « `Bukkit.getPlayer` → check null →
`getPlayerWW` → check null → `isState` → message » répété ~40 fois. Le système ci-dessous élimine cette répétition : les
paramètres sont **déclarés** via une annotation, **validés et résolus** par le dispatcher, puis passés dans un
`CommandContext`. **Approche additive** : l'ancienne signature et le chemin hérité restent fonctionnels (API publique et
addons préservés).

### 10.1 Types introduits

**API** — `fr.ph1lou.werewolfapi` :

- `annotations/Params.java` — conteneur des paramètres d'entrée (membre `args()` des annotations de commande) ; regroupe
  6 tableaux typés : `players`, `bukkitPlayers`,
  `integers`, `strings`, `enums`, `choices`.
- `annotations/PlayerArg.java`, `PlayerBukkitArg.java`, `IntArg.java`, `StringArg.java`,
  `EnumArg.java`, `ChoiceArg.java` — une annotation par type, avec **uniquement ses champs pertinents** (ex.
  `PlayerArg` : `index`, `states`, `excludeSelf`, `stateMessageKey`,
  `required` ; `PlayerBukkitArg` : `index`, `required` ; `IntArg` : `index`, `min`, `max`,
  `required` ; `EnumArg` : `index`, `enumClass`).
- `commands/CommandContext.java` — contexte résolu : `player(i)`, `bukkitPlayer(i)`,
  `intValue(i)`, `string(i)`, `enumValue(i, Class)`, `has(i)`, `args()`.
- `commands/IParameterizedCommand.java` — `extends ICommand`, ajoute
  `execute(WereWolfAPI, Player, CommandContext)` ; l'ancienne `execute(..., String[])`
  reçoit un `default` qui lève (jamais appelé car le dispatcher branche sur `instanceof`).
- `commands/IParameterizedCommandRole.java` — variante pour `ICommandRole`.

Le membre `Params args() default @Params;` est présent sur `@AdminCommand`,
`@PlayerCommand`, `@RoleCommand`. Chaque type expose uniquement ses propres champs (type-safe : impossible d'écrire un
champ incohérent avec le type).

**Plugin** — `fr.ph1lou.werewolfplugin.commands` :

- `ArgumentResolver.java` — valide + résout les `@Params` (fusionne les 5 tableaux par
  `index`, un handler par type) contre `args[]`, envoie les messages `werewolf.check.*`
  existants via un `MessageSender`, retourne un `Optional<CommandContext>` (vide = échec, commande non appelée). Fournit
  aussi l'autocomplétion (`complete(game, params, index, partial)`).
- `MessageSender.java` — canal abstrait (`forPlayer` / `forPlayerWW`) pour envoyer indifféremment à un `Player` Bukkit
  ou un `IPlayerWW`.
- `CommandContextImpl.java` — implémentation prérésolue (valeurs en `Map<Integer,Object>`).

### 10.2 Flux de résolution

```
/ww <sous-commande> <arg0> <arg1>
        │  Admin.onCommand / Command.onCommand
        ▼
   accessCommand / checkCommand(s)   ← validations de classe (statesGame, argNumbers,
        │                               statesPlayer, permissions, roleKeys…)
        ▼
   cmd instanceof IParameterizedCommand(IRole) ?
        │  non → execute(..., String[])            (chemin hérité, inchangé)
        │  oui ▼
        │   ArgumentResolver.resolve(game, senderUuid, MessageSender, args(), args)
        │       ├─ PLAYER : Bukkit.getPlayer → offline_player ; getPlayerWW →
        │       │           player_not_found ; excludeSelf → not_yourself ;
        │       │           states → state_player (ou stateMessageKey)
        │       ├─ PLAYER_BUKKIT : Bukkit.getPlayer → offline_player (aucune vérification
        │       │                   de partie/état — pour les cibles admin arbitraires)
        │       ├─ INT    : parseInt → number_required ; bornes min/max
        │       ├─ STRING : choices / maxLength
        │       ├─ ENUM   : Enum.valueOf(enumClass, …)
        │       └─ CHOICE : parmi choices
        │       → Optional<CommandContext>  (empty = message déjà envoyé)
        ▼
   execute(game, player|playerWW, ctx)   ← métier uniquement, ctx.player(0)…
```

Le dispatcher branche sur `instanceof` aux points d'appel `Admin.java:92`,
`Command.java:172` (rôles) et `Command.java:203` (joueurs) : chemin contextualisé si la commande implémente
`IParameterizedCommand`/`IParameterizedCommandRole`, chemin hérité sinon. `onTabComplete` (`Admin.java`, `Command.java`)
complète désormais les paramètres : PLAYER → noms des `IPlayerWW` dans un état autorisé ; CHOICE/ENUM → valeurs
autorisées. L'index du paramètre en cours de frappe = `args.length - 2`
(le tableau tab-complete inclut le nom de sous-commande en position 0).

### 10.3 Exemple (avant / après)

Avant — bloc répétitif dans `execute()` :

```java
public void execute(WereWolfAPI game, IPlayerWW playerWW, String[] args) {
    Player p = Bukkit.getPlayer(args[0]);
    if (p == null) {
        playerWW.sendMessageWithKey("werewolf.check.offline_player");
        return;
    }
    IPlayerWW target = game.getPlayerWW(p.getUniqueId()).orElse(null);
    if (target == null || !target.isState(StatePlayer.ALIVE)) {
        playerWW.sendMessageWithKey("werewolf.check.player_not_found");
        return;
    }
    // ... métier
}
```

Après — déclaration + accès direct :

```java

@RoleCommand(key = "...", roleKeys = RoleBase.HUNTER, requiredPower = true,
        statesPlayer = StatePlayer.DEATH, argNumbers = 1,
        args = @Params(players = @PlayerArg(index = 0, states = {StatePlayer.ALIVE})))
public class CommandHunter implements IParameterizedCommandRole {
    public void execute(WereWolfAPI game, IPlayerWW playerWW, CommandContext ctx) {
        IPlayerWW target = ctx.player(0);   // résolu + validé par le dispatcher
        // ... métier uniquement
    }
}
```

### 10.4 Conventions spécifiques

| Règle                                  | Détail                                                                                                                                                                                                                                                                                                                                                |
|----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`index`**                            | Position dans le tableau `args` reçu par `execute`, **post-sous-commande** (le nom de sous-commande est déjà retiré par le dispatcher) : `index = 0` = 1er vrai paramètre.                                                                                                                                                                            |
| **`byUuid`** (PLAYER)                  | Résout l'argument comme une UUID textuelle (`game.getPlayerWW(UUID.fromString(raw))`) au lieu d'un nom (`Bukkit.getPlayer`). Pour les cibles **mortes/hors-ligne** (ex. `CommandShaman`, `CommandScapeGoat`, `CommandBarbarian`). Échec → `werewolf.check.not_in_game_player`. L'autocomplétion est désactivée pour ce mode (un UUID ne se tape pas). |
| **`states`** (PLAYER)                  | États autorisés de la cible. Vide = aucun état requis. Pour « non-mort » (rejeter seulement `DEATH`), utiliser `{ALIVE, JUDGEMENT}`.                                                                                                                                                                                                                  |
| **`excludeSelf`**                      | Interdit la self-cible → `werewolf.check.not_yourself`.                                                                                                                                                                                                                                                                                               |
| **`stateMessageKey`**                  | Surcharge le message d'échec d'état (sinon générique `werewolf.check.state_player`). Préserve un message métier spécifique.                                                                                                                                                                                                                           |
| **`rejectIfAlreadyAffected`** (PLAYER) | Rejette si la cible est déjà dans `getAffectedPlayers()` du rôle exécuteur (`IAffectedPlayers`). Sans effet si l'exécuteur n'a pas de rôle ou si celui-ci n'implémente pas `IAffectedPlayers`. Même catégorie que `excludeSelf`/`maxDistanceKey` (dépend de l'exécuteur).                                                                             |
| **`alreadyAffectedMessageKey`**        | Surcharge le message « déjà affecté » (vide → `werewolf.check.already_get_power`).                                                                                                                                                                                                                                                                    |
| **`required = false`**                 | Paramètre optionnel (ignoré si `args` trop court) ; testable via `ctx.has(index)`.                                                                                                                                                                                                                                                                    |
| **Messages**                           | Toutes les clés `werewolf.check.*` sont réutilisées (aucune nouvelle clé i18n).                                                                                                                                                                                                                                                                       |
| **`argNumbers`**                       | Reste le garde-fou validé en premier ; `index >= args.length` est aussi défendu par le résolveur.                                                                                                                                                                                                                                                     |

#### Gardes-fous de classe (`@RoleCommand`)

Validés par le dispatcher **avant** `execute()` (et avant la résolution des
`@Params`), dans `Command.checkCommand` (`Command.java:155-171`). Aucun code à écrire dans `execute` — poser le flag
suffit.

| Membre                   | Défaut           | Condition                                                                  | Échec → message                   |
|--------------------------|------------------|----------------------------------------------------------------------------|-----------------------------------|
| `requiredPower`          | `false`          | `role instanceof IPower && hasPower()`                                     | `werewolf.check.power`            |
| `requiredAbilityEnabled` | **`true`**       | `role.isAbilityEnabled()`                                                  | `werewolf.check.ability_disabled` |
| `requiredLimitedUse`     | `-1` (désactivé) | si `>= 0` : `role instanceof ILimitedUse && getUse() < requiredLimitedUse` | `werewolf.check.power`            |

> ⚠️ `requiredAbilityEnabled` vaut `true` par défaut : toute `@RoleCommand` est
> bloquée tant que le rôle est désactivé (`disableAbilities()`), **sans rien
> faire**. Ne pas recoder ce check dans `execute`.
>
> Pour consommer un pouvoir unique, poser `requiredPower = true` sur la commande
> et appeler `setPower(false)` dans `execute`. Pour une limite fixe de N usages,
> implémenter `ILimitedUse` et poser `requiredLimitedUse = N` (le dispatcher
> bloque la N+1ᵉ fois). Pour une limite **dynamique** (ex. via un `@IntValue`),
> garder un check manuel (`CommandCheck.rejectIfNoUseLeft` ou un test explicite).

### 10.5 Limites v1

`@PlayerArg` exige que la cible soit un `IPlayerWW` dans la partie (résolu via
`Bukkit.getPlayer` + `getPlayerWW` + `isState`). Pour les commandes admin ciblant un joueur **en ligne arbitraire** (pas
nécessairement dans la partie), `@PlayerBukkitArg`
résout la cible en `Player` via `Bukkit.getPlayer` **sans** aucun `getPlayerWW` ni vérification d'état — c'est le cas
pour `CommandHost`, `CommandModerator`,
`CommandWhitelist`, `CommandInventory`, `CommandLate`.

Deux commandes admin restent sur le chemin hérité `ICommand` :

- `CommandTP` — utilise le sélecteur `@a` (tous les joueurs en ligne) et accepte 1 **ou** 2 cibles
  (`argNumbers = {1, 2}`), ce qui ne se déclare pas via un paramètre typé ;
- `CommandKill` — résout la cible **par nom dans la partie** (`getPlayersWW().filter(name)`)
  puis, en phase `GAME`, **rejette les joueurs en ligne** (`Bukkit.getPlayer != null` → refus) :
  logique métier contradictoire avec tout argument « joueur résolu ».

### 10.6 Couverture

42 commandes migrées (rôles, lovers, utilities, admin joueur-dans-la-partie, entiers, 5 admin joueur-en-ligne via
`@PlayerBukkitArg`, et 3 rôles en UUID via `byUuid` :
`CommandShaman`, `CommandScapeGoat`, `CommandBarbarian`). 2 commandes admin restent sur le chemin hérité : `CommandTP`,
`CommandKill` (cf. §10.5).

### 10.7 Helpers métier — `CommandCheck`

`commands/CommandCheck.java` factorise des **checks métier récurrents** à appeler depuis
`execute(...)` (chemin hérité, ou checks non-déclarables). Trois helpers :

| Méthode                                                         | Rôle                                                                                           | Équivalent déclaratif ?                                                                                                                                                         |
|-----------------------------------------------------------------|------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `rejectIfAlreadyAffected(sender, role, target, messageKey)`     | Rejette si la cible est dans `role.getAffectedPlayers()` (`instanceof IAffectedPlayers` géré). | **Oui** — `@PlayerArg(rejectIfAlreadyAffected = true, alreadyAffectedMessageKey = …)` (cf. §10.4). Le helper ne reste que pour les commandes non-paramétrées (`CommandShaman`). |
| `rejectIfTooFar(game, sender, target, distanceKey, messageKey)` | Rejette si distance exécuteur↔cible > `IntValue` `distanceKey`.                                | **Oui** — `@PlayerArg(maxDistanceKey = …, maxDistanceMessageKey = …)`.                                                                                                          |
| `rejectIfNoUseLeft(sender, role, limit)`                        | Rejette si le rôle (`ILimitedUse`) a épuisé ses utilisations.                                  | Partiel — `@RoleCommand(requiredLimitedUse = N)` couvre les limites fixes ; le helper reste pour les limites **dynamiques** (ex. `CommandFox` via `IntValue`).                  |

Le check « déjà affecté » est désormais **déclaratif** : 11 commandes paramétrées (`Guard`, `Thug`, `Raven`,
`Protector`, `Spy`, `Trapper`, `Librarian`, `Benefactor`,
`Croupier`, `Detective`, `Shaman`) portent `rejectIfAlreadyAffected = true` sur leur
`@PlayerArg`, et le résolveur effectue la vérification pendant la résolution (après
`states`, avant `maxDistance`). `CommandDetective` (2 cibles) déclare le flag sur ses deux
`@PlayerArg` ; `CommandShaman` (cible en UUID) est désormais paramétrée via `byUuid`. **Aucune commande n'appelle plus
`rejectIfAlreadyAffected` manuellement** — le helper reste comme implémentation de référence (et `rejectIfNoUseLeft` est
toujours utilisé par
`CommandFox` pour une limite dynamique).

> **Différences d'ordre/message (bénignes) à connaître.** Déplacer ces checks dans le
> résolveur les fait s'exécuter **avant** `execute()`, ce qui peut inverser leur priorité
> vs. des checks restés dans `execute()`, ou ajouter un message là où il y avait retour
> silencieux :
> - `CommandCroupier` — `repeated_target` reçoit désormais le préfixe `Prefix.RED` ;
> - `CommandBenefactor` — liste pleine (≥3) **et** cible déjà présente → `already_use_on_player`
>   au lieu de `too_many_players` ;
> - `CommandDetective` — même cible twice **et** déjà inspectée → `already_inspect` au lieu
>   de `two_distinct_player` ;
> - `CommandShaman` / `CommandScapeGoat` — cible absente → message `not_in_game_player`
>   (auparavant retour silencieux) ; de plus `Shaman` voit `already_get_power` primer sur
>   `not_enough_life` / `cannot_use` en cas de chevauchement ;
> - `CommandBarbarian` — cible absente → `not_in_game_player` au lieu de `player_not_found`
>   (le cas hors-`JUDGEMENT` conserve `player_not_found` via `stateMessageKey`).
>
> Dans tous les cas le comportement métier (rejet) est inchangé ; seul le message (ou son
> ordre) diffère dans ces cas limites.

> **Groupe UUID + « en ligne » requis (non couvert).** `CommandInfect`,
> `CommandDevotedServant`, `CommandWitch` résolvent une cible par UUID **mais exigent en
> plus qu'elle soit en ligne** (`Bukkit.getPlayer(uuid) != null`). `byUuid` seul ne suffit
> pas (pas de vérification de présence en ligne) ; une future option
> `@PlayerArg(byUuid = true, requireOnline = true)` les couvrirait.

---

## 11. Système de bâtiments

Le système de bâtiments génère, **à la création de la map** (cf. §6.1, `MapManager.generatePlatform`), des structures
aléatoires dans un rayon configurable (`IntValueBase.BUILDINGS_RADIUS`, défaut **400** blocs)
autour du spawn. Il s'agit d'une `@Configuration` classique (`ConfigBase.BUILDINGS`,
`configs/Buildings.java`) avec des sous-configs booléennes par type de bâtiment, des `@IntValue` (rayon, comptes par
type, capacité d'urne) et des `@Timer` imbriqués (déclenchement des 3 sessions de vote par urne). Activé par défaut ;
entièrement toggleable par l'hôte.

### 11.1 Structures data-driven (JSON)

Les structures physiques sont définies par des **fichiers JSON** dans `resources/buildings/`
(`vote_office.json`, `council_chamber.json`, `decorative_house.json`, `sanctuary.json`) — surchargeables par
l'utilisateur via `plugins/WereWolfPlugin/buildings/*.json`. Format : palette de caractères (`W`, `J`, `P`, `G`, `.`…)
mappée sur des noms `UniversalMaterial` ou des noms de `Material` 1.13+ avec fallback legacy (`STONE_PRESSURE_PLATE`→
`STONE_PLATE`, `OAK_PLANKS`→`WOOD`…) pour la compatibilité **1.8.8 → 1.21.4**. Chaque couche `y` est un tableau de
lignes (une par `z`), chaque caractère un bloc
`x`. Les blocs « fonctionnels » (urne, plaques, vitre, bannière) sont déclarés dans une section
`functional` de coordonnées relatives.

- `BuildingDefinition` (parse minimal-json) → `StructurePaster.paste(definition, world, x, y, z, bannerColor)`
  force-load les chunks puis pose les blocs. La **bannière colorée** est posée via
  `UniversalMaterial.WHITE_BANNER` + `Banner.setBaseColor(DyeColor)` (cross-version).
- `BuildingLoader` charge les fichiers bundled + externes, indexés par `BuildingType`.

### 11.2 Types de bâtiments

| Type              | Config                                | Bannière              | Fonction                                                                                        |
|-------------------|---------------------------------------|-----------------------|-------------------------------------------------------------------------------------------------|
| `DECORATIVE`      | —                                     | aucune                | purement décoratif                                                                              |
| `VOTE_OFFICE`     | `ConfigBase.URN_VOTE`                 | rouge                 | contient une **urne** (jukebox) pour voter                                                      |
| `COUNCIL_CHAMBER` | `ConfigBase.BUILDING_COUNCIL_CHAMBER` | bleu                  | 2 plaques de pression séparées par une vitre pour consulter l'Honneur                           |
| `SANCTUARY`       | `ConfigBase.BUILDING_SANCTUARY`       | rouge/bleu/vert/jaune | apparaît après le 2ᵉ vote, visite 10 s → pomme d'or ; tous visités → regain de parole + Honneur |

`BuildingsManager` est instancié par `GameManager` (champ `buildingsManager`), enregistré comme listener via
`ListenersManager` (`BuildingListener`). `generate()` est appelé depuis
`MapManager.generatePlatform` après la plateforme (garde `if (world.equals(this.world))`).

### 11.3 Bureaux de vote — vote par urne (`UrnVote` + `UrnVoteManager`)

Le vote par urne est une **config top-level** `ConfigBase.URN_VOTE` (`configs/UrnVote.java`), **incompatible avec
`ConfigBase.VOTE`** (vote à main levée) via `incompatibleConfigs` — l'hôte ne peut activer que l'un des deux (cf.
`ConfigurationsGUI` qui bloque l'activation tant que l'autre est active). Les bureaux de vote ne sont générés que si
`URN_VOTE` est actif (et `BUILDINGS`). Par défaut `URN_VOTE`
est actif et `VOTE` inactif (nouveau mode par défaut).

3 **sessions** surviennent au cours de la partie, déclenchées par 3 `@Timer` décrémentaux (imbriqués dans la config
`URN_VOTE`) dont `onZero` fire un event API dédié (`events/buildings/UrnVote{1,2,3}StartEvent`). Le listener
`configs/UrnVote` réagit → `UrnVoteManager.startSession(n)` (ouvre le vote pour
`TimerBase.URN_VOTE_DURATION` s) puis `endSession()` (sanctions). Côté joueur, clic-droit sur l'urne (jukebox) →
`BuildingListener` ouvre `UrnChoiceGui` / `UrnTargetGui` (smart-invs).

**3 façons de voter** (état par joueur tracé dans `UrnVoteManager`) :

1. Voter contre un joueur — **une seule fois** au total, dans un seul bureau. Cible limitée aux joueurs **déjà croisés**
   (`IPlayerWW.getRole().getPlayersMet()`, comme le vote à main levée).
2. Vote blanc — une fois (première urne ouverte).
3. Vote blanc supplémentaire — un par urne (`Set<Location>` par joueur).

**Capacité décroissante** : chaque urne a une capacité (`IntValueBase.URN_CAPACITY`) qui **diminue de 1 à chaque
session** à partir de la 2ᵉ (`VoteOffice.reduceCapacity` + `resetSession` en début de session). Une urne pleine
(`votesCast >= capacity`) est inutilisable.

**Sanctions** (`endSession` → joueur le plus voté) :

| Session | Sanction                                                                                                       |
|---------|----------------------------------------------------------------------------------------------------------------|
| 1       | -2 Honneur + **Exposed** réel                                                                                  |
| 2       | -2❤ permanents + -2 Honneur + Exposed                                                                         |
| 3       | -2❤ + -2 Honneur + **silence** (Weakness+Slowness 5 min, id `werewolf.urn_vote_silence`) + révélation du rôle |

L' **Exposed** est branché sur le vrai `ExposedEvent` (API) via `utils/ExposedHelper.expose(game, playerWW)`
(réutilise la logique de `random_events/Exposed.announce()` : 2 rôles d'autres camps + rôle d'affichage, shuffle, fire
cancellable, broadcast `werewolf.random_events.exposed.message`). Chaque vote fire aussi
`events/buildings/UrnVoteCastEvent` (`@StatisticsEvent`, `@StatisticsPlayer` + `@StatisticsTarget`) pour les stats. Les
variations d'Honneur (vote contre +1, vote blanc -1) ne s'appliquent que si
`ConfigBase.HONOR` est actif.

### 11.4 Chambres du conseil

À tout moment, 2 joueurs se placent sur les 2 plaques de pression séparées par une vitre.
`BuildingListener` écoute `Action.PHYSICAL` → détecte le partenaire sur l'autre plaque (`getNearbyEntities` y-radius
2.0) → révèle l'Honneur de chacun (`council_reveal`). Requiert
`ConfigBase.HONOR` actif.

**Limite par joueur** : 3 consultations / partie, 10 min de cooldown entre chacune
(`BuildingsManager.COUNCIL_MAX_USES` / `COUNCIL_COOLDOWN`, tracé via `game.getTimer()`). Les 2 joueurs consomment chacun
un usage. Vérifié via `canConsult(uuid)` avant la révélation.

### 11.5 Sanctuaires

`generateSanctuaries()` (planifié **60 s après la fin du 2ᵉ vote** par `Buildings.scheduleSanctuaries`)
place 4 sanctuaires (`IntValueBase.BUILDINGS_SANCTUARY_COUNT`, défaut 4) en anneau externe (~80% du rayon), chacun d'une
couleur unique (rouge/bleu/vert/jaune) avec sa bannière colorée.

- **Particules** : tant qu'un joueur n'a pas visité un sanctuaire, `ParticleHelper.sendColored` lui envoit des
  `Particle.REDSTONE` + `DustOptions` colorées (reflection pure → no-op sur 1.8-1.12, la bannière colorée servant alors
  de repère).
- **Visite** : rester ≤ 5 blocs pendant 10 s (`tickSanctuaries`, tâche répétée 1 s) →
  `Sanctuary.markVisited`, pomme d'or, fire `events/buildings/SanctuaryVisitEvent`
  (`@StatisticsEvent` + `@StatisticsPlayer`).
- **Récompense** (tous visités) : `PotionModifier.remove(WEAKNESS/SLOWNESS, "werewolf.urn_vote_silence")`
  (regain de parole si silencé) + variation d'Honneur : **+1** si `Camp.VILLAGER`, **0** sinon (loups / solitaires).

### 11.6 Contrats API ajoutés (`WereWolfAPI`)

- `basekeys/ConfigBase` : `BUILDINGS`, `URN_VOTE`, `BUILDING_COUNCIL_CHAMBER`,
  `BUILDING_SANCTUARY`. (`URN_VOTE` et `VOTE` sont mutuellement exclusifs via `incompatibleConfigs`.)
- `basekeys/IntValueBase` : `BUILDINGS_RADIUS`, `BUILDINGS_VOTE_OFFICE_COUNT`,
  `BUILDINGS_COUNCIL_CHAMBER_COUNT`, `BUILDINGS_DECORATIVE_COUNT`, `BUILDINGS_SANCTUARY_COUNT`,
  `URN_CAPACITY`.
- `basekeys/TimerBase` : `URN_VOTE_1_START`, `URN_VOTE_2_START`, `URN_VOTE_3_START`, `URN_VOTE_DURATION`.
- `events/buildings/` : `UrnVote1StartEvent`, `UrnVote2StartEvent`, `UrnVote3StartEvent` (timers
  `onZero`), `UrnVoteCastEvent` (`@StatisticsEvent`), `SanctuaryVisitEvent` (`@StatisticsEvent`).

> Le vote à main levée existant (`/ww vote`, `configs/Vote.java`, `IVoteManager`) est inchangé et
> coexiste avec le vote par urne.

---

## 12. Annexe — fichiers clés

### WereWolfAPI (contrats)

| Rôle                                  | Fichier                                                                                                                                                |
|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Définitions d'annotations             | `WereWolfAPI/src/main/java/fr/ph1lou/werewolfapi/annotations/*.java` (+ `statistics/*.java`)                                                           |
| Contrat du registre                   | `WereWolfAPI/.../registers/IRegisterManager.java:28`                                                                                                   |
| Conteneur `Wrapper`                   | `WereWolfAPI/.../utils/Wrapper.java:3`                                                                                                                 |
| Lecteur réflexif rôles                | `WereWolfAPI/.../role/impl/RoleImpl.java:87` et `:263`                                                                                                 |
| Lecteur réflexif lovers               | `WereWolfAPI/.../lovers/impl/LoverBaseImpl.java:14` et `:27`                                                                                           |
| Description de rôle                   | `WereWolfAPI/.../role/utils/DescriptionBuilder.java:72`                                                                                                |
| Interfaces-marqueurs de rôle          | `WereWolfAPI/.../role/interfaces/` (`IPower`, `IAffectedPlayers`, `ILimitedUse`, `IProgress`, `ITransformed`, `IInvisible`, `IGuesser`)                |
| Classes de base de rôle               | `WereWolfAPI/.../role/impl/` (`RoleImpl`, `RoleWereWolf`, `RoleNeutral`, `RoleWithLimitedSelectionDuration`)                                           |
| Event « faux loup »                   | `WereWolfAPI/.../events/werewolf/AppearInWereWolfListEvent.java:8`                                                                                     |
| Event de taux de Force                | `WereWolfAPI/.../events/werewolf/RequestStrengthRateEvent.java:8` (cf. §6.8)                                                                           |
| Event message de mort                 | `WereWolfAPI/.../events/game/life_cycle/AnnouncementDeathEvent.java:13` (`setRole` override)                                                           |
| Rôle de mort (`IPlayerWW`)            | `WereWolfPlugin/.../game/PlayerWW.java:699` (`getDeathRole`/`addDeathRole`/`removeDeathRole`)                                                          |
| Modificateur d'aura                   | `WereWolfAPI/.../player/impl/AuraModifier.java:10` + `role/interfaces/IAura.java`                                                                      |
| Contrat des couples                   | `WereWolfAPI/.../lovers/ILover.java` (`getLovers`, `swap`, `dissolve`), `ILoverManager.java` (`addLover`/`removeLover`) (cf. §5.5)                     |
| Events de couple                      | `WereWolfAPI/.../events/lovers/` (`DonEvent` cancellable, `LoverDeathEvent`, `RevealLoversEvent`, `AroundLoverEvent`)                                  |
| Point d'entrée API                    | `WereWolfAPI/.../GetWereWolfAPI.java:16`                                                                                                               |
| Enums référencés                      | `WereWolfAPI/.../enums/` (`Category`, `RoleAttribute`, `Aura`, `StateGame`, `StatePlayer`, `UniversalMaterial`)                                        |
| Constantes de clés i18n               | `WereWolfAPI/.../basekeys/*.java` (`RoleBase`, `LoverBase`, `TimerBase`, `ConfigBase`, `ScenarioBase`, `IntValueBase`, `EventBase`, `Prefix`)          |
| Annotations de paramètres de commande | `WereWolfAPI/.../annotations/Params.java`, `PlayerArg.java`, `PlayerBukkitArg.java`, `IntArg.java`, `StringArg.java`, `EnumArg.java`, `ChoiceArg.java` |
| Contexte de commande résolu           | `WereWolfAPI/.../commands/CommandContext.java`                                                                                                         |
| Interfaces de commande paramétrées    | `WereWolfAPI/.../commands/IParameterizedCommand.java`, `IParameterizedCommandRole.java`                                                                |

### WereWolfPlugin (exécution)

| Rôle                                                 | Fichier                                                                                                                                     |
|------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| Classe principale + `@ModuleWerewolf`                | `WereWolfPlugin/src/main/java/fr/ph1lou/werewolfplugin/Main.java:32`                                                                        |
| Démarrage du registre                                | `WereWolfPlugin/.../Main.java:64` (`onEnable`)                                                                                              |
| Scan / registre                                      | `WereWolfPlugin/.../Register.java`                                                                                                          |
| Scan de jar (maison)                                 | `WereWolfPlugin/.../utils/ReflectionUtils.java`                                                                                             |
| Instanciation scenarios/timers/configs/events        | `WereWolfPlugin/.../game/ListenersManager.java`                                                                                             |
| Assignation des rôles                                | `WereWolfPlugin/.../timers/RoleDuration.java:99`                                                                                            |
| Commandes admin / joueur                             | `WereWolfPlugin/.../commands/Admin.java`, `commands/Command.java`                                                                           |
| Décrément timers + `onZero`                          | `WereWolfPlugin/.../tasks/GameTask.java:139`                                                                                                |
| Lecture stats sur events                             | `WereWolfPlugin/.../statistiks/StatisticsEvents.java:48`                                                                                    |
| Valeurs par défaut timers/configs                    | `WereWolfPlugin/.../game/StorageConfiguration.java`                                                                                         |
| Résolveur d'arguments de commande                    | `WereWolfPlugin/.../commands/ArgumentResolver.java`                                                                                         |
| Canal de message commande                            | `WereWolfPlugin/.../commands/MessageSender.java`                                                                                            |
| Contexte de commande (impl)                          | `WereWolfPlugin/.../commands/CommandContextImpl.java`                                                                                       |
| Helpers de checks métier commande                    | `WereWolfPlugin/.../commands/CommandCheck.java`                                                                                             |
| Exemple de rôle complet (Illusionniste)              | `WereWolfPlugin/.../roles/villagers/Illusionist.java:39` + `commands/roles/villager/illusionist/CommandIllusionist.java:14` (cf. §8.6)      |
| Exemple rôle falsifiant affichage + mort (Faussaire) | `WereWolfPlugin/.../roles/werewolfs/CounterfeiterWereWolf.java:38` + `commands/roles/werewolf/counterfeiter/` (cf. §5.1, §6.7)              |
| Cycle des events de mort                             | `WereWolfPlugin/.../listeners/DeathListener.java:64` (`onPlayerDeath` → `FinalDeathEvent` `:161` → `AnnouncementDeathEvent` `:198`)         |
| Scaling Force/Résistance (PvP)                       | `WereWolfPlugin/.../listeners/PatchPotions.java:35` (`onPatchPotion` fire `RequestStrengthRateEvent`) + `configs/Honor.java:307` (cf. §6.8) |
| Handler par défaut « liste des loups »               | `WereWolfPlugin/.../listeners/WerewolfListener.java:82` (`onAppearInWereWolfList`, priorité `LOWEST`)                                       |
| Couples : base & dissolution                         | `WereWolfPlugin/.../roles/lovers/AbstractLover.java` (`dissolve`), `LoverImpl.java:36` (suicide), `AmnesiacLover.java:73` (cf. §5.5)        |
| Don de vie entre amoureux                            | `WereWolfPlugin/.../commands/roles/lovers/CommandLovers.java` (fire `DonEvent`)                                                             |
| Système de bâtiments (génération + registre)         | `WereWolfPlugin/.../buildings/BuildingsManager.java` (cf. §11)                                                                              |
| Structures JSON + paster cross-version               | `WereWolfPlugin/.../buildings/BuildingLoader.java`, `StructurePaster.java`, `resources/buildings/*.json`                                    |
| Vote par urne (3 sessions, sanctions, Exposed)       | `WereWolfPlugin/.../configs/UrnVote.java`, `buildings/UrnVoteManager.java`, `utils/ExposedHelper.java` (cf. §11.3)                          |
| Sanctuaires (particules, visite, récompense)         | `WereWolfPlugin/.../buildings/Sanctuary.java`, `ParticleHelper.java`                                                                        |
| Listener bâtiments (clic urne + plaques conseil)     | `WereWolfPlugin/.../listeners/BuildingListener.java`                                                                                        |
| GUI urne                                             | `WereWolfPlugin/.../guis/UrnChoiceGui.java`, `UrnTargetGui.java`, `UrnVoteGuiHelper.java`                                                   |

---

## 13. Système de vote par urne — internals détaillés

### 13.1 Structure des urnes (`VoteOffice`)

Contrairement à ce que le nom « coffre » pourrait laisser penser, **il n'y a pas de coffre physique** dans un bureau de
vote. L'urne est un **bloc jukebox** (`Material.JUKEBOX`). Le clic-droit sur le jukebox est **annulé**
(`BuildingListener.java:58`) pour empêcher l'ouverture d'un inventaire vanilla ; à la place, un **GUI virtuel
SmartInventory** s'ouvre (`UrnChoiceGui` → `UrnTargetGui`).

`VoteOffice` (`buildings/VoteOffice.java:14`) étend `Building` et stocke :

| Champ                       | Type                | Rôle                                                   |
|-----------------------------|---------------------|--------------------------------------------------------|
| `urnLocation`               | `Location` (final)  | Position du bloc jukebox                               |
| `baseCapacity` / `capacity` | `int`               | Capacité de votes (décrément à partir de la session 2) |
| `votesCast`                 | `int`               | Votes utilisés cette session                           |
| `votesAgainst`              | `Map<UUID,Integer>` | UUID cible → nombre de votes                           |
| `blankVotes`                | `int`               | Nombre de votes blancs                                 |
| `blankVoters`               | `Set<UUID>`         | Joueurs ayant voté blanc                               |

> ⚠️ `resetSession()` (`VoteOffice.java:62-66`) ne réinitialise **pas** `blankVotes` —
> bug latent potentiel.

### 13.2 Flux de vote

```
Clic-droit jukebox
  └─ BuildingListener.onJukeboxInteract (BuildingListener.java:31-78)
       ├─ vérifie URN_VOTE actif, joueur vivant
       ├─ special case: MesquinWereWolf rig-urn (lignes 60-70)
       └─ UrnChoiceGui.getInventory(office).open(player)
            ├─ "Voter contre" → UrnTargetGui (liste des joueurs croisés via getPlayersMet())
            │    └─ UrnVoteGuiHelper.recordVoteAgainst(game, voter, office, target)
            │         ├─ office.recordVoteAgainst(targetUUID)  ← stockage per-urn
            │         ├─ urn.markVotedAgainst(voterUUID)       ← verrou global (1 seul vote-contre)
            │         ├─ +1 Honneur au votant (sauf si urne trafiquée par Mesquin)
            │         └─ fire UrnVoteCastEvent (cancellable, per-vote)
            └─ "Vote blanc" → UrnVoteGuiHelper.recordBlank
```

**Verrouillage** : un joueur ne peut voter contre quelqu'un qu'**une seule fois au total**
(`UrnVoteManager.votedAgainst`, `Set<UUID>` global). Les votes blancs permettent un premier blanc global + un blanc
supplémentaire par urne distincte (`blankVotedUrns`,
`Map<UUID, Set<Location>>`).

### 13.3 Dépouillement (`UrnVoteManager.endSession`)

Le dépouillement est déclenché par une **tâche planifiée** (`UrnVote.java:76-78`) à la fin de la durée de session
(`TimerBase.URN_VOTE_DURATION`). **Aucun event n'est fire au moment du dépouillement** — la logique est entièrement dans
`UrnVoteManager.endSession()`
(`UrnVoteManager.java:92-127`).

```
endSession(sessionNumber):
  1. Agrège les votesAgainst de TOUS les VoteOffice → Map<UUID,Integer> totals
  2. Trouve le max (strict ">" → premier arrivé gagne les égalités, ordre HashMap non déterministe)
  3. winner = game.getPlayerWW(uuid)
  4. applySanctions(target, sessionNumber, max)
```

**Sanctions escaladantes** (`applySanctions`, `UrnVoteManager.java:129-169`) :

| Session | Sanction                                              |
|---------|-------------------------------------------------------|
| ≥ 1     | -2 Honneur + Exposed (révélation de rôle)             |
| ≥ 2     | + -2❤ permanents                                     |
| ≥ 3     | + Weakness+Slowness 5 min + rôle annoncé publiquement |

### 13.4 Hooks pour extensions (Gri-gri, etc.)

- **`UrnVoteCastEvent`** (fire per-vote, cancellable) — porte voter, target, blank, session, office. Utilisé par le
  Citizen (`Citizen.java:210`).
- **Pattern Mesquin** — `MesquinWereWolf.rigUrn(Location)` + `isAnyUrnRiggedAt(game, loc)`
  coché dans `UrnVoteGuiHelper.java:40`. C'est l'analogue le plus proche de « placer un objet dans une urne ».
- **Pas d'event au dépouillement** — pour réagir au dépouillement, modifier
  `endSession()` directement (entre la détermination du winner ligne 124 et
  `applySanctions` ligne 126), ou créer un nouvel event fire à cet endroit.

---

## 14. Système d'Honneur du Ronin & interactions de vol

### 14.1 Plage d'Honneur personnalisée

Le Ronin possède son **propre système d'Honneur** (`roninHonor`) **séparé** de l'Honneur standard
(`IPlayerWW.getHonor()`, clamped `[-3, 3]`). La plage du Ronin est **`[-4, 4]`**.

- `onHonorChange` (`Ronin.java:131`, `EventPriority.LOWEST`) **annule tous les
  `HonorChangeEvent`** externes pour le joueur Ronin → l'Honneur standard ne varie jamais pendant que le Ronin est
  actif.
- Les variations d'Honneur passent par `applyHonorChange(HonorSource)` qui utilise
  `roninHonor` (champ interne) avec son propre clamp `Math.max(-4, Math.min(4, ...))`.

### 14.3 Synchronisation au Swap (`SwapEvent`)

Quand le Ronin est swappé (événement aléatoire Swap), le joueur (ancien Ronin) récupère un autre rôle. Son Honneur
standard doit être synchronisé avec le `roninHonor` (clamped à
`[-3, 3]`), sinon il reste à sa valeur d'avant le Ronin.

```java

@EventHandler(priority = EventPriority.LOWEST)
public void onSwap(SwapEvent event) {
    if (player pas concerné)return;
    syncing = true;                              // désactive onHonorChange
    modifyHonor(clamped - getHonor());           // sync (clamped à [-3, 3])
    syncing = false;
}
```

Le flag `syncing` est nécessaire car `modifyHonor()` fire un `HonorChangeEvent`
**synchroniquement** (`PlayerWW.java:773-774`), et si l'event est annulé, `modifyHonor`
**revert** la valeur (`PlayerWW.java:775-776`). Sans `syncing`, le `onHonorChange`
annulerait la synchronisation.

---

## 15. Bonnes pratiques temporelles — timer + event vs polling `second()`

`ListenerWerewolf.second()` est appelée **chaque seconde** par `GameTask` (§6.4) sur tous les rôles, scénarios, timers,
configs et random events enregistrés. C'est un hook pratique pour de la logique qui doit **progresser** chaque seconde
(décrément d'un compteur, effets visuels continus comme les particules du `BloodMoon`, vérifications de proximité
répétées de l'`AmnesicLover` ou de l'`AlerterWereWolf`).

> ⚠️ **À éviter** : utiliser `second()` pour détecter la **fin d'une durée fixe** (polling d'un horodatage
> `game.getTimer() + DURÉE`). Cela consomme un branchement à chaque tick du jeu pour un événement qui ne se produit
> qu'une fois, et couple le déclenchement au rythme du `GameTask` : si la tâche s'interrompt en fin de partie, le
> déclenchement est silencieusement perdu.

### Pratique recommandée — scheduler Bukkit + event dédié

Pour une mécanique « au bout de N secondes, faire X » :

1. À l'initialisation de la mécanique, planifier une tâche différée via le scheduler Bukkit
   (`Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks)` ou `BukkitRunnable.runTaskLater`).
   `1 seconde = 20 ticks`. Récupérer le plugin via `JavaPlugin.getPlugin(Main.class)`.
2. Dans la tâche, **fire un event dédié** (`Bukkit.getPluginManager().callEvent(new MonEvent(...))`) plutôt que
   d'exécuter la logique en ligne. L'event est `Cancellable` et annoté `@StatisticsEvent`, et porte toute l'info
   nécessaire aux listeners (convention §9 « `IPlayerWW` > `UUID` »).
3. La logique métier vit dans un `@EventHandler` (typiquement `EventPriority.HIGHEST` pour laisser les autres
   mécaniques annuler d'abord). Elle vérifie `event.isCancelled()` avant d'appliquer l'effet.
4. Garder une référence sur la `BukkitTask` et l'annuler (`cancel()`) si la mécanique est interrompue (mort, fin de
   partie, cycle jour/nuit) pour éviter un déclenchement fantôme.

**Bénéfices** : déclenchement ponctuel (zéro coût hors de l'instant T), découplage entre la cause temporelle et la
logique (d'autres rôles/configs peuvent observer ou bloquer via l'event), et la clôture est annulable proprement.

### Exemple — `NightAssembly` (assemblée nocturne)

Avant (polling via `second()`) :

```java
private int assemblyEndTimer = 0;

private void startAssembly() {
    assemblyEndTimer = game.getTimer() + ASSEMBLY_DURATION;   // horodatage cible
    ...
}

@Override
public void second() {                              // appelé chaque seconde, toujours
    if (!assemblyActive) return;
    if (game.getTimer() < assemblyEndTimer) return; // polling jusqu'à échéance
    assemblyActive = false;
    processResults();
}
```

Après (timer + event) — event dédié
`WereWolfAPI/.../events/random_events/NightAssemblyEndEvent.java` (`@StatisticsEvent`, `Cancellable`, porte les
votants `Set<IPlayerWW>`) :

```java
private void startAssembly() {
    ...
    assemblyTask = Bukkit.getScheduler().runTaskLater(
            JavaPlugin.getPlugin(Main.class),
            () -> {
                if (game.isState(StateGame.END) || !assemblyActive) return;
                Set<IPlayerWW> voterWWs = new HashSet<>();
                voters.forEach(u -> game.getPlayerWW(u).ifPresent(voterWWs::add));
                Bukkit.getPluginManager().callEvent(new NightAssemblyEndEvent(voterWWs));
            },
            ASSEMBLY_DURATION * 20L);
}

@EventHandler(priority = EventPriority.HIGHEST)
public void onAssemblyEnd(NightAssemblyEndEvent event) {
    if (!assemblyActive) return;
    assemblyActive = false;
    cancelAssemblyTask();
    if (event.isCancelled()) return;
    processResults();
}
```

La surcharge de `second()` est supprimée ; le déclenchement n'a plus lieu qu'à l'instant T, et d'autres mécaniques
peuvent s'opposer à la clôture de l'assemblée en annulant `NightAssemblyEndEvent`.

> **Quand garder `second()`** : uniquement pour de la logique réellement **continue** (compte à rebours affiché,
> effets périodiques, vérifications de proximité répétées). Pour un seuil temporel ponctuel, préférer le scheduler +
> event.

---

## 16. Nouveaux rôles et événements (session)

### 16.1 Loup-Garou Alerteur (`AlerterWereWolf`)

- `@Role` : `RoleBase.ALERTER_WEREWOLF`, `Category.WEREWOLF`, `defaultAura = NEUTRAL`
- Étend `RoleWereWolf` (Strength nocturne, hurlement, bonus de kill automatiques)
- Implémente `IPower` (1x/épisode) + `IAffectedPlayers` (1 cible)
- **Pouvoir** : `/ww alerte [Pseudo]` → suit la cible 3 minutes (proximité trackée dans
  `second()`, `@IntValue ALERTER_DISTANCE` défaut 20 blocs). Au bout de 3 min, alerte un Loup aléatoire avec le
  `getDisplayRole()` de la cible. Le tueur de cible doit être non-loup (pas d'alerte sur son propre camp).
- L'Alerteur reçoit le nom du Loup alerté ; le Loup alerté reçoit le nom de l'Alerteur + le rôle de la cible.

### 16.2 Loup-Garou Mesquin (`MesquinWereWolf`)

- `@Role` : `RoleBase.MESQUIN_WEREWOLF`, `Category.WEREWOLF`, `defaultAura = DARK`
- Étend `RoleWereWolf` (Strength nocturne, hurlement, bonus de kill automatiques)
- Implémente `ILimitedUse` (3 utilisations)
- **Pouvoir** : 3x par partie, trafique une urne de vote en cliquant sur le jukebox (même hors session de vote).
  `rigUrn(Location)` stocke l'urne trafiquée.
  `isAnyUrnRiggedAt(WereWolfAPI, Location)` est une méthode statique qui scanne tous les joueurs (les rigs persistent si
  le Mesquin meurt).
- Les Villageois qui votent dans une urne trafiquée ne gagnent pas d'Honneur (check dans
  `UrnVoteGuiHelper.recordVoteAgainst`, pas dans `Honor.java` car l'Honneur du vote par urne est géré dans
  `UrnVoteGuiHelper`, pas dans `Honor`).
- Un Mesquin avec des utilisations restantes **rigge** en cliquant (il ne vote pas). Un Mesquin sans utilisation
  restante **vote** normalement.

### 16.3 Cupidon Rancunier (événement aléatoire)

- `@RandomEvent` : `EventBase.CUPID_RANCUNIER`
- **Déclenchement** : à la mort du couple (LoverDeathEvent ou AmnesiacLoverDeathEvent)
  après l'épisode 5, si le Cupidon est vivant et qu'aucun Rival n'est dans la partie.
- **Effet** : joue `Sound.AMBIENCE_THUNDER`, `setTransformedToVillager(false)` +
  `setSolitary(true)` (le Cupidon devient neutre solitaire, gagne seul), `addAuraModifier(
  Aura.DARK)`, envoie un message de transformation, `register(false)` (one-shot),
  `game.checkVictory()`.

### 16.4 Assemblée nocturne (événement aléatoire)

- `@RandomEvent` : `EventBase.NIGHT_ASSEMBLY`
- **Déclenchement** : à la première nuit d'un épisode aléatoire ≥ 3 (50% de chance chaque nuit, une seule fois par
  partie).
- **Phase de vote** (1 minute) : message aux Loups, `assemblyActive = true`. Les Loups utilisent
  `/ww assemblee [Pseudo]` ( `@RoleCommand` avec `roleKeys = {}`, disponible à tous, check `isWereWolf()` interne).
  Votes stockés dans `Map<UUID,UUID>` (voter → target).
- **Fin de l'assemblée** (scheduler `runTaskLater` de `ASSEMBLY_DURATION * 20` ticks → fire
  `NightAssemblyEndEvent`, `@StatisticsEvent` cancellable) : le handler `onAssemblyEnd` (`HIGHEST`) compte les votes,
  trouve le plus ciblé. Votants : `-1 Honneur` + flèche directionnelle (ActionBarEvent + `Utils.updateArrow`) vers le
  joueur le plus ciblé. Non-votants : `+1 Honneur`. (Voir §15 pour la pratique « timer + event vs `second()`. »)
- **Fin de la nuit** (`DayEvent`) : notifie le joueur ciblé + `Weakness I` (infinie, clé `EventBase.NIGHT_ASSEMBLY`).
- **Nuit suivante** (`NightEvent`) : retire la `Weakness` (fin de l'épisode).
- La commande accède à l'instance `NightAssembly` via
  `game.getListenersManager().getRandomEvent(EventBase.NIGHT_ASSEMBLY)`.

### 16.5 Récapitulatif des fichiers ajoutés

| Élément            | Fichiers                                                                                                                                                                                                                      |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Alerteur           | `WereWolfAPI/.../basekeys/RoleBase.java` (+ `ALERTER_WEREWOLF`), `IntValueBase.java` (+ `ALERTER_DISTANCE`), `WereWolfPlugin/.../roles/werewolfs/AlerterWereWolf.java`, `commands/roles/werewolf/alerter/CommandAlerter.java` |
| Mesquin            | `WereWolfAPI/.../basekeys/RoleBase.java` (+ `MESQUIN_WEREWOLF`), `WereWolfPlugin/.../roles/werewolfs/MesquinWereWolf.java`, `listeners/BuildingListener.java` (modifié), `guis/UrnVoteGuiHelper.java` (modifié)               |
| Cupidon Rancunier  | `WereWolfAPI/.../basekeys/EventBase.java` (+ `CUPID_RANCUNIER`), `WereWolfPlugin/.../random_events/CupidRancunier.java`                                                                                                       |
| Assemblée nocturne | `WereWolfAPI/.../basekeys/EventBase.java` (+ `NIGHT_ASSEMBLY`), `WereWolfAPI/.../events/random_events/NightAssemblyEndEvent.java`, `WereWolfPlugin/.../random_events/NightAssembly.java`, `commands/roles/werewolf/CommandAssembly.java`                         |
