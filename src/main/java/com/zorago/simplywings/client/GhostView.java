package com.zorago.simplywings.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public final class GhostView {
    private static final String TEAM = "simplywings_ghost";
    private static boolean active;

    static void tick(Player player) {
        boolean invisible = player.isInvisible();
        if (invisible && !active) {
            join(player);
        } else if (!invisible && active) {
            leave(player);
        }
    }

    private static void join(Player player) {
        Scoreboard scoreboard = player.level().getScoreboard();
        String name = player.getScoreboardName();
        active = true;
        if (scoreboard.getPlayersTeam(name) != null) {
            return;
        }
        PlayerTeam team = scoreboard.getPlayerTeam(TEAM);
        if (team == null) {
            team = scoreboard.addPlayerTeam(TEAM);
        }
        team.setSeeFriendlyInvisibles(true);
        scoreboard.addPlayerToTeam(name, team);
    }

    private static void leave(Player player) {
        Scoreboard scoreboard = player.level().getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(TEAM);
        if (team != null) {
            if (scoreboard.getPlayersTeam(player.getScoreboardName()) == team) {
                scoreboard.removePlayerFromTeam(player.getScoreboardName(), team);
            }
            scoreboard.removePlayerTeam(team);
        }
        active = false;
    }

    static void clear() {
        active = false;
    }

    private GhostView() {
    }
}
