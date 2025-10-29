/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;
import controller.KontakController;
import model.Kontak;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author ASUS
 */
public class PengelolaanKontakFrame extends javax.swing.JFrame {

    /**
     * Creates new form PengelolaanKontakFrame
     */
    
    private DefaultTableModel model;
    private KontakController controller;
    public PengelolaanKontakFrame() {
        initComponents();
        
        
         // Inisialisasi controller dan tabel
        controller = new KontakController();
        model = new DefaultTableModel(new String[]{"No", "Nama", "Nomor Telepon", "Kategori"}, 0);
        tblKontak.setModel(model);

        // Panggil fungsi load data awal
        loadContacts();
        
        this.setSize(555, 680); // atur ukuran default jendela
        this.setLocationRelativeTo(null); // tampil di tengah layar
    }

    
     // ========================== MENAMPILKAN DATA ==========================
    private void loadContacts() {
        try {
            model.setRowCount(0);
            List<Kontak> contacts = controller.getAllContacts();
            for (Kontak contact : contacts) {
                model.addRow(new Object[]{
                        contact.getId(),
                        contact.getNama(),
                        contact.getNomorTelepon(),
                        contact.getKategori()
                });
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ========================== FUNGSI TAMBAH ==========================
    private void addContact() {
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) {
            return;
        }

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                JOptionPane.showMessageDialog(this, "Kontak nomor telepon ini sudah ada.",
                        "Kesalahan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.addContact(nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan!");
            clearInputFields();
        } catch (SQLException ex) {
            showError("Gagal menambahkan kontak: " + ex.getMessage());
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor telepon tidak boleh kosong.");
            return false;
        }
        if (!phoneNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.");
            return false;
        }
        if (phoneNumber.length() < 8 || phoneNumber.length() > 15) {
            JOptionPane.showMessageDialog(this, "Nomor telepon harus 8–15 digit.");
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        txtNama.setText("");
        txtNomorTelepon.setText("");
        cmbKategori.setSelectedIndex(0);
    }

    // ========================== FUNGSI EDIT ==========================
    private void editContact() {
        int selectedRow = tblKontak.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.",
                    "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKategori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) {
            return;
        }

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
                JOptionPane.showMessageDialog(this, "Nomor telepon ini sudah ada.",
                        "Kesalahan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.updateContact(id, nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui!");
            clearInputFields();
        } catch (SQLException ex) {
            showError("Gagal memperbarui kontak: " + ex.getMessage());
        }
    }

    private void populateInputFields(int selectedRow) {
        String nama = model.getValueAt(selectedRow, 1).toString();
        String nomorTelepon = model.getValueAt(selectedRow, 2).toString();
        String kategori = model.getValueAt(selectedRow, 3).toString();

        txtNama.setText(nama);
        txtNomorTelepon.setText(nomorTelepon);
        cmbKategori.setSelectedItem(kategori);
    }

