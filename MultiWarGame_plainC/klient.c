#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/shm.h>
#include <sys/sem.h>
#include <string.h>
#include <signal.h>
#include <ncurses.h>

#define MSGLEN 60

typedef struct Message
{
  long mtyp;
  char mtext[MSGLEN];
} Message;

int main()
{

    int klucz_kolejki = msgget(100, IPC_CREAT|0666);
    if(klucz_kolejki == -1)
    {
        perror("Klientowi nie udało się podłączyć do kolejki.\n");
        return 1;
    }
    long typ_wysylanej;
    Message msgx;
    msgrcv(klucz_kolejki, &msgx,MSGLEN,5,0);
    typ_wysylanej = atoi(msgx.mtext);
    msgx.mtyp=6;
    msgsnd(klucz_kolejki,&msgx,MSGLEN,0);
    long typ_odbieranej= typ_wysylanej+2;

    int shmid = shmget(110+typ_odbieranej, sizeof(char)*MSGLEN,IPC_CREAT | 0666);
    if(shmid==-1)
    {
        perror("Problem u klienta z pamiecia wspoldzielona.\n");
        return 1;
    }
    char *tresc = shmat(shmid,NULL,0);

    initscr();
    cbreak();
    noecho();
    if(fork())
    {
      while(1)
      {
        int i=0;

        while(1)
        {
          move(12,0);
          (*(tresc+i))= getch();
          if((*(tresc+i))==10 || i==MSGLEN-1)
          {
            (*(tresc+i))='\0';
            break;
          }
          if((*(tresc+i))!=127) i++;
          else if(i>0) i--;
          (*(tresc+i))='\0';
        }
        Message msg;
        strcpy(msg.mtext,tresc);
        i=0; (*(tresc+i))='\0';
        msg.mtyp = typ_wysylanej;
        //scanf("%s",msg.mtext);
    //	mvscanw(12,0,"%s",msg.mtext);
        msgsnd(klucz_kolejki, &msg, MSGLEN,0);
      }
    }
    else
    {
    int lr=0,ll=0,lc=0,lj=0;
    WINDOW *czytaj = newwin(1,30,13,0);
    WINDOW *surowce = newwin(1,30,0,0);
    WINDOW *wojsko = newwin(4,30,1,0);
    WINDOW *wiadomosc = newwin(6,MSGLEN,6,0);
    refresh();
    int licz_wiadomosci=0;
  //  char przechowuj_wiadomosci[4][30];
      while(1)
      {
        Message msg;
        msgrcv(klucz_kolejki, &msg,MSGLEN,typ_odbieranej,0);
        //printf("Klient odebral: %s\n", msg.mtext);

        if(msg.mtext[0]=='x')
        {

          //aktualizuj licznosc dostepnych wojsk
            int a = 1;
            wclear(wojsko);
            for(a; msg.mtext[a]!='\0'; a++)
            {
              if(msg.mtext[a]=='s')
              {
                //aktualizuj surowce
                wclear(surowce);
                wprintw(surowce, "Surowce: %d",atoi(msg.mtext+a+1));
                wrefresh(surowce);
              }
              else if(msg.mtext[a]=='j')
              {
                lj = atoi(msg.mtext+a+1);
              }
              else if(msg.mtext[a]=='r')
              {
                lr = atoi(msg.mtext+a+1);
              }
              else if(msg.mtext[a]=='l')
              {
                ll = atoi(msg.mtext+a+1);
              }
              else if(msg.mtext[a]=='c')
              {
                lc = atoi(msg.mtext+a+1);
              }
            }
            wprintw(wojsko,"(150)Robotnicy: %d\n",lr);
            wprintw(wojsko,"(100)Lekkich: %d\n",ll);
            wprintw(wojsko,"(250)Ciezkich: %d\n",lc);
            wprintw(wojsko,"(550)Jazdy: %d\n",lj);
            wrefresh(wojsko);
        }
        else
        {
          //wyswietl wiadomosc
          if(licz_wiadomosci>=6) { wclear(wiadomosc); wrefresh(wiadomosc); licz_wiadomosci=0;}
          wprintw(wiadomosc,"%s\n",msg.mtext);
          wrefresh(wiadomosc);
          licz_wiadomosci++;
        }
        if(strcmp(msg.mtext,"wygrales")==0 || strcmp(msg.mtext,"przegrales")==0)
        {
          wprintw(wiadomosc,"%s\n",msg.mtext);
          wrefresh(wiadomosc);
          sleep(2);
          endwin();
          shmdt(tresc);
          shmctl(shmid,IPC_RMID,0);
          kill(0,2);
        }
        wclear(czytaj);
        wprintw(czytaj,"%s",tresc);
        wrefresh(czytaj);
        move(12,0);
      }
    }
    return 0;
}
