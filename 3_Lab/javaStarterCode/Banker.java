import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

// package Week3;

public class Banker {
	private int numberOfCustomers;	// the number of customers
	private int numberOfResources;	// the number of resources

	private int[] available; 	// the available amount of each resource
	private int[][] maximum; 	// the maximum demand of each customer
	private int[][] allocation;	// the amount currently allocated
	private int[][] need;		// the remaining needs of each customer

	/**
	 * Constructor for the Banker class.
	 * @param resources          An array of the available count for each resource.
	 * @param numberOfCustomers  The number of customers.
	 */
	public Banker (int[] resources, int numberOfCustomers) {
		// TODO: set the number of resources
		this.numberOfResources = resources.length;

		// TODO: set the number of customers
		this.numberOfCustomers = numberOfCustomers;

		// TODO: set the value of bank resources to available
		this.available = resources;

		// TODO: set the array size for maximum, allocation, and need
		this.maximum = new int[numberOfCustomers][numberOfResources];
		this.allocation = new int[numberOfCustomers][numberOfResources];
		this.need = new int[numberOfCustomers][numberOfResources];
	}

	/**
	 * Sets the maximum number of demand of each resource for a customer.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param maximumDemand  An array of the maximum demanded count for each resource.
	 */
	public void setMaximumDemand(int customerIndex, int[] maximumDemand) {
		// TODO: add customer, update maximum and need
		this.maximum[customerIndex] = maximumDemand;
		this.need[customerIndex] = maximumDemand.clone();
	}

	/**
	 * Prints the current state of the bank.
	 */
	public void printState() {
		System.out.println("Current state:");

		// TODO: print available
		System.out.println("Available:");
		String[] strArr = Arrays.stream(this.available).mapToObj(String::valueOf).toArray(String[]::new);
		String printed = String.join(" ",strArr);
		System.out.println(printed);

		// TODO: print maximum
		System.out.println("\nMaximum:");
		for (int[] intArr : this.maximum){
			strArr = Arrays.stream(intArr).mapToObj(String::valueOf).toArray(String[]::new);
			printed = String.join(" ",strArr);
			System.out.println(printed);
		}

		// TODO: print allocation
		System.out.println("\nAllocation:");
		for (int[] intArr : this.allocation){
			strArr = Arrays.stream(intArr).mapToObj(String::valueOf).toArray(String[]::new);
			printed = String.join(" ",strArr);
			System.out.println(printed);
		}
		// TODO: print need
		System.out.println("\nNeed:");
		for (int[] intArr : this.need){
			strArr = Arrays.stream(intArr).mapToObj(String::valueOf).toArray(String[]::new);
			printed = String.join(" ",strArr);
			System.out.println(printed);
		}
	}

	/**
	 * Requests resources for a customer loan.
	 * If the request leave the bank in a safe state, it is carried out.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param request        An array of the requested count for each resource.
	 * @return true if the requested resources can be loaned, else false.
	 */
	public synchronized boolean requestResources(int customerIndex, int[] request) {
		// TODO: print the request
		System.out.println("Customer " + customerIndex + " requesting");
		String[] strArr = Arrays.stream(request).mapToObj(String::valueOf).toArray(String[]::new);
		String printed = String.join(" ", strArr);
		System.out.println(printed);
		// TODO: check if request larger than need
		for(int i = 0; i < this.numberOfResources; i++){
			if (request[i] > this.need[customerIndex][i]) {
				System.out.println("Denied. Request larger than need.");
				System.out.println("Request: " + request[i] + " Need: " + this.need[customerIndex][i]);
				return false;
			}
		}
		// TODO: check if request larger than available
		for(int i = 0; i < this.numberOfResources; i++){
			if (request[i] > this.available[i]) {
				System.out.println("Denied. Request larger than available resources.");
				return false;
			}
		}
		// TODO: check if the state is safe or not
		if(!this.checkSafe(customerIndex, request)){
			System.out.println("Denied. Request will leave system in unsafe state.");
			return false;
		}
		// TODO: if it is safe, allocate the resources to customer customerNumber
		for (int i = 0; i < this.numberOfResources; i++){
			this.allocation[customerIndex][i] += request[i];
			this.need[customerIndex][i] -= request[i];
			this.available[i] -= request[i];
		}
		return true;
	}

	/**
	 * Releases resources borrowed by a customer. Assume release is valid for simplicity.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param release        An array of the release count for each resource.
	 */
	public synchronized void releaseResources(int customerIndex, int[] release) {
		// TODO: print the release
		System.out.println("Customer " + customerIndex + " releasing");
		String[] strArr = Arrays.stream(release).mapToObj(String::valueOf).toArray(String[]::new);
		String printed = String.join(" ", strArr);
		System.out.println(printed);
		// TODO: release the resources from customer customerNumber
		int[] innerAllocation = this.allocation[customerIndex];
		int[] innerNeed = this.need[customerIndex];
		for (int i = 0; i < this.numberOfResources; i++) {
			if (release[i] > innerAllocation[i]) {
				innerNeed[i] += innerAllocation[i];
				innerAllocation[i] = 0;
			} else {
				innerNeed[i] += release[i];
				innerAllocation[i] -= release[i];
			}
		}
	}

