package org.example;
public class Test {

    public static void main(String[] args){
        for (int i = 0; i < 10; i++) {
            System.out.print("Hey " + i + "\r"); // Print the line, then immediately jump back
            System.out.flush(); // Nudge it to show the change
            //  You might want to add a little delay here so you can see it happen
            try {
                Thread.sleep(300); // Wait for 300 milliseconds. Adjust as needed
            } catch (InterruptedException e) {
                // Eh, whatever
            }
        }
        System.out.print("\r      \r"); // Then clear the last line if you want
        System.out.flush();
    }
}

