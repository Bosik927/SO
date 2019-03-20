
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;



public class PCB {



    public enum stany
    {
        NOWY, WYKONYWANY, OCZEKUJACY, GOTOWY, ZAKONCZONY;
    }
    private int PID;
    private String nazwa_procesu;
    private int PPID;
    private stany stan;
    public int licznikRozkazow=0;
    private int rola = 0; //0 - nieznany, 1-rodzic, 2-dziecko
    private int numerProgramu;
    public int wskaznik; //wskaznik na znak czytany z dysku
    private int rozmiarProgramu;

    public int A,B,C,D,E,F; //rejestry
    private List<PCB>dzieci;
    private PCB rodzic;
    public int Tau;
    public int t;

    public Memory.Dane_stronicy[] tab;



    public Random rand = new Random();
    public PCB(int licznik,String nazwa)
    {
        this.PID=licznik;
        this.nazwa_procesu=nazwa;
        this.dzieci = new ArrayList<PCB>();
        this.stan = stany.NOWY;
        if(this.getRodzic()!=null)
            this.PPID=this.getRodzic().getPID();
        else
            this.PPID=0;
        this.Tau = 0;
        this.t = 0;


    }
    public PCB()
    {
        this.PID=100;
        this.nazwa_procesu="ofiara";
        this.stan=stany.NOWY;
    }
    public int get_Tau()
    {
        return this.Tau;

    }
    public int get_t()
    {
        this.t = this.licznikRozkazow;
        return this.t;
    }
    public void set_Tau(int T)
    {
        this.Tau=T;
    }
    public int getRozmiarProgramu()
    {
        return this.rozmiarProgramu;
    }
    public void dodajDziecko(PCB dziecko)
    {
        dzieci.add(dziecko);
    }
    public void setRodzic(PCB rodzic)
    {
        this.rodzic= rodzic;
        this.PPID= rodzic.getPID();
    }
    public void setRozmiarProgramu(int rozmiar)
    {
        this.rozmiarProgramu=rozmiar;
    }
    public void setProgram(Integer program)
    {
        this.numerProgramu=program;
        this.rozmiarProgramu=4;
        this.wskaznik=0;
    }
    //public boolean usunDziecko(PCB dziecko)
    //{
    //    return this.dzieci.remove(dziecko);
    // }
    public String getNazwyDzieci()
    {
        String wynik = "";
        for(PCB pcb:this.dzieci)
        {
            wynik+=pcb.getImie()+ "\n";
            wynik += pcb.getNazwyDzieci();
        }
        return wynik;
    }

    /* public String rysujDrzewo(int poziom)
     {
         String wynik = "";
         for(int i =0;i<poziom+1;i++)
             wynik +='\t';
         wynik+="-" + this.nazwa_procesu + " [" + this.getNazwaStanu() + " ]\n";

         for(PCB pcb:ProcessManager.procesy) // tu byl wskaznik na liste dzieci
         {
             wynik+=pcb.rysujDrzewo(poziom+1);
         }
         return wynik;
     }*/
    public boolean rowne(int PID)
    {
        if(PID==this.PID)return true;
        return false;
    }

    public boolean equals(PCB pcb)
    {
        if(pcb==this) return true;
        return false;
    }
    public stany getStan()
    {
        return this.stan;

    }


    public String getNazwaStanu()
    {
        switch(this.stan)
        {
            case NOWY:
                return "Nowy";
            case WYKONYWANY:
                return "Wykonywany";
            case OCZEKUJACY:
                return "Oczekujacy";
            case GOTOWY:
                return "Gotowy";
            case ZAKONCZONY:
                return "Zakonczony";
            default:
                return "Nieznany";
        }
    }
    public void setStan(stany stan)
    {
        this.stan=stan;
    }

    public int getPID()
    {
        return this.PID;

    }
    public int getPPID() {
        return this.PPID;
    }

    public String getImie() {
        return this.nazwa_procesu;
    }

    public PCB getRodzic() {
        return this.rodzic;
    }

    public LinkedList<PCB> getDzieci() {
        return ProcessManager.procesy;
    }

