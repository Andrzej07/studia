#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <stdbool.h>
#include <signal.h>
#include <stdlib.h>

#define MSGLEN 60

enum Typ_jednostki {ROBO,JAZDA,LEKKA,CIEZKA};

typedef struct jednostka
{
  int cena;
  double atak;
  double obrona;
  int czas;
} jednostka;

typedef struct Message
{
  long mtyp;
  char mtext[MSGLEN];
} Message;

typedef struct	StanGracza
{
  int lLekkich,lCiezkich,lJazdy,lRobo;
  int lSurowcow;
  char kolejka[100];
  int it_wstawianie, it_przetwarzanie;
  int que_len;
  int punkty;
  bool atakuje;
} StanGracza;

void moja(){}

enum Typ_jednostki daj_typ(char znak)
{
  switch(znak)
  {
  case 'l':
    return LEKKA;
  case 'c':
    return CIEZKA;
  case 'r':
    return ROBO;
  case 'j':
    return JAZDA;
  }
}

void powiadom_wojsko(int klucz, int klient, StanGracza* Gracz)
{
  Message msg;
  msg.mtyp = klient+2;
  sprintf(msg.mtext,"xr%dl%dc%dj%d",(*Gracz).lRobo,(*Gracz).lLekkich,(*Gracz).lCiezkich,(*Gracz).lJazdy);
  msgsnd(klucz,&msg,MSGLEN,0);
}

void powiadom_sur(int klucz, int klient, StanGracza* Gracz)
{
  Message msg;
  msg.mtyp = klient+2;
  sprintf(msg.mtext,"xs%d",(*Gracz).lSurowcow);
  msgsnd(klucz,&msg,MSGLEN,0);
}

void wiadomosc(int klucz, int klient, char* zawartosc)
{
  Message msg;
  msg.mtyp = klient+2;
  strcpy(msg.mtext,zawartosc);
  msgsnd(klucz,&msg,MSGLEN,0);
}

