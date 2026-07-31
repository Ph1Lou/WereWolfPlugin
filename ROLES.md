# Rôles du plugin WereWolfPlugin — Documentation technique (basée sur le code Java)

Ce document décrit le **comportement réel** de chacun des 99 rôles, établi en lisant l'implémentation Java des classes de rôle (`WereWolfPlugin/.../roles/`), leurs commandes dédiées (`commands/roles/`) et leurs events dédiés (`WereWolfAPI/.../events/roles/`). Les métadonnées (camp, aura, attribut) proviennent de l'annotation `@Role` ; les noms affichés du fichier de traduction `fr_FR.json`.

## Légende

- **Camp** : Village / Loups-Garous / Neutre (objectif propre). `getCamp()` est dérivé de `isNeutral()`/`isWereWolf()`.
- **Aura** : Obscure (perçu Loup) / Lumineuse (perçu Villageois) / Neutre. `RoleWereWolf` donne automatiquement Vision Nocturne permanente + Force la nuit à tous les loups natifs.
- **Attribut** : nature du rôle (Information, Information mineure, Hybride, Loup-Garou, Villageois, Neutre).
- **Classe de base** : `RoleImpl` (villageois/infectable), `RoleWereWolf` (loup natif), `RoleNeutral` (neutre), `RoleWithLimitedSelectionDuration` (pouvoir à fenêtre de sélection).
- **Marqueurs** : interfaces implémentées — `IPower` (pouvoir unique), `IAffectedPlayers` (cibles mémorisées), `ILimitedUse` (usages limités), `IProgress`, `ITransformed`, `IInvisible`, `IGuesser`.
- **Commande** : pouvoir activé via `/ww <commande>`.

Total : **99 rôles** répartis en 3 camps.

## Camp : Villageois

### Analyste

- **Clé :** `analyst`
- **Classe :** `Analyst` (extends `RoleImpl`) · **Marqueurs :** ILimitedUse, IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Rôle à deux commandes distinctes. Timer imbriqué `ANALYSE_DURATION` (défaut 1800s, meetUp 360s, `decrementAfterRole=true`) — bloque le premier pouvoir tant qu'il n'a pas atteint 0 (CommandAnalystSee.java:43).

Pouvoir "voir" (`/ww command_see <joueur>`, `requiredLimitedUse=5` donc 5 utilisations max) : refusé tant que `ANALYSE_DURATION>0`. Soumis à un cooldown d'un cycle jour (`DAY_DURATION`) via un flag `coolDownDisabledPower` réarmé par tâche différée (CommandAnalystSee.java:51-59). Incrémente `use`. Filtre les `PotionModifier` additifs de la cible sur {STRENGTH, RESISTANCE, WEAKNESS, SPEED, INVISIBILITY} et déclenche `AnalystEvent(hasEffect)` (TellableStoryEvent). Ajoute la cible aux `affectedPlayers`. Indique seulement si la cible possède au moins un de ces effets (CommandAnalystSee.java:64-86).

Pouvoir "analyser" (`/ww command_analyse <joueur>`, `requiredPower=true`) : la cible doit déjà avoir été "vue" (présente dans affectedPlayers, CommandAnalystAnalyse.java:39). Consomme le power (`setPower(false)`). Déclenche `AnalystExtraDetailsEvent` contenant l'ensemble complet des types d'effets de potion actifs de la cible, envoyés nom par nom à l'analyste (CommandAnalystAnalyse.java:46-64). La cible est notifiée (message `call_back` / `call_back_no_villager` selon son camp). L'analyste reçoit une aura DARK temporaire (`addAuraModifier`, CommandAnalystAnalyse.java:73).

Aucun effet passif, aucun `@EventHandler` dans la classe de rôle (toute la logique est dans les commandes). `recoverPower()` vide. La description affiche les usages restants (5-use) et un timer = `ROLE_DURATION + ANALYSE_DURATION` (Analyst.java:45-51).

### Ancien

- **Clé :** `elder`
- **Classe :** `Elder` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Pouvoir passif : tant que `power=true` et `isAbilityEnabled()`, l'Ancien reçoit **Resistance** permanente via `recoverPotionEffect()` (Elder.java:65-75). Le pouvoir est consommé (`setPower(false)`) à la première mort.

`onSecondDeathEvent` (Elder.java:77-119, priority LOW, ignoreCancelled) : déclenché à la mort de l'Ancien. Si `hasPower()` et `isAbilityEnabled()`, tire `ElderResurrectionEvent` en déterminant si le tueur est un villageois (`killerWW.get().getRole().isCamp(Camp.VILLAGER)`), puis `setPower(false)` :
- **Si le tueur est un villageois** (`isKillerAVillager()` true) : le tueur subit `-10` PV (`removePlayerHealth(10)`), ses capacités sont désactivées (`disableAbilities()`), et il reçoit un message d'avertissement. L'Ancien meurt normalement (résurrection annulée implicitement).
- **Si le tueur est un loup-garou** : `event.setCancelled(true)`, l'Ancien est **ressuscité** (`game.resurrection()`), et ses capacités sont désactivées (`this.disableAbilities()`).
- Si aucun tueur ou autre cas : le pouvoir est consommé sans effet spécial.

`disableAbilitiesRole()` (Elder.java:121-126) : retire l'effet Resistance lié à la clé du rôle. Aucune commande dédiée. Aucun timer/IntValue. Pas de condition de victoire perso (camp VILLAGER via RoleImpl).

### Astronome

- **Clé :** `astronomer`
- **Classe :** `Astronomer` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Information mineure

`defaultAura=DARK` mais l'aura bascule à chaque cycle : `onDay` ajoute un `AuraModifier(DARK, overwrite=false)`, `onNight` ajoute `AuraModifier(LIGHT, overwrite=false)` (Astronomer.java:127-135).

Effet passif : `recoverPotionEffect()` donne Vision Nocturne permanente (Astronomer.java:99-101).

