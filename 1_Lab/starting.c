#include <stdio.h>
#include<stdlib.h>
#include<errno.h>
#include<unistd.h>
#include<string.h>
#include<ctype.h>
// #include<regex.h>
#define MAX_INPUT 8192
int hIndex = 0; //For history
char *hist[10]; //Array to store the history

void systemCall(char *command);

void main(){

	char command[MAX_INPUT];//to store users command

	while(1){
		//Q1
		char cwd[1024];


		if (getcwd(cwd, sizeof(cwd)) != NULL)
			printf("csh:%s$ ", cwd);
		else
			perror("getcwd() error");
		fgets(command,MAX_INPUT,stdin);//take input from user
		systemCall(command);
	}
}


//A modified systemCall that takes in both cd and history as command
void systemCall(char *command){
	if (hIndex == 10){
		hIndex = 0;
	}
	const char *p[MAX_INPUT];
	int i1=0;
	char* tempcommand = calloc(strlen(command)+1, sizeof(char));
	strcpy(tempcommand, command);
	p[i1] = strtok(tempcommand," \n");
	while(p[i1]!=NULL)
  {
  	p[++i1] = strtok(NULL," \n");
	}
	//Storing history (Case 3)
	if (!strcmp("history",p[0])){
		//Display history
		int temp = hIndex;
		for(int i2=1;i2<=10;i2++){
			temp == 0?temp = 9:temp--;
			if(hist[temp] == NULL){
				break;
			}
			printf("%d %s", i2, hist[temp]);
		}
		return;
	} else if (!strcmp("!!",p[0])){
		//Execute most recent command
		int temp = hIndex;
		temp == 0?temp = 9:temp--;
		system(hist[temp]);
		return;
	} else if (atoi(p[0])>=1 && atoi(p[0])<=10){
		// printf("atoi value is %d",atoi(p[0]));
		int num = atoi(p[0]) - 1;
		int temp = hIndex -1 - num;
		temp < 0 ? temp = 10 + temp:temp;
		if(hist[temp] == NULL){
			printf("history not available for %s. Type \"history\" to see list of available history.\n",p[0]);
			return;
		}
		systemCall(hist[temp]);
		return;
	}
	hist[hIndex] = malloc(strlen(command) + 1);
	strcpy(hist[hIndex], command);
	hIndex++;

	//Changing Directory (Case 2)
	if (!strcmp("cd",p[0])){
		if(p[1]==NULL || !strcmp("~",p[1]))
			chdir(getenv("HOME"));
		else if(!strcmp(".",p[1]))
			return;
		else if(!strcmp("..",p[1]))
			chdir("..");
		else {
			int isExist = chdir(p[1]);
			if(isExist == -1){
				printf("Directory does not exist. Type \"ls\" to see all files and directories.\n");
			}
			return;
		}
	}
	system(command);
}
