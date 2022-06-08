package fr.ph1lou.werewolfplugin.configs;

import fr.ph1lou.werewolfapi.annotations.Configuration;
import fr.ph1lou.werewolfapi.annotations.ConfigurationBasic;
import fr.ph1lou.werewolfapi.annotations.IntValue;
import fr.ph1lou.werewolfapi.annotations.Timer;
import fr.ph1lou.werewolfapi.basekeys.ConfigBase;
import fr.ph1lou.werewolfapi.basekeys.IntValueBase;
import fr.ph1lou.werewolfapi.basekeys.TimerBase;
import fr.ph1lou.werewolfapi.enums.UniversalMaterial;
import fr.ph1lou.werewolfapi.events.game.vote.VoteBeginEvent;

@Configuration(config = @ConfigurationBasic(key = ConfigBase.VOTE,
        defaultValue = true,
        meetUpValue = true),
        timers = {
                @Timer(key = TimerBase.VOTE_BEGIN,
                        defaultValue = 40 * 60,
                        meetUpValue = 6 * 60,
                        decrement = true,
                        step = 30,
                        onZero = VoteBeginEvent.class),
                @Timer(key = TimerBase.VOTE_DURATION,
                        defaultValue = 60 * 3,
                        meetUpValue = 60,
                        step = 10),
                @Timer(key = TimerBase.VOTE_WAITING,
                        defaultValue = 60,
                        meetUpValue = 60,
                        step = 10)
        },
        configValues = @IntValue(key = IntValueBase.VOTE_END, defaultValue = 10,
                meetUpValue = 8,
                step = 1, item = UniversalMaterial.PLAYER_HEAD),
        configurations = {
        @ConfigurationBasic(key = ConfigBase.VOTE_EVERY_OTHER_DAY),
                @ConfigurationBasic(key = ConfigBase.NEW_VOTE)}
)
public class Vote {

}
