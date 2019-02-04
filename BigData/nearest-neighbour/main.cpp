#include <fstream>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <set>
#include <algorithm>
#include <utility>
#include <limits>
#include <iomanip>
#include <time.h>
using namespace std;

class User;

bool comparePairs (const pair<int, float>& lhs, const pair<int, float>& rhs) { 
	return lhs.second > rhs.second;
}

class Song {
public:
	Song(int id) {
		this->id = id;
	};
	int id;
	set<User*> users;

	void addUser(User* user) {
		if (users.find(user) == users.end()) {
			//user->updateSimilarities(users);
			users.insert(user);
		}
	}
};

class User {
public:
	User(int id) {
		this->id = id;
	};

	int id;
	set<Song*> songs;

	vector<pair<int,float>> similarities(unordered_map<int, User*> users_map) {
		unordered_map<int, int> common_count;
		for (Song* song : songs) {
			for (User* user : song->users) {
				common_count[user->id]++;
			}
		}

		vector<pair<int, float>> similarities;
		for (unordered_map<int, int>::iterator it = common_count.begin(); it != common_count.end(); ++it) {
			set<Song*> combinedSet;
			set<Song*> songs2 = users_map[it->first]->songs;
			combinedSet.insert(songs.begin(), songs.end());
			combinedSet.insert(songs2.begin(), songs2.end());
			similarities.push_back(pair<int, float>(it->first, ((float)it->second)/((float)combinedSet.size())));
		}
		sort(similarities.begin(), similarities.end(), comparePairs);
		similarities.resize(100);
		return similarities;
	}

	void addSong(Song* song) {
		songs.insert(song);
	}
};

int main(int argc, const char * argv[]) {
	if (argc == 1) {
		printf("Please specify file location\n");
		return -1;
	}
	int count = 100;
	if (argc == 3) {
		printf("Setting limit to %d \n", atoi(argv[2]));
		count = atoi(argv[2]);
	}
	clock_t start = clock();
	unordered_map<int, User*> users_map;
	unordered_map<int, Song*> songs_map;
	FILE * fp;
	fp = fopen(argv[1], "r");
	if (fp == NULL) {
		printf("Could not open file\n");
		return -1;
	}
	int song_id;
	int user_id;
	int date_id;
	vector<int> original_order_ids;

	int i = 0;
	while (fscanf(fp, "%d,%d,%d", &song_id, &user_id, &date_id) != EOF) {
		//cout << song_id << " " << user_id << endl;
		User *user = nullptr;
		Song *song = nullptr;
		if (users_map.find(user_id) == users_map.end()) {
			user = new User(user_id);
			users_map[user_id] = user;
			if (original_order_ids.size() < count) {
				original_order_ids.push_back(user_id);
			}
		}
		if (songs_map.find(song_id) == songs_map.end()) {
			song = new Song(song_id);
			songs_map[song_id] = song;
		}
		if (song == nullptr) {
			song = songs_map[song_id];
		}
		if (user == nullptr) {
			user = users_map[user_id];
		}
		user->addSong(song);
		song->addUser(user);
	}
	//cout << endl << users_map.size() << " " << songs_map.size() << endl;
	clock_t stop = clock();
	double elapsed = (double)(stop - start) * 1000.0 / CLOCKS_PER_SEC;
	printf("ReadingFile::Time elapsed in ms: %f\n", elapsed);
	printf("Calculating similarities for %d users\n", original_order_ids.size());
	ofstream outputfile;
	outputfile.open("result.csv");
	for (int user_id : original_order_ids) {
		vector<pair<int, float>> similarities = users_map[user_id]->similarities(users_map);
		outputfile << setprecision(3) << "User = " << user_id << endl;
		for (int i = 0; i < min((int)similarities.size(), 100); i++) {
			outputfile << similarities[i].first << " " << similarities[i].second << endl;
		}
	}
	outputfile.close();
	//int zakoncz;
	//cin >> zakoncz;
	stop = clock();
	elapsed = (double)(stop - start) * 1000.0 / CLOCKS_PER_SEC;
	printf("Completed::Time elapsed in ms: %f\n", elapsed);

	return 0;
}