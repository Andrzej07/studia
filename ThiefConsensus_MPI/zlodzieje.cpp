#include <mpi.h>
#include <unistd.h>
#include <cstdio>
#include <cstdlib>
#include <ctime>
#include <queue>
#include <set>
#include <iostream>
#include <cassert>


#define QD_REQ 10
#define QD_FIN 11
#define QD_ACK 12
#define QP_REQ 13
#define QP_FIN 14
#define QP_ACK 15
#define FIN 16
using namespace std;

struct QueueItem {
	int id;
	int time;

        QueueItem(int id, int time)
        {
            this->id = id;
            this->time = time;
        }
};

struct CompareQueue {
	bool operator()(QueueItem const &lhs, QueueItem const &rhs)
	{
		if (lhs.time != rhs.time)
			return lhs.time > rhs.time;
		else
			return lhs.id > rhs.id;
	}
};

class Thief {
    typedef priority_queue<QueueItem, vector<QueueItem>, CompareQueue> PrioQueue;

    const int P;
    const int D;
    bool waitingForFence;
    bool waitingForHouse;
	int size, rank;
    int time;
	int nrD;
    PrioQueue qP;
    PrioQueue *qD;
	set<int> nP;
    set<int> nD;

    void notifyAll(int tag, int *package, int size)
	{
        for (int i = 0; i < this->size; i++)
		{			
			if (i != rank)
                MPI_Send(package, size, MPI_INT, i, tag, MPI_COMM_WORLD);
		}
	}

	int getQPPosition()
	{
		int pos = 0;
		/*vector<QueueItem> removed;
		while (qP.top().id != rank)
		{
			pos++;
			removed.push_back(QueueItem(qP.top())
		}*/
		priority_queue<QueueItem, vector<QueueItem>, CompareQueue> qP2(qP);
		while (qP2.top().id != rank)
		{
			pos++;
			qP2.pop();
		}
		return pos;
	}

public:
    Thief(int size, int rank, int nrD, int D, int P): P(P), D(D)
    {
        this->size = size;
        this->rank = rank;
        this->nrD = nrD;
        this->waitingForFence = false;
        this->waitingForHouse = false;
        this->time = 0;
        qD = new PrioQueue[D];
    }
    ~Thief()
    {
        delete [] qD;
    }

	int getNrD() { return nrD; }
	void chooseNextHouse()
	{
		int len = qD[0].size();
		nrD = 0;
		for (int i = 1; i < D; i++)
		{
			// choose if queue is shorter or rand choose if equal
                        if (qD[i].size() < len || (qD[i].size() == len && rand() % 2))
			{
				len = qD[i].size();
				nrD = i;
			}
		}
	}

	void queueHouse()
	{
		waitingForHouse = true;
		qD[nrD].push(QueueItem(rank, time));
	}
	void queueFence()
	{
		waitingForFence = true;
		qP.push(QueueItem(rank, time));
	}
	void removeFromHouseQueue()
	{
		waitingForHouse = false;
		assert(qD[nrD].top().id == rank);
		qD[nrD].pop();
	}
	void removeFromFenceQueue(int id)
	{
		if(id == rank)
			waitingForFence = false;
		vector<QueueItem> safe;
		// Search for target
        while (qP.top().id != id)
		{
			safe.push_back(qP.top());
			qP.pop();
		}
		// Remove target
		qP.pop();
		// Recover innocent victims
        /*for (auto &item : safe)
                qP.push(item); */
        for(int i =0; i< safe.size(); i++)
        {
            qP.push(safe[i]);
        }
		
	}
	void sendHouseChoice()
	{		
		// Reset not interested
		nD.clear();
		int package[3] = { rank, time, nrD };
		notifyAll(QD_REQ, package, 3);
	}
	void sendFenceRequest()
	{
		// Reset not interested
		nP.clear();	
		int package[2] = { rank, time };
		notifyAll(QP_REQ, package, 2);
	}
	void sendFinished()
	{
		int package[3] = { rank, time, nrD };
		notifyAll(FIN, package, 3);
	}
	void incrementTime() { time++; }
	int getTime() { return time; }
	bool canRobHouse()
	{
        int sumsize = nD.size();
        //int sumsize = 0;
		for (int i = 0; i < D; i++)
		{
			sumsize += qD[i].size();
		}
        return (sumsize == size) && (qD[nrD].top().id == rank);
	}

	bool canAccessFence()
	{
		// If we have all info and a fence is available (our position in queue is less than amount of fences)
		int pos = getQPPosition();
		return (nP.size()+qP.size() == size) && (pos < P);
	}

