import java.io.FileNotFoundException;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.lang.System;
import java.lang.Runtime;


public class MeanThread {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		// TODO: read data from external file and store it in an array
		       // Note: you should pass the file as a first command line argument at runtime.

		int[] array = new int[1048576];
		String[] stringArray = new String[1048576];
		Scanner sc = new Scanner(new File(args[0]));

		while(sc.hasNextLine()){
			stringArray = sc.nextLine().trim().split("\\s+");
			array = Arrays.stream(stringArray).mapToInt(Integer::parseInt).toArray();


		}





		// define number of threads
		int numOfThread = Integer.valueOf(args[1]);// this way, you can pass number of threads as
		     // a second command line argument at runtime.

		// TODO: partition the array list into N subArrays, where N is the number of threads

		int sizeOfSubarray = 1048576/numOfThread;
		int[][] arrayPartition = new int[numOfThread][sizeOfSubarray];

		for (int i = 0; i < numOfThread;i++){
			arrayPartition[i] = Arrays.copyOfRange(array, i*sizeOfSubarray, (i+1)*sizeOfSubarray);
		}

		int processorCount= Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processorCount);

		// TODO: start recording time

		long start = System.nanoTime();

		// TODO: create N threads and assign subArrays to the threads so that each thread computes mean of
		    // its repective subarray. For example,

		MeanMultiThread[] arraysOfThread = new MeanMultiThread[numOfThread];

		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i] = new MeanMultiThread(arrayPartition[i]);
		}

		// MeanMultiThread thread1 = new MeanMultiThread(arrayPartition[0]);
		// MeanMultiThread threadn = new MeanMultiThread(arrayPartition[1]);

		//Tip: you can't create big number of threads in the above way. So, create an array list of threads.

		// TODO: start each thread to execute your computeMean() function defined under the run() method
		   //so that the N mean values can be computed. for example,

		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i].start();
		}

		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i].join();
		}

		// TODO: show the N mean values
		System.out.println("Temporal mean value of thread n is ... ");

		String meanString = "";

		for(int i = 0; i < numOfThread; i++){
			meanString += arraysOfThread[i].getMean() + " ";
		}

		System.out.println(meanString);


		double globalMean = 0.0;

		for(int i = 0; i < numOfThread; i++){
			globalMean += arraysOfThread[i].getMean();
		}

		globalMean = globalMean/numOfThread;

		// TODO: stop recording time and compute the elapsed time

		long finish = System.nanoTime();

		System.out.println("Time elapsed: " + (finish-start)/1000000.0 + "ms");

		System.out.println("The global mean value is ... " + globalMean);

	}
}

//Extend the Thread class
class MeanMultiThread extends Thread {
	private int[] list;
	private double mean;

	MeanMultiThread(int[] array) {
		this.list = array;
	}

	public double getMean() {
		return mean;
	}
	public void run() {
		// TODO: implement your actions here, e.g., computeMean(...)
		mean = computeMean(list);
	}

	private double computeMean(int[] list){
		long sum = 0;
		for (int i = 0; i < list.length;i++){
			sum += list[i];
		}
		double mean = (double)sum/list.length;
		return mean;
	}

}
