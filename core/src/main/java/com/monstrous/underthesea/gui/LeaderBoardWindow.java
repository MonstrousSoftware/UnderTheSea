package com.monstrous.underthesea.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.monstrous.underthesea.World;
import com.monstrous.underthesea.leaderboard.LeaderBoardEntry;
import com.monstrous.underthesea.screens.Main;

public class LeaderBoardWindow extends Window {

    private Array<LeaderBoardEntry> leaderBoard;
    private Skin skin;
    private Main game;
    private World world;
    private boolean[] newScore;

    public LeaderBoardWindow(String title, Skin skin, World world, Array<LeaderBoardEntry> leaderBoard, final Main game ) {
        super(title, skin);
        this.skin = skin;
        this.leaderBoard = leaderBoard;
        this.game = game;
        this.world = world;

        getTitleLabel().setAlignment(Align.center);

        newScore = new boolean[1];
    }

    public void refresh() {

        clear();

        newScore[0] =  world.gameComplete && !world.scoreSavedToServer;


        TextButton okButton = new TextButton("CLOSE", skin);
        if(newScore[0])
            okButton.setText("SAVE SCORE");

        String style = "window";

        Table board = new Table();
        for(LeaderBoardEntry entry : leaderBoard ){ // we rely on leader board to have a sensible nr of entries
            board.add( new Label( entry.rank, skin, style) ).pad(10);
            board.add( new Label( entry.displayName, skin, style) ).width(120).pad(10);
            board.add( new Label( entry.score, skin, style) ).width(100).pad(10);
            board.row();
        }
        board.pack();

        TextField nameField = new TextField(game.userName, skin);
        nameField.setMaxLength(16);
        nameField.setOnlyFontChars(true);
        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.userName = nameField.getText();
            }
        });



        Table nameEntry = new Table();
        nameEntry.add( new Label("Your name: ", skin, style));
        nameEntry.add( nameField );
        nameEntry.pack();


        add( new Label("BEST TIMES", skin, style));
        row();
        add(board);
        row();
        if(newScore[0]) {
            add(nameEntry);
            row();
        }
        add(okButton).width(100);
        pack();


        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if(newScore[0]) {
                    world.scoreSavedToServer = true;
                    if(game.gameJolt != null ) {
                        game.gameJolt.addScore(game.userName, world.getTimeString(), (int) world.playTime); // send score to the server
                        game.gameJolt.getScores();          // this is asynchronous, so we cannot show the updated table now
                    }
                }
                setVisible(false);  // hide the window
            }
        });
    }


}