	void handleCommunication()
	{
		MPI_Status status;
        int flag = 0;
		MPI_Iprobe(MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &flag, &status);
        while (flag)
		{

			int count; 
			MPI_Get_count(&status, MPI_INT, &count);
            if(count > 3 || count < 1)
            {
                printf("Caught weird message of length %d.\n", count);
                break;
            }
            int *package = new int[count];
			MPI_Recv(package, count, MPI_INT, status.MPI_SOURCE, status.MPI_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            /*printf("%d is receiving %d.", rank, status.MPI_TAG);
            for(int i = 0; i<count;i++)
                printf(" %d ", package[i]);
            printf("\n"); */
            switch (status.MPI_TAG)
			{
			// HOUSE REQUEST
			case QD_REQ:
				assert(status.MPI_TAG == QD_REQ);
				assert(status.MPI_SOURCE == package[0]);
				// Remove sender from not interested
				nD.erase(package[0]);
				// Add sender to house queue
				qD[package[2]].push(QueueItem(package[0], package[1]));
				// Respond that I'm not waiting for house
				if (!waitingForHouse)
				{
					time++;
                    int x[2] = {rank, time};
                    MPI_Send(&x, 2, MPI_INT, status.MPI_SOURCE, QD_ACK, MPI_COMM_WORLD);
				}
				break;
			// FENCE REQUEST
			case QP_REQ:
				assert(status.MPI_TAG == QP_REQ);
				assert(status.MPI_SOURCE == package[0]);
				// Add sender to my fence queue
				qP.push(QueueItem(package[0], package[1]));
				// Remove him from list of people not interested
				nP.erase(package[0]);
				// Respond that I'm not waiting for fence
				if (!waitingForFence)
				{
					time++;
                    int x[2] = {rank, time};
                    MPI_Send(&x, 2, MPI_INT, status.MPI_SOURCE, QP_ACK, MPI_COMM_WORLD);
				}
				break;
			// SOMEONE IS NOT WAITING FOR FENCE
			case QP_ACK:
				assert(status.MPI_TAG == QP_ACK);
				assert(status.MPI_SOURCE == package[0]);
				// Add him to my list of people not interested
				nP.insert(package[0]);
				break;
			// SOMEONE IS NOT WAITING FOR HOUSE
			case QD_ACK:
				assert(status.MPI_TAG == QD_ACK);
				assert(status.MPI_SOURCE == package[0]);
				// Add him to my list of people not interested
				nD.insert(package[0]);
				break;				
			// SOMEONE FINISHED THEIR ROUTINE
			case FIN:

				assert(status.MPI_TAG == FIN);
				assert(status.MPI_SOURCE == package[0]);
				assert(qD[package[2]].top().id == package[0]);
				qD[package[2]].pop();
                nD.insert(package[0]);
				removeFromFenceQueue(package[0]);
                nP.insert(package[0]);
               /* int sumsize=0;
                for(int i=0; i<D; i++)
                    sumsize += qD[i].size();
                printf("ND SIZE : %d\n",nD.size());
                printf("SUMSIZE : %d\n", sumsize); */
				break;
			}
			time = max(package[1], time) + 1;
			delete[] package;
            MPI_Iprobe(MPI_ANY_SOURCE, MPI_ANY_TAG, MPI_COMM_WORLD, &flag, &status);
		}
	}
	
	void waitAndCommunicate(int hundredsMs)
	{
	    while(hundredsMs--)
        {
        	usleep(100000); // 100ms
        	handleCommunication();
        }  
	}
	
	void printHouseQueues()
	{
		//for(int i = 0; i<qD.size();i++)
		{
			
		}
	}
};

int main(int argc, char **argv)
{
    srand(time(NULL));
    MPI_Init(&argc, &argv);
    int size, rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    if(argc != 3)
    {
        printf("Please input D and P parameters when launcing.\n");
        return 1;
    }
    int D = atoi(argv[1]);
    int P = atoi(argv[2]);
    if(D<1 || P<1)
    {
        printf("Both D and P have to be at least 1.\n");
        return 2;
    }

	// Choose initial house
    Thief thief(size, rank, rank % D, D, P);

	bool choose = false;
	// Main loop
    printf("Hello!%d %d\n", rank, size);
	while (true)
	{
		// HOUSE SECTION
		// Choose next house - the one with shortest queue
		if (choose)
			thief.chooseNextHouse();            
		thief.incrementTime();
		thief.queueHouse();                
		thief.sendHouseChoice();
                //printf("Thief %d chose house %d. Time is %d.\n", rank, thief.getNrD(), thief.getTime());
        printf("[%d] Thief %d chose house %d.\n", thief.getTime(), rank, thief.getNrD());
		// Wait until full info and first in queue to house
                while (!thief.canRobHouse())
                        thief.handleCommunication();
                //printf("Thief %d robbed house %d. Time is %d.\n", rank, thief.getNrD(), thief.getTime());
		printf("[%d] Thief %d robbed house %d.\n", thief.getTime(), rank, thief.getNrD());
		// FENCE(paser) SECTION
		thief.incrementTime();
		thief.queueFence();
		thief.sendFenceRequest();

		// Wait until full info & priveledge to access fence
		while (!thief.canAccessFence())
			thief.handleCommunication();
		//printf("Thief %d is selling goods. Time is %d.\n", rank, thief.getTime());
		printf("[%d] Thief %d is selling goods.\n", thief.getTime(), rank);
		// Time required to sell item and get house ready
        thief.waitAndCommunicate(rand() % 50 + 10);

		thief.removeFromFenceQueue(rank);
		thief.removeFromHouseQueue();
		thief.incrementTime();
		thief.sendFinished();		
		choose = true;
        //printf("Thief %d is done. Releasing house %d. Time is %d.\n", rank, thief.getNrD(), thief.getTime());
        printf("[%d] Thief %d is done. Releasing house %d.\n", thief.getTime(), rank, thief.getNrD());
        thief.waitAndCommunicate(rand() % 50 + 10);      
	}
    MPI_Finalize();
	return 0;
}
