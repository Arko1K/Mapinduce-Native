import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.*;

import com.google.gson.Gson;

public class Main {

    static int BOUND_X = 500;
    static int BOUND_Y = 400;
    static int SCAN_REGION = 20; // Used to divide the map into tiles.
    static int SCAN_RANGE = 40; // Used to look ahead and around while tracing a line.
    static double COLLINEARITY_TOLERANCE = 0.01;
    static int LINEARITY_REGION = 10;

    static int WHITE = Color.white.getRGB();

    static int startX, startY;

    // static int[][] reds;
    static ArrayList<Integer[]> redPixList;
    static boolean[][] tilesScanned;
    static boolean[][] pixelsScanned;
    // static int[][] regionPixelsScanned;
    static ArrayList<Integer[]> redRegionPixList;
    static Robot robo;
    static BufferedImage shot;
    static int width, height;

    static ArrayList<Integer> red;
    static ArrayList<Integer> green;
    static ArrayList<Integer> blue;


//    public static void main(String[] args) {
//        Chori.getListingDetails(Chori.sampleListingId);
//        Chori.getContactDetails(Chori.sampleListingId);
//    }


    public static void main(String[] args) {
        try {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            // reds = new int[d.width][d.height];
            redPixList = new ArrayList<Integer[]>();
            tilesScanned = new boolean[(int)Math.ceil(d.width / SCAN_REGION)][(int)Math.ceil(d.height / SCAN_REGION)];
            width = d.width;
            height = d.height;
            pixelsScanned = new boolean[width][height];
            // regionPixelsScanned = new int[width][height];
            redRegionPixList = new ArrayList<Integer[]>();
            robo = new Robot();
            shot = robo.createScreenCapture(new Rectangle(d));
            int X = d.width / 2 + BOUND_X, Y = BOUND_Y, upX = d.width - BOUND_X, upY = d.height - BOUND_Y;
            boolean found = false;
            outerloop:
            while(Y <= upY) {
                while(X <= upX) {
                    // robo.mouseMove(X, Y);
                    if (isRedMatch(new Color(shot.getRGB(X, Y)))) {
                        found = true;
                        break outerloop;
                    }
                    X++;
                }
                X = d.width / 2 + BOUND_X;
                Y++;
            }

            // ImageIO.write(shot, "PNG", new File("/home/arko1k/snap_linetrace_shot.png"));

            if(found) {
//                ArrayList<Integer[]> region = lineScan(shot, X - 100, Y - 100, X + 500, Y + 500);
//                BufferedImage biReg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
//                int pixCountReg = region.size();
//                for(int i = 0; i < pixCountReg; i++) {
//                    Integer[] coor = region.get(i);
//                    biReg.setRGB(coor[0], coor[1], WHITE);
//                }
//                ImageIO.write(biReg, "PNG", new File("/home/arko1k/snap_region.png"));

                startX = X;
                startY = Y;
                pixelsScanned[X][Y] = true;
                redPixList.add(new Integer[]{X, Y});
                getRegion(X, Y);
                selectNext(X - 1, Y, X, Y);

                // minimalize();

                BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
                int pixCount = redPixList.size();
                for (int i = 0; i < pixCount; i++) {
                    Integer[] coor = redPixList.get(i);
                    bi.setRGB(coor[0], coor[1], WHITE);
                }
                ImageIO.write(bi, "PNG", new File("/home/arko1k/snap_linetrace.png"));
                System.out.println("Pixel count: " + pixCount);
                System.out.println(new Gson().toJson(redPixList));

//                ArrayList<Integer[]> smoothPix = smoothen(redPixList);
//                BufferedImage biMin = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
//                boolean buttonSwitch = false;
//                int pixCountMin = smoothPix.size();
//                for(int i = 0; i < pixCountMin; i++) {
//                    Integer[] coor = smoothPix.get(i);
//                    biMin.setRGB(coor[0], coor[1], WHITE);
//                    robo.mouseMove(coor[0], coor[1]);
//                    Thread.sleep(200);
//                    buttonSwitch = !buttonSwitch;
//                    if(buttonSwitch) {
//                        robo.mousePress(InputEvent.BUTTON1_MASK);
//                        robo.mouseRelease(InputEvent.BUTTON1_MASK);
//                    }
//                    else {
//                        robo.mousePress(InputEvent.BUTTON3_MASK);
//                        robo.mouseRelease(InputEvent.BUTTON3_MASK);
//                    }
//                }
//                Thread.sleep(2000);
//                robo.keyPress(KeyEvent.VK_ENTER);
//                robo.keyRelease(KeyEvent.VK_ENTER);
//                ImageIO.write(biMin, "PNG", new File("/home/arko1k/snap_linetrace_smooth.png"));
//                System.out.println("Pixel count: " + pixCountMin);

                BufferedImage biReg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
                int redRegPixCount = redRegionPixList.size();
                for (int i = 0; i < redRegPixCount; i++) {
                    Integer[] coor = redRegionPixList.get(i);
                    biReg.setRGB(coor[0], coor[1], WHITE);
                }
                ImageIO.write(biReg, "PNG", new File("/home/arko1k/snap_region.png"));
                System.out.println(new Gson().toJson(redRegionPixList));
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean isRedMatch(Color c) {
        // #DB473A
        // 219,71,58

    /*  --- Red ---
        Min: 191
        Max: 240
        Mean: 226.98671446646983
        Number of values: 4742
        Number of distinct values: 49
        Mode: 229. Frequency: 270
        Median: 227.0

        --- Green ---
        Min: 68
        Max: 169
        Mean: 120.27773091522565
        Number of values: 4742
        Number of distinct values: 102
        Mode: 166. Frequency: 70
        Median: 121.0

        --- Blue ---
        Min: 55
        Max: 169
        Mean: 110.83256010122311
        Number of values: 4742
        Number of distinct values: 114
        Mode: 161. Frequency: 72
        Median: 111.0
    */

        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();
        return (red >= 191 && red <= 240 && green >= 68 && green <= 169 && blue >= 55 && blue <= 169);
        // return (red > 190 && red < 250 && green > 40 && green < 170 && blue > 40 && blue < 170);
    }

    private static boolean isRedRegionMatch(Color c) {
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();
        // return (red >= 235 && red <= 255 && green >= 220 && green <= 240 && blue >= 210 && blue <= 230);
        // return (red >= 235 && red <= 250);
        return (red >= 191 && red <= 240 && green >= 68 && green <= 169 && blue >= 55 && blue <= 169);
    }

    private static void generateStats() {
        red = new ArrayList<Integer>();
        green = new ArrayList<Integer>();
        blue = new ArrayList<Integer>();
        splitColors("koramangala");
        splitColors("ejipura");
        System.out.println("\n--- Red ---");
        getStats(red);
        System.out.println("\n--- Green ---");
        getStats(green);
        System.out.println("\n--- Blue ---");
        getStats(blue);
    }

    private static void splitColors(String filename) {
        try {
            BufferedImage bi = ImageIO.read(new File("/home/arko1k/" + filename + ".png"));
            for (int i = 0; i < bi.getWidth(); i++) {
                for (int j = 0; j < bi.getHeight(); j++) {
                    Color c = new Color(bi.getRGB(i, j));
                    if (isRedMatch(c)) {
                        red.add(c.getRed());
                        green.add(c.getGreen());
                        blue.add(c.getBlue());
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getStats(ArrayList<Integer> a) {
        Collections.sort(a);
        int len = a.size();
        int min = a.get(0);
        System.out.println("Min: " + min);
        System.out.println("Max: " + a.get(len - 1));

        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += a.get(i);
        }
        System.out.println("Mean: " + sum / len);

        int[] freq = new int[len];
        int maxFreq = 0;
        int mode = 0;
        int distinct = 0;
        for (int i = 0; i < len; i++) {
            int val = a.get(i);
            int index = val - min;
            if(freq[index]++ == 0)
                distinct++;
            if (freq[index] > maxFreq) {
                maxFreq = freq[index];
                mode = val;
            }
        }
        System.out.println("Number of values: " + len);
        System.out.println("Number of distinct values: " + distinct);
        System.out.println("Mode: " + mode + ". Frequency: " + maxFreq);

        int middle = len / 2;
        double median = (len % 2 == 1) ? a.get(middle) : ((a.get(middle - 1) + a.get(middle)) / 2.0);
        System.out.println("Median: " + median);
    }

    private static ArrayList<Integer[]> collect(int currX, int currY, int range) {
        ArrayList<Integer[]> collected = new ArrayList<Integer[]>();
        int X = currX, Y = currY;
        for(int i = 1; i <= range; i++) {
            int xIncr = 1, yIncr = 1, rounds = 0;
            boolean x = true;
            while (rounds < 3) {
                if (x) {
                    X += xIncr;
                    if (Math.abs(X - currX) == i) {
                        rounds++;
                        xIncr *= -1;
                        x = false;
                    }
                } else {
                    Y += yIncr;
                    if (Math.abs(Y - currY) == i) {
                        yIncr *= -1;
                        x = true;
                    }
                }
                if (!pixelsScanned[X][Y] && isRedMatch(new Color(shot.getRGB(X, Y))))
                    collected.add(new Integer[]{X, Y});
            }
        }
        return collected;
    }

    private static double angle(int fromX, int fromY, int toX, int toY) {
        double angle = Math.toDegrees(Math.atan2(-toY + fromY, toX - fromX));
        if(angle < 0)
            angle += 360;
        return angle;
    }

    // TODO: Advanced selection based on combination of weightage of angle and distance.
    // TODO: Sense of direction.
    // TODO: Light red region on the inside.

    private static void selectNext(int prevX, int prevY, int currX, int currY) {
        boolean found = false;
        int X = currX, Y = currY;
        for(int i = 1; i <= SCAN_RANGE; i++) {
            ArrayList<Integer[]> collected = collect(currX, currY, i);
            int collSize = collected.size();
            if (collSize > 0) {
                double angle = angle(prevX, prevY, currX, currY);
                int minIndex = 0;
                double minAngleDiff = 360;
                for (int j = 0; j < collSize; j++) {
                    Integer[] currPix = collected.get(j);
                    double currAngleDiff = Math.abs(angle(currX, currY, currPix[0], currPix[1]) - angle);
                    if(currAngleDiff > 180)
                        currAngleDiff = 360 - currAngleDiff;
                    if (currAngleDiff < minAngleDiff) {
                        minAngleDiff = currAngleDiff;
                        minIndex = j;
                    }
                }
                Integer[] pix = collected.get(minIndex);
                X = pix[0];
                Y = pix[1];
                for(int j = 0; j < collected.size(); j++) {
                    Integer[] collectedPix = collected.get(j);
                    pixelsScanned[collectedPix[0]][collectedPix[1]] = true;
                }
                found = true;
                break;
            }
        }
        if(found) {
            // robo.mouseMove(X, Y);
            redPixList.add(new Integer[]{X, Y});
            getRegion(X, Y);
            selectNext(currX, currY, X, Y);
        }
        else {
            // robo.mouseMove(currX, currY);
            System.out.println("Lost at " + currX + " " + currY);
        }
    }

    private static boolean areCollinear(int aX, int aY, int bX, int bY, int cX, int cY) {
        return (Math.abs(aX - bX) <= LINEARITY_REGION && Math.abs(aY - bY) <= LINEARITY_REGION
                && Math.abs(bX - cX) <= LINEARITY_REGION && Math.abs(bY - cY) <= LINEARITY_REGION
                && Math.abs(cX - aX) <= LINEARITY_REGION && Math.abs(cY - aY) <= LINEARITY_REGION
                && (aX * (bY - cY) + bX * (cY - aY) + cX * (aY - bY)) < COLLINEARITY_TOLERANCE);
    }

    private static double distFromOrigin(Integer[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1]);
    }

    private static void minimalize() {
        for(int p = 0; p < 1; p++) {
            int pixCount = redPixList.size(), runningCount = pixCount;
            int pixIndex = 0;
            for (int i = 0; i < pixCount; i++) {
                int i2 = (pixIndex + 1) % runningCount, i3 = (pixIndex + 2) % runningCount;
                Integer[] a = redPixList.get(pixIndex);
                Integer[] b = redPixList.get(i2);
                Integer[] c = redPixList.get(i3);
                if (areCollinear(a[0], a[1], b[0], b[1], c[0], c[1])) {
                    double distA = distFromOrigin(a);
                    double distB = distFromOrigin(b);
                    double distC = distFromOrigin(c);
                    double mid = Math.min(Math.min(Math.max(distA, distB), Math.max(distB, distC)), Math.max(distA, distC));
                    int pixelBetween = mid == distA ? i : (mid == distB ? i + 1 : i + 2);
                    redPixList.remove(pixIndex--);
                    runningCount--;
                }
                pixIndex++;
            }
        }
    }

    static double epsilon = 0.9;

    private static ArrayList<Integer[]> smoothen(ArrayList<Integer[]> jaggedPoly) {
        int len = jaggedPoly.size() - 1;
        Integer[] start = jaggedPoly.get(0);
        Integer[] end = jaggedPoly.get(len);
        double dmax = 0;
        int index = 0;
        double dist = absDist(start, end);
        for(int i = 1; i < len; i++) {
            double d = shortestDistanceToSegment(jaggedPoly.get(i), start, end);
            if (d > dmax) {
                index = i;
                dmax = d;
            }
        }
        // if (dmax > (epsilon * dist / dmax)) { // epsilon = 0.04;
        if (dmax > epsilon) {
            ArrayList<Integer[]> recResults1 = smoothen(new ArrayList<Integer[]>(jaggedPoly.subList(0, index)));
            ArrayList<Integer[]> recResults2 = smoothen(new ArrayList<Integer[]>(jaggedPoly.subList(index, len)));
            recResults1.addAll(recResults2);
            return recResults1;
        } else {
            ArrayList<Integer[]> result = new ArrayList<Integer[]>();
            result.add(start);
            result.add(end);
            return result;
        }
    }

    private static double shortestDistanceToSegment(Integer[] p0, Integer[] p1, Integer[] p2) {
        int x0 = p0[0], y0 = p0[1], x1 = p1[0], y1 = p1[1], x2 = p2[0], y2 = p2[1];
        return Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / Math.sqrt(Math.pow((y2 - y1), 2) + Math.pow((x2 - x1), 2));
    }

    private static double absDist(Integer[] a, Integer[] b) {
        return Math.sqrt(Math.pow((b[0] - a[0]), 2) + Math.pow((b[1] - a[1]), 2));
    }

    private static void getRegion(int X, int Y) {
        int tileX = X / SCAN_REGION, tileY = Y / SCAN_REGION;
        if(!tilesScanned[tileX][tileY]) {
            tilesScanned[tileX][tileY] = true;
            int downX = X - X % SCAN_REGION, downY = Y - Y % SCAN_REGION, upX = downX + SCAN_REGION - 1, upY = downY + SCAN_REGION - 1;
            X = downX;
            Y = downY;
            while (Y <= upY) {
                while (X <= upX) {
                    Color color = new Color(shot.getRGB(X, Y));
                    if (isRedRegionMatch(color)) {
                        redRegionPixList.add(new Integer[]{X, Y});
                        // regionPixelsScanned[X][Y] = color.getRGB();
                    }
                    X++;
                }
                X = downX;
                Y++;
            }
        }
    }

    private static ArrayList<Integer[]> lineScan(BufferedImage shot, int downX, int downY, int upX, int upY) {
        ArrayList<Integer[]> region = new ArrayList<Integer[]>();
        int X = downX, Y = downY;
        while(Y <= upY) {
            boolean collect = false;
            int collectStartX = 0;
            while(X <= upX) {
                if(isRedMatch(new Color(shot.getRGB(X, Y)))) {
                    region.add(new Integer[]{X, Y});
                    if(collect && X - collectStartX > 2) {
                        collect = false;
                        collectStartX = X;
                    }
                    else {
                        collect = true;
                        collectStartX = X;
                    }
                }
                X++;
                if(collect) {
                    region.add(new Integer[]{X, Y});
                }
            }
            X = downX;
            Y++;
        }
        return region;
    }
}