package org.devathon.contest2016.game.state;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.devathon.contest2016.DevathonPlugin;
import org.devathon.contest2016.game.Game;
import org.devathon.contest2016.module.Module;
import org.devathon.contest2016.user.GameUser;

/**
 * @author tmxx
 * @version 1.0
 */
public class IngameState extends GameState {
    public IngameState( DevathonPlugin devathonPlugin, Game game ) {
        super( devathonPlugin, game, "Ingame", devathonPlugin.getMainConfig().getIngameTime() );
    }

    @Override
    public void run() {
        if ( this.getTime() == 0 ) {
            this.getGame().setGameState( this.getGame().getEndingState() );
            return;
        }
        if ( this.getTime() % 30 == 0 || this.getTime() <= 10 ) {
            Bukkit.broadcastMessage( this.getDevathonPlugin().getPrefix() + "§e" + this.getTime() +
                    " second" + ( this.getTime() == 1 ? "" : "s" ) + " §cleft" );
            for ( Player player : Bukkit.getOnlinePlayers() ) {
                player.playSound( player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 5F, 5F );
            }
        }
        this.setTime( this.getTime() - 1 );
        for ( GameUser gameUser : GameUser.getGameUsers() ) {
            gameUser.getIndividualScoreboard().updateIngame();
        }
        for ( Module module : this.getDevathonPlugin().getModuleList() ) {
            module.check();
            for ( GameUser gameUser : GameUser.getPlayingUsers() ) {
                if ( module.isInBounds( gameUser.getPlayer().getLocation() ) ) {
                    if ( gameUser.getModule() == null ) {
                        gameUser.setModule( module );
                        gameUser.getPlayer().sendMessage( this.getDevathonPlugin().getPrefix() + "§eYou reached module " + module.getModuleConfig().getName() );
                    } else {
                        if ( !gameUser.getModule().equals( module ) ) {
                            gameUser.setModule( module );
                            gameUser.getPlayer().sendMessage( this.getDevathonPlugin().getPrefix() + "§eYou reached module " + module.getModuleConfig().getName() );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void start() {
        Bukkit.broadcastMessage( this.getDevathonPlugin().getPrefix() + "§eIngame time started!" );

        for ( GameUser gameUser : GameUser.getPlayingUsers() ) {
            gameUser.getPlayer().teleport( this.getDevathonPlugin().getMapConfig().getIngameSpawn().getLocation() );
            gameUser.getIndividualScoreboard().setupIngameObjective();
        }
    }

    @Override
    public void end() {

    }
}
