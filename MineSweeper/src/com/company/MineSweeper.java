package com.company;

import javax.swing.*;

public class MineSweeper {

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } //Just for a better look
        catch (Exception ignored) {}
        GameBoard.SetDifficulty();
    }
}
