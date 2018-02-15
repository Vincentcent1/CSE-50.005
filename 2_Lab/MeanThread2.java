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

		// TODO: partition the array listlist into N subArrays, where N is the number of threads

		int sizeOfSubarray = 1048576/numOfThread;
		int[][] arrayPartition = new int[numOfThread][sizeOfSubarray];

		for (int i = 0; i < numOfThread;i++){
			arrayPartition[i] = Arrays.copyOfRange(array, i*sizeOfSubarray, (i+1)*sizeOfSubarray);
		}

		int processorCount= Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processorCount);

		// TODO: start recording time


		// TODO: create N threads and assign subArrays to the threads so that each thread computes mean of
		    // its repective subarray. For example,

		MeanMultiThread[] arraysOfThread = new MeanMultiThread[numOfThread];

		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i] = new MeanMultiThread(arrayPartition[i]);
		}

		long start = System.nanoTime();
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


		// thread1.start(); //start thread1 on from run() function
		// threadn.start();//start thread2 on from run() function

		// thread1.join();//wait until thread1 terminates
		// threadn.join();//wait until threadn terminates

		// TODO: show the N mean values
		System.out.println("Temporal mean value of thread n is ... ");

		// TODO: store the temporal mean values in a new array so that you can use that
		    /// array to compute the global mean.

		// TODO: compute the global mean value from N mean values.

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
static class MeanMultiThread extends RecursiveAction {
	final int[] array;
	final int low,high;
	double mean;
	MeanMultiThread next;

	MeanMultiThread(int[] array, int low, int high, MeanMultiThread next) {
		this.array = array;
		this.low = low;
		this.high = high;
		this.next = next;
	}

	double computeMean(int low, int high){
		long sum = 0;
		for (int i = low; i < high; i++){
			sum += array[i];
		}
		double mean = (double)sum/array.length;
		return mean;
	}

	@Override
	protected void compute(){
		int l = low;
		int h = high;
		MeanMultiThread right = null;




		int mid = array.length/2;
		invokeAll(new MeanMultiThread(Arrays.copyOfRange(array,0,mid)),
		          new MeanMultiThread(Arrays.copyOfRange(array,mid,array.length))
		          );
		merge(low,mid,high);
	}

	void merge()


	public double getMean() {
		return mean;
	}
	public void run() {
		// TODO: implement your actions here, e.g., computeMean(...)
		mean = computeMean(array);
	}


}





