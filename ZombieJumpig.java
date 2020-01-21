import femto.mode.HiRes16Color;
import femto.Game;
import femto.State;
import femto.input.Button;
import femto.palette.Pico8;
import femto.font.TIC80;

import entities.Entity;

import sprites.Pig;
import sprites.Dust;
import sprites.Bat;
import sprites.Zombie;
import sprites.Coffee;
import sprites.Coin;

import sprites.Points100;
import sprites.Points200;
import sprites.Points300;
import sprites.Points400;
import sprites.Points1000;

import images.MiniCoffee;
import images.Buildings;
import images.LeftWall;
import images.RightWall;
import images.Logo;

import sounds.BonusSound;
import sounds.CoffeeSound;
import sounds.CoinSound;
import sounds.GameOverSound;
import sounds.GrowlSound;
import sounds.HitSound;
import sounds.JumpSound;

// TODO: Sounds: Enemy destroyed

class ZombieJumpig extends State {
    // Colours
    final int SKY_COLOR = 12;
    final int BUILDING_COLOR = 5;
    final int LABELS_COLOR = 9;
    final int VALUES_COLOR = 3;
    final int SIDEBAR_COLOR = 0;
    final int ENERGY_BORDER_COLOR = 3;
    final int ENERGY_BAR_COLOR = 9; 
    
    // Sizes
    final int SCREEN_WIDTH = 220;
    final int SCREEN_HEIGHT = 176;
    
    final int SKY_WIDTH = 136;
    final int BUILDING_WIDTH = 12;
    
    final int TEXT_X = 167;
    final int HISCORE_LABEL_Y = 65;
    final int HISCORE_VALUE_Y = 73;
    final int SCORE_LABEL_Y = 87;
    final int SCORE_VALUE_Y = 95;
    final int COFFEES_LABEL_Y = 129;
    final int COFFEES_VALUE_Y = 137;
    final int ENERGY_LABEL_Y = 154;
    final int ENERGY_Y = 162;
    
    final float MAX_ENERGY = 43;
    
    // Player
    Entity player;
    float playerEnergy = MAX_ENERGY;
    int[] JUMP_TABLE = {0, 2, 4, 6, 8, 9, 11, 13, 14, 16, 17, 19, 20, 22, 23, 25, 26, 27, 28, 30, 31, 32, 33, 34, 35, 36, 37, 37, 38, 39, 40, 40, 41, 41, 42, 42, 43, 43, 44, 44, 44, 44, 45, 45, 45, 45, 45, 45, 45, 45, 44, 44, 44, 44, 43, 43, 42, 42, 41, 41};
    float playerSpeedDown = 1.0;
    
    boolean isJumping = false;
    boolean isAttacking = false;
    boolean isFacingLeft = false;
    
    final float ENERGY_PER_JUMP = 0.5;
    final float ENERGY_HIT_PER_FRAME = 0.4;
    final float ENERGY_BONUS = 4;
    final int PLAYER_FLASHING_STEPS = 40;
    
    // Enemies
    Entity[] enemies;
    float zombieSpeedUp = 0.2;
    final int BAT_MOVEMENT = 10;
    final int BAT_MOVEMENT_STEPS = 200;
    
    // Items
    Entity[] items;
    final int MAX_COFFEES = 5;
    
    // Game
    boolean isGameOver = false;
    boolean isPaused = false;
    boolean isReady = true;
    
    long score;
    long hiscore;
    int coffees = MAX_COFFEES;
    int POINTS_ANIMATION_STEPS = 100;
    HiRes16Color screen;
    boolean[] scoreBonus = new boolean[3];
    final long SCORE_BONUS = 2000;
    
    // Images
    Pig hero; 
    Dust dust;
    
    Bat enemy0;
    Bat enemy1;
    Zombie enemy2;
    
    Coffee coffee;
    Coin coin0;
    Coin coin1;
    MiniCoffee miniCoffee;
    
    Points100 points1001;
    Points100 points1002;
    Points200 points2001;
    Points200 points2002;
    Points300 points300;
    Points400 points400;
    Points1000 points1000;
    
    Buildings buildings;
    LeftWall leftWall;
    RightWall rightWall;
    
