package ie.ucd.engac.ui;

import ie.ucd.engac.LifeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainMenu extends JPanel implements ActionListener {

    private static final int PANEL_WIDTH = 640;
    private static final int PANEL_HEIGHT = 480;

    private LifeGame lifeGameParent;

    private JButton newGameButton;
    private JButton quitGameButton;
    private JButton playButton;
    private JComboBox<String> jCombo;
    private JTextArea jTextArea;

    private int numPlayers = 2;

    public MainMenu(LifeGame lifeGame){
        super();
        lifeGameParent = lifeGame;
        setBackground(Color.lightGray);
        Dimension sizePreferences = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
        setPreferredSize(sizePreferences);
        setFocusable(true);
        requestFocus();

        GridBagLayout mainMenuManager = new GridBagLayout();
        setLayout(mainMenuManager);
        GridBagConstraints buttonAreaConstraints = new GridBagConstraints();
        buttonAreaConstraints.gridx = 1;
        buttonAreaConstraints.gridy = 1;
        constructButtonArea();
    }

    private void constructButtonArea(){
        GridBagConstraints newGameButtonConstraints = new GridBagConstraints();
        newGameButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        newGameButtonConstraints.gridx = 1;
        newGameButtonConstraints.gridy = 0;
        newGameButton = new JButton("New Game");
        newGameButton.setActionCommand("New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.addActionListener(this);
        add(newGameButton,newGameButtonConstraints);

        GridBagConstraints quitGameButtonConstraints = new GridBagConstraints();
        quitGameButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        quitGameButtonConstraints.gridx = 1;
        quitGameButtonConstraints.gridy = 2;
        quitGameButton = new JButton("Exit");
        quitGameButton.setActionCommand("Exit");
        quitGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitGameButton.addActionListener(this);
        add(quitGameButton,quitGameButtonConstraints);


        GridBagConstraints playButtonConstraints = new GridBagConstraints();
        playButtonConstraints.gridx = 1;
        playButtonConstraints.gridy = 0;
        playButton = new JButton("Play");
        playButton.setActionCommand("Play");
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.setVisible(false);
        playButton.addActionListener(this);
        add(playButton,playButtonConstraints);

        GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.fill = GridBagConstraints.HORIZONTAL;
        textConstraints.gridx = 1;
        textConstraints.gridy = 1;
        String text = "Number of players:";
        jTextArea = new JTextArea(text);
        jTextArea.setBackground(Color.lightGray);
        jTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        jTextArea.setVisible(false);
        add(jTextArea,textConstraints);

        GridBagConstraints jComboConstraints = new GridBagConstraints();
        jComboConstraints.fill = GridBagConstraints.HORIZONTAL;
        jComboConstraints.gridx = 1;
        jComboConstraints.gridy = 2;
        String[] numPlayersList = { "2", "3", "4"};
        jCombo = new JComboBox<>(numPlayersList);
        jCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        jCombo.addActionListener(this::setNumPlayers);
        jCombo.setSelectedIndex(0);
        jCombo.setVisible(false);
        add(jCombo,jComboConstraints);
    }

    private void newGame(){
        setVisibilityMainScreen(false);
        setVisibilityNumPlayers(true);
    }
    public void returnToMainMenu(){
        setVisibilityMainScreen(true);
        setVisibilityNumPlayers(false);
    }

    private void setVisibilityMainScreen(boolean bool){
        newGameButton.setVisible(bool);
        quitGameButton.setVisible(bool);
    }

    private void setVisibilityNumPlayers(boolean bool){
        jCombo.setVisible(bool);
        jTextArea.setVisible(bool);
        playButton.setVisible(bool);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent source = (JComponent) e.getSource();
        if (source instanceof JButton){
            switch (((JButton) source).getActionCommand()){
                case "New Game": newGame(); break;
                case "Exit":     lifeGameParent.dispose(); break;
                case "Play":     lifeGameParent.initialiseGame(numPlayers);
            }
        }
        else
            System.err.println("Error, unhandled action event" + e.getSource());
    }

    private void setNumPlayers(ActionEvent e) {
        JComboBox comboBox = (JComboBox)e.getSource();
        numPlayers = comboBox.getSelectedIndex()+2;
    }
}
