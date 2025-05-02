package org.example;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Application extends JFrame {

    private Thread videoThread;
    private boolean isPaused = false;

    private int currentRow;
    private Converter converter;
    private FrameReader frameReader;

    private final JScrollPane scrollPane;
    private Dimension windowSize;
    private final JTextPane commandPane;
    private final JTextPane videoPane;
    private static final String PROMPT = ">";

    private boolean isVideoScene = false;

    public Application() {
        setTitle("ASCII BadApple");
        windowSize = new Dimension(600, 500);
        getContentPane().setBackground(Color.BLACK);
        setSize(windowSize);

        videoPane = new JTextPane();
        initVideoPane();

        commandPane = new JTextPane();
        initCommandPane();
        scrollPane = new JScrollPane(commandPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screenCenter = getScreenCenter();
        setLocation(screenCenter.width, screenCenter.height);
        setVisible(true);
        commandPane.requestFocusInWindow();

    }


    private void changeScene() {
        if (isVideoScene) {
            remove(videoPane);
            add(commandPane);
            isVideoScene = false;
        } else {
            remove(commandPane);
            add(videoPane);
            isVideoScene = true;
        }

        revalidate();
        repaint();

        if (isVideoScene) {
            videoPane.requestFocusInWindow();
        } else {
            commandPane.requestFocusInWindow();
        }

    }

    private void initVideoPane() {
        videoPane.setBackground(Color.BLACK);
        videoPane.setForeground(Color.WHITE);
        videoPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        videoPane.setEditable(false);

        Font curFont = videoPane.getFont();
        Font nfont = new Font(Font.MONOSPACED, curFont.getStyle(), 24);
        videoPane.setFont(nfont);

        videoPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_Q) {
                    e.consume();
                    changeScene();
                    videoThread.stop();
                    videoThread = null;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    e.consume();
                    isPaused = !isPaused;
                }
            }
        });

        videoPane.setText("Hello");
        videoPane.setRequestFocusEnabled(true);

    }

    private void initCommandPane() {
        commandPane.setBackground(Color.BLACK);
        commandPane.setForeground(Color.GREEN);
        commandPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        insertPrompt();


        StyledDocument doc = commandPane.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, doc.getLength(), attributes, false);

        Font defaultFont = new Font("Courier New",Font.PLAIN,24);
        commandPane.setFont(defaultFont);

//        try {
//            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/minecraft.ttf"));
//            font = font.deriveFont(16f);
//            commandPane.setFont(font);
//        } catch (FontFormatException | IOException e) {
//            throw new RuntimeException(e);
//        }

        commandPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int caretPosition = commandPane.getCaretPosition();
                StyledDocument doc = commandPane.getStyledDocument();

                try {
                    int currentLineStart = Utilities.getRowStart(commandPane, caretPosition);
                    int lastLineStart = Utilities.getRowStart(commandPane, doc.getLength());
                    int promptEndOffset = lastLineStart + PROMPT.length();


                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        e.consume();
                        readCommand();
                        insertPrompt();
                        updateCurrentRow();

                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                        if (caretPosition <= currentLineStart + PROMPT.length()) {
                            e.consume();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (currentLineStart == lastLineStart) {
                            e.consume();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                        if (currentLineStart == lastLineStart && caretPosition == promptEndOffset) {
                            e.consume();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_HOME) {

                        if (currentLineStart == lastLineStart) {
                            e.consume();
                            commandPane.setCaretPosition(promptEndOffset);
                        }
                    }

                } catch (BadLocationException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });



    }

    private void readCommand() {
        StyledDocument document = commandPane.getStyledDocument();
        int documentLength = document.getLength();
        int promptLength = PROMPT.length();

        try {
            int lastPromptLineStart = Utilities.getRowStart(commandPane, documentLength - promptLength - 1);

            String command = document.getText(lastPromptLineStart + promptLength, documentLength - (lastPromptLineStart + promptLength));

            proceedCommand(command);

        } catch (BadLocationException e) {
            System.out.println(e.getMessage());
        }

    }

    private void updateCurrentRow() {
        currentRow += 1;
    }

    private void proceedCommand(String command) {

        if (command.isEmpty()) {
            return;
        }


        if (command.startsWith("list")) {
            executeList();
        } else if (command.startsWith("play")) {
            executePlay(command);
        } else if(command.startsWith("create")){
            executeCreate(command);
        } else if (command.startsWith("help")){
            executeHelp();
        } else{
            writeOnDocument("There is no such command. Use \"help\" to see all available commands");
        }

    }

    private void executeHelp(){
        writeOnDocument("Available commands:");
        writeOnDocument("    help - show that menu");
        writeOnDocument("    play NxM - play NxM video (for example: play 210x100)");
        writeOnDocument("    list - show list of available video formats");
        writeOnDocument("    create NxM - create new video format");
    }

    private void executeCreate(String command){
        if (command.split(" ").length == 1){
            writeOnDocument("Invalid command");
            return;
        }
        String res = command.split(" ")[1];
        int width = Integer.parseInt(res.split("x")[0]);
        int height = Integer.parseInt(res.split("x")[1]);
        converter = new Converter(width,height);
        converter.convertToFrame("resources/Video/BadApple.mp4");
        writeOnDocument("Converting completed");
    }

    private void executePlay(String command){
        if (command.split(" ").length == 1) {
            writeOnDocument("Invalid command");
            return;
        }
        String res = command.split(" ")[1];
        int width = Integer.parseInt(res.split("x")[0]);
        int height = Integer.parseInt(res.split("x")[1]);
        frameReader = new FrameReader(width, height);
        if (frameReader.hasReader()) {
            changeScene();
            startVideo();
        } else {
            writeOnDocument("There is no video with such resolution");
        }
    }

    private void executeList(){
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
            writeOnDocument(outputMessage.toString());
        }
        else{
            writeOnDocument("There is no converted videos :(");
        }
    }

    private void startVideo() {
        videoThread = null;
        videoThread = new Thread(() -> {
            String frame = frameReader.nextFrame();
            while (!frame.isEmpty()) {
                while (isPaused) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
                videoPane.setText(frame);
                try {
                    TimeUnit.MILLISECONDS.sleep(1000 / 30);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
                frame = frameReader.nextFrame();
            }
            changeScene();
        });

        videoThread.start();
    }

    private void writeOnDocument(String val) {
        StyledDocument document = commandPane.getStyledDocument();
        try {
            document.insertString(document.getLength(), "\n" + val, null);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }


    private void insertPrompt() {
        try {
            StyledDocument doc = commandPane.getStyledDocument();
            int rowStart = Utilities.getRowStart(commandPane, 0);
            if (rowStart == -1) {
                doc.insertString(doc.getLength(), PROMPT, null);
            } else {
                doc.insertString(doc.getLength(), "\n" + PROMPT, null);
            }
            commandPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Dimension getScreenCenter() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screensize = toolkit.getScreenSize();
        int tempX = (screensize.width - windowSize.width) / 2;
        int tempY = (screensize.height - windowSize.height) / 2;
        return new Dimension(tempX, tempY);
    }

}
