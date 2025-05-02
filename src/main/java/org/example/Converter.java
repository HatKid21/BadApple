package org.example;


import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class Converter {

    private static final Java2DFrameConverter converter =new Java2DFrameConverter();

    private final int HEIGHT;
    private final int WIDTH;
    private final String outputPath;
    private BufferedWriter writer;
    private int totalFrames = 0;

    public Converter(int width, int height){
        this.HEIGHT = height;
        this.WIDTH = width;
        this.outputPath = "resources/BadApple/" + width + "x" + height;
    }

    public void play(){
        FrameReader frameReader = new FrameReader(WIDTH,HEIGHT);
        if (!frameReader.hasReader()){
            System.out.println("Something went wrong");
            return;
        }
        String frame = frameReader.nextFrame();
        while(frame != null) {
            try {
                System.out.print(frameReader.getEmptyFrame());
                System.out.print(frame);
                frame = frameReader.nextFrame();
                TimeUnit.MILLISECONDS.sleep(29);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void convertToFrame(String videoPath){
        if (new File(outputPath).exists()){
            System.out.println("This file already exists");
            return;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            this.writer = writer;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new File(videoPath));

        try {
            frameGrabber.start();
        } catch (FrameGrabber.Exception e){
            System.out.println(e.getMessage());
        }

        Frame frame;

        while (true){
            try {
                if ((frame = frameGrabber.grabFrame()) == null) break;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            BufferedImage imageFrame = converter.getBufferedImage(frame);
            if (imageFrame != null){
                imageFrame = resizeImage(imageFrame);
                writeFrame(createFrame(imageFrame),totalFrames);
                totalFrames++;
            }

        }
        System.out.println("Converting have completed!");
    }

    private void writeFrame(char[][] frame, int frameNum){
        StringBuilder stringFrame;
        if (frameNum == 0){
            stringFrame = new StringBuilder(frameNum);
        } else{
            stringFrame = new StringBuilder(frameNum).append("\n");
        }
        for (char[] chars : frame){
            StringBuilder line = new StringBuilder();
            for (char c : chars){
                line.append(c);
            }
            stringFrame.append(line).append("\n");
        }
        try {
            writer.write(stringFrame.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private char[][] createFrame(BufferedImage imageFrame){
        char[][] frame = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                frame[i][j] = parseColor(imageFrame.getRGB(j,i));
            }
        }
        return frame;
    }

    private char parseColor(int rgb){
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        if (r >= (255 - 1) && g >= (255 - 1) && b >= (255 - 1)){
            return '.';
        }
        return '#';
    }

    private BufferedImage resizeImage(BufferedImage initialImage){
        BufferedImage resizedImage = new BufferedImage(WIDTH,HEIGHT,initialImage.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(initialImage,0,0,WIDTH,HEIGHT,null);
        graphics.dispose();
        return resizedImage;
    }

}
