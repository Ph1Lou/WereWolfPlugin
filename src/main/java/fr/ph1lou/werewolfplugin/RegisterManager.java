package fr.ph1lou.werewolfplugin;


import fr.ph1lou.werewolfapi.registers.impl.AddonRegister;

public class RegisterManager extends Register {

    private static RegisterManager instance;
    private final AddonRegister addonRegister;

    public RegisterManager(Main main) {
        super(main);
        this.addonRegister = new AddonRegister("","fr",main);
        instance = this;
    }

    public static RegisterManager get() {
        return instance;
    }

    public AddonRegister getAddonRegister(){
        return this.addonRegister;
    }

}