    Logo logo;
    
    // Sounds
    BonusSound bonusSound;
    CoffeeSound coffeeSound;
    CoinSound coinSound;
    GameOverSound gameOverSound;
    GrowlSound growlSound;
    HitSound hitSound;
    JumpSound jumpSound;

    public void init() {
        // Sprites
        hero = new Pig();
        hero.wall();
        
        dust = new Dust();
        dust.animation();
        
        enemy0 = new Bat();
        enemy0.flying();
        enemy1 = new Bat();
        enemy1.flying();
        enemy2 = new Zombie();
        enemy2.crawling();
        
        coffee = new Coffee();
        coffee.hot();
        
        coin0 = new Coin();
        coin0.spin();
        
        coin1 = new Coin();
        coin1.spin();
        
        points1001 = new Points100();
        points1001.points();
        points1002 = new Points100();
        points1002.points();
        points2001 = new Points200();
        points2001.points();
        points2002 = new Points200();
        points2002.points();
        points300 = new Points300();
        points300.points();
        points400 = new Points400();
        points400.points();
        points1000 = new Points1000();
        points1000.points();
        
        // Images
        miniCoffee = new MiniCoffee();
        buildings = new Buildings();
        leftWall = new LeftWall();
        rightWall = new RightWall();
        logo = new Logo();
        
        // Entities
        player = new Entity();
        player.initPlayer();
        
        enemies = new Entity[3];
        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Entity();
        }
        
