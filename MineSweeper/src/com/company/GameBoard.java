package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

// value 0 is for empty blocks , values from 1 to 8 are for blocks near mines , value 10 is for mines

public class GameBoard extends JFrame implements MouseListener {
    private int size, MinePopulation, total_flags;
    private JPanel top, center;
    private JLabel flagCount, timeLabel;
    private int DisarmedMines;
    private int elapsedTime = 0, seconds = 0 , minutes = 0 , hours = 0;
    private boolean started = false;
    private String seconds_string = String.format("%02d", seconds);
    private String minutes_string = String.format("%02d", minutes);
    private String hours_string = String.format("%02d", hours);
    private Block[][] blocks;
    private final ImageIcon FlagImage = new ImageIcon("Flag.png");
    private final ImageIcon BombImage = new ImageIcon("Bomb.png");
    private final Timer timer = new Timer(1000, new ActionListener() {

        public void actionPerformed(ActionEvent e) {

            elapsedTime=elapsedTime+1000;
            hours = (elapsedTime/3600000);
            minutes = (elapsedTime/60000) % 60;
            seconds = (elapsedTime/1000) % 60;
            seconds_string = String.format("%02d", seconds);
            minutes_string = String.format("%02d", minutes);
            hours_string = String.format("%02d", hours);
            timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);
        }
    });

    GameBoard(int size){

        this.size = size;
        DisarmedMines = 0;
        blocks = new Block[size][size];

        flagCount = new JLabel();
        flagCount.setFont(new Font("Serif",Font.PLAIN,20));
        flagCount.setForeground(new Color(255,50,50));

        timeLabel = new JLabel();
        timeLabel.setText(hours_string+":"+minutes_string+":"+seconds_string);
        timeLabel.setFont(new Font("Serif",Font.PLAIN,20));
        timeLabel.setForeground(new Color(255,50,50));

        this.setIconImage(BombImage.getImage());
        this.setTitle("MineSweeper");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        switch (size) {
            case 8 -> {
                this.setSize(370, 470);
                MinePopulation = (size * 3) / 2 ;
                total_flags = (size * 3) / 2;
            }
            case 12 -> {
                this.setSize(600, 700);
                MinePopulation = size * 2;
                total_flags = size * 2;
            }
            case 16 -> {
                this.setSize(700, 800);
                MinePopulation = size * 3;
                total_flags = size * 3;
            }
        }
        flagCount.setText("Flags: " + total_flags + "   ");

        top = new JPanel();
        top.setPreferredSize(new Dimension(370, 40));
        top.setLayout(new BorderLayout());
        top.setBackground(Color.darkGray);
        top.addMouseListener(this);
        top.add(timeLabel, BorderLayout.WEST);
        top.add(flagCount, BorderLayout.EAST);

        center = new JPanel();
        center.setPreferredSize(new Dimension(370 , 435));
        center.setLayout(new GridLayout(size, size));

        SetUpBlocks();
        SetUpMines();
        SetUpNeighbors();

        this.add(center, BorderLayout.CENTER);
        this.add(top, BorderLayout.NORTH);
        this.setVisible(true);
    }
    static void SetDifficulty() {
        String[] choices = {"Easy", "Normal", "Hard"};
        int input = JOptionPane.showOptionDialog(null,
                " Set The Difficulty: ",
                " Difficulty", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon("QuestionMark.png"),
                choices,
                null);

        switch (input) {
            case 0 -> new GameBoard(8);
            case 1 -> new GameBoard(12);
            case 2 -> new GameBoard(16);
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!started) {
            timer.start();
            started = true;
        }
        Object source = e.getSource();
        if(SwingUtilities.isRightMouseButton(e))
            HandleAction("right" , source);
        else if(SwingUtilities.isLeftMouseButton(e))
            HandleAction("left" , source);
    }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) { }

    public void mouseExited(MouseEvent e) { }

    public void SetUpBlocks(){
        int row,col;
        for (row = 0 ; row < size ; row++){
            for (col = 0 ; col < size ; col++){
                blocks[row][col] = new Block(row, col);
                blocks[row][col].reset();
                blocks[row][col].setFocusable(false);
                blocks[row][col].addMouseListener(this);
                center.add(blocks[row][col]);
            }
        }
    }

    public void restart(){
        int n = JOptionPane.showOptionDialog(null,
                "Do You Want to Retry?",
                "Play again, maybe?",
                JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon("Wondering.png"),
                null,
                null);

        if(n == JOptionPane.YES_OPTION){
            this.setVisible(false);
            SetDifficulty();
        }
    }

    public void SetUpMines(){
        int i;
        for (i = 0 ; i < MinePopulation ; i++){
            int row = (int) (Math.random() * 100) % size;
            int col = (int) (Math.random() * 100) % size;

            if(blocks[row][col].getValue() != 10)
                blocks[row][col].setValue(10);

            else if(blocks[row][col].getValue() == 10)
                i--;
        }
    }
    public void HandleAction(String str , Object source){
        if(str.equalsIgnoreCase("right")){
            int row, col;
            for (row = 0; row < size; row++) {
                for (col = 0; col < size; col++) {
                    if (source == blocks[row][col]) {
                        if(blocks[row][col].getIcon() == FlagImage && blocks[row][col].isEnabled()) {
                            if(blocks[row][col].getValue() == 10)
                                DisarmedMines--;
                            blocks[row][col].setIcon(null);
                            total_flags++;
                            flagCount.setText("Flags: " + total_flags);
                        }
                        else if(blocks[row][col].getIcon() == null && blocks[row][col].isEnabled()){
                            if(total_flags > 0) {
                                if (blocks[row][col].getValue() == 10)
                                    DisarmedMines++;
                                blocks[row][col].setIcon(FlagImage);
                                total_flags--;
                                flagCount.setText("Flags: " + total_flags);
                            }
                            else if(blocks[row][col].isEnabled())
                                JOptionPane.showMessageDialog(
                                        null,
                                        "You're out of flags !!",
                                        "WARNING" ,
                                        JOptionPane.ERROR_MESSAGE); }
                        WinCheck();
                    }
                }
            }
        }
        else if(str.equals("left")){
            int row , col;
            for (row = 0 ; row < size ; row++){
                for (col = 0 ; col < size ; col++){
                    if (source == blocks[row][col]){
                        if(blocks[row][col].getValue() == 10 && blocks[row][col].getIcon() == null){
                            timer.stop();
                            showBombs();
                            Object[] options = {"OK"};
                            JOptionPane.showOptionDialog(
                                    null,
                                    "You Lost ",
                                    "Better Luck Next Time",
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.ERROR_MESSAGE,
                                    new ImageIcon("Sad.png"),
                                    options,
                                    options[0]);

                            restart();
                        }
                    else if(blocks[row][col].getValue() == 0 && blocks[row][col].getIcon() == null) {
                            ArrayList<Block> ToBeChecked = new ArrayList<>();
                            ToBeChecked.add(blocks[row][col]);
                            RevealBlocks(ToBeChecked);
                            flagCount.setText("Flags: " + total_flags);
                        }
                    else if(blocks[row][col].getIcon() == null)
                        blocks[row][col].showValue();
                    }
                }
            }
        }
    }
    public void RevealBlocks(ArrayList<Block> ToBeChecked) {
        while (!ToBeChecked.isEmpty()) {

            Block block = ToBeChecked.iterator().next();
            ToBeChecked.remove(block);
            block.showValue();
            Block[] neighbors = CountNeighborMines(block);

            for (Block neighbour : neighbors) {
                if (neighbour == null)
                    break;
                if (neighbour.getValue() == 0 && neighbour.isEnabled()) {
                    if(neighbour.getIcon() != null){
                        neighbour.setIcon(null);
                        total_flags++;
                    }
                    ToBeChecked.add(neighbour);
                }
                else {
                    if(neighbour.getIcon() != null){
                        neighbour.setIcon(null);
                        total_flags++;
                    }
                    neighbour.showValue();
                }
            }
        }
    }

    public void showBombs(){
        int row,col;
        for (row = 0 ; row < size ; row++){
            for (col = 0 ; col < size ; col++){
                if(blocks[row][col].getValue() == 10 )
                    blocks[row][col].setIcon(BombImage);
            }
        }
    }
    public void WinCheck(){
        if(DisarmedMines == MinePopulation) {
            JOptionPane.showMessageDialog(
                    null,
                    "Yow Win",
                    "Congratulations !",
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon("Happy.png"));
        }
    }

    public void SetUpNeighbors(){
        int row , col;
        for (row = 0 ; row < size ; row++){
            for (col = 0 ; col < size ; col++){
                if(!(blocks[row][col].getValue() == 10)) {
                    setNeighborMinesCount(blocks[row][col]);
                }
            }
        }
    }

    public void setNeighborMinesCount(Block block){
        Block[] neighbors = CountNeighborMines(block);
        for (Block c : neighbors){
            if(c == null)
                continue;
            if(c.getValue() == 10)
                block.setValue(block.getValue() + 1);
        }
    }

    public Block[] CountNeighborMines(Block block){
        Block[] neighbors = new Block[8];
        int i = 0;
        int neighborRow , neighborCol;

        for (neighborRow = -1; neighborRow <= 1; neighborRow++) {
            for (neighborCol = -1; neighborCol <= 1; neighborCol++) {

                if (neighborRow == 0 && neighborCol == 0)
                    continue;

                int rowValue = block.getRow() + neighborRow;
                int colValue = block.getCol() + neighborCol;

                if (rowValue < 0 || rowValue >= size || colValue < 0 || colValue >= size)
                    continue;

                neighbors[i++] = blocks[rowValue][colValue];
            }
        }
        return neighbors;
    }
}
