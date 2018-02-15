#include <stdio.h>
#include <stdlib.h>
int i;
FILE * file;
int main(int argc, const char * argv[]){
	file = fopen("input.txt","r");
	if (file) {
		while(fscanf(file, "%d",&i) == 1){
			printf("Integer is %d",i);
		} 
		
	}


}