Commande `/ww command` (`requiredPower`, 0 arg) : uniquement la nuit (`game.isDay(Day.DAY)` → refus). Sélectionne aléatoirement un joueur vivant qui est neutre OU loup ET à plus de 20 blocs (CommandAstronomer.java:38-43). `setPower(false)`, déclenche `TrackEvent`, mémorise la cible (addAffectedPlayer stocke aussi `targetLocation` = position de la cible et `playerLocation` = position de l'astronome au moment du choix, Astronomer.java:62-66). Power restauré après 2 minutes (tâche différée 2*60*20 ticks, CommandAstronomer.java:67).

`second()` : tant que le power est consommé, envoie chaque seconde une particule "flèche" (`Utils.sendParticleArrow`) depuis la position mémorisée du joueur vers l'angle de la position mémorisée de la cible (Astronomer.java:104-125). Les positions sont figées au moment de la sélection (ne se mettent pas à jour).

### Aubergiste

- **Clé :** `innkeeper`
- **Classe :** `Innkeeper` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

IntValue `INNKEEPER_DETECTION_RADIUS` (default 10, meetUp 10, step 1). `availableRooms=3` (chambres), `defaultWalkSpeed=0.2f`.

Activation par **clic droit** sur un joueur (PlayerInteractAtEntityEvent, Innkeeper.java:187-262) : si jour, vivant, `isAbilityEnabled()`, `power=true`, et `availableRooms > 0` :
- Si le joueur cliqué est un **ancien client** (`previousClientDatas`) : si `watching` et `seenPlayers` non vide : tire `InnkeeperInfoMeetEvent`, révèle un joueur vu aléatoire + le nombre de joueurs vus, met `watching=false`. Si pas de joueurs vus : message d'erreur.
- Si le joueur cliqué est un **client actuel** (`clientDatas`) : message "déjà client".
- Sinon : si `clientDatas.size() < availableRooms`, tire `InnkeeperHostEvent`, ajoute comme client.

`onDay` (Innkeeper.java:146-158) : si `isAbilityEnabled()` et vivant : `power=true`, déplace `clientDatas` vers `previousClientDatas`, vide `clientDatas`.

`onNight` (Innkeeper.java:160-185) : `power=false`. Pour chaque client actuel, lance une tâche asynchrone répétée toutes les `20*5` ticks (5s) qui ajoute à `seenPlayers` tous les joueurs vivants (hors aubergiste et hors client) à moins de `INNKEEPER_DETECTION_RADIUS` du client. S'annule au jour, si `watching=false`, ou fin de partie.

`onKill` (FinalDeathEvent, Innkeeper.java:83-139) :
- Si un **client meurt** : tire `ClientDeathEvent`, révèle le rôle du tueur au joueur (ou "pve"), `availableRooms--`, retire le client, met `watching=false`. Si `availableRooms == 0` : tire `InnkeeperSpeedEvent`, met `walkSpeed = 0.2 * 1.1` (+10%).
- Si un **client est le tueur** de quelqu'un : tire `ClientKillEvent`, met `watching=false` pour tous les clients, vide `clientDatas`.

`disableAbilitiesRole` (Innkeeper.java:141-144) : `power=false`. Pas de condition de victoire perso. `attribute=MINOR_INFORMATION`.

### Auramancien

- **Clé :** `auramancer`
- **Classe :** `Auramancer` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Hybride

Rôle `category=VILLAGER`, `attribute=HYBRID`, `defaultAura=NEUTRAL`. `@IntValue AURAMANCER_DISTANCE` (50 def, 25 meetUp, step 2). Rôle basé sur l'aura, sans commande `/ww` ni power consommable.

`addAuraModifier` override (Auramancer.java:122) : si non `auraLocked`, aura différente, et nom ≠ `DeathListener.KILLER` → ajoute le modifier (permanent), `AuramancerChangeAuraEvent`. `removeAuraModifier`/`removeTemporaryAuras` sont des no-ops (les changements d'aura sont définitifs).

`isNeutral()` (Auramancer.java:156) = `super.isNeutral() || (aura==DARK && !super.isWereWolf())` : un Auramancer DARK non-loup devient neutre.

`getDescription` (Auramancer.java:57) : si DARK et aucun joueur LIGHT vivant → `auraLocked=true` + Speed (définitif). Si DARK et des LIGHT existent → en choisit un au hasard comme `knownPlayer` (nom révélé).

`recoverPower` (Auramancer.java:114) : si `auraLocked` → Speed.

`onFirstDeathEvent` (Auramancer.java:160) :
- Si l'Auramancer est le tueur : l'aura de la victime change la sienne. Victime DARK → DARK→NEUTRAL ou NEUTRAL→LIGHT (purification). Victime LIGHT/NEUTRAL → devient DARK. `AuramancerChangeAuraEvent`.
- Sinon, si aura==NEUTRAL et tueur à moins de `AURAMANCER_DISTANCE` : « sent » l'aura de la victime **et** celle du tueur (calculée avant le kill si 1er kill), `AuramancerEvent` ×2, message au joueur.

`onPlayerDamage` (EntityDamageByEntityEvent, Auramancer.java:217) :
- Si aura==DARK et l'Auramancer est l'attaquant et la cible a aura LIGHT → `+strengthRate %` de dégâts.
- Si aura==LIGHT et un joueur LIGHT (parmi ceux à moins de `AURAMANCER_DISTANCE`) est l'attaquant → `+strengthRate/2 %` de dégâts (buffe les alliés LIGHT proches).

`onFinalDeath` (Auramancer.java:257) : si non `auraLocked`, aura==DARK, et aucun LIGHT vivant → `auraLocked=true` + Speed (verrouillage définitif en DARK).

Pas de `isWin` custom (le camp dérive de l'aura via `isNeutral()`).

### Avocat

- **Clé :** `avocat`
- **Classe :** `Avocat` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Villageois

`defaultAura=LIGHT`. Constantes `COOLDOWN=600s` (10 min) et `DEFENSE_DURATION=600s` (10 min).

Commande `/ww command <joueur>` (cible ALIVE ou JUDGEMENT) : refusée si cooldown non écoulé (`canDefend()`) ou si la cible est la même que la dernière défendue (`isLastDefended`). Déclenche `AvocatDefendEvent` puis `defend(target)` (CommandAvocat.java:30-49). `defend()` (Avocat.java:58-96) : clear affected, mémorise `defendedPlayer` + `lastDefendedPlayer`, enregistre `lastUseTimer`, remet à zéro `preventedDecrease`/`anyHonorChange`, incrémente `defenseGeneration`, ajoute la cible. Programme la fin de défense après `DEFENSE_DURATION*20` ticks : si aucune perte d'honneur n'a eu lieu (`!anyHonorChange`), transfère à l'avocat le delta d'honneur `defended.getHonor() - avocat.getHonor()` (modifyHonor) et annonce l'honneur final de la cible ; sinon annonce le total `preventedDecrease`.

`@EventHandler onHonorChange` (priorité LOWEST) : si la cible défendue subit une baisse d'honneur (`newHonor < oldHonor`), annule l'event et accumule la différence dans `preventedDecrease` (Avocat.java:98-115). Actif seulement si abilityEnabled, avocat vivant, `defendedPlayer` défini.

Aucun effet de potion. `recoverPower()` vide.

### Bibliothécaire

- **Clé :** `librarian`
- **Classe :** `Librarian` (extends `RoleImpl`) · **Marqueurs :** ILimitedUse, IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

`use=0`, max 3 (hardcodé dans la description `3 - use` et `requiredLimitedUse=3` dans la commande). `storage` : liste de messages accumulés.

Commande `/ww librarian <cible>` (CommandLibrarian.java) : `requiredLimitedUse=3`, cible vivante, `excludeSelf`, `rejectIfAlreadyAffected`. Incrémente `use`, tire `LibrarianRequestEvent`, ajoute la cible aux affected, envoie à la cible un texte cliquable suggérant `/ww <request_command>`.

Commande `/ww <request_command> <message>` (CommandSendToLibrarian.java, PlayerCommand, `autoCompletion=false`) : **n'importe quel joueur** peut l'exécuter. Trouve le(s) Bibliothécaire(s) qui ont ce joueur en affected. Retire le joueur des affected, tire `LibrarianGiveBackEvent`, ajoute le message au `storage` du Bibliothécaire, notifie le joueur (contribution) et le Bibliothécaire (message reçu).

`onFinalDeath` (Librarian.java:92-114) : si le Bibliothécaire meurt, que `storage` n'est pas vide, et `isAbilityEnabled()` : **broadcast global** de la mort, puis broadcast de chaque message stocké comme une "page" numérotée (`&page&`, `&message&`). Vide le `storage`. Tire `LibrarianDeathEvent`.

Pas de `recoverPotionEffect`. Pas de timer/IntValue. Pas de condition de victoire perso. `attribute=MINOR_INFORMATION`.

### Bienfaiteur

- **Clé :** `benefactor`
- **Classe :** `Benefactor` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Villageois

`defaultAura=LIGHT`. `COOLDOWN=300s` (5 min).

Commande `/ww command <joueur>` (excludeSelf, `rejectIfAlreadyAffected`) : refusée si cooldown (`canGive`) ou si déjà 3 affectés. Déclenche `BenefactorGiveHeartEvent` (TellableStoryEvent). Ajoute la cible, `onGiven()` (enregistre `lastUseTimer`). Programme après `DELIVERY_DELAY=3*60*20` ticks (3 min) : si la cible est vivante, `addPlayerMaxHealth(2)` (+1 cœur max) et notifie la cible (CommandBenefactor.java:53-62).

`second()` (passif, Benefactor.java:45-54) : si abilityEnabled ET `affectedPlayers.size() >= 3` ET `game.getTimer() % 60 == 0` → `addPlayerHealth(2)` (soigne l'avocat… le bénéfacteur lui-même de 1 cœur chaque minute). Donc une fois 3 bénéficiaires livrés, régénération passive de 2 HP/min.

Aucun `@EventHandler`, aucun effet de potion. `recoverPower()` vide.

### Bohémienne

- **Clé :** `bohemian`
- **Classe :** `Bohemian` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Information

`defaultAura=LIGHT`. `COOLDOWN=600s` (10 min).

`recoverPower()` (init unique, flag `initialized`) : fixe l'honneur du bohémien à 1 (`modifyHonor(1 - getHonor())`, Bohemian.java:43-48).

`@EventHandler onDay` : si vivant, annonce le niveau d'honneur courant (Bohemian.java:51-58).

`@EventHandler onSpyInfo` (SpyInfoEvent) : incrémente un compteur par cible (`targetCounts`) et enregistre la dernière nature d'event espionné (`lastNature`, Bohemian.java:60-65).

Commande `/ww command <joueur> <montant>` (montant 1-3, excludeSelf, cible ALIVE/JUDGEMENT) : refusée si cooldown (`canSacrifice`) ou si honneur < montant. Déclenche `BohemianSacrificeEvent(amount)`. Sacrifie `amount` points d'honneur (`modifyHonor(-amount)`). Effet selon le montant (CommandBohemian.java:55-79) :
- 1 : révèle l'honneur actuel de la cible.
- 2 : révèle le nombre de commandes espionnes effectuées par la cible (`getTargetCount`) + la dernière nature.
- 3 : révèle le camp affiché de la cible (`getDisplayCamp`).

Aucun effet de potion. Pas de condition de victoire perso.

### Bouc émissaire

- **Clé :** `scape_goat`
- **Classe :** `ScapeGoat` (extends `RoleImpl`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Information mineure

`defaultAura=DARK`, `attribute=MINOR_INFORMATION`. `IAffectedPlayers` à un seul joueur (`playerWW`).
`onMultiVoteResultEvent` (MultiVoteResultEvent, ScapeGoat.java:58) : lors d'une égalité de vote, si vivant : `setPower(true)`, envoie un message et un texte cliquable `/ww <command> <uuid>` pour chaque joueur à égalité. Planifie une tâche différée de `TimerBase.VOTE_WAITING` ticks : si un joueur a été sélectionné via la commande, déclenche `VoteResultEvent(selected)` ; sinon si la liste n'est pas vide, déclenche `VoteResultEvent(self)` (le ScapeGoat se sacrifie). Puis `setPower(false)`, `clearAffectedPlayer`.
`onVoteResultEvent` (VoteResultEvent ignoreCancelled, ScapeGoat.java:95) : si vivant et `!hasPower()` : si aucun résultat (`getPlayerWW()==null`), envoie un message + donne une **Pomme Dorée**. Sinon, choisit un joueur vivant aléatoire ayant un rôle **différent** de celui du voté, révèle son rôle au ScapeGoat, et donne une **Pomme Dorée**.
Commande `CommandScapeGoat` (CommandScapeGoat.java:26, `autoCompletion=false`, `byUuid`) : déclenche `ScapeGoatEvent`, `clearAffectedPlayer` + `addAffectedPlayer(voteWW)`, message de confirmation.
Le ScapeGoat brise donc les égalités de vote en choisissant qui est éliminé, et reçoit une Pomme Dorée + info de rôle lors d'un vote normal.

### Braconnier

- **Clé :** `poacher`
- **Classe :** `Poacher` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle de traque/passif. `defaultAura=NEUTRAL`, `attribute=VILLAGER`. Timer `TimerBase.POACHER_PROGRESS` (défaut 10s, meetUp 10). IntValue `IntValueBase.POACHER_DISTANCE` (défaut 10, meetUp 10, pas 1). Constantes : `DETECTION_DISTANCE=50`, `CARCASS_DURATION=900s` (15 min).
`onAnnouncementDeath` (Poacher.java:71, HIGHEST) : si la cible de l'annonce est le Poacher et que le mort est un loup (catégorie WEREWOLF, ou `WOLF_DOG`, ou `WHITE_WEREWOLF`), enregistre une `Carcass` (position de mort, durée 900s) dans `carcasses`.
`second()` (Poacher.java:147) : décrémente les carcasses et supprime les expirées. Si vivant et carcasses présentes : pour chaque carcasse à ≤50 blocs, joue l'effet `MOBSPAWNER_FLAMES` au point de mort. Trouve la carcasse la plus proche à < `POACHER_DISTANCE` ; incrémente `progress`. Quand `progress >= POACHER_PROGRESS` (10s), récupère la fourrure : déclenche `PoacherRecoverFurEvent`, `furNumbers++`, rappelle `recoverPotionEffect`.
`recoverPotionEffect` (Poacher.java:124) : si `furNumbers>0`, ajoute un `AuraModifier` DARK permanent (`addAuraModifier`).
`getDisplayCamp`/`getDisplayRole` (Poacher.java:131/139) : si `furNumbers>0` ou infecté, s'affiche comme camp WEREWOLF / rôle WEREWOLF (auprès des rôles d'info), sinon villageois.
`onActionBarEvent` (Poacher.java:228) : affiche une flèche directionnelle + distance par paliers de 10 vers la carcasse la plus proche (≤50), ou un indicateur de récupération si à portée (`POACHER_DISTANCE`).
`onPatchPotion` (Poacher.java:287, EntityDamageByEntityEvent HIGHEST) : si le Poacher est victime d'un joueur et `furNumbers>0`, réduit les dégâts de `resistanceLevels*0.20` avec `resistanceLevels = 0.5 + 0.1*(furNumbers-1)` (soit 10% à la 1ère fourrure, +2% par fourrure supplémentaire).

### Chaman

- **Clé :** `shaman`
- **Classe :** `Shaman` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

`defaultAura=NEUTRAL`, `attribute=MINOR_INFORMATION`.
`onFirstDeathEvent` (FinalDeathEvent, Shaman.java:53) : si vivant et ability enabled : si le mort est `HERMIT` ou `FEARFUL_WEREWOLF`, envoie `hidden_death` et retour. Si pas de `lastKiller`, retour. Sinon envoie un texte cliquable `/ww <command> <uuid du mort>`.
`onAnnounceDeath` (HIGHEST ignoreCancelled, Shaman.java:84) : à sa propre mort, révèle son vrai rôle dans l'annonce.
Commande `CommandShaman` (CommandShaman.java:32, `autoCompletion=false`, `byUuid`, `rejectIfAlreadyAffected`) : nécessite `health >= 2` et `game.getTimer() - 14 - target.deathTime <= 30` (mort récente, ≤30s après l'annonce +14). Récupère le `lastKiller` de la cible, `addAffectedPlayer`, déclenche `ShamanEvent`. **`removePlayerMaxHealth(2)`** (coûte 1 cœur de vie max), ajoute un `AuraModifier` DARK (1, false). Construit une liste de 4 noms mélangés : le tueur + 3 joueurs vivants aléatoires (non-tueur). Envoie la liste au Shaman qui doit deviner le tueur parmi eux.

### Chasseur

- **Clé :** `hunter`
- **Classe :** `Hunter` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`powerModifier=1`. Configuration `HUNTER_CAN_SHOOT` (ConfigurationBasic, `meetUpValue=true`).

`onFinalDeathEvent` (Hunter.java:62-84) :
- Si le Chasseur meurt et que `HUNTER_CAN_SHOOT` est actif : `setPower(true)`, envoie message, programme `setPower(false)` après `20*30` ticks (30s).
- Si un loup-garou meurt et que le Chasseur en est le tueur (`getLastKiller().get().equals(getPlayerWW())`) : `damageBonus += 0.1`, envoie message "loup tué".

`onPlayerdamage` (EntityDamageByEntityEvent, Hunter.java:86-108) : si le Chasseur (vivant, `isAbilityEnabled()`) frappe un joueur qui est un loup-garou, et qu'il n'a **pas** l'effet Strength : multiplie les dégâts par `(1 + strengthRate/100 * (0.5 + damageBonus))`. Bonus de base **0.5**, +0.1 par loup tué.

`onWerewolfKillEvent` (Hunter.java:110-115) : si le Chasseur est la victime d'un `WereWolfKillEvent`, **annule** l'événement (immunisé aux attaques de loups la nuit).

Commande `/ww hunter <cible>` (CommandHunter.java) : `requiredPower=true`, `statesPlayer=DEATH` (utilisable mort). Interdit de cibler son propre tueur (`getLastKiller()`). Tire `HunterShotEvent`, inflige `-10` PV à la cible (`removePlayerHealth(10)`), broadcast global.

Pas de `recoverPotionEffect` redéfini. `defaultAura=NEUTRAL`. Pas de condition de victoire perso.

### Chien Loup

- **Clé :** `wolf_dog`
- **Classe :** `WolfDog` (extends `RoleImpl`) · **Marqueurs :** ITransformed, IPower
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Hybride

Rôle hybride (attribute=HYBRID), `defaultAura=DARK`, `auraDescriptionSpecialUseCase` défini. `transformed` démarre à `false`, `power` à `true`. Aucune config/IntValue annoté (lit `TimerBase.WEREWOLF_LIST`).

`isWereWolf()` surchargé (WolfDog.java:77) : `super.isWereWolf() || transformed`. `recoverPower()` (WolfDog.java:66) : envoie un message indiquant le timer avant `WEREWOLF_LIST`.

Transformation — `CommandWolfDog` (`/ww`, `requiredPower=true`, 0 arg) : consomme le pouvoir, déclenche `WolfDogChooseWereWolfForm`, `setTransformed(true)` (qui ajoute un `AuraModifier` LIGHT priorité 1 non-additif, WolfDog.java:189), notifie, déclenche `UpdateNameTagEvent`, appelle `game.checkVictory()`.

Échéance — `onWereWolfList(WereWolfListEvent)` (WolfDog.java:82) : au timer `WEREWOLF_LIST`, si le pouvoir était encore disponible, envoie un message « temps écoulé » et `power=false` (le WolfDog ne peut plus transformer et reste dans son état initial non-transformé).

Effets de potion nocturnes — `onNightForWereWolf(NightEvent, priority=HIGH)` (WolfDog.java:102) : si non nativement loup et transformé → retire STRENGTH (clé WEREWOLF). Si non transformé → ajoute STRENGTH (clé WEREWOLF). `onDayForWereWolf(DayEvent, priority=HIGH)` (WolfDog.java:114) : si non transformé → retire STRENGTH. Donc avant transformation, le WolfDog reçoit Force la nuit ; après transformation, la Force est retirée.

Intégration chat loup :
- `onChatSpeak(WereWolfCanSpeakInChatEvent)` (WolfDog.java:123) : peut parler dans le chat loup uniquement si `!transformed || super.isWereWolf()`.
- `onWerewolfWHat(WereWolfChatEvent, priority=LOW)` (WolfDog.java:92) : si le WolfDog est la cible d'un message du chat loup, l'annule si `transformed && !super.isWereWolf()` (transformé non natif → ne reçoit pas les messages du chat loup).
- `onAppearInWereWolfList(AppearInWereWolfListEvent)` (WolfDog.java:132) : apparaît dans la liste loup si `!transformed || super.isWereWolf()`.
- `onRequestSeeWereWolfListEvent(RequestSeeWereWolfListEvent)` (WolfDog.java:142) : peut voir la liste loup si `transformed || super.isWereWolf()`.

Affichage inversé :
- `getDisplayCamp()` (WolfDog.java:152) : transformé → VILLAGER ; non transformé → WEREWOLF.
- `getDisplayRole()` (WolfDog.java:161) : transformé → clé du premier rôle vivant de camp VILLAGER ; non transformé → clé du premier rôle vivant de camp WEREWOLF.

Synthèse des deux états :
- **Non transformé** (état initial) : camp réel VILLAGER (`isWereWolf()=false` sauf infection), affiché comme WEREWOLF, apparaît dans la liste loup (les loups le voient), peut parler dans le chat loup, ne peut PAS voir la liste loup, reçoit Force la nuit, pas d'aura spéciale.
- **Transformé** (via commande avant `WEREWOLF_LIST`) : camp réel WEREWOLF, affiché comme VILLAGER, n'apparaît plus dans la liste loup, ne peut plus parler/recevoir le chat loup, PEUT voir la liste loup, perd la Force, gagne l'aura LIGHT.

Condition de victoire dérivée du camp : VILLAGER si non transformé, WEREWOLF si transformé.

### Citoyen

- **Clé :** `citizen`
- **Classe :** `Citizen` (extends `RoleImpl`) · **Marqueurs :** ILimitedUse, IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

`attribute=MINOR_INFORMATION`. `IntValue CITIZEN_SEE_VOTE_NUMBER` défaut 3 (meetUp 3, pas 1). Constantes `REDIRECT_WINDOW=30`, `OFFICE_PROXIMITY=15`.

`@EventHandler onDay` (Citizen.java:116-156) : si config `HIDE_COMPOSITION` active, envoie 3 rôles mélangés (1 présent + 2 absents de la compo) au citoyen.

Votes classiques (non-urne) :
- `onVoteEnd` (Citizen.java:158-179) : programme une tâche à `VOTE_WAITING - 30`s. Si toujours en WAITING et vivant : si `use < CITIZEN_SEE_VOTE_NUMBER`, propose un clic pour voir les votes ; si `hasPower`, active `canRedirect` et annonce la fenêtre de redirection.
- `onVoteResult` (Citizen.java:181-195) : ferme la fenêtre ; si `redirectTarget` défini, remplace la cible du vote (`event.setPlayerWW`).
- CommandCitizenSeeVote `/ww command_1` : incrémente `use`, déclenche `CitizenSeeVoteEvent`, liste chaque votant et sa cible (CommandCitizenSeeVote.java:35-48).
- CommandCitizenDetourner `/ww detourner_command <joueur>` (`requiredPower`) : définit `redirectTarget` et consomme le power, uniquement pendant la fenêtre (CommandCitizenDetourner.java:29-37).

Votes à urne :
- `onUrnVoteCast` + `onUrnVote1/2/3Start` : mémorise le bureau/session voté. À la fin de `URN_VOTE_DURATION`, envoie au citoyen le nombre de votes non blancs et la liste des joueurs visés (Citizen.java:209-256).
- CommandCitizenAnnuler `/ww annuler_command` : annule la session d'urne courante si le citoyen est à moins de `OFFICE_PROXIMITY=15` blocs d'un bureau et qu'il n'a pas déjà annulé cette session (CommandCitizenAnnuler.java + Citizen.java:269-291).

`@EventHandler onRumor` (RumorsWriteEvent) : transmet au citoyen l'auteur et le message de la rumeur (Citizen.java:258-267). `CommandCitizenChangeVote` est dépréciée/vide.

Aucun effet de potion. `recoverPower()` vide.

### Comédien

- **Clé :** `comedian`
- **Classe :** `Comedian` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IPower (hérité via RoleWithLimitedSelectionDuration)
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`defaultAura=NEUTRAL`. Constructeur `setPower(false)`. 3 masques possibles (`ComedianMask` : force, vitesse, résistance), max 3 masques au total.

`@EventHandler onDay` (Comedian.java:58-77) : retire l'effet de potion du dernier masque porté (`getLastMask()` → `PotionModifier.remove`). Si `masks.size() < 3`, `setPower(true)` et envoie un message invitant à porter un masque (avec timer `POWER_DURATION`).

Commande `/ww command <int>` (`requiredPower`, int 1-3) : refus si masque déjà porté ou index hors 0-2. `setPower(false)`, `addMask`, déclenche `UseMaskEvent` (TellableStoryEvent), et ajoute l'effet de potion du masque de façon permanente (`PotionModifier.add(type, key)` sans durée, CommandComedian.java:43-57).

Ainsi, à chaque lever du jour l'effet du masque de la veille est retiré, et un nouveau masque peut être porté (jusqu'à 3 cumulés dans l'historique). Un seul effet actif à la fois (le dernier porté). La description liste tous les masques portés.

### Conteuse

- **Clé :** `story_teller`
- **Classe :** `StoryTeller` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information

Rôle d'information (attribute=INFORMATION). Config `IntValueBase.STORY_TELLER_DAY` (défaut 5, meetUp 3, step 1). Aucun pouvoir consommable, aucun `IPower`.

`onActionableStory(ActionableStoryEvent)` (StoryTeller.java:42) : ajoute systématiquement le joueur concerné par l'événement dans un `Set<IPlayerWW> players`. Ce set accumule tous les joueurs ayant réalisé une action « actionable story » durant le cycle.

`onDay(DayEvent)` (StoryTeller.java:47) : si `event.getNumber() >= STORY_TELLER_DAY` ET le StoryTeller est vivant ET `isAbilityEnabled()` ET le set n'est pas vide, déclenche `StoryTellerEvent(playerWW, players)` puis envoie un message listant tous les noms des joueurs ayant réalisé une action actionable. Le set est ensuite vidé à chaque `DayEvent` (même avant STORY_TELLER_DAY, StoryTeller.java:74).

Aucun effet de potion, aucune commande dédiée, aucune condition de victoire perso. Le StoryTeller apprend donc chaque jour (à partir de STORY_TELLER_DAY) la liste des joueurs qui ont été actifs durant le cycle précédent.

### Corbeau

- **Clé :** `raven`
- **Classe :** `Raven` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Villageois

`defaultAura=DARK`, `attribute=VILLAGER`. `setPower(false)` à l'init. Mémorise `last` (maudit) et `vote`.
`onDay` (LOWEST, Raven.java:82) : retire JUMP_BOOST et l'aura modifier du `last`, lui envoie `no_longer_curse`, `last=null`. Si vivant : `setPower(true)`, message avec `POWER_DURATION`.
Commande `CommandRaven` (CommandRaven.java:33, `rejectIfAlreadyAffected`) : `setPower(false)`, déclenche `CurseEvent`, `clearAffectedPlayer` + `addAffectedPlayer` (définit `last`), donne à la cible **JUMP_BOOST permanent** + `AuraModifier` DARK, envoie un message à la cible.
`getPlayersMet()` (Raven.java:117) : retourne tous les joueurs sauf lui-même (rencontre tout le monde).
`onVoteEvent` (VoteEvent, Raven.java:124) : quand le Corbeau vote, ajoute 1 vote au score de la cible et mémorise `vote`.
`onVoteEvent` (VoteResultEvent, Raven.java:135) : si la cible correspond au voté, envoie une flèche de particule vers lui.
`onVoteEndEvent` (VoteResultEvent, Raven.java:153) : si le joueur voté par le Corbeau est effectivement éliminé, donne au Corbeau STRENGTH (60s, niveau -1) et SPEED (60s, niveau -1).
`onPlayerDamage` (EntityDamageEvent, Raven.java:173) : annule les dégâts de chute (FALL) subis par le Corbeau.

### Croupier

- **Clé :** `croupier`
- **Classe :** `Croupier` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers, IPower (hérité)
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information

`defaultAura=NEUTRAL`, `attribute=INFORMATION`. Config `CROUPIER_EVERY_OTHER_DAY`. `dayNumber` init -8.

`@EventHandler onDay` (Croupier.java:74-90) : si config "un jour sur deux" et jour consécutif, skip. Sinon `setPower(true)` et annonce avec timer `POWER_DURATION`.

Commande `/ww command <joueur>` (excludeSelf, rejectIfAlreadyAffected, `requiredPower`) : nécessite ≥4 autres joueurs vivants (hors croupier et cible). Ajoute la cible, `setPower(false)`. Tire au sort un `playerRevealed` parmi les autres vivants, déclenche `CroupierChooseEventEvent`. Sélectionne ensuite un `role1` (clé ET camp différents de playerRevealed) puis un `role2` (clé différente de role1 et de playerRevealed). Construit une liste mélangée de 3 clés : `role1.key`, `role2.key`, `playerRevealed.getDisplayRole()`. Déclenche `CroupierEvent` (SpyEvent). Envoie à la cible une "carte" avec le nom de playerRevealed et les 3 rôles possibles (1 vrai + 2 leurres de camps différents) ; envoie au croupier un message de confirmation (CommandCroupier.java:43-116).

### Cupidon

- **Clé :** `cupid`
- **Classe :** `Cupid` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Hybride

`attribute=HYBRID`, `powerModifier=1`. Configs `RANDOM_CUPID` et `AMNESIAC_LOVERS`.

Commande `/ww command <p1> <p2>` ( ICommandRole legacy, `requiredPower`, 2 args) : si config `RANDOM_CUPID` active, refuse (amoureux tirés au sort). Les deux joueurs doivent être distincts, en ligne, vivants, et différents du cupidon. Ajoute les deux aux affectedPlayers, `setPower(false)`, déclenche `CupidLoversEvent` (CommandCupid.java:35-80). La description mentionne le timer `LOVER_DURATION` pour la fenêtre de désignation.

`@EventHandler onAroundLoverEvent` (Cupid.java:98-116) : si le cupidon fait partie des joueurs "around", ajoute ses amoureux à l'event ; si l'un des amoureux est déjà dans la liste, ajoute le cupidon. Ainsi le cupidon est considéré comme entourant ses amoureux.

Aucun effet de potion, aucun event dédié propre. `recoverPower()` vide.

### Détective

- **Clé :** `detective`
- **Classe :** `Detective` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers, IPower (hérité)
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Information

`defaultAura=LIGHT`, `attribute=INFORMATION`. Config `DETECTIVE_EVERY_OTHER_DAY`. Constantes `FIRST_EPISODE=3`, `GUEST_EPISODE=5`. Constructeur `setPower(false)`.

`recoverPower()` (Detective.java:139-142) : tire au sort `sameIsBlue` (booléen aléatoire) déterminant si "même camp" = bleu ou jaune.

`@EventHandler onDay` (Detective.java:72-99) : actif seulement à partir du jour 3. Skip un jour sur deux si config active. Remet `canGuess=false`. Si `!powerLost`, `setPower(true)` et annonce avec timer `POWER_DURATION`.

CommandDetective `/ww command <p1> <p2>` (2 joueurs, excludeSelf, rejectIfAlreadyAffected, `requiredPower`) : les deux cibles doivent avoir été "rencontrées" (`getPlayersMet`). Compare les `getDisplayCamp()`. `setPower(false)`, déclenche `InvestigateEvent(sameCamp)` (SpyEvent). Ajoute les deux aux affectedPlayers. Retourne un code couleur : sameCamp → couleur "same" (bleu ou jaune selon `sameIsBlue`), sinon l'autre couleur. `onInvestigated()` active `canGuess` si `dayNumber >= 5` ; si oui, propose la commande de devinette (suggest) valable 60s (CommandDetective.java:53-96).

CommandDetectiveGuess `/ww command_guess <bleu|jaune>` : si `canGuess`, clear. Si la couleur devinée correspond au code "same" → message correct. Sinon → `setPowerLost(true)` (perte définitive du pouvoir, CommandDetectiveGuess.java:32-41).

Le détective apprend donc si deux joueurs rencontrés partagent le même camp via un code couleur, et à partir du jour 5 peut deviner quelle couleur signifie "même camp" : bonne réponse = confirmation, mauvaise = rôle désactivé à vie.

### Druide

- **Clé :** `druid`
- **Classe :** `Druid` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Information mineure

`defaultAura=DARK`, `attribute=MINOR_INFORMATION`. `IntValue DRUID_DISTANCE` défaut 50 (meetUp 50, pas 5). Pas de commande dédiée.

`@EventHandler onDay` (Druid.java:61-69) : si vivant, `setPower(true)` et envoie un message journalier.

`@EventHandler onAppleEat` (PlayerItemConsumeEvent, priorité LOWEST, Druid.java:71-121) : si le druide (lui-même, vivant) mange une `GOLDEN_APPLE` la NUIT (`!game.isDay(Day.DAY)`) tout en SNEAKANT (s'accroupissant) et qu'il a le power → `setPower(false)`. Compte les joueurs vivants à moins de `DRUID_DISTANCE` blocs (hors lui-même) dont l'aura du rôle == DARK. Déclenche `DruidUsePowerEvent` avec le compte et la liste. Annonce au druide le nombre de joueurs à aura sombre dans le rayon et la distance.

Aucun effet de potion permanent. `recoverPower()` vide. Le pouvoir est donc réactivé chaque jour et consommé en mangeant une pomme dorée accroupi la nuit.

### Enfant Sauvage

- **Clé :** `wild_child`
- **Classe :** `WildChild` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, ITransformed, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Hybride

Rôle hybride (attribute=HYBRID), `defaultAura=NEUTRAL`. Timer `TimerBase.MODEL_DURATION` (défaut 240s, meetUp 240s, `decrementAfterRole=true`, `onZero=AutoModelEvent`). `power` démarre à `true`, `transformed` démarre à `false`.

`isWereWolf()` surchargé (WildChild.java:147) : retourne `transformed || super.isWereWolf()` (infecté OU transformé).

Choix du modèle — `CommandWildChild` (`/ww`, `requiredPower=true`, 1 arg joueur vivant, `excludeSelf=true`) : ajoute la cible aux affected players, consomme le pouvoir, déclenche `ModelEvent`, notifie le Wild Child.

Auto-modèle — `onAutoModel(AutoModelEvent)` (WildChild.java:99) : déclenché quand le timer `MODEL_DURATION` atteint zéro. Si le pouvoir est encore disponible, sélectionne automatiquement un modèle via `Utils.autoSelect(game, playerWW)`, l'ajoute, consomme le pouvoir, déclenche `ModelEvent`, révèle le modèle au Wild Child (si vivant).

Transformation — `onFinalDeath(FinalDeathEvent)` (WildChild.java:153) : quand le modèle (affected player) meurt, si le Wild Child n'est pas mort et pas déjà transformé, déclenche `WildChildTransformationEvent`. Si non annulé, `setTransformed(true)` et, si `!super.isWereWolf()` (pas déjà infecté), déclenche `NewWereWolfEvent` (le Wild Child devient loup).

Hurlements — `onHowling(WereWolfHowlingEvent, ignoreCancelled=true)` (WildChild.java:183) : si transformé et que le Wild Child est la cible d'un hurlement et qu'un modèle existe :
- Si le modèle a `getHonor() > 0` : révèle la distance (en blocs) entre le Wild Child et le lieu du hurlement.
- Sinon : compte les loups-garous vivants (hors hurleur) à moins de 50 blocs du hurleur et révèle ce nombre.

`onEndPlayerMessage` (WildChild.java:214) : ajoute le nom du modèle et, si transformé, un marqueur de transformation au message de fin.

Aucun effet de potion direct. Camp dérivé : VILLAGER tant que non transformé/non infecté, WEREWOLF une fois transformé.

### Ermite

- **Clé :** `hermit`
- **Classe :** `Hermit` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

IntValue `HERMIT_DISTANCE` (default 20, meetUp 20, step 4). `defaultAura=NEUTRAL`.

`second()` (Hermit.java:148-210) : si vivant et `isAbilityEnabled()`, compte les joueurs vivants à moins de `HERMIT_DISTANCE` :
- **0 joueurs proches** : **Speed**.
- **1-3 joueurs proches** (branche else de `number >= 4`) : retire Weakness. Si jour : **Strength** (retire Resistance). Si nuit : **Resistance** (retire Strength).
- **≥ 4 joueurs proches** : **Weakness** (retire Strength et Resistance).

`onFirstDeath` (Hermit.java:79-92, LOWEST, ignoreCancelled) : si l'Ermite subit une `FirstDeathEvent`, **annule** la mort, puis programme `game.death()` après `14*20` ticks (14s) si toujours en `JUDGEMENT`. (Retarde la mort inévitable.)

`onVote` (Hermit.java:95-102) : annule les votes contre l'Ermite (immunité au vote).

`onLoverDeathMessage` (AnnouncementLoverDeathEvent, Hermit.java:104-109) : annule l'annonce de mort liée aux amoureux si cela concerne l'Ermite ou ses amoureux.

`onUpdate` (UpdatePlayerNameTagEvent, Hermit.java:111-123, HIGH) : cache l'Ermite du tablist à sa mort.

`onDeath` (AnnouncementDeathEvent, Hermit.java:125-132) : annule l'annonce de mort de l'Ermite.

`onUpdateCompo` (UpdateCompositionEvent, Hermit.java:134-146) : si la raison est `DEATH` et la clé est `HERMIT`, annule (l'Ermite n'apparaît pas dans la composition à sa mort).

`disableAbilitiesRole` (Hermit.java:62-77) : retire Speed, Strength, Resistance, Weakness. Pas de commande dédiée. Pas de condition de victoire perso.

### Espion

- **Clé :** `spy`
- **Classe :** `Spy` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Rôle d'information mineur (attribute=MINOR_INFORMATION). Config `IntValueBase.SPY_DAY` (défaut 5, meetUp 2, step 1). Le pouvoir (`power`) démarre à `false`.

Activation du pouvoir : sur `DayEvent` (Spy.java:95), lorsque `event.getNumber() >= SPY_DAY`, `power` passe à `true`. Un message indique au Spy qu'il peut utiliser son pouvoir (Spy.java:121).

Commande dédiée `CommandSpy` (`/ww`, `requiredPower=true`, 1 arg joueur vivant, `rejectIfAlreadyAffected=true`) : sélectionne une cible à espionner. Refuse si la cible est identique à `lastSpied` (pas deux jours de suite sur le même joueur, CommandSpy.java:37). Déclenche `SpyChoseEvent`, consomme le pouvoir (`setPower(false)`), efface l'affected player précédent et ajoute le nouveau.

Comptage via `onActionableStory(ActionableStoryEvent)` (Spy.java:86) : à chaque événement « actionable story » (déclenché par le système de statistiques ou par des rôles comme le Rival) concernant le joueur espionné (`playerWW`), un compteur `count` est incrémenté.

Sur `DayEvent` (Spy.java:93) : si le Spy est vivant et `isAbilityEnabled()` et qu'une cible est mémorisée, déclenche `SpyResultEvent(playerWW, cible, count)` puis envoie un message au Spy avec le nom de la cible et le nombre d'actions « actionable story » qu'elle a réalisées pendant le cycle écoulé. Ensuite `count` est remis à 0, `lastSpied` prend la valeur de la cible courante, et `playerWW` (cible) est remis à null — le Spy devra re-sélectionner une cible le jour suivant.

Aucun effet de potion. Aucune condition de victoire perso (camp VILLAGER par défaut).

### Fossoyeur

- **Clé :** `gravedigger`
- **Classe :** `Gravedigger` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

IntValue `GRAVEDIGGER_DISTANCE` (default 70, meetUp 30, step 5). `defaultAura=NEUTRAL`, `attribute=MINOR_INFORMATION`.

`onFinalDeathEvent` (Gravedigger.java:93-161) : à chaque mort d'un autre joueur, calcule la position de mort, trouve les joueurs vivants à moins de `GRAVEDIGGER_DISTANCE`, récupère le `displayRole` du mort. Tire `CreateGravediggerClueEvent` et stocke un `GravediggerClue` (position, joueurs proches, noms mélangés, clé de rôle). Si `isAbilityEnabled()` : calcule un angle — si le camp du mort n'est pas VILLAGER, angle **aléatoire** ; sinon angle réel vers la mort (`Utils.getAngle`). Tire `GravediggerDirectionEvent`, envoie une flèche de particules (`Utils.sendParticleArrow`, 20 particules) dans cette direction au Gravedigger.

`second()` (Gravedigger.java:164-250) : toutes les 10 secondes, pour chaque clue à moins de 20 blocs du Gravedigger, incrémente un compteur :
- **count 6** : révèle le nom de la victime + nombre de joueurs proches (clue 1), `return`.
- **count 12** : révèle le rôle de la victime + 1/3 des noms des joueurs proches (clue 2), `return`.
- **count 18** : révèle 2/3 des noms (clue 3). **Attention : pas de `break`/`return`** — chute vers case 24.
- **count 24** : révèle tous les noms (clue 4), supprime le clue. Du fait de la chute, count 18 déclenche simultanément les clues 3 et 4 et supprime le clue ; count 24 n'est jamais atteint indépendamment.

`buildNamesString` (Gravedigger.java:252-263) : affiche les `thirds/3` premiers noms en clair, le reste en obfusqué (`ChatColor.MAGIC`). Pas de commande dédiée. Pas de condition de victoire perso.

### Frère Siamois

- **Clé :** `siamese_twin`
- **Classe :** `SiameseTwin` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`defaultAura=NEUTRAL`, `attribute=VILLAGER`, `requireDouble=true` (nécessite 2 joueurs). Pas de commande.
`recoverPower` (SiameseTwin.java:81) : `addPlayerMaxHealth(4)` (+2 cœurs de vie max permanents).
`getDescription` / `getBrother` (SiameseTwin.java:65) : liste les autres `SIAMESE_TWIN` vivants (son jumeau).
`onWerewolfList` (WereWolfListEvent, SiameseTwin.java:58) : à la liste des loups, envoie le nom du jumeau (révélée à ce moment, ou via timer `WEREWOLF_LIST` si pas encore écoulé).
`second()` (SiameseTwin.java:86) : calcule la moyenne des ratios de vie (`health/maxHealth`) de tous les `SIAMESE_TWIN` vivants, puis fixe la vie de chaque jumeau à `moyenne * maxHealth` (vies liées). Joue un son `BURP` quand la vie est réduite. Si la moyenne ramène un jumeau au-dessus de 10, il n'est pas abaissé sous 10 (garde un seuil). Les deux jumeaux partagent donc une barre de vie commune.

### Garde

- **Clé :** `guard`
- **Classe :** `Guard` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers, IPower (via base)
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`RoleWithLimitedSelectionDuration` : `power=true` par défaut, remis à `false` au `SelectionEndEvent` (expiration du timer `POWER_DURATION`) avec message de fin de sélection.

Deux mécanismes distincts : (1) protection quotidienne via `power`, (2) résurrection unique via `powerFinal` (initialement `true`).

Activation par commande `/ww guard <joueur>` (CommandGuard.java) : `requiredPower=true`, cible vivante, `excludeSelf`, `rejectIfAlreadyAffected`. Vérifie `isPowerFinal()`. Met `power=false`, tire `GuardEvent`, ajoute la cible comme affected (et `last`), ajoute un `AuraModifier` **LIGHT** (priorité 40, temporaire) à la cible. Programme le retrait de l'aura après `10*60*20` ticks (10 minutes) ainsi que `clearLast()`.

`onDay` (Guard.java:111-131, LOWEST) : retire l'aura du dernier protégé (`removeAuraModifier`, `last=null`). Si vivant et `powerFinal` : `setPower(true)`, envoie message avec timer `POWER_DURATION`.

`onSecondDeathEvent` (Guard.java:55-94, LOWEST, ignoreCancelled) : si `powerFinal`, que le mourant est `last` (le protégé), qu'un tueur existe et que ce tueur `isWereWolf()` : tire `GuardResurrectionEvent`. Si non annulé : **ressuscite** le protégé (`game.resurrection(last)`), donne **Resistance** au Guard pendant `20*60*20` ticks (20 min, amplifier 0), ajoute un `AuraModifier` **DARK** (priorité 1) au Guard, annule la mort, et `powerFinal=false` (usage unique).

`disableAbilitiesRole` non redéfini (hérité). `defaultAura=NEUTRAL`. Pas de condition de victoire perso.

### Idiot du Village

- **Clé :** `village_idiot`
- **Classe :** `VillageIdiot` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle villageois (attribute=VILLAGER). `power` démarre à `true`. Aucune config/IntValue/timer. Aucune commande dédiée.

Survie — `onSecondDeathEvent` (priority=LOW, `ignoreCancelled=true`, VillageIdiot.java:96) : déclenché quand le Village Idiot meurt. Conditions : le mourant est le Village Idiot, `hasPower()` vrai, `isAbilityEnabled()`, un `getLastKiller()` est présent, et le tueur n'est PAS un loup-garou (`!killerWW.getRole().isWereWolf()`). Effet : consomme le pouvoir (`setPower(false)`), déclenche `VillageIdiotEvent`, ressuscite (`game.resurrection`), déclenche `UpdateNameTagEvent`, retire 4 points de vie max (`removePlayerMaxHealth(4)`, soit 2 cœurs), annule la mort, broadcaste l'annonce.

Immunité au vote — `onVote(VoteEvent)` (VillageIdiot.java:64) : si le pouvoir a été consommé (`!hasPower()`) et que le Village Idiot est vivant et est la cible du vote, annule le vote (`event.setCancelled(true)`) et envoie un message. Après sa première survie, le Village Idiot ne peut donc plus être voté.

Suffixe de nametag — `onUpdateNameTag(UpdatePlayerNameTagEvent, priority=HIGH)` (VillageIdiot.java:83) : si le pouvoir est consommé et vivant, ajoute un suffixe au nametag du Village Idiot.

Aucune condition de victoire perso.

### Illusionniste

- **Clé :** `illusionist`
- **Classe :** `Illusionist` (extends `RoleImpl`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`power=true` initialement, `wait=false`.

Activation par commande `/ww` sans args (CommandIllusionist.java) : `requiredPower=true`. Met `power=false`, tire `IllusionistActivatePowerEvent`, met `wait=true`, envoie message.

`onFinalDeathEvent` (Illusionist.java:60-150) : si `isAbilityEnabled()`, vivant, et `wait=true` : met `wait=false`. Sélectionne un **joueur vivant non-loup** aléatoire parmi les vivants (excluant l'Illusionniste). Tire `IllusionistAddPlayerOnWerewolfListEvent`. Si non annulé : ajoute le joueur comme affected, déclenche `UpdateNameTagEvent`, notifie tous les loups vivants ("nouveau loup") + joue `Sound.WOLF_HOWL`. Après `20*60` ticks (60s) : envoie au joueur ciblé un message de révélation, sélectionne **2 autres joueurs vivants** aléatoires, mélange les 3 noms (cible + 2 autres), tire `IllusionistGetNamesEvent`, envoie les 3 noms à l'Illusionniste.

`onWerewolfListRequest` (AppearInWereWolfListEvent, Illusionist.java:152-158) : si la cible de l'événement est le joueur affecté (`this.playerWW`), met `setAppear(true)` — le joueur apparaît dans la liste des loups-garous.

L'affected player est unique (un seul à la fois). `addAffectedPlayer` remplace `this.playerWW`. Pas de timer/IntValue. Pas de condition de victoire perso.

### Inquisiteur

- **Clé :** `inquisitor`
- **Classe :** `Inquisitor` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers, IPower (via base)
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

`RoleWithLimitedSelectionDuration` : `power=true` par défaut, remis à `false` au `SelectionEndEvent`.

`onDay` (Inquisitor.java:68-81) : si non mort et `isAbilityEnabled()` : `setPower(true)`, envoie message avec timer `POWER_DURATION`.

Commande `/ww inquisitor <cible>` (CommandInquisitor.java) : `requiredPower=true`, cible `ALIVE` ou `JUDGEMENT`. Ajoute la cible aux affected, `setPower(false)`, tire `InquisitorEvent`. Si non annulé :
- **Si la cible est un loup-garou** (`isWereWolf()`) : `disableAbilities()` de la cible, notifie la cible (désactivation) et l'Inquisiteur (succès).
- **Si la cible n'est pas un loup** : `disableAbilities()` de l'Inquisiteur lui-même (penalité), message d'échec.

`defaultAura=NEUTRAL`, `attribute=MINOR_INFORMATION`. Pas de condition de victoire perso. Pas de `recoverPotionEffect` redéfini.

### Interprête

- **Clé :** `interpreter`
- **Classe :** `Interpreter` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Constructeur (Interpreter.java:52-106) : sélectionne **3 rôles** absents de la config (`getRoleCount(key)==0`), non `requireDouble`, non `INTERPRETER` :
1. Un rôle `category=VILLAGER` + `attribute=INFORMATION`.
2. Un rôle `attribute=VILLAGER` (non déjà sélectionné).
3. Un rôle `category=VILLAGER` avec `attribute` ≠ `INFORMATION` et ≠ `HYBRID`.

`onNight` (Interpreter.java:119-138, LOWEST) : après `DAY_DURATION * 4/5` ticks (80% de la durée du jour), si `isAbilityEnabled()` et vivant : `setPower(true)`, envoie un texte cliquable pour chacun des 3 rôles. Le clic exécute `/ww <command> <roleKey>`.

`onDay` (Interpreter.java:161-184, LOWEST) : si toujours Interprète, `isAbilityEnabled()`, vivant, et `hasPower()` : envoie "fin de sélection", `setPower(false)`.

Commande `/ww interpreter <roleKey>` (CommandInterpreter.java) : `requiredPower=true`, `autoCompletion=false`. Valide le roleKey, `setPower(false)`, tire `InterpreterEvent`, appelle `activateRole(roleKey)`.

`activateRole` (Interpreter.java:191-257) : instancie le rôle choisi par réflexion (`getConstructor(WereWolfAPI, IPlayerWW).newInstance`). Si l'Interprète est infecté → `role.setInfected()`. Si neutre → `role.setTransformedToNeutral(true)`. Appelle `recoverPotionEffects()`, enregistre le listener du nouveau rôle, désenregistre le listener de l'Interprète, `setRole(role)`, ajoute la clé Interprète aux `deathRoles`. Après `DAY_DURATION * 2` ticks : si pas mort, **revient à l'Interprète** (désenregistre le rôle, clear effects, `setRole(this)`, retire la clé des deathRoles, message de fin).

`onDeath` (FinalDeathEvent, Interpreter.java:140-159, LOWEST) : si l'Interprète meurt alors qu'il est transformé (`role != null`) : désenregistre le rôle, `role=null`, `setRole(this)` — redevient Interprète à la mort.

Pas de timer/IntValue propre (utilise `DAY_DURATION`). Pas de condition de victoire perso.

### Jumeau

- **Clé :** `twin`
- **Classe :** `Twin` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle villageois (attribute=VILLAGER), `requireDouble=true` (deux instances minimum). Timer `TimerBase.TWIN_DURATION` (défaut 2700s, meetUp 300s, `decrementAfterRole=true`, `onZero=AutoTwinEvent`). Config `IntValueBase.TWIN_DISTANCE` (défaut 50, meetUp 50, step 5).

Révélation — `onTwinReveal(AutoTwinEvent)` (Twin.java:77) : déclenché quand le timer `TWIN_DURATION` atteint zéro. Construit `twinList` = tous les autres joueurs ayant le rôle TWIN. Déclenche `TwinRevealEvent`, envoie un message révélant la liste des jumeaux.

Mort d'un jumeau — `onTwinDeath(FinalDeathEvent)` (Twin.java:106) : si un autre joueur TWIN meurt et que ce Twin est vivant, envoie un message de deuil et applique `STRENGTH` (amplificateur 0, durée 6000) et `SPEED` (amplificateur 0, durée 6000) via `addPotionModifier`.

Pouvoir nocturne — `onNight(NightEvent)` (Twin.java:129) : si vivant, `twinList` non null, aucun `WEAKNESS`/`SLOWNESS` actif sur le joueur, trouve le premier jumeau vivant. Si la distance avec ce jumeau est `< TWIN_DISTANCE*2` (100 blocs par défaut), envoie un message « trop proche » et s'arrête. Sinon, calcule la liste des joueurs vivants (hors jumeau et soi-même) situés à moins de `TWIN_DISTANCE` (50 blocs) du jumeau. Tirage aléatoire 50/50 :
- Soit déclenche `TwinListEvent` et envoie la liste des noms des joueurs proches du jumeau.
- Soit tire un joueur aléatoire parmi eux, déclenche `TwinRoleEvent`, et révèle le rôle affiché (`getDisplayRole()`) de ce joueur au Twin.

Aucune condition de victoire perso.

### Lapin

- **Clé :** `rabbit`
- **Classe :** `Rabbit` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle passif, sans commande. `attribute=VILLAGER`. `defaultWalkSpeed=0.2f`.
`second()` (Rabbit.java:46) : si vivant et ability enabled, si `health < 6` (3 cœurs) change l'état `belowHearts`. Vitesse de marche = `0.2 * 1.1` (10% plus rapide) normalement, `0.2 * 1.2` (20% plus rapide) sous 3 cœurs.
`onCheckReduceDamage` (EntityDamageByEntityEvent, Rabbit.java:62) : si ability enabled, victime vivante, `health < 12` (6 cœurs), victime ni neutre ni loup, distance Rabbit↔victime ≤ 20, et la victime n'a pas déjà RESISTANCE → dégâts × 0.9 (réduit de 10% les dégâts subis par les villageois à basse vie à proximité).
`onNight` (LOWEST, Rabbit.java:100) : ajoute WEAKNESS. `onDay` (Rabbit.java:108) : retire WEAKNESS. `recoverPotionEffect` (Rabbit.java:119) : si ability enabled et qu'il fait nuit, ajoute WEAKNESS (Weakness la nuit).

### Marchande de Fruit

- **Clé :** `fruit_merchant`
- **Classe :** `FruitMerchant` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Timer `FRUIT_MERCHANT_COOL_DOWN` (default 1200s, meetUp 300s). IntValue `FRUIT_MERCHANT_DISTANCE` (default 50, meetUp 50, step 5).

Activation par commande `/ww` sans args (CommandFruitMerchant.java) : `requiredPower=true`. Trouve tous les joueurs vivants à moins de `FRUIT_MERCHANT_DISTANCE`. Met `power=false`. Pour chaque joueur trouvé, `addAffectedPlayer` stocke le nombre initial de pommes d'or (`Utils.countGoldenApple`). Envoie message de confirmation. Après `FRUIT_MERCHANT_COOL_DOWN/2` ticks : recompte les pommes d'or, tire `FruitMerchantRecoverInformationEvent`, envoie à tous les joueurs suivis l'ancien et le nouveau compte. Après un autre `FRUIT_MERCHANT_COOL_DOWN/2` : `setPower(true)` (récupération du pouvoir).

`onDeath` (FinalDeathEvent, FruitMerchant.java:109-136) : si un joueur suivi meurt, tire `FruitMerchantDeathEvent` avec `GoldenCount` (ancien vs nouveau compte de pommes d'or), envoie l'info au FruitMerchant, retire le joueur des affected.

`onAppleEat` (PlayerItemConsumeEvent, priority LOWEST, FruitMerchant.java:138-155) : si le FruitMerchant mange une **Golden Apple**, 25% de chance (`nextInt(4)==0`) de gagner **+2 PV** (`addPlayerHealth(2)`).

`onEat` (PlayerItemConsumeEvent, FruitMerchant.java:157-170) : à chaque consommation d'aliment, `+1` au niveau de faim.

Pas de condition de victoire perso. `attribute=MINOR_INFORMATION`.

### Mire

- **Clé :** `mire`
- **Classe :** `Mire` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

IntValue `MIRE_DISTANCE` (default 60, meetUp 60, step 5). `attribute=MINOR_INFORMATION`.

`playerDamage` (EntityDamageEvent, Mire.java:62-110) : si un joueur a **moins de 6 PV** (3 cœurs) après dégâts, en jeu, vivant :
- **Si c'est la Mire elle-même** : tire `MireUnderThreeHeartsEvent`. Si non annulé : ajoute **Absorption** pendant `600` ticks (30s, amplifier 0). (Commentaire TODO dans le code : "FAIRE CONFIG POUR AJOUTER QU'UN COEUR".)
- **Si c'est un autre joueur à moins de `MIRE_DISTANCE`** : tire `MireNearbyPlayerUnderThreeHeartEvent`. Si non annulé : envoie un message d'avertissement à la Mire.

`eatGoldenApple` (PlayerItemConsumeEvent, Mire.java:112-143) : si la Mire mange une **Golden Apple** : trouve le joueur vivant avec le **plus faible ratio PV/PVmax** (`getHealth()/getMaxHealth()`), tire `MireNearbyPlayerUnderThreeHeartEvent`, envoie le nom à la Mire. Si aucun joueur : message "tout le monde est full".

Pas de `recoverPotionEffect`/`recoverPower` redéfini. Pas de commande dédiée. Pas de timer. Pas de condition de victoire perso.

### Montreur d'Ours

- **Clé :** `bear_trainer`
- **Classe :** `BearTrainer` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information

`attribute=INFORMATION`. Config booléenne `BEAR_TRAINER_EVERY_OTHER_DAY` ; `IntValue BEAR_TRAINER_DISTANCE` défaut 50 (meetUp 50, pas 5). `dayNumber` init à -8.

`@EventHandler onDay` (BearTrainer.java:56-87) : si config "un jour sur deux" active et jour consécutif (`event.getNumber() == dayNumber+1`), skip. Calcule l'ensemble des joueurs vivants à moins de `BEAR_TRAINER_DISTANCE` blocs dont le rôle satisfait `isDisplayCamp(WEREWOLF)` OU (`displayCamp == camp réel` ET `isWereWolf`). Déclenche `GrowlEvent` (SpyEvent) avec cet ensemble.

`@EventHandler onGrowl` (HIGHEST, BearTrainer.java:89-131) : si l'ensemble est vide, annule l'event + message `no_growl`. Sinon : joue `Sound.WOLF_GROWL` à tous les joueurs en ligne, broadcast un message avec une mention "growling" par loup détecté, et fixe l'aura du dresseur selon le nombre : 0→LIGHT, 1→NEUTRAL, ≥2→DARK (`AuraModifier(overwrite=true)`).

Aucune commande dédiée. `recoverPower()` vide. Pas d'effet de potion.

### Occultiste

- **Clé :** `occultist`
- **Classe :** `Occultist` (extends `Villager (extends RoleImpl) — hérite donc du système de VillagerKit (kit aléatoire GOLEM/MINER/ARCHER/PRIEST/BLACK_SMITH/BOOK_SELLER donné dans `Villager.recoverPower`)`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Information mineure

Rôle passif d'information, sans commande dédiée. `defaultAura=DARK`, `attribute=MINOR_INFORMATION`.
L'Occultist collecte les derniers vœux des morts. À chaque `FinalDeathEvent` (Occultist.java:46) : ajoute le mort à `deaths` (si l'Occultist est vivant et ability enabled). Quand `deaths.size()==4` (Occultist.java:59), déclenche `OccultistRevealWishesEvent` ; si non annulé, fusionne `deaths` + `troublemakers`, mélange, et envoie privément à l'Occultist le vœu (`getWish()`) de chacun.
`onWishChangeEvent` (Occultist.java:94) : si le joueur changeant de vœu est un `TROUBLEMAKER`, l'ajoute à `troublemakers` afin que son vœu soit inclus dans la révélation (les TroubleMakers pouvant échanger leurs vœux).
`onDay` (Occultist.java:81) : au jour numéro 6, broadcaste un message d'annonce/consigne.
À sa propre mort (Occultist.java:50), broadcaste son propre vœu.

### Oracle

- **Clé :** `oracle`
- **Classe :** `Oracle` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers (via IPower géré par la base)
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Pouvoir activé par commande `/ww`. `attribute=MINOR_INFORMATION`. Config booléenne `ConfigBase.ORACLE_EVERY_OTHER_DAY`. `setPower(false)` à l'init, `dayNumber=-8`.
`onDay` (Oracle.java:70) : si `ORACLE_EVERY_OTHER_DAY` actif ET `event.getNumber()==dayNumber+1`, on saute le jour (pouvoir un jour sur deux). Sinon `dayNumber=nombre`, `setPower(true)`, envoie un message avec le timer `POWER_DURATION`.
`recoverPotionEffect` (Oracle.java:97) : Vision Nocturne permanente.
Commande `CommandOracle` (CommandOracle.java:30) : cible un joueur vivant, lit son `getAura()`, déclenche `OracleEvent`, `setPower(false)`, `addAffectedPlayer`, envoie le nom + l'aura traduite du joueur. Révèle donc l'aura (camp apparent) d'un joueur par jour.

### Petite Fille

- **Clé :** `little_girl`
- **Classe :** `LittleGirl` (extends `RoleImpl`) · **Marqueurs :** IInvisible
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`recoverPotionEffect` (LittleGirl.java:185-188) : **Vision Nocturne** permanente.

Invisibilité via `onCloseInvent` (InventoryCloseEvent, LittleGirl.java:190-238) : si nuit, en jeu, vivant, et les **4 slots d'armure** (36-39) sont vides :
- Si pas invisible et `isAbilityEnabled()` : ajoute **Invisibility**, retire **Strength** du loup si infectée (`isInfected()`), `setInvisible(true)`, tire `InvisibleEvent(true)`, `UpdateNameTagEvent`.
- Si invisible et armure présente : retire Invisibility, recrée Strength du loup si infectée, `setInvisible(false)`, tire `InvisibleEvent(false)`.

`onDay` (LittleGirl.java:82-100) : si invisible, retire Invisibility, `setInvisible(false)`, `InvisibleEvent(false)`, `UpdateNameTagEvent`, message.

`onNight` (LittleGirl.java:54-63) : envoie message "retirer l'armure". `onDayWillCome` (LittleGirl.java:102-112) : message d'avertissement.

`onWerewolfHowling` (WereWolfHowlingEvent, LittleGirl.java:65-80) : si la cible est la Petite Fille, incrémente `compteur`. Si invisible : `setDuration(20*(5+3*compteur))`, `setCancelled(false)` — les hurlements de loups ciblant la Petite Fille durent plus longtemps et ne sont pas annulés.

`onWWChatLittleGirl` (WereWolfChatEvent, LittleGirl.java:240-246) : si la cible est la Petite Fille, `setCancelled(false)` — peut recevoir le chat des loups.

`second()` (LittleGirl.java:114-157) : si nuit, vivante, invisible : joue un `Effect.STEP_SOUND` à la position de chaque autre joueur invisible — **REDSTONE_BLOCK** si le joueur invisible est un loup, **LAPIS_BLOCK** sinon.

`disableAbilitiesRole` (LittleGirl.java:248-262) : si invisible, retire Strength (loup) et Invisibility, `setInvisible(false)`, `InvisibleEvent(false)`. `defaultAura=NEUTRAL`. Pas de commande dédiée. Pas de condition de victoire perso.

### Prêtresse

- **Clé :** `priestess`
- **Classe :** `Priestess` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information

`attribute=INFORMATION`. IntValue `IntValueBase.PRIESTESS_DISTANCE` (défaut 10, meetUp 10, pas 2). `getAura()` (Priestess.java:117) retourne toujours `LIGHT`. `setPower(false)` à l'init.
`onDay` (Priestess.java:84) : `setPower(true)`, envoie un message avec `PRIESTESS_DISTANCE` et `POWER_DURATION`.
Commande `CommandPriestess` (CommandPriestess.java:29) : cible un joueur vivant à ≤ `PRIESTESS_DISTANCE` ET nécessite `health >= 5`. Déclenche `PriestessEvent` (camp = `getDisplayCamp` de la cible), `setPower(false)`, `addAffectedPlayer`, **`removePlayerMaxHealth(4)`** (coûte 2 cœurs de vie max), envoie le camp apparent.
`onDeath` (FinalDeathEvent, Priestess.java:70) : si le mort est dans `affectedPlayer` ET `isWereWolf()`, envoie un message, retire la cible, **`addPlayerMaxHealth(2)`** (récupère 1 cœur de vie max).
`onAnnounceDeath` (HIGHEST, Priestess.java:121) : à sa propre mort, révèle son vrai rôle dans l'annonce.
`onAnnouncementDeathEvent` (HIGH, Priestess.java:136) : si config `HONOR` active, retour. Sinon fausse l'affichage du rôle des morts ("magic") : pour un neutre non-loup, 5% de chance d'affichage magique ; sinon 80% : si Prêtresse vivante, cache les loups (affiche magic pour les loups) ; si morte, cache les non-loups. 20% : inverse. Brouille donc l'information de rôle des morts selon que la Prêtresse est vivante ou non.
`onCompositionUpdate` (HIGHEST, Priestess.java:178) : annule l'événement (cache les mises à jour de composition).
`onUpdate` (UpdatePlayerNameTagEvent HIGHEST, Priestess.java:183) : pour les joueurs morts, met le suffixe à "" (cache le suffixe de mort).

### Prophétesse

- **Clé :** `prophetess`
- **Classe :** `Prophetess` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Rôle d'information automatique, sans commande. `defaultAura=NEUTRAL`, `attribute=MINOR_INFORMATION`.
`onDay` (HIGHEST, Prophetess.java:39) : si ability disabled, retour. Au 1er jour traité (`!initialized`), snapshot les auras de tous les vivants et retour (initialisation). Les jours suivants, si vivante : compare l'aura actuelle de chaque vivant avec le snapshot précédent ; envoie la liste des joueurs dont l'aura a changé (ou `no_change`), puis re-snapshot.
`snapshotAuras()` (Prophetess.java:74) : mappe `UUID -> Aura` pour tous les `getAlivePlayersWW()`.
Aucun effet de potion, aucun pouvoir consommable. Détecte les changements d'aura (transformations, infectés, etc.) entre deux jours.

### Rebouteux

- **Clé :** `bonesetter`
- **Classe :** `Bonesetter` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, ILimitedUse
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Villageois

`defaultAura=LIGHT`. Compteur `use` max 3, liste `alreadyUsed` pour interdire de re-cibler le même joueur.

Commande `/ww command <joueur>` (excludeSelf) : refusée si `use==3`. Refus si cible déjà dans `alreadyUsed`. Déclenche `BonesetterChooseEvent` (TellableStoryEvent). Incrémente `use`, ajoute la cible à `affectedPlayers` ET à `alreadyUsed` (CommandBonesetter.java:32-52 ; Bonesetter.java:99-103).

`@EventHandler onDamage` (ignoreCancelled, Bonesetter.java:57-87) : si la cible affectée subit des dégâts qui la ramèneraient à ≤8 PV (`health - finalDamage <= 8`), déclenche `BonesetterHealEvent`. Si non annulé : applique Régénération II (amplifier 1) pendant `6*25=150` ticks (7,5s) à la cible, retire la cible des affectedPlayers (protection one-shot). Uniquement en état GAME et cible vivante.

`@EventHandler onConsume` (Bonesetter.java:89-96) : quand le bonesetter mange une pomme dorée (`GOLDEN_APPLE`), `addPlayerHealth(2)` (+1 cœur).

Aucun effet passif permanent. `recoverPower()` vide.

### Renard

- **Clé :** `fox`
- **Classe :** `Fox` (extends `RoleImpl`) · **Marqueurs :** IProgress, ILimitedUse, IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Information mineure

Configs : `FOX_SMELL_NUMBER` (default 3, meetUp 3) — nombre de reniflages ; `FOX_DISTANCE` (default 15, meetUp 15, step 5) — distance max. Timer `FOX_SMELL_DURATION` (default 90s, meetUp 30s) — durée d'un reniflage.

Activation par commande `/ww fox <joueur>` (CommandFox.java) : `requiredPower=true`, cible vivante, `excludeSelf`, `maxDistanceKey=FOX_DISTANCE`. Vérifie `use < FOX_SMELL_NUMBER`. Met `power=false`, tire `BeginSniffEvent`, efface les affected players, ajoute la cible, remet `progress=0`.

Effets passifs : `recoverPotionEffect()` (Fox.java:158-163) ajoute **Speed** si c'est la nuit et vivant.

`onNight` (Fox.java:111-125, HIGH) : si `use < FOX_SMELL_NUMBER`, `setPower(true)`, ajoute **Speed**, envoie un message avec le nombre de reniflages restants. `onDay` (Fox.java:127-134) : retire Speed, efface affected players, `progress=0`.

`second()` (Fox.java:172-250) : si nuit, vivant, a une cible affectée vivante à moins de `FOX_DISTANCE` : incrémente `progress` de `100/(FOX_SMELL_DURATION+1)` par seconde. Envoie un message de progression tous les ~10%. À `progress >= 100` : détermine si la cible est un loup (`isDisplayCamp(WEREWOLF)` ou camp affiché == camp réel && `isWereWolf()`), tire `SniffEvent`. Si non annulé : ajoute la cible à `sniffedPlayers`, `use++`, envoie message. Si `use >= FOX_SMELL_NUMBER` : compte les loups parmi les reniflés, envoie le résultat. Si `wolfCount > 0` : ajoute un `AuraModifier` **DARK** (priorité 1, non temporaire) — l'aura du Renard devient sombre.

`disableAbilitiesRole()` : retire Speed. `defaultAura=LIGHT`, `auraDescriptionSpecialUseCase` spécial. Pas de condition de victoire perso.

### Salvateur

- **Clé :** `protector`
- **Classe :** `Protector` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Villageois

`defaultAura=LIGHT`, `attribute=VILLAGER`. `setPower(false)` à l'init. Mémorise le dernier protégé dans `last`.
`onDay` (LOWEST, Protector.java:68) : retire `RESISTANCE` et l'aura modifier du `last` protégé, `last=null`, `setPower(true)`, envoie un message avec `POWER_DURATION`.
Commande `CommandProtector` (CommandProtector.java:33, `rejectIfAlreadyAffected`) : `setPower(false)`, déclenche `ProtectionEvent`, `clearAffectedPlayer` + `addAffectedPlayer` (définit `last`), donne à la cible **RESISTANCE permanente** + un `AuraModifier` LIGHT. Message de confirmation.
`onPlayerDamage` (EntityDamageEvent, Protector.java:106) : si la victime est le `last` protégé et la cause est `FALL`, dégâts × 0.5.
La protection dure donc jusqu'au prochain jour (levée au `DayEvent`).

### Servante dévouée

- **Clé :** `devoted_servant`
- **Classe :** `DevotedServant` (extends `RoleImpl`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`defaultAura=NEUTRAL`. Ne gère qu'une seule cible (`playerWW` unique via addAffectedPlayer).

`@EventHandler onMaskedDeath` (FirstDeathEvent, DevotedServant.java:113-200) : si abilityEnabled, hasPower, vivant, et que le mort est un VILLAGER, et que le tueur n'est ni le serviteur lui-même ni un `GRIMY_WEREWOLF` → envoie un message cliquable `/ww command <uuid>`. Programme une tâche différée de 15s (15*20 ticks) : si la cible n'est plus morte (ressuscitée entre-temps), clear + message de résurrection ; sinon déclenche `DevotedServantEvent` (TellableStoryEvent), `setPower(false)`, et vole le rôle : `publicClone()` du rôle de la cible, `setRole(roleClone)`, enregistre son listener, préserve l'état infected/solitary, déclenche `NewWereWolfEvent` si le rôle cloné est un loup natif, ajoute `DEVOTED_SERVANT` aux death-roles du serviteur, puis `recoverPower()`+`recoverPotionEffects()` sur le clone.

CommandDevotedServant `/ww command <uuid>` (`requiredPower`, cible en JUDGEMENT, byUuid) : ajoute la cible (unique). C'est la sélection déclenchée par le message cliquable (CommandDevotedServant.java:34-37).

`@EventHandler onAnnouncementDeath` (DevotedServant.java:56-70) : si l'annonce de mort concerne la cible volée, remplace le rôle annoncé par la clé du serviteur (`event.setRole(this.getKey())`).

`@EventHandler onCompositionUpdate` (HIGHEST, DevotedServant.java:72-92) : si raison DEATH et clé = rôle de la cible volée, annule une fois l'update de composition (cache un mort de la compo, flag `hide`).

`@EventHandler onUpdate` (UpdatePlayerNameTagEvent, DevotedServant.java:94-111) : si la cible volée est morte, fixe son suffixe de nametag à la clé du serviteur.

Aucun effet de potion propre. `recoverPower()` vide.

### Serviteur

- **Clé :** `servitor`
- **Classe :** `Servitor` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`attribute=VILLAGER`. IntValue `IntValueBase.SERVITOR_DISTANCE` (défaut 25, meetUp 25, pas 5). `power=true` à l'init. Pas de commande : le maître est auto-sélectionné.
`onDay` (Servitor.java:69) : si ability enabled, `hasPower`, vivant : déclenche `ServitorMasterChosenEvent` avec `Utils.autoSelect(...)`, définit `master`, envoie le nom du maître.
`onSecondDeathEvent` (LOW ignoreCancelled, Servitor.java:98) : si ability enabled, `hasPower`, `master!=null`, et le mourant est le Servitor tué par son `master` → annule l'événement (résurrection), message, `autoResurrection()`.
`autoResurrection()` (Servitor.java:126) : `setPower(false)`, déclenche `ServitorDefinitiveMasterEvent`, `game.resurrection(servitor)`. Le pouvoir est alors consommé définitivement.
`second()` (Servitor.java:141) : si `master` mort, retire WEAKNESS/STRENGTH. Si Servitor vivant et `master` à < `SERVITOR_DISTANCE` : si `power` encore disponible → ajoute STRENGTH ; sinon (après résurrection, pouvoir consommé) → ajoute WEAKNESS. Hors portée : retire les deux. Le Servitor est donc renforcé près de son maître tant qu'il a son pouvoir, puis affaibli (Weakness) près de son maître après avoir été ressuscité par lui.

### Soeur

- **Clé :** `sister`
- **Classe :** `Sister` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

`attribute=VILLAGER`, `requireDouble=true`. IntValue `IntValueBase.SISTER_DISTANCE` (défaut 20, meetUp 20, pas 2). `IAffectedPlayers` = `killerWWS` (tueurs mémorisés).
`onWerewolfList` (Sister.java:85) : à la liste des loups, envoie la liste des sœurs (`getSister`).
`second()` (Sister.java:104) : si vivante et qu'une autre `SISTER` vivante est à < `SISTER_DISTANCE`, ajoute RESISTANCE (durée 100, niveau 0). Effet de proximité entre sœurs.
`onSisterDeath` (FinalDeathEvent, Sister.java:181) : à sa propre mort, déclenche `SisterDeathEvent` (avec son `lastKiller` ou null).
`onSisterDeathReveal` (SisterDeathEvent, Sister.java:132) : si non annulé et vivante, s'ajoute à `allSisters`, envoie un choix cliquable : voir le **nom** du tueur (`/ww <command_name> <uuid|pve>`) OU voir le **rôle** du tueur (`/ww <command_role> <uuid|pve>`). Ajoute le `lastKiller` de la sœur morte à `killerWWS`.
`onSisterDeathRevealEnd` (SisterDeathEvent HIGHEST, Sister.java:176) : si `allSisters` vide (aucune sœur survivante), annule l'événement.
`CommandSisterSeeName` (CommandSisterSeeName.java:23) : retire le killerWW de `killerWWS`, déclenche `SisterSeeNameEvent`, envoie le nom du tueur (ou "pve").
`CommandSisterSeeRole` (CommandSisterSeeRole.java:23) : retire le killerWW, déclenche `SisterSeeRoleEvent`, envoie le `getDisplayRole` du tueur (ou "pve").
Chaque sœur survivante choisit donc, à la mort d'une sœur, de révéler soit le nom, soit le rôle de son tueur (un seul choix par tueur via `killerWWS`).

### Sorcière

- **Clé :** `witch`
- **Classe :** `Witch` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Villageois

Rôle villageois (attribute=VILLAGER), `defaultAura=DARK`. Config imbriquée `ConfigBase.WITCH_AUTO_RESURRECTION` (défaut `true`). `power` démarre à `true`. Usage unique.

Auto-résurrection — `onThirdDeathEvent(ThirdDeathEvent, ignoreCancelled=true)` (Witch.java:99) : si `hasPower()` et `isAbilityEnabled()` :
- Si la mourante est la Sorcière elle-même : appelle `autoResurrection()`. Si `WITCH_AUTO_RESURRECTION` est actif, déclenche `WitchResurrectionEvent(self, self)`, consomme le pouvoir, ressuscite la Sorcière, annule la mort. Si la config est inactive, l'auto-résurrection échoue (la Sorcière meurt).
- Sinon (un autre joueur meurt) et la Sorcière est vivante : envoie un texte cliquable proposant de ressusciter ce joueur via la commande `/ww witch.command <uuid>`.

Commande dédiée `CommandWitch` (`/ww`, `requiredPower=true`, `autoCompletion=false`, 1 arg joueur en état `JUDGEMENT`, par UUID) : si `WITCH_AUTO_RESURRECTION` est inactif et que la cible est la Sorcière elle-même, refus. Si `game.getTimer() - deathTime < 7` (moins de 7s depuis la mort), ignoré. Sinon consomme le pouvoir, déclenche `WitchResurrectionEvent`, ajoute la cible aux affected players, ressuscite la cible.

Résumé config : `WITCH_AUTO_RESURRECTION=true` → la Sorcière se ressuscite automatiquement à sa propre mort ET peut ressusciter autrui manuellement. `=false` → pas d'auto-résurrection, ne peut ressusciter qu'autrui (pas elle-même).

Aucun effet de potion, aucune condition de victoire perso.

### Témoin

- **Clé :** `witness`
- **Classe :** `Witness` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Rôle d'information mineur (attribute=MINOR_INFORMATION). `power` démarre à `true`. Aucune config/IntValue/timer annoté (mais lit `TimerBase.WEREWOLF_LIST` pour la description).

Révélation du coupable — `onWereWolfListEvent(WereWolfListEvent)` (Witness.java:98) : au moment où la liste des loups est révélée (timer `WEREWOLF_LIST`), récupère tous les loups-garous vivants, en choisit un au hasard (`Random` basé sur `System.currentTimeMillis()`), l'ajoute aux affected players et envoie un message révélant son nom.

Perte à la mort du coupable — `onFinalDeathEvent(FinalDeathEvent)` (Witness.java:116) : si le coupable (affected player) meurt et que (le Witness est vivant OU le pouvoir est encore disponible), consomme le pouvoir (`power=false`), retire 4 points de vie max (`removePlayerMaxHealth(4)`, 2 cœurs) et notifie. Le Witness perd donc 2 cœurs quand son coupable meurt.

Suivi du vol — `onTargetIsStolen(StealEvent)` (Witness.java:135) : si le coupable est volé (victime d'un StealEvent), remplace le coupable par le voleur dans les affected players et notifie (si le Witness est vivant).

Si le Witness est lui-même volé — `onStealEvent(StealEvent)` (Witness.java:150) : si le pouvoir est encore disponible, re-révèle le nom du coupable au Witness (qui a changé de corps). Si le pouvoir est déjà consommé, retire à nouveau 4 points de vie max.

Suivi du swap — `onSwap(SwapEvent, priority=HIGHEST)` (Witness.java:162) : si le Witness est vivant, le pouvoir disponible, et que le coupable est impliqué dans un swap, met à jour le coupable vers l'autre partie du swap et notifie (si `isAbilityEnabled()`).

Réduction de dégâts — `onPlayerDamage(EntityDamageByEntityEvent)` (Witness.java:189) : si `hasPower()` et que le Witness (en tant qu'attaquant) frappe le coupable, réduit les dégâts de 30 % (`damage * 0.7`).

Aucune condition de victoire perso.

### Tombeur

- **Clé :** `stud`
- **Classe :** `Stud` (extends `RoleImpl`) · **Marqueurs :** IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Hybride

Rôle hybride (attribute=HYBRID). `power` démarre à `true`. Aucune commande dédiée, aucun effet de potion, aucune config/IntValue.

Pouvoir principal — `onSecondDeathEvent` (priority=LOWEST, Stud.java:66) : déclenché quand le Stud subit une mort (SecondDeathEvent). Conditions : événement non annulé, le mourant est le Stud, `hasPower()` est vrai, `isAbilityEnabled()`, le Stud a un `getLastKiller()` présent et vivant, et le tueur n'est pas déjà un lover du Stud. Effet : déclenche `StudLoverEvent(stud, killer)` + `UpdateNameTagEvent` pour les deux, consomme le pouvoir (`setPower(false)`), crée un `LoverImpl` (couple classique) entre le Stud et son tueur via `game.getLoversManager().addLover(...)`, enregistre le listener, annonce le couple (`announceLovers()`), annule la mort (`event.setCancelled(true)`) et ressuscite le Stud (`game.resurrection(...)`).

`onDeath(FinalDeathEvent)` (Stud.java:56) : à la mort définitive du Stud, si `power` est `false` (pouvoir déjà consommé → un couple a été formé), ajoute `LoverBase.LOVER` à la composition via `game.getConfig().addOneLover(...)` afin que le couple apparaisse dans la compo (le couple du Stud n'étant pas affiché à l'origine).

Aucune condition de victoire perso (camp VILLAGER ; le couple peut changer la dynamique).

### Trappeur

- **Clé :** `trapper`
- **Classe :** `Trapper` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle villageois (attribute=VILLAGER). `power` démarre à `false` (forcé dans le constructeur, Trapper.java:38). Aucune config/IntValue.

Activation : sur `DayEvent` (Trapper.java:72), si le Trapper est vivant, `setPower(true)` et envoi d'un message de suivi. Le pouvoir est donc réactivé chaque jour.

Commande dédiée `CommandTrapper` (`/ww`, `requiredPower=true`, 1 arg joueur vivant, `excludeSelf=true`, `rejectIfAlreadyAffected=true`) : déclenche `TrackEvent`, consomme le pouvoir (`setPower(false)`), efface les affected players et ajoute la cible. Notifie la cible (« you are being tracked ») et le Trapper.

Affichage directionnel — `onActionBarRequest(ActionBarEvent)` (Trapper.java:99) : si l'ActionBar demandée est celle du Trapper, qu'il est vivant, et que `hasPower()` est `false` (une cible est suivie), appende à l'ActionBar le nom de chaque affected player vivant ainsi qu'une flèche directionnelle (`Utils.updateArrow(player, targetLocation)`) pointant vers lui. Tant que le pouvoir n'est pas re-consommé le lendemain, la flèche reste visible.

Aucune condition de victoire perso.

### Trublion

- **Clé :** `troublemaker`
- **Classe :** `Troublemaker` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle villageois (attribute=VILLAGER), `defaultAura=NEUTRAL`. `power` démarre à `true`. Aucune config/IntValue. Le pouvoir n'est jamais réactivé (usage unique).

Commande dédiée `CommandTroubleMaker` (`/ww`, `requiredPower=true`, 1 arg joueur vivant, pas d'exclusion de soi) : déclenche `TroubleMakerEvent`, consomme le pouvoir (`setPower(false)`), ajoute la cible aux affected players, téléporte la cible à une position aléatoire autour d'un cercle (`mapManager.transportation(target, random*2π)`), notifie la cible et le Troublemaker.

Accès au chat loup — `onRequestAccessWereWolfChat(WereWolfCanSpeakInChatEvent)` (Troublemaker.java:106) : si le Troublemaker est vivant, est le joueur concerné, et `isAbilityEnabled()`, force `event.setCanSpeak(true)`. Le Troublemaker peut donc parler dans le chat des loups-garous en permanence (sans être loup).

Mort — `onFinalDeath(FinalDeathEvent)` (Troublemaker.java:42) : à la mort du Troublemaker, si `isAbilityEnabled()`, déclenche `TroubleMakerDeathEvent`, broadcaste un message d'annonce, puis téléporte TOUS les joueurs vivants en cercle autour d'un point central (`transportation(player, i*2π/playersCount)`).

Aucune condition de victoire perso.

### Vaudouiste

- **Clé :** `vaudouiste`
- **Classe :** `Vaudouiste` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Obscure · **Attribut :** Information mineure

Rôle d'information mineur (attribute=MINOR_INFORMATION), `defaultAura=DARK`. Constantes : `PROXIMITY_RANGE=20`, `PROXIMITY_REQUIRED=120`, `DOLL_DURATION=900` (15 min), `MAX_DOLLS=3`, `SACRIFICE_HEALTH=2` (1 cœur). Aucune config/IntValue/timer annoté. `addAffectedPlayer` stocke la cible dans une Map avec un compte à rebours de 900s.

Suivi de proximité — `second()` (Vaudouiste.java:66) : chaque seconde, si `isAbilityEnabled()` et vivant : (1) décrémente les minuteurs des poupées existantes, retire celles expirées (`removeAffectedPlayer`) et notifie ; (2) pour chaque joueur vivant à moins de 20 blocs, incrémente `proximitySeconds` (plafonné à 120) ; à 120 atteint, envoie un message « proximité suffisante ».

Création de poupée — `createDoll(target)` via `CommandVaudou` (`/ww`, 1 arg joueur vivant, `excludeSelf=true`, `rejectIfAlreadyAffected=true`) : refuse si poupée existante, si proximité < 120, ou si `dolls.size() >= 3`. Déclenche `VoodooCreateDollEvent`, ajoute la cible (minuteur 900s), notifie le Vaudouiste.

Notification de dégâts — `onDamage(EntityDamageEvent, ignoreCancelled=true)` (Vaudouiste.java:161) : si un joueur porteur d'une poupée subit des dégâts mortels (`getFinalDamage() > 0`), envoie au Vaudouiste un message avec le nom et le nombre de cœurs de dégâts.

Mort d'une cible — `onFinalDeath(FinalDeathEvent)` (Vaudouiste.java:182) : si une cible de poupée meurt, retire la poupée. Si le Vaudouiste est vivant et qu'un tueur est identifié, ajoute une `PendingSacrifice(dead, killer)` à la file et notifie. Sinon, envoie un message différent (pas de tueur).

Sacrifice — `sacrifice()` via `CommandSacrifice` (`/ww`, 0 arg) : si la file `pendingSacrifices` est vide, message d'erreur. Sinon, défile la plus ancienne, réduit la vie max du Vaudouiste de 2 (`addPlayerMaxHealth(-2)`, soit 1 cœur) et révèle le nom du mort, le nom et le rôle du tueur.

Aucune condition de victoire perso.

### Vieux sage

- **Clé :** `wise_elder`
- **Classe :** `WiseElder` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Information mineure

Rôle d'information mineur (attribute=MINOR_INFORMATION). Deux IntValues : `WISE_ELDER_DISTANCE` (défaut 15, meetUp 15, step 3) et `WISE_ELDER_BEGIN_DAY` (défaut 6, meetUp 3, step 1). Trois compteurs internes : `neutralCounter`, `darkCounter`, `lightCounter`. Booléen `active` démarre à `false`.

Activation — `onDay(DayEvent)` (WiseElder.java:72) : si `isAbilityEnabled()` et vivant, lorsque `event.getNumber() == WISE_ELDER_BEGIN_DAY`, passe `active=true`. Si `active`, déclenche `WiseElderRevealAuraAmountEvent(playerWW, neutral, dark, light)`, envoie un message avec les trois compteurs, puis `resetCounters()`.

Comptage — `second()` (WiseElder.java:108) : chaque seconde, si `isAbilityEnabled()`, `active`, et vivant, parcourt tous les joueurs en ligne (via `Bukkit.getOnlinePlayers()`), filtre ceux vivants et à moins de `WISE_ELDER_DISTANCE` blocs, lit `getRole().getAura()` et incrémente le compteur correspondant (NEUTRAL/DARK/LIGHT). Le comptage est cumulatif sur tout le cycle jour-nuit.

Aucune commande dédiée, aucun effet de potion, aucune condition de victoire perso. Le WiseElder apprend donc chaque jour (à partir de `WISE_ELDER_BEGIN_DAY`) le total cumulé des auras des joueurs passés à portée.

### Villageois

- **Clé :** `villager`
- **Classe :** `Villager` (extends `RoleImpl`) · **Marqueurs :** aucune
- **Camp :** Villageois · **Aura :** Neutre · **Attribut :** Villageois

Rôle villageois (attribute=VILLAGER). Aucune config/IntValue/timer annoté (mais utilise `ConfigBase.TROLL_ROLE`).

Constructeur (Villager.java:34) : si la partie est en cours (`StateGame.GAME`), tire aléatoirement un `VillagerKit` parmi les 6 valeurs (GOLEM, MINER, ARCHER, PRIEST, BLACK_SMITH, BOOK_SELLER). Si `TROLL_ROLE` est inactif, déclenche `VillagerKitEvent` (annonçant le kit aux statistiques).

`recoverPower()` (Villager.java:55) : si `TROLL_ROLE` inactif et `isAbilityEnabled()`, donne des objets selon le kit tiré :
- `GOLEM` : 15 lingots de fer + 3 livres enchantés Solidité III.
- `MINER` : pioche en diamant Efficacité II.
- `ARCHER` : 64 flèches + 6 ficelles.
- `PRIEST` : 3 pommes dorées.
- `BLACK_SMITH` : 1 enclume + 10 flacons d'expérience.
- `BOOK_SELLER` : 8 livres + 10 flacons d'expérience.

`onEndPlayerMessage(EndPlayerMessageEvent)` (Villager.java:96) : ajoute le nom du kit au message de fin de partie du joueur.

Aucun effet de potion, aucune condition de victoire perso.

### Voyante

- **Clé :** `seer`
- **Classe :** `Seer` (extends `RoleWithLimitedSelectionDuration`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Villageois · **Aura :** Lumineuse · **Attribut :** Information

`attribute=INFORMATION`, `defaultAura=LIGHT`. Configs `SEER_EVERY_OTHER_DAY` et `CHATTY_SEER`. `setPower(false)` à l'init, `dayNumber=-8`.
`onDay` (Seer.java:69) : si vivant, si `SEER_EVERY_OTHER_DAY` actif et `event.getNumber()==dayNumber+1` retour. Sinon `dayNumber=nombre`, `setPower(true)`, message avec `POWER_DURATION`.
`recoverPotionEffect` (Seer.java:109) : Vision Nocturne permanente.
Commande `CommandSeer` (CommandSeer.java:37) : cible un vivant, lit son `getDisplayCamp` (VILLAGER/NEUTRAL/WEREWOLF), déclenche `SeerEvent`, `setPower(false)`, `addAffectedPlayer`. Construit deux rôles : le `getDisplayRole` de la cible + un leurre (`pickDecoy`, CommandSeer.java:87 : rôle présent dans la config, de catégorie VILLAGER si la cible n'est pas villageoise). Ordre aléatoire, envoie les deux rôles. Si `CHATTY_SEER` actif, broadcaste les deux rôles à tout le monde. La Voyante sait donc qu'un des deux rôles est le vrai, l'autre est un faux.

## Camp : Loups-Garous

### Grand Méchant Loup

- **Clé :** `big_bad_werewolf`
- **Classe :** `BigBadWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** IPower
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup à aura DARK par défaut, mais `getAura()` (BigBadWerewolf.java:121-124) est surchargé pour toujours retourner `Aura.DARK` — l'aura est inconditionnellement DARK, ignorant les aura modifiers. `auraDescriptionSpecialUseCase` définie. Aucune config/timer. Aucune commande dédiée. Aucun event dédié.

Système de Force par paliers (`strengthLevel`, init à 2) :
- `recoverPotionEffect()` (BigBadWerewolf.java:114-118) : si abilityEnabled et `strengthLevel >= 1`, ajoute `STRENGTH` (clé BigBad).
- `onDeath(FinalDeathEvent)` (BigBadWerewolf.java:61-82) : si un loup (autre que self) meurt → `wolfDiedThisCycle=true`, `strengthLevel=0`, retire `STRENGTH`, envoie un message d'avertissement (sauf si self est déjà mort).
- `onDay(DayEvent)` (BigBadWerewolf.java:84-104) : si `wolfDiedThisCycle` → reset flag, `strengthLevel=0`, retire Force. Sinon si `strengthLevel==0` → `strengthLevel=1`, ajoute Force (récupération partielle).
- `onStrengthRate(RequestStrengthRateEvent, NORMAL)` (BigBadWerewolf.java:106-111) : si `strengthLevel==1` et self → divise le `strengthRate` par 2 (Force réduite de moitié).
- `disableAbilitiesRole()` (BigBadWerewolf.java:53-59) : retire `STRENGTH` (clé BigBad).

Logique des paliers : niveau 2 initial (Force pleine) → mort d'un loup → niveau 0 (pas de Force) → lendemain sans mort → niveau 1 (Force à moitié) → reste à 1 définitivement. Le niveau 2 n'est jamais récupéré après une première perte. `hasPower()`/`setPower()` existent mais le pouvoir n'est jamais consommé ni réinitialisé dans la classe (power reste true).

Hérite de RoleWereWolf (Vision Nocturne + Force la nuit via clé WEREWOLF, en plus de sa propre Force).

### Infect Père des Loups

- **Clé :** `infect_father_of_the_wolves`
- **Classe :** `InfectFatherOfTheWolves` (extends `RoleWereWolf`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup natif, aura DARK. `weight = 1.5f` (poids de tirage accru). Config imbriquée : `@Configuration(config = @ConfigurationBasic(key = ConfigBase.INFECT_AUTO_RESURRECTION))` — toggle d'auto-résurrection.

Système de corruption passive (`second()`, InfectFatherOfTheWolves.java:94) : chaque seconde, si ALIVE et `isAbilityEnabled()`, pour chaque joueur vivant non-loup : s'il est à moins de 20 blocks (`CORRUPTION_DISTANCE`) de l'Infect, +1/5 de corruption ; pour chaque autre loup vivant à moins de 20 blocks de ce joueur, +1/20. Plafonné à 100 (`CORRUPTION_THRESHOLD`). `isCorrupted(uuid)` = corruption >= 100.

Pouvoir d'infection (one-shot, `power` init true) : `onSecondDeathEvent` (InfectFatherOfTheWolves.java:156, priority HIGH, ignoreCancelled) : si `hasPower()` et `isAbilityEnabled()`, la victime a un killer qui est un loup. Deux cas :
- Si la victime est l'Infect lui-même : tente `autoResurrection()` → annule l'event si succès. `autoResurrection()` (ligne 199) : nécessite `INFECT_AUTO_RESURRECTION` actif, fire `InfectionEvent(self, self)`, `setPower(false)`, puis `game.resurrection(self)`. Si annulé, message d'erreur et retour false.
- Sinon : si l'Infect est ALIVE et la victime est corrompue (`isCorrupted`), envoie un message cliquable `/ww infect <uuid>`.

Commande dédiée `CommandInfect` (CommandInfect.java:24) : `requiredPower=true`, cible en `JUDGEMENT` par UUID. Vérifie : killer de la cible est un loup, délai `getTimer() - deathTime <= 7`, ET `isCorrupted(uuid)`. Si OK : `setPower(false)`, fire `InfectionEvent(playerWW, cible)` (qui a un flag `informInfectionCancelledMessage`), si non annulé : `addAffectedPlayer(cible)`, `game.resurrection(cible)`, puis si la cible n'était pas déjà loup → `setInfected()` + fire `NewWereWolfEvent`. `setInfected()` rappelé une 2e fois (commentaire "répétition indispensable"). `game.checkVictory()` final.

`recoverPower()` vide. `getDescription()` affiche l'état du pouvoir (disponible/non) et une section items. Pas de timer/IntValue propre (la distance 20 et les taux 1/5, 1/20 sont des constantes hardcodées).

### Loup Garou

- **Clé :** `werewolf`
- **Classe :** `WereWolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup natif de base, aura DARK. Rôle le plus simple : aucun pouvoir actif, aucune commande dédiée, aucun @EventHandler, aucun timer/config/IntValue.

Hérite uniquement du comportement de `RoleWereWolf` : `isWereWolf()=true`, et `recoverPotionEffect()` (de la classe mère) donne automatiquement Vision Nocturne permanente + Force la nuit à tous les loups. `recoverPower()` vide. La description n'affiche que les effets standards du loup (`werewolf.description.werewolf`).

### Loup Garou Alpha

- **Clé :** `alpha_werewolf`
- **Classe :** `AlphaWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup à aura DARK par défaut. Aucune config/timer/IntValue. Aucune commande dédiée. Aucun event dédié.

Effet passif nocturne : `recoverPotionEffect()` (AlphaWereWolf.java:44-53) ajoute `ABSORPTION` (durée 6000 ticks = 5 min, amplificateur 0) si c'est la nuit et abilityEnabled. `onNight(NightEvent)` (AlphaWereWolf.java:55-62) ajoute la même `ABSORPTION` à chaque transition de nuit (vérifie abilityEnabled mais pas isState(ALIVE)).

Préfixe de chat loup : `onChat(WereWolfChatPrefixEvent)` (AlphaWereWolf.java:64-74) — si l'émetteur est l'Alpha et que le demandeur est un loup, définit un préfixe personnalisé (`werewolf.roles.alpha_werewolf.prefix`) et ajoute un formatter `&alpha&` contenant le nom de l'Alpha. Cela marque visuellement ses messages dans le chat des loups.

`recoverPower()` vide. Pas de condition de victoire perso. Hérite des effets RoleWereWolf (Vision Nocturne + Force la nuit).

### Loup Garou Craintif

- **Clé :** `fearful_werewolf`
- **Classe :** `FearFulWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Neutre · **Attribut :** Loup-Garou

Loup à aura NEUTRAL par défaut. Config `IntValueBase.FEARFUL_WEREWOLF_DISTANCE` (défaut 20, meetUp 20, step 4). Aucune commande dédiée. Aucun event dédié propre.

Immunités et dissimulation de mort :
- `onUpdateCompo(UpdateCompositionEvent)` (FearFulWerewolf.java:62-74) : si la raison est `DEATH` et la clé est `FEARFUL_WEREWOLF` → `setCancelled(true)` (la mort n'apparaît pas dans la composition).
- `onVote(VoteEvent)` (FearFulWerewolf.java:76-83) : si le FearFul est voté → `setCancelled(true)` (ne peut pas recevoir de votes).
- `onFirstDeath(FirstDeathEvent, LOWEST, ignoreCancelled=true)` (FearFulWerewolf.java:85-98) : si le FearFul est la cible → `setCancelled(true)` (annule la mort immédiate), puis planifie `game.death()` après 14 secondes (280 ticks) si toujours en état `JUDGEMENT`. Le FearFul survit temporairement mais meurt après le délai sauf changement d'état.
- `onDeath(AnnouncementDeathEvent)` (FearFulWerewolf.java:100-107) : annule l'annonce de mort.
- `onUpdate(UpdatePlayerNameTagEvent, HIGH)` (FearFulWerewolf.java:109-121) : si mort → `setTabVisibility(false)` (caché du tab).
- `onLoverDeathMessage(AnnouncementLoverDeathEvent)` (FearFulWerewolf.java:123-128) : annule le message si le mort est le FearFul ou si le FearFul est dans les lovers du mort.
- `onRequestHowling(WereWolfCanHowlingEvent)` (FearFulWerewolf.java:130-139) : `setCanHowling(false)` — le FearFul ne peut pas hurler.

Effets passifs dynamiques via `second()` (FearFulWerewolf.java:141-208) : compte les loups vivants (hors self) à moins de `FEARFUL_WEREWOLF_DISTANCE` blocs (`number`) :
- `number == 0` (seul) : ajoute `SPEED`.
- `number >= 4` : ajoute `WEAKNESS`.
- `number <= 1` : si jour → `RESISTANCE` + retire `STRENGTH` (clé WEREWOLF) ; si nuit → `STRENGTH` (clé WEREWOLF) + retire `RESISTANCE`.
- `number` entre 2 et 3 : retire à la fois `STRENGTH` (clé WEREWOLF) et `RESISTANCE`.
- Les effets SPEED/WEAKNESS utilisent la clé du FearFul ; STRENGTH/RESISTANCE alternent entre clé FearFul et clé WEREWOLF selon le contexte.

`disableAbilitiesRole()` (FearFulWerewolf.java:210-222) : retire SPEED, RESISTANCE, WEAKNESS (clé FearFul). `recoverPower()` vide. Hérite de RoleWereWolf (Vision Nocturne + Force la nuit, mais la `second()` retire explicitement la Force du loup de base quand `number <= 1` le jour ou `number >= 2`).

### Loup Garou Feutré

- **Clé :** `falsifier_werewolf`
- **Classe :** `FalsifierWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Lumineuse · **Attribut :** Loup-Garou

Loup à aura LIGHT par défaut. `auraDescriptionSpecialUseCase` définie. Aucune config/timer. Aucune commande dédiée. Event dédié : `NewDisplayRole` (@StatisticsEvent, cancellable, expose `newDisplayRole`/`newDisplayCamp`).

Constructeur (FalsifierWereWolf.java:36-40) : définir immédiatement `setDisplayCamp(Camp.VILLAGER)` et `setDisplayRole(RoleBase.VILLAGER)` — le Falsifier apparaît comme un simple Villageois dès le départ.

Déguisement automatique : `onSelectionEnd(SelectionEndEvent)` (FalsifierWereWolf.java:42-84) — déclenché à la fin de la phase de sélection (attribution des rôles) :
- Si pas ALIVE → return.
- Si ability désactivée (`!isAbilityEnabled()`) : révèle sa vraie nature — `setDisplayCamp(WEREWOLF)`, `setDisplayRole(FALSIFIER_WEREWOLF)`, ajoute `AuraModifier` DARK.
- Si ability activée : collecte tous les vivants non-loups, tire un rôle au hasard, fire `NewDisplayRole` (cancellable).
  - Si annulé : révèle sa vraie nature (WEREWOLF/FALSIFIER, aura DARK).
  - Sinon : `setDisplayRole` = rôle tiré, `setDisplayCamp` = camp de l'event, ajoute `AuraModifier` avec l'`getDefaultAura()` du rôle tiré (l'aura affichée correspond au déguisement).
  - Envoie un message au Falsifier avec le rôle affiché.

`getDescription()` (FalsifierWereWolf.java:87-96) : affiche une ligne supplémentaire avec le rôle actuellement affiché (`getDisplayRole()`).

`recoverPower()` vide. Le déguisement est définitif (pas de reset dans la classe). Hérite de RoleWereWolf (Vision Nocturne + Force la nuit, mais son aura apparente est celle du déguisement).

### Loup Garou Grimeur

- **Clé :** `grimy_werewolf`
- **Classe :** `GrimyWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Loups-Garous · **Aura :** Lumineuse · **Attribut :** Loup-Garou

Loup natif (isWereWolf=true via RoleWereWolf). Aura LIGHT par défaut (loup "éclairé"). `weight` non défini.

Pouvoir principal (one-shot, consommé via `power`) : quand le GrimyWereWolf tue un joueur, `onSecondDeathEvent` (GrimyWereWolf.java:93) se déclenche. Conditions : `hasPower()` vrai, `isAbilityEnabled()`, la victime a un `lastKiller` qui est bien ce joueur, la victime n'est pas soi-même, et le Grimy est ALIVE. Il envoie alors un message cliquable proposant la commande `/ww grim <uuid>` (GrimyWereWolf.java:118-126).

Commande dédiée `CommandGrimy` (CommandGrimy.java:17) : `requiredPower=true`, cible = joueur en `JUDGEMENT` par UUID, `excludeSelf`. Vérifie que le killer de la victime est bien le joueur ET que `game.getTimer() - victim.getDeathTime() <= 7` (délai de 7s). Appelle `grime(victim)`.

Méthode `grime()` (GrimyWereWolf.java:129) : fire `GrimEvent` (Cancellable, extends SelectionEvent). Si annulé, message d'erreur. Sinon : `power=false`, retire 1 `RoleBase.WEREWOLF` de la composition (`removeOneRole`), ajoute la victime à `affectedPlayer`, et détermine `grimedRoleKey` : si d'autres loups vivants existent, prend au hasard le `getKey()` de l'un d'eux ; sinon reste `RoleBase.WEREWOLF`.

`recoverPower()` (GrimyWereWolf.java:87) : si la config `ConfigBase.TROLL_ROLE` n'est pas active, ajoute 1 `RoleBase.WEREWOLF` à la composition (compense le rejet futur).

Effets de masquage de rôle :
- `onDeathAnnounce` (GrimyWereWolf.java:176, priority HIGH) : à l'annonce de mort de la victime grimed, remplace le rôle annoncé par `grimedRoleKey`.
- `onSelfDeathAnnounce` (GrimyWereWolf.java:188, HIGHEST) : à la propre mort du Grimy, si `affectedPlayer` non vide, annonce le rôle de `affectedPlayer.get(0)` (swap d'identité) ; sinon annonce son vrai rôle.
- `onCompositionUpdate` (GrimyWereWolf.java:202, HIGHEST) : si la raison est `DEATH` et la clé correspond au vrai rôle de la victime grimed, annule l'event une seule fois (flag `hide`) pour cacher le décès dans la composition.
- `onFinalDeath` (GrimyWereWolf.java:160) : à la mort du Grimy lui-même, si `power` encore vrai → retire 1 WEREWOLF de la compo ; sinon retire 1 du rôle de la première affectedPlayer et broadcast un message d'actualisation.
- `onUpdate` (UpdatePlayerNameTagEvent, GrimyWereWolf.java:224) : pour les morts présents dans `affectedPlayer`, remplace le suffixe du nametag : si `SHOW_ROLE_TO_DEATH` actif, affiche `grimedRoleKey` ; si `SHOW_ROLE_CATEGORY_TO_DEATH` actif, affiche le camp WEREWOLF.

Aucun timer/IntValue/config imbriqué. Pas d'effet de potion passif au-delà de ceux de RoleWereWolf (Vision Nocturne + Force la nuit).

### Loup Garou Hurleur

- **Clé :** `howling_werewolf`
- **Classe :** `HowlingWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup natif, aura DARK. Aucun effet de potion passif supplémentaire.

Compteur interne `power` (int, init 0, max 3). Deux handlers liés au hurlement (système de hurlement général via `CommandWereWolfHowling` qui est une commande `roleKeys={}` disponible à tous les loups, déclenchant `WereWolfCanHowlingEvent` puis `WereWolfHowlingEvent` sur tous les joueurs vivants) :

1. `onRequestHowling` (WereWolfCanHowlingEvent, HowlingWerewolf.java:46) : si l'event concerne ce joueur et `power < 3`, met `canHowling=true` et incrémente `power`. Donc le HowlingWerewolf peut hurler jusqu'à 3 fois dans la partie. (Par défaut, sans rôle l'autorisant, `canHowling` reste faux.)

2. `onHowling` (WereWolfHowlingEvent, `ignoreCancelled=true`, HowlingWerewolf.java:61) : si ce joueur est la **cible** (`targetWW`) du hurlement d'un autre loup, et est ALIVE, il reçoit Régénération II (amplifier 1) pendant 7 secondes (140 ticks) via `addPotionModifier(REGENERATION, 7*20, 1, getKey())`, et un message l'avertissant du hurleur.

`recoverPower()` vide. Pas de commande dédiée propre (utilise la commande générale de hurlement). Pas de timer/config/IntValue.

### Loup Garou Mystique

- **Clé :** `mystical_werewolf`
- **Classe :** `MysticalWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Lumineuse · **Attribut :** Loup-Garou

Loup natif, aura LIGHT. Aucun effet de potion passif supplémentaire.

1. Mutisme dans le chat des loups : `onMessage` (WereWolfCanSpeakInChatEvent, priority LOWEST, MysticalWereWolf.java:53) : si l'event concerne ce joueur, met `canSpeak=false` et envoie un message "no_message". Le MysticalWerewolf ne peut **jamais** parler dans le chat des loups.

2. Révélation à la mort d'un loup : `onWereWolfDeath` (FinalDeathEvent, MysticalWereWolf.java:62) : si ce joueur est ALIVE, que le mort est un loup (`isWereWolf()`) et `isAbilityEnabled()` : sélectionne parmi les vivants un joueur dont le camp affiché n'est pas WEREWOLF, **ou** dont le camp affiché = camp réel ET n'est pas loup (donc un joueur dont l'affichage est trompeur ou un non-loup affiché correctement). Tire un joueur au hasard dans cette liste, fire `MysticalWerewolfRevelationEvent(self, cible)` (annoté `@SpyEvent`), puis envoie un message avec le nom de la cible et son `getDisplayRole()` (rôle affiché, pas réel). Si `isAbilityEnabled()` faux, message "ability_disabled".

`recoverPower()` vide. Pas de commande dédiée, pas de timer/config/IntValue.

### Loup Garou Perfide

- **Clé :** `mischievous_werewolf`
- **Classe :** `MischievousWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** IInvisible
- **Camp :** Loups-Garous · **Aura :** Neutre · **Attribut :** Loup-Garou

Loup natif, **aura NEUTRAL** (particularité : loup à aura neutre). Implémente `IInvisible` (champ `invisible`).

Pouvoir d'invisibilité nocturne conditionnel : `onCloseInvent` (InventoryCloseEvent, MischievousWereWolf.java:167) — déclenché à la fermeture d'un inventaire. Conditions : le joueur est ce rôle, état `GAME`, `Day.NIGHT`, ALIVE. Si les 4 slots d'armure (36=boots, 37=leggings, 38=chestplate, 39=helmet) sont tous vides ET pas déjà invisible ET `isAbilityEnabled()` : ajoute Invisibilité + Force (clé `this.getKey()`) via `PotionModifier.add`, `setInvisible(true)`, fire `InvisibleEvent(true)` et `UpdateNameTagEvent`. Si l'armure est remise alors qu'invisible : retire Invisibilité + Force, `setInvisible(false)`, fire `InvisibleEvent(false)`.

`onNight` (NightEvent, priority HIGHEST, MischievousWereWolf.java:51) : si ALIVE, **retire la Force du loup de base** (`PotionModifier.remove(STRENGTH, RoleBase.WEREWOLF, 0)`) — le bonus passif de RoleWereWolf est supprimé ; le Mischievous doit s'enlever l'armure pour récupérer sa propre Force (clé `this.getKey()`). Envoie un message "remove_armor".

`onDay` (DayEvent, MischievousWereWolf.java:107) : si invisible, retire Invisibilité + Force, `setInvisible(false)`, fire `InvisibleEvent(false)` + `UpdateNameTagEvent`. `onDayWillCome` (DayWillComeEvent) : simple message d'avertissement "bientôt le jour".

Détection d'autres invisibles (`second()`, MischievousWereWolf.java:63) : chaque seconde, la nuit, si ALIVE et invisible, parcourt tous les autres rôles `IInvisible` invisibles et joue un `Effect.STEP_SOUND` à leur location : `REDSTONE_BLOCK` si c'est un loup, `LAPIS_BLOCK` sinon. Effet sonore/visuel de repérage entendu uniquement par le joueur Mischievous.

`disableAbilitiesRole()` (ligne 214) : si invisible, retire Invisibilité + Force, setInvisible(false), fire events, et envoie un message "ability_disabled".

Pas de commande dédiée, pas de timer/config/IntValue. `recoverPower()` vide.

### Loup Garou Vengeur

- **Clé :** `avenger_werewolf`
- **Classe :** `AvengerWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** IAffectedPlayers
- **Camp :** Loups-Garous · **Aura :** Neutre · **Attribut :** Loup-Garou

Loup à aura NEUTRAL par défaut. Config `IntValueBase.AVENGER_WEREWOLF_DISTANCE` (défaut 10, meetUp 10, step 2). `auraDescriptionSpecialUseCase` définie. Aucune commande dédiée (mécanique automatique). Events dédiés : `RegisterAvengerListEvent` et `DeathAvengerListEvent` (tous deux extends SelectionEvent, cancellables).

`recoverPower()` (AvengerWereWolf.java:132-134) : `removePlayerMaxHealth(6)` → perd 3 cœurs (passe de 20 à 14 de vie max, soit 7 cœurs).

Détection automatique via `second()` (AvengerWereWolf.java:96-128) : scanne les joueurs en ligne vivants, filtre ceux dont le rôle est un loup (`isWereWolf()`), à distance < `AVENGER_WEREWOLF_DISTANCE` et non déjà dans `affectedPlayers`. Pour chaque nouveau loup détecté, fire `RegisterAvengerListEvent` (cancellable) puis l'ajoute à `affectedPlayers` et notifie l'Avenger. Note : `second()` ne vérifie pas `isAbilityEnabled()`, uniquement `isState(ALIVE)`.

Vengeance à la mort : `onFinalDeath(FinalDeathEvent)` (AvengerWereWolf.java:60-94) — si le mort est dans `affectedPlayers` et l'Avenger vivant : fire `DeathAvengerListEvent` (cancellable). Si non annulé : 
- Si `maxHealth >= 24` : `addPlayerMaxHealth(1)` (demi-cœur).
- Sinon : `addPlayerMaxHealth(2)` (1 cœur).
- Si `maxHealth >= 20` après gain : ajoute un `AuraModifier` DARK (l'Avenger devient DARK une fois sa vie max restaurée à 20+).

Progression : départ à 14 → +2 par mort de loup tracé → atteint 20 après 3 morts (aura DARK activée) → continue à +2 jusqu'à 24, puis +1 au-delà.

Hérite de RoleWereWolf (Vision Nocturne + Force la nuit).

### Loup Muselant

- **Clé :** `silencer_werewolf`
- **Classe :** `SilencerWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** IPower, IGuesser, IAffectedPlayers
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup natif, aura DARK. Pouvoir de devinette (`IGuesser`) limité à la catégorie `VILLAGER` (`getAvailableCategories()` retourne uniquement `Category.VILLAGER`).

Activation nocturne : `onNight` (NightEvent, SilencerWerewolf.java:137) : variable locale `failedGuess=false` (toujours fausse — code mort), si `isAbilityEnabled()` → `power=true` + message "power". Le pouvoir se réarme donc **chaque nuit**.

Mécanisme de guess via commande partagée `CommandGuess` (CommandGuess.java:21, `roleKeys = {MASTERMIND, SILENCER_WEREWOLF}`, cible ALIVE ou JUDGEMENT) : appelle `canGuess(target)`, puis ouvre `GuessInventory` filtré sur les catégories disponibles. À la sélection d'un rôle dans le GUI → `resolveGuess(key, target)` + fire `GuessEvent`.

`canGuess(target)` (SilencerWerewolf.java:86) : vérifie soi-même ALIVE, cible != soi, `power` vrai et `isAbilityEnabled()`. Garde supplémentaire : compte les vivants par camp ; si `nWW >= nVillagers` (loups >= villageois), refuse ("more_wolves").

`resolveGuess(key, target)` (ligne 117) : `power=false` (consommé pour la nuit). Si la clé devinée == clé réelle de la cible → succès : ajoute la cible à `affectedPlayers`, appelle `target.getRole().disableAbilities()` (désactive les aptitudes de la cible), messages de succès (Silencer) et de désactivation (cible). Si échec → message d'échec et `this.disableAbilities()` (le Silencer **se désactive lui-même** définitivement ; dès lors `isAbilityEnabled()`=false et `onNight` ne réarme plus le pouvoir).

`recoverPower()` vide. Pas de timer/config/IntValue. Aucun event dédié propre. La description inclut `setCommand` et `setPower` (effets de loup standards).

### Loup Sanguinaire

- **Clé :** `bloodthirsty_werewolf`
- **Classe :** `BloodthirstyWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup à aura DARK par défaut. 4 IntValues : `BLOODTHIRSTY_SPEED` (défaut 15%, step 5), `BLOODTHIRSTY_MAX_LIFE_DETECT` (défaut 80%, step 5), `BLOODTHIRSTY_MAX_LIFE_DETECT_DAY` (défaut 6, meetUp 2, step 1), `BLOODTHIRSTY_TAKE_DAMAGE_DAY` (défaut 8, meetUp 3, step 1). 1 Timer : `BLOODTHIRSTY_COOL_DOWN` (défaut 15 min, meetUp 5 min, step 30). `BASE_SPEED = 0.2f` (BloodthirstyWereWolf.java:80).

Pouvoir de traque via commande `/ww <joueur>` (`CommandBloodthirsty.java`, `requiredPower=true`, 1 arg joueur vivant excluant self). La commande fire `BloodthirstyWerewolfHuntDownEvent` (@TellableStoryEvent, cancellable). Si non annulé, appelle `addAffectedPlayer(target)` (BloodthirstyWereWolf.java:130-137) qui : ajoute la cible, `setPower(false)`, stocke `traquedPlayerWW`, envoie confirmation.

Récupération du pouvoir : `recoverPower()` (BloodthirstyWereWolf.java:122-127) — si `!hasPower()`, met `havePower=true` et notifie. Appelé à l'init (havePower démarre à false → passe à true) et après cooldown.

Suivi directionnel via `second()` (BloodthirstyWereWolf.java:169-194) et `onActionBarRequest(ActionBarEvent)` (BloodthirstyWereWolf.java:298-324) : si une cible est traquée, calcule la direction via `Utils.updateArrow()` et affiche le nom de la cible + flèche directionnelle dans l'action bar. Si la flèche est `"↑"` (cible devant), ajoute un bonus de walk speed = `BASE_SPEED * BLOODTHIRSTY_SPEED / 100` (défaut +0.03). Sinon retire le bonus.

Mort de la cible : `onPlayerFinalDeath(FinalDeathEvent, NORMAL)` (BloodthirstyWereWolf.java:196-215) — si le mort est la cible traquée : notifie, planifie `recoverPower()` après `BLOODTHIRSTY_COOL_DOWN` secondes (délai en ticks × 20), clears `traquedPlayerWW`.

Obligation de combat : `onEntityDamageByEntity(EntityDamageByEntityEvent, NORMAL)` (BloodthirstyWereWolf.java:217-234) — si le Bloodthirsty inflige des dégâts à un joueur non-loup, met `haveDealDamage=true`.

Vérification des dégâts : `checkDamage(DayEvent, NORMAL)` (BloodthirstyWereWolf.java:273-296) — un jour sur deux (même parité que `BLOODTHIRSTY_TAKE_DAMAGE_DAY`), à partir du jour configuré : si `haveDealDamage` → message positif. Sinon si vivant → retire 8 points de vie (ou descend à 0.5 si vie < 8) et message négatif. Reset `haveDealDamage=false` à la fin.

Détection de basse vie : `checkList(DayEvent, NORMAL)` (BloodthirstyWereWolf.java:236-271) — à partir de `BLOODTHIRSTY_MAX_LIFE_DETECT_DAY`, un jour sur deux (même parité) : liste les joueurs vivants dont le ratio `health/maxHealth` < `BLOODTHIRSTY_MAX_LIFE_DETECT/100` (défaut 80%). Fire `BloodthirstyWerewolfLifeDetectionEvent` (cancellable) par joueur, puis `BloodthirstyWereWolfLowLifeListDisplayEvent`. Envoie la liste au Bloodthirsty.

Hérite de RoleWereWolf (Vision Nocturne + Force la nuit).

### Loup Ténébreux

- **Clé :** `tenebrous_werewolf`
- **Classe :** `TenebrousWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Loups-Garous · **Aura :** Neutre · **Attribut :** Loup-Garou

Loup natif. `auraDescriptionSpecialUseCase = "werewolf.roles.tenebrous_werewolf.aura"` (cas d'aura spécial). Aucun `defaultAura` explicite (donneur d'aura dynamique).

Timers/IntValues imbriqués :
- `@Timer(key = TimerBase.WEREWOLF_TENEBROUS_DURATION, defaultValue = 30, meetUpValue = 20)` — durée d'obscurité en secondes.
- `@IntValue(key = IntValueBase.TENEBROUS_WEREWOLF_DISTANCE, defaultValue = 50, meetUpValue = 50, step = 5, item = UniversalMaterial.BLACK_WOOL)` — portée en blocks.

Pouvoir (one-shot, `power` init true) via commande dédiée `CommandTenebrous` (CommandTenebrous.java:25, `requiredPower=true`, `argNumbers=0`, pas de cible) : doit être la **nuit** (`Day.DAY` → refus). Récupère tous les joueurs vivants non-loups à moins de `TENEBROUS_WEREWOLF_DISTANCE` blocks. Fire `TenebrousEvent(self, affectedPlayers)` (Cancellable, `@TellableStoryEvent`, `@StatisticsEvent("werewolf.tenebrous")`, liste modifiable via `removeAffectedPlayer`). Si non annulé : `setPower(false)`, pour chaque cible → `addAffectedPlayer(c)`, ajoute Blindness I (amplifier 1) pendant `WEREWOLF_TENEBROUS_DURATION * 20` ticks (clé du rôle), message "darkness" à la cible. Le Tenebrous gagne un modificateur d'aura `Aura.DARK` (amplifier 1, non display). Tous les loups vivants reçoivent un message "darkness_wolves".

Retrait de cécité : `onPlayerDamage` (EntityDamageByEntityEvent, TenebrousWerewolf.java:92) : si la cible est dans `affectedPlayers` et le damager est un loup (`isWereWolf()`), retire la Blindness (amplifier 1, clé du rôle) et retire la cible de `affectedPlayers`. Donc un loup qui frappe une victime aveuglée la libère.

`recoverPower()` vide. Pas d'effet de potion passif. La description affiche distance et durée formatées.

### Loup-Garou Alerteur

- **Clé :** `alerter_werewolf`
- **Classe :** `AlerterWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Loups-Garous · **Aura :** Neutre · **Attribut :** Loup-Garou

Loup à aura NEUTRAL par défaut. Config `IntValueBase.ALERTER_DISTANCE` (défaut 20, meetUp 20, step 5). Constante `PROXIMITY_REQUIRED = 180` secondes (AlerterWereWolf.java:36).

Pouvoir principal via commande `/ww` (`CommandAlerter.java`, `requiredPower=true`, 1 arg joueur vivant excluant self et reject si déjà affecté). La commande (`CommandAlerter.java:27-36`) appelle `setPower(false)`, `clearAffectedPlayer()`, `addAffectedPlayer(target)`, `resetProximity()`. Le pouvoir est donc consommé immédiatement à la sélection de la cible ; une seule cible par jour.

Régénération du pouvoir : `onDay(DayEvent)` (AlerterWereWolf.java:60-66) remet `power=true` et `proximitySeconds=0` à chaque jour, si abilityEnabled et ALIVE. `recoverPower()` met `power=false` à l'init.

Suivi automatique via `second()` (AlerterWereWolf.java:68-86) : si une cible est définie, même monde, et distance ≤ `ALERTER_DISTANCE`, incrémente `proximitySeconds` chaque seconde (compteur cumulatif, jamais décrémenté). Quand `proximitySeconds >= 180` (3 minutes cumulées de proximité), déclenche `alert()`, puis `setPower(false)`, `clearAffectedPlayer()`, reset proximity.

Méthode `alert()` (AlerterWereWolf.java:88-123) : si la cible est elle-même un loup (`isWereWolf()`), ne fait rien (retour silencieux). Sinon, collecte tous les loups vivants (hors self), en choisit un au hasard, lui envoie un message contenant le nom de l'Alerter et le rôle affiché de la cible. Notifie l'Alerter du nom du loup alerté, et notifie le loup alerté qu'il a été contacté. Si aucun loup vivant n'est disponible, envoie un message d'échec à l'Alerter.

Pas d'effets passifs propres (hérite de RoleWereWolf : Vision Nocturne + Force la nuit). Pas d'event dédié.

### Loup-Garou Amnésique

- **Clé :** `amnesiac_werewolf`
- **Classe :** `AmnesicWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** IPower
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup à aura DARK par défaut. Aucune config/timer. Constantes : `PROXIMITY_RANGE = 10`, `HOWLING_RANGE = 50`, `REVEAL_MIN = 4200` (70 min), `REVEAL_MAX = 5400` (90 min) (AmnesicWerewolf.java:30-33).

`recoverPower()` (AmnesicWerewolf.java:44-47) : tire `revealTime` aléatoirement entre 70 et 90 minutes de jeu, et s'ajoute lui-même à `knownWolves`.

Découverte passive par proximité : `second()` (AmnesicWerewolf.java:60-70) — chaque seconde, ajoute à `knownWolves` tout loup vivant (hors self) situé à moins de 10 blocs. L'Amnesic ne connaît donc les autres loups que s'il s'en approche physiquement.

Visibilité dans la liste des loups : `onAppearInWereWolfList(AppearInWereWolfListEvent)` (AmnesicWerewolf.java:72-84) gère deux cas :
1. Si l'Amnesic est le viewer : un autre loup n'apparaît dans sa liste que s'il est dans `knownWolves`.
2. Si l'Amnesic est la cible regardée par un autre loup ET que `game.getTimer() < revealTime` : `setAppear(false)` — l'Amnesic est caché de la liste des autres loups jusqu'à 70-90 min de jeu.

Pouvoir actif unique via commande `/ww` (`CommandAmnesicHowl.java`, `requiredPower=true`, 0 arg) → appelle `howl()` (AmnesicWerewolf.java:86-107) : compte les loups vivants dans un rayon de 50 blocs, joue le son `WOLF_HOWL` à tous les joueurs vivants dans ce rayon, leur envoie un message d'avertissement, puis `setPower(false)` (pouvoir consommé définitivement, jamais réactivé dans la classe). Envoie à l'Amnesic le nombre de loups proches.

Hérite de RoleWereWolf (Vision Nocturne + Force la nuit).

### Loup-Garou Déloyal

- **Clé :** `disloyal_werewolf`
- **Classe :** `DisloyalWerewolf` (extends `RoleWereWolf`) · **Marqueurs :** ILimitedUse
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup à aura DARK par défaut. Aucune config/timer. Aucun event dédié consommé directement par la classe (mais la commande fire `DisloyalHarmEvent`). Compteur `use` démarre à 0.

Commande dédiée `/ww` sans arg (`CommandDisloyalNuire.java`, `requiredLimitedUse=2`, 0 arg) → limite à 2 utilisations totales. La commande (`CommandDisloyalNuire.java:27-63`) :
- Constante `HARM_DISTANCE = 30` blocs.
- Collecte tous les joueurs vivants (hors self, même monde) à moins de 30 blocs dans un `Set`.
- Fire `DisloyalHarmEvent` (@TellableStoryEvent, cancellable, expose `playerWW` et `targets`).
- Si annulé → message d'erreur.
- Sinon, pour chaque cible : `modifyHonor(-1)` (perte de 1 point d'honneur). Si la cible est un loup, lui envoie un message d'avertissement avec le nom du Disloyal.
- `setUse(getUse() + 1)`.
- Notifie le Disloyal du nombre de joueurs affectés.

`recoverPower()` vide. `getDescription()` et `getUse()`/`setUse()` simples. Hérite de RoleWereWolf (Vision Nocturne + Force la nuit). Le rôle est minimaliste : tout le comportement actif est dans la commande.

### Loup-Garou Faussaire

- **Clé :** `counterfeiter_werewolf`
- **Classe :** `CounterfeiterWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Loups-Garous · **Aura :** Neutre · **Attribut :** Loup-Garou

Loup à aura NEUTRAL par défaut. `auraDescriptionSpecialUseCase` définie. Aucune config/timer. Constante `TWO_HEARTS = 4` (unités de vie). Events dédiés : `CounterfeiterFalsifyEvent` (@TellableStoryEvent, @StatisticsEvent, extends SelectionEvent, cancellable, expose `facadeRole`/`facadeCamp`/`facadeAura`).

Deux commandes dédiées :
1. Falsification `/ww <joueur>` (`CommandCounterfeiterFalsify.java`, 1 arg, joueur vivant excluant self, `rejectIfAlreadyAffected=true`) → appelle `falsify(target)` (CounterfeiterWereWolf.java:76-134) :
   - Si cible déjà falsifiée → rejet.
   - Détermine `targetIsWolf` via `isWereWolf()`. Aura de façade : `LIGHT` si la cible est loup, `DARK` sinon.
   - Construit un pool de rôles parmi les vivants (hors cible et self) : si cible est loup → rôles non-loups ; si cible n'est pas loup → rôles loups ou solitaires (`isSolitary()`).
   - Si pool vide → rejet.
   - Tire aléatoirement un rôle de façade (`facadeRole`, `facadeCamp`).
   - Fire `CounterfeiterFalsifyEvent` (cancellable).
   - Si une cible était déjà falsifiée, la restaure d'abord (`restoreFacade`).
   - Sauvegarde le `displayRole`/`displayCamp` actuel de la cible, puis les écrase par ceux de la façade. Ajoute un `AuraModifier` avec l'aura de façade (clé `counterfeiter_werewolf_facade`).
   - Fire `UpdateNameTagEvent` pour rafraîchir le nametag.
   - Notifie le Counterfeiter. Une seule cible falsifiée à la fois, mais peut changer de cible librement (pas de `requiredPower`).

2. Hurlement `/ww` sans arg (`CommandCounterfeiterHowl.java`, `requiredPower=true`, 0 arg) → compte les loups vivants dans un rayon de 50 blocs, joue `WOLF_HOWL` à tous les joueurs dans ce rayon, envoie un avertissement, `setPower(false)` (pouvoir consommé définitivement, jamais réactivé).

`restoreFacade()` (CounterfeiterWereWolf.java:136-144) : restaure le `displayRole`/`displayCamp` d'origine et retire l'`AuraModifier` de façade, puis fire `UpdateNameTagEvent`.

`onDay(DayEvent)` (CounterfeiterWereWolf.java:146-153) : si vivant et une cible est falsifiée → `restoreFacade` + clear (la falsification dure un cycle jour/nuit).

`onFinalDeath(FinalDeathEvent)` (CounterfeiterWereWolf.java:155-175) : si le Counterfeiter meurt → restaure la façade. Si la cible falsifiée meurt et que le Counterfeiter est vivant → `addPlayerMaxHealth(-4)` (perte de 2 cœurs) + message.

`onAnnouncementDeath(AnnouncementDeathEvent, HIGHEST, ignoreCancelled=true)` (CounterfeiterWereWolf.java:177-182) : si la cible falsifiée meurt, l'annonce de mort affiche le rôle falsifié (`getDisplayRole()`) au lieu du vrai rôle.

`recoverPower()` vide. `power` démarre à true. Hérite de RoleWereWolf (Vision Nocturne + Force la nuit).

### Loup-Garou Mesquin

- **Clé :** `mesquin_werewolf`
- **Classe :** `MesquinWereWolf` (extends `RoleWereWolf`) · **Marqueurs :** ILimitedUse
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup natif, aura DARK. Aucun effet de potion passif supplémentaire. Aucune commande dédiée propre, aucun event dédié, aucun @EventHandler dans la classe.

Pouvoir passif/actif via `ILimitedUse` : `MAX_USES = 3`, compteur `use`. Méthode `rigUrn(Location)` (MesquinWereWolf.java:44) : piège une urne (location) si `use < 3`, location non null et non déjà piégée ; incrémente `use`. `isUrnRigged(Location)` vérifie l'appartenance. `isAnyUrnRiggedAt(game, location)` (statique, ligne 63) parcourt tous les joueurs ayant un rôle `MesquinWereWolf` et teste si l'un a piégé cette location.

Activation concrète (hors classe) :
- `BuildingListener.onJukeboxInteract` (BuildingListener.java:60) : quand un Mesquin (ALIVE, en GAME, config `URN_VOTE` active) fait clic-droit sur un jukebox = bureau de vote : si `use < MAX_USES`, appelle `rigUrn(office.getUrnLocation())` au lieu d'ouvrir le menu de vote. L'urne est piégée et le Mesquin ne peut pas voter à ce bureau (return early). Messages `rig_perform` / `rig_already`.
- `UrnVoteGuiHelper.recordVoteAgainst` (UrnVoteGuiHelper.java:39) : lorsqu'un joueur vote contre une cible à une urne, si `HONOR` actif et l'urne est **non** piégée, +1 honneur. Si l'urne est piégée par un Mesquin, le votant ne gagne pas d'honneur (le vote compte quand même). Le vote blanc (`recordBlank`) inflige toujours -1 honneur quelle que soit l'urne.

`recoverPower()` vide. Pas de timer/config/IntValue imbriqué.

### Vilain Petit Loup

- **Clé :** `naughty_little_wolf`
- **Classe :** `NaughtyLittleWolf` (extends `RoleWereWolf`) · **Marqueurs :** aucune
- **Camp :** Loups-Garous · **Aura :** Obscure · **Attribut :** Loup-Garou

Loup natif, aura DARK. Rôle purement passif, aucune commande, aucun event dédié, aucun @EventHandler dédié autre que les cycles jour/nuit.

Effet passif : Vitesse la nuit uniquement.
- `recoverPotionEffect()` (NaughtyLittleWolf.java:48) : si `Day.NIGHT`, ajoute Speed (amplifier 0, durée infinie par défaut, clé `this.getKey()`).
- `onNight` (NightEvent, priority HIGH, ligne 54) : si ALIVE et `isAbilityEnabled()`, ajoute Speed.
- `onDay` (DayEvent, priority HIGH, ligne 68) : retire Speed (clé `this.getKey()`).
- `disableAbilitiesRole()` (ligne 73) : si ALIVE, retire Speed.

`recoverPower()` vide. Pas de timer/config/IntValue. La description n'inclut que `setEffects` (pas de `setDescription`).

## Camp : Neutres

### Ange

- **Clé :** `angel`
- **Classe :** `Angel` (extends `RoleNeutral`) · **Marqueurs :** IAffectedPlayers, ILimitedUse
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Ange neutre devant choisir, via deux commandes `/ww` clickables (`werewolf.roles.angel.command_1` → GuardianAngel, `command_2` → FallenAngel, voir Angel.java:233-268), une forme avant la fin du timer `TimerBase.ANGEL_DURATION` (240s par défaut et en meetUp, `decrementAfterRole=true`, onZero = `AutoAngelEvent`, Angel.java:62-67). Config imbriquée `ConfigBase.SWEET_ANGEL`.

`recoverPower()` (Angel.java:271-279) accorde +4 cœurs permanents via `heartAndMessageTargetManagement()` (extraHearts=4) et affiche le texte de choix tant que `choice == ANGEL`.

Timer écoulé sans choix → `onAutoAngel` (Angel.java:282-329) : choix aléatoire 50/50 (FALLEN_ANGEL ou GUARDIAN_ANGEL), puis sélection automatique d'une cible via `Utils.autoSelect` (ajout à `affectedPlayer`). Si FallenAngel choisi et qu'il fait nuit → Resistance. Si GuardianAngel → +6 cœurs supplémentaires. Fire `AngelChoiceEvent` puis `AngelTargetEvent`, `checkVictory()`.

Comportement dépendant de la forme :
- FALLEN_ANGEL : révèle le nom et le rôle de la cible. `onNight` (HIGHEST, Angel.java:506) ajoute Resistance (si `isAbilityEnabled()`), `onDay` (LOWEST, Angel.java:498) la retire. Si la cible meurt et que l'Ange est le tueur (`getLastKiller` ou `getLastStrikers`) → fire `FallenAngelTargetDeathEvent` + +6 cœurs bonus (Angel.java:360-369).
- GUARDIAN_ANGEL : +6 cœurs à la révélation. `onActionBarRequest` (Angel.java:385) affiche une flèche directionnelle vers le protégé vivant. `onLover`/`AroundLoverEvent` (Angel.java:429) rattache le protégé au cercle de couple de l'Ange. Si le protégé meurt → `onFinalDeath` (Angel.java:349) retire les 6 cœurs, fire `AngelTargetDeathEvent` (Angel.java:371-381).

Commande dédiée `werewolf.roles.guardian_angel.command` (CommandAngelRegen, `requiredLimitedUse=3`, rôles ANGEL+GUARDIAN_ANGEL) : jusqu'à 3 utilisations, donne Régénération (400 ticks) au protégé connecté (CommandAngelRegen.java:56-77).

`isNeutral()` surchargé (Angel.java:416-422) : retourne `false` (donc bascule en camp VILLAGER) si `SWEET_ANGEL` actif ET forme GUARDIAN_ANGEL ET protégé mort. `getAura()` = LIGHT.

Condition de victoire perso `onDetectVictoryWithProtege` (WinConditionsCheckEvent, Angel.java:451-496) : construit un "cercle" (protégé + tous les Angels/GuardianAngels liés à ce protégé) ; si `game.getPlayersCount() == list.size()` → `event.setWin()`, `setVictoryTeam(RoleBase.GUARDIAN_ANGEL)`.

`disableAbilitiesRole()` retire la Resistance.

### Ange Déchu

- **Clé :** `fallen_angel`
- **Classe :** `FallenAngel` (extends `Angel (extends RoleNeutral transitivement)`) · **Marqueurs :** aucune propre (hérite IAffectedPlayers, ILimitedUse via Angel)
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Sous-classe d'`Angel` qui démarre immédiatement en forme `AngelForm.FALLEN_ANGEL` : le constructeur (FallenAngel.java:19-24) appelle `setChoice(FALLEN_ANGEL)` et fire `AngelChoiceEvent`. Aucun `timers`/`configValues`/modificateur sharpness-protection propre (contrairement à Angel).

Hérite de tout le comportement de `Angel` :
- `recoverPower()` hérité : +4 cœurs (extraHearts de base) + message de révélation de cible ( FallenAngel.java n'a pas de cible tant que non sélectionnée).
- Les handlers `onAutoAngel`, `onDay`, `onNight`, `onFinalDeath`, `onLover`, `onDetectVictoryWithProtege`, `disableAbilitiesRole` sont hérités. Comme `choice != ANGEL`, `onAutoAngel` saute le bloc de choix aléatoire mais exécute quand même la sélection automatique de cible (`Utils.autoSelect`) à la fin du timer `ANGEL_DURATION` global, puis révèle le rôle de la cible (Angel.java:308-322).
- `onNight` (HIGHEST) ajoute Resistance la nuit, `onDay` (LOWEST) la retire.
- Si la cible meurt et que le FallenAngel est le tueur → `FallenAngelTargetDeathEvent` + +6 cœurs (cumulés aux +4 de base).

### Ange Gardien

- **Clé :** `guardian_angel`
- **Classe :** `GuardianAngel` (extends `Angel (extends RoleNeutral transitivement)`) · **Marqueurs :** aucune propre (hérite IAffectedPlayers, ILimitedUse via Angel)
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Sous-classe d'`Angel` démarrant immédiatement en forme `AngelForm.GUARDIAN_ANGEL` : le constructeur (GuardianAngel.java:20-25) appelle `setChoice(GUARDIAN_ANGEL)` et fire `AngelChoiceEvent`. Aucun timer/config/modificateur propre.

Hérite de tout le comportement de `Angel` en forme gardien :
- `recoverPower()` hérité : +4 cœurs de base.
- `onAutoAngel` hérité : saute le choix (choice != ANGEL), sélection auto de cible à la fin du timer `ANGEL_DURATION`, puis +6 cœurs supplémentaires (Angel.java:317) + révélation du rôle du protégé.
- `onActionBarRequest` : flèche directionnelle vers le protégé vivant.
- `onLover`/AroundLoverEvent : rattache le protégé au cercle de couple.
- `onFinalDeath` : si le protégé meurt → -6 cœurs.
- `onDetectVictoryWithProtege` : victoire si seuls l'Ange et son protégé (et cercle) survivent, `setVictoryTeam(RoleBase.GUARDIAN_ANGEL)`.
- `isNeutral()` hérité : `false` si `SWEET_ANGEL` actif ET protégé mort (bascule camp VILLAGER).
- Commande `werewolf.roles.guardian_angel.command` (CommandAngelRegen) : jusqu'à 3 Régénérations du protégé.
- Pas de Resistance (réservée à FALLEN_ANGEL).

### Assassin

- **Clé :** `assassin`
- **Classe :** `Assassin` (extends `RoleNeutral`) · **Marqueurs :** ILimitedUse
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

Modificateurs `@Role` : sharpness fer/diamant +1, protection fer/diamant +1, powerModifier +1 ; `defaultAura = DARK`.

Effet passif : Force le jour uniquement. `recoverPotionEffect()` (Assassin.java:164-171) ajoute Strength si `!isDay(NIGHT)` et `isAbilityEnabled()`. `onDay` (Assassin.java:74-82) ajoute Strength, `onNight` (LOWEST, Assassin.java:66-72) la retire. `disableAbilitiesRole()` retire la Strength.

Pouvoir limité à 2 usages (`requiredLimitedUse=2`, `ILimitedUse`). Quand l'Assassin tue un joueur (`onSecondDeathEvent`, HIGH, ignoreCancelled, Assassin.java:84-118) : si `use < 2`, `isAbilityEnabled()`, victime ≠ soi, et `victim.getLastKiller() == this` → envoie un message clickable `/ww werewolf.roles.assassin.command <uuid>`. La commande (CommandAssassin.java, cible en état JUDGEMENT) vérifie en plus `game.getTimer() - victim.getDeathTime() <= 7`, puis appelle `assassinate(victim)` (Assassin.java:120-136) : fire `AssassinEvent`, si non annulé → `use++`, ajoute la victime à `assassinated`, message confirmant (rôle révélé à l'assassin).

Effet de l'assassinat : `onAnnouncementDeath` (HIGHEST, Assassin.java:138-144) annule l'annonce publique de mort des joueurs présents dans `assassinated` → le rôle de la victime n'est pas révélé à la mort.

`recoverPower()` vide. Pas de condition de victoire perso.

### Barbare

- **Clé :** `barbarian`
- **Classe :** `Barbarian` (extends `RoleNeutral`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

`defaultAura = NEUTRAL`. IntValue `IntValueBase.BARBARIAN_DISTANCE` (25 par défaut/meetUp, step 5,GRAY_WOOL).

Pouvoir passif de combat (`onPlayerDamageByPlayer`, Barbarian.java:140-182) : si `isAbilityEnabled()`, état GAME, et le barbare (corps à corps ou flèche) frappe un joueur non encore dans `damagedPlayers` → dégâts ×2 et ajout à `damagedPlayers` (une seule fois par victime).

Pouvoir actif unique (`IPower`, `requiredPower=true`). Quand un joueur meurt (`onMaskedDeath`/FirstDeathEvent, Barbarian.java:78-111) : si `hasPower()`, vivant, même monde, et `deathLocation.distance(barbare) <= BARBARIAN_DISTANCE` → message clickable `/ww werewolf.roles.barbarian.command <uuid>`. La commande (CommandBarbarian.java, cible en JUDGEMENT) : `setPower(false)`, fire `BarbarianEvent`, `addAffectedPlayer(victim)`. Effets ensuite : `onCompositionUpdate` (HIGH, Barbarian.java:113-118) annule l'update de composition pour ce joueur ; `onMaskedDeathAnnouncement` (HIGH, Barbarian.java:120-138) force le format `werewolf.announcement.death_message` (annonce générique, rôle masqué) tant que le barbare est vivant.

`recoverPower()` vide. Pas de condition de victoire perso.

### Charmeuse

- **Clé :** `charmer`
- **Classe :** `Charmer` (extends `RoleNeutral`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Timer `TimerBase.CHARMER_COUNTDOWN` (6000s par défaut / 1200s en meetUp, `decrementAfterRole=true`, Charmer.java:45-47). `incompatibleRoles = RoleBase.SCAMMER`. sharpness fer/diamant +1.

Pouvoir actif (`IPower`, `/ww werewolf.roles.charmer.command <player>`, `requiredPower=true`, cible ALIVE excludeSelf, CommandCharmer.java) : `setPower(false)`, fire `CharmerEvent`, `clearAffectedPlayer` + `addAffectedPlayer`, puis crée un `FakeLoverCharmer` (couple factice charmer+cible). Le `FakeLoverCharmer` annonce au charmer le nom de sa cible, mais à la cible il se comporte comme un lover normal (FakeLoverCharmer.java:25-38) — la cible croit être en couple. Le `FakeLover`/`FakeLoverCharmer` n'implémente aucun suicide lié (FakeLover.java) : pas de mort en chaîne.

Choix automatique si non fait avant la fin du timer lover (`onLoverDurationEnd`/RevealLoversEvent, Charmer.java:177-208) : si `playerWW == null` → `setPower(false)`, sélection aléatoire d'une cible vivante, création du FakeLoverCharmer, `announceLovers()`. Sinon ré-annonce.

Effets passifs :
- `onLoverDead` (LoverDeathEvent, Charmer.java:115-140) : si un vrai `LoverBase.LOVER` ou `AMNESIAC_LOVER` meurt → fire `CharmerGetEffectDeathEvent` ; si non annulé et `isAbilityEnabled()` → Absorption (niveau 4, durée `Integer.MAX_VALUE`) + Speed permanents.
- `onCharmedDeath` (FinalDeathEvent, Charmer.java:92-113) : si le charmed meurt → fire `CharmedDeathEvent(isBeforeCountDown = CHARMER_COUNTDOWN > 0)` ; si avant la fin du compte à rebours → -6 cœurs au charmer.
- `onCharmerDeath` (FinalDeathEvent, Charmer.java:142-152) : révèle au charmed l'identité du charmer.

`recoverPower()` vide. Pas de condition de victoire perso.

### Escroc

- **Clé :** `scammer`
- **Classe :** `Scammer` (extends `RoleNeutral`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Hybride

Rôle `attribute=HYBRID`, `incompatibleRoles={CHARMER}`. `@Timer SCAMMER_DELAY` (9 def, 3 meetUp), `@IntValue SCAMMER_DISTANCE` (20, step 2).

Pouvoir automatique — `second()` (Scammer.java:79) : toutes les `SCAMMER_DELAY` secondes, si `hasPower()` et vivant, pour chaque joueur vivant ( Bukkit online ) à moins de `SCAMMER_DISTANCE`, incrémente un compteur dans `affectedPlayer` (Map). À 100 → déclenche `ScamEvent`.

`onScam` (Scammer.java:117) : si non annulé → `setPower(false)`, clear affected. **Échange permanent des rôles** : le rôle de la cible est donné au Scammer (`targetRole.setPlayerWW(this.getPlayerWW())`, `getPlayerWW().setRole(targetRole)`), `SwapEvent`. La cible devient `WereWolf` (si elle était loup/neutre-solitaire) ou `Villager`. Gestion de l'infection (`setInfected`) et de la solitude (si Scammer solitary et cible non → bonus de PV jusqu'à 30 ; si cible solitary transféré). `recoverPower()`/`recoverPotionEffects()` appelés sur le rôle volé. La cible reçoit `addDeathRole`. Le Scammer apprend le rôle volé.

Donc pouvoir unique : le Scammer vole définitivement le rôle d'une cible après ~100 secondes de proximité cumulée, la cible devient Villageois/Loup.

`disableAbilitiesRole`/`enableAbilitiesRole` (Scammer.java:201,206) : toggles `setPower`. Pas de commande dédiée (pouvoir passif/automatique). Pas de `isWin` custom.

### Feu Follet

- **Clé :** `will_o_the_wisp`
- **Classe :** `WillOTheWisp` (extends `RoleNeutral`) · **Marqueurs :** IInvisible, ILimitedUse, IPower
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Rôle `sharpnessIron/DiamondModifier=1`. `@IntValue WILL_O_THE_WISP_DISTANCE` (50, step 5). Timers : `WILL_O_THE_WISP_DURATION_INCENDIARY_MADNESS` (45s), `WILL_O_THE_WISP_COOLDOWN_INCENDIARY_MADNESS` (600s def, 300s meetUp), `WILL_O_THE_WISP_COOLDOWN_TP` (600s def, 300s meetUp).

`second()` (WillOTheWisp.java:104) : si invisible, joue un `STEP_SOUND` (REDSTONE_BLOCK si l'autre invisible est loup, LAPIS_BLOCK sinon) à la position de chaque autre rôle `IInvisible` invisible → révèle les invisibles au WillOTheWisp.

`onNight` (WillOTheWisp.java:146) : parmi les joueurs vivants à moins de `WILL_O_THE_WISP_DISTANCE`, en choisit un au hasard → `WillOTheWispRecoverRoleEvent` → révèle son `displayRole` au WillOTheWisp.

Commande `/ww` (CommandWillOTheWisp, `requiredPower`, 0 arg, « folie incendiaire ») : `setPower(false)`, cooldown `COOLDOWN_INCENDIARY_MADNESS`. `WillOTheWispIncendiaryMadnessEvent`. Accorde Speed pendant `DURATION_INCENDIARY_MADNESS` (clé `INCENDIARY_MADNESS`).

`onInteraction` (EntityDamageByEntityEvent, WillOTheWisp.java:187) : si le WillOTheWisp a le modifier `INCENDIARY_MADNESS` et frappe un joueur → `setFireTicks(8s)`, tracke 8s.
`onPlayerDamage` (HIGH, WillOTheWisp.java:219) : si un dégât `FIRE_TICK` est annulé (resistance/protection feu) sur un joueur tracké → le ré-active (force le feu).

`onInteractWithFeather` (PlayerInteractEvent, WillOTheWisp.java:235) : clic avec une plume. Si `canBeTeleported` → cooldown `COOLDOWN_TP`, `WillOTheWispTeleportEvent`, `setUse(+1)` (ILimitedUse). Téléporte dans la direction du regard sur `WILL_O_THE_WISP_DISTANCE`, Y = highest block + 10. Applique Wither 400 ticks (20s).

`onCloseInvent` (InventoryCloseEvent, WillOTheWisp.java:282) : si slots d'armure (36-39) vides → devient invisible : Invisibility + Absorption II (permanents). Si infecté et nuit → retire le Strength loup. `setInvisible(true)`, `InvisibleEvent`. Si armure rééquipée → redevient visible, restore le Strength loup si infecté et nuit.

`disableAbilitiesRole` (WillOTheWisp.java:351) : si invisible, retire Strength/Invisibility/Absorption, `setInvisible(false)`.

### Imitateur

- **Clé :** `imitator`
- **Classe :** `Imitator` (extends `RoleNeutral`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

`defaultAura = NEUTRAL`. sharpness fer/diamant +1. `auraDescriptionSpecialUseCase` personnalisé.

Effet passif : Strength permanente tant que `power == true`. `recoverPotionEffect()` (Imitator.java:205-214) ajoute Strength si `power` et `isAbilityEnabled()`. `disableAbilitiesRole()` la retire.

Pouvoir unique (`IPower`, 1 usage, automatique sur kill). `onFirstDeathEvent` (LOWEST, ignoreCancelled, Imitator.java:112-134) : si `victim.getLastKiller() == imitator` et `hasPower()` → `event.setCancelled(true)` (la mort est suspendue), puis après 7 secondes (`scheduleSyncDelayedTask 7*20`) : si toujours vivant et `hasPower()` → `imitatorRecoverRole(victim)` ; sinon re-fire `SecondDeathEvent` (la victime meurt finalement).

`imitatorRecoverRole` (Imitator.java:137-198) : `setPower(false)`, clone le rôle de la victime (`role.publicClone()`), `HandlerList.unregisterAll(this)`, `setRole(roleClone)`, `BukkitUtils.registerListener(roleClone)`. Gère l'infection (propage `setInfected`) ou, si le rôle cloné est un loup natif, fire `NewWereWolfEvent`. `roleClone.setTransformedToNeutral(true)` (le rôle volé est forcé en camp NEUTRAL). `addDeathRole(IMITATOR)`. `clearPotionEffects(key)`, fire `StealEvent`. Si `isAbilityEnabled()==false` → `disableAbilities()`. `removeTemporaryAuras()`, `recoverPower()`/`recoverPotionEffects()` du clone. Gère `solitary` (si la victime était solitary, +cœurs jusqu'à 30 max 8). Swap des lovers (`lover.swap`). Fire `UpdateNameTagEvent`, puis `game.death(victim)` (la victime meurt réellement après le vol).

`onPlayerDeath` (PlayerDeathEvent, Imitator.java:96-110) : si l'imitator est le killer → Speed pendant 1200 ticks.

Pas de commande dédiée. `recoverPower()` vide. Pas de condition de victoire perso.

### Joueur de Flûte

- **Clé :** `flute_player`
- **Classe :** `FlutePlayer` (extends `RoleNeutral`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Neutres · **Aura :** Lumineuse · **Attribut :** Neutre

`defaultAura = LIGHT`. Timer `TimerBase.FLUTE_PLAYER_PROGRESS` (6s défaut / 3s meetUp, step 1). IntValue `IntValueBase.FLUTE_PLAYER_DISTANCE` (20 défaut/meetUp, step 4). sharpness fer/diamant +1.

Craft custom (FlutePlayer.java:96-113) : une flûte (stick enchanté Flame, nom traduit) via recipe `XOX/OSO/XOX` (O=gold, S=stick), enregistré à l'init. `onFluteCraft` (PrepareItemCraftEvent, FlutePlayer.java:198-214) bloque le craft aux non-FlutePlayer. `onInventoryClick` (CraftItemEvent, FlutePlayer.java:150-196) : premier craft → `hasOwnFlute=true` ; crafts suivants → `fluteInStore++` (stock), items consommés rendus, inventaire fermé.

Progression d'enchantement (`second()`, FlutePlayer.java:234-274) : chaque `FLUTE_PLAYER_PROGRESS` secondes (modulo), pour chaque joueur vivant ≠ soi, non déjà charmed (`affectedPlayer`) ni fluté (`flutedPlayer`) :
- si à < `FLUTE_PLAYER_DISTANCE` du FlutePlayer → progress +1 ;
- sinon si à < distance d'un joueur déjà fluté ET `flutedCycle % 3 == 0` → progress +1.
`onHit` (EntityDamageByEntityEvent, FlutePlayer.java:276-302) : si le FlutePlayer frappe un joueur (corps à corps) → +10 progress (one-shot par victime via `hitPlayers`). `tryEnchant` (FlutePlayer.java:304-318) : à progress > 100 → fire `EnchantedEvent`, ajoute à `affectedPlayer`, message, puis `applyTierEffects()`.

Tiers d'effets (`applyTierEffects`, FlutePlayer.java:320-360), comptés sur les charmed vivants :
- ≥3 charmed : +4 cœurs (one-shot `bonus3Heart`).
- ≥12 charmed : +2 cœurs (one-shot `bonus12Heart`).
- ≥6 charmed : Strength permanente (add/remove).
- ≥9 charmed : Speed permanente (add/remove).
- ≥6 ET `count+1 >= playersCount` (tous enchantés sauf soi) : Resistance + fire `AllPlayerEnchantedEvent` (one-shot `all`).

`onRequestStrengthRate` (FlutePlayer.java:362-379) : si 6-8 charmed → `strengthRate / 2` (force native réduite) ; ≥9 → rate normal.

Transmission de flûte (`onInteraction`/PlayerInteractAtEntityEvent, FlutePlayer.java:428-480) : si `fluteInStore > 0` et `power` true, right-click sur un joueur → place une flûte dans son inventaire (slot 9-39 libre), ajoute à `flutedPlayer` (propagation), `pendingNotify`, `power=false` (1/jour), `fluteInStore--`, fire `GiveFluteEvent`. `onDay` (FlutePlayer.java:216-232) : remet `power=true` si `hasOwnFlute`, notifie les `pendingNotify`, affiche le stock.

Protections : `onInventoryClick` (InventoryClickEvent, FlutePlayer.java:391-410) et `onDrop` (PlayerDropItemEvent, FlutePlayer.java:412-426) empêchent les joueurs flutés de manipuler/drop les flûtes.

`onFinalDeath` (HIGHEST, FlutePlayer.java:482-496) nettoie `flutedPlayer/pendingNotify/hitPlayers/progress` du mort et recalcule les tiers. `recoverPotionEffect()` rappelle `applyTierEffects()`. `recoverPower()` vide. Pas de condition de victoire perso dans la classe.

### Loup Garou Blanc

- **Clé :** `white_werewolf`
- **Classe :** `WhiteWereWolf` (extends `RoleNeutral`) · **Marqueurs :** aucune
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

Rôle `defaultAura=DARK`, `attribute=NEUTRAL`. Classe très simple, sans commande, sans timer/config, sans `@EventHandler`.

`recoverPower()` (WhiteWereWolf.java:27) : `+10 PV max` (5 cœurs supplémentaires).

`isWereWolf()` (WhiteWereWolf.java:43) = `true` (override dur) : apparaît comme loup, dans la liste/chat loup. **Attention** : étend `RoleNeutral` et non `RoleWereWolf`, donc ne reçoit **pas** la Vision Nocturne + Force nocturne automatique de `RoleWereWolf.recoverPotionEffects()`. Seul effet : +10 PV.

`isNeutral()` (WhiteWereWolf.java:48) = `true` : `getCamp()` → NEUTRAL. Pas de `isWin` custom dans cette classe (la condition de victoire solitaire, si elle existe, est gérée hors de cette classe). Pas d'évènement dédié.

### Mastermind

- **Clé :** `mastermind`
- **Classe :** `Mastermind` (extends `RoleNeutral`) · **Marqueurs :** IGuesser, IAffectedPlayers, IPower
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

`defaultAura = NEUTRAL`. Aucun modificateur sharpness/protection.

Pouvoir de devinette (IGuesser). `canGuess` (Mastermind.java:49-67) : refusé si mort, si cible = soi, ou si cible déjà dans `guessedPlayers`/`failedPlayers`. `resolveGuess(key, target)` (Mastermind.java:69-82) : si le rôle deviné correspond → `guessedPlayers.add`, +2 cœurs ; sinon → `failedPlayers.add`, -2 cœurs. `getAvailableCategories()` retourne un set vide (toutes catégories autorisées).

Commande de désactivation `/ww werewolf.roles.mastermind.command_disable <player>` (CommandMastermindDisable, cible ALIVE ou JUDGEMENT) : si la cible est dans `guessedPlayers` (déjà devinée avec succès) → fire `MastermindEvent`, si non annulé → -2 cœurs au Mastermind, `targetWW.getRole().disableAbilities()` (désactive le pouvoir du rôle cible), messages aux deux.

`IPower` : `power=true` au départ, stocké mais non consommé par ces commandes. `recoverPower()` vide. `IAffectedPlayers` mapped sur `guessedPlayers`. Pas d'effet de potion passif, pas de condition de victoire perso.

### Nécromancien

- **Clé :** `necromancer`
- **Classe :** `Necromancer` (extends `RoleNeutral`) · **Marqueurs :** IProgress, IAffectedPlayers
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

`defaultAura = DARK`. sharpness fer/diamant +1. IntValue `IntValueBase.NECROMANCER_DISTANCE` (20 défaut/meetUp, step 5, BLACK_WOOL).

Mécanique de marquage passif (`second()`, Necromancer.java:159-219) :
- Si pas de victime active (`playerWW == null`) et `health < 7` : marque tout joueur vivant à `< NECROMANCER_DISTANCE` (addAffectedPlayer, retrait auto après 5 min via `scheduleSyncDelayedTask 5*60*20`), fire `UpdateNameTagEvent`.
- Si victime active vivante : `progress` augmente selon la distance (<15: +4, <45: +3, <75: +2, <100: +1). À `progress >= 600` → reset, `health++`, -2 cœurs à la victime, +2 cœurs au nécromancien (vol de vie). Plafonné à `health < 7`.

Affichage : `onNameTagUpdate` (UpdatePlayerNameTagEvent, HIGH, Necromancer.java:221-233) ajoute le suffixe `werewolf.roles.necromancer.mark` aux joueurs marqués vus par le nécromancien. `onActionBar` (Necromancer.java:236-261) affiche la `progress` courante.

Résurrection (`OnFirstDeath`/SecondDeathEvent, NORMAL, ignoreCancelled, Necromancer.java:75-125) : si `isAbilityEnabled()`, vivant, pas de victime active, la victime est marquée (`getAffectedPlayers().contains`), et `deathLocation.distance(nécromancien) <= NECROMANCER_DISTANCE` → fire `NecromancerResurrectionEvent` ; si non annulé → `playerWW = victim`, `clearAffectedPlayer`, `event.setCancelled(true)`, `game.resurrection(victim)` (la victime ressuscite et devient la cible drainée).

Mort de la victime ressuscitée (`onDeadAliveDeath`/FinalDeathEvent, Necromancer.java:127-157) : si le tueur est le nécromancien → rien ; si tueur = autre joueur → `playerWW = killer` (nouvelle victime), message, `progress=0` ; si pas de tueur (PvE) → `playerWW = null`, message, `progress=0`.

`getProgress()`/`setProgress()` (IProgress). Pas de commande dédiée, pas de condition de victoire perso. `recoverPower()` vide.

### Pyromane

- **Clé :** `pyromane`
- **Classe :** `Pyromane` (extends `RoleNeutral`) · **Marqueurs :** IPower
- **Camp :** Neutres · **Aura :** Lumineuse · **Attribut :** Neutre

`defaultAura = LIGHT`. sharpness fer/diamant +1. Constantes : `COVER_COOLDOWN=600s`, `HEART_LOSS_DURATION=600s`, `HEART_LOSS_AMOUNT=4`, `FIRE_DURATION=1200s`, `FIRE_PROXIMITY=15`, `BUILDING_PROXIMITY=15`, `MIN_BURN_RESISTANCE=-1.5f`.

`recoverPower()` (Pyromane.java:268-272) : `setPower(false)` + `burnPower=false` (démarre sans pouvoirs ; activés au premier jour via `onDay`).

Trois pouvoirs journaliers (`onDay`, Pyromane.java:234-240, si vivant et abilityEnabled) : `setPower(true)` (ignite) + `burnPower=true` (burn).

1. Cover (`/ww werewolf.roles.pyromane.cover_command <p1> <p2>`, 2 cibles ALIVE/JUDGEMENT excludeSelf, CommandPyromaneCover.java) : cooldown `canCover()` = 600s depuis `lastCoverTimer`. Les deux cibles doivent avoir été « rencontrées » (`hasMet` = présentes dans `getPlayerWW().getPlayersMet()`) et distinctes. Pour chaque cible → fire `PyromaneCoverEvent`, puis `cover()` (ajout à `coveredPlayers`, notifie la cible). `onCovered()` met à jour `lastCoverTimer`.

2. Ignite (`/ww werewolf.roles.pyromane.ignite_command`, `requiredPower=true`, CommandPyromaneIgnite.java) : si `coveredPlayers` non vide → `ignite()` (Pyromane.java:95-140) : `setPower(false)`, fire `PyromaneIgniteEvent` ; si annulé → `setPower(true)`. Sinon, pour chaque cible coverée vivante : retire de `coveredPlayers`, `-4 cœurs` pendant 600s (rendus après via tâche différée), diminue `burnResistance` (-0.5 initial puis -0.25, plancher -1.5), notifie la cible.

3. Burn building (`/ww werewolf.roles.pyromane.burn_command`, CommandPyromaneBurn.java) : si `canBurn()` → `findNearestBuilding()` (IVoteOffice/ICouncilChamber/ISanctuary à `< BUILDING_PROXIMITY=15` blocs). `burnBuilding()` (Pyromane.java:173-183) : `burnPower=false`, `burnPending=true`, délai aléatoire 3-6 min → `startFire()` (Pyromane.java:185-194) : `fireLocation`, `fireEndTimer = timer + FIRE_DURATION`. `second()` (Pyromane.java:208-232) : pendant le feu, joueurs vivants à `< FIRE_PROXIMITY=15` non déjà coverés → ajoutés à `coveredPlayers` (propagation par le feu). À `timer >= fireEndTimer` → `endFire()` (Pyromane.java:196-206) : si `IVoteOffice` → capacité divisée par 2 (`reduceCapacity`).

Effet de combat (`onDamage`, HIGHEST, Pyromane.java:242-256) : si le pyromane frappe un joueur dont `burnResistance != 0` → `damage *= (1 - res*0.20)`. Une `burnResistance` négative augmente donc les dégâts infligés par le pyromane à cette cible (plus elle a été ignitée, plus elle est vulnérable).

`hasPower()`/`setPower()` gèrent `ignitePower`. Pas de condition de victoire perso.

### Renégat

- **Clé :** `renegade`
- **Classe :** `Renegade` (extends `RoleNeutral`) · **Marqueurs :** IPower
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Rôle neutre qui doit choisir un camp en fonction des joueurs qu'il côtoie. `@Timer RENEGADE_DISCOVERY` (10s def/meetUp) et `@IntValue RENEGADE_DISTANCE` (20, step 5).

`recoverPower()` (Renegade.java:79) : au premier init, tire aléatoirement `blueIsVillage` (la couleur BLEUE correspond au camp VILLAGE ou LOUP), assigne aléatoirement une couleur BLEUE/ROUGE à chaque joueur, et démarre `balance=0.5f`, `power=false`.

Pouvoir principal — `second()` (Renegade.java:94) : chaque seconde, trouve le joueur vivant le plus proche (ignore les rôles `IInvisible` invisibles). S'il est à moins de `RENEGADE_DISTANCE` :
- Ajuste `balance` de `±1/50` par seconde (BLEU → +, ROUGE → -).
- Découverte progressive : après `RENEGADE_DISCOVERY` secondes de proximité continue avec un même joueur, il est marqué `discovered` et son camp (couleur) est révélé au Renegade (Renegade.java:139).
- Si `balance>=1` → `joinCamp(VILLAGER ou WEREWOLF selon blueIsVillage)` ; si `<=0` → camp opposé.

`joinCamp()` (Renegade.java:179) : déclenche `RenegadeJoinCampEvent`. Si VILLAGER → `setTransformedToVillager(true)` ; si WEREWOLF → `setInfected()` + `NewWereWolfEvent`. Compte les joueurs découverts de la couleur joined qui sont dans le camp adverse, l'annonce, puis `game.checkVictory()`.

`onDay` (Renegade.java:160) : si pas encore joined, `setPower(true)` chaque jour. À l'épisode ≥ `FORCED_JOIN_EPISODE=6` (Renegade.java:56), force le join selon `balance>=0.5`.

Commande `/ww <joueur>` (CommandRenegade.java) : `requiredPower`, cible vivante/jugement ≠ soi. Si la cible a été « découverte », déclenche `RenegadeSwapEvent` puis `swap()` (Renegade.java:218) — échange les couleurs BLEUE/ROUGE entre le Renegade et la cible. Consomme le power.

`onActionBar` (Renegade.java:244) : affiche une barre bleue/rouge de 20 caractères proportionnelle à `balance`, ou le camp joined.

`onDamage` (Renegade.java:271) : si joined, réduit les dégâts subis de 10 % (`RESISTANCE_LEVELS=0.5 * 0.20`) — mais uniquement si `joinedCamp==VILLAGER && nuit` OU `joinedCamp==WEREWOLF && jour` (protection pendant la phase « faible » du camp).

Pas de `isWin` custom : la victoire est celle du camp rejoint.

### Rival

- **Clé :** `rival`
- **Classe :** `Rival` (extends `RoleNeutral`) · **Marqueurs :** IPower, ILimitedUse
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

Rôle neutre `require_roles={CUPID}`, `sharpnessIron/DiamondModifier=1`, `powerModifier=1`, `defaultAura=DARK`. Cible un couple (non maudit) pour le détruire.

`onLoverReveal` (Rival.java:114) : à la révélation des couples, choisit aléatoirement un couple (exclut CURSED_LOVER et ceux le contenant). Stocke `lover`. Si c'est un `LoverImpl`, retrouve le Cupidon (`cupidWW`) qui l'a créé. Annonce les rôles des deux amoureux.

Pouvoir principal — tuer un membre du couple : `onLoverKilled` (FinalDeathEvent LOWEST, Rival.java:148) : si le Rival tue l'un des amoureux, `lover.dissolve()` (dissout le couple : pas de suicide du survivant, pas d'annonce → anonymat du Rival), `RivalLoverEvent`, `killed=true`, `power=false`. Le survivant reçoit un message « seul ».

`onLoverDeath`/`onAmnesiacLoverDeath` (Rival.java:180,189) : si le couple meurt naturellement (pas par le Rival), `applyPenalty()` (Rival.java:194) : `power=false`, `-10 PV max` (`PENALTY_HEALTH`), puis régénération de `+2 PV max` toutes les `REGEN_INTERVAL_TICKS=5min` pendant 5 intervalles. `RivalLoverDeathEvent`.

Commandes (4) :
- `/ww <p1> <p2> <p3>` analyser (CommandAnalyser, cooldown `ANALYSE_COOLDOWN=15min`, Rival.java:62) : compte combien des 3 sont dans le couple.
- `/ww <p1> <p2>` deviner (CommandDeviner, cooldown `DEVINER_COOLDOWN=30min`, one-shot) : si les deux sont exactement les membres du couple → `guessed=true`.
- `/ww` isoler (CommandIsoler, `requiredLimitedUse=2`, Rival.java:278) : requiert `guessed`. Isole le membre du couple le plus proche (≤`ISOLATE_RANGE=15` blocs) pendant `ISOLATE_DURATION=3min`. Pendant l'isolement, `onDon` (Rival.java:369) annule tout `DonEvent` impliquant l'isolé. `ActionableStoryEvent`.
- `/ww <1-4>` bonus (CommandBonus, Rival.java:328) : requiert `killed`. Choix unique parmi : (1) 25 pommes dorées, (2) Speed II permanent, (3) Strength permanent, (4) Regeneration I permanent.

`onDamage` (Rival.java:387) : si `guessed`, réduit de 30 % (`DAMAGE_REDUCTION`) les dégâts reçus d'un membre du couple.

`onActionBar` (Rival.java:410) : si `guessed`, flèche vers un amoureux vivant ; sinon si `cupidWW` vivant, flèche vers le Cupidon.

`onSwap`/`onSteal` (Rival.java:95,217) : recale `cupidWW` lors d'échange/vol de rôle. `ILimitedUse.getUse()` = `isolateUse` (max `ISOLATE_MAX_USE=2`).

### Romulus ou Remus

- **Clé :** `romulus_remus`
- **Classe :** `RomulusRemus` (extends `RoleImpl`) · **Marqueurs :** IAffectedPlayers, ITransformed
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Hybride

Rôle `requireDouble=true`, `attribute=HYBRID`, `@IntValue ROMULUS_REMUS_DISTANCE_BROTHER` (50 def, 40 meetUp, step 5). Au départ `displayRole=VILLAGER`, `displayCamp=VILLAGER`, `transformed=false`.

`isNeutral()` (RomulusRemus.java:73) = `!transformed` → neutre tant que non transformé.
`isWereWolf()` (RomulusRemus.java:129) = `super.isWereWolf() || (transformed && !romulus)` : seul Remus (romulus=false) devient loup en se transformant.

`initialize()` (RomulusRemus.java:191, déclenché par `WereWolfListEvent`) : apparie les deux RomulusRemus comme frères, tire aléatoirement qui est Romulus vs Remus, et choisit une « mère » = un loup vivant aléatoire (ou un joueur quelconque si pas de loup). Appelle `initialize(brother, mother, isRomulus)` sur le frère. `affectedPlayers` = [frère, mère].

`onFinalDeath` (RomulusRemus.java:143) :
- Si le frère meurt et que le tueur est ce joueur → `killedBrother=true`. Si déjà transformé → Strength (clé `romulus_remus_strength`) ; si Remus non-loup → `NewWereWolfEvent`.
- Si la mère meurt → `setTransformed(true)`, retire Resistance, `MotherDeathEvent`.

`recoverPotionEffect` (RomulusRemus.java:267) : si `transformed && killedBrother` → Strength.

`onNight` (RomulusRemus.java:274) : si transformé et loup (Remus), retire le Strength générique `RoleBase.WEREWOLF` (garde uniquement le sien).

`onActionBar` (RomulusRemus.java:289) : tant que non transformé et frère vivant, flèche vers le frère.

`onDetectVictoryWithFamily` (WinConditionsCheckEvent, RomulusRemus.java:326) : si non transformé, frère et mère vivants, et `alivePlayers==3` → victoire, `victoryTeam=ROMULUS_REMUS`.

`onAppearInWerewolfList` (RomulusRemus.java:349) : si loup (Remus transformé) et `!romulus && !killedBrother` → n'apparaît pas dans la liste des loups (loup caché).

`second()` (RomulusRemus.java:364) : toutes les 5s, si frère vivant et distance > `ROMULUS_REMUS_DISTANCE_BROTHER` → Resistance 120 ticks ; sinon retire la Resistance. Si mère à moins de 20 blocs et non `metMother` → programme un message à la mère dans 2 min (RomulusRemus.java:399), `metMother=true`.

`disableAbilitiesRole` : retire la Resistance. `onSwap`/`onFamilyIsStolen` (RomulusRemus.java:414,447) : recale frère/mère lors d'échange/vol.

### Ronin

- **Clé :** `ronin`
- **Classe :** `Ronin` (extends `RoleNeutral`) · **Marqueurs :** IPower
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

Rôle `defaultAura=DARK` basé sur un système d'honneur. `recoverPower()` (Ronin.java:86) : `roninHonor=-4`, `highestHonor=-4`, `power=false`, `applyHonorEffects()`.

Gains/pertes d'honneur (`applyHonorChange`, Ronin.java:266) : tuer un LOUP +2, NEUTRE +2, VILLAGER -1 ; être voté -2 ; voter +2 ; abstention -1 ; groupe trop important -1 ; visiter tous les sanctuaires +2. Borné [-4, +4]. **Les effets sont basés sur `highestHonor` (pic atteint), pas sur l'honneur courant** — on garde son meilleur bonus même en cas de chute.

`applyHonorEffects()` (Ronin.java:303) selon `highestHonor` :
- PV max : h4→+6, h3→+4, h2→+2, h1/h0→0, h-1/h-2→-2, h-3/h-4→-4.
- Aura : ≥2 LIGHT, ≥-1 NEUTRAL, sinon DARK.
- Resistance : ≥3 → Resistance I (level 1.0), ≥1 → 10 % de réduction (0.5), ≤-4 → +10 % dégâts (-0.5).
- h4 → Speed I.

`onHonorChange` (LOWEST, Ronin.java:116) : annule tout changement d'honneur externe (le Ronin gère le sien). `onSwap` (Ronin.java:123) : resynchronise l'honneur avec le nouveau corps.

Pouvoir — commande `/ww <cible>` (CommandRonin, `requiredPower`) → `startDuel(target)` (Ronin.java:340) : `setPower(false)`, puis selon l'honneur de la cible : >0 → hémorragie, <0 → silence, ==0 → aléatoire.
- Hémorragie (Ronin.java:358) : durée selon `highestHonor` (120s à h4 → 30s). Malus de walk speed (jusqu'à -75 %). Malus de resistance. `onDamage` (Ronin.java:410) : si le Ronin frappe la cible hémorragique → +1 dégât différé (5 ticks). La cible subit aussi le malus de resistance.
- Silence (Ronin.java:378) : `target.getRole().disableAbilities()` pour une durée selon `highestHonor` (20min à h4 → 4min). À la fin, `enableAbilities()`.

`onDay` (Ronin.java:203) : `setPower(true)`, vérifie `isInOversizedGroup()` (≥ `game.getGroup()` joueurs vivants dans un rayon de 30 blocs) → -1 honneur.

`onFinalDeath` (Ronin.java:137) : si le Ronin meurt → fin hémorragie+silence. Si la cible d'hémorragie/silence meurt → nettoie. Si le Ronin est le tueur → gain d'honneur selon le camp de la victime. `onVoteResult`/`onVote`/`onAbstention` : changements d'honneur. `onSanctuaryVisit` (Ronin.java:188) : tracke les couleurs de sanctuaires visités ; tous visités → +2, reset.

`sendHonorParticles` (Ronin.java:238) : toutes les 2s, particules blanches (honneur positif) ou rouges (négatif) au-dessus des joueurs vivants à moins de 60 blocs.

`onActionBar` (Ronin.java:444) : affiche `roninHonor` et `highestHonor`.

### Succube

- **Clé :** `succubus`
- **Classe :** `Succubus` (extends `RoleNeutral`) · **Marqueurs :** IProgress, IAffectedPlayers, IPower
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

Rôle `defaultAura=DARK`. `@Timer SUCCUBUS_DURATION` (180 def, 120 meetUp), `@IntValue SUCCUBUS_DISTANCE` (20, step 4).

`recoverPower()` (Succubus.java:119) : envoie `charming_message` (peut charmer).

Pouvoir — commande `/ww <cible>` (CommandSuccubus, `requiredPower`, cible vivante ≠ soi) : si pas déjà d'affecté, et cible à moins de `SUCCUBUS_DISTANCE` → `BeginCharmEvent`, ajoute la cible en `affectedPlayer`. Démarre le charme.

`second()` (Succubus.java:124) : si a une cible affectée, `hasPower()`, et cible à moins de `SUCCUBUS_DISTANCE` → incrémente `progress` de `100/(SUCCUBUS_DURATION+1)` par seconde. À `progress>=100` → `CharmEvent`, `setProgress(0)`, `setPower(false)` (charme consommé). Si non annulé → la cible est charmée (message + son), `game.checkVictory()`.

`onFinalDeath` (Succubus.java:184) : si la cible charmée meurt → clear, `setPower(true)`, `setProgress(0)`, `charming_message` (peut recharmer une nouvelle cible).

`onSecondDeathEvent` (LOW, Succubus.java:199) : si la Succubus elle-même meurt (`SecondDeathEvent`) et a une cible charmée (`!hasPower()`) → `SuccubusResurrectionEvent`. Si non annulé → annule la mort, **tue la cible charmée** (`target.damage(10000)` ou `game.death`), `game.resurrection(getPlayerWW())`. La Succubus ressuscite en sacrifiant son charmé.

`onDetectVictoryWitchCharmed` (WinConditionsCheckEvent, Succubus.java:240) : si `!hasPower()` et cible vivante, construit la chaîne des Succubus/cibles charmées ; si `playersCount == taille de la chaîne` → victoire, `victoryTeam=SUCCUBUS`.

`onLover` (AroundLoverEvent, Succubus.java:281) : si charmée (`!hasPower()`), relie la Succubus et ses charmés dans les évènements de couple.

### Tueur en Série

- **Clé :** `serial_killer`
- **Classe :** `SerialKiller` (extends `RoleNeutral`) · **Marqueurs :** IPower
- **Camp :** Neutres · **Aura :** Obscure · **Attribut :** Neutre

Rôle `defaultAura=DARK`, `sharpnessIron/DiamondModifier=1`, `protectionIron/DiamondModifier=1`, `powerModifier=1`.

`recoverPower()` (SerialKiller.java:66) : ajoute `extraHeart` PV max (0 au départ).
`recoverPotionEffect()` (SerialKiller.java:72) : si `hasPower()` → Strength permanente.

`onFinalDeath` (SerialKiller.java:80) : si le SerialKiller est le tueur → `SerialKillerEvent`. Si `hasPower()` → retire la Strength et `setPower(false)` (le premier kill consomme le pouvoir initial). Puis si `isAbilityEnabled()` → `+2 PV max` (`extraHeart+=2`, soit +1 cœur) et `+1 pomme dorée`. Donc chaque kill donne +1 cœur et une pomme dorée ; le premier kill retire en plus la Strength initiale.

`disableAbilitiesRole` (SerialKiller.java:105) : retire la Strength si `hasPower`. Pas de commande dédiée. Pas de `isWin` custom (neutre standard).

### Voleur

- **Clé :** `thief`
- **Classe :** `Thief` (extends `RoleNeutral`) · **Marqueurs :** IAffectedPlayers, IPower
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Hybride

Rôle `attribute=HYBRID`, `auraDescriptionSpecialUseCase`. Pas de timer/config dédiés.

`recoverPotionEffect()` (Thief.java:100) : si `hasPower()` et ability enabled → Resistance permanente.

`onPlayerDeath` (PlayerDeathEvent, Thief.java:110) : si le Thief est le tueur Bukkit → Speed pendant 1200 ticks (60s).

Pouvoir — `onFirstDeathEvent` (LOWEST, ignoreCancelled, Thief.java:125) : si le Thief est le tueur de la victime et `hasPower()` → **annule la mort** (empêche le FinalDeath). Après un délai de 7 secondes (Thief.java:138), si toujours vivant et `hasPower()` → `thiefRecoverRole()` ; sinon re-déclenche `SecondDeathEvent`.

`thiefRecoverRole()` (Thief.java:151) : clone le rôle de la victime (`publicClone()`), `setPower(false)`, `HandlerList.unregisterAll(this)`, `setRole(roleClone)` sur le Thief. Gère l'infection (`setInfected` sur le clone) / loup natif (`NewWereWolfEvent`). Enregistre `addDeathRole`. `StealEvent`. Clear des potion effects. Transfert de solitude (bonus PV jusqu'à 30 si la cible était solitary). **Transfère les couples** (`lover.swap`) vers le Thief. `UpdateNameTagEvent`. `game.death(victim)`.

Donc le Thief vole le rôle de sa victime en portant le coup fatal (pouvoir unique) : la victime meurt définitivement, le Thief devient son rôle. Pas de commande dédiée.

`disableAbilitiesRole` (Thief.java:214) : retire la Resistance.

### Voyou

- **Clé :** `thug`
- **Classe :** `Thug` (extends `RoleNeutral`) · **Marqueurs :** IPower, IAffectedPlayers
- **Camp :** Neutres · **Aura :** Neutre · **Attribut :** Neutre

Rôle `defaultAura=NEUTRAL`, `sharpnessIron/DiamondModifier=1`. `@IntValue THUG_DISTANCE` (25, step 5). `probability` démarre à 10.

`onDay` (Thug.java:82) : `setPower(true)` chaque jour, rafraîchit le nametag de l'affecté, envoie `command_message`.

`recoverPotionEffect` (Thug.java:102) : si `hasPower2()` → Resistance.

Commande `/ww <cible>` (CommandThug, `requiredPower`, cible vivante, `rejectIfAlreadyAffected`) : `setPower(false)`, `ThugEvent`, ajoute la cible en `affectedPlayer` (single). La cible reçoit une alerte. `UpdateNameTagEvent`.

`onUpdateNameTag` (Thug.java:277) : si le Thug a un affecté et `!hasPower()` (cible choisie) → cache le nametag de la cible (`setVisibility(false)`). Le Thug rend anonyme sa cible.

`onPlayerDeath` (FinalDeathEvent, Thug.java:119) : si le Thug est le tueur :
- Si `!hasPower2()` → `setPower2(true)` + Resistance (premier kill).
- Si la victime a des kills → `+2 PV max` (+1 cœur).
- `probability` % de chance → `ThugRevealEvent` → `deathPlayerWithRoleRevealed` = victime.

`onAnnouncement` (AnnouncementDeathEvent HIGH, Thug.java:162) : si la victime est `deathPlayerWithRoleRevealed` → format `death_message` (rôle révélé dans l'annonce de mort).

`onPlayerDeath` (DeathItemsEvent, Thug.java:170) : si la victime meurt à moins de 25 blocs du Thug et lâche >1 pomme dorée → `ThugRecoverGoldenAppleEvent`, vole la moitié des pommes dorées (convertit les drops en cobblestone), les donne au Thug. `probability += 10`.

Donc le Thug : marque une cible (anonymise son pseudo), gagne Resistance au 1er kill, +1 cœur par kill (de joueurs avec kills), chance de révéler le rôle de la victime, et vole des pommes dorées sur les morts proches (augmente sa proba de révélation). Pas de `isWin` custom.