    public String toString()
    {
        StringBuilder desc = new StringBuilder();
        desc.append("===== PCB =====\n");

        desc.append("Nazwa: ");
        desc.append(nazwa_procesu);

        desc.append("\nPID: ");
        desc.append(PID);

        desc.append("\nStan: ");
        desc.append(getNazwaStanu());

        desc.append("\nRejestry PCB");
        desc.append(
                "\tA: " + A + "\tB: " + B + "\tC: " + C + "\tD: " + D + "\tE: " + E + "\tF: " + F
        );

        desc.append("\n===============");

        return desc.toString();

    }
//    public char readNextFromMemory() //zwraca znak konkretnego miejsac w procesie
//    {
//        //char m = SHELL.memory.pobierzZnak(wskaznik);
//        ++wskaznik;
//        return m;
//    }

    public void spij()
    {
        this.A = Procesor.A;
        this.B = Procesor.B;
        this.C = Procesor.C;
        this.D = Procesor.D;
        this.E = Procesor.E;
        this.F = Procesor.F;
        this.stan = stany.OCZEKUJACY;
    }
    public void budz(){
        Procesor.A = this.A;
        Procesor.B = this.B;
        Procesor.C = this.C;
        Procesor.D = this.D;
        Procesor.E = this.E;
        Procesor.F = this.F;
        this.stan = stany.GOTOWY;
    }
    public void zakoncz()
    {
        this.stan = stany.ZAKONCZONY;
        Scheduler.removePCB(this);
        boolean czyOdblokowane = false;
        if(rodzic!=null)
        {
            for(PCB rodzenstwo:rodzic.getDzieci())
            {
                if(rodzenstwo.getStan()!=stan.GOTOWY && rodzenstwo.getStan()!=stany.OCZEKUJACY)
                {
                    czyOdblokowane = true;
                }
            }
        }
        if(czyOdblokowane)
            ProcessManager.obudzProces(rodzic.getImie());

    }

    public void fork(String kod, String nazwa) throws IOException
    {

        if(ProcessManager.nazwy.contains(nazwa))
        {
            System.out.println("Taki proces juz istnieje, nie mozna stworzyc procesu");
        }
        else
        {
            File plik = new File(kod+".txt");
            if(plik.exists())
            {
                nazwa_procesu=nazwa;
                this.rola=1;
                ProcessManager.stworzProces(nazwa_procesu,numerProgramu,this );
                //SHELL.memory.Zapisz_program(kod, nazwa_procesu);
//                ProcessManager.zatrzymajProces(nazwa_procesu); TODO zakomentarzowalem na razie, nie wiem czy to potrzebne czy nie

                PCB noweDziecko = getProces(nazwa_procesu); // TODO tu dodalem ProcessManager.

                noweDziecko.A = this.A;
                noweDziecko.B = this.B;
                noweDziecko.C = this.C;
                noweDziecko.D = this.D;
                noweDziecko.E = this.E;
                noweDziecko.F = this.F;


                noweDziecko.rola = 2;
                noweDziecko.numerProgramu=this.numerProgramu;
                noweDziecko.wskaznik=this.wskaznik;
                noweDziecko.rozmiarProgramu= this.rozmiarProgramu;


                System.out.println("Wyswietlanie listy dzieci");
                System.out.println(ProcessManager.procesy);
            }
            else

                System.out.println("Nie ma takiego pliku, sprobuj ponownie");
        }
    }



    /*public PCB znajdz(String nazwa)
    {
        if(nazwa_procesu.equals(nazwa))
            return this;
        for(PCB dziecko : dzieci)
        {
            PCB wynik = dziecko.znajdz(nazwa);

            if(wynik!=null)
                return wynik;
        }
        return null;
    }*/
    public PCB getProces(int pid)
    {
        if(PID == pid)
            return this;

        for(PCB dziecko: dzieci)
        {
            PCB wynik = dziecko.getProces(pid);
            if(wynik!=null)
                return wynik;
        }
        return null;

    }
    public PCB getProces(String nazwa)
    {
        if(nazwa_procesu.equals(nazwa) )
            return this;
        if(dzieci==null)
        {
            System.out.println("PCB getProces - nie ma");
        }
        else
        {

            System.out.println("Jakis proces jest i szukamy go");
            for(PCB dziecko: dzieci)
            {
                PCB wynik = dziecko.getProces(nazwa);
                if(wynik!=null)
                    return wynik;
            }
        }
        return null;

    }
    public void skocz(int gdzie)
    {
        wskaznik = gdzie;
    }

}