bool wojna(Message msg,int semid,int klucz_kolejki,jednostka *tab_jedn,StanGracza* Gracz1, StanGracza* Gracz2, int atakujacy)
// GRACZ1 TO STRONA ATAKUJACA  atakujacy to identyfikator do wysylania wiadomosci
{
    (*Gracz1).atakuje=true;
    struct sembuf p1,p2,v1,v2;
    p1.sem_num=0; p1.sem_flg=0; p1.sem_op=-1; //opuszcza dla Gracz1
    p2.sem_num=1; p2.sem_flg=0; p2.sem_op=-1; //opuszcza dla Gracz2
    v1.sem_num=0; v1.sem_flg=0; v1.sem_op=1; //podnosci dla Gracz1
    v2.sem_num=1; v2.sem_flg=0; v2.sem_op=1; //podnosci dla Gracz2
    printf("Atak ma dane %s\n",msg.mtext);
    int broniacy;
    if(atakujacy==1) broniacy=2;
    else if(atakujacy==2) broniacy=1;
    int a = 4, pozyczam_j=0,pozyczam_l=0,pozyczam_c=0;
    for(a; msg.mtext[a]!='\0';a++)
    {
      if(msg.mtext[a]=='j')
      {
	pozyczam_j=atoi(msg.mtext+a+1);
      }
      else if(msg.mtext[a]=='l')
      {
	pozyczam_l=atoi(msg.mtext+a+1);
      }
      else if(msg.mtext[a]=='c')
      {
	pozyczam_c=atoi(msg.mtext+a+1);
      }
    }

    semop(semid,&p1,1);
    if((*Gracz1).lLekkich >= pozyczam_l) {(*Gracz1).lLekkich -= pozyczam_l;}
    else {pozyczam_l=(*Gracz1).lLekkich; (*Gracz1).lLekkich = 0;}
    if((*Gracz1).lCiezkich >= pozyczam_c) {(*Gracz1).lCiezkich -= pozyczam_c;}
    else {pozyczam_c=(*Gracz1).lCiezkich; (*Gracz1).lCiezkich = 0;}
    if((*Gracz1).lJazdy >= pozyczam_j) {(*Gracz1).lJazdy -= pozyczam_j;}
    else {pozyczam_j=(*Gracz1).lJazdy; (*Gracz1).lJazdy = 0;}
    semop(semid,&v1,1);
    powiadom_wojsko(klucz_kolejki,atakujacy,Gracz1);
    printf("Wysylam do ataku %d %d %d\n",pozyczam_l,pozyczam_c,pozyczam_j);
    wiadomosc(klucz_kolejki,atakujacy,"Twe wojska wyruszyly.");
    sleep(5);
    wiadomosc(klucz_kolejki,broniacy,"Zostales zaatakowany!");
    semop(semid,&p1,1); semop(semid,&p2,1);
    double sila_ataku1 = (pozyczam_l*tab_jedn[2].atak)+(pozyczam_j*tab_jedn[1].atak)+ (pozyczam_c*tab_jedn[3].atak);
    double sila_obrony1 = (pozyczam_l*tab_jedn[2].obrona)+ (pozyczam_j*tab_jedn[1].obrona)+ (pozyczam_c*tab_jedn[3].obrona);
    double sila_ataku2 = ((*Gracz2).lLekkich*tab_jedn[2].atak)+ ((*Gracz2).lCiezkich*tab_jedn[3].atak)+((*Gracz2).lJazdy*tab_jedn[1].atak);
    double sila_obrony2 = ((*Gracz2).lLekkich*tab_jedn[2].obrona)+ ((*Gracz2).lCiezkich*tab_jedn[3].obrona)+((*Gracz2).lJazdy*tab_jedn[1].obrona);
    if(sila_ataku1>=sila_obrony2 && sila_ataku1!=0)
    {
      printf("Obronca stracil wszystko\n");
      (*Gracz1).punkty++;
      char wiad[25];
      sprintf(wiad,"Zdobyles punkt! Masz %d.",(*Gracz1).punkty);
      wiadomosc(klucz_kolejki,broniacy,"Twoja obrona zostala zlamana!");
      wiadomosc(klucz_kolejki,atakujacy,wiad);
      (*Gracz2).lJazdy = 0;
      (*Gracz2).lCiezkich = 0;
      (*Gracz2).lLekkich = 0;
    }
    else if(sila_obrony2!=0)
    {
      wiadomosc(klucz_kolejki,atakujacy,"Twoj atak sie nie powiodl.");
      wiadomosc(klucz_kolejki,broniacy,"Odparles wroga!");
      (*Gracz2).lJazdy = (*Gracz2).lJazdy -(int)( (sila_ataku1/sila_obrony2) * (*Gracz2).lJazdy );
      (*Gracz2).lCiezkich = (*Gracz2).lCiezkich - (int)( (sila_ataku1/sila_obrony2) *(*Gracz2).lCiezkich );
      (*Gracz2).lLekkich = (*Gracz2).lLekkich -(int)( (sila_ataku1/sila_obrony2) *(*Gracz2).lLekkich );
//	  printf("Obronca ma teraz %d %d %d\n", (*Gracz2).lLekkich, (*Gracz2).lCiezkich,(*Gracz2).lJazdy);
    }
    if(sila_ataku2>=sila_obrony1 && sila_ataku2!=0)
    {
      printf("Atakujacy stracil wszystko\n");
      pozyczam_l = 0; pozyczam_c = 0; pozyczam_j = 0;
    }
    else if(sila_obrony1!=0)
    {
/*	  printf("Atakujacy stracil %d %d %d\n", (int)((sila_ataku1/sila_obrony2) *pozyczam_l),
      (int)((sila_ataku1/sila_obrony2) *pozyczam_c), (int) ((sila_ataku1/sila_obrony2)*pozyczam_j)); */
      pozyczam_l =  pozyczam_l - (int)((sila_ataku2/sila_obrony1) * pozyczam_l);
      pozyczam_c = pozyczam_c - (int)((sila_ataku2/sila_obrony1)*pozyczam_c);
      pozyczam_j = pozyczam_j - (int)((sila_ataku2/sila_obrony1)*pozyczam_j);
    }
    (*Gracz1).lLekkich += pozyczam_l;
    (*Gracz1).lCiezkich += pozyczam_c;
    (*Gracz1).lJazdy += pozyczam_j;
    char powrocilo[30];
    sprintf(powrocilo,"Powrocilo l%d c%d j%d.",pozyczam_l,pozyczam_c,pozyczam_j);
    wiadomosc(klucz_kolejki,atakujacy,powrocilo);
    semop(semid,&v1,1); semop(semid,&v2,1);
    powiadom_wojsko(klucz_kolejki,atakujacy,Gracz1);
    powiadom_wojsko(klucz_kolejki,broniacy,Gracz2);
    printf("Liczba punktow %d\n",(*Gracz1).punkty);
    if((*Gracz1).punkty == 5)
    {
      wiadomosc(klucz_kolejki,atakujacy,"wygrales");
      wiadomosc(klucz_kolejki,broniacy,"przegrales");
      return true;
    }
    (*Gracz1).atakuje=false;
    return false;
}

