package vue.interfaceGUI.admin;

import javax.imageio.ImageIO;  
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

import controle.AdminController;
import controle.LoginControler;
import modele.beans.*;
import vue.bibliothecaire.BibliothecaireVue;

public class AdminVue extends JFrame {

    private static final long serialVersionUID = 1L;
    private AdminController controleur ;

    // Declare all components
    private JTabbedPane tabbedPane;
   
    // Components for Membres panel
    private JTextField nomField, prenomField, cniField, emailField, passwordField, telephoneField, adresseField,rechercheMembre;
    private JRadioButton maleButton, femaleButton;
    private ButtonGroup sexeGroup;
    private JComboBox<String> etatComboBox;
    
    private JFormattedTextField dateInscriptionField, dateNaissanceField;
    private JButton genererPasswordButton, ajouterMembreButton, annulerMembreButton,imagebtn;
    private JTable membreTable;

    private JTextField nomBibliField, prenomBibliField, cniBibliField, adresseBibliField, telephoneBibliField, emailBibliField, passwordBibliField, salaireBibliField;
    private JRadioButton maleBibliButton, femaleBibliButton;
    private ButtonGroup sexeBibliGroup;
    private JFormattedTextField dateEmbaucheField;
    private JButton genererPasswordBibliButton, ajouterBibliButton, annulerBibliButton;
    private JTable bibliothecaireTable;
    private DefaultTableModel tableBiblioModel;

