package Frame;

public class Mahasiswa {
    String Nama, Jurusan, Asal, Gender;
    long NIM;
    
    public Mahasiswa(String nama, long nim, String jurusan, String asal, String gender) {
        this.Nama = nama;
        this.NIM = nim;
        this.Jurusan = jurusan;
        this.Asal = asal;
        this.Gender = gender;
    }

    public void displayMahasiswa() {
        System.out.print("\tNama: " + Nama);
        System.out.print(", \tNIM: " + NIM);
        System.out.print(", \tJurusan: " + Jurusan);
        System.out.print(", \tAsal: " + Asal);
        System.out.println(", \tGender: " + Gender);
    }

    public long getKey(){
        return NIM;
    }
    
    public String getNama() {
        return Nama;
    }

    public long getNIM() {
        return NIM;
    }

    public String getJurusan() {
        return Jurusan;
    }

    public String getAsal() {
        return Asal;
    }

    public String getGender() {
        return Gender;
    }
}