    // ========================== FUNGSI HAPUS ==========================
    private void deleteContact() {
        int selectedRow = tblKontak.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            try {
                controller.deleteContact(id);
                loadContacts();
                JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus!");
                clearInputFields();
            } catch (SQLException e) {
                showError(e.getMessage());
            }
        }
    }

    // ========================== FUNGSI PENCARIAN ==========================
    private void searchContact() {
        String keyword = txtPencarian.getText().trim();
        if (!keyword.isEmpty()) {
            try {
                List<Kontak> contacts = controller.searchContacts(keyword);
                model.setRowCount(0);
                for (Kontak contact : contacts) {
                    model.addRow(new Object[]{
                            contact.getId(),
                            contact.getNama(),
                            contact.getNomorTelepon(),
                            contact.getKategori()
                    });
                }
                if (contacts.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tidak ada kontak ditemukan.");
                }
            } catch (SQLException ex) {
                showError(ex.getMessage());
            }
        } else {
            loadContacts();
        }
    }

    // ========================== EXPORT CSV ==========================
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan File CSV");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write("ID,Nama,Nomor Telepon,Kategori\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    writer.write(
                            model.getValueAt(i, 0) + "," +
                            model.getValueAt(i, 1) + "," +
                            model.getValueAt(i, 2) + "," +
                            model.getValueAt(i, 3) + "\n"
                    );
                }
                JOptionPane.showMessageDialog(this,
                        "Data berhasil diekspor ke " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                showError("Gagal menulis file: " + ex.getMessage());
            }
        }
    }

    // ========================== IMPORT CSV ==========================
    private void importFromCSV() {
        showCSVGuide();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin file CSV yang dipilih sudah sesuai dengan format?",
                "Konfirmasi Impor CSV",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Pilih File CSV");
            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = fileChooser.getSelectedFile();

                try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
                    String line = reader.readLine(); // header
                    if (!validateCSVHeader(line)) {
                        JOptionPane.showMessageDialog(this,
                                "Format header CSV tidak valid.\nHarus: ID,Nama,Nomor Telepon,Kategori",
                                "Kesalahan CSV", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int rowCount = 0, errorCount = 0, duplicateCount = 0;
                    StringBuilder errorLog = new StringBuilder("Baris bermasalah:\n");

                    while ((line = reader.readLine()) != null) {
                        rowCount++;
                        String[] data = line.split(",");
                        if (data.length != 4) {
                            errorCount++;
                            errorLog.append("Baris ").append(rowCount + 1).append(": format kolom salah.\n");
                            continue;
                        }

                        String nama = data[1].trim();
                        String nomorTelepon = data[2].trim();
                        String kategori = data[3].trim();

                        if (nama.isEmpty() || nomorTelepon.isEmpty()) {
                            errorCount++;
                            errorLog.append("Baris ").append(rowCount + 1).append(": nama/nomor kosong.\n");
                            continue;
                        }

                        if (!nomorTelepon.matches("\\d+")) {
                            errorCount++;
                            errorLog.append("Baris ").append(rowCount + 1).append(": nomor tidak valid.\n");
                            continue;
                        }

                        try {
                            if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                                duplicateCount++;
                                errorLog.append("Baris ").append(rowCount + 1).append(": kontak duplikat.\n");
                                continue;
                            }

                            controller.addContact(nama, nomorTelepon, kategori);
                        } catch (SQLException ex) {
                            errorCount++;
                            errorLog.append("Baris ").append(rowCount + 1)
                                    .append(": gagal simpan ke DB - ")
                                    .append(ex.getMessage()).append("\n");
                        }
                    }

                    loadContacts();

                    if (errorCount > 0 || duplicateCount > 0) {
                        errorLog.append("\nTotal error: ").append(errorCount)
                                .append("\nTotal duplikat: ").append(duplicateCount);
                        JOptionPane.showMessageDialog(this, errorLog.toString(),
                                "Kesalahan Impor", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Semua data berhasil diimpor!");
                    }

                } catch (IOException ex) {
                    showError("Gagal membaca file: " + ex.getMessage());
                }
            }
        }
    }

    private void showCSVGuide() {
        String guide = """
                Format CSV yang benar:
                Header wajib: ID,Nama,Nomor Telepon,Kategori
                ID bisa kosong
                Contoh:
                1,Andi,08123456789,Teman
                2,Budi,08567890123,Keluarga
                """;
        JOptionPane.showMessageDialog(this, guide, "Panduan CSV", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validateCSVHeader(String header) {
        return header != null && header.trim().equalsIgnoreCase("ID,Nama,Nomor Telepon,Kategori");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblKontak = new javax.swing.JLabel();
        lblNomorTelepon = new javax.swing.JLabel();
        lblKategori = new javax.swing.JLabel();
        lblPencarian = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtNomorTelepon = new javax.swing.JTextField();
        txtPencarian = new javax.swing.JTextField();
        cmbKategori = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Pengelolaan Kontak");
        setSize(new java.awt.Dimension(500, 500));

        jPanel1.setBackground(new java.awt.Color(49, 49, 49));
        jPanel1.setPreferredSize(new java.awt.Dimension(30, 30));

        lblJudul.setFont(new java.awt.Font("Poppins Black", 0, 14)); // NOI18N
        lblJudul.setForeground(new java.awt.Color(255, 255, 255));
        lblJudul.setText("Aplikasi Pengelolaan Kontak");
        jPanel1.add(lblJudul);

        getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBackground(new java.awt.Color(49, 49, 49));
        jPanel2.setPreferredSize(new java.awt.Dimension(250, 250));

        lblKontak.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblKontak.setForeground(new java.awt.Color(255, 255, 255));
        lblKontak.setText("Nama Kontak");

        lblNomorTelepon.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblNomorTelepon.setForeground(new java.awt.Color(255, 255, 255));
        lblNomorTelepon.setText("Nomor Telepon");

        lblKategori.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblKategori.setForeground(new java.awt.Color(255, 255, 255));
        lblKategori.setText("Kategori");

        lblPencarian.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        lblPencarian.setForeground(new java.awt.Color(255, 255, 255));
        lblPencarian.setText("Pencarian");

        txtNama.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N

        txtNomorTelepon.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        txtNomorTelepon.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomorTeleponKeyTyped(evt);
            }
        });

        txtPencarian.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        cmbKategori.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Keluarga", "Teman", "Kantor" }));

        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKontak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKontakMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKontak);

        btnTambah.setBackground(new java.awt.Color(49, 49, 49));
        btnTambah.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnTambah.setForeground(new java.awt.Color(255, 255, 255));
        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(49, 49, 49));
        btnEdit.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setBackground(new java.awt.Color(49, 49, 49));
        btnHapus.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnExport.setBackground(new java.awt.Color(49, 49, 49));
        btnExport.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnExport.setForeground(new java.awt.Color(255, 255, 255));
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setBackground(new java.awt.Color(49, 49, 49));
        btnImport.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnImport.setForeground(new java.awt.Color(255, 255, 255));
        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnKeluar.setBackground(new java.awt.Color(49, 49, 49));
        btnKeluar.setFont(new java.awt.Font("Poppins SemiBold", 0, 12)); // NOI18N
        btnKeluar.setForeground(new java.awt.Color(255, 255, 255));
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnKeluar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnImport))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNomorTelepon)
                            .addComponent(lblKontak)
                            .addComponent(lblPencarian)
                            .addComponent(lblKategori))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNomorTelepon)
                            .addComponent(txtNama)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPencarian)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKontak)
                    .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNomorTelepon)
                    .addComponent(txtNomorTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKategori)
                    .addComponent(cmbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus))
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPencarian)
                    .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImport)
                    .addComponent(btnExport)
                    .addComponent(btnKeluar))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editContact();        
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportToCSV();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addContact();  
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteContact();  
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        importFromCSV();
    }//GEN-LAST:event_btnImportActionPerformed

    private void tblKontakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontakMouseClicked
        int selectedRow = tblKontak.getSelectedRow();
        if (selectedRow != -1) {
            populateInputFields(selectedRow);
        }
    }//GEN-LAST:event_tblKontakMouseClicked

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
        searchContact();
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void txtNomorTeleponKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomorTeleponKeyTyped
       char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
                evt.consume(); // Menolak input non-angka
        }       
    }//GEN-LAST:event_txtNomorTeleponKeyTyped

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnKeluarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblKategori;
    private javax.swing.JLabel lblKontak;
    private javax.swing.JLabel lblNomorTelepon;
    private javax.swing.JLabel lblPencarian;
    private javax.swing.JTable tblKontak;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNomorTelepon;
    private javax.swing.JTextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
