package byow.Core;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.Serializable;

public class Menu implements Serializable {
    private int width;
    private int height;
    private final String titleContent = "CS 61B:The Game";
    private final String[] menuContents = {"New Game (N)", "Load Game (L)", "Quit (Q)"};

    public Menu(int width, int height) {
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMenu() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2.0, this.height * (2.0 / 3.0), titleContent);
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        int distance = 0;
        for (String menuContent : menuContents) {
            StdDraw.text(this.width / 2.0, this.height / 2.0 - distance, menuContent);
            distance += 2;
        }
        StdDraw.show();
    }
    public void printSeed() {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2.0, this.height / 2.0, "seed :");
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.show();
    }
    public void printSeed(String seed) {
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.white);
        Font font = new Font("Monaco", Font.BOLD, 50);
        StdDraw.setFont(font);
        StdDraw.text(this.width / 2.0, this.height / 2.0 , "seed :" + seed);
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.show();
    }
}
