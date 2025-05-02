package org.example;

import java.io.File;
import java.util.Scanner;

public class BadAppleApplication {

    private static final Scanner sc = new Scanner(System.in);

    public static void start(){
        System.out.println("BadApple ASCII video player");
        String arg = sc.nextLine();
        while (!arg.isEmpty()){
            if (arg.startsWith("exit")){
                break;
            }
            parseCommand(arg);
            arg = sc.nextLine();
        }
    }

    private static void parseCommand(String arg){
        System.out.println(arg);
        if (arg.startsWith("play")){
//            System.out.println("User wants to play a video");
            executePlay(arg);
        } else if (arg.startsWith("list")){
//            System.out.println("User wants to view a list");
            executeList();
        } else if (arg.startsWith("create")){
//            System.out.println("User wants to create a new format");
            executeCreate(arg);
        } else if (arg.startsWith("help")){
            executeHelp();
        } else{
            System.out.println("There is no such command. Use \"help\" to see all available commands");
        }
    }

    private static void executeHelp(){
        System.out.println("Available commands:");
        System.out.println("\thelp - show that menu");
        System.out.println("\tplay NxM - play NxM video (for example: play 210x100)");
        System.out.println("\tlist - show list of available video formats");
        System.out.println("\tcreate NxM - create new video format");
    }

    private static void executePlay(String name){
        String[] split = name.split(" ");
        if (split.length == 1){
            System.out.println("Incorrect input");
            return;
        }
        if (!split[1].contains("x")){
            System.out.println("Incorrect input");
            return;
        }
        int[] res = new int[2];
        String[] temp = split[1].split("x");
        if (temp.length == 2){
            try {
                res[0] = Integer.parseInt(temp[0]);
                res[1] = Integer.parseInt(temp[1]);
            } catch (NumberFormatException e ){
                System.out.println("Incorrect input");
                System.out.println(e.getMessage());
                return;
            }
        } else{
            System.out.println("Incorrect input");
            return;
        }
        Converter converter = new Converter(res[0],res[1]);
        converter.play();
    }

    private static void executeList(){
        File folder = new File("resources/BadApple");
        File[] files = folder.listFiles();
        if (files != null){
            StringBuilder outputMessage = new StringBuilder("Available formats :");
            int order = 1;
            for (File file : files){
                outputMessage.append("\n");
                if (file.isFile()){
                    outputMessage.append("\t").append(order).append(". ").append(file.getName());
                }
                order++;
            }
            System.out.println(outputMessage);
        }else {
            System.out.println("There is no converted videos :(");
        }
    }

    private static void executeCreate(String arg){
        String[] split = arg.split(" ");
        if (split.length == 1){
            System.out.println("Incorrect input");
            return;
        }
        if (!split[1].contains("x")){
            System.out.println("Incorrect input");
            return;
        }
        String[] temp = split[1].split("x");
        int[] res = new int[2];
        if (temp.length == 2){
            try {
                res[0] = Integer.parseInt(temp[0]);
                res[1] = Integer.parseInt(temp[1]);
            } catch (NumberFormatException e ){
                System.out.println("Incorrect input");
                System.out.println(e.getMessage());
                return;
            }
        } else{
            System.out.println("Incorrect input");
            return;
        }
        Converter converter = new Converter(res[0],res[1]);
        converter.convertToFrame("Video/BadApple.mp4");
    }


}