        items = new Entity[3];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Entity();
        }
        
        respawnEntities();
    
        // HiScore
        hiscore = Main.cookie.hiscore;
        
        // Screen
        screen = new HiRes16Color(Pico8.palette(), TIC80.font());
        setupScreen();
        redrawBackground();
        updateScore();
        updateCoffees();
        updateEnergy();

        // Sounds
        coffeeSound = new CoffeeSound(0);
        
        coinSound = new CoinSound(1);
        gameOverSound = new GameOverSound(1);
        
        growlSound = new GrowlSound(2);
        jumpSound = new JumpSound(2);
        
        hitSound = new HitSound(3);
        bonusSound = new BonusSound(3);
    }
    
    public void shutdown() {
        screen = null;
    }
    
    public void update() {
        if (!isReady) {
            showReady();
            
            if (Button.A.justPressed() || Button.B.justPressed() || Button.C.justPressed()) {
                isReady = true;
            }
        } else {
            if (!isGameOver) {
                if (Button.C.justPressed()) {
                    isPaused = !isPaused;
                }
                if (isPaused) {
                    showPause();
                } else {
                    checkInput();
                    redrawBackground();
                    updateEntities();
                }
            } else {
                showGameOver();
                
                Main.cookie.hiscore = hiscore;
                Main.cookie.saveCookie();
                
                if (Button.A.justPressed() || Button.C.justPressed()) {
                    Game.changeState(new ZombieJumpig()); 
                } else if (Button.B.justPressed()) {
                    Game.changeState(new Start()); 
                }
            }
        }
        screen.flush();
    }
    
    private void checkInput() {
        if (!isJumping && !isAttacking && Button.A.justPressed()) {
            isJumping = true;
            hero.jump();
            jumpSound.play();
        }
        
        if (!isAttacking && coffees > 0 && Button.B.justPressed()) {
            isJumping = false;
            isAttacking = true;
            player.ty = player.y;
            hero.attack();
            coffees--;
            updateCoffees();
            growlSound.play();
            player.flashing = 0;
        }
    }
    
    // Screen
    private void setupScreen() {
        screen.clear(SIDEBAR_COLOR);
        
        // Walls
        leftWall.draw(screen, 0, 0);
        rightWall.draw(screen, BUILDING_WIDTH + SKY_WIDTH, 0);
        
        // Sidebar
        logo.draw(screen, SCREEN_WIDTH - 53, 7);
        
        screen.setTextColor(LABELS_COLOR);
        screen.setTextPosition(TEXT_X, HISCORE_LABEL_Y);
        screen.print("HI-SCORE");
        
        screen.setTextPosition(TEXT_X, SCORE_LABEL_Y);
        screen.print("SCORE");
        
        screen.setTextPosition(TEXT_X, COFFEES_LABEL_Y);
        screen.print("COFFEES");
        
        screen.setTextPosition(TEXT_X, ENERGY_LABEL_Y);
        screen.print("ENERGY");
        
        screen.setTextColor(VALUES_COLOR);
        screen.setTextPosition(TEXT_X, HISCORE_VALUE_Y);
        screen.print(Utils.formatScore(hiscore));
        
        // Energy bar
        screen.drawHLine(TEXT_X + 1, ENERGY_Y, 45, ENERGY_BORDER_COLOR);
        screen.drawHLine(TEXT_X + 1, ENERGY_Y + 6, 45, ENERGY_BORDER_COLOR);
        screen.drawVLine(TEXT_X, ENERGY_Y + 1, 5, ENERGY_BORDER_COLOR);
        screen.drawVLine(TEXT_X + 46, ENERGY_Y + 1, 5, ENERGY_BORDER_COLOR);
    }
    
    private void updateScore() {
        screen.fillRect(TEXT_X, SCORE_VALUE_Y, 60, 8, SIDEBAR_COLOR);
        screen.setTextColor(VALUES_COLOR);
        screen.setTextPosition(TEXT_X, SCORE_VALUE_Y);
        screen.print(Utils.formatScore(score));
        
        if (score > hiscore) {
            hiscore = score;
            
            screen.fillRect(TEXT_X, HISCORE_VALUE_Y, 60, 8, SIDEBAR_COLOR);
            screen.setTextColor(VALUES_COLOR);
            screen.setTextPosition(TEXT_X, HISCORE_VALUE_Y);
            screen.print(Utils.formatScore(hiscore));
        }
    }
    
    private void updateCoffees() {
        screen.fillRect(TEXT_X, COFFEES_VALUE_Y, 60, 8, SIDEBAR_COLOR);
        int x = TEXT_X;
        for (int i = 0; i < coffees; i++) {
            miniCoffee.draw(screen, x, COFFEES_VALUE_Y);
            x += 9;
        }
    }
    
    private void updateEnergy() {
        if (playerEnergy <= 0) {
            playerEnergy = 0;
            isGameOver = true;
            gameOverSound.play();
        } else if (playerEnergy > MAX_ENERGY) {
            playerEnergy = MAX_ENERGY;
        }
        
        screen.fillRect(TEXT_X + 2, ENERGY_Y + 2, 44, 3, SIDEBAR_COLOR);
        screen.fillRect(TEXT_X + 2, ENERGY_Y + 2, (int) playerEnergy, 3, ENERGY_BAR_COLOR);
    }
    
    private void redrawBackground() {
        buildings.draw(screen, BUILDING_WIDTH, 0);
    }
    
    private void showReady() {
        screen.fillRect(36, 68, 88, 40, SIDEBAR_COLOR);
        screen.setTextColor(VALUES_COLOR);
        screen.setTextPosition(62, 85);
        screen.print("READY?");
    }
    
    private void showPause() {
        screen.fillRect(36, 68, 88, 40, SIDEBAR_COLOR);
        screen.setTextColor(VALUES_COLOR);
        screen.setTextPosition(62, 85);
        screen.print("PAUSED");
    }
    
    private void showGameOver() {
        screen.fillRect(36, 68, 88, 40, SIDEBAR_COLOR);
        screen.setTextColor(LABELS_COLOR);
        screen.setTextPosition(54, 85);
        screen.print("GAME OVER");
    }
    
    // Entities
    private void updateEntities() {
        // Update player movement
        player.y = (int) Math.round(player.ty);
        if (isJumping) {
            player.y -= JUMP_TABLE[player.step++];
            if (player.y < 0) {
                player.y = 0;
            }
            
            if (isFacingLeft) {
                player.x -= 2;
            } else {
                player.x += 2;
            }
        } else if (isAttacking) {
            if (isFacingLeft) {
                player.x -= 4;
            } else {
                player.x += 4;
            }
            player.step++;
        } else {
            player.ty += playerSpeedDown;
            player.y = (int) Math.round(player.ty);
        }
        
        if (player.y >= SCREEN_HEIGHT) {
            isGameOver = true;
            gameOverSound.play();
        }
        
        // Check if actions are done
        if (isJumping && player.step == JUMP_TABLE.length) {
            hero.wall();
            isJumping = false;
            isFacingLeft = !isFacingLeft;
            player.ty = player.y;
            player.step = 0;
            playerEnergy -= ENERGY_PER_JUMP;
            updateEnergy();
            
            respawnEntities();
        }
        
        if (isAttacking && (
            (!isFacingLeft && player.x >= 132) || 
            (isFacingLeft && player.x <= 12)
            )
        ) {
            if (player.x < 12) {
                player.x = 12;
            } else if (player.x > 132) {
                player.x = 132;
            }
            
            hero.wall();
            isAttacking = false;
            isFacingLeft = !isFacingLeft;
            player.step = 0;
            
            respawnEntities();
            
            if (scoreBonus[0] && scoreBonus[1] && scoreBonus[2]) {
                score += SCORE_BONUS;
                updateScore();
                bonusSound.play();
            }
            
            scoreBonus[0] = false;
            scoreBonus[1] = false;
            scoreBonus[2] = false;
        }
        
        // Draw hero
        if (!isJumping && !isAttacking) {
            dust.x = player.x;
            dust.y = player.y - 14;
            dust.setMirrored(isFacingLeft);
            dust.draw(screen);
        }
        
        hero.x = player.x;
        hero.y = player.y;
        hero.setMirrored(isFacingLeft);
        if (player.flashing > 0) {
            player.flashing--;
            if (isJumping) {
                hero.jumphit();
            } else {
                hero.wallhit();
            }
        }
        hero.draw(screen);
        
        // Enemies
        if (enemies[2].isEnabled) {
            enemies[2].ty -= zombieSpeedUp;
            enemies[2].y = (int) enemies[2].ty;
            if (enemies[2].y < -20) {
                enemies[2].initZombie();
            }
        }
        for (int i = 0; i < 2; i++) {
            if (enemies[i].isEnabled) {
                enemies[i].y = (int) (enemies[i].ty + Math.sin(enemies[i].step++ * 2 * 3.1415926 / BAT_MOVEMENT_STEPS) * BAT_MOVEMENT);
                if (enemies[i].step == BAT_MOVEMENT_STEPS) {
                    enemies[i].step = 0;
                }
            }
        }
        
        for (int i = 0; i < enemies.length; i++) {
            Entity e = enemies[i];
            switch(i) {
                case 0:
                    if (e.isEnabled) {
                        enemy0.x = e.x;
                        enemy0.y = e.y;
                        enemy0.setMirrored(enemy0.x > 70);
                        enemy0.draw(screen);
                    } else if (e.step > 0) {
                        e.step--;
                        points2001.x = e.x;
                        points2001.y = e.y;
                        points2001.draw(screen);
                    }
                    break;
                    
                case 1:
                    if (e.isEnabled) {
                        enemy1.x = e.x;
                        enemy1.y = e.y;
                        enemy1.setMirrored(enemy1.x > 70);
                        enemy1.draw(screen);
                    } else if (e.step > 0) {
                        e.step--;
                        points2002.x = e.x;
                        points2002.y = e.y;
                        points2002.draw(screen);
                    }
                    break;
                
                case 2:
                    if (e.isEnabled) {
                        enemy2.x = e.x;
                        enemy2.y = e.y;
                        enemy2.setMirrored(enemy2.x != 12);
                        enemy2.draw(screen);
                    } else if (e.step > 0) {
                        e.step--;
                        points400.x = e.x;
                        points400.y = e.y;
                        points400.draw(screen);
                    }
                    break;
            }
        }
        
        // Items
        for (int i = 0; i < items.length; i++) {
            Entity e = items[i];
            switch(i) {
                case 0:
                    if (e.isEnabled) {
                        coin0.x = e.x;
                        coin0.y = e.y;
                        coin0.draw(screen);
                    } else if (e.step > 0) {
                        e.step--;
                        points1001.x = e.x - 4;
                        points1001.y = e.y - 4;
                        points1001.draw(screen);
                    }
                    break;
                    
                case 1:
                    if (e.isEnabled) {
                        coin1.x = e.x;
                        coin1.y = e.y;
                        coin1.draw(screen);
                    } else if (e.step > 0) {
                        e.step--;
                        points1002.x = e.x - 4;
                        points1002.y = e.y - 4;
                        points1002.draw(screen);
                    }
                    break;
                
                case 2:
                    if (e.isEnabled) {
                        coffee.x = e.x;
                        coffee.y = e.y;
                        coffee.draw(screen);
                    } else if (e.step > 0) {
                        e.step--;
                        if (e.points != 1000) {
                            points300.x = e.x - 2;
                            points300.y = e.y - 2;
                            points300.draw(screen);
                        } else {
                            points1000.x = e.x - 2;
                            points1000.y = e.y - 2;
                            points1000.draw(screen);
                        }
                    }
                    
                    break;
            }
        }
        
        // Check collisions with enemies and items
        if (isAttacking) { // Enemies are destroyed, get items
            for (int i = 0; i < enemies.length; i++) {
                if (enemies[i].isEnabled && enemies[i].hasCollisioned(player)) {
                    enemies[i].isEnabled = false;
                    enemies[i].step = POINTS_ANIMATION_STEPS;
                    
                    score += enemies[i].points;
                    updateScore();
                    
                    playerEnergy += ENERGY_BONUS;
                    updateEnergy();
                    
                    scoreBonus[i] = true;
                    
                    hitSound.play();
                }
            }
        } else { // Zombies hurt you, get items
            for (int i = 0; i < enemies.length; i++) {
                if (enemies[i].isEnabled && enemies[i].hasCollisioned(player)) {
                    playerEnergy -= ENERGY_HIT_PER_FRAME;
                    updateEnergy();
                    
                    player.flashing = PLAYER_FLASHING_STEPS;
                }
            } 
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i].isEnabled && items[i].hasCollisioned(player)) {
                items[i].isEnabled = false;
                items[i].step = POINTS_ANIMATION_STEPS;
                
                if (i == 2) { // Coffee
                    if (coffees < MAX_COFFEES) {
                        coffees++;
                        updateCoffees();
                    } else {
                        items[i].points = 1000;
                    }
                    score += items[i].points;
                    coffeeSound.play();
                } else {
                    coinSound.play();
                }
                
                score += items[i].points;
                updateScore();
            } 
        }
    }
    
    private void respawnEntities() {
        // Enemies
        if (!enemies[0].isEnabled && enemies[0].step <= 0) {
            do {
                enemies[0].initBat();
            } while (isEntityCollisioned(enemies[0]));
            enemies[0].isEnabled = true;
        }
        if (!enemies[1].isEnabled && enemies[1].step <= 0) {
            do {
                enemies[1].initBat();
            } while (isEntityCollisioned(enemies[1]));
            enemies[1].isEnabled = true;
        }
        if (!enemies[2].isEnabled && enemies[2].step <= 0) {
            do {
                enemies[2].initZombie();
            } while (isEntityCollisioned(enemies[2]));
            enemies[2].isEnabled = true;
        }
        
        // Items
        if (!items[0].isEnabled && items[0].step <= 0) {
            do {
                items[0].initCoin();
            } while (isEntityCollisioned(items[0]));
            items[0].isEnabled = true;
        }
        if (!items[1].isEnabled && items[1].step <= 0) {
            do {
                items[1].initCoin();
            } while (isEntityCollisioned(items[1]));
            items[1].isEnabled = true;
        }
        if (!items[2].isEnabled && items[2].step <= 0) {
            do {
                items[2].initCoin();
            } while (isEntityCollisioned(items[2]));
            items[2].isEnabled = true;
        }
    }
    
    private boolean isEntityCollisioned(Entity entity) {
        // Player
        if (entity.hasCollisioned(player)) {
            return true;
        }
        
        // Items
        for (int i = 0; i < items.length; i++) {
            if (items[i].isEnabled && items[i].hasCollisioned(entity)) {
                return true;
            }
        }
        
        // Enemies
        for (int i = 0; i < enemies.length; i++) {
            if (enemies[i].isEnabled && enemies[i].hasCollisioned(entity)) {
                return true;
            }
        }
        
        return false;
    }
}
