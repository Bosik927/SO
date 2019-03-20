import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;



public class Memory {

    private char[] RAM;
    private File plik_wymiany;
    public int plik_k = 0; //koniec pliku wymiany
    // public Queue<Integer> kolejka; // FIFO - kolejka
    public LinkedList<Integer> kolejka; // FIFO - kolejka
    public Ramki tab_ramek;
    public doWymiany dane;
    public int s = 0;
    private static PCB p1;
    private PCB p2;

    /////////////////////////////////////////////////////////////////////////////////////////
    //KONSTRUKTOR + POCZATKOWA INICJALIZACJA PAMIECI
    public Memory()
    {
        RAM = new char[128];
        for (int i = 0; i < RAM.length; i++)
            RAM[i] = ' ';
        plik_wymiany = new File("plik_wymiany.txt");
        if (!plik_wymiany.exists())
            try {
                plik_wymiany.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        czyscPlik(); //czyszczenie pliku wymiany
        kolejka = new LinkedList<>();
        tab_ramek = new Ramki();
        dane = new doWymiany();
        p1 = new PCB();
    }
    public class Ramki
    {
        public class Ramka {
            public int PID = -1;
            public int nrStrony = -1;
            public boolean czyWolna = true;
        }
        public Ramka[] tab_ramek = new Ramka[8];

        public Ramki()
        {
            for (int i = 0; i <8; i++)
            {
                tab_ramek[i] = new Ramka();
            }
        }

        public int sprawdzRamke(int i) {
            if (tab_ramek[i].czyWolna)
                return -1;
            else
                return tab_ramek[i].PID;
        }

        public void zwolnijRamke(int i) {
            tab_ramek[i].czyWolna = true;
            tab_ramek[i].PID = -1;
            tab_ramek[i].nrStrony = -1;
        }

        public void zajmijRamke(int i, int ID, int nrStrony) {
            if (tab_ramek[i].czyWolna) {
                tab_ramek[i].czyWolna = false;
                tab_ramek[i].PID = ID;
                tab_ramek[i].nrStrony = nrStrony;
            } else
                System.out.println("RAMKA ZAJETA");
        }

        public void zwolnijZasoby(int id) {
            for (int i = 0; i < 8; i++) {
                if (tab_ramek[i].PID == id) {
                    tab_ramek[i].PID = -1;
                    tab_ramek[i].nrStrony = -1;
                    tab_ramek[i].czyWolna = true;
                    usun(i);

                }
            }

        }

        public void wyswietlTabliceRamek() { // wyswietla status wolnych/zajetych ramek
            for (int i = 0; i < 8; i++) {
                System.out.println(i+"."+tab_ramek[i].PID + "         " + tab_ramek[i].nrStrony + "           " + tab_ramek[i].czyWolna + "\n");
            }
        }


    }

    public class doWymiany { //klasa pomocnicza przechowujaca dane o miejscach procesow w pliku wymiany
        public class Pola{
            public int PID; //id procesu
            public int nr = 0; //miejsce w pliku wymiany
        }
        public Pola[] dane = new Pola[30];

        public doWymiany()
        {
            for (int i = 0; i <30; i++)
            {
                dane[i] = new Pola();
            }
        }

        public int zwrocNr(int id)
        {
            for (int i = 0; i < 30; i++)
            {
                if (dane[i].PID == id)
                    return dane[i].nr;
            }
            return 0;
        }
    }

    public class Dane_stronicy {
        public int nrRamki = -1;
        public boolean bit = false; //bit poprawnosci
    }
    public Dane_stronicy[] Tablica_stronic;

    public int Sprawdz_stronice(int i) { // funkcja zwracajca nr ramki lub -1 w przypadku jej braku
        if (Tablica_stronic[i].bit) {
            return Tablica_stronic[i].nrRamki;
        } else {
            return -1;
        }
    }

    public int Sprawdz_stronice(Dane_stronicy[] tablica, int i) { // funkcja zwracajca nr ramki lub -1 w przypadku jej braku
        if (tablica[i].bit) {
            return tablica[i].nrRamki;
        } else {
            return -1;
        }
    }

    public void bitNa0(int nr) { //'zwalnia' konkretna ramke z tablicy stronic

        for (int i = 0; i < Tablica_stronic.length; i++) {
            if (Tablica_stronic[i].nrRamki == nr) {
                Tablica_stronic[i].bit = false;
                Tablica_stronic[i].nrRamki = -1;
            }
        }

    }

    public void bitNa0(int nrRamki, PCB proces) { //'zwalnia' konkretna ramke z tablicy stronic
        for (int i = 0; i < proces.tab.length; i++) {
            if (proces.tab[i].nrRamki == nrRamki) {
                proces.tab[i].bit = false;
                proces.tab[i].nrRamki = -1;
            }
        }
    }

    public void bitNa1(int nrStronicy, int nrRamki) {
        if (!Tablica_stronic[nrStronicy].bit) {
            Tablica_stronic[nrStronicy].bit = true;
            Tablica_stronic[nrStronicy].nrRamki = nrRamki;
        }
    }

    public void bitNa1(int nrStronicy, int nrRamki, PCB proces) {

        if (!proces.tab[nrStronicy].bit) {
            proces.tab[nrStronicy].bit = true;
            proces.tab[nrStronicy].nrRamki = nrRamki;
        }
    }

    public Dane_stronicy[] Stworz_tablice(int rozmiar)
    {
        Tablica_stronic = new Dane_stronicy[(rozmiar + 15)/16];
        for (int i = 0; i < Tablica_stronic.length; i++) {
            Tablica_stronic[i] = new Dane_stronicy();
        }
        return Tablica_stronic;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void czyscPlik() { // zeruje plik wymiany i umieszcza kod bezczynnosci
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(plik_wymiany));
            out.write("JP 0            "); // tutaj umiescic kod bezczynnosci
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean czyRAMpelny() { // sprawdza czy wszystkie ramki sa zajete
        for (int i = 0; i < 8; i++) {
            if (tab_ramek.sprawdzRamke(i) == -1) {
                return false;
            }
        }
        return true;
    }

    private int wybierzOfiare() { // zwraca numer ofiary FIFO
        return kolejka.getFirst();

    }

    private void zaktualizujKolejke(int nr) { // aktualizuje kolejke fifo podanym numerem ramki
        if (kolejka.size() < 8)
            kolejka.add(nr);
        else {
            kolejka.remove();
            kolejka.add(nr);
        }
    }

    public void usun(int nr)
    {
        for (int i = 0; i < kolejka.size(); i++)
        {
            if (kolejka.get(i) == nr)
                kolejka.remove(i);
        }
    }


    public int znajdzWolnaRamke() { // zwraca numer wolnej ramki
        for (int i = 0; i < 8; i++) {
            if (tab_ramek.sprawdzRamke(i) == -1)
                return i;
        }
        return -1;
    }

    public String czytaj(int adres) { // zwraca string do spacji
        PCB proces = Scheduler.getRunningPCB();
        String dane = new String();
        for (int i = 0; adres + i < proces.getRozmiarProgramu(); i++) {
            char temp = pobierzZnak(adres + i);
            if (temp == ' ')
                break;
            dane += temp;
        }

        return dane;
    }

    public char pobierzZnak(int adres) //pobiera znak z konkretnego miejsca w procesie
    {
        PCB proces = Scheduler.getRunningPCB();

        if (adres > pobierzRozmiarProgramu(proces)) {
            System.out.println("ADRES LOGICZNY WIEKSZY NIZ ROZMIAR PROGRAMU");
            return ' ';
        }

        int strona = adres / 16;
        int ramka = Sprawdz_stronice(proces.tab,strona);

        if (ramka != -1) {
            return RAM[ramka * 16 + adres % 16];
        }

        if (czyRAMpelny())
        {
            int ofiara = wybierzOfiare(); //nr ramki
            int idOfiary = tab_ramek.tab_ramek[ofiara].PID; //nr ID Ofiary

            p1 = ProcessManager.getProces(idOfiary); //przypisanie procesu ofiary
            bitNa0(ofiara, p1); // zamiana w tablicy stronic ofiary
            tab_ramek.zwolnijRamke(ofiara);
        }
        int wolna = znajdzWolnaRamke();
        String strona2 = pobierzStrone(strona, proces);
        for (int i = wolna * 16, j = 0; j < 16 && j < strona2.length(); i++, j++) {
            RAM[i] = strona2.charAt(j);
        }
        bitNa1(strona, wolna, proces);
        tab_ramek.zajmijRamke(wolna, proces.getPID(),strona);
        zaktualizujKolejke(wolna);

        int d = wolna * 16 + adres % 16;
        return RAM[d];

    }

    public void Zapisz_do_pamieci(int adres, char znak) {
        PCB proces = Scheduler.getRunningPCB(); //pobranie aktualnie dzialajacego procesu
        if (adres > pobierzRozmiarProgramu(proces)) {
            System.out.println("ADRES LOGICZNY WIEKSZY NIZ ROZMIAR PROGRAMU");
            return;
        }

        int strona = adres / 16;
        int ramka = Sprawdz_stronice(proces.tab,strona);

        if (ramka != -1) { //gdy ramka jest przypisana, zapisujemy do RAMu
            RAM[ramka * 16 + adres % 16] = znak;
            return;
        }

        if (czyRAMpelny())
        {
            int ofiara = wybierzOfiare(); //nr ramki w RAM
            int idOfiary = tab_ramek.tab_ramek[ofiara].PID; //nr ID Ofiary

            p1 = ProcessManager.getProces(idOfiary);
            //p1.getProces(idOfiary); //przypisanie procesu ofiary
            bitNa0(ofiara, p1); // zamiana w tablicy stronic ofiary
            tab_ramek.zwolnijRamke(ofiara);

        }

        int wolna = znajdzWolnaRamke(); // nowy nr ramki

        String strona2 = pobierzStrone(strona, proces); //strona do wrzucenia do pamieci
        for (int i = wolna * 16, j = 0; j < 16; i++, j++) {
            RAM[i] = strona2.charAt(j);
        }

        bitNa1(strona, wolna, proces); //tablica stronic - strona w pamieci

        tab_ramek.zajmijRamke(wolna, proces.getPID(),strona);
        RAM[ramka * 16 + adres % 16] = znak;
        zaktualizujKolejke(wolna);
    }

    public void Zapisz_program(String nazwa, String nazwa_procesu) throws IOException //zapisanie programu do pliku wymiany
    {
        File plik = new File(nazwa+".txt");
        String program = new String();
        String textLine = new String();


        try {
            FileReader fileReader = new FileReader(plik);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            do {
                textLine = bufferedReader.readLine();
                program += " ";
                program += textLine;
            } while(textLine != null);
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Nie odnaleziono pliku");
        }
        program = program.substring(1,program.length() - 5);

        PCB proces = ProcessManager.getProces(nazwa_procesu);
        // okreslenie miejsca w pliku wymiany
        dane.dane[s].PID = proces.getPID();
        dane.dane[s].nr = plik_k;
        s++;

        int rozmiar = program.length();
        proces.tab = Stworz_tablice(rozmiar);
        proces.setRozmiarProgramu(rozmiar);

        String pom = new String();
        pom += program;
        if (program.length()%16 != 0) //dopelnienie strony do 16 znakow
        {
            int k = program.length()%16;
            for (int i = 0; i < (16 - k); i++)
            {
                pom += " ";
            }
        }
        plik_k += pom.length();


        if (proces.getPID() == 1)
        {
            try{
                FileWriter fw = new FileWriter(plik_wymiany);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(pom);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try{
                FileWriter fw = new FileWriter(plik_wymiany,true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(pom);
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int pobierzRozmiarProgramu(PCB proces) { // zwraca rozmiar programu o podanym PCB
        return proces.getRozmiarProgramu();
    }

    /////////////////////////////////WIRTUALNA///////////////////////////////////////////////

    private String pobierzStrone(int nrStrony, PCB proces) { // zwraca strone o danym numerze z pliku wymiany

        int poczatek = 0;
        for (int i = 0; i < 30; i++)
        {
            if (dane.dane[i].PID == proces.getPID())
                poczatek = dane.dane[i].nr;
        }

        String dane2 = new String(); //dane z pliku wymiany
        try {
            Scanner scan = new Scanner(plik_wymiany); //zapisanie pliku wymiany do stringa
            dane2 = scan.nextLine();
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String dane = new String();

        dane = dane2.substring(poczatek + nrStrony*16 , poczatek + nrStrony*16 + 16);
        return dane;
    }

    /////////////////////////////////////////////////WYSWIETLANIE//////////////////////////////////////////////////////////////////
    public void wyswietlKolejke() { // wyswietla kolejke FIFO
        for (int i = 0; i < kolejka.size(); i++)
            System.out.println(kolejka.get(i));
    }

    public void wyswietlPamiec() { // wyswietla zawartosc pamieci
        for (int i = 0; i < 8; i++) {
            for (int j = 16 * i + 0; j < 16 * i + 16; j++) {
                System.out.print(j + "\t");
            }
            System.out.print("\n");

            for (int j = 16 * i + 0; j < 16 * i + 16; j++) {
                System.out.print(RAM[j] + "\t");
            }
            System.out.print("\n\n");

        }
    }

    public void WyswietlTabliceStronic(String nazwa)
    {
        p2 = p1;
        p2 = ProcessManager.getProces(nazwa);
        if (p2 != null) {
            if (p2.tab != null) {
                for (int i = 0; i < p2.tab.length; i++)
                {
                    System.out.println(i + "         " + p2.tab[i].nrRamki + "           " + p2.tab[i].bit + "\n");
                }
            }
        }
        else
            System.out.println("Proces o podanej nazwie nie istnieje");

    }

}