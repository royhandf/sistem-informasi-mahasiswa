package Frame;

import java.io.*;
import java.util.logging.*;
import javax.swing.*;

public class DataMahasiswa {

    private int JumlahData;
    HalamanUtama beranda;
    private final Mahasiswa[] mahasiswa;
    private final Mahasiswa[] hashMahasiswa;
    public Object[][] dataItem;
    public String[][] temp;
    int Size;

    //Ini method untuk menampilkan data yang telah diinsert (ditampilkan di console)
    public void displayArray() {
        System.out.println("\tJumlah data : =" + JumlahData);
        for (int i = 0; i < JumlahData; i++) {
            mahasiswa[i].displayMahasiswa();
        }
        System.out.println("----------------------------------------------------\n");
    }

    public DataMahasiswa(int size) {
        Size = size;
        hashMahasiswa = new Mahasiswa[Size];
        mahasiswa = new Mahasiswa[Size];
        JumlahData = 0;
    }

    public int hasFunc(long key) {
        return (int) (key % Size);
    }

    public void insertHash(String nama, long nim, String jurusan, String asal, String gender) {
        Mahasiswa mhs = new Mahasiswa(nama, nim, jurusan, asal, gender);
         // Long key = mhs.getKey();
        int hashVal = hasFunc(nim);
        while (hashMahasiswa[hashVal] != null) {
            ++hashVal;
            hashVal %= Size;
        }
        hashMahasiswa[hashVal] = mhs;
        JumlahData++;
    }

    public void displayTable() {
        System.out.println("Table: ");
        for (int i = 0; i < Size; i++) {
            if (hashMahasiswa[i] != null) {
                System.out.println(" | " + i + "\t | " + hashMahasiswa[i].getNama() + " |");
            } else {
                System.out.println(" | " + i + "\t | -- |");
            }
        }
        System.out.println("");
    }

