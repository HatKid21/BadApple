package org.example;

import java.io.*;

public class FrameReader {

    private final String EMPTY_FRAME = "\n".repeat(50);
    private BufferedReader reader;

    public FrameReader(int width,int height){
        String outputPath = "resources/BadApple/" + width + "x" + height;
        if (new File(outputPath).exists()){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(outputPath));
                this.reader = reader;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("There is no video with such resolution");
        }
    }

    public boolean hasReader(){
        return reader != null;
    }

    public String getEmptyFrame(){
        return EMPTY_FRAME;
    }

    public String nextFrame(){
        StringBuilder frame = new StringBuilder();
        String line;
        while (true){
            try {
                line = reader.readLine();
                if (line == null){
                    return null;
                }
                if (!line.isEmpty()){
                    frame.append(line).append("\n");
                } else{
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return frame.toString();
    }

}
