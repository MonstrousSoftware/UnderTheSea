package com.monstrous.underthesea.leaderboard;

import com.badlogic.gdx.utils.Array;

public class LeaderBoard {
    private Array<LeaderBoardEntry> entries;
    private Array<LeaderBoardClient> clients;

    public LeaderBoard() {
        entries = new Array<>();
        clients = new Array<>();
    }

    public void clear() {
        entries.clear();
    }

    public void add( LeaderBoardEntry entry ) {
        entries.add(entry);
    }

    public Array<LeaderBoardEntry> getEntries() {
        return entries;
    }

    public void registerClient( LeaderBoardClient client ){
        clients.add(client);
    }
    public void propagate() {
        for( LeaderBoardClient client : clients )
            client.leaderBoardIsUpdated();

    }
}