    public AdminVue(List<Membre> membres, List<Bibliothecaire> bibliothecaires) {
    	controleur = new AdminController();
        setTitle("Gestion des Utilisateurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        initComponents(membres, bibliothecaires);
        setVisible(true);
        ImageIcon icon = new ImageIcon("src/vue/icons/EMSI.jpg"); 
        setIconImage(icon.getImage());
        
    }

    
    private void initComponents(List<Membre> membres, List<Bibliothecaire> bibliothecaires) {
        tabbedPane = new JTabbedPane();

        // Initialize and add panels to the tabbed pane
        JPanel membrePanel = createMembrePanel(membres);
        JPanel bibliothecairePanel = createBibliothecairePanel(bibliothecaires);

        tabbedPane.addTab("Gestion des Membres", membrePanel);
        tabbedPane.addTab("Gestion des Bibliothécaires", bibliothecairePanel);
        JMenuBar menuBar = new JMenuBar();

        // Create the "File" menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Create the "Log Out" menu item
        JMenuItem logOutMenuItem = new JMenuItem("Log Out");
        fileMenu.add(logOutMenuItem);

        // Add action listener for the "Log Out" menu item
        logOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle log out action
                int response = JOptionPane.showConfirmDialog(AdminVue.this, 
                        "Êtes-vous sûr de vouloir vous déconnecter ?", 
                        "Confirmation de déconnexion", 
                        JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    LoginControler.main(null);
                    
                }
            }
        });
        setJMenuBar(menuBar);
        add(tabbedPane);
    }

    // Membres panel creation and functionalities
    private JPanel createMembrePanel(List<Membre> membres) {
        JPanel panel = new JPanel(new BorderLayout());
        initMembreComponents();
        JPanel sidebar = createMembreSidebar();
        JPanel tablePanel = createMembreTablePanel(membres);
      
        
        
        
        panel.add(sidebar, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
             
    }

    private void initMembreComponents() {
	   nomField = createStyledTextField();
	    prenomField = createStyledTextField();
	    cniField = createStyledTextField();
	    emailField = createStyledTextField();
	    passwordField = createStyledTextField();
	    telephoneField = createStyledTextField();
	    adresseField = createStyledTextField();

	    maleButton = createStyledRadioButton("M ♀");
	    femaleButton = createStyledRadioButton("F ♂️");
	    sexeGroup = new ButtonGroup();
	    sexeGroup.add(maleButton);
	    sexeGroup.add(femaleButton);

	    // Create combo box with styling
	    etatComboBox = new JComboBox<>(new String[]{"Actif", "Inactif"});
	    etatComboBox.setBackground(Color.WHITE);
	    etatComboBox.setForeground(Color.BLACK);
	    etatComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

	    // Create formatted text fields with styling
	    dateInscriptionField = createStyledFormattedTextField();
	    dateNaissanceField = createStyledFormattedTextField();
	     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    dateInscriptionField.setText(LocalDate.now().format(formatter));
        genererPasswordButton = createStyledButton("Générer Mot de Passe ✨", new Color(59, 30, 84),Color.white);
        ajouterMembreButton=new JButton("Ajouter ➕");
        ajouterMembreButton.setBackground(Color.gray);
        ajouterMembreButton.setForeground(Color.white);
        ajouterMembreButton.setFocusPainted(false);
        ajouterMembreButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        ajouterMembreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ajouterMembreButton.setOpaque(true);
        ajouterMembreButton.setContentAreaFilled(true);
        ajouterMembreButton.setBorderPainted(false);
        annulerMembreButton = createStyledButton("Annuler ❌", Color.RED,Color.white);

        genererPasswordButton.addActionListener(e -> passwordField.setText(generateRandomPassword()));
        ajouterMembreButton.addActionListener(this::handleAjouterMembre);
        annulerMembreButton.addActionListener(this::handleAnnulerMembre);
        setupValidationListeners();
    }

    private JPanel createMembreSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBorder(BorderFactory.createTitledBorder("Ajouter un Nouveau Membre "));
        sidebar.setLayout(new GridLayout(0, 2, 5, 5));
        sidebar.setPreferredSize(new Dimension(400, 800));
        sidebar.add(createLabeledField("Nom :", nomField));
        sidebar.add(createLabeledField("Prénom :", prenomField));
        sidebar.add(createLabeledField("CNI :", cniField));

        sidebar.add(createLabeledField("Sexe", maleButton, femaleButton));
        sidebar.add(createLabeledField("Date de Naissance 📅 :", dateNaissanceField));
        sidebar.add(createLabeledField("Téléphone 📞 :", telephoneField));
        sidebar.add(createLabeledField("Adresse 📍 :", adresseField));
        sidebar.add(createLabeledField("État :", etatComboBox));
        sidebar.add(createLabeledField("Date d'Inscription 📅 :", dateInscriptionField));
        sidebar.add(createLabeledField("Email 📧 :", emailField));
        sidebar.add(createLabeledField("Mot de Passe 🔒 :", passwordField));
        
        maleButton.setSelected(true);
        JPanel genererPasswordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        genererPasswordPanel.add(".",genererPasswordButton);
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(ajouterMembreButton,BorderLayout.NORTH);
        controlPanel.add(annulerMembreButton,BorderLayout.SOUTH);

        
        sidebar.add(genererPasswordPanel);
        sidebar.add(controlPanel);

        ajouterMembreButton.setEnabled(false);


        return sidebar;
    }

    private JPanel createMembreTablePanel(List<Membre> membres) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"ID", "Nom", "Prénom", "CNI", "Sexe", "Adresse", "Téléphone", "Date d'Inscription", "Email", "État", "Profile"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Populate the table model with Membre data
        for (Membre m : membres) {
            Object[] row = {
                m.getId_membre(),
                m.getNom(),
                m.getPrenom(),
                m.getCni(),
                m.getSexe(),
                m.getAddresse(),
                m.getN_tele(),
                m.getDate_inscription(),
                m.getEmail(),
                m.getStatus(),
                "👤‍"
            };
            tableModel.addRow(row);
        }

        // Create the JTable with the model
        membreTable = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10; // Only the last column is editable
            }
        };

        // Add a TableRowSorter for search functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        membreTable.setRowSorter(sorter);

        // Style the table (assuming styleTable is defined elsewhere)
        styleTable(membreTable);

        // Create a search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLbl = new JLabel("Recherche 🔎 ");
        JTextField rechercheMembre = new JTextField(20); // Search field with fixed width
        searchPanel.add(searchLbl);
        searchPanel.add(rechercheMembre);

        // Add a KeyListener to the search field
        rechercheMembre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = rechercheMembre.getText().trim();
                if (searchText.isEmpty()) {
                    sorter.setRowFilter(null); // Show all rows if the field is empty
                } else {
                    // Create a RowFilter that checks all columns
                    sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                        @Override
                        public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                            for (int i = 0; i < entry.getValueCount(); i++) {
                                String cellValue = entry.getStringValue(i);
                                if (cellValue != null && cellValue.toLowerCase().contains(searchText.toLowerCase())) {
                                    return true; // If any cell contains the search text, include the row
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });

        // Add button renderer and editor for the last column
        membreTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRenderer());
        membreTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditor(new JTextField(), this, membreTable));

        // Create a JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(membreTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.NORTH); // Add the search panel to the top

        return tablePanel;
    }
    private void handleAjouterMembre(ActionEvent e) {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String cni = cniField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String sexe = maleButton.isSelected() ? "Male" : "w";
        String etat = etatComboBox.getSelectedItem().toString();
        String dateNaissance = dateNaissanceField.getText();
        String dateInscription = dateInscriptionField.getText();
        if (validateMembreFields()) {
            Membre membre = new Membre();
            membre.setNom(nom);
            membre.setPrenom(prenom);
            membre.setCni(cni);
            membre.setN_tele(telephone);
            membre.setAddresse(adresse);
            membre.setEmail(email);
            membre.setPaswd(password);
            membre.setSexe(sexe);
            membre.setStatus(etat);
            membre.dateNaissance(dateNaissance);
            membre.setDate_inscription(dateInscription);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images JPG et PNG", "jpg", "png"));
            int returnValue = fileChooser.showOpenDialog(this);
            
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String imageName = nom + "_" + prenom + getFileExtension(selectedFile.getName());
                File destinationFile = new File("src\\vue\\MembreImage\\"+ imageName);

                try {
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    membre.setPhoto(destinationFile.getAbsolutePath()); // Assurez-vous que Membre a un champ pour stocker le chemin de l'image

                    if (controleur.newMembre(membre)) {
                        JOptionPane.showMessageDialog(this, "Membre ajouté avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        handleAnnulerMembre(e);
                        RefreshMembreTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du membre.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la copie de l'image : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                } 
            }
        }
        
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // Pas d'extension
        }
        return fileName.substring(lastIndexOfDot); // Retourne l'extension (ex: .jpg ou .png)
    }
    private boolean validateMembreFields() {
        boolean allValid = true;

        // Validation du champ "Nom" (Exemple : Lettres uniquement, 2-30 caractères)
        if (!nomField.getText().matches("[a-zA-Z]{2,15}")) {
            nomField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            nomField.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        // Validation du champ "Prénom"
        if (!prenomField.getText().matches("[a-zA-Z]{2,15}")) {
            prenomField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            prenomField.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        // Validation du champ "CNI" (Exemple : 2 lettres suivies de 3 à 8 chiffres)
        if (!cniField.getText().matches("[a-zA-Z]{2}\\d{3,8}")) {
            cniField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            cniField.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        // Validation du champ "Email"
        if (!emailField.getText().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,6}$")) {
            emailField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            emailField.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        // Validation du champ "Téléphone" (Exemple : 10 chiffres)
        if (!telephoneField.getText().matches("\\d{10}")) {
            telephoneField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            telephoneField.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        // Validation du champ "Adresse" (Non vide)
        if (adresseField.getText().trim().isEmpty()) {
            adresseField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            adresseField.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        // Validation du champ "Mot de passe" (Min 8 caractères)
        if (passwordField.getText().trim().isEmpty() || !passwordField.getText().matches("^.{8,}$")) {
            passwordField.setBorder(BorderFactory.createLineBorder(Color.RED)); // Invalid input
            allValid = false;
        } else {
            passwordField.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Valid input
        }


        if (!dateNaissanceField.getText().matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            dateNaissanceField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            dateNaissanceField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
        if(allValid) {
            ajouterMembreButton.setBackground(new Color(76, 175, 80));
            ajouterMembreButton.setForeground(Color.white);
            ajouterMembreButton.setFocusPainted(false);
            ajouterMembreButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            ajouterMembreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ajouterMembreButton.setOpaque(true);
            ajouterMembreButton.setContentAreaFilled(true);
            ajouterMembreButton.setBorderPainted(false);
        }else {
            ajouterMembreButton.setBackground(Color.gray);
            ajouterMembreButton.setForeground(Color.white);
            ajouterMembreButton.setFocusPainted(false);
            ajouterMembreButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            ajouterMembreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ajouterMembreButton.setOpaque(true);
            ajouterMembreButton.setContentAreaFilled(true);
            ajouterMembreButton.setBorderPainted(false);
        }

        return allValid;
    }


    private void setupValidationListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateButtonState();
            }
        };

        nomField.getDocument().addDocumentListener(listener);
        prenomField.getDocument().addDocumentListener(listener);
        cniField.getDocument().addDocumentListener(listener);
        emailField.getDocument().addDocumentListener(listener);
        telephoneField.getDocument().addDocumentListener(listener);
        adresseField.getDocument().addDocumentListener(listener);
        dateNaissanceField.getDocument().addDocumentListener(listener);
        passwordField.getDocument().addDocumentListener(listener);
    }

    private void updateButtonState() {
    	ajouterMembreButton.setEnabled(validateMembreFields());
    }
    private void RefreshMembreTable() {
        DefaultTableModel tableModel = (DefaultTableModel) membreTable.getModel();
        tableModel.setRowCount(0); // Efface toutes les lignes existantes

        List<Membre> ms = controleur.getMembres(); 
        for (Membre membre : ms) {
            Object[] row = {
                membre.getId_membre(),
                membre.getNom(),
                membre.getPrenom(),
                membre.getCni(),
                membre.getSexe(),
                membre.getAddresse(),
                membre.getN_tele(),
                membre.getDate_inscription(),
                membre.getEmail(),
                membre.getStatus(),
                "👤"
            };
            tableModel.addRow(row); 
            
            
        }
      
        membreTable.repaint(); 
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
        private JButton button;
        private String label;
        private boolean clicked;
        private int selectedRow;
        private JTable table;
        private JFrame parentFrame;

        public ButtonEditor(JTextField textField, JFrame parentFrame, JTable table) {
            super(textField);
            this.parentFrame = parentFrame;
            this.table = table;
            button = new JButton();
            button.setOpaque(true);

            // Add action listener directly to the button
            button.addActionListener(e -> {
                int memberId = (int) table.getValueAt(selectedRow, 0); // Assuming ID is in column 0
                Membre membre = controleur.getMembreById(memberId);
                afficherFenetreDetails(membre);
                fireEditingStopped(); // Stop editing after clicking
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = value != null ? value.toString() : "";
            button.setText(label);
            clicked = true;
            selectedRow = row; // Get the selected row directly
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            clicked = false; // Reset clicked state
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

 
        

     
        private void afficherFenetreDetails(Membre membre) {
            JFrame detailFrame = new JFrame("Profile");
            AdminVue.this.setEnabled(false);
            detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            detailFrame.setSize(750, 700);
            detailFrame.setLocationRelativeTo(null);
            
            ImageIcon icon = new ImageIcon("src/vue/icons/profile.jpg"); 
            detailFrame.setIconImage(icon.getImage());
            // Use GridBagLayout for better control over component placement
            detailFrame.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5); // Padding around components

            // Create components
         // Create components using the styled text field method
            JTextField nomField = createStyledTextField();
            nomField.setText(membre.getNom());

            JTextField prenomField = createStyledTextField();
            prenomField.setText(membre.getPrenom());

            JTextField cniField = createStyledTextField();
            cniField.setText(membre.getCni());

            JFormattedTextField dateNaissanceFieldFenetre = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
            dateNaissanceFieldFenetre.setText(membre.getDate_n());
            dateNaissanceFieldFenetre.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for date field
            dateNaissanceFieldFenetre.setPreferredSize(new Dimension(200, 30)); // Set preferred size

            JTextField emailField = createStyledTextField();
            emailField.setText(membre.getEmail());

            JTextField paswdField = createStyledTextField(); // Empty password field

            JTextField telephoneField = createStyledTextField();
            telephoneField.setText(membre.getN_tele());

            JTextField adresseField = createStyledTextField();
            adresseField.setText(membre.getAddresse());

            JFormattedTextField dateInscriptionFieldFenetre = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
            dateInscriptionFieldFenetre.setText(membre.getDate_inscription());
            dateInscriptionFieldFenetre.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font for date field
            dateInscriptionFieldFenetre.setPreferredSize(new Dimension(200, 30)); // Set preferred size

            // Gender selection
            JRadioButton maleButtonFenetre = new JRadioButton("Male");
            JRadioButton femaleButtonFenetre = new JRadioButton("Female");
            ButtonGroup sexeGroupFenetre = new ButtonGroup();
            sexeGroupFenetre.add(maleButtonFenetre);
            sexeGroupFenetre.add(femaleButtonFenetre);
            if (membre.getSexe().equals("Male")) {
                maleButtonFenetre.setSelected(true);
            } else {
                femaleButtonFenetre.setSelected(true);
            }

            // Image display
            JLabel imageLabel = new JLabel();
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.CENTER);
            loadImage(membre.getPhoto(), imageLabel);

            // Add components to the frame
            addComponent(detailFrame, imageLabel, gbc, 0, 0, 2, 1);
            addComponent(detailFrame, new JLabel("Nom :"), gbc, 0, 1);
            addComponent(detailFrame, nomField, gbc, 1, 1);
            addComponent(detailFrame, new JLabel("Prénom :"), gbc, 0, 2);
            addComponent(detailFrame, prenomField, gbc, 1, 2);
            addComponent(detailFrame, new JLabel("CNI :"), gbc, 0, 3);
            addComponent(detailFrame, cniField, gbc, 1, 3);
            addComponent(detailFrame, new JLabel("Sexe :"), gbc, 0, 4);
            addComponent(detailFrame, maleButtonFenetre, gbc, 1, 4);
            addComponent(detailFrame, femaleButtonFenetre, gbc, 1, 5);
            addComponent(detailFrame, new JLabel("Date de Naissance :"), gbc, 0, 6);
            addComponent(detailFrame, dateNaissanceFieldFenetre, gbc, 1, 6);
            addComponent(detailFrame, new JLabel("Email :"), gbc, 0, 7);
            addComponent(detailFrame, emailField, gbc, 1, 7);
            addComponent(detailFrame, new JLabel("Mot de passe :"), gbc, 0, 8);
            addComponent(detailFrame, paswdField, gbc, 1, 8);
            addComponent(detailFrame, new JLabel("Téléphone :"), gbc, 0, 9);
            addComponent(detailFrame, telephoneField, gbc, 1, 9);
            addComponent(detailFrame, new JLabel("Adresse :"), gbc, 0, 10);
            addComponent(detailFrame, adresseField, gbc, 1, 10);
            addComponent(detailFrame, new JLabel("Date d'inscription :"), gbc, 0, 11);
            addComponent(detailFrame, dateInscriptionFieldFenetre, gbc, 1, 11);
            
            // Status ComboBox
            JComboBox<String> etatComboBoxFenetre = new JComboBox<>(new String[]{"Actif", "Inactif"});
            if (membre.getStatus().equals("Inactif")) {
                etatComboBoxFenetre.setSelectedItem("Inactif");
            }
            addComponent(detailFrame, new JLabel("Statut :"), gbc, 0, 12);
            addComponent(detailFrame, etatComboBoxFenetre, gbc, 1, 12);

            // Buttons
            JPanel buttonPanel = new JPanel();

            
            JButton modifierButton = createStyledButton("Enregistrer 💾", new Color(92, 179, 56),Color.white);
            JButton supprimerButton = createStyledButton("Supprimer 🗑️", new Color(250, 64, 50),Color.white);
            JButton reinitialiserMdpsButton = createStyledButton("Réinitialiser Mot de passe 🗘", new Color(59, 30, 84),Color.white);
            JButton annulerButton = createStyledButton("Annuler ❌", new Color(180, 63, 63),Color.white);
            JButton recuButton = createStyledButton("Imprimer Carte 📄", new Color(249, 115, 0),Color.white);
            
            reinitialiserMdpsButton.addActionListener(e -> paswdField.setText(generateRandomPassword()));
            
            buttonPanel.add(modifierButton);
            buttonPanel.add(supprimerButton);
            buttonPanel.add(reinitialiserMdpsButton);
            buttonPanel.add(recuButton);
            buttonPanel.add(annulerButton);
            
            addComponent(detailFrame, buttonPanel, gbc, 0, 13, 2, 1);
//-----------
            
            annulerButton.addActionListener(e -> {
            	this.setEnabled(true);
            	detailFrame.dispose();
            });

            modifierButton.addActionListener(e -> {
                membre.setNom(nomField.getText());
                membre.setPrenom(prenomField.getText());
                membre.setCni(cniField.getText());
                membre.setSexe(maleButtonFenetre.isSelected() ? "Male" : "Female");
                membre.setDate_n(dateNaissanceFieldFenetre.getText());
                membre.setEmail(emailField.getText());
                membre.setPaswd(paswdField.getText());
                membre.setN_tele(telephoneField.getText());
                membre.setAddresse(adresseField.getText());
                membre.setDate_inscription(dateInscriptionFieldFenetre.getText());
                membre.setStatus(etatComboBoxFenetre.getSelectedItem().toString());

                if (controleur.updateMembre(membre)) {
                	 JOptionPane.showMessageDialog(this, "Membre ajouté avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                     handleAnnulerMembre(e);
                     RefreshMembreTable();
                     membreTable.revalidate();
                     membreTable.repaint(); // Force table redraw
                } else {
                    JOptionPane.showMessageDialog(detailFrame, "Erreur lors de la modification.");
                }
            });

            supprimerButton.addActionListener(e -> {
                int confirmation = JOptionPane.showConfirmDialog(
                    detailFrame,
                    "Êtes-vous sûr de vouloir supprimer le membre : ("+membre.getNom()+" "+membre.getPrenom()+")?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirmation == JOptionPane.YES_OPTION) {
                    if (controleur.supprimerMembre(membre.getId_membre())) {
                        JOptionPane.showMessageDialog(detailFrame, "("+membre.getNom()+" "+membre.getPrenom()+") supprimé avec succès !");
                        this.setEnabled(true);
                        RefreshMembreTable();
                        detailFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(detailFrame, "Erreur lors de la suppression.");
                    }
                }
            });

            recuButton.addActionListener(e -> {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) {
                        return Printable.NO_SUCH_PAGE;
                    }

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    // Set background color
                    g2d.setColor(new Color (245, 245, 245));
                    g2d.fillRect(0, 0, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());

                    // Draw header/logo and library name
                    try {
                        Image logo = Toolkit.getDefaultToolkit().getImage("src/vue/icons/logo.jpg");
                        int logoWidth = 100;
                        int logoHeight = 50;
                        int logoX = 20;
                        int logoY = 20;
                        g2d.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);
                    } catch (Exception ex) {
                        System.err.println("Erreur lors du chargement de l'image : " + ex.getMessage());
                    }

                    g2d.setFont(new Font("Arial", Font.BOLD, 24));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("EMSI 4IIR Bibliotheque", 140, 50);

                    g2d.setFont(new Font("Arial", Font.PLAIN, 16));
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawString("Membre Du Bibliothèque", 140, 70);

                    // Draw a horizontal divider
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(20, 90, (int) pageFormat.getImageableWidth() - 40, 2);

                    // Draw member photo
                    int photoX = 20;
                    int photoY = 110;
                    int photoWidth = 100;
                    int photoHeight = 100;

                    try {
                        Image memberPhoto = Toolkit.getDefaultToolkit().getImage(membre.getPhoto());
                        g2d.drawImage(memberPhoto, photoX, photoY, photoWidth, photoHeight, null);
                    } catch (Exception ex) {
                        System.err.println("Erreur lors du chargement de l'image du membre : " + ex.getMessage());
                    }

                    // Draw member details
                    int detailX = photoX + photoWidth + 20;
                    int detailY = photoY; 

                    g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                    g2d.setColor(Color.BLACK);

                    String[] labels = {
                        "Nom : " + membre.getNom(),
                        "Prénom : " + membre.getPrenom(),
                        "CNI : " + membre.getCni(),
                        "Sexe : " + membre.getSexe(),
                        "Date de Naissance : " + membre.getDate_n(),
                        "Email : " + membre.getEmail(),
                        "Téléphone : " + membre.getN_tele(),
                        "Adresse : " + membre.getAddresse(),
                        "Date d'inscription : " + membre.getDate_inscription()
                    };

                    for (String label : labels) {
                        g2d.drawString(label, detailX, detailY);
                        detailY += 20;
                    }

                    // Draw footer
                    int footerY = (int) pageFormat.getImageableHeight() - 50;
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.fillRect(20, footerY, (int) pageFormat.getImageableWidth() - 40, 40);

                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("Merci pour votre confiance en EIM", 30, footerY + 25);

                    return Printable.PAGE_EXISTS;
                });

                boolean doPrint = job.printDialog();
                if (doPrint) {
                    try {
                        job.print();
                    } catch (PrinterException ex) {
                        System.err.println("Erreur d'impression : " + ex.getMessage());
                    }
                }
            });



            detailFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    AdminVue.this.setEnabled(true);
                }

                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    AdminVue.this.setEnabled(true);
                }
            });
            detailFrame.setVisible(true);
        }

        private void loadImage(String imagePath, JLabel imageLabel) {
            try {
                BufferedImage memberImage = ImageIO.read(new File(imagePath));
                int originalWidth = memberImage.getWidth();
                int originalHeight = memberImage.getHeight();
                double aspectRatio = (double) originalWidth / originalHeight;
                int newWidth = 100;
                int newHeight = (int) (newWidth / aspectRatio);
                Image scaledImage = memberImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setPreferredSize(new Dimension(newWidth, newHeight));
            } catch (Exception ex) {
                System.err.println("Erreur lors du chargement de l'image du membre : " + ex.getMessage());
            }
        }

        private void addComponent(JFrame frame, Component component, GridBagConstraints gbc, int gridx, int gridy) {
            addComponent(frame, component, gbc, gridx, gridy, 1, 1);
        }

        private void addComponent(JFrame frame, Component component, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
            gbc.gridx = gridx;
            gbc.gridy = gridy;
            gbc.gridwidth = gridwidth;
            gbc.gridheight = gridheight;
            frame.add(component, gbc);
        }
    



    private void handleAnnulerMembre(ActionEvent e) {
    	nomField.setText("");
    	prenomField.setText("");
    	cniField.setText("");
    	emailField.setText("");
    	passwordField.setText("");
    	telephoneField.setText("");
    	adresseField.setText("");
    	dateNaissanceField.setText("");
		nomField.setBorder(BorderFactory.createLineBorder(Color.black));
		prenomField.setBorder(BorderFactory.createLineBorder(Color.black));
		cniField.setBorder(BorderFactory.createLineBorder(Color.black));
		emailField.setBorder(BorderFactory.createLineBorder(Color.black));
		telephoneField.setBorder(BorderFactory.createLineBorder(Color.black));
		adresseField.setBorder(BorderFactory.createLineBorder(Color.black));
		passwordField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        dateNaissanceField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    private String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }


    private JPanel createBibliothecairePanel(List<Bibliothecaire> bibliothecaires) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel sidebar = createBibliothecaireSidebar();
        JPanel tablePanel = createBibliothecaireTablePanel(bibliothecaires);
        
        panel.add(sidebar, BorderLayout.WEST);
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBibliothecaireSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 2, 10, 10));
        sidebar.setPreferredSize(new Dimension(400, 800));
        sidebar.setBorder(BorderFactory.createTitledBorder("Ajouter un Nouveau Bibliothécaire"));
        sidebar.setBackground(new Color(240, 240, 240)); // Light gray background

        // Create text fields
        nomBibliField = createStyledTextField();
        prenomBibliField = createStyledTextField();
        cniBibliField = createStyledTextField();
        adresseBibliField = createStyledTextField();
        telephoneBibliField =createStyledTextField();
        emailBibliField = createStyledTextField();
        passwordBibliField = createStyledTextField();
        salaireBibliField = createStyledTextField();
        dateEmbaucheField = createStyledFormattedTextField();

        maleBibliButton = new JRadioButton("M ♂️");
        femaleBibliButton = new JRadioButton("F ♀");
        sexeBibliGroup = new ButtonGroup();
        sexeBibliGroup.add(maleBibliButton);
        sexeBibliGroup.add(femaleBibliButton);

        // Set columns for text fields
        nomBibliField.setColumns(15);
        prenomBibliField.setColumns(15);
        cniBibliField.setColumns(15);
        adresseBibliField.setColumns(15);
        telephoneBibliField.setColumns(15);
        emailBibliField.setColumns(15);
        passwordBibliField.setColumns(15);
        salaireBibliField.setColumns(15);
        dateEmbaucheField.setColumns(15);
        
        maleBibliButton.setSelected(true);
        
        // Add labeled fields to sidebar
        sidebar.add(createLabeledField("Nom :", nomBibliField));
        sidebar.add(createLabeledField("Prénom :", prenomBibliField));
        sidebar.add(createLabeledField("CNI :", cniBibliField));
        sidebar.add(createLabeledField("Sexe :", maleBibliButton, femaleBibliButton));
        sidebar.add(createLabeledField("Date d'Embauche 🗓️ :", dateEmbaucheField));
        sidebar.add(createLabeledField("Téléphone 📞:", telephoneBibliField));
        sidebar.add(createLabeledField("Adresse 📍:", adresseBibliField));
        sidebar.add(createLabeledField("Salaire 💲:", salaireBibliField));
        sidebar.add(createLabeledField("Email ✉ :", emailBibliField));
        sidebar.add(createLabeledField("Mot de Passe 🔓 :", passwordBibliField));

        // Button panel
        JPanel passwdGenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel controlBtnPanel = new JPanel(new BorderLayout());

        genererPasswordBibliButton = createStyledButton("Générer Mot de Passe ✨", new Color(59, 30, 84),Color.white);
        ajouterBibliButton = new JButton("Ajouter ➕"); 
        ajouterBibliButton.setBackground(Color.gray);
        ajouterBibliButton.setForeground(Color.white);
        ajouterBibliButton.setFocusPainted(false);
        ajouterBibliButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        ajouterBibliButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ajouterBibliButton.setOpaque(true);
        ajouterBibliButton.setContentAreaFilled(true);
        ajouterBibliButton.setBorderPainted(false);
        
        annulerBibliButton = createStyledButton("Annuler ❌", Color.RED,Color.white);
        passwdGenPanel.add(genererPasswordBibliButton);
        controlBtnPanel.add(ajouterBibliButton,BorderLayout.NORTH);
        controlBtnPanel.add(annulerBibliButton,BorderLayout.SOUTH);

        sidebar.add(passwdGenPanel);
        sidebar.add(controlBtnPanel);

        // Action listeners
        genererPasswordBibliButton.addActionListener(e -> passwordBibliField.setText(generateRandomPassword()));
        ajouterBibliButton.addActionListener(e->{
        	handleAjouterBibli(e);
 
        });
        annulerBibliButton.addActionListener(this::handleAnnulerBibli);
        setupValidationListenersBiblio();
        
        return sidebar;
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text, Color backgroundColor,Color frontColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(frontColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        
        return button;
    }

    private JPanel createBibliothecaireTablePanel(List<Bibliothecaire> bibliothecaires) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        String[] columnNames = {"ID", "Nom", "Prénom", "CNI", "Sexe", "Adresse", "Téléphone", "Date d'Embauche", "Email", "Salaire", "Profile"};
        tableBiblioModel = new DefaultTableModel(columnNames, 0); // Initialize the table model

        // Populate the table model with Bibliothecaire data
        for (Bibliothecaire b : bibliothecaires) {
            Object[] row = {
                b.getId(),
                b.getNom(),
                b.getPrenom(),
                b.getCni(),
                b.getSexe(),
                b.getAdresse(),
                b.getTelephone(),
                b.getDateEmbauche(),
                b.getEmail(),
                b.getSalaire(),
                "👤" 
            };
            tableBiblioModel.addRow(row);
        }

        // Create the JTable with the model
        bibliothecaireTable = new JTable(tableBiblioModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10; // Only the last column is editable
            }
        };

        // Create a TableRowSorter for search functionality
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableBiblioModel);
        bibliothecaireTable.setRowSorter(sorter);

        // Create a search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLbl = new JLabel("Recherche:");
        JTextField rechercheBiblio = new JTextField(20); // Search field with fixed width
        searchPanel.add(searchLbl);
        searchPanel.add(rechercheBiblio);

        // Add a KeyListener to the search field
        rechercheBiblio.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = rechercheBiblio.getText().trim();
                if (searchText.isEmpty()) {
                    sorter.setRowFilter(null); // Show all rows if the field is empty
                } else {
                    // Create a RowFilter that checks all columns
                    sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                        @Override
                        public boolean include(RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                            for (int i = 0; i < entry.getValueCount(); i++) {
                                String cellValue = entry.getStringValue(i);
                                if (cellValue != null && cellValue.toLowerCase().contains(searchText.toLowerCase())) {
                                    return true; // If any cell contains the search text, include the row
                                }
                            }
                            return false; // Exclude the row if no cell matches
                        }
                    });
                }
            }
        });

        // Style the table (assuming styleTable is defined elsewhere)
        styleTable(bibliothecaireTable);

        // Set up button renderer and editor for the last column
        bibliothecaireTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonRendererBiblio());
        bibliothecaireTable.getColumnModel().getColumn(10).setCellEditor(new ButtonEditorBiblio(new JCheckBox(), this));

        // Create a JScrollPane for the table
        JScrollPane scrollPane = new JScrollPane(bibliothecaireTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(searchPanel, BorderLayout.NORTH); // Add the search panel to the top

        return tablePanel;
    }

    // ButtonRendererBiblio class
    class ButtonRendererBiblio extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public ButtonRendererBiblio() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "👁️");
            return this;
        }
    }

    // ButtonEditorBiblio class
    class ButtonEditorBiblio extends DefaultCellEditor {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JButton button;
        private boolean clicked;
        private JFrame parentFrame;

        public ButtonEditorBiblio(JCheckBox checkBox, JFrame parentFrame) {
            super(checkBox);
            this.parentFrame = parentFrame;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped(); // Stop editing and notify listeners
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(value != null ? value.toString() : "👁️");
            clicked = true;
            int id = (int) table.getValueAt(row, 0); 
            Bibliothecaire bibliothecaire = controleur.getBibliothecairesById(id); // Fetch bibliothecaire details
            afficherFenetreDetailsBibliothecaire(bibliothecaire); // Show details in a new window

            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // Perform any action if needed
            }
            clicked = false;
            return button.getText(); // Return the button text
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    public void afficherFenetreDetailsBibliothecaire(Bibliothecaire bibliothecaire) {
        AdminVue.this.setEnabled(false);

        JFrame detailsFrame= new JFrame("Détails du Bibliothécaire");
        detailsFrame.setLayout(new BorderLayout());
        detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailsFrame.setSize(700, 500);
        detailsFrame.setLayout(new BorderLayout());
        detailsFrame.setLocationRelativeTo(null);
        ImageIcon icon = new ImageIcon("src/vue/icons/profile.jpg"); 
        detailsFrame.setIconImage(icon.getImage());
        
        
        JTextField nomF = new JTextField(bibliothecaire.getNom());
        JTextField prenomF = new JTextField(bibliothecaire.getPrenom());
        JTextField cniF = new JTextField(bibliothecaire.getCni());
        JTextField sexeF = new JTextField(bibliothecaire.getSexe());
        JTextField adresseF = new JTextField(bibliothecaire.getAdresse());
        JTextField telephoneF = new JTextField(bibliothecaire.getTelephone());
        JTextField dateEmbaucheF = new JTextField(bibliothecaire.getDateEmbauche());
        JTextField salaireF = new JTextField(String.valueOf(bibliothecaire.getSalaire()));
        JTextField emailF = new JTextField(bibliothecaire.getEmail());
        JTextField passwordF = new JTextField();
        
        
         JPanel  lblFldPanel =new JPanel(new GridLayout(11,2,5,5));
        
         lblFldPanel.add(new JLabel("ID:"));
         lblFldPanel.add(new JLabel(String.valueOf(bibliothecaire.getId())));
         lblFldPanel.add(new JLabel("Nom:"));
         lblFldPanel.add(nomF);
         lblFldPanel.add(new JLabel("Prénom:"));
         lblFldPanel.add(prenomF);
         lblFldPanel.add(new JLabel("CNI:"));
         lblFldPanel.add(cniF);
         lblFldPanel.add(new JLabel("Sexe:"));
         lblFldPanel.add(sexeF);
         lblFldPanel.add(new JLabel("Adresse:"));
         lblFldPanel.add(adresseF);
         lblFldPanel.add(new JLabel("Téléphone:"));
         lblFldPanel.add(telephoneF);
         lblFldPanel.add(new JLabel("Date d'Embauche:"));
         lblFldPanel.add(dateEmbaucheF);
         lblFldPanel.add(new JLabel("Salaire:"));
         lblFldPanel.add(salaireF);
         lblFldPanel.add(new JLabel("Email:"));
         lblFldPanel.add(emailF);
         lblFldPanel.add(new JLabel("Mot de passe:"));
         lblFldPanel.add(passwordF);
         
        JButton modifierButton = createStyledButton("Enregistrer ✎💾",new Color(92, 179, 56),Color.white);
        JButton supprimerButton = createStyledButton("Supprimer 🗑️", new Color(250, 64, 50),Color.white);
        JButton resetPasswordButton = createStyledButton("Réinitialiser Mot de Passe 🔄", new Color(59, 30, 84),Color.white);
        JButton closeButton = createStyledButton("Annuler ❌", new Color(180, 63, 63),Color.white);
        JPanel btnPanel=new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.add(new JLabel());
        btnPanel.add(modifierButton);
        btnPanel.add(supprimerButton);
        btnPanel.add(resetPasswordButton);
        btnPanel.add(closeButton);
        detailsFrame.add(lblFldPanel,BorderLayout.CENTER);
        detailsFrame.add(btnPanel,BorderLayout.SOUTH);
        
        modifierButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                detailsFrame,
                "Êtes-vous sûr de vouloir modifier " + bibliothecaire.getPrenom() + " " + bibliothecaire.getNom() + " ?",
                "Confirmation de modification",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                try {
                    // Créer un nouvel objet Bibliothecaire avec les nouvelles données
                    Bibliothecaire b = new Bibliothecaire(
                        bibliothecaire.getId(),
                        nomF.getText(),
                        prenomF.getText(),
                        cniF.getText(),
                        sexeF.getText(),
                        adresseF.getText(),
                        telephoneF.getText(),
                        dateEmbaucheF.getText(),
                        Double.parseDouble(salaireF.getText()), // Conversion du salaire en double
                        emailF.getText(),
                        passwordF.getText()
                    );

                    // Appeler la méthode de mise à jour
                    if (controleur.updateBibliothecaire(b)) {
                        JOptionPane.showMessageDialog(
                            detailsFrame, 
                            "Modification réussie !", 
                            "Succès", 
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        RefreshBiblioTable(); 
                        
                    } else {
                        JOptionPane.showMessageDialog(
                            detailsFrame, 
                            "Échec de la modification.", 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (NumberFormatException ex) {
                    // Gérer les cas où le salaire n'est pas un nombre valide
                    JOptionPane.showMessageDialog(
                        detailsFrame, 
                        "Le salaire doit être un nombre valide.", 
                        "Erreur de saisie", 
                        JOptionPane.ERROR_MESSAGE
                    );
                } catch (Exception ex) {
                    // Gérer d'autres exceptions inattendues
                    JOptionPane.showMessageDialog(
                        detailsFrame, 
                        "Une erreur inattendue s'est produite : " + ex.getMessage(), 
                        "Erreur", 
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(detailsFrame, "Modification annulée.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        


        supprimerButton.addActionListener(e -> {
        	
        	 int confirmation = JOptionPane.showConfirmDialog(
	        			 detailsFrame,
	                     "Êtes-vous sûr de vouloir supprimer "+bibliothecaire.getPrenom()+" "+bibliothecaire.getNom()+" ?",
	                     "Confirmation de suppression",
	                     JOptionPane.YES_NO_OPTION,
	                     JOptionPane.WARNING_MESSAGE
        			 );
             if (confirmation == JOptionPane.YES_OPTION) {
                 if (controleur.suprimerBibliothecaire(bibliothecaire.getId())) {
                     JOptionPane.showMessageDialog(detailsFrame, "Bibliothécaire supprimé avec succès !");
                     RefreshBiblioTable();
                     this.setEnabled(true);
                     detailsFrame.dispose();
                 } else {
                     JOptionPane.showMessageDialog(detailsFrame, "Erreur lors de la suppression.");
                 }
             }
                 
            
        });
        resetPasswordButton.addActionListener(e ->{
        	passwordF.setText(generateRandomPassword());
        });
        closeButton.addActionListener(e -> {
        	this.setEnabled(true);
        		detailsFrame.dispose();
        });

        detailsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                AdminVue.this.setEnabled(true);
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                AdminVue.this.setEnabled(true);
            }
        });
        
        detailsFrame.setVisible(true);
    }

    private void handleAjouterBibli(ActionEvent e) {
    	
    	String sexe;
    	if(maleBibliButton.isSelected()) {
    		sexe="Male";
    	}else {
    		sexe="Female";
    	}
    	if(validateBiblioFields()) {
	    		Bibliothecaire b = new Bibliothecaire(
				    			0,
				    			nomBibliField.getText(),
				    	        prenomBibliField.getText(),
				    	        cniBibliField.getText(),
				    	        sexe,
				    	        adresseBibliField.getText(),
				    	        telephoneBibliField.getText(),
				    	        dateEmbaucheField.getText(),
				    	        Double.parseDouble(salaireBibliField.getText()),
				    	        emailBibliField.getText(),
				    	        passwordBibliField.getText());
    		 	int confirmation = JOptionPane.showConfirmDialog(
    		 			this,
                     "Souhaitez-vous ajouter un nouveau bibliothécaire ("+b.getPrenom()+" "+b.getNom()+") ?",
                     "Confirmation l'ajout",
                     JOptionPane.YES_NO_OPTION,
                     JOptionPane.WARNING_MESSAGE
                 );
                 if (confirmation == JOptionPane.YES_OPTION) {
                     if (controleur.newBibliothecaire(b)) {
                         JOptionPane.showMessageDialog(this, "Bibliothécaire est ajouter avec succès !");
                         handleAnnulerBibli(e);
                         RefreshBiblioTable();
                         
                     } else {
                         JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout .");
                     }
                 }
    	}
				
    	
    }

    private void handleAnnulerBibli(ActionEvent e) {
		  nomBibliField.setText("");
		  prenomBibliField.setText("");
		  cniBibliField.setText("");
		  adresseBibliField.setText("");
		  telephoneBibliField.setText("");
		  emailBibliField.setText("");
		  passwordBibliField.setText("");
		  salaireBibliField.setText("");
      	nomBibliField.setBorder(BorderFactory.createLineBorder(Color.black));
    	prenomBibliField.setBorder(BorderFactory.createLineBorder(Color.black));
    	cniBibliField.setBorder(BorderFactory.createLineBorder(Color.black));
    	emailBibliField.setBorder(BorderFactory.createLineBorder(Color.black));
        telephoneField.setBorder(BorderFactory.createLineBorder(Color.black));
    	adresseBibliField.setBorder(BorderFactory.createLineBorder(Color.black));
    	telephoneBibliField.setBorder(BorderFactory.createLineBorder(Color.black));
    	passwordBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
    	salaireBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    	dateEmbaucheField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        ajouterBibliButton.setEnabled(false);
    }





    private JPanel createLabeledField(String label, JComponent... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        for (JComponent component : components) {
            panel.add(component);
        }
        return panel;
    }
    private void RefreshBiblioTable() {
        if (tableBiblioModel != null) {
        	tableBiblioModel.setRowCount(0); // Clear all existing rows

            List<Bibliothecaire> bibliothecaires = controleur.getBibliothecaires(); 
            for (Bibliothecaire bibliothecaire : bibliothecaires) {
                Object[] row = {
                    bibliothecaire.getId(),
                    bibliothecaire.getNom(),
                    bibliothecaire.getPrenom(),
                    bibliothecaire.getCni(),
                    bibliothecaire.getSexe(),
                    bibliothecaire.getAdresse(),
                    bibliothecaire.getTelephone(),
                    bibliothecaire.getDateEmbauche(),
                    bibliothecaire.getEmail(),
                    bibliothecaire.getSalaire(),
                    "👤" 
                };
                tableBiblioModel.addRow(row); 
            }
            bibliothecaireTable.repaint(); // Repaint the table to reflect changes
        } else {
            System.err.println("Table model is not initialized.");
        }
    }

    private boolean validateBiblioFields() {
        boolean allValid = true;

        // Validation du champ "Nom"
        if (!nomBibliField.getText().matches("[a-zA-Z]{2,15}")) {
            nomBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            nomBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Prénom"
        if (!prenomBibliField.getText().matches("[a-zA-Z]{2,15}")) {
            prenomBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            prenomBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "CNI"
        if (!cniBibliField.getText().matches("[a-zA-Z]{2}\\d{3,8}")) {
            cniBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            cniBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Email"
        if (!emailBibliField.getText().matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,6}$")) {
            emailBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            emailBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Téléphone"
        if (!telephoneBibliField.getText().matches("\\d{10}")) {
            telephoneBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            telephoneBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Adresse"
        if (adresseBibliField.getText().trim().isEmpty()) {
            adresseBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            adresseBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Mot de Passe"
        if (!passwordBibliField.getText().matches("^.{8,}$")) {
            passwordBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            passwordBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Salaire"
        if (!salaireBibliField.getText().matches("[0-9]{1,8}")) {
            salaireBibliField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            salaireBibliField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        // Validation du champ "Date d'Embauche"
        if (!dateEmbaucheField.getText().matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            dateEmbaucheField.setBorder(BorderFactory.createLineBorder(Color.RED));
            allValid = false;
        } else {
            dateEmbaucheField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }


        // Update button state based on validation
        if (allValid) {
            ajouterBibliButton.setBackground(new Color(76, 175, 80));
            ajouterBibliButton.setForeground(Color.white);
            ajouterBibliButton.setEnabled(true); // ajouterBibliButton.setFocusPainted(false);
            ajouterBibliButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            ajouterBibliButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ajouterBibliButton.setOpaque(true);
            ajouterBibliButton.setContentAreaFilled(true);
            ajouterBibliButton.setBorderPainted(false);
        } else {
            ajouterBibliButton.setBackground(Color.gray);
            ajouterBibliButton.setForeground(Color.white);
            ajouterBibliButton.setEnabled(false); // Ensure button is disabled if not valid
            ajouterBibliButton.setFocusPainted(false);
            ajouterBibliButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            ajouterBibliButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ajouterBibliButton.setOpaque(true);
            ajouterBibliButton.setContentAreaFilled(true);
            ajouterBibliButton.setBorderPainted(false);
        }

        return allValid;
    }
    
    
    private void setupValidationListenersBiblio() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { 
                ajouterBibliButton.setEnabled(validateBiblioFields());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	ajouterBibliButton.setEnabled(validateBiblioFields());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            	ajouterBibliButton.setEnabled(validateBiblioFields());
            }
            
        };

        nomBibliField.getDocument().addDocumentListener(listener);
        prenomBibliField.getDocument().addDocumentListener(listener);
        cniBibliField.getDocument().addDocumentListener(listener);
        emailBibliField.getDocument().addDocumentListener(listener);
        telephoneBibliField.getDocument().addDocumentListener(listener);
        adresseBibliField.getDocument().addDocumentListener(listener);
        dateEmbaucheField.getDocument().addDocumentListener(listener);
        salaireBibliField.getDocument().addDocumentListener(listener);
        passwordBibliField.getDocument().addDocumentListener(listener);
    
    }


    public static void styleTable(JTable table) {
    	    // Set table font and row height
    	    table.setFont(new Font("Lato",Font.CENTER_BASELINE, 13));
    	    table.setRowHeight(30);

    	    // Set header font and background color
    	    JTableHeader header = table.getTableHeader();
    	    header.setFont(new Font("Lato", Font.PLAIN, 16));
    	    header.setBackground(new Color(130, 77, 116));
    	    header.setForeground(Color.WHITE);

    	    // Set table grid colors
    	    table.setGridColor(new Color(83, 100, 147));
    	    table.setShowGrid(true);

    	    // Alternate row colors
    	    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    	        @Override
    	        public void setValue(Object value) {
    	            super.setValue(value);
    	            if (value != null && value.equals("Actif")) {
    	                setForeground(new Color(0, 128, 0)); // Green for "Actif"
    	            } else if (value != null && value.equals("Inactif")) {
    	                setForeground(Color.RED); // Red for "Inactif"
    	            } else {
    	                setForeground(Color.BLACK); // Default text color
    	            }
    	        }

    	        @Override
    	        public java.awt.Component getTableCellRendererComponent(
    	                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	            java.awt.Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	            if (!isSelected) {
    	                if (row % 2 == 0) {
    	                    component.setBackground(new Color(230, 240, 250)); // Light row color
    	                } else {
    	                    component.setBackground(new Color(210, 230, 250)); // Alternate row color
    	                }
    	            } else {
    	                component.setBackground(new Color(140, 200, 255)); // Selected row background
    	                setForeground(Color.WHITE); 
    	            }
    	            return component;
    	        }
    	    });
    }
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setBackground(Color.LIGHT_GRAY);
        radioButton.setForeground(Color.BLACK);
        radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        return radioButton;
    }

    private JFormattedTextField createStyledFormattedTextField() {
        JFormattedTextField formattedTextField = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
        formattedTextField.setColumns(15);
        formattedTextField.setBackground(Color.WHITE);
        formattedTextField.setForeground(Color.BLACK);
        formattedTextField.setFont(new Font("Arial", Font.PLAIN, 14));
        formattedTextField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return formattedTextField;
    }


}



