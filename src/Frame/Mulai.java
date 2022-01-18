package Frame;

public class Mulai {

    public static void main(String[] args) {
        // Ini adalah kode untuk memunculkan splash screen dengan memanggil kelas Loading
        Loading login = new Loading();
        HalamanUtama halamanutama = new HalamanUtama();
        login.setVisible(true);
        try {
            for (int i = 0; i <= 100; i++) {
                Thread.sleep(10);
                login.LabelPersen.setText(Integer.toString(i) + "%");
                login.progressBar.setValue(i);
                if (i == 100) { //ketika i = 100 maka kelas HalamanUtama dipanggil
                    halamanutama.setVisible(true);
                    login.dispose();
                }
            }
        } 
        catch (InterruptedException e) {
        }
    }
}
