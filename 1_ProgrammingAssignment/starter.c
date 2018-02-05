#include <stdio.h>
#include<stdlib.h>
#include<errno.h>
#include<unistd.h>
#include<string.h>
#include<ctype.h>

#define INELIGIBLE 0
#define READY 1
#define RUNNING 2
#define FINISHED 3

#define MAX_LENGTH 1024
#define MAX_PARENTS 10
#define MAX_CHILDREN 10
#define MAX_NODES 50

typedef struct node {
  int id; //parsed
  char prog[MAX_LENGTH]; //parsed
  char *args[MAX_LENGTH/2 + 1]; //parsed
  int num_args; //parsed
  char input[MAX_LENGTH]; //parsed
  char output[MAX_LENGTH]; //parsed
  int parents[MAX_PARENTS];
  int num_parents;
  int children[MAX_CHILDREN]; //parsed
  int num_children; //parsed
  int status;
  pid_t pid;
} node_t;

/**
 * Search for tokens in the string s, separated by the characters in 
 * delimiters. Populate the string array at *tokens.
 *
 * Return the number of tokens parsed on success, or -1 and set errno on 
 * failure.
 */
int parse_tokens(const char *s, const char *delimiters, char ***tokens) {
  const char *s_new;
  char *t;
  int num_tokens;
  int errno_copy;

  /* Check arguments */
  if ((s == NULL) || (delimiters == NULL) || (tokens == NULL)) {
    errno = EINVAL;
    return -1;
  }

  /* Clear token array */
  *tokens = NULL;

  /* Ignore initial segment of s that only consists of delimiters */
  s_new = s + strspn(s, delimiters);

  /* Make a copy of s_new (strtok modifies string) */
  t = (char *) malloc(strlen(s_new) + 1);
  if (t == NULL) {
    return -1;    
  }
  strcpy(t, s_new);

  /* Count number of tokens */
  num_tokens = 0;
  if (strtok(t, delimiters) != NULL) {
    for (num_tokens = 1; strtok(NULL, delimiters) != NULL; num_tokens++) ;
  }

  /* Allocate memory for tokens */
  *tokens = (char**) malloc((num_tokens + 1)*sizeof(char *));
  if (*tokens == NULL) {
    errno_copy = errno;
    free(t);  // ignore errno from free
    errno = errno_copy;  // retain errno from malloc
    return -1;
  }

  /* Parse tokens */
  if (num_tokens == 0) {
    free(t);
  } else {
    strcpy(t, s_new);
    **tokens = strtok(t, delimiters);
    for (int i=1; i<num_tokens; i++) {
      *((*tokens) +i) = strtok(NULL, delimiters);      
    }
  }
  *((*tokens) + num_tokens) = NULL;  // end with null pointer

  return num_tokens;
}

void free_parse_tokens(char **tokens) {
  if (tokens == NULL) {
    return;    
  }
  
  if (*tokens != NULL) {
    free(*tokens);    
  }

  free(tokens);
}

/**
 * Parse the input line at line, and populate the node at node, which will
 * have id set to id.
 * 
 * Return 0 on success or -1 and set errno on failure.
 */
