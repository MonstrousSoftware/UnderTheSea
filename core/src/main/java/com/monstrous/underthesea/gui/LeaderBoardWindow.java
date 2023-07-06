package com.monstrous.underthesea.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.monstrous.underthesea.World;
import com.monstrous.underthesea.leaderboard.LeaderBoard;
import com.monstrous.underthesea.leaderboard.LeaderBoardClient;
import com.monstrous.underthesea.leaderboard.LeaderBoardEntry;
import com.monstrous.underthesea.screens.Main;

public class LeaderBoardWindow extends Window implements LeaderBoardClient {

    private LeaderBoard leaderBoard;
    private Skin skin;
    private Main game;
    private World world;
    private boolean[] newScore;
    private Label titleLabel;
    private Table board;
    private TextButton okButton;
    private TextButton saveButton;
    private Table nameEntry;

    // note: world can be null for a read-only leaderboard
    public LeaderBoardWindow(String title, Skin skin, World world, LeaderBoard leaderBoard, final Main game ) {
        super(title, skin);
        this.skin = skin;
        this.leaderBoard = leaderBoard;
        this.game = game;
        this.world = world;

        getTitleLabel().setAlignment(Align.center);

        board = new Table();
        leaderBoard.registerClient(this);               // get notified of asynchronous updates

        newScore = new boolean[1];

        if(game.gameJolt != null)
            game.gameJolt.getScores();  // update score table from server

        //newScore[0] =  world.gameComplete && !world.scoreSavedToServer; // completed the game and not saved the score yet?

        okButton = new TextButton("CLOSE", skin);
        saveButton = new TextButton("SAVE SCORE", skin);

        // let leaderBoardIsUpdated() take care of filling the board

        String style = "window";
        TextField nameField = new TextField(game.userName, skin);
        nameField.setMaxLength(16);
        nameField.setOnlyFontChars(true);
        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.userName = nameField.getText();
            }
        });


        nameEntry = new Table();
        nameEntry.add( new Label("Your name: ", skin, style));
        nameEntry.add( nameField );
        nameEntry.pack();


        if(!game.gameJolt.onLine)
            titleLabel = new Label("Server connection offline.\nNo leader board available.", skin, style);
        else
            titleLabel = new Label("FASTEST TIMES FOR MISSION COMPLETION", skin, style);


        rebuild();

        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                    world.scoreSavedToServer = true;    // don't save the same score multiple times
                    if(game.gameJolt != null ) {
                        game.gameJolt.addScore(game.userName, world.getTimeString(), (int) world.playTime); // send score to the server
                        // this will also update the leader board
                    }
                    rebuild();
            }
        });

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
              setVisible(false);  // hide the window
            }
        });
    }

    public void rebuild() {
        Gdx.app.log("leader board window", "rebuild");
        newScore[0] = world != null && world.gameComplete && !world.scoreSavedToServer; // completed the game and not saved the score yet?

        clear();
        add(titleLabel).pad(5);
        row();
        add(board);
        row();
        if( newScore[0] ) {  // allow name entry
            add(nameEntry);
            row();
            add(saveButton).width(100);
        }
        else
            add(okButton).width(100);
        pack();
    }


    private void fillTable() {
        String style = "window";

        board.clear();
        for(LeaderBoardEntry entry : leaderBoard.getEntries() ){ // we rely on leader board to have a sensible nr of entries
            board.add( new Label( entry.rank, skin, style) ).pad(10);
            board.add( new Label( entry.displayName, skin, style) ).width(120).pad(10);
            board.add( new Label( entry.score, skin, style) ).width(100).pad(10);
            board.row();
        }
        board.pack();
    }

    @Override
    public void leaderBoardIsUpdated() {
        Gdx.app.log("leader board window", "refresh");
        fillTable();
        rebuild();
    }
}
