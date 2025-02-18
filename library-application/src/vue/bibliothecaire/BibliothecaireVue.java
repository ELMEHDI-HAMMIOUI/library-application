package vue.bibliothecaire;


import controle.BibliothecaireController;
import controle.LoginControler;
import modele.beans.Emprunt;
import modele.beans.Livre;
import modele.beans.Membre;
import modele.dao.implementations.EmpruntDaoImp;
import vue.interfaceGUI.admin.AdminVue;
import vue.interfaceGUI.generale.LoginVue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class BibliothecaireVue extends JFrame {
	private static final long serialVersionUID = 1L;
	BibliothecaireController controller;
    private JTextField titreField, auteurField, genreField,
                       isbnField, anneeField, exemplairesField,
                       tarifField, penaliteField, imagePathField;
    private JTable tableLivres;
    private DefaultTableModel tableModel;
    private JButton choisirImageButton,ajouterButton;
    private JTable tableRetourEmp,tableHystoriqueEmp;  
    private JTable membreTable;

    public BibliothecaireVue() {
        controller = new BibliothecaireController(); 

        setTitle("Gestion des Livres");
        setSize(1400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ImageIcon icon = new ImageIcon("src/vue/icons/EMSI.jpg"); 
        setIconImage(icon.getImage());

        // Create the menu bar
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
                int response = JOptionPane.showConfirmDialog(BibliothecaireVue.this, 
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
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Livres", createPanelAjouterLivre());
        tabbedPane.addTab("Emprunts", createPanelGestionEmprunts());
        add(tabbedPane);

        tabbedPane.setBackground(new Color(230, 230, 230));
        tabbedPane.setForeground(Color.BLACK);
        setVisible(true);
    }



    private JTabbedPane createPanelGestionEmprunts() {
        JTabbedPane empruntTabbedPane = new JTabbedPane();
        empruntTabbedPane.addTab("Emprunter un Livre", createPanelEmprunterLivre());
        empruntTabbedPane.addTab("Retourner un Livre", createPanelRetournerLivre());
        empruntTabbedPane.addTab("Emprunts Historique ",createPanelEmpruntsNonRetournés());
        return empruntTabbedPane;
    }
    
    private  JPanel createPanelEmpruntsNonRetournés(){
    	 JPanel panel = new JPanel();
         panel.setLayout(new BorderLayout());

         // Panel pour la recherche
         JPanel searchPanel = new JPanel();
         JLabel searchLabel = new JLabel("Rechercher:");
         JTextField searchField = new JTextField(20);

         searchPanel.add(searchLabel);
         searchPanel.add(searchField);

         String[] columnNames = {
             "ID Emprunt", "ID Membre", "Nom", "Prénom", "ISBN", "Titre du Livre",
             "Date d'Emprunt", "Date de Retour Prévue", "Date de Retour Effective", 
             "Tarif", "Pénalité de Retard"
         };

         DefaultTableModel model = new DefaultTableModel(columnNames, 0);
         tableHystoriqueEmp = new JTable(model);
         styleTable(tableHystoriqueEmp);


         JScrollPane scrollPane = new JScrollPane(tableHystoriqueEmp);

         searchField.getDocument().addDocumentListener(new DocumentListener() {
             @Override
             public void insertUpdate(DocumentEvent e) {
                 filterTable();
             }

             @Override
             public void removeUpdate(DocumentEvent e) {
                 filterTable();
             }

             @Override
             public void changedUpdate(DocumentEvent e) {
                 filterTable();
             }

             private void filterTable() {
                 String searchText = searchField.getText().toLowerCase();
                 DefaultTableModel filteredModel = new DefaultTableModel(columnNames, 0);

                 for (int i = 0; i < model.getRowCount(); i++) {
                     for (int j = 0; j < model.getColumnCount(); j++) {
                         String cellValue = model.getValueAt(i, j).toString().toLowerCase();
                         if (cellValue.contains(searchText)) {
                             filteredModel.addRow(new Object[]{
                                 model.getValueAt(i, 0), model.getValueAt(i, 1),
                                 model.getValueAt(i, 2), model.getValueAt(i, 3),
                                 model.getValueAt(i, 4), model.getValueAt(i, 5),
                                 model.getValueAt(i, 6), model.getValueAt(i, 7),
                                 model.getValueAt(i, 8), model.getValueAt(i, 9),
                             });
                             break; 
                         }
                     }
                 }
                 tableHystoriqueEmp.setModel(filteredModel);             
             }
         });

         panel.add(searchPanel, BorderLayout.NORTH);
         panel.add(scrollPane, BorderLayout.CENTER);

         fillTableWithDataHistoriqueEmprunte(model);

         return panel;
    }
    private  void fillTableWithDataHistoriqueEmprunte(DefaultTableModel model) {
	    BibliothecaireController controller = new BibliothecaireController();
	    List<Emprunt> emprunts = controller.getAllEmprunts();
	    
	    for (Emprunt emprunt : emprunts) {
	        if (!emprunt.getDateRetourEffective().equals("-")) {
	            Membre membre = controller.getMembreById(emprunt.getIdMembre());
	            Livre livre = controller.getLivreByIsbn(emprunt.getIsbn());
	            model.addRow(new Object[]{
	                emprunt.getIdEmprunt(),
	                membre.getId_membre(),
	                membre.getNom(),
	                membre.getPrenom(),
	                livre.getIsbn(),
	                livre.getTitre(),
	                emprunt.getDateEmprunt(),
	                emprunt.getDateRetourPrevue(),
	                emprunt.getDateRetourEffective(),
	                emprunt.getTarif(),
	                emprunt.getPenalite()        
                });
	
	        }
	    }
	
	   
	}
    
    
    private  JPanel createPanelRetournerLivre() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel("Rechercher:");
        JTextField searchField = new JTextField(20);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Table pour afficher les informations des emprunts
        String[] columnNames = {
            "ID Emprunt", "ID Membre", "Nom", "Prénom", "ISBN", "Titre du Livre",
            "Date d'Emprunt", "Date de Retour Prévue", "Date de Retour Effective", 
            "Tarif", "Pénalité de Retard", "Action"
        };

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        tableRetourEmp = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == model.getColumnCount() - 1; // Rendre la dernière colonne éditable
            }
        };
        styleTable(tableRetourEmp);

        // Ajouter un bouton "Retourner" dans la dernière colonne
        tableRetourEmp.getColumnModel().getColumn(11).setCellRenderer(new ButtonRetourEmpruntRender());
        tableRetourEmp.getColumnModel().getColumn(11).setCellEditor(new ButtonRetourEmpruntEditor(model, tableRetourEmp));


        JScrollPane scrollPane = new JScrollPane(tableRetourEmp);

        // Ajouter un DocumentListener pour la recherche dynamique
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String searchText = searchField.getText().toLowerCase();
                DefaultTableModel filteredModel = new DefaultTableModel(columnNames, 0);

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        String cellValue = model.getValueAt(i, j).toString().toLowerCase();
                        if (cellValue.contains(searchText)) {
                            filteredModel.addRow(new Object[]{
                                model.getValueAt(i, 0), model.getValueAt(i, 1),
                                model.getValueAt(i, 2), model.getValueAt(i, 3),
                                model.getValueAt(i, 4), model.getValueAt(i, 5),
                                model.getValueAt(i, 6), model.getValueAt(i, 7),
                                model.getValueAt(i, 8), model.getValueAt(i, 9),
                                model.getValueAt(i, 10), "Retourner"
                            });
                            break; 
                        }
                    }
                }

                // Mettre à jour le tableau avec le modèle filtré
                tableRetourEmp.setModel(filteredModel);
                tableRetourEmp.getColumnModel().getColumn(11).setCellRenderer(new ButtonRetourEmpruntRender());
                tableRetourEmp.getColumnModel().getColumn(11).setCellEditor(new ButtonRetourEmpruntEditor(model, tableRetourEmp));

            }
        });

        // Ajouter les composants au panel principal
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Remplir le tableau avec des données d'exemple
       fillTableWithData(model);

        return panel;
    }

	private  void fillTableWithData(DefaultTableModel model) {
	    BibliothecaireController controller = new BibliothecaireController();
	    List<Emprunt> emprunts = controller.getAllEmprunts();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    
	    for (Emprunt emprunt : emprunts) {
	        if (emprunt.getDateRetourEffective().equals("-")) {
	            Membre membre = controller.getMembreById(emprunt.getIdMembre());
	            Livre livre = controller.getLivreByIsbn(emprunt.getIsbn());
	            model.addRow(new Object[]{
	                emprunt.getIdEmprunt(),
	                membre.getId_membre(),
	                membre.getNom(),
	                membre.getPrenom(),
	                livre.getIsbn(),
	                livre.getTitre(),
	                emprunt.getDateEmprunt(),
	                emprunt.getDateRetourPrevue(),
	                emprunt.getDateRetourEffective(),
	                emprunt.getTarif(),
	                emprunt.getPenalite(),
	                "Retourner"
	            });
	
	            try {
	                Date dateRetourPrevue = sdf.parse(emprunt.getDateRetourPrevue());
	                Date currentDate = new Date();
	
	                if (dateRetourPrevue.before(currentDate)) {
	                    // Si l'emprunt est en retard, on ajoute une couleur de fond
	                    int rowIndex = model.getRowCount() - 1; // Index de la dernière ligne ajoutée
	                    tableRetourEmp.setValueAt(emprunt.getIdEmprunt(), rowIndex, 0); // Juste pour s'assurer que la ligne est ajoutée
	                    tableRetourEmp.setRowHeight(rowIndex, 30); // Optionnel : ajuster la hauteur de la ligne
	                    tableRetourEmp.setBackground(new Color(255, 102, 102)); // Couleur de fond rouge
	                }
	            } catch (ParseException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	
	    // Appliquer un renderer pour colorer les lignes en retard
	    tableRetourEmp.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
	        @Override
	        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,int col) {
	            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,11);
	            // Vérifier si la ligne est en retard
	            String dateRetourPrevue = (String) model.getValueAt(row, 7); // Supposons que la date de retour prévue est à la colonne 7
	            try {
	                Date dateRetourPrevueDate = sdf.parse(dateRetourPrevue);
	                if (dateRetourPrevueDate.before(new Date())) {
	                    c.setBackground(new Color(255, 102, 102)); // Couleur de fond rouge
	                } else {
	                    c.setBackground(Color.WHITE); // Couleur de fond par défaut
	                }
	            } catch (ParseException e) {
	                e.printStackTrace();
	            }
	            return c;
	        }
	    });
	}
	
    private  void refreshEmpruntTable(DefaultTableModel model) {
	    model.setRowCount(0); 
	    fillTableWithData(model); 
    }
    
    
    static class ButtonRetourEmpruntRender extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public ButtonRetourEmpruntRender() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Retourner" : value.toString());
            setForeground(isSelected ? Color.WHITE : Color.BLACK);
            setBackground(isSelected ? Color.BLUE : Color.LIGHT_GRAY);
            return this;
        }
    }

     class ButtonRetourEmpruntEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private DefaultTableModel model;
        private JTable table;

        public ButtonRetourEmpruntEditor(DefaultTableModel model, JTable table) {
            super(new JCheckBox());
            this.model = model;
            this.table = table;
            BibliothecaireController ctr=new BibliothecaireController();
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    int row = table.getSelectedRow();
                    if (row != -1) {
                    	int id_emp = Integer.parseInt(model.getValueAt(row, 0).toString());
                    	Emprunt emp=ctr.getEmpruntById(id_emp);
                    	Membre membre=ctr.getMembreById(emp.getIdMembre());
                    	Livre livre = ctr.getLivreByIsbn(emp.getIsbn());
                    	
                    	
                    	
                    	  JFrame detailFrame = new JFrame("Emprunter");
                          detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                          detailFrame.setSize(580,460);
                          detailFrame.setLocationRelativeTo(null);
                          
                          detailFrame.setLayout(new BorderLayout());

                          // Create a panel for member information
                          JPanel memberInfoPanel = new JPanel();
                          memberInfoPanel.setLayout(new GridBagLayout());
                          GridBagConstraints gbc = new GridBagConstraints();
                          gbc.fill = GridBagConstraints.HORIZONTAL;
                          gbc.insets = new Insets(5, 5, 5, 5); 

                          // Load and display the image
                          JLabel imageLabel = new JLabel();
                          imageLabel.setHorizontalAlignment(JLabel.CENTER);
                          imageLabel.setVerticalAlignment(JLabel.CENTER);
                          loadImage(membre.getPhoto(), imageLabel);

                          // Add the image label to the member info panel
                          addComponent(memberInfoPanel, imageLabel, gbc, 0, 0, 2, 1);

                          // Create components for member information
                          JTextField nomField = createStyledTextField();
                          nomField.setText(membre.getNom());
                          JTextField prenomField = createStyledTextField();
                          prenomField.setText(membre.getPrenom());

                          JTextField cniField = createStyledTextField();
                          cniField.setText(membre.getCni());

                          JTextField dateNaissanceFieldFenetre = createStyledTextField();
                          dateNaissanceFieldFenetre.setText(membre.getDate_n());
                          JTextField emailField = createStyledTextField();
                          emailField.setText(membre.getEmail());


                          JTextField telephoneField = createStyledTextField();
                          telephoneField.setText(membre.getN_tele());

                          JTextField adresseField = createStyledTextField();
                          adresseField.setText(membre.getAddresse());

                          JTextField dateInscriptionFieldFenetre = createStyledTextField();
                          dateInscriptionFieldFenetre.setText(membre.getDate_inscription());
                          JTextField StatusField = createStyledTextField();
                          StatusField.setText(membre.getStatus());
                          // Gender selection

                          
                          JTextField sexeField = createStyledTextField();
                          sexeField.setText(membre.getSexe());
                         
                          sexeField.setEditable(false);
                          nomField.setEditable(false);
                          prenomField.setEditable(false);
                          cniField.setEditable(false);
                          emailField.setEditable(false);
                          telephoneField.setEditable(false);
                          dateNaissanceFieldFenetre.setEditable(false);
                          dateInscriptionFieldFenetre.setEditable(false);
                          adresseField.setEditable(false);
                          StatusField.setEditable(false);

                          if(membre.getStatus().equals("Actif")) {
                          	StatusField.setForeground(Color.green);
                          }else {
                          	StatusField.setForeground(Color.red);


                          }
                          // Add components to the member info panel
                          addComponent(memberInfoPanel, new JLabel("Nom :"), gbc, 0, 1);
                          addComponent(memberInfoPanel, nomField, gbc, 1, 1);
                          addComponent(memberInfoPanel, new JLabel("Prénom :"), gbc, 0, 2);
                          addComponent(memberInfoPanel, prenomField, gbc, 1, 2);
                          addComponent(memberInfoPanel, new JLabel("CNI :"), gbc, 0, 3);
                          addComponent(memberInfoPanel, cniField, gbc, 1, 3);
                          addComponent(memberInfoPanel, new JLabel("Sexe :"), gbc, 0, 4);
                          addComponent(memberInfoPanel, sexeField, gbc, 1, 4);
                          addComponent(memberInfoPanel, new JLabel("Date de Naissance :"), gbc, 0, 6);
                          addComponent(memberInfoPanel, dateNaissanceFieldFenetre, gbc, 1, 6);
                          addComponent(memberInfoPanel, new JLabel("Email :"), gbc, 0, 7);
                          addComponent(memberInfoPanel, emailField, gbc, 1, 7);
                          addComponent(memberInfoPanel, new JLabel("Téléphone :"), gbc, 0, 9);
                          addComponent(memberInfoPanel, telephoneField, gbc, 1, 9);
                          addComponent(memberInfoPanel, new JLabel("Adresse :"), gbc, 0, 10);
                          addComponent(memberInfoPanel, adresseField, gbc, 1, 10);
                          addComponent(memberInfoPanel, new JLabel("Date d'inscription :"), gbc, 0, 11);
                          addComponent(memberInfoPanel, dateInscriptionFieldFenetre, gbc, 1, 11);
                          addComponent(memberInfoPanel, new JLabel("État :"), gbc, 0, 12);
                          addComponent(memberInfoPanel, StatusField, gbc, 1, 12);
                          
                          
                          JPanel retourEmpruntPanel = new JPanel();
                          retourEmpruntPanel.setLayout(new GridBagLayout());
                          GridBagConstraints gbc2 = new GridBagConstraints();
                          gbc2.insets = new Insets(10, 10, 10, 10); // Espacement autour des composants
                          gbc2.fill = GridBagConstraints.HORIZONTAL;

                          // Informations sur le livre
                          gbc2.gridx = 0; gbc2.gridy = 0; gbc2.anchor = GridBagConstraints.WEST;
                          retourEmpruntPanel.add(new JLabel("ISBN:"), gbc2);
                          gbc2.gridx = 1; gbc2.gridy = 0;
                          retourEmpruntPanel.add(new JLabel(livre.getIsbn()), gbc2);

                          gbc2.gridx = 0; gbc2.gridy = 1;
                          retourEmpruntPanel.add(new JLabel("Titre:"), gbc2);
                          gbc2.gridx = 1; gbc2.gridy = 1;
                          retourEmpruntPanel.add(new JLabel(livre.getTitre()), gbc2);

                          gbc2.gridx = 0; gbc2.gridy = 2;
                          retourEmpruntPanel.add(new JLabel("Nombre d'exemplaires:"), gbc2);
                          gbc2.gridx = 1; gbc2.gridy = 2;
                          retourEmpruntPanel.add(new JLabel(String.valueOf(livre.getNombreExemplaires())), gbc2);

                          gbc2.gridx = 0; gbc2.gridy = 3;
                          retourEmpruntPanel.add(new JLabel("Date d'emprunt:"), gbc2);
                          gbc2.gridx = 1; gbc2.gridy = 3;
                          retourEmpruntPanel.add(new JLabel(emp.getDateEmprunt()), gbc2);

                          gbc2.gridx = 0; gbc2.gridy = 4;
                          retourEmpruntPanel.add(new JLabel("Date de retour prévue:"), gbc2);
                          gbc2.gridx = 1; gbc2.gridy = 4;
                          retourEmpruntPanel.add(new JLabel(emp.getDateRetourPrevue()), gbc2);

                          double  penalite = 0.0;
                          SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                          
                          try {
                              Date dateRetourPrevue = sdf.parse(emp.getDateRetourPrevue());
                              Date currentDate = new Date();
                              long diffInMillies = currentDate.getTime() - dateRetourPrevue.getTime();

                              if (dateRetourPrevue.before(currentDate)) {
                                  long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
                                  penalite = diffInDays * livre.getPenaliteParJour();
                              }
                          } catch (ParseException e1) {
                              e1.printStackTrace();
                          }

                          gbc2.gridx = 0; gbc2.gridy = 5;
                          if(penalite!=0) {
                              retourEmpruntPanel.add(new JLabel("Pénalité de retard:"), gbc2);
                              JTextField penaliteField = new JTextField(penalite+" DH");
                              penaliteField.setEditable(false); // Champ non modifiable
                              gbc2.gridx = 1; gbc2.gridy = 5;
                              penaliteField.setBorder(new LineBorder(Color.RED, 2));
                              retourEmpruntPanel.add(penaliteField, gbc2);

                          }
                          final double penaliteV=penalite;
                
                          JButton enregistrerRetourButton = new JButton("Enregistrer le retour");
                          enregistrerRetourButton.addActionListener(new ActionListener() {
                              @Override
                              public void actionPerformed(ActionEvent e) {
                            	  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								  emp.setDateRetourEffective(sdf.format(new Date()));
								  emp.setPenalite(penaliteV);
								  if(controller.marquerRetour(emp)) {
									    JOptionPane.showMessageDialog(null, "Retour enregistré avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
								  }else {
									    JOptionPane.showMessageDialog(null, "Échec de l'enregistrement du retour. Veuillez réessayer.", "Erreur", JOptionPane.ERROR_MESSAGE);
								  }
				            	  rafraichirListeLivres();
				            	    fillTableWithDataHistoriqueEmprunte((DefaultTableModel) tableHystoriqueEmp.getModel());
				            	  refreshEmpruntTable((DefaultTableModel) tableRetourEmp.getModel());
								  detailFrame.dispose();
								  
                              }
                          });
                          gbc2.gridx = 0; gbc2.gridy = 6; gbc2.gridwidth = 2; gbc2.anchor = GridBagConstraints.CENTER;
                          retourEmpruntPanel.add(enregistrerRetourButton, gbc2);

                          // Ajout d'une bordure autour du panel
                          retourEmpruntPanel.setBorder(BorderFactory.createTitledBorder("Retour d'emprunt"));

                          // Ajout du panel au frame
                          detailFrame.add(retourEmpruntPanel, BorderLayout.CENTER);
                          detailFrame.add(memberInfoPanel, BorderLayout.WEST);
                          detailFrame.setVisible(true);
                      }
                 
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,int col) {
            label = (value == null) ? "Retourner" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Action effectuée
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    
    
    //---------------

    private JPanel createPanelEmprunterLivre() {
        JPanel panel = new JPanel(new BorderLayout());

        // Récupérer la liste des membres
        List<Membre> membres = controller.getAllMembres();

        // Créer le modèle de tableau
        String[] columnNames = {"ID", "Nom", "Prénom", "CNI", "Sexe", "Email", "Téléphone", "Adresse", "Date d'inscription", "Statut", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Remplir le modèle avec les données des membres
        for (Membre membre : membres) {
            Object[] rowData = {
                membre.getId_membre(),
                membre.getNom(),
                membre.getPrenom(),
                membre.getCni(),
                membre.getSexe(),
                membre.getEmail(),
                membre.getN_tele(),
                membre.getAddresse(),
                membre.getDate_inscription(),
                membre.getStatus(),
                "Emprunter" // Ajouter le bouton "Emprunter"
            };
            model.addRow(rowData);
            
        }

        // Créer le tableau
        membreTable = new JTable(model);
        styleTable(membreTable);
        membreTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(membreTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        membreTable.getColumnModel().getColumn(10).setCellRenderer(new ButtonMembreRenderer());
        membreTable.getColumnModel().getColumn(10).setCellEditor(new ButtonMembreEditor(new JCheckBox()));

        
        return panel;
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
    private void afficherFenetreDetails(Membre membre, List<Livre> livres) {
        JFrame detailFrame = new JFrame("Profile");
        this.setEnabled(false);
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setSize(1000, 700);
        detailFrame.setLocationRelativeTo(null);
        
        // Use BorderLayout for the main frame
        detailFrame.setLayout(new BorderLayout());

        // Create a panel for member information
        JPanel memberInfoPanel = new JPanel();
        memberInfoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components

        // Load and display the image
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        loadImage(membre.getPhoto(), imageLabel); // Ensure this method corcontrollertly sets the image

        // Add the image label to the member info panel
        addComponent(memberInfoPanel, imageLabel, gbc, 0, 0, 2, 1);

        // Create components for member information
        JTextField nomField = createStyledTextField();
        nomField.setText(membre.getNom());
        JTextField prenomField = createStyledTextField();
        prenomField.setText(membre.getPrenom());

        JTextField cniField = createStyledTextField();
        cniField.setText(membre.getCni());

        JTextField dateNaissanceFieldFenetre = createStyledTextField();
        dateNaissanceFieldFenetre.setText(membre.getDate_n());
        JTextField emailField = createStyledTextField();
        emailField.setText(membre.getEmail());


        JTextField telephoneField = createStyledTextField();
        telephoneField.setText(membre.getN_tele());

        JTextField adresseField = createStyledTextField();
        adresseField.setText(membre.getAddresse());

        JTextField dateInscriptionFieldFenetre = createStyledTextField();
        dateInscriptionFieldFenetre.setText(membre.getDate_inscription());
        JTextField StatusField = createStyledTextField();
        StatusField.setText(membre.getStatus());
        // Gender selection

        
        JTextField sexeField = createStyledTextField();
        sexeField.setText(membre.getSexe());
       
        sexeField.setEditable(false);
        nomField.setEditable(false);
        prenomField.setEditable(false);
        cniField.setEditable(false);
        emailField.setEditable(false);
        telephoneField.setEditable(false);
        dateNaissanceFieldFenetre.setEditable(false);
        dateInscriptionFieldFenetre.setEditable(false);
        adresseField.setEditable(false);
        StatusField.setEditable(false);

        if(membre.getStatus().equals("Actif")) {
        	StatusField.setForeground(Color.green);
        }else {
        	StatusField.setForeground(Color.red);


        }
        // Add components to the member info panel
        addComponent(memberInfoPanel, new JLabel("Nom :"), gbc, 0, 1);
        addComponent(memberInfoPanel, nomField, gbc, 1, 1);
        addComponent(memberInfoPanel, new JLabel("Prénom :"), gbc, 0, 2);
        addComponent(memberInfoPanel, prenomField, gbc, 1, 2);
        addComponent(memberInfoPanel, new JLabel("CNI :"), gbc, 0, 3);
        addComponent(memberInfoPanel, cniField, gbc, 1, 3);
        addComponent(memberInfoPanel, new JLabel("Sexe :"), gbc, 0, 4);
        addComponent(memberInfoPanel, sexeField, gbc, 1, 4);
        addComponent(memberInfoPanel, new JLabel("Date de Naissance :"), gbc, 0, 6);
        addComponent(memberInfoPanel, dateNaissanceFieldFenetre, gbc, 1, 6);
        addComponent(memberInfoPanel, new JLabel("Email :"), gbc, 0, 7);
        addComponent(memberInfoPanel, emailField, gbc, 1, 7);
        addComponent(memberInfoPanel, new JLabel("Téléphone :"), gbc, 0, 9);
        addComponent(memberInfoPanel, telephoneField, gbc, 1, 9);
        addComponent(memberInfoPanel, new JLabel("Adresse :"), gbc, 0, 10);
        addComponent(memberInfoPanel, adresseField, gbc, 1, 10);
        addComponent(memberInfoPanel, new JLabel("Date d'inscription :"), gbc, 0, 11);
        addComponent(memberInfoPanel, dateInscriptionFieldFenetre, gbc, 1, 11);
        addComponent(memberInfoPanel, new JLabel("État :"), gbc, 0, 12);
        addComponent(memberInfoPanel, StatusField, gbc, 1, 12);
 
        if (!membre.getStatus().equals("Inactif")) {            
            // Create a panel for the book table
            JPanel bookTablePanel = new JPanel();
            bookTablePanel.setLayout(new BorderLayout());
            JPanel searchPanel = new JPanel();

            // Create a search field for filtering the book table
            JTextField searchField = createStyledTextField();
            JLabel searchlbl=new JLabel("Chercher");
            searchField.setSize(60,20);
            searchField.setToolTipText("Search books...");
            
            searchPanel.add(searchlbl);
            searchPanel.add(searchField);
            
            bookTablePanel.add(searchPanel, BorderLayout.NORTH);

            // Create table for books
            String[] bookColumnNames = {"Titre", "ISBN", "Nombre d'exemplaires", "Tarif Emprunt/Jour", "Pénalité/Jour", "Emprunter"};
            DefaultTableModel bookModel = new DefaultTableModel(bookColumnNames, 0);

            // Fill the model with book data
            for (Livre livre : livres) {
                Object[] bookData = {
                    livre.getTitre(),
                    livre.getIsbn(),
                    livre.getNombreExemplaires(),
                    livre.getTarifEmpruntParJour(),
                    livre.getPenaliteParJour(),
                    "Emprunter" // Button text
                };
                bookModel.addRow(bookData);
            }

            // Create the JTable and JScrollPane
            JTable bookTable = new JTable(bookModel);
            styleTable(bookTable);
            bookTable.setFillsViewportHeight(true);
            JScrollPane bookScrollPane = new JScrollPane(bookTable);
            bookTablePanel.add(bookScrollPane, BorderLayout.CENTER);

       

            bookTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonEmpruntRenderer());
            bookTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEmpruntEditor(bookTable, livres, membre));

            // Add a row sorter to the table
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(bookModel);
            bookTable.setRowSorter(sorter);

            // Add a listener to the search field to filter the table
            searchField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    String searchText = searchField.getText();
                    if (searchText.trim().length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                    }
                }
            });

            // Add panels to the main frame
            detailFrame.add(bookTablePanel, BorderLayout.CENTER);
            detailFrame.add(memberInfoPanel, BorderLayout.WEST);


        } else {
            JPanel errorContainer = new JPanel();
            errorContainer.setLayout(new BoxLayout(errorContainer, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical stacking

            JLabel inactifTitle = new JLabel("Le membre ne peut pas emprunter un exemplaire");
            inactifTitle.setFont(new Font("Arial", Font.BOLD, 16)); // Set font size and style
            inactifTitle.setForeground(Color.RED); // Set text color to red for error theme

            // Create the description label with a smaller font
            JLabel inactifDesc = new JLabel("Membre " + membre.getNom() + " est inactif !!");
            inactifDesc.setFont(new Font("Arial", Font.PLAIN, 12)); // Set font size and style
            inactifDesc.setForeground(Color.RED); // Set text color to red for error theme

            // Add the labels to the error container
            errorContainer.add(inactifTitle,BorderLayout.CENTER);
            errorContainer.add(inactifDesc,BorderLayout.CENTER);
            
            // Add the error container to the member info panel
            detailFrame.add(errorContainer,BorderLayout.NORTH);
            detailFrame.add(memberInfoPanel, BorderLayout.CENTER);
            detailFrame.setSize(400,detailFrame.getHeight());
        }

        // Add the member info panel to the detail frame

        // Window listener to re-enable the main frame when the detail frame is closed
        detailFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                setEnabled(true);
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                setEnabled(true);
            }
        });
        detailFrame.setVisible(true);
    }
    
    
    
    //-----------------------------
    private JPanel createPanelAjouterLivre() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(204, 204, 204)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        leftPanel.setPreferredSize(new Dimension(450, mainPanel.getHeight())); // Set width for sidebar

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        addFormField(leftPanel, "Titre:", titreField = createStyledTextField(), gbc, 0);
        addFormField(leftPanel, "Auteur:", auteurField = createStyledTextField(), gbc, 1);
        addFormField(leftPanel, "Genre:", genreField = createStyledTextField(), gbc, 2);
        addFormField(leftPanel, "ISBN:", isbnField = createStyledTextField(), gbc, 3);
        addFormField(leftPanel, "Année de Publication:", anneeField = createStyledTextField(), gbc, 4);
        addFormField(leftPanel, "Nombre d'Exemplaires:", exemplairesField = createStyledTextField(), gbc, 5);
        addFormField(leftPanel, "Tarif Emprunt (par jour):", tarifField = createStyledTextField(), gbc, 6);
        addFormField(leftPanel, "Pénalité (par jour):", penaliteField = createStyledTextField(), gbc, 7);
        addFormField(leftPanel, "Image(chemin):", imagePathField = createStyledTextField(), gbc, 8);
        imagePathField.setEditable(false);
        
        choisirImageButton = createStyledButton("Choisir Image", new Color(255, 193, 7));
        gbc.gridy = 9;
        leftPanel.add(choisirImageButton, gbc);
        
        choisirImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                imagePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);
        
        ajouterButton= createStyledButton("Ajouter Livre", new Color(76, 175, 80));
        ajouterButton.setEnabled(false); 
        ajouterButton.setBackground(Color.GRAY);
        ajouterButton.setForeground(Color.white);
        buttonsPanel.add(ajouterButton);
        
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        leftPanel.add(buttonsPanel, gbc);

        // Add DocumentListeners to validate fields
        DocumentListener documentListener = new DocumentListener() {
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

        titreField.getDocument().addDocumentListener(documentListener);
        auteurField.getDocument().addDocumentListener(documentListener);
        genreField.getDocument().addDocumentListener(documentListener);
        isbnField.getDocument().addDocumentListener(documentListener);
        anneeField.getDocument().addDocumentListener(documentListener);
        exemplairesField.getDocument().addDocumentListener(documentListener);
        tarifField.getDocument().addDocumentListener(documentListener);
        penaliteField.getDocument().addDocumentListener(documentListener);
        imagePathField.getDocument().addDocumentListener(documentListener);
    
       
        // Right panel for the table
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204)));

        String[] columnNames = {"Titre", "Auteur", "Genre", "ISBN", "Année", "Exemplaires", "Tarif/Jour", "Pénalité/Jour", "Détails"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableLivres = new JTable(tableModel);
        styleTable(tableLivres);

        try {
            List<Livre> livres = controller.listerLivres();
            tableModel.setRowCount(0);
            for (Livre livre : livres) {
                tableModel.addRow(new Object[]{
                    livre.getTitre(),
                    livre.getAuteur(),
                    livre.getGenre(),
                    livre.getIsbn(),
                    livre.getAnneePublication(),
                    livre.getNombreExemplaires(),
                    livre.getTarifEmpruntParJour(),
                    livre.getPenaliteParJour(),
                    "➢" // Set the button text here
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de chargement : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane scrollPane = new JScrollPane(tableLivres);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        JTextField searchField = createStyledTextField();
        searchPanel.add(new JLabel("ISBN à rechercher:"));
        
        searchPanel.add(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable(searchField.getText());
            }
        });
        searchPanel.setFont(new Font("Lato", Font.CENTER_BASELINE, 13));

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        ajouterButton.addActionListener(e -> handleAjout());
        
        // Set the renderer and editor for the details button column
        tableLivres.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        tableLivres.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        return mainPanel;
    }

    private void updateButtonState() {
        boolean isValid = true;

        // Validate each field and update border color
        if (!validateTitre(titreField.getText())) {
            titreField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            titreField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validateAuteur(auteurField.getText())) {
            auteurField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            auteurField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validateGenre(genreField.getText())) {
            genreField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            genreField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validateISBN(isbnField.getText())) {
            isbnField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            isbnField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validateAnnee(anneeField.getText())) {
            anneeField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            anneeField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validateExemplaires(exemplairesField.getText())) {
            exemplairesField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            exemplairesField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validateTarif(tarifField.getText())) {
            tarifField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        } else {
            tarifField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        if (!validatePenalite(penaliteField.getText())) {
            penaliteField.setBorder(new LineBorder(Color.RED));
            isValid = false;
        }
        else {
        	penaliteField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }
        if (!validateImage(imagePathField.getText())){
        	imagePathField.setBorder(new LineBorder(Color.RED));
             isValid = false;
        }else {
        	imagePathField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Reset to default
        }

        // Enable or disable the button based on validity
        ajouterButton.setEnabled(isValid);
        ajouterButton.setBackground(isValid ? new Color(92, 179, 56) : Color.LIGHT_GRAY);
    }

    private boolean validateTitre(String titre) {
        return titre.length() <= 30 && !titre.isEmpty(); // Accept any title up to 30 characters
    }

    private boolean validateAuteur(String auteur) {
        return Pattern.matches("^[a-zA-Z]{3,20}$", auteur.trim()); // Only letters, 3 to 20 characters
    }

    private boolean validateGenre(String genre) {
        return Pattern.matches("^[a-zA-Z]{3,20}$", genre.trim()); // Only letters, 3 to 20 characters
    }

    private boolean validateISBN(String isbn) {
        return Pattern.matches("^[0-9]{8,30}$", isbn.trim()); // Only digits, 8 to 30 characters
    }

    private boolean validateAnnee(String annee) {
        return Pattern.matches("^[1-9]\\d{3}$", annee.trim()); // Format XXXX, e.g., 2025
    }

    private boolean validateExemplaires(String exemplaires) {
        return Pattern.matches("^[1-9][0-9]{0,3}$", exemplaires.trim()); // 1 to 4 digits, starting from 1
    }

    private boolean validateTarif(String tarif) {
        return Pattern.matches("^\\d+(\\.\\d{1,2})?$", tarif.trim()); // Format like 5.55
    }

    private boolean validatePenalite(String penalite) {
        return Pattern.matches("^\\d+(\\.\\d{1,2})?$", penalite.trim()); // Format like 5.5
    }
    
    private boolean validateImage(String image) {
        return !image.isEmpty();
    }
    
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    private void filterTable(String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        tableLivres.setRowSorter(sorter);
        if (query.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Use (?i) to make the regex case-insensitive
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void afficherLivreDetail(Livre livre) {
        setEnabled(false);
        JFrame detailFrame = new JFrame("Détails du Livre");
        detailFrame.setLayout(new BorderLayout());
        detailFrame.setResizable(false);

        // Image Panel
        JPanel imagePanel = new JPanel();
        ImageIcon originalIcon = new ImageIcon(livre.getImagePath());
        Image scaledImage = originalIcon.getImage().getScaledInstance(-1, 200, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel);
        detailFrame.add(imagePanel, BorderLayout.WEST); // Add image panel to the left

        JPanel informationPanel = new JPanel(new GridLayout(0, 2));

        // Create and add text fields
        JTextField titreField = new JTextField(livre.getTitre());
        informationPanel.add(new JLabel("Titre:"));
        informationPanel.add(titreField);

        JTextField auteurField = new JTextField(livre.getAuteur());
        informationPanel.add(new JLabel("Auteur:"));
        informationPanel.add(auteurField);

        JTextField genreField = new JTextField(livre.getGenre());
        informationPanel.add(new JLabel("Genre:"));
        informationPanel.add(genreField);

        JTextField isbnField = new JTextField(livre.getIsbn());
        informationPanel.add(new JLabel("ISBN:"));
        informationPanel.add(isbnField);

        JTextField anneeField = new JTextField(String.valueOf(livre.getAnneePublication()));
        informationPanel.add(new JLabel("Année:"));
        informationPanel.add(anneeField);

        JTextField exemplairesField = new JTextField(String.valueOf(livre.getNombreExemplaires()));
        informationPanel.add(new JLabel("Exemplaires:"));
        informationPanel.add(exemplairesField);

        JTextField tarifField = new JTextField(String.valueOf(livre.getTarifEmpruntParJour()));
        informationPanel.add(new JLabel("Tarif/Jour:"));
        informationPanel.add(tarifField);

        JTextField penaliteField = new JTextField(String.valueOf(livre.getPenaliteParJour()));
        informationPanel.add(new JLabel("Pénalité/Jour:"));
        informationPanel.add(penaliteField);

        JPanel buttonPanel = new JPanel();
        JButton modifierButton = createStyledButton("Enregistrer ", new Color(76, 175, 80));
        JButton supprimerButton = createStyledButton("Supprimer", new Color(244, 67, 54));
        JButton annulerButton = createStyledButton("Annuler", Color.RED);

        buttonPanel.add(modifierButton);
        buttonPanel.add(supprimerButton);
        buttonPanel.add(annulerButton);

        JPanel container = new JPanel(new BorderLayout());
        container.add(informationPanel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        detailFrame.add(container, BorderLayout.CENTER); // Add the container to the center

        // Action listeners for buttons
        modifierButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(detailFrame, "Voulez-vous vraiment modifier ce livre?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                // Set the attributes of the livre object using the text field values
                livre.setTitre(titreField.getText());
                livre.setAuteur(auteurField.getText());
                livre.setGenre(genreField.getText());
                livre.setIsbn(isbnField.getText());
                livre.setAnneePublication(Integer.parseInt(anneeField.getText()));
                livre.setNombreExemplaires(Integer.parseInt(exemplairesField.getText()));
                livre.setTarifEmpruntParJour(Double.parseDouble(tarifField.getText()));
                livre.setPenaliteParJour(Double.parseDouble(penaliteField.getText()));

                // Call the controller to modify the book
                boolean success = controller.modifierLivre(livre);
                String message = success ? "Le livre a été modifié avec succès." : "Échec de la modification du livre.";
                JOptionPane.showMessageDialog(detailFrame, message);
                if (success) {
                    rafraichirListeLivres();
                }
            }
        });

        supprimerButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(detailFrame, "Voulez-vous vraiment supprimer ce livre?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    boolean success = controller.supprimerLivre(livre.getIsbn());
                    String message = success ? "Le livre a été supprimé avec succès." : "Impossible de supprimer ce livre. Des exemplaires sont actuellement empruntés.";
                    JOptionPane.showMessageDialog(detailFrame, message);
                    if (success) {
                        rafraichirListeLivres();
                        setEnabled(true);
            	        detailFrame.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(detailFrame, "Erreur: " + ex.getMessage(), "e", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        annulerButton.addActionListener(e ->{   	
	    	setEnabled(true);
	        detailFrame.dispose();

        });
        detailFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                setEnabled(true);
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                setEnabled(true);
            }
        });
        detailFrame.pack();
        detailFrame.setLocationRelativeTo(null);
        detailFrame.setVisible(true);
    }
    private void handleAjout() {
        try {
            Livre livre = new Livre(
                titreField.getText(),
                auteurField.getText(),
                genreField.getText(),
                isbnField.getText(),
                Integer.parseInt(anneeField.getText()),
                Integer.parseInt(exemplairesField.getText()),
                Double.parseDouble(tarifField.getText()),
                Double.parseDouble(penaliteField.getText()),
                imagePathField.getText()
            );
            controller.ajouterLivre(livre);
            JOptionPane.showMessageDialog(this, "Livre ajouté avec succès !");
            clearFields();
            rafraichirListeLivres();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rafraichirListeLivres() {
        try {
            List<Livre> livres = controller.listerLivres();
            tableModel.setRowCount(0);
            for (Livre livre : livres) {
                tableModel.addRow(new Object[]{
                    livre.getTitre(),
                    livre.getAuteur(),
                    livre.getGenre(),
                    livre.getIsbn(),
                    livre.getAnneePublication(),
                    livre.getNombreExemplaires(),
                    livre.getTarifEmpruntParJour(),
                    livre.getPenaliteParJour(),
                    "➢"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de chargement : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
        clearFields();
        tableLivres.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        tableLivres.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));
    }
   
    private void clearFields() {
        titreField.setText("");
        auteurField.setText("");
        genreField.setText("");
        isbnField.setText("");
        anneeField.setText("");
        exemplairesField.setText("");
        tarifField.setText("");
        penaliteField.setText("");
        imagePathField.setText("");
    }
    //--Emprunte
    
    //Methode Utilise style ou creation 

    public static void styleTable(JTable table) {
        // Set table font and row height
        table.setFont(new Font("Lato", Font.CENTER_BASELINE, 13));
        table.setRowHeight(30);

        // Set header font and background color
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Lato", Font.PLAIN, 16));
       // header.setBackground(new Color(130, 77, 116)); // Set header background color
        header.setForeground(new Color(130, 77, 116)); // Set header text color

        // Set table grid colors
        table.setGridColor(new Color(83, 100, 147));
        table.setShowGrid(true);

        // Alternate row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value ) {
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
    private void addFormField(JPanel panel, String label, JTextField field, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        
        JLabel jlabel = new JLabel(label);
        jlabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(jlabel, gbc);
        
        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(200, 25));
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(120, 30));
        return button;
    }
    
    
    
    private void addComponent(Container container, Component component, GridBagConstraints gbc, int gridx, int gridy) {
        addComponent(container, component, gbc, gridx, gridy, 1, 1); // Default width and height of 1
    }

    private void addComponent(Container container, Component component, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        container.add(component, gbc);
    }
    private void afficherFenetreEmprunt(Membre membre, Livre livre) {
        // Check the number of copies
        if (livre.getNombreExemplaires() < 2) {
            JOptionPane.showMessageDialog(null, "Le livre est hors prêt (un seul exemplaire restant)", "Erreur", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        JFrame empruntFrame = new JFrame("Emprunter un Livre");
        empruntFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        empruntFrame.setSize(500, 400);
        empruntFrame.setLocationRelativeTo(null);
        empruntFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        ImageIcon icon = new ImageIcon("src/vue/icons/profile.jpg"); 
        empruntFrame.setIconImage(icon.getImage());
        // Labels and Fields
        JTextField titreField = createStyledTextField();
        titreField.setText(livre.getTitre());
        titreField.setEnabled(false);

        JTextField isbnField = createStyledTextField();
        isbnField.setText(livre.getIsbn());
        isbnField.setEnabled(false);

        JTextField nomField = createStyledTextField();
        nomField.setText(membre.getNom());
        nomField.setEnabled(false);

        JTextField prenomField = createStyledTextField();
        prenomField.setText(membre.getPrenom());
        prenomField.setEnabled(false);

        JTextField dateRetourPrevueField = createStyledTextField();
        JTextField tarifField = createStyledTextField();
        tarifField.setEnabled(false);

        JButton emprunterButton = new JButton("Emprunter");
        emprunterButton.setEnabled(false); // Initially disabled

        // Add DocumentListener to dateRetourPrevueField
        dateRetourPrevueField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTarif();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTarif();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTarif();
            }

            private void updateTarif() {
            	DecimalFormat df = new DecimalFormat("#.00");
                try {
                    // Get the input date
                    String dateText = dateRetourPrevueField.getText().trim();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date retourPrevue = dateFormat.parse(dateText);

                    // Calculate the difference in days
                    long diffInMillis = retourPrevue.getTime() - System.currentTimeMillis();
                    long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                    // Update the tarif field if days are valid
                    if (days > 0) {
                        double tarifTotal = livre.getTarifEmpruntParJour() * days;
                        tarifField.setText(""+df.format(tarifTotal));
                        emprunterButton.setEnabled(true); // Enable the button if the date is valid
                    } else {
                        tarifField.setText("0.00");
                        emprunterButton.setEnabled(false); // Disable the button if the date is invalid
                    }
                } catch (Exception ex) {
                    // Reset the tarif field if the date is invalid
                    tarifField.setText("0.00");
                    emprunterButton.setEnabled(false); // Disable the button on error
                    
                }
            }
        });

        // Action listener for the "Emprunter" button
        emprunterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	Emprunt emp=new Emprunt(
            			0,
            		    livre.getIsbn(), 
            		    membre.getId_membre(),
            		    ""+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            		    dateRetourPrevueField.getText(),
            		    "-", 
            		    Double.parseDouble(tarifField.getText().replace(',','.')),
            		    0.0
            		);
           
            	if (controller.emprunterLivre(emp)) {
            	    createReceipt(membre, livre, dateRetourPrevueField.getText(), tarifField.getText());
            	    refreshEmpruntTable((DefaultTableModel) tableRetourEmp.getModel());
            	    String membreFullName=controller.getMembreById(emp.getIdMembre()).getNom()+" "+controller.getMembreById(emp.getIdMembre()).getPrenom();
            	    String LivreTitre = controller.getLivreByIsbn(emp.getIsbn()).getTitre()+"";
            	    String tarif = emp.getTarif()+"";
            	    String penalite = controller.getLivreByIsbn(emp.getIsbn()).getPenaliteParJour()+"";

            	    String date_retourPrevue = emp.getDateRetourPrevue();

            	    String succesMsg="L'exemplaire du livre ("+LivreTitre+")  a été emprunté avec succès \n par le membre : "+membreFullName+"\n à retourner avant le : "+date_retourPrevue+"\n Tarif à payer : "+tarif+"DH  \n"
            	    		+ " En cas de retard, une pénalité de "+penalite+"DH par jour sera appliquée ⚠︎";
            	    JOptionPane.showMessageDialog(null,succesMsg , "Succès", JOptionPane.INFORMATION_MESSAGE);
            	    rafraichirListeLivres();
            	} else {
            	    JOptionPane.showMessageDialog(null, "L'emprunt n'a pas pu être effectué. ", "Erreur", JOptionPane.ERROR_MESSAGE);
            	}
            	empruntFrame.dispose();
            }
        });

        // Add components to the frame
        addComponent(empruntFrame, new JLabel("Titre du Livre :"), gbc, 0, 0);
        addComponent(empruntFrame, titreField, gbc, 1, 0);

        addComponent(empruntFrame, new JLabel("ISBN :"), gbc, 0, 1);
        addComponent(empruntFrame, isbnField, gbc, 1, 1);

        addComponent(empruntFrame, new JLabel("Nom du Membre :"), gbc, 0, 2);
        addComponent(empruntFrame, nomField, gbc, 1, 2);

        addComponent(empruntFrame, new JLabel("Prénom du Membre :"), gbc, 0, 3);
        addComponent(empruntFrame, prenomField, gbc, 1, 3);

        addComponent(empruntFrame, new JLabel("Date de Retour Prévue (dd/MM/yyyy) :"), gbc, 0, 4);
        addComponent(empruntFrame, dateRetourPrevueField, gbc, 1, 4);

        addComponent(empruntFrame, new JLabel("Tarif Total (DH) :"), gbc, 0, 5);
        addComponent(empruntFrame, tarifField, gbc, 1, 5);

        addComponent(empruntFrame, emprunterButton, gbc, 0, 6); // Add the button to the frame

        // Display the frame
        empruntFrame.setVisible(true);
    }

    private void createReceipt(Membre membre, Livre livre, String dateRetourPrevue, String tarifTotal) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Set background color
            g2d.setColor(new Color(245, 245, 245));
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
            g2d.drawString("Reçu d'Emprunt", 140, 70);

            // Draw a horizontal divider
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRect(20, 90, (int) pageFormat.getImageableWidth() - 40, 2);

            // Draw member details
            int detailX = 20;
            int detailY = 110;

            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.setColor(Color.BLACK);

            String[] labels = {
                "Nom : " + membre.getNom(),
                "Prénom : " + membre.getPrenom(),
                "CNI : " + membre.getCni(),
                "Titre du Livre : " + livre.getTitre(),
                "ISBN : " + livre.getIsbn(),
                "Date d'Emprunt : " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                "Date de Retour Prévue : " + dateRetourPrevue,
                "Tarif Total : " + tarifTotal + " DH"
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
            g2d.drawString("Merci pour votre confiance en EMSI", 30, footerY + 25);

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
    }

 

    //class du render 
    public class ButtonRenderer extends JButton implements TableCellRenderer {
        
		private static final long serialVersionUID = 1L;

		public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    public class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped(); // Stop editing
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Here you can call the method to show the book details
                // Assuming you have a method to get the book by row index
                Livre livre = controller.getLivreByIsbn((String) tableLivres.getValueAt(tableLivres.getSelectedRow(), 3)); // Assuming ISBN is in column 3
                afficherLivreDetail(livre);
            }
            isPushed = false;
            return label;
        }
    }
    
    public class ButtonEditorEmprunt extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private BibliothecaireVue bibliothecaireVue; // Référence à la vue pour afficher les détails du livre
        private JTable table; // Référence à la table pour obtenir des informations sur la ligne sélectionnée

        public ButtonEditorEmprunt(JTextField jTextField, BibliothecaireVue bibliothecaireVue, JTable table) {
            super(jTextField);
            this.bibliothecaireVue = bibliothecaireVue;
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped(); // Stop editing
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Ici, vous pouvez appeler la méthode pour afficher les détails du livre
                // Supposons que l'ISBN est dans la colonne 3
                String isbn = (String) table.getValueAt(table.getSelectedRow(), 3); // Ajustez l'index de colonne si nécessaire
                Livre livre = controller.getLivreByIsbn(isbn); // Récupérer le livre par ISBN
                bibliothecaireVue.afficherLivreDetail(livre); // Afficher les détails du livre
            }
            isPushed = false;
            return label;
        }
        }
    

    public class ButtonMembreEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public ButtonMembreEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped(); // Stop editing
                }
            });
        }

        
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table; // Stocker la référence du tableau
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Récupérer le membre correspondant à la ligne
                int row = table.getSelectedRow();
                int memberId = (int) table.getValueAt(row, 0); // Supposons que l'ID est à la colonne 0
                Membre membre = controller.getMembreById(memberId); // Assurez-vous d'avoir cette méthode
                List<Livre> livre = null;
                
				try {
					livre = controller.listerLivres();
	                afficherFenetreDetails(membre,livre);
	      
				} catch (Exception e) {
					// 
					e.printStackTrace();
				} // Assurez-vous d'avoir cette méthode

            }
            isPushed = false;
            return label;
        }
    }
    

	public class ButtonMembreRenderer extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = -2338347430378590110L;



		public ButtonMembreRenderer() {
	        setOpaque(true);
	    }
	
	

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
	        setText((value == null) ? "" : value.toString());
	        setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
	        return this;
		}
	}
	
	
	//emprunt renders
	
	public class ButtonEmpruntRenderer extends JButton implements TableCellRenderer {
	    public ButtonEmpruntRenderer() {
	        setOpaque(true);
	    }

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        setText(value == null ? "Emprunter" : value.toString());
	        return this;
	    }
	}
	class ButtonEmpruntEditor extends DefaultCellEditor {
	    private JButton button;
	    private boolean clicked;
	    private JTable table;
	    private List<Livre> livres;
	    private Membre membre; // Pass the member object
	    private int row;

	    public ButtonEmpruntEditor(JTable table, List<Livre> livres, Membre membre) {
	        super(new JTextField());
	        this.table = table;
	        this.livres = livres;
	        this.membre = membre;
	        this.button = new JButton("Emprunter");

	        button.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                fireEditingStopped();
	                if (clicked) {
	                    // Retrieve the selected book
	                    String bookTitle = table.getValueAt(row, 0).toString();
	                    String isbn = table.getValueAt(row, 1).toString();

	                    Livre selectedBook = livres.stream()
	                            .filter(livre -> livre.getTitre().equals(bookTitle) && livre.getIsbn().equals(isbn))
	                            .findFirst()
	                            .orElse(null);

	                    if (selectedBook != null) {
	                        if (membre.getStatus().equals("Inactif")) {
	                            JOptionPane.showMessageDialog(button, "Le membre est inactif et ne peut pas emprunter.", "Erreur", JOptionPane.ERROR_MESSAGE);
	                        } else if (selectedBook.getNombreExemplaires() > 0) {
	                            afficherFenetreEmprunt(membre,selectedBook);
	                            table.setValueAt(selectedBook.getNombreExemplaires(), row, 2);
	                            
	                        } else {
	                            JOptionPane.showMessageDialog(button, "Aucun exemplaire disponible pour: " + bookTitle, "Erreur", JOptionPane.WARNING_MESSAGE);
	                        }
	                    } else {
	                        JOptionPane.showMessageDialog(button, "Livre introuvable!", "Erreur", JOptionPane.ERROR_MESSAGE);
	                    }
	                }
	                clicked = false;
	            }
	        });
	    }

	    @Override
	    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	        this.row = row;
	        this.clicked = true;
	        button.setText(value.toString());
	        return button;
	    }

	    @Override
	    public Object getCellEditorValue() {
	        return "Emprunter";
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

	
}

