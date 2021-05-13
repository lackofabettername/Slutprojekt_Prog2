package game2_1.music;

import game2_1.Application;
import game2_1.WindowLogic;
import game2_1.events.*;
import processing.core.PConstants;
import processing.core.PGraphics;
import ui.*;
import utility.Bounds2;
import utility.Debug;
import utility.MathF;

import java.io.IOException;

public class BeatMapper implements WindowLogic, UIListener {
    private final Application window;

    private final UI ui;

    private MusicPlayer musicPlayer;
    private BeatHandler beats;
    private String songPath;

    private float PixelsPerSecond = 200;
    private float bpm = 60;
    private float bps;
    private float playbackRate = 1;

    private float uiHeight = 150;

    private float mouseX, mouseY;

    private transient long musicStartTime;
    private transient float musicTime;

    public BeatMapper(Application window) {
        this.window = window;

        //region UI
        MenuFramework menu = new MenuFramework("BeatMapperMenu", this, new Bounds2(0, 0, window.WindowW, uiHeight));

        menu.addMenuObject(new MenuNumberField("BPM", 0.5f, 18), 0);
        menu.addMenuObject(new MenuNumberField("StartOffset", 1, 18), 0);
        menu.addMenuObject(new MenuNumberField("Speed", 0.1f, 18), 0);
        menu.addMenuObject(new MenuNumberField("Pixels per Second", 10, 18), 0);


        MenuDropdownFramework dropdown = new MenuDropdownFramework("dropdown song selector");
        dropdown.addMenuObject(new MenuFileSelector("Song selector", "music", file -> file.toString().endsWith(".wav")));
        menu.addMenuObject(dropdown, 4, 0, 2, 1);

        menu.addMenuObject(new MenuButton("Start", 18), 0, 1, 2, 1);
        menu.addMenuObject(new MenuButton("Pause", 18), 2, 1, 2, 1);
        menu.addMenuObject(new MenuButton("Stop", 18), 4, 1, 2, 1);

        menu.addMenuObject(new MenuButton("Save", 18), 0, 2, 6, 1);

        menu.fitElements(3);
        dropdown.fitElements(0);

        dropdown.expandedBounds.h *= 6;
        ui = new UI(window, menu);

        ((MenuNumberField) ui.getMenuObject("Speed")).value = 1;
        ((MenuNumberField) ui.getMenuObject("BPM")).value = bpm;
        ((MenuNumberField) ui.getMenuObject("Pixels per Second")).value = PixelsPerSecond;
        //endregion

        bps = bpm / 60;
    }

    @Override
    public void render(PGraphics g) {
        g.background(0);
        g.fill(1);

        ui.onRender(g);

        if (musicPlayer != null) {
            float time = getTime();

            float h = 10;
            beats.forEach((type, queue) -> {
                float y = MathF.map(type + 0.5f, 0, beats.amountOfTypes(), uiHeight, g.height);

                //region Guides
                g.stroke(1f, 0.3f);
                g.strokeWeight(1);
                for (
                        float val = 1000 / bps,
                        x = getScreenCoordinate(((float) Math.round(time / val) - time / val) * val);
                        x < g.width;
                        x += getScreenCoordinate(val)
                ) {
                    g.line(g.width - x, y - h, g.width - x, y + h);
                }
                //endregion

                //region Beats
                g.stroke(0.5f);
                g.line(0, y, g.width, y);

                g.strokeWeight(2);

                g.stroke(1);
                for (BeatHandler.Beat beat : queue) {
                    float x = beat.timeStamp() - time;

                    if (x < 0) continue;

                    x = getScreenCoordinate(x);

                    g.line(g.width - x, y - h, g.width - x, y + h);
                }
                //endregion
            });

            //region Cursor
            float y = (float) (Math.floor((mouseY - uiHeight) / (g.height - uiHeight) * 3) + 0.5f) / 3;
            if (y >= 0 && y < beats.amountOfTypes()) {
                y = y * (g.height - uiHeight) + uiHeight;

                float x = getHoveredTimeStamp();

                //x += beats.startOffset / 1000f;

                g.text(Math.round(time) + "\n" + Math.round(x * 1000 + beats.startOffset), g.width / 2f, g.height / 2f);

                x -= time / 1000;

                x *= PixelsPerSecond;
                g.line(g.width - x, y - h, g.width - x, y + h);
            }
            //endregion
        }
    }

    private float getTime() {
        return musicPlayer.playing ?
                musicPlayer.getMicrosecondPosition() / 1000f + beats.startOffset :
                musicTime;
    }

