import femto.Game;
import femto.State;
import femto.mode.HiRes16Color;
import femto.palette.Pico8;
import femto.font.TIC80;
import femto.input.Button;

import images.LogoBig;
import images.Buildings;
import images.PressButtonBG;

import sounds.TitleMusic;

// TODO: Pig falling from

class Start extends State {
    // Constants
    final int HISCORE_LABEL_COLOR = 13;
    final int HISCORE_VALUE_COLOR = 3;
    final int PRESS_BUTTON_COLOR = 3;
    final int TEXT_FLASH_STEPS = 50;
    
    final int SCREEN_WIDTH = 220;
    final int SCREEN_HEIGHT = 176;
    
    final String TEXT_START = "PRESS ANY BUTTON";
    final String TEXT_HISCORE = "HIGH SCORE";
    
    static float BACKGROUND_SCROLL_STEPS = 12;
    
    // Properties
    int textFlash = 0;
    long hiscore;
    int backgroundPosition = 0;
    int backgroundStep = 0;
    
    HiRes16Color screen;
    
    LogoBig logo;
    Buildings buildings;
    PressButtonBG pressButtonBG;
    
    TitleMusic music;
    
    public void init() {
        screen = new HiRes16Color(Pico8.palette(), TIC80.font());
        
        // Cookie
        hiscore = Main.cookie.hiscore;
        
        // Images
        logo = new LogoBig();
        buildings = new Buildings();
        pressButtonBG = new PressButtonBG();
        
        // Sound
        music = new TitleMusic(0);
        music.play();
    }

    public void update() {
        checkInput();
        updateScreen();
    }

    public void shutdown(){ 
        screen = null;
    }
    
    // Input
    private void checkInput() {
        if (Button.A.justPressed() || Button.B.justPressed() || Button.C.justPressed()) {
            Game.changeState(new ZombieJumpig());   
        }
    }
    
    // Screen
    private void updateScreen() {
        // Backgound
        int repeatBackground = (int) Math.ceil((float) SCREEN_WIDTH / (float) buildings.width()) + 1;
        int x = backgroundPosition;
        for (int i = 0; i < repeatBackground; i++) {
            buildings.draw(screen, x, 0);
            x += buildings.width();
        }
        backgroundStep++;
        if (backgroundStep == BACKGROUND_SCROLL_STEPS) {
            backgroundPosition--;
            backgroundStep = 0;
            if (backgroundPosition == -buildings.width()) {
                backgroundPosition = 0;
            }
        }
        
        // Logo
        logo.draw(screen, (SCREEN_WIDTH - logo.width()) / 2, 16);
        
        // High Score
        screen.setTextColor(HISCORE_LABEL_COLOR);
        int w = screen.textWidth(TEXT_HISCORE);
        screen.setTextPosition((SCREEN_WIDTH - w) / 2, 110);
        screen.print(TEXT_HISCORE);
        
        screen.setTextColor(HISCORE_VALUE_COLOR);
        String hiscoreText = Utils.formatScore(hiscore);
        w = screen.textWidth(hiscoreText);
        screen.setTextPosition((SCREEN_WIDTH - w) / 2, 118);
        screen.print(hiscoreText);
        
        // Press any button flashing
        if (textFlash < TEXT_FLASH_STEPS) {
            pressButtonBG.draw(screen, 59, 152);
            
            screen.setTextColor(PRESS_BUTTON_COLOR);
            w = screen.textWidth(TEXT_START);
            screen.setTextPosition((SCREEN_WIDTH - w) / 2, 156);
            screen.print(TEXT_START);
        }
        textFlash++;
        if (textFlash == TEXT_FLASH_STEPS * 2) {
            textFlash = 0;
        }
        
        screen.flush();
    }
}