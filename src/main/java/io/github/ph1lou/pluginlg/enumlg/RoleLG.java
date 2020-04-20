package io.github.ph1lou.pluginlg.enumlg;


import io.github.ph1lou.pluginlg.listener.roleslisteners.*;

public enum RoleLG {

    COUPLE(null, true, null),
    COUPLE_MAUDIT(null, true, null),
    CUPIDON(Camp.VILLAGE, true, null),
    LOUP_GAROU(Camp.LG, true, null),
    INFECT(Camp.LG, true, null),
    LOUP_FEUTRE(Camp.LG, true, ListenerRolesFalsifierWolf.class),
    LOUP_PERFIDE(Camp.LG, true, ListenerRolesMischievousWolf.class),
    VILAIN_PETIT_LOUP(Camp.LG, true, null),
    VOLEUR(Camp.NEUTRAL, true, null),
    ENFANT_SAUVAGE(Camp.VILLAGE, true, null),
    SORCIERE(Camp.VILLAGE, true, null),
    PETITE_FILLE(Camp.VILLAGE, true, ListenerRolesLittleGirl.class),
    SALVATEUR(Camp.VILLAGE, false, ListenerRolesProtector.class),
    RENARD(Camp.VILLAGE, false, ListenerRolesFox.class),
    MONTREUR_OURS(Camp.VILLAGE, true, ListenerRolesBearTrainer.class),
    VOYANTE_BAVARDE(Camp.VILLAGE, false, ListenerRolesChattySeer.class),
    VOYANTE(Camp.VILLAGE, false, ListenerRolesSeer.class),
    TRUBLION(Camp.VILLAGE, true, null),
    SOEUR(Camp.VILLAGE, true, null),
    ANCIEN(Camp.VILLAGE, true, null),
    CORBEAU(Camp.VILLAGE, false, ListenerRolesRaven.class),
    VILLAGEOIS(Camp.VILLAGE, true, null),
    DETECTIVE(Camp.VILLAGE, false, ListenerRolesDetective.class),
    MINEUR(Camp.VILLAGE, true, null),
    CITOYEN(Camp.VILLAGE, true, ListenerRolesCitizen.class),
    FRERE_SIAMOIS(Camp.VILLAGE, true, null),
    COMEDIEN(Camp.VILLAGE, false, ListenerRolesComedian.class),
    TRAPPEUR(Camp.VILLAGE, false, ListenerRolesTrapper.class),
    ANGE(Camp.NEUTRAL, true, null),
    ANGE_GARDIEN(Camp.NEUTRAL, true, null),
    ANGE_DECHU(Camp.NEUTRAL, true, null),
    LOUP_AMNESIQUE(Camp.NEUTRAL, true, null),
    LOUP_GAROU_BLANC(Camp.NEUTRAL, true, ListenerRolesWhiteWerewolf.class),
    ASSASSIN(Camp.NEUTRAL, true, ListenerRolesAssassin.class),
    TUEUR_EN_SERIE(Camp.NEUTRAL, true, null);

    private final Camp camp;
    private final Boolean power;
    final Class<? extends ListenerRoles> listenerRoleClass;

    RoleLG(Camp camp, Boolean power, Class<? extends ListenerRoles> listenerRoleClass) {
        this.camp = camp;
        this.power = power;
        this.listenerRoleClass = listenerRoleClass;
    }


    public Camp getCamp() {
        return this.camp;
    }


    public Boolean getPower() {
        return this.power;
    }

    public Class<? extends ListenerRoles> getListener() {
        return listenerRoleClass;
    }

}
