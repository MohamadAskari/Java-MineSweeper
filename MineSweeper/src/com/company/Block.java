package com.company;

import javax.swing.*;
import java.awt.*;

public class Block extends JButton {

    private final int row;
    private final int col;
    private int value;

    Block(final int row, final int col) {
        this.row = row;
        this.col = col;
        setText("");
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) { this.value = value; }

    public int getRow() { return row; }

    public int getCol() { return col; }

    public void showValue(){
        if(!(this.getValue() == 10)) {
            this.setEnabled(false);
            this.setText(this.getValue() == 0 ? "" : String.valueOf(this.getValue()));
            this.setFont(new Font("Verdana",Font.PLAIN,12));
        }
    }

    public void reset(){
        this.setText("");
        this.value = 0;
    }
}