int main()
{
    //inicjalizacja ponumerowanie graczy
    int klucz_kolejki = msgget(100, IPC_CREAT|0666);
    if(klucz_kolejki==-1)
    {
        perror("Serwer nie podlaczyl sie do kolejki.\n");
        return 1;
    }
    Message msgx;
    msgx.mtyp=5;
    strcpy(msgx.mtext,"1");
    msgsnd(klucz_kolejki,&msgx,MSGLEN,0);
    msgx.mtext[0]='2';
    msgsnd(klucz_kolejki,&msgx,MSGLEN,0);
    msgrcv(klucz_kolejki,&msgx,MSGLEN,6,0);
    msgrcv(klucz_kolejki,&msgx,MSGLEN,6,0);

    //tworzy strukture informacji o graczach
    int shmid = shmget(100, sizeof(StanGracza),IPC_CREAT | 0666);
    StanGracza *Gracz1 = shmat(shmid, NULL,0);
    (*Gracz1).lLekkich = 0; (*Gracz1).lCiezkich = 0; (*Gracz1).lJazdy = 0; (*Gracz1).lRobo = 0; (*Gracz1).lSurowcow = 300; (*Gracz1).it_wstawianie=0;
    (*Gracz1).it_przetwarzanie=0; (*Gracz1).que_len = 0;
    (*Gracz1).punkty = 0; (*Gracz1).atakuje=false;
    int shmid2 = shmget(102, sizeof(StanGracza),IPC_CREAT | 0666);
    StanGracza *Gracz2 = shmat(shmid2,NULL,0);
    (*Gracz2).lLekkich = 0; (*Gracz2).lCiezkich = 0; (*Gracz2).lJazdy = 0; (*Gracz2).lRobo = 0; (*Gracz2).lSurowcow = 300; (*Gracz2).it_wstawianie=0;
    (*Gracz2).it_przetwarzanie=0; (*Gracz2).que_len = 0;
    (*Gracz2).punkty = 0; (*Gracz2).atakuje=false;
    int shmid3 = shmget(104, sizeof(bool),IPC_CREAT | 0666);
    bool *koniec = shmat(shmid3,NULL,0);
    (*koniec) = false;
    if(shmid==-1 || shmid2==-1 || shmid3==-1)
    {
        perror("Problem z pamiecia wspoldzielona u serwera.\n");
        return 1;
    }
    powiadom_wojsko(klucz_kolejki,1,Gracz1);
    powiadom_wojsko(klucz_kolejki,2,Gracz2);
    powiadom_sur(klucz_kolejki,1,Gracz1);
    powiadom_sur(klucz_kolejki,1,Gracz1);

    int semid = semget(55, 2, 0600|IPC_CREAT);
    if(semid==-1)
    {
        perror("Serwer nie uzyskal semafora.\n");
        return 1;
    }
    semctl(semid,0,SETVAL,1); //inicjalizacja semafora na podniesiony
    semctl(semid,1,SETVAL,1);
    struct sembuf p1,p2,v1,v2;
    p1.sem_num=0; p1.sem_flg=0; p1.sem_op=-1; //opuszcza dla Gracz1
    p2.sem_num=1; p2.sem_flg=0; p2.sem_op=-1; //opuszcza dla Gracz2
    v1.sem_num=0; v1.sem_flg=0; v1.sem_op=1; //podnosci dla Gracz1
    v2.sem_num=1; v2.sem_flg=0; v2.sem_op=1; //podnosci dla Gracz2

    //struktury informacyjne
    jednostka tab_jedn[4]; // 0-robotnik 1-jazda 2-lekka 3-ciezka
    //robotnik
    tab_jedn[0].cena = 150;
    tab_jedn[0].atak = 0;
    tab_jedn[0].obrona = 0;
    tab_jedn[0].czas = 2;
    //jazda
    tab_jedn[1].cena = 550;
    tab_jedn[1].atak = 3.5;
    tab_jedn[1].obrona = 1.2;
    tab_jedn[1].czas = 5;
    //lekka
    tab_jedn[2].cena = 100;
    tab_jedn[2].atak = 1;
    tab_jedn[2].obrona = 1.2;
    tab_jedn[2].czas = 2;
    //ciezka
    tab_jedn[3].cena = 250;
    tab_jedn[3].atak = 1.5;
    tab_jedn[3].obrona = 3;
    tab_jedn[3].czas = 3;

    //sygnaly wybudzajace produkcje
    signal(14,SIG_IGN);
    signal(16,SIG_IGN);
    if(fork())
    {
      if(fork())
      {
	  //odbior1
	  while(!(*koniec))
	  {
	    Message msg;
	    msgrcv(klucz_kolejki, &msg, MSGLEN, 1, 0);
	    if(!(msg.mtext[0]=='a' && msg.mtext[1]=='t' && msg.mtext[2]=='a' && msg.mtext[3]=='k'))
	    {
	      semop(semid,&p1,1);
	      char znak = msg.mtext[0];
	      int ilosc = atoi(msg.mtext+1);
	      printf("Serwer odebral od 1: %s  ilosc: %d  typ:  %c\n",msg.mtext, ilosc, znak);
	      if((ilosc<100-(*Gracz1).que_len) && (znak=='l' || znak=='c' || znak=='r' || znak=='j') && ilosc>0)
	      {
		enum Typ_jednostki typ_jedn = daj_typ(znak);

		if(tab_jedn[typ_jedn].cena*ilosc < (*Gracz1).lSurowcow)
		{
		  (*Gracz1).lSurowcow -= tab_jedn[typ_jedn].cena*ilosc;
		  powiadom_sur(klucz_kolejki,1,Gracz1);
		  while(ilosc--)
		  {
		    (*Gracz1).kolejka[(*Gracz1).it_wstawianie] = znak;
		    (*Gracz1).it_wstawianie = ((*Gracz1).it_wstawianie+1)%100;
		    (*Gracz1).que_len++;
		  }
		  //wyslanie informacji o tym ze nalezy rozpoczac rekrutacje
		  kill(0,14);
		}
		else
		{
		  wiadomosc(klucz_kolejki,1,"Nie stac cie!");
		}
		semop(semid,&v1,1);
	      }
	      else
	      {
            semop(semid,&v1,1);
	      }
	    }
	    else
	    {
	      //rozpatrzenie ataku gracza1
	      if(atoi(msg.mtext+5)>0 && !(*Gracz1).atakuje && (msg.mtext[4]=='l' || msg.mtext[4]=='c' || msg.mtext[4]=='j'))
	      {
		if(fork()==0)
		{
		  (*koniec)= wojna(msg, semid, klucz_kolejki, tab_jedn,Gracz1,Gracz2,1);
		  if((*koniec)){ sleep(2);
		      semctl(semid,0,IPC_RMID,0);
		      shmdt(Gracz1); shmdt(Gracz2); shmdt(koniec);
		      shmctl(shmid,IPC_RMID,0); shmctl(shmid2,IPC_RMID,0);
		      shmctl(shmid3,IPC_RMID,0);
		      msgctl(klucz_kolejki,IPC_RMID,0);
		      kill(0,2);}
		  _exit(0);
		}
	      }
	      else if(atoi(msg.mtext+5)<=0)
	      {
		wiadomosc(klucz_kolejki,1,"Nie wybrales wojsk...");
	      }
	      else if((*Gracz1).atakuje)
	      {
		wiadomosc(klucz_kolejki,1,"Poczekaj na powrot wojsk.");
	      }
	      else
	      {
		wiadomosc(klucz_kolejki,1,"Niepoprawne polecenie ataku.");
	      }
	    }
	  }
      }
      else
      {
	  //odbior2
	  while(!(*koniec))
	  {
	    Message msg;
	    msgrcv(klucz_kolejki, &msg, MSGLEN, 2, 0);
	    if(!(msg.mtext[0]=='a' && msg.mtext[1]=='t' && msg.mtext[2]=='a' && msg.mtext[3]=='k'))
	    {
	      semop(semid,&p2,1);
	      char znak = msg.mtext[0];
	      int ilosc = atoi(msg.mtext+1);
	      printf("Serwer odebral od 2: %s  ilosc: %d  typ:  %c\n",msg.mtext, ilosc, znak);
	      if((ilosc<100-(*Gracz2).que_len) && (znak=='l' || znak=='c' || znak=='r' || znak=='j') && ilosc>0)
	      {
            enum Typ_jednostki typ_jedn = daj_typ(znak);

            if(tab_jedn[typ_jedn].cena*ilosc < (*Gracz2).lSurowcow)
            {
              (*Gracz2).lSurowcow -= tab_jedn[typ_jedn].cena*ilosc;
              powiadom_sur(klucz_kolejki,2,Gracz2);
              while(ilosc--)
              {
                (*Gracz2).kolejka[(*Gracz2).it_wstawianie] = znak;
                (*Gracz2).it_wstawianie = ((*Gracz2).it_wstawianie+1)%100;
                (*Gracz2).que_len++;
              }
              //wyslanie informacji o tym ze nalezy rozpoczac rekrutacje
              kill(0,16);
            }
            else
            {
              wiadomosc(klucz_kolejki,2,"Nie stac cie!");
            }
	    semop(semid,&v2,1);
	      }
	      else
	      {
		semop(semid,&v2,1);
	      }
	    }
	    else
	    {
	      //rozpatrzenie ataku gracza2
	      if(atoi(msg.mtext+5)>0 && !(*Gracz2).atakuje && (msg.mtext[4]=='l' || msg.mtext[4]=='c' || msg.mtext[4]=='j'))
	      {
		if(fork()==0)
		{
		  (*koniec) = wojna(msg, semid, klucz_kolejki, tab_jedn,Gracz2,Gracz1,2);
		  if((*koniec)){ sleep(2);
		    semctl(semid,0,IPC_RMID,0);
		    shmdt(Gracz1); shmdt(Gracz2); shmdt(koniec);
		    shmctl(shmid,IPC_RMID,0); shmctl(shmid2,IPC_RMID,0);
		    shmctl(shmid3,IPC_RMID,0);
		    msgctl(klucz_kolejki,IPC_RMID,0); kill(0,2);}
		  _exit(0);
		}
	      }
	      else if(atoi(msg.mtext+5)<=0)
	      {
		wiadomosc(klucz_kolejki,2,"Nie wybrales wojsk...");
	      }
	      else if((*Gracz2).atakuje)
	      {
		wiadomosc(klucz_kolejki,2,"Poczekaj na powrot wojsk.");
	      }
	      else
	      {
		wiadomosc(klucz_kolejki,2,"Niepoprawne polecenie ataku.");
	      }
	    }
	  }
	}
    }
    else
    {
      //logika
      if(fork())
      {
	//naliczanie surowcow
	while(!(*koniec))
	{
	  sleep(1);
	  semop(semid,&p1,1);
	  (*Gracz1).lSurowcow += 50 + (5* (*Gracz1).lRobo);
	  semop(semid,&v1,1);
	  semop(semid,&p2,1);
	  (*Gracz2).lSurowcow += 50 + (5* (*Gracz2).lRobo);
	  semop(semid,&v2,1);
	  powiadom_sur(klucz_kolejki,1,Gracz1);
	  powiadom_sur(klucz_kolejki,2,Gracz2);
	}
      }
      else
      {
	//obsluga trenowania wojsk
	if(fork())
	{
	  //trenowanie gracza1
	  while(!(*koniec))
	  {
	    printf("Produkcja1 zasypia\n");
	    signal(14,moja);
	    pause();
	    signal(14,SIG_IGN);
	    printf("Produkcja1 sie budzi\n");
	    while((*Gracz1).que_len != 0)
	    {
	      semop(semid,&p1,1);
	      enum Typ_jednostki typ_jedn = daj_typ((*Gracz1).kolejka[(*Gracz1).it_przetwarzanie]);
	      (*Gracz1).it_przetwarzanie = ((*Gracz1).it_przetwarzanie+1)%100;
	      semop(semid,&v1,1);
	      sleep(tab_jedn[typ_jedn].czas);
	      semop(semid,&p1,1);
	      if(typ_jedn==0) (*Gracz1).lRobo++;
	      if(typ_jedn==1) (*Gracz1).lJazdy++;
	      if(typ_jedn==2) (*Gracz1).lLekkich++;
	      if(typ_jedn==3) (*Gracz1).lCiezkich++;
	      (*Gracz1).que_len--;
	      semop(semid,&v1,1);
	      powiadom_wojsko(klucz_kolejki,1,Gracz1);
	    }
	  }
	}
	else
	{
	  //trenowanie gracza 2
	  while(!(*koniec))
	  {
	    signal(16,moja);
	    pause();
	    signal(16,SIG_IGN);
	    while((*Gracz2).que_len != 0)
	    {
	      semop(semid,&p2,1);
	      enum Typ_jednostki typ_jedn = daj_typ((*Gracz2).kolejka[(*Gracz2).it_przetwarzanie]);
	      (*Gracz2).it_przetwarzanie = ((*Gracz2).it_przetwarzanie+1)%100;
	      semop(semid,&v2,1);
	      sleep(tab_jedn[typ_jedn].czas);
	      semop(semid,&p2,1);
	      if(typ_jedn==0) (*Gracz2).lRobo++;
	      if(typ_jedn==1) (*Gracz2).lJazdy++;
	      if(typ_jedn==2) (*Gracz2).lLekkich++;
	      if(typ_jedn==3) (*Gracz2).lCiezkich++;
	      (*Gracz2).que_len--;
	      semop(semid,&v2,1);
	      powiadom_wojsko(klucz_kolejki,2,Gracz2);
	    }
	  }
	}

      }
    }

    return 0;
}
