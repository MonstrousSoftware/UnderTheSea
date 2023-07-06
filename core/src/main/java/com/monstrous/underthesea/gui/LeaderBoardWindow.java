package com.monstrous.underthesea.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
    // we need to pass the stage for animation calculations, the window is not yet in a stage when it is in the constructor, so we cannot use getParent().
    public LeaderBoardWindow(String title, Skin skin, Stage stage, World world, LeaderBoard leaderBoard, final Main game ) {
        super(title, skin);
        this.skin = skin;
        this.leaderBoard = leaderBoard;
        this.game = game;
        this.world = world;

        getTitleLabel().setAlignment(Align.center);

        board = new Table();
        leaderBoard.registerClient(this);               // get notified of asynchronous updates

        newScore = new boolean[1];

//        if(game.gameJolt != null)
//            game.gameJolt.getScores();  // update score table from server

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

        // centre position for this window
        float wx = (stage.getWidth() - getWidth())/2;
        float wy = (stage.getHeight() - getHeight())/2;
        // animate that the window drops from the top of the screen
        setPosition(wx, stage.getHeight());     //  place at top of the screen
        addAction(Actions.moveTo(wx, wy, .4f, Interpolation.swingOut));

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
                setKeepWithinStage(false);  // allow window to go offscreen
                // swing window up and then remove the window
                // remove is called as part of the action sequence so that the animation can play
                addAction(Actions.sequence(Actions.moveTo(wx, stage.getHeight(), .4f, Interpolation.swingIn),
                    new Action() {
                        @Override
                        public boolean act(float delta) {
                            remove();
                            return true;
                        }
                    }));
            }
        });
    }

    private void rebuild() {
        Gdx.app.log("leader board window", "rebuild");
        newScore[0] = world != null && world.gameComplete && !world.scoreSavedToServer; // completed the game and not saved the score yet?

        String style = "window";

        board.clear();
        for(LeaderBoardEntry entry : leaderBoard.getEntries() ){ // we rely on leader board to have a sensible nr of entries
            Table rowTable = new Table();
            rowTable.add( new Label( entry.rank, skin, style) ).pad(10);
            rowTable.add( new Label( entry.displayName, skin, style) ).width(120).pad(10);
            rowTable.add( new Label( entry.score, skin, style) ).width(100).pad(10);

//            rowTable.getColor().a = 0;
//            rowTable.addAction(Actions.fadeIn(0.1f));
            board.add(rowTable);
            board.row();
        }
        board.pack();

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


    @Override
    public void leaderBoardIsUpdated() {
        Gdx.app.log("leader board window", "refresh");
        rebuild();
    }
}
