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

import java.io.File;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class BeatMapper implements WindowLogic, UIListener {
    private final Application window;
    /**
     * Saves the hash of the beats when saved. If the hash changes,
     * this is set to null and marks a change which should be saved in case of crash.
     */
    private Integer saved;

    private final UI ui;

    private MusicPlayer musicPlayer;
    private BeatHandler beats;
    private String songPath;

    private float pixelsPerSecond;
    private float bpm = 60;
    private float bps;
    private float playbackRate = 1;
    private int subBeats = 1;

    private final float uiHeight = 150;

    private float mouseX, mouseY;

    private long musicStartTime;
    private float musicTime;
    private final int musicEditorOffsetPixels = 150;

    public BeatMapper(Application window) {
        this.window = window;

        //region UI
        MenuFramework menu = new MenuFramework("BeatMapperMenu", this, new Bounds2(0, 0, window.WindowW, uiHeight));

        menu.addMenuObject(new MenuNumberField("BPM", 0.5f, 18), 0);
        menu.addMenuObject(new MenuNumberField("StartOffset", 1, 18), 0);
        menu.addMenuObject(new MenuNumberField("SubBeats", null, 1, 0.1f, 18), 0);
        menu.addMenuObject(new MenuNumberField("Speed", null, 0.01f, 0.3f, 18), 0);
        menu.addMenuObject(new MenuNumberField("Pixels per Second", 10, 18), 0);


        MenuDropdownFramework dropdown = new MenuDropdownFramework("dropdown song selector");
        dropdown.addMenuObject(new MenuFileSelector("Song selector", "music", File::isDirectory));
        menu.addMenuObject(dropdown, 5, 0, 3, 1);

        MenuFramework temp = new MenuFramework("Holder");
        temp.addMenuObject(new MenuButton("Start", 18), 0);
        temp.addMenuObject(new MenuButton("Pause", 18), 0);
        temp.addMenuObject(new MenuButton("Stop", 18), 0);

        temp.addMenuObject(new MenuButton("Save", 18), 0, 1, 3, 1);
        menu.addMenuObject(temp, 0, 1, 8, 2);


        menu.fitElements(3);
        temp.fitElements(3, 0);
        dropdown.fitElements(0);


        dropdown.expandedBounds.h *= 6;
        ui = new UI(window, menu);

        pixelsPerSecond = (window.WindowW - musicEditorOffsetPixels) * bps / 10;
        ((MenuNumberField) ui.getMenuObject("BPM")).value = bpm;
        ((MenuNumberField) ui.getMenuObject("SubBeats")).value = subBeats;
        ((MenuNumberField) ui.getMenuObject("Speed")).value = 1;
        ((MenuNumberField) ui.getMenuObject("Pixels per Second")).value = pixelsPerSecond;
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

            g.strokeWeight(1);
            g.stroke(1, 0.4f);
            g.line(window.WindowW - musicEditorOffsetPixels, uiHeight, window.WindowW - musicEditorOffsetPixels, window.WindowH);

            g.translate(-musicEditorOffsetPixels, 0);

            float h = 10;
            beats.forEach((type, beats) -> {
                float y = MathF.map(type + 0.5f, 0, this.beats.amountOfTypes(), uiHeight, g.height);

                //region Guides
                g.stroke(1f, 0.3f);
                g.strokeWeight(1);
                for (
                        float val = 1000 / bps,
                        x = getScreenCoordinate(((float) Math.round(time / val) - time / val) * val);
                        x < g.width;
                        x += getScreenCoordinate(val)) {
                    if (x < 0)
                        continue;
                    g.line(g.width - x, y - h, g.width - x, y + h);
                }
                //endregion

                //region Beats
                g.stroke(0.5f);
                g.line(0, y, g.width, y);


                for (BeatHandler.Beat beat : beats) {
                    float x = beat.timeStamp() - time;

                    if (x < -musicEditorOffsetPixels) continue;
                    if (x < 0) {
                        g.strokeWeight(1);
                        g.stroke((1 + x / musicEditorOffsetPixels) * 0.6f);
                    } else {
                        g.strokeWeight(2);
                        g.stroke(1);
                    }

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

                x *= pixelsPerSecond;

                g.stroke(1, 0.5f);
                g.line(g.width - x, y - h, g.width - x, y + h);
            }
            //endregion
        }
    }

    /**
     * @return songtime in milliseconds
     */
    private float getTime() {
        return (musicPlayer.playing() ?
                musicPlayer.getMicrosecondPosition() :
                musicTime
        ) / 1000f + beats.startOffset;
    }

    /**
     * @return timestamp in seconds
     */
    private float getHoveredTimeStamp() {
        float time = getTime() / 1000;

        float x = (window.WindowW - mouseX - musicEditorOffsetPixels);
        x /= pixelsPerSecond;
        x += time;

        float snap = bps * subBeats;
        x = Math.round(x * snap) / snap; //snap to closest beat

        return x;
    }

    /**
     * @param timeStamp in milliseconds
     * @return x coordinate in pixels
     */
    private float getScreenCoordinate(float timeStamp) {
        return timeStamp / 1000 * pixelsPerSecond;
    }

    private void start() {
        //WWif (musicPlayer.playing)
        musicPlayer.stop();

        musicPlayer = new MusicPlayer(songPath + "song.wav", playbackRate);
        musicPlayer.start(musicStartTime);
    }

    /**
     * @param offset microsecond offset
     */
    private void start(long offset) {
        //if (musicPlayer.playing)
        musicPlayer.stop();

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

    /**
     * @param amount microseconds skipped
     */
    private void skip(long amount) {
        if (musicPlayer.playing()) {
            pause();
            start(amount);
        } else {
            musicStartTime = MathF.clamp(musicStartTime + amount, (long) (-bps * 1_000_000), Long.MAX_VALUE);
            musicTime = MathF.clamp(musicTime + amount, (long) (-bps * 1_000_000), Long.MAX_VALUE);
        }
    }

    private void save(boolean auto) {
        try {
            String time = new Date().toInstant().truncatedTo(ChronoUnit.SECONDS)
                    .toString().replace("T", " ").replace("Z", "");

            Debug.log(time);
            beats.save(songPath + "beats" + (auto ? " AUTOSAVE " + time.replace(":", ".") : "") + ".txt");
            Debug.log("Saved " + time.replaceAll(".* ", ""));

            saved = beats.hashCode();
        } catch (IOException e) {
            Debug.logError(e);
        }
    }
    //region Events
    @Override
    public void onKeyEvent(KeyEvent event) {
        if (ui.handleEvent(event))
            return;

        if (event.Type == KeyEventType.KeyPressed) {
            if (!event.Coded) {
                if (event.Key == ' ') {
                    if (musicPlayer.playing())
                        stop();
                    else
                        start();
                }
            } else {
                switch ((int) event.Key) {
                    case PConstants.LEFT -> skip(500_000);
                    case PConstants.RIGHT -> skip(-500_500);
                }
            }
        }

        if (beats != null && saved != null && saved != beats.hashCode())
            saved = null;
    }

    @Override
    public void onMouseEvent(MouseEvent event) {
        this.mouseX = event.MouseX;
        this.mouseY = event.MouseY;

        if (ui.handleEvent(event))
            return;

        if (event.Type == MouseEventType.MouseButtonPressed) {
            int y = (int) (Math.floor((mouseY - uiHeight) / (window.WindowH - uiHeight) * 3) + 0.5f);
            if (event.mouseButton() == MouseEvent.LeftMouseButton) {
                beats.addBeat((byte) y, (long) (getHoveredTimeStamp() * 1000), 1);
            } else if (event.mouseButton() == MouseEvent.RightMouseButton) {
                beats.removeBeat((byte) y, (long) (getHoveredTimeStamp() * 1000));
            }
        } else if (event.Type == MouseEventType.MouseWheel) {
            skip(event.scrollWheel() * -100_000L);
        }

        if (beats != null && saved != null && saved != beats.hashCode())
            saved = null;
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
            case "StartOffset" -> beats.startOffset = (int) ((MenuNumberField) caller).value;
            case "SubBeats" -> {
                subBeats = (int) ((MenuNumberField) caller).value;
                subBeats = MathF.clamp(subBeats, 1, 16);
                ((MenuNumberField) caller).value = subBeats;
            }
            case "Speed" -> {
                playbackRate = ((MenuNumberField) caller).value;
                if (musicPlayer != null)
                    musicPlayer.playbackRate = playbackRate;
            }
            case "Pixels per Second" -> {
                pixelsPerSecond = ((MenuNumberField) caller).value;
                pixelsPerSecond = MathF.clamp(pixelsPerSecond, ((MenuNumberField) caller).stepSize, Integer.MAX_VALUE);
                ((MenuNumberField) caller).value = pixelsPerSecond;
            }
            case "Start" -> start();
            case "Pause" -> pause();
            case "Stop" -> stop();
            case "Save" -> save(false);
            default -> {
                if (caller.parent instanceof MenuObject && ((MenuObject) caller.parent).name.equals("Song selector")) {
                    if (musicPlayer != null)
                        stop();

                    songPath = "music/" + caller.name + "/";
                    musicPlayer = new MusicPlayer(songPath + "song.wav", playbackRate);
                    beats = BeatHandler.load(songPath + "beats.txt");
                    saved = beats.hashCode();

                    setBPM(beats.bpm);
                    pixelsPerSecond = (window.WindowW - musicEditorOffsetPixels) * bps / 5; //5 beats visible on screen
                    ((MenuNumberField) ui.getMenuObject("Pixels per Second")).value = pixelsPerSecond;

                    ((MenuNumberField) ui.getMenuObject("StartOffset")).value = beats.startOffset;
                    musicTime = 0;
                    musicStartTime = 0;

                    //beats = new BeatHandler();

                    ((MenuDropdownFramework) ((MenuObject) caller.parent).parent).collapsedText = new MenuText(caller.name, 14);
                }
            }
        }

        if (beats != null && saved != null && saved != beats.hashCode())
            saved = null;
    }
    //endregion


    @Override
    public void onExit() {
        if (musicPlayer != null)
            musicPlayer.stop();
        if (beats != null && saved == null)
            save(true);
    }
}
