/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Scheduler {

    private static PCB DUMMY = new PCB(0,"DUMMY"); //Atrapa - pracuje, kiedy wektor jest pusty
    private static PCB RUNNING = DUMMY; //wykonywany proces
    private static final double alpha = 0.5;
    private static LinkedList<PCB> ReadyPCBs = new LinkedList<PCB>(); //zamiast <?> będzie wstawiona nazwa PCB od Marcina
    //private static int t_last3[]={0,0,0}; //przewidywane czasy wykonania 3 ostatnio wykonywanych procesow
    private static int tn; //rzeczywisty czas wykonywanie poprzedniego procesu
    private static int Tn=0; //przewidziany czas wykonywanie poprzedniego procesu

    public static void addPCB(PCB p) //Ocject reprezentuje tu klase bloku kontrolnego procesu; metoda wstawia PCB w odpowienie miejsce konternera procesow gotowych
    {
        setTau(p);  //obliczenie tau otrzymanego porcesu
        ListIterator<PCB> it = Scheduler.ReadyPCBs.listIterator();
        while (it.hasNext()) //przeszukanie całej listy w celu znalezienia miesca gdzie wstawic otrzymany proces
        {
            if (p.get_Tau()<it.next().get_Tau()) //get_Tau() pobiera Tau zapisane w PCB - obliczone wczesniej
                it.add(p);
        }
        if (it.hasPrevious() && !it.hasNext()) //jesli iterator jest na koncu listy (czyli PCB nie zostalo nigdzie dodane
            it.add(p);

        if (ReadyPCBs.getFirst().getImie()==p.getImie()) //jesli proces zostal dodany na poczatek kolejki - uruchamiamy go
            setPCBtoRunning();
        System.out.println("Dodano proces "+p.getImie()+" do kolejki procesow gotowych"); // dodatkowo trzeba tez wyswietlic jaki to proces (nazwa/id) i gdzie zostal wstawiony
    }

    public static void removePCB(PCB p) //p jest procesem zakonczonym - usuniecie z kolejki procesow gotowych
    {
        if (Scheduler.ReadyPCBs.contains(p))
        {
            Scheduler.ReadyPCBs.remove(p);
            System.out.println("Usunieto proces "+p.getImie()+" z kolejki procesow gotowych"); // dodatkowo trzeba tez wyswietlic jaki to proces (nazwa/id) i gdzie zostal wstawiony
        }
        else
            System.out.println("Nie znaleziono podanego procesu w kolejce procesow gotowych");
    }

    public static void setPCBtoRunning() //uruchamia proces, ktory ma najkrotszy pozostaly czas wykonania
    {
        if (!Scheduler.ReadyPCBs.isEmpty())
        {
            RUNNING=Scheduler.ReadyPCBs.getFirst();
            System.out.println("Uruchomienie procesu "+ReadyPCBs.getFirst().getImie());
        }
        else
        {
            RUNNING= DUMMY;
            System.out.println("Uruchomienie procesu Dummy");
        }
    }
    private static void setTau(PCB p) //pierwszy proces musi się wykonać po to, żeby dalej algorytm miał parametry do obliczania kolejnych
    {
        if (Scheduler.Tn!=0) //Jesli nie jest to piwerwszy wykonywany proces
        {
            p.set_Tau((int)(alpha*tn+(1-alpha)*Tn)); // Tn+1 = alfa*tn + (1-alfa)*Taun
        }
        else //jesli jest to pierwszy proces to przewidujemy ze bedzie sie wykonywal 5 jednostek czsu
        {
            p.set_Tau(5);
            Scheduler.tn=p.get_t(); //zapisujemy jego t pobrane z PCB
            Scheduler.Tn=5; //zapisujemy Tau poprzedniego procesu jako 5
        }
    }
    public static PCB getRunningPCB()
    {
        return Scheduler.RUNNING;
    }
    public static PCB getDummyPCB()
    {
        return Scheduler.DUMMY;
    }
    public static String drawQueue() {
        StringBuilder sb = new StringBuilder();

        for (PCB pcb: Scheduler.ReadyPCBs) {
            sb.append("<  ");
            sb.append(pcb.getImie()); // getImie() zaimplementowane przez Marcina
            sb.append("  ");
        }

        return sb.toString();
    }
}
