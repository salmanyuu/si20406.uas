package android.example.uas.mypaket;

public class MyData {

    String kunci;
    String isi;

    public MyData(){

    }


    public MyData(String kunci, String isi) {
        this.kunci = kunci;
        this.isi = isi;
    }

    public String getKunci() {
        return kunci;
    }

    public void setKunci(String kunci) {
        this.kunci = kunci;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }
}
