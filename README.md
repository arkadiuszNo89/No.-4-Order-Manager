# No.-4-Order-Manager

  W aplikacji jako punkt wyjścia tworzymy bazę danych dostawców oraz ich poszczególnych produktów, a dalej - na tej podstawie - możemy kreować zamówienia, które są dynamicznie skalowane na postęp od momentu rozpoczęcia zamówienia do daty i czasu jego zakończenia. Uzyskujemy podgląd do danych zamówień aktywnych lub archiwalnych, pomiędzy którymi punkt przejścia sygnalizowany jest użytkownikowi.

  W górnej części okna aplikacji znajduje się interfejs funkcyjny uszeregowany ikonami w poszczególne działania
    (po wskazaniu kursorem na ikonę zostaje ona podświetlona, a zamiast zegara pojawia się jej opis) :

  - "Aktywne zamówienia" - horyzontalne paski aktywnych zamówień zawierające tytuł, pasek postępu oraz czas pozostały do zakończenia. Segregowane są one w kolejności od tego, którego czas zakończenia najbliższy jest czasowi obecnemu. Po dwukrotnym kliknięciu przyciskiem myszy w nowym oknie wyświetli się ticket z danymi oraz przyciski (w zależności od sytuacji), które pozwalają zarządzać istniejącym zamówieniem: 
      [usuń] (usuwa zamówienie), 
      [Do archiwum] (zakończy aktywne zamówienie przed upływem czasu i przenosi je do archiwum lub przenosi zamówienie, którego koniec jest       sygnalizowany. W archiwum ticket nie posiada już tego przycisku), 
      [Przenieś wszystkie] (przenosi wszystkie zamówienia sygnalizowane końcem upływu czasu, przycisk pojawia się kiedy istnieje więcej niż       jedno sygnalizujące zakończenie zamówienie).
  - "Dostawcy" - lista dostawców. Po wybraniu jednego dane dostawcy pojawiają się w obszarze prawej strony ekranu aplikacji. Widnieją tutaj      również przyciski, które umożliwiają dodawanie nowych dostawców, ich edycję, przejście do ekranu koszyka (tworzenie nowych zamówień)        oraz przycisk "Zamów", który potwierdza utworzone wcześniej zamówienie jako ostatnia bramka sprawdzająca (do tego momentu można je          jeszcze edytować).
  - "Produkty" - znajduje się tutaj lista produktów, która rzutuje listę dostawców, którzy dany produkt posiadają. Każdy dostawca może          posiadać ten sam produkt, ale inną cenę, lead time oraz minimalną ilość do zamówienia. Lista pozwala porównać produkty u różnych            dostawców i wybrać odpowiedni - cel stworzenia zakładki "Produkty".
  - "Arhiwum" - lista zamówień, których zakończenie zostało potwierdzone lub które zostały przeniesione w to miejsce manualnie. Informacja      z czasem pozostałym do końca zamienia się w datę i czas zakończenia. Ticket informacyjny w dalszym ciągu pojawia się po dwukrotnym kliknięciu w dane zamówienie.
  - "Zapisz jako" - zapisuje istniejących dostawców, produkty oraz zamówienia do wybranego przez użytkownika pliku.
  - "Zapisz" - dokonuje tego samego zapisu, ale do pliku, który wcześniej został zarejestrowany poprzez zapisywanie jako lub ładowanie.
  - "Załaduj dane" - ładuje dostawców, produkty oraz zamówienia z wybranego pliku.
  - "Dodaj dane" - dodaje niepowtarzających się dostawców i ich produkty z wybranego pliku bez naruszenia obecnych danych programu.
