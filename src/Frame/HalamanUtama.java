package Frame;

import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Koneksi.koneksi;
// PRAKTIKUM STRUKTUR DATA

public final class HalamanUtama extends javax.swing.JFrame {
    String Nama, Jurusan, Asal, Gender, sql;
    long NIM;
    String[][] temp;
    Object[][] dataItem;
    int JumlahData = 0;
    DefaultTableModel model;
    int posX, posY;
    Connection con;
    Statement state;
    ResultSet rs;
    PreparedStatement ps;
    
    // Ini adalah Array yang menyimpan alamat (String) pada ikon gender
    String[] AlamatGambar = {"build\\classes\\Ikon\\Laki-laki.png", "build\\classes\\Ikon\\Perempuan.png"};

    public HalamanUtama() {
        try {
            initComponents();
            con = koneksi.getKoneksi();
            state = koneksi.getKoneksi().createStatement();
            
            setLocationRelativeTo(null);
            setResizable(false);
            CbBoxGender.setSelectedIndex(0);
            CbBoxSorting.setSelectedIndex(2);
            LabelHasilCari.setText("");
            TandaSeru.setVisible(false);
            ButtonHapusItem.setEnabled(false);
            ButtonBatalEdit.setVisible(false);
            IkonMahasiswa.setIcon(null);
            TampilkanData(0);
            hapusHistoryPencarian();
        } catch (Exception ex) {
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteAll() {
        //Ini adalah kode yang dijalankan ketika ingin menghapus semua data yang ada
        int Konfirmasi = JOptionPane.showConfirmDialog(null, "SEMUA DATA AKAN DIHAPUS!"+ "\nApakah Anda yakin?", "Konfirmasi", JOptionPane.YES_OPTION);
        if (Konfirmasi == JOptionPane.YES_OPTION) {
            hapusAllData();
            JumlahData = 0;
            simpanJumlahData();
            ButtonHapusItem.setEnabled(false);
            ambilData(0);
            model.setRowCount(0);
            TampilkanData(0);
        }
    }

    public void hasilPencarian() {
        Load_Ikon ikon = new Load_Ikon();
        // Ini adalah kode untuk menampilkan hasil pencarian
        File HasilCari = new File("HasilCari.txt");
        int index = 0;
        String[] hasil = new String[5];
        try {
            Scanner scanner = new Scanner(HasilCari);
            while (index < hasil.length && scanner.hasNext()) {
                hasil[index] = scanner.nextLine();
                index++;
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("ada error " + e);
        }
        LabelHasilCari.setVisible(true);
        TfNama.setText(hasil[0]);
        TfNIM.setText(hasil[1]);
        TfJurusan.setText(hasil[2]);
        TfAsal.setText(hasil[3]);
        if (hasil[4].equals("Laki-laki")) {
            CbBoxGender.setSelectedIndex(0);
            // Ini adalah kode untuk menampilkan ikon mahasiswa laki-laki
            ImageIcon image = ikon.ImageIcon(AlamatGambar[0]);
            IkonMahasiswa.setIcon(image);
        } else {
            CbBoxGender.setSelectedIndex(1);
            // Ini adalah kode untuk menampilkan ikon mahasiswa perempuan
            ImageIcon image = ikon.ImageIcon(AlamatGambar[1]);
            IkonMahasiswa.setIcon(image);
        }
        ButtonHapusItem.setEnabled(true);
        ButtonBatalEdit.setVisible(true);
    }

    public void bersihkanInput() {
        //Ini kode untuk membersihkan input yang ada di semua textfield
        TfNama.setText("");
        TfNIM.setText("");
        TfJurusan.setText("");
        TfAsal.setText("");
        CbBoxGender.setSelectedIndex(0);
        IkonMahasiswa.setIcon(null);
        ButtonBatalEdit.setVisible(false);
        ButtonHapusItem.setEnabled(false);
        LabelHasilCari.setVisible(false);
        TandaSeru.setVisible(false);
    }

    public void simpanJumlahData() {
        // Ini kode untuk menyimpan jumlah data ke dalam file txt
        File NamaFile = new File("JumlahData.txt");
        try {
            FileWriter filewriter = new FileWriter(NamaFile);
            PrintWriter printwriter = new PrintWriter(filewriter);
            printwriter.println(JumlahData);
            printwriter.close();
            filewriter.close();
            System.out.println(">Jumlah data telah tersimpan");
        } catch (Exception e) {
            System.out.println("Ada error. " + e);
        }
    }

    public void updateData() {
    // Kode untuk update data ke dalam file txt (memperbarui serta menyimpan
    // data yang telah diedit)
        String DataUnsorted = "DataUnsorted.txt";
        String kata;
        try {
            FileWriter fwDataUnsorted = new FileWriter(DataUnsorted);
            BufferedWriter bfDataUnsorted = new BufferedWriter(fwDataUnsorted);
            for (int baris = 0; baris < temp.length; baris++) {
                for (int kolom = 0; kolom < 5; kolom++) {
                    if (temp[baris][kolom] != null) {
                        kata = temp[baris][kolom];
                        if (kolom == 0) { //Mengganti spasi menjadi underscore pada kolom yang mengandung String
                            kata = temp[baris][kolom].replaceAll(" ", "_");
                        }                 //dimungkinkan string mengandung spasi
                        if (kolom == 2) {
                            kata = temp[baris][kolom].replaceAll(" ", "_");
                        }
                        if (kolom == 3) {
                            kata = temp[baris][kolom].replaceAll(" ", "_");
                        }
                        bfDataUnsorted.write(kata + " ");
                    }
                }
                if (temp[baris][0] != null) {
                    bfDataUnsorted.newLine();
                }
            }
            bfDataUnsorted.close();
            fwDataUnsorted.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Sepertinya ada error :(");
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        simpanJumlahData();
    }

    public boolean isDuplicate() { //Method untuk memeriksa apakah NIM telah terdaftar
        Nama = TfNama.getText().replace(" ", "_");
        NIM = Long.parseLong(TfNIM.getText());
        Jurusan = TfJurusan.getText().replace(" ", "_");
        Asal = TfAsal.getText().replace(" ", "_");
        if (CbBoxGender.getSelectedIndex() == 0) {
            Gender = "Laki-laki";
        } else {
            Gender = "Perempuan";
        }
        //Memeriksa apakah NIM sudah terdaftar atau belum
        for (int baris = 0; baris < temp.length; baris++) {
            for (int kolom = 0; kolom < temp[baris].length; kolom++) {
                if (NIM == Long.parseLong(temp[baris][1])) {
                    return true;  //jika NIM telah terdaftar (duplicate case)
                }
            }
        }
        return false;  //jika NIM telah terdaftar (duplicate case)
    }

    public void Tambahkan() {
        if (isDuplicate()) { //Kondisi : Jika NIM telah terdaftar
            // Ini adalah konfirmasi ketika menambahkan item dengan NIM yang telah terdaftar
            TandaSeru.setVisible(true);
            int Konfirmasi = JOptionPane.showConfirmDialog(null, "NIM sudah terdaftar. Perbarui data?", "Konfirmasi", JOptionPane.YES_OPTION);
            if (Konfirmasi == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM mahasiswa WHERE nim = '"+TfNIM.getText()+"'";
                String sql2 = "Insert into mahasiswa values ('"+TfNIM.getText()+"','"+TfNama.getText()+"','"+TfJurusan.getText()+"','"
                        +TfAsal.getText()+"','"+CbBoxGender.getSelectedItem()+"');";
                try {
                    state.execute(sql);
                    state.execute(sql2);
                } catch (SQLException ex) {
                    Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                for (int baris = 0; baris < temp.length; baris++) {
                    for (int kolom = 0; kolom < temp[baris].length; kolom++) {
                        if (NIM == Long.parseLong(temp[baris][1])) {
                            //Memperbarui data
                            temp[baris][0] = Nama;
                            temp[baris][1] = Long.toString(NIM);
                            temp[baris][2] = Jurusan;
                            temp[baris][3] = Asal;
                            temp[baris][4] = Gender;
                        }       //Maka akan mengupdate data
                    }
                }
                updateData(); //Mengupdate data dan menyimpan
                bersihkanInput();
                ButtonBatalEdit.setVisible(false);
                hapusHistoryPencarian();
                IkonMahasiswa.setIcon(null);
                TandaSeru.setVisible(false);
                // Menampilkan data terbaru
                ambilData(0);
                model.setRowCount(0);
                TampilkanData(0);
            } else { //jika canceled
                TandaSeru.setVisible(false);
            }
        } else {
            //Jika NIM belum terdaftar
            variabelKeArray(); // Menyimpan data baru
            bersihkanInput();
            IkonMahasiswa.setIcon(null);
            ButtonBatalEdit.setVisible(false);
            TandaSeru.setVisible(false);
            // Menampilkan data terbaru
            ambilData(0);
            model.setRowCount(0);
            TampilkanData(0);
        }
    }

    public void variabelKeArray() {
        //Ini adalah kode untuk mengambil informasi item lalu ditambahkan sebagai item baru
        dataItem = new Object[1][5];
        for (int baris = 0; baris < 1; baris++) {
            for (int kolom = 0; kolom < 5; kolom++) {
                if (kolom == 0) {
                    dataItem[baris][kolom] = Nama;
                }
                else if (kolom == 1) {
                    dataItem[baris][kolom] = NIM;
                }
                else if (kolom == 2) {
                    dataItem[baris][kolom] = Jurusan;

                }
                else if (kolom == 3) {
                    dataItem[baris][kolom] = Asal;
                }
                if (kolom == 4) {
                    dataItem[baris][kolom] = Gender;
                }
            }
            JumlahData++;
            simpanJumlahData();
            tambahData();
        }
    }

    public void tambahData() {
        // Ini kode untuk menambah data ke dalam file .txt
        String DataMahasiswa = "DataUnsorted.txt";
        try {
            FileWriter fwDataMahasiswa = new FileWriter(DataMahasiswa, true);
            BufferedWriter bfDataMahasiswa = new BufferedWriter(fwDataMahasiswa);
            for (int baris = 0; baris < dataItem.length; baris++) {
                for (int kolom = 0; kolom < 5; kolom++) {
                    bfDataMahasiswa.write(dataItem[baris][kolom] + " ");
                }
                bfDataMahasiswa.newLine();
            }
            bfDataMahasiswa.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Sepertinya ada error :(");
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Sorting(int PilihSorting) {
        // Ini kode untuk sorting data berdasarkan Jurusan (ascending)
        // Data dikirim ke kelas DataSorted lalu memanggil method selection sort
        String tempNama = null;
        long tempNIM = 0;
        String tempJurusan = null;
        String tempAsal = null;
        String tempGender = null;
        DataMahasiswa dataMahasiswa = new DataMahasiswa(JumlahData);
        for (int baris = 0; baris < temp.length; baris++) {
            for (int kolom = 0; kolom < temp[baris].length; kolom++) {
                if (kolom == 0) {
                    tempNama = temp[baris][kolom].replace(" ", "_");
                }
                if (kolom == 1) {
                    tempNIM = Long.parseLong(temp[baris][kolom]);
                }
                if (kolom == 2) {
                    tempJurusan = temp[baris][kolom].replace(" ", "_");
                }
                if (kolom == 3) {
                    tempAsal = temp[baris][kolom].replace(" ", "_");
                }
                if (kolom == 4) {
                    tempGender = temp[baris][kolom];
                }
            }
            dataMahasiswa.Insert(tempNama, tempNIM, tempJurusan, tempAsal, tempGender);
        }
        if (PilihSorting == 0) {
            dataMahasiswa.NamaByShellSort(); //ini nanti sorting dengan shellsort
        }
        if (PilihSorting == 1) {
            dataMahasiswa.NimByQuicksort(); //ini nanti sorting dengan quicksort
        }
        dataMahasiswa.simpanData();
        dataMahasiswa.displayArray();
    }

    public void ambilData(int JenisData) {
        // Ini kode untuk Mengambil data serta jumlah item yang terbaru yang tersimpan dalam file txt
        // lalu dirubah menjadi array 2 dimensi
        // agar nanti bisa ditampilkan dalam tabel        
        File DataSorted = new File("DataSorted.txt");
        File DataUnsorted = new File("DataUnsorted.txt");
        File FilenElemen = new File("JumlahData.txt");
        int baris = 0;
        int kolom = 0;
        String kata;
        try {
            if (!DataSorted.exists() || !DataUnsorted.exists() || !FilenElemen.exists()) {
                DataSorted.createNewFile();
                DataUnsorted.createNewFile();
                FilenElemen.createNewFile();
            }
            Scanner scanSorted = new Scanner(DataSorted);
            Scanner scanUnsorted = new Scanner(DataUnsorted);
            Scanner scanJumlData = new Scanner(FilenElemen);
            if (scanJumlData.hasNext()) {
                JumlahData = Integer.parseInt(scanJumlData.next());
            }
            temp = new String[JumlahData][5];
            //Ini kode untuk merubah String dari txt menjadi array 2 dimensi
            if (JenisData == 0) {  //jika jenis data yang ingin diambil adalah data unsorted
                while (baris < temp.length && scanUnsorted.hasNextLine()) {
                    kolom = 0;
                    while (kolom < temp[baris].length && scanUnsorted.hasNext()) {
                        kata = scanUnsorted.next().replace("_", " ");
                        temp[baris][kolom] = kata;
                        kolom++;
                    }
                    baris++;
                }
                scanSorted.close();
                scanUnsorted.close();
                scanJumlData.close();
            }
            if (JenisData == 1) {
                while (baris < temp.length && scanSorted.hasNextLine()) {
                    kolom = 0;
                    while (kolom < temp[baris].length && scanSorted.hasNext()) {
                        kata = scanSorted.next().replace("_", " ");
                        temp[baris][kolom] = kata;
                        kolom++;
                    }
                    baris++;
                }
                scanSorted.close();
                scanSorted.close();
                scanJumlData.close();
            }
        } catch (Exception e) {
            System.out.println("Ada Error " + e);
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //pencarian berdasarkan nama atau NIM
    public void Cari(long nim) {
        //Ini kode untuk merubah array 2 dimensi menjadi variabel lalu insertHash ke kelas DataSorted
        String tempNama = null;
        long tempNIM = 0;
        String tempJurusan = null;
        String tempAsal = null;
        String tempGender = null;
        DataMahasiswa dataMahasiswa = new DataMahasiswa(50);
        for (int baris = 0; baris < temp.length; baris++) {
            for (int kolom = 0; kolom < temp[baris].length; kolom++) {
                if (kolom == 0) {
                    tempNama = temp[baris][kolom];
                }
                if (kolom == 1) {
                    tempNIM = Long.parseLong(temp[baris][kolom]);
                }
                if (kolom == 2) {
                    tempJurusan = temp[baris][kolom];
                }
                if (kolom == 3) {
                    tempAsal = temp[baris][kolom];
                }
                if (kolom == 4) {
                    tempGender = temp[baris][kolom];
                }
            }
            dataMahasiswa.insertHash(tempNama, tempNIM, tempJurusan, tempAsal, tempGender);
        }
        dataMahasiswa.displayTable();
        if (dataMahasiswa.find(nim) != null) {
            LabelHasilCari.setText("Kata kunci \"" + nim + "\" ditemukan");
            hasilPencarian();
            TfPencarian.setText("");
        }
    }

    public void hapusHistoryPencarian() {
        // Ini kode untuk menghapus riwayat hasil pencarian
        File NamaFile = new File("HasilCari.txt");
        try {
            FileWriter filewriter = new FileWriter(NamaFile);
            PrintWriter printwriter = new PrintWriter(filewriter);
            printwriter.println("");
            printwriter.close();
            System.out.println(">Riwayat pencarian telah dibersihkan");
        } catch (Exception e) {
            System.out.println("Ada error. " + e);
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    //Menampilkan data ke tabel
    public void TampilkanData(int JenisData) {
        //dari array 2 dimensi dirubah menjadi array 1 dimensi
        if (JenisData == 0) {
            ambilData(0);
        }
        if (JenisData == 1) {
            ambilData(1);
        }
        model = (DefaultTableModel) Tabelnya.getModel();
        model.setRowCount(0);
        for (int baris = 0; baris < temp.length; baris++) {
            String[] barisData = new String[temp[baris].length];
            for (int kolom = 0; kolom < temp[baris].length; kolom++) {
                barisData[kolom] = temp[baris][kolom];
            }
            //Ini adalah kode untuk menampilkan data ke tabel
            model.addRow(barisData);
        }
    }

    public void klikTabel() {
        model = (DefaultTableModel) Tabelnya.getModel();
        Load_Ikon icon = new Load_Ikon();

        TfNama.setText(model.getValueAt(Tabelnya.getSelectedRow(), 0).toString());
        TfNIM.setText(model.getValueAt(Tabelnya.getSelectedRow(), 1).toString());
        TfJurusan.setText(model.getValueAt(Tabelnya.getSelectedRow(), 2).toString());
        TfAsal.setText(model.getValueAt(Tabelnya.getSelectedRow(), 3).toString());
        String tempGender = (model.getValueAt(Tabelnya.getSelectedRow(), 4).toString());
        if (tempGender.equals("Laki-laki")) {
            CbBoxGender.setSelectedIndex(0);
            // Ini adalah kode untuk menampilkan ikon mahasiswa laki-laki
            ImageIcon image = icon.ImageIcon(AlamatGambar[0]);
            IkonMahasiswa.setIcon(image);
        } else {
            CbBoxGender.setSelectedIndex(1);
            // Ini adalah kode untuk menampilkan ikon mahasiswa perempuan
            ImageIcon image = icon.ImageIcon(AlamatGambar[1]);
            IkonMahasiswa.setIcon(image);
        }
        ButtonHapusItem.setEnabled(true);
        ButtonBatalEdit.setVisible(true);
    }

    public void hapusItem() {
        try {
            ambilData(0);
            NIM = Long.parseLong(TfNIM.getText());
            // Ini adalah kode konfirmasi ketika ingin menghapus item yang telah dipilih ataupun dicari
            int Konfirmasi = JOptionPane.showConfirmDialog(null, "Hapus Data ini?", "Konfirmasi", JOptionPane.YES_OPTION);
            if (Konfirmasi == JOptionPane.YES_OPTION) {
                for (int baris = 0; baris < temp.length; baris++) {
                    if (NIM == Long.parseLong(temp[baris][1])) {
                        temp[baris][0] = null;
                        temp[baris][1] = null;
                        temp[baris][2] = null;
                        temp[baris][3] = null;
                        temp[baris][4] = null;
                        JumlahData--;
                        JOptionPane.showMessageDialog(null, "Data berhasil dihapus");
                    }
                }
                ButtonHapusItem.setEnabled(false);
                updateData();
                ButtonBatalEdit.setVisible(false);
                ambilData(0);
                model.setRowCount(0);
                TampilkanData(0);
                hapusHistoryPencarian();
                LabelHasilCari.setText("");
                bersihkanInput();
                IkonMahasiswa.setIcon(null);
                TandaSeru.setVisible(false);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Masukkan input dengan benar"+e);
        }
    }

    public void hapusAllData() {
        // Ini kode untuk menghapus semua data yang tersimpan di dalam file txt
        File JumlData = new File("JumlahData.txt");
        File DataSorted = new File("DataUnsorted.txt");
        try {
            FileWriter fwJmlData = new FileWriter(JumlData);
            FileWriter fwDataSorted = new FileWriter(DataSorted);
            PrintWriter pwJmlData = new PrintWriter(fwJmlData);
            PrintWriter pwDataSorted = new PrintWriter(fwDataSorted);
            pwJmlData.println("");
            pwDataSorted.println("");
            pwJmlData.close();
            pwDataSorted.close();
            System.out.println(">Semua data telah terhapus");
        } catch (Exception e) {
            System.out.println("Ada error. " + e);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelUtama = new javax.swing.JPanel();
        PanelPencarian = new javax.swing.JPanel();
        TfPencarian = new javax.swing.JTextField();
        ButtonCari = new javax.swing.JButton();
        PanelInfoCari = new javax.swing.JLabel();
        LabelDeveloper = new javax.swing.JLabel();
        PanelInformasi = new javax.swing.JPanel();
        LabelNama = new javax.swing.JLabel();
        TfNama = new javax.swing.JTextField();
        LabelNIM = new javax.swing.JLabel();
        TfNIM = new javax.swing.JTextField();
        TfJurusan = new javax.swing.JTextField();
        LabelJurusan = new javax.swing.JLabel();
        LabelAsal = new javax.swing.JLabel();
        ButtonTambahkan = new javax.swing.JButton();
        ButtonBersihkan = new javax.swing.JButton();
        PanelGambar = new javax.swing.JPanel();
        IkonMahasiswa = new javax.swing.JLabel();
        TfAsal = new javax.swing.JTextField();
        LabelHasilCari = new javax.swing.JLabel();
        ButtonHapusItem = new javax.swing.JButton();
        LabelInfoAtas = new javax.swing.JLabel();
        LabelGender = new javax.swing.JLabel();
        CbBoxGender = new javax.swing.JComboBox<>();
        LabelInfoBawah = new javax.swing.JLabel();
        ButtonBatalEdit = new javax.swing.JButton();
        TandaSeru = new javax.swing.JLabel();
        PanelTabel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Tabelnya = new javax.swing.JTable();
        CbBoxSorting = new javax.swing.JComboBox<>();
        LabelPengurutan = new javax.swing.JLabel();
        ButtonHapusData = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 255));

        PanelUtama.setBackground(new java.awt.Color(102, 51, 255));
        PanelUtama.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        PanelUtama.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                PanelUtamaMouseDragged(evt);
            }
        });
        PanelUtama.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                PanelUtamaMousePressed(evt);
            }
        });

        PanelPencarian.setBackground(new java.awt.Color(102, 51, 255));
        PanelPencarian.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pencarian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 18), new java.awt.Color(255, 255, 255))); // NOI18N

        TfPencarian.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TfPencarian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TfPencarianActionPerformed(evt);
            }
        });
        TfPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TfPencarianKeyPressed(evt);
            }
        });

        ButtonCari.setBackground(new java.awt.Color(255, 255, 255));
        ButtonCari.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ButtonCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonCari.png"))); // NOI18N
        ButtonCari.setText("Cari");
        ButtonCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonCariActionPerformed(evt);
            }
        });

        PanelInfoCari.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        PanelInfoCari.setForeground(new java.awt.Color(255, 255, 255));
        PanelInfoCari.setText("Pencarian berdasarkan NIM mahasiswa.");

        LabelDeveloper.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout PanelPencarianLayout = new javax.swing.GroupLayout(PanelPencarian);
        PanelPencarian.setLayout(PanelPencarianLayout);
        PanelPencarianLayout.setHorizontalGroup(
            PanelPencarianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPencarianLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TfPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ButtonCari, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelInfoCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelDeveloper)
                .addContainerGap())
        );
        PanelPencarianLayout.setVerticalGroup(
            PanelPencarianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPencarianLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelPencarianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonCari)
                    .addComponent(PanelInfoCari)
                    .addComponent(LabelDeveloper)
                    .addComponent(TfPencarian))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        PanelInformasi.setBackground(new java.awt.Color(102, 51, 255));
        PanelInformasi.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data Informasi Mahasiswa", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 36), new java.awt.Color(255, 255, 255))); // NOI18N

        LabelNama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LabelNama.setForeground(new java.awt.Color(255, 255, 255));
        LabelNama.setText("Nama :");

        TfNama.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        LabelNIM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LabelNIM.setForeground(new java.awt.Color(255, 255, 255));
        LabelNIM.setText("NIM :");

        TfNIM.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        TfJurusan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        LabelJurusan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LabelJurusan.setForeground(new java.awt.Color(255, 255, 255));
        LabelJurusan.setText("Jurusan :");

        LabelAsal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LabelAsal.setForeground(new java.awt.Color(255, 255, 255));
        LabelAsal.setText("Asal :");

        ButtonTambahkan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        ButtonTambahkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonSimpan.png"))); // NOI18N
        ButtonTambahkan.setText("Tambahkan");
        ButtonTambahkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonTambahkanActionPerformed(evt);
            }
        });

        ButtonBersihkan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        ButtonBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonBersihkan.png"))); // NOI18N
        ButtonBersihkan.setText("Bersihkan");
        ButtonBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonBersihkanActionPerformed(evt);
            }
        });

        PanelGambar.setBackground(new java.awt.Color(51, 51, 51));

        IkonMahasiswa.setForeground(new java.awt.Color(51, 51, 51));
        IkonMahasiswa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout PanelGambarLayout = new javax.swing.GroupLayout(PanelGambar);
        PanelGambar.setLayout(PanelGambarLayout);
        PanelGambarLayout.setHorizontalGroup(
            PanelGambarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelGambarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(IkonMahasiswa, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelGambarLayout.setVerticalGroup(
            PanelGambarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelGambarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IkonMahasiswa, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addContainerGap())
        );

        TfAsal.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        LabelHasilCari.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelHasilCari.setForeground(new java.awt.Color(255, 255, 255));
        LabelHasilCari.setText("Hasil Pencarian");

        ButtonHapusItem.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        ButtonHapusItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonHapus.png"))); // NOI18N
        ButtonHapusItem.setText("Hapus");
        ButtonHapusItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonHapusItemActionPerformed(evt);
            }
        });

        LabelInfoAtas.setForeground(new java.awt.Color(255, 255, 255));
        LabelInfoAtas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonBantuan.png"))); // NOI18N
        LabelInfoAtas.setText("Untuk melakukan pengeditan data mahasiswa, silakan pilih terlebih dahulu data yang ada di tabel.");

        LabelGender.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        LabelGender.setForeground(new java.awt.Color(255, 255, 255));
        LabelGender.setText("Gender :");

        CbBoxGender.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        CbBoxGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Laki-laki", "Perempuan" }));
        CbBoxGender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbBoxGenderActionPerformed(evt);
            }
        });

        LabelInfoBawah.setForeground(new java.awt.Color(255, 255, 255));
        LabelInfoBawah.setText("Atau bisa dicari NIM mahasiswa di kolom pencarian.");

        ButtonBatalEdit.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        ButtonBatalEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonEdit.png"))); // NOI18N
        ButtonBatalEdit.setText("Batal");
        ButtonBatalEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonBatalEditActionPerformed(evt);
            }
        });

        TandaSeru.setBackground(new java.awt.Color(255, 0, 51));
        TandaSeru.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        TandaSeru.setForeground(new java.awt.Color(255, 255, 255));
        TandaSeru.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        TandaSeru.setText("!");
        TandaSeru.setOpaque(true);

        javax.swing.GroupLayout PanelInformasiLayout = new javax.swing.GroupLayout(PanelInformasi);
        PanelInformasi.setLayout(PanelInformasiLayout);
        PanelInformasiLayout.setHorizontalGroup(
            PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInformasiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelInformasiLayout.createSequentialGroup()
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ButtonHapusItem)
                            .addGroup(PanelInformasiLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LabelJurusan)
                                    .addComponent(LabelNIM)
                                    .addComponent(LabelNama)
                                    .addComponent(LabelAsal)
                                    .addComponent(LabelGender))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelInformasiLayout.createSequentialGroup()
                                .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(TfAsal, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TfJurusan, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TfNIM, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TfNama, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(TandaSeru, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(CbBoxGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelInformasiLayout.createSequentialGroup()
                                .addComponent(ButtonBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(ButtonTambahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(28, 28, 28)
                        .addComponent(PanelGambar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(LabelInfoAtas)))
            .addGroup(PanelInformasiLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(LabelInfoBawah)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelInformasiLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(LabelHasilCari)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ButtonBatalEdit)
                .addGap(216, 216, 216))
        );
        PanelInformasiLayout.setVerticalGroup(
            PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelInformasiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelInformasiLayout.createSequentialGroup()
                        .addComponent(LabelHasilCari)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelInformasiLayout.createSequentialGroup()
                        .addComponent(ButtonBatalEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelGambar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelInformasiLayout.createSequentialGroup()
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TfNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelNama))
                        .addGap(18, 18, 18)
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TfNIM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelNIM)
                            .addComponent(TandaSeru))
                        .addGap(18, 18, 18)
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TfJurusan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelJurusan))
                        .addGap(18, 18, 18)
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelAsal)
                            .addComponent(TfAsal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LabelGender)
                            .addComponent(CbBoxGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(PanelInformasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonBersihkan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonTambahkan, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonHapusItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(LabelInfoAtas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelInfoBawah)
                .addGap(7, 7, 7))
        );

        PanelTabel.setBackground(new java.awt.Color(102, 51, 255));
        PanelTabel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Data yang Tersimpan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 36), new java.awt.Color(255, 255, 255))); // NOI18N

        Tabelnya.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        Tabelnya.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Tabelnya.setForeground(new java.awt.Color(51, 51, 51));
        Tabelnya.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nama", "NIM", "Jurusan", "Asal", "Gender"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabelnya.setGridColor(new java.awt.Color(102, 102, 102));
        Tabelnya.setRowHeight(20);
        Tabelnya.getTableHeader().setReorderingAllowed(false);
        Tabelnya.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabelnyaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Tabelnya);

        CbBoxSorting.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        CbBoxSorting.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nama", "NIM", "Waktu" }));
        CbBoxSorting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbBoxSortingActionPerformed(evt);
            }
        });

        LabelPengurutan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        LabelPengurutan.setForeground(new java.awt.Color(255, 255, 255));
        LabelPengurutan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonSorting.png"))); // NOI18N
        LabelPengurutan.setText("Urutkan Berdasarkan :");

        ButtonHapusData.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        ButtonHapusData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Ikon/IkonDeleteAll.gif"))); // NOI18N
        ButtonHapusData.setText("Hapus semua data");
        ButtonHapusData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonHapusDataActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelTabelLayout = new javax.swing.GroupLayout(PanelTabel);
        PanelTabel.setLayout(PanelTabelLayout);
        PanelTabelLayout.setHorizontalGroup(
            PanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                    .addGroup(PanelTabelLayout.createSequentialGroup()
                        .addComponent(LabelPengurutan)
                        .addGap(18, 18, 18)
                        .addComponent(CbBoxSorting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ButtonHapusData)))
                .addContainerGap())
        );
        PanelTabelLayout.setVerticalGroup(
            PanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelTabelLayout.createSequentialGroup()
                .addGroup(PanelTabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CbBoxSorting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelPengurutan)
                    .addComponent(ButtonHapusData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout PanelUtamaLayout = new javax.swing.GroupLayout(PanelUtama);
        PanelUtama.setLayout(PanelUtamaLayout);
        PanelUtamaLayout.setHorizontalGroup(
            PanelUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelUtamaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(PanelPencarian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelUtamaLayout.createSequentialGroup()
                        .addComponent(PanelInformasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PanelTabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelUtamaLayout.setVerticalGroup(
            PanelUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelUtamaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(PanelPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelUtamaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelTabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelInformasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelUtama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelUtama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PanelUtamaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelUtamaMousePressed
        // Ini adalah kode untuk membuat frame transparan ketika diklik
        //dengan menurunkan nilai opacitynya
        posX = evt.getX();
        posY = evt.getY();
    }//GEN-LAST:event_PanelUtamaMousePressed

    private void PanelUtamaMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelUtamaMouseDragged
        // Ini adalah kode untuk menggerakkan frame undecorated dengan mengambil nilai x,y dari pointer
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - posX, y - posY);
    }//GEN-LAST:event_PanelUtamaMouseDragged

    private void ButtonHapusDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonHapusDataActionPerformed
        int Konfirmasi = JOptionPane.showConfirmDialog(null, "SEMUA DATA AKAN DIHAPUS!\nApakah Anda yakin?", "Konfirmasi", JOptionPane.YES_OPTION);
        if (Konfirmasi == JOptionPane.YES_OPTION) {
            String sql = "truncate table mahasiswa;";
        
        try {
            state.execute(sql);
            
        } catch (SQLException ex) {
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
            hapusAllData();
            JumlahData = 0;
            simpanJumlahData();
            ButtonHapusItem.setEnabled(false);
            ambilData(0);
            TampilkanData(0);
        }
    }//GEN-LAST:event_ButtonHapusDataActionPerformed

    private void CbBoxSortingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbBoxSortingActionPerformed
        //Ini kode untuk fungsi combo box sorting
        if (CbBoxSorting.getSelectedIndex() == 0) {
            ambilData(0);
            //sorting lalu simpan
            Sorting(0);

            model.setRowCount(0);
            TampilkanData(1);
        }
        if (CbBoxSorting.getSelectedIndex() == 1) {
            ambilData(0);
            //sorting lalu simpan
            Sorting(1);

            model.setRowCount(0);
            TampilkanData(1);
        }
        if (CbBoxSorting.getSelectedIndex() == 2) {
            TampilkanData(0);
        }
    }//GEN-LAST:event_CbBoxSortingActionPerformed

    private void TabelnyaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelnyaMouseClicked
        klikTabel();
    }//GEN-LAST:event_TabelnyaMouseClicked

    private void ButtonBatalEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonBatalEditActionPerformed
        bersihkanInput();
        LabelHasilCari.setVisible(false);
        ButtonBatalEdit.setVisible(false);
        ButtonHapusItem.setEnabled(false);
        IkonMahasiswa.setIcon(null);
        hapusHistoryPencarian();
        TandaSeru.setVisible(false);
    }//GEN-LAST:event_ButtonBatalEditActionPerformed

    private void CbBoxGenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbBoxGenderActionPerformed
        Load_Ikon ikon = new Load_Ikon();
        if (CbBoxGender.getSelectedIndex() == 0) {
            // Ini adalah kode untuk menampilkan ikon mahasiswa laki-laki
            ImageIcon image = ikon.ImageIcon(AlamatGambar[0]);
            IkonMahasiswa.setIcon(image);
        } else {
            // Ini adalah kode untuk menampilkan ikon mahasiswa perempuan
            ImageIcon image = ikon.ImageIcon(AlamatGambar[1]);
            IkonMahasiswa.setIcon(image);
        }
    }//GEN-LAST:event_CbBoxGenderActionPerformed

    private void ButtonHapusItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonHapusItemActionPerformed
        String sql = "DELETE FROM mahasiswa WHERE nim = '"+TfNIM.getText()+"'";
        try {
            state.execute(sql);
            
        } catch (SQLException ex) {
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        hapusItem();
    }//GEN-LAST:event_ButtonHapusItemActionPerformed

    private void ButtonBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonBersihkanActionPerformed
       bersihkanInput();
    }//GEN-LAST:event_ButtonBersihkanActionPerformed

    private void ButtonTambahkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonTambahkanActionPerformed
        String sql = "insert into mahasiswa values ('"+TfNIM.getText()+"','"+TfNama.getText()+"','"+TfJurusan.getText()+"','"+TfAsal.getText()+"','"+CbBoxGender.getSelectedItem()+"');";
        try {
            state.execute(sql);
        } catch (SQLException ex) {
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ambilData(0);
            Tambahkan();
            JOptionPane.showMessageDialog(null, "Data berhasil disimpan");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Masukkan input dengan benar");
        }
    }//GEN-LAST:event_ButtonTambahkanActionPerformed

    private void ButtonCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonCariActionPerformed
        long tempNIM = 0;
        try {
            tempNIM = Long.parseLong(TfPencarian.getText());
            Cari(tempNIM);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "\"" + tempNIM + "\"  tidak ditemukan");
        }
    }//GEN-LAST:event_ButtonCariActionPerformed

    private void TfPencarianKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TfPencarianKeyPressed
        //Ini adalah kode untuk mencari data ketika tombol Enter ditekan
        long key = 0;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                key = Long.parseLong(TfPencarian.getText());
                Cari(key);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Masukkan input dengan benar");
            }
        }
    }//GEN-LAST:event_TfPencarianKeyPressed

    private void TfPencarianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TfPencarianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TfPencarianActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HalamanUtama.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new HalamanUtama().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButtonBatalEdit;
    private javax.swing.JButton ButtonBersihkan;
    private javax.swing.JButton ButtonCari;
    private javax.swing.JButton ButtonHapusData;
    private javax.swing.JButton ButtonHapusItem;
    private javax.swing.JButton ButtonTambahkan;
    private javax.swing.JComboBox<String> CbBoxGender;
    private javax.swing.JComboBox<String> CbBoxSorting;
    private javax.swing.JLabel IkonMahasiswa;
    private javax.swing.JLabel LabelAsal;
    private javax.swing.JLabel LabelDeveloper;
    private javax.swing.JLabel LabelGender;
    private javax.swing.JLabel LabelHasilCari;
    private javax.swing.JLabel LabelInfoAtas;
    private javax.swing.JLabel LabelInfoBawah;
    private javax.swing.JLabel LabelJurusan;
    private javax.swing.JLabel LabelNIM;
    private javax.swing.JLabel LabelNama;
    private javax.swing.JLabel LabelPengurutan;
    private javax.swing.JPanel PanelGambar;
    private javax.swing.JLabel PanelInfoCari;
    private javax.swing.JPanel PanelInformasi;
    private javax.swing.JPanel PanelPencarian;
    private javax.swing.JPanel PanelTabel;
    private javax.swing.JPanel PanelUtama;
    private javax.swing.JTable Tabelnya;
    private javax.swing.JLabel TandaSeru;
    private javax.swing.JTextField TfAsal;
    private javax.swing.JTextField TfJurusan;
    private javax.swing.JTextField TfNIM;
    private javax.swing.JTextField TfNama;
    private javax.swing.JTextField TfPencarian;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
