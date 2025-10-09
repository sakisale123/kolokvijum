Kolokvijum 1
1. Kreirati novi Android projekat pod nazivom Kolokvijum1.
2. Prva aktivnost (MainActivity) sadrži dva fragmenta. Prvi fragment (FirstFragment)
se nalazi u gornjoj polovini aktivnosti, dok se drugi (SecondFragment) nalazi u
donjoj polovini aktivnosti i ima pozadinu zelene boje. (1.5)
3. FirstFragment ima Checkbox i Switch (1).
4. SecondFragment ima jedno EditText polje i jedno dugme: “Sačuvaj” (1).
5. Kreirati dva kanala za notifikacije (2).
6. Čekiranjem Checkbox-a pokreće se servis koji na svakih 10 sekundi proverava
da li je omogućen Wi-Fi (4). Ukoliko se odčekira, servis se zaustavlja (1).
7. Kreirati BroadcastReciever koji će osluškivati proveru i ukoliko je Wi-Fi uključen,
putem prvog kanala poslati notifikaciju sa porukom: “Sve je u redu!” (3). Ukoliko
nije, poslaće notifikaciju sa porukom: “Uključi Wi-Fi!” i akcionim dugmetom čijim
klikom se otvaraju podešavanja za Wi-Fi (3).
8. Kreirati model i tabelu u bazi za entitet Provera sa jednim poljem: vrednost (0.5).
9. Klikom na Switch u bazi se čuva entitet sa vrednošću “true” ako je Wi-Fi
omogućen ili vrednošću “false”, ako nije (1).
10.Klikom na dugme “Sačuvaj” preuzeti tekst iz EditText polja i poslati notifikaciju
putem drugog kanala sa tekstom iz EditText polja (2).