    public void simpanData() {
        // Kode untuk menyimpan data ke dalam file txt
        String LokasiData = "DataSorted.txt";
        try {
            FileWriter filewriter = new FileWriter(LokasiData);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            for (int i = 0; i < mahasiswa.length; i++) {
                bufferedwriter.write(mahasiswa[i].getNama() + " " + mahasiswa[i].getNIM() + " " + mahasiswa[i].getJurusan() + " " + mahasiswa[i].getAsal() + " " + mahasiswa[i].getGender());
                bufferedwriter.newLine();
            }
            bufferedwriter.close();
        } 
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Sepertinya ada error :(");
            Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Ini kode untuk insert data ke kelas mahasiswa UNTUK SORTING
    public void Insert(String nama, long nim, String jurusan, String asal, String gender) {
        mahasiswa[JumlahData] = new Mahasiswa(nama, nim, jurusan, asal, gender);
        JumlahData++;
    }

    //Sort berdasarkan jurusan
    int BandingNama;
    int TukarNama;

    public void NamaByShellSort() {
        BandingNama = 0;
        TukarNama = 0;
        int luar, dalam, min;
        System.out.println("----------------------------------------------------");
        for (luar = 0; luar < JumlahData - 1; luar++) {
            min = luar;
            for (dalam = luar + 1; dalam < JumlahData; dalam++) {
                if (mahasiswa[dalam].getNama().compareTo(mahasiswa[min].getNama()) < 0) {
                    min = dalam;
                }
                BandingNama++;
            }
            swap(luar, min);
            System.out.println(mahasiswa[luar].getNama() + " ditukar dengan " + mahasiswa[min].getNama());
            TukarNama++;
        }
        System.out.println("Sorting: Selection sort berdasarkan nama");
        System.out.println("Membandingkan: " + BandingNama + " kali");
        System.out.println("Menukar: " + TukarNama + " kali");
    }

    //Sort berdasarkan NIM
    int BandingNIM;
    int TukarNIM;

    public void NimByQuicksort() {
        BandingNIM = 0;
        TukarNIM = 0;
        int luar, dalam, min;
        System.out.println("----------------------------------------------------");
        for (luar = 0; luar < JumlahData - 1; luar++) {
            min = luar;
            for (dalam = luar + 1; dalam < JumlahData; dalam++) {
                if (mahasiswa[dalam].getNIM() < mahasiswa[min].getNIM()) {
                    min = dalam;
                }
                BandingNIM++;
            }
            swap(luar, min);
            System.out.println(mahasiswa[luar].getNIM() + " ditukar dengan " + mahasiswa[min].getNIM());
            TukarNIM++;
        }
        System.out.println("\tSorting: Selection sort berdasarkan NIM");
        System.out.println("\tMembandingkan: " + BandingNIM + " kali");
        System.out.println("\tMenukar: " + TukarNIM + " kali");
    }

    public void swap(int one, int two) { //Ini kode untuk menukar item
        Mahasiswa temp = mahasiswa[one];
        mahasiswa[one] = mahasiswa[two];
        mahasiswa[two] = temp;
    }

    // Pencarian hash
    public Mahasiswa find(long key) {
        int hashVal = hasFunc(key);
        System.out.println(">find data with NIM " + key);
        while (hashMahasiswa[hashVal] != null) {
            if (hashMahasiswa[hashVal].getNIM() == key) {
                File NamaFile = new File("HasilCari.txt");
                try {
                    FileWriter filewriter = new FileWriter(NamaFile);
                    PrintWriter printwriter = new PrintWriter(filewriter);
                    printwriter.println(hashMahasiswa[hashVal].getNama());
                    printwriter.println(hashMahasiswa[hashVal].getNIM());
                    printwriter.println(hashMahasiswa[hashVal].getJurusan());
                    printwriter.println(hashMahasiswa[hashVal].getAsal());
                    printwriter.println(hashMahasiswa[hashVal].getGender());
                    printwriter.close();
                } catch (IOException e) {
                    System.out.println("Ada error. " + e);
                    Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, e);
                }
                System.out.println(">\"" + key + "\" ditemukan");
                System.out.println(">Hasil pencarian telah disimpan");
                JOptionPane.showMessageDialog(null, "\"" + key + "\"  ditemukan");
                return hashMahasiswa[hashVal];
            }
            ++hashVal;
            hashVal %= Size;
        }
        JOptionPane.showMessageDialog(null, "\"" + key + "\"  tidak ditemukan");
        System.out.println(">\"" + key + "\" tidak ditemukan");
        return null;
    }

    // Pencarian berdasarkan nama atau NIM
    public boolean Cari(String kunci) {
        int baris;
        String tempNIM;
        for (baris = 0; baris < JumlahData; baris++) {
            tempNIM = String.valueOf(hashMahasiswa[baris].getNIM());
            if (mahasiswa[baris].getNama().equalsIgnoreCase(kunci) || tempNIM.equals(kunci)) {
                // Ini kode untuk menyimpan hasil pencarian ke dalam file txt
                File NamaFile = new File("HasilCari.txt");
                try {
                    FileWriter filewriter = new FileWriter(NamaFile);
                    PrintWriter printwriter = new PrintWriter(filewriter);
                    for (int kolom = 0; kolom < 5; kolom++) {
                        if (kolom == 0) {   //kolom untuk nama
                            printwriter.println(mahasiswa[baris].getNama());
                        }
                        if (kolom == 1) {   //kolom untuk NIM
                            printwriter.println(mahasiswa[baris].getNIM());
                        }
                        if (kolom == 2) {   //kolom untuk jurusan
                            printwriter.println(mahasiswa[baris].getJurusan());
                        }
                        if (kolom == 3) {   //kolom untuk asal
                            printwriter.println(mahasiswa[baris].getAsal());
                        }
                        if (kolom == 4) {   //kolom untuk gender
                            printwriter.println(mahasiswa[baris].getGender());
                        }
                    }
                    printwriter.close();
                    System.out.println(">Hasil pencarian telah disimpan");
                } catch (IOException e) {
                    System.out.println("Ada error. " + e);
                    Logger.getLogger(HalamanUtama.class.getName()).log(Level.SEVERE, null, e);
                }
                System.out.println(">\"" + kunci + "\" ditemukan");
                break;
            }
        }
        if (baris == JumlahData) {
            JOptionPane.showMessageDialog(null, "\"" + kunci + "\"  tidak ditemukan");
            System.out.println(">\"" + kunci + "\" tidak ditemukan");
            return false;
        } else {
            JOptionPane.showMessageDialog(null, "\"" + kunci + "\"  ditemukan");
            return true;
        }
    }
}
