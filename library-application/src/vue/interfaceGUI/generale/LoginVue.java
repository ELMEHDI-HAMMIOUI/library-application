
package vue.interfaceGUI.generale;

import javax.imageio.ImageIO;
import javax.swing.* ;
import java.util.List;

import controle.LoginControler;
import modele.beans.Livre;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class LoginVue extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextField searchField;
    private JPanel bookPanel;
    private JLabel status;
    private LoginControler controller; // Reference to the controller

    public void setController(LoginControler controller) {
        this.controller = controller;
    }

    public LoginVue(List<Livre> livres) {
        controller = new LoginControler(); // Initialize the controller

        // Initialize the window
        setTitle("Login Interface");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());
        setResizable(false);
        
        ImageIcon icon = new ImageIcon("src/vue/icons/EMSI.jpg"); 
        setIconImage(icon.getImage());

        // Create sidebar panel for login
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main panel for book information
        JPanel mainPanel = createMainPanel(livres);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better control
        sidebarPanel.setBackground(new Color(41, 128, 185)); // Blue background
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Create components for the login form
        JLabel titleLabel = new JLabel("Veuillez Se Connecter à votre Compte");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridwidth = 2; // Span across two columns
        gbc.gridx = 0;
        gbc.gridy = 0;
        sidebarPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset to default
        gbc.gridy++; // Move to the next row

        JLabel usernameLabel = new JLabel("Email/User Name");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sidebarPanel.add(usernameLabel, gbc);

        gbc.gridx = 1; // Move to the next column
        usernameField = new JTextField(15); // Set a smaller width
        customizeTextField(usernameField);
        sidebarPanel.add(usernameField, gbc);

        gbc.gridx = 0; // Reset to first column
        gbc.gridy++; // Move to the next row

        JLabel passwordLabel = new JLabel("Mot de Passe");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sidebarPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; // Move to the next column
        passwordField = new JPasswordField(15); // Set a smaller width
        customizeTextField(passwordField);
        sidebarPanel.add(passwordField, gbc);

        gbc.gridx = 0; // Reset to first column
        gbc.gridy++; // Move to the next row
        gbc.gridwidth = 2; // Span across two columns
        status = new JLabel();
        status.setForeground(Color.RED);
        status.setPreferredSize(new Dimension(200, 20)); // Set a preferred size for the status label
        sidebarPanel.add(status, gbc);

        gbc.gridwidth = 1; // Reset to default
        gbc.gridy++; // Move to the next row
        gbc.gridx = 0; // Reset to first column

        loginButton = new JButton("Login");
        customizeButton(loginButton);
        sidebarPanel.add(loginButton, gbc);

        // Add action for the login button
        loginButton.addActionListener(e -> {
            String username = getUsername();
            String password = getPassword();
            controller.handleLogin(username, password);
        });

        return sidebarPanel;
    }
    private JPanel createMainPanel(List<Livre> livres) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE); // White background

        // Add a search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(30);
        JLabel searchLbl=new JLabel("Rechercher 🔎");
        customizeTextField(searchField);
        searchPanel.add(searchLbl);
        searchPanel.add(searchField);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Create a panel to display books
        bookPanel = new JPanel();
        bookPanel.setLayout(new GridLayout(0, 3, 10, 10)); // Dynamic rows, 3 columns
        bookPanel.setBackground(Color .WHITE);
        bookPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add books to the panel
        displayBooks(livres);

        mainPanel.add(new JScrollPane(bookPanel), BorderLayout.CENTER);

        // Add listener to the search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                List<Livre> filteredBooks = livres.stream()
                        .filter(book -> book.getTitre().toLowerCase().contains(query))
                        .toList();
                displayBooks(filteredBooks);
            }
        });

        return mainPanel;
    }

    private void displayBooks(List<Livre> books) {
        bookPanel.removeAll(); // Clear previous books
        for (Livre book : books) {
            JPanel bookCard = createBookCard(book.getTitre(), book.getAuteur(), book.getImagePath());
            bookPanel.add(bookCard);
        }
        bookPanel.revalidate();
        bookPanel.repaint();
    }

    private JPanel createBookCard(String bookName, String authorName, String imagePath) {
        // Create the main card panel with vertical stacking
        JPanel bookCard = new JPanel();
        bookCard.setLayout(new BorderLayout());
        bookCard.setBackground(new Color(212, 235, 248)); // Light blue background
        bookCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        bookCard.setPreferredSize(new Dimension(120, 180)); // Set fixed size for consistency

        // Create and set the book image label
        JLabel bookImage = new JLabel();
        ImageIcon icon = new ImageIcon(resizeImage(imagePath, 100, 150)); // Resize image to fit
        bookImage.setIcon(icon);
        bookImage.setHorizontalAlignment(SwingConstants.CENTER);

        // Create the bottom panel for text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.setBackground(new Color(100, 149, 237)); // Blue color for text panel

        // Create and style the book title label
        JLabel bookTitle = new JLabel(bookName, SwingConstants.CENTER);
        bookTitle.setFont(new Font("Arial", Font.BOLD, 12));
        bookTitle.setForeground(Color.WHITE);

        // Create and style the author label
        JLabel bookAuthor = new JLabel(authorName, SwingConstants.CENTER);
        bookAuthor.setFont(new Font("Arial", Font.PLAIN, 10));
        bookAuthor.setForeground(Color.WHITE);

        // Add title and author to the text panel
        textPanel.add(bookTitle);
        textPanel.add(bookAuthor);

        // Add components to the book card
        bookCard.add(bookImage, BorderLayout.CENTER);
        bookCard.add(textPanel, BorderLayout.SOUTH);

        return bookCard;
    }

    // Helper method to resize an image
    private Image resizeImage(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image originalImage = originalIcon.getImage();
        return originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    private void customizeTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        textField.setPreferredSize(new Dimension(200, 25));
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 87, 34)); // Orange color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }



	public void setStatus(String error) {
		status.setText(error);
	}
}





