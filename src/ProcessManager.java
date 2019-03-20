

import java.util.LinkedList;


public class ProcessManager {

    private static int licznik  = 1;
    public static PCB INIT = new PCB(0,"INIT"); // Zmienilem na public dla Shella
    private static PCB ODPALONY = null;

    public static LinkedList<PCB> procesy = new LinkedList<>();
    public static LinkedList<String> nazwy = new LinkedList<>();
    public static LinkedList<PCB> proces = new LinkedList<>();


    public Memory.Dane_stronicy[] tab;
    public ProcessManager()
    {
        INIT.setStan(PCB.stany.GOTOWY);

    }
    public static void usunProces(String nazwa)
    {
        if(nazwy.contains(nazwa))
        {
            PCB pcb = getProces(nazwa);
            usunProces(pcb);
        }
        else
        {
            System.out.println("Brak takiego procesu");
        }
    }
    public static void zwrocStan()
    {
        if(procesy!=null)
        {
            for(PCB p: procesy)
                System.out.println("Stan procesu o nazwie: "+ p.getImie() + " ma stan:" + p.getNazwaStanu());
        }
    }
    //przenoszenie dzieci do INITA

    private static void usunProces(PCB pcb)
    {


        if(!pcb.getDzieci().isEmpty()&& pcb.getStan()==PCB.stany.WYKONYWANY)
        {
            System.out.println("Usuwanie");
            for(PCB dziecko : pcb.getDzieci())
            {
                INIT.dodajDziecko(pcb);
                dziecko.setRodzic(INIT);
            }
            procesy.remove(pcb);
            nazwy.remove(pcb.getImie());
            Scheduler.removePCB(pcb);
        }
        else if (!pcb.getDzieci().isEmpty())
        {
            System.out.println("Usuwanie");
            for(PCB dziecko : pcb.getDzieci())
            {
                INIT.dodajDziecko(pcb);
                dziecko.setRodzic(INIT);
            }
            procesy.remove(pcb);
            nazwy.remove(pcb.getImie());

        }
        else
        {
            System.out.println("Brak takiego procesu");
        }

    }
    private static String wyswietlanie()
    {
        String wynik = "Nazwa procesu: " + "INIT" + " stan procesu: " + "Gotowy ";

        return wynik;
    }
    private static int getIloscDzieci(PCB node)
    {
        int n = node.getDzieci().size();
        for(PCB dziecko : node.getDzieci())
            n+=getIloscDzieci(dziecko);
        return n;
    }
    //sprawdzenie czy prcoes istnieje w danym nodzie
    private static boolean istnieje(PCB node, PCB keyNode)
    {
        boolean wynik = false;
        if(node.equals(keyNode))
            return true;
        else
        {
            for(PCB dziecko: node.getDzieci())
            {
                if(istnieje(dziecko,keyNode))
                    wynik = true;
            }
        }
        return wynik;
    }

    private static boolean istnieje(PCB keyNode)
    {
        return istnieje(INIT,keyNode);

    }
    public static boolean istnieje(String nazwa_procesu)
    {
        for(PCB dziecko: INIT.getDzieci())
            if(dziecko.getImie().equals(nazwa_procesu))
                return true;
        return false;
    }
    public static void setStan(PCB.stany stan, PCB pcb)
    {
        PCB pc = znajdz(INIT,pcb);
        pc.setStan(stan);
    }

    public static PCB znajdz(String nazwa)
    {
        return INIT.getProces(nazwa);
    }

    private static PCB znajdz(PCB keyNode)
    {
        return znajdz(INIT,keyNode);
    }
    private static PCB znajdz(PCB node, PCB keyNode)
    {
        if(node==null)
            return null;
        if(node.equals(keyNode))
            return node;
        else
        {
            PCB cnode = null;
            for(PCB dziecko: node.getDzieci())
                if((cnode=znajdz(dziecko,keyNode))!=null)
                    return cnode;
        }
        return null;
    }

    private static  void dodajProces(PCB pcb, PCB rodzic)
    {
        rodzic.dodajDziecko(pcb);
        pcb.setRodzic(rodzic);
        ++licznik;
        System.out.println("ID procesu to: " + pcb.getPID());
        procesy.add(pcb);
        nazwy.add(pcb.getImie());
    }
    public static String getNazwyDzieci(String nazwa)
    {
        PCB pcb = znajdz(nazwa);
        if(pcb!=null)
            return pcb.getNazwyDzieci();
        else
            return "Nie mozna znalezc procesu rodzica";
    }
    public static void rysujDrzewo(String nazwa)
    {
        System.out.println("Wyswietlanie drzewa");
        if(nazwa.equals("INIT"))
        {
            System.out.println(wyswietlanie());
            {
                if(procesy!=null)
                {
                    for(PCB p: procesy)
                        System.out.println("Nazwa procesu: " + p.getImie() + " stan procesu: " + p.getNazwaStanu() );
                }
            }
        }
        else
        {
            PCB pcb = getProces(nazwa);
            if(pcb!=null)
            {

                for(PCB p: procesy)
                {
                    if(p.getPID()>=pcb.getPID())
                    {
                        System.out.println("Nazwa procesu: " + p.getImie() + " stan procesu: " + p.getNazwaStanu() );
                    }


                }
            }
            else
                System.out.println("Nie mozna znalezc procesu rodzica");
        }
    }
    public static String stworzProces(String nazwa, Integer program, PCB rodzic)
    {
        PCB proces = new PCB(licznik,nazwa);

        proces.setProgram(program);

        dodajProces(proces, rodzic);

        proces.setStan(PCB.stany.GOTOWY);
        Scheduler.addPCB(proces);

        return nazwa;
    }
    public static String stworzProces(String nazwa, Integer program)
    {
        return stworzProces(nazwa, program, INIT);
    }
    public static String stworzProces(Integer program)
    {
        return stworzProces("PROGRAM *" + licznik,program,INIT);
    }
    public static PCB getODPALONY()
    {
        return ODPALONY;
    }

    public static void stopODPALONY()
    {
        PCB odpalonePCB = Scheduler.getRunningPCB();

        Scheduler.removePCB(odpalonePCB);
        odpalonePCB.spij();

    }
    public static boolean zatrzymajProces(String nazwa)
    {
//        PCB pcb = znajdz(nazwa);
        PCB pcb = getProces(nazwa);
        if(pcb!=null)
        {

            Scheduler.removePCB(pcb);
            pcb.spij();
            return true;
        }
        return false;
    }
    public static boolean obudzProces(String nazwa)
    {
        PCB pcb = znajdz(nazwa);
        if(pcb!=null && pcb.getStan()==PCB.stany.OCZEKUJACY)
        {
            pcb.budz();
            Scheduler.addPCB(pcb);
            return true;
        }
        return false;

    }
    public static boolean uruchomProces()
    {
        PCB pcb = Scheduler.getRunningPCB();
        if(pcb!=null)
        {
            ODPALONY=pcb;
            pcb.setStan(PCB.stany.WYKONYWANY);
            return true;
        }
        else
            return false;
    }
    public static void zakonczProces()
    {
        ODPALONY.zakoncz();
        usunProces(ODPALONY);
        ODPALONY=null;
    }
    public static PCB getProces(String nazwa)
    {
        for(PCB p:procesy)
        {
            if(p.getImie().equals(nazwa))
                return p;
        }
        return null;
    }

    public static PCB getProces(int id)
    {
        for(PCB p:procesy)
        {
            if(p.getPID()==id)
            {
                return p;
            }

        }
        return null;
    }
}