int parse_input_line(char *line, int id, node_t *node) {
  char **strings;  // string array
  char **arg_list;  // string array
  char **child_list;  // string array
  int a;

  /* Split the line on ":" delimiters */
  if (parse_tokens(line, ":", &strings) == -1) {
    perror("Failed to parse node information");
    return -1;
  }

  /* Parse the space-delimited argument list */
  if (parse_tokens(strings[0], " ", &arg_list) == -1) {
    perror("Failed to parse argument list");
    free_parse_tokens(strings);
    return -1;
  }

  /* Parse the space-delimited child list */
  if (parse_tokens(strings[1], " ", &child_list) == -1) {
    perror("Failed to parse child list");
    free_parse_tokens(strings);
    return -1;
  }

  /* Set node id */
  node->id = id;
  fprintf(stderr, "... id = %d\n", node->id);

  /* Set program name */
  strcpy(node->prog, arg_list[0]);
  fprintf(stderr, "... prog = %s\n", node->prog);

  /* Set program arguments */
  for (a = 0; arg_list[a] != NULL; a++) {
    node->args[a] = arg_list[a];
    node->num_args++;
    fprintf(stderr, "... arg[%d] = %s\n", a, node->args[a]);
  }

  node->args[a] = NULL;
  fprintf(stderr, "... arg[%d] = %s\n", a, node->args[a]);

  fprintf(stderr, "... num_args = %d\n", node->num_args);

  /* Set input file */
  strcpy(node->input, strings[2]);
  fprintf(stderr, "... input = %s\n", node->input);
  
  /* Set output file */
  strcpy(node->output, strings[3]);
  fprintf(stderr, "... output = %s\n", node->output);
    
  /* Set child nodes */
  node->num_children = 0;
  if (strcmp(child_list[0], "none") != 0) {
    for (int c = 0; child_list[c] != NULL; c++) {
      if (c < MAX_CHILDREN) {
        if (atoi(child_list[c]) != id) {
          node->children[c] = atoi(child_list[c]);
          fprintf(stderr, "... child[%d] = %d\n", c, node->children[c]);
          node->num_children++;
        } else {
          perror("Node cannot be a child of itself");
          return -1;
        }
      } else {
        perror("Exceeded maximum number of children per node");
        return -1;
      }
    }
  }
  fprintf(stderr, "... num_children = %d\n", node->num_children);
  /* Set parent nodes */
  node->num_parents = 0;

  return 0;
}

/**
 * Parse the file at file_name, and populate the array at node. *node is array of node
 * 
 * Return the number of nodes parsed on success, or -1 and set errno on
 * failure.
 */
int parse_graph_file(char *file_name, node_t *nodes) {
  FILE *f;
  char line[MAX_LENGTH];
  int id = 0;
  int errno_copy;

  /* Open file for reading */
  fprintf(stderr, "Opening file...\n");
  f = fopen(file_name, "r");
  if (f == NULL) {
    perror("Failed to open file");
    return -1;
  }

  /* Read file line by line */
  fprintf(stderr, "Reading file...\n");
  while (fgets(line, MAX_LENGTH, f) != NULL) {
    strtok(line, "\n");  // remove trailing newline

    /* Parse line */
    fprintf(stderr, "Parsing line %d...\n", id);
    if (parse_input_line(line, id, nodes) == 0) { //nodes is the address of the node
      nodes++;  // increment pointer to point to next node in array
      id++;  // increment node ID
      if (id >= MAX_NODES) {
        perror("Exceeded maximum number of nodes");
        return -1;
      }
    } else {
      perror("Failed to parse input line");
      return -1;
    }
  }

  /* Handle file reading errors and close file */
  if (ferror(f)) {
    errno_copy = errno;
    fclose(f);  // ignore errno from fclose
    errno = errno_copy;  // retain errno from fgets
    perror("Error reading file");
    return -1;
  }

  /* If no file reading errors, close file */
  if (fclose(f) == EOF) {
    perror("Error closing file");
    return -1;  // stream was not successfully closed
  }
  
  /* If no file closing errors, return number of nodes parsed */  
  return id;
}

/**
 * Parses the process tree represented by nodes and determines the parent(s)
 * of each node.
 */
int parse_node_parents(node_t *nodes, int num_nodes) {
	int counter = 0;
	int id = 0;

	while(counter < num_nodes){	
		id = nodes[counter].id;
		int *child_list = nodes[counter].children; //Reference to children array
		for (int i = 0; i < nodes[counter].num_children; i++) {
			int *parents = nodes[child_list[i]].parents; //get reference to the parents array
			int *num_parents = &nodes[child_list[i]].num_parents; //Get pointer to the number of parents
			parents[*num_parents] = id; //Append data to the array
			(*num_parents)++;
			printf("ID %d: Number of parents for id %d: %d\n",id,child_list[i],*num_parents);
		}
		counter++;
	}
	// counter = 0;
	// while(counter < num_nodes){
	// 	printf("1st parent of ID %d: %d\n",nodes[counter].id, nodes[counter].parents[0]);
	// 	counter++;
	// }
}