	/**
	 * Checks if the request will leave the bank in a safe state.
	 * @param customerIndex  The customer's index (0-indexed).
	 * @param request        An array of the requested count for each resource.
	 * @return true if the requested resources will leave the bank in a
	 *         safe state, else false
	 */
	private synchronized boolean checkSafe(int customerIndex, int[] request) {
		// TODO: check if the state is safe
		int[] temp_avail= new int[this.numberOfResources];
		int[] work = new int[this.numberOfResources];
		int[][] temp_need = new int[this.numberOfCustomers][this.numberOfResources];
		int[][] temp_allocation = new int[this.numberOfCustomers][this.numberOfResources];
		for (int cIndex = 0; cIndex < this.numberOfCustomers; cIndex++) {
			temp_need[cIndex] = this.need[cIndex].clone();
			temp_allocation[cIndex] = this.allocation[cIndex].clone();
		}
		boolean[] finish = new boolean[this.numberOfCustomers]; //initialised as false
		boolean possible = true;

		for (int i = 0; i < numberOfResources; i++) {
			temp_avail[i] = this.available[i] - request[i];
			temp_need[customerIndex][i] = temp_need[customerIndex][i] - request[i];
			temp_allocation[customerIndex][i] = temp_allocation[customerIndex][i] + request[i];
		}
		work = temp_avail;

		while(possible){
			possible = false;
			for (int cIndex = 0; cIndex < this.numberOfCustomers; cIndex++) {
				if (!finish[cIndex]) {
					boolean isResourceAvailable = true;
					for (int rIndex = 0; rIndex < this.numberOfResources; rIndex++) {
						if (temp_need[cIndex][rIndex] > work[rIndex]){
							isResourceAvailable = false;
							break;
						}
					}
					if (isResourceAvailable) {
						possible = true;
						for (int rIndex = 0; rIndex < this.numberOfResources; rIndex++) {
							work[rIndex] += temp_allocation[cIndex][rIndex];
						}
						finish[cIndex] = true;
					}
				}
			}
		}
		for (int cIndex = 0; cIndex < this.numberOfCustomers; cIndex++) {
			if (!finish[cIndex]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Parses and runs the file simulating a series of resource request and releases.
	 * Provided for your convenience.
	 * @param filename  The name of the file.
	 */
	public static void runFile(String filename) {

		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(filename));

			String line = null;
			String [] tokens = null;
			int [] resources = null;

			int n, m;

			try {
				n = Integer.parseInt(fileReader.readLine().split(",")[1]);
			} catch (Exception e) {
				System.out.println("Error parsing n on line 1.");
				e.printStackTrace();
				fileReader.close();
				return;
			}

			try {
				m = Integer.parseInt(fileReader.readLine().split(",")[1]);
			} catch (Exception e) {
				System.out.println("Error parsing n on line 2.");
				e.printStackTrace();
				fileReader.close();
				return;
			}

			try {
				tokens = fileReader.readLine().split(",")[1].split(" ");
				resources = new int[tokens.length];
				for (int i = 0; i < tokens.length; i++)
					resources[i] = Integer.parseInt(tokens[i]);
			} catch (Exception e) {
				System.out.println("Error parsing resources on line 3.");
				e.printStackTrace();
				fileReader.close();
				return;
			}

			Banker theBank = new Banker(resources, n);

			int lineNumber = 4;
			while ((line = fileReader.readLine()) != null) {
				tokens = line.split(",");
				if (tokens[0].equals("c")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.setMaximumDemand(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						System.out.println("Failed setting maximum demand.");
						e.printStackTrace();
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("r")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.requestResources(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						System.out.println("Failed requesting resources.");
						e.printStackTrace();
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("f")) {
					try {
						int customerIndex = Integer.parseInt(tokens[1]);
						tokens = tokens[2].split(" ");
						resources = new int[tokens.length];
						for (int i = 0; i < tokens.length; i++)
							resources[i] = Integer.parseInt(tokens[i]);
						theBank.releaseResources(customerIndex, resources);
					} catch (Exception e) {
						System.out.println("Error parsing resources on line "+lineNumber+".");
						System.out.println("Failed releasing resources.");
						e.printStackTrace();
						fileReader.close();
						return;
					}
				} else if (tokens[0].equals("p")) {
					theBank.printState();
				}
			}
			fileReader.close();
		} catch (IOException e) {
			System.out.println("Error opening: "+filename);
		}

	}

	/**
	 * Main function
	 * @param args  The command line arguments
	 */
	public static void main(String [] args) {
		if (args.length > 0) {
			runFile(args[0]);
		}
	}

}