    private float getHoveredTimeStamp() {
        float time = getTime();

        float x = (window.WindowW - mouseX) / PixelsPerSecond;
        x += (time) / 1000;

        x = Math.round(x * bps) / bps;

        return x;
    }

    private float getScreenCoordinate(float timeStamp) {
        return timeStamp / 1000 * PixelsPerSecond;
    }

    private void start() {
        musicPlayer = new MusicPlayer(songPath, playbackRate);
        musicPlayer.start(musicStartTime);
    }
    private void start(int offset) {
        pause();
        musicStartTime = MathF.clamp(musicStartTime + offset, 0, Long.MAX_VALUE);
        start();
    }

    private void pause() {
        musicTime = musicStartTime = musicPlayer.getMicrosecondPosition();
        musicPlayer.stop();
    }

    private void stop() {
        musicTime = musicStartTime;
        musicPlayer.stop();
    }

    private void setBPM(float bpm) {
        bpm = MathF.clamp(bpm, 10, Integer.MAX_VALUE);

        beats.bpm = bpm;
        ((MenuNumberField) ui.getMenuObject("BPM")).value = bpm;

        this.bpm = bpm;
        this.bps = bpm / 60;
    }

    //region Events
    @Override
    public void keyEvent(KeyEvent event) {
        if (ui.handleEvent(event))
            return;

        if (event.Type == KeyEventType.KeyPressed) {
            if (!event.Coded) {
                if (event.Key == ' ') {
                    if (musicPlayer.playing)
                        pause();
                    else
                        start();
                }
            } else {
                switch ((int) event.Key) {
                    case PConstants.LEFT -> {
                        if (musicPlayer.playing) {
                            pause();
                            start(500_000);
                        } else {
                            musicStartTime += 500_000;
                            musicTime += 500_000;
                        }
                    }
                    case PConstants.RIGHT -> {
                        if (musicPlayer.playing) {
                            pause();
                            start(-500_000);
                        } else {
                            musicStartTime -= 500_000;
                            musicTime -= 500_000;
                        }
                    }
                }
            }
            Debug.log((int) event.Key);
        }
    }

    @Override
    public void mouseEvent(MouseEvent event) {
        this.mouseX = event.MouseX;
        this.mouseY = event.MouseY;

        if (ui.handleEvent(event))
            return;

        if (event.Type == MouseEventType.MouseButtonPressed) {
            int y = (int) (Math.floor((mouseY - uiHeight) / (window.WindowH - uiHeight) * 3) + 0.5f);

            if (event.MouseButton == MouseButton.Left) {
                beats.addBeat((byte) y, (long) (getHoveredTimeStamp() * 1000), 1);
            } else if (event.MouseButton == MouseButton.Right) {
                beats.removeBeat((byte) y, (long) (getHoveredTimeStamp() * 1000));
            }
        }
    }

    @Override
    public void uiEvent(MenuObject caller) {
        switch (caller.name) {
            case "BPM" -> {
                try {
                    setBPM(((MenuNumberField) caller).value);
                } catch (NumberFormatException e) {
                    Debug.logError(e);
                }
            }
            case "StartOffset" -> {
                beats.startOffset = (int) ((MenuNumberField) caller).value;
                // ((MenuNumberField) ui.getMenuObject("StartOffset")).value = beats.startOffset;
            }
            case "Speed" -> {
                playbackRate = ((MenuNumberField) caller).value;
                if (musicPlayer != null)
                    musicPlayer.playbackRate = playbackRate;
            }
            case "Pixels per Second" -> {
                PixelsPerSecond = ((MenuNumberField) caller).value;
                PixelsPerSecond = MathF.clamp(PixelsPerSecond, ((MenuNumberField) caller).stepSize, Integer.MAX_VALUE);
                ((MenuNumberField) caller).value = PixelsPerSecond;
            }
            case "Start" -> start();
            case "Pause" -> pause();
            case "Stop" -> stop();
            case "Save" -> {
                try {
                    beats.save(songPath.replaceAll("wav", "txt"));
                } catch (IOException e) {
                    Debug.logError(e);
                }
            }
            default -> {
                if (caller.parent instanceof MenuFramework parent) {
                    if (parent.name.equals("Song selector")) {
                        songPath = "music/" + caller.name;
                        musicPlayer = new MusicPlayer(songPath, playbackRate);
                        beats = BeatHandler.load(songPath.replaceAll("wav", "txt"));

                        setBPM(beats.bpm);
                        ((MenuNumberField) ui.getMenuObject("StartOffset")).value = beats.startOffset;

                        //beats = new BeatHandler();

                        ((MenuDropdownFramework) parent.parent).collapsedText = new MenuText(caller.name, 14);
                    }
                }
            }
        }
    }
    //endregion
}
