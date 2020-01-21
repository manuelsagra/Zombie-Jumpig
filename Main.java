import femto.Game;
import femto.State;
import femto.font.TIC80;
import femto.sound.Mixer;

class Main extends State {
    
    static final var cookie = new HiScore();
    
    public static void main(String[] args) {
        Mixer.init(8000);
        Game.run(TIC80.font(), new Start());
    }
    
}