/**
 * Prints the process tree represented by nodes to standard error.
 *
 * Returns 0 if printed successfully.
 */
int print_process_tree(node_t *nodes, int num_nodes) {
	for(int i = 0; i < num_nodes; i++){
		fprintf(stderr,"Process ID: %d\n", nodes->id);
		fprintf(stderr,"...prog = %s\n", nodes->prog);
		fprintf(stderr,"...num_args = %d\n", nodes->num_args);
		for(int i2 = 0; i2 < nodes->num_args; i2++){
			fprintf(stderr,"...args[%d] = %s\n", i2, nodes->args[i2]);
		}		
		fprintf(stderr,"...input = %s\n", nodes->input);
		fprintf(stderr,"...output = %s\n", nodes->output);
		fprintf(stderr,"...num_parents = %d\n",nodes->num_parents);
		for(int i3 = 0; i3 < nodes->num_parents; i3++){
			fprintf(stderr,"...parents[%d] = %d\n", i3, nodes->parents[i3]);
		}
		fprintf(stderr,"...num_children = %d\n",nodes->num_children);
		for(int i4 = 0; i4 < nodes->num_children; i4++){
			fprintf(stderr,"...children[%d] = %d\n", i4, nodes->children[i4]);
		}		
		fprintf(stderr,"...status = %d\n", nodes->status);
		fprintf(stderr,"...pid = %d\n", nodes->pid);
		nodes++;
	}
	return 0;
}

/**
 * Checks the status of each node in the process tree represented by nodes and 
 * verifies whether it can progress to the next stage in the cycle:
 *
 * INELIGIBLE -> READY -> RUNNING -> FINISHED
 *
 * Returns the number of nodes that have finished running, or -1 if there was 
 * an error.
 */
int parse_node_status(node_t *nodes, int num_nodes) {
	int num_nodes_finished = 0;
	return num_nodes_finished;
}

/**
Check the status of one node and mark it as READY if all parents are FINISHED.
*/
int update_node_readyness(node_t *nodes, int index) {
	node_t *localNode = &nodes[index];
	if (localNode -> status == 0)
	{
		int *parents = localNode->parents;
		for (int i = 0; i < localNode->num_parents; i++)
		{
			if(nodes[parents[i]].status < 3){ // Check if parents are FINISHED
				localNode -> status == INELIGIBLE; // if not, the node are not eligible
			}
		}
		localNode -> status = READY; //All parents are FINISHED, node is ready.
	}
	return 0;
}


/**
 * Takes in a graph file and executes the programs in parallel.
 */
int main(int argc, char *argv[]) {
  node_t nodes[MAX_NODES];
  int num_nodes;
  int num_nodes_finished;

  /* Check command line arguments */
  char *file_name = argv[1];

  /* INSERT CODE */

  /* Parse graph file */
  fprintf(stderr, "Parsing graph file...\n");
  /* INSERT CODE - invocation to parse_graph_file */
  num_nodes = parse_graph_file(file_name,nodes);
  printf("Number of nodes: %d\n", num_nodes);

  /* Parse nodes for parents */
  fprintf(stderr, "Parsing node parents...\n");
  /* INSERT CODE - invocation to parse_node_parents */
  parse_node_parents(nodes, num_nodes);

  /* Print process tree */
  fprintf(stderr, "\nProcess tree:\n");
  print_process_tree(nodes,num_nodes);

  /* INSERT CODE  - print the process tree */

  /* Run processes */
  fprintf(stderr, "Running processes...\n");

  /* INSERT CODE  - invocation to parse_node_status */
  num_nodes_finished = parse_node_status(nodes,num_nodes);
  
  if (num_nodes_finished < 0) {
    perror("Error executing processes");
    return EXIT_FAILURE;
  }

  fprintf(stderr, "All processes finished. Exiting.\n");
  return EXIT_SUCCESS;
}
