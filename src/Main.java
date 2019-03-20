import java.util.Scanner;

/**
 * @author Stanisław Fiuta
 * Date: 3/20/2019
 * Time: 9:18 PM
 */
public class Main {

    public static void main(String[] a) {
        Memory memory = new Memory();
        Scanner scanner = new Scanner(System.in);

        int wybor = 0;
        while(true) {
            System.out.println("0. Zakoncz program");
            System.out.println("1. Wyswietl kolejke fifo");
            System.out.println("2. Znajdz wolna ramke");
            System.out.println("3. Wyswietl pamiec");
            System.out.println("4. Wyswietl tablice ramek");

            wybor = scanner.nextInt();

            switch(wybor) {
                case 0:
                    return;
                case 1:
                    memory.wyswietlKolejke();
                    break;
                case 2:
                    System.out.println("Wolna ramka : " + memory.znajdzWolnaRamke());
                    break;
                case 3:
                    memory.wyswietlPamiec();
                    break;
                case 4:
                    System.out.println("   PID \t NumerStrony \t Czy wolna");
                    memory.tab_ramek.wyswietlTabliceRamek();
                    break;
            }

        }


    }

}
