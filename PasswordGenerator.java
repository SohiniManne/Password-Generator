package passwordgenerator;

import javax.swing.*;
import javax.swing.UIManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.awt.datatransfer.*;
import java.awt.Toolkit;

public class PasswordGenerator extends JFrame {
    private JTextField lengthField;
    private JCheckBox uppercaseBox, lowercaseBox, numbersBox, symbolsBox;
    private JTextArea passwordArea;
    private JButton generateButton, copyButton;
    private JLabel strengthLabel;
    
    // Character sets for password generation
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private SecureRandom random = new SecureRandom();
    
    public PasswordGenerator() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setDefaultValues();
    }
    
    private void initializeComponents() {
        setTitle("Password Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Input components
        lengthField = new JTextField("12", 5);
        uppercaseBox = new JCheckBox("Uppercase Letters (A-Z)", true);
        lowercaseBox = new JCheckBox("Lowercase Letters (a-z)", true);
        numbersBox = new JCheckBox("Numbers (0-9)", true);
        symbolsBox = new JCheckBox("Symbols (!@#$%^&*)", false);
        
        // Output components
        passwordArea = new JTextArea(3, 30);
        passwordArea.setEditable(false);
        passwordArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        passwordArea.setBackground(Color.WHITE);
        passwordArea.setBorder(BorderFactory.createLoweredBevelBorder());
        
        // Buttons
        generateButton = new JButton("Generate Password");
        copyButton = new JButton("Copy to Clipboard");
        copyButton.setEnabled(false);
        
        // Strength indicator
        strengthLabel = new JLabel("Password Strength: ");
        strengthLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JLabel titleLabel = new JLabel("Password Generator");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);
        
        // Password length
        gbc.gridwidth = 1; gbc.insets = new Insets(5, 0, 5, 10);
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Password Length:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(lengthField, gbc);
        
        // Character options
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(uppercaseBox, gbc);
        gbc.gridy = 3;
        mainPanel.add(lowercaseBox, gbc);
        gbc.gridy = 4;
        mainPanel.add(numbersBox, gbc);
        gbc.gridy = 5;
        mainPanel.add(symbolsBox, gbc);
        
        // Generate button
        gbc.gridy = 6; gbc.insets = new Insets(15, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(generateButton, gbc);
        
        // Password output
        gbc.gridy = 7; gbc.insets = new Insets(10, 0, 5, 0);
        JScrollPane scrollPane = new JScrollPane(passwordArea);
        scrollPane.setPreferredSize(new Dimension(350, 80));
        mainPanel.add(scrollPane, gbc);
        
        // Copy button
        gbc.gridy = 8; gbc.insets = new Insets(5, 0, 10, 0);
        mainPanel.add(copyButton, gbc);
        
        // Strength indicator
        gbc.gridy = 9; gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(strengthLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePassword();
            }
        });
        
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard();
            }
        });
        
        // Enable/disable generate button based on checkbox selection
        ActionListener checkboxListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGenerateButton();
            }
        };
        
        uppercaseBox.addActionListener(checkboxListener);
        lowercaseBox.addActionListener(checkboxListener);
        numbersBox.addActionListener(checkboxListener);
        symbolsBox.addActionListener(checkboxListener);
    }
    
    private void setDefaultValues() {
        updateGenerateButton();
    }
    
    private void updateGenerateButton() {
        boolean anySelected = uppercaseBox.isSelected() || lowercaseBox.isSelected() || 
                             numbersBox.isSelected() || symbolsBox.isSelected();
        generateButton.setEnabled(anySelected);
    }
    
    private void generatePassword() {
        try {
            int length = Integer.parseInt(lengthField.getText().trim());
            
            if (length < 1) {
                JOptionPane.showMessageDialog(this, "Password length must be at least 1", 
                                            "Invalid Length", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (length > 1000) {
                JOptionPane.showMessageDialog(this, "Password length cannot exceed 1000", 
                                            "Invalid Length", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            StringBuilder characterSet = new StringBuilder();
            
            if (uppercaseBox.isSelected()) characterSet.append(UPPERCASE);
            if (lowercaseBox.isSelected()) characterSet.append(LOWERCASE);
            if (numbersBox.isSelected()) characterSet.append(NUMBERS);
            if (symbolsBox.isSelected()) characterSet.append(SYMBOLS);
            
            if (characterSet.length() == 0) {
                JOptionPane.showMessageDialog(this, "Please select at least one character type", 
                                            "No Character Types Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String password = generateSecurePassword(length, characterSet.toString());
            passwordArea.setText(password);
            copyButton.setEnabled(true);
            
            updatePasswordStrength(password);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for password length", 
                                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateSecurePassword(int length, String characterSet) {
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each selected type
        if (uppercaseBox.isSelected()) {
            password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (lowercaseBox.isSelected()) {
            password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (numbersBox.isSelected()) {
            password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        if (symbolsBox.isSelected()) {
            password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        }
        
        // Fill the rest with random characters from the full set
        while (password.length() < length) {
            password.append(characterSet.charAt(random.nextInt(characterSet.length())));
        }
        
        // Shuffle the password to avoid predictable patterns
        return shuffleString(password.toString());
    }
    
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
    
    private void updatePasswordStrength(String password) {
        int score = calculatePasswordStrength(password);
        String strength;
        Color color;
        
        if (score < 3) {
            strength = "Weak";
            color = Color.RED;
        } else if (score < 5) {
            strength = "Medium";
            color = Color.ORANGE;
        } else if (score < 7) {
            strength = "Strong";
            color = Color.BLUE;
        } else {
            strength = "Very Strong";
            color = Color.GREEN;
        }
        
        strengthLabel.setText("Password Strength: " + strength);
        strengthLabel.setForeground(color);
    }
    
    private int calculatePasswordStrength(String password) {
        int score = 0;
        
        // Length bonus
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;
        
        // Character type bonuses
        if (password.matches(".*[a-z].*")) score++; // lowercase
        if (password.matches(".*[A-Z].*")) score++; // uppercase
        if (password.matches(".*[0-9].*")) score++; // numbers
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) score++; // symbols
        
        // Complexity bonus
        if (password.length() >= 16 && score >= 6) score++;
        
        return score;
    }
    
    private void copyToClipboard() {
        String password = passwordArea.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            JOptionPane.showMessageDialog(this, "Password copied to clipboard!", 
                                        "Copied", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PasswordGenerator().setVisible(true);
            }
        });
    }
}