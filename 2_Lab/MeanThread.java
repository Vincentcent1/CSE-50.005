import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MeanThread {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		// TODO: read data from external file and store it in an array
		       // Note: you should pass the file as a first command line argument at runtime.

		int[] array = int[1048576];

		Scanner sc = new Scanner(new File(args[1]));

		while(sc.hasNextLine()){
			array = sc.nextLine().split("\\s+");
		}





		// define number of threads
		int NumOfThread = Integer.valueOf(args[2]);// this way, you can pass number of threads as
		     // a second command line argument at runtime.
<<<<<<< HEAD
		int sizeOfSubarray = 1048576/numOfThread;

		int processorCount= Runtime.getRuntime().availableProcessors();
		System.out.println("Number of processors: " + processorCount);

		// TODO: start recording time

		long start = System.nanoTime();

		// TODO: create N threads and assign subArrays to the threads so that each thread computes mean of
		    // its repective subarray. For example,

		MeanMultiThread[] arraysOfThread = new MeanMultiThread[numOfThread];

		//Use Static variable to minimize cost of copying array. Partition is also not needed with static variable
		MeanMultiThread.initializeArray(array);


		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i] = new MeanMultiThread(i*sizeOfSubarray,(i + 1)*sizeOfSubarray);
		}

		// MeanMultiThread thread1 = new MeanMultiThread(arrayPartition[0]);
		// MeanMultiThread threadn = new MeanMultiThread(arrayPartition[1]);

=======

		// TODO: partition the array list into N subArrays, where N is the number of threads

		// TODO: start recording time

		// TODO: create N threads and assign subArrays to the threads so that each thread computes mean of
		    // its repective subarray. For example,

		MeanMultiThread thread1 = new MeanMultiThread(subArray1);
		MeanMultiThread threadn = new MeanMultiThread(subArrayn);
>>>>>>> parent of 630f974... MeanThread not optimised
		//Tip: you can't create big number of threads in the above way. So, create an array list of threads.

		// TODO: start each thread to execute your computeMean() function defined under the run() method
		   //so that the N mean values can be computed. for example,
		thread1.start(); //start thread1 on from run() function
		threadn.start();//start thread2 on from run() function

<<<<<<< HEAD
		long startThread = System.nanoTime();
		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i].start();
		}


		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i].join();
		}

		long endThread = System.nanoTime();

		// thread1.start(); //start thread1 on from run() function
		// threadn.start();//start thread2 on from run() function

		// thread1.join();//wait until thread1 terminates
		// threadn.join();//wait until threadn terminates
=======
		thread1.join();//wait until thread1 terminates
		threadn.join();//wait until threadn terminates
>>>>>>> parent of 630f974... MeanThread not optimised

		// TODO: show the N mean values
		System.out.println("Temporal mean value of thread n is ... ");

		// TODO: store the temporal mean values in a new array so that you can use that
		    /// array to compute the global mean.

		// TODO: compute the global mean value from N mean values.

<<<<<<< HEAD
		double globalMean = 0.0;


		for(int i = 0; i < numOfThread; i++){
			globalMean += arraysOfThread[i].getMean();
		}

		globalMean = globalMean/numOfThread;

		// TODO: stop recording time and compute the elapsed time
		long finish = System.nanoTime();


		System.out.println("Time elapsed: " + (finish-start)/1000000.0 + "ms");
		System.out.println("Time required to start the thread: " + (endThread-startThread)/1000000.0 + "ms");

		System.out.println("The global mean value is ... " + globalMean);
=======
		// TODO: stop recording time and compute the elapsed time

		System.out.println("The global mean value is ... ");
>>>>>>> parent of 630f974... MeanThread not optimised

	}
}

//Extend the Thread class
class MeanMultiThread extends Thread {
<<<<<<< HEAD
	private static int[] array;
	private int low;
	private int high;
	private double mean;

	public static void initializeArray(int[] inputArray){
		array = inputArray;
=======
	private ArrayList<Integer> list;
	private double mean;
	MeanMultiThread(ArrayList<Integer> array) {
		list = array;
>>>>>>> parent of 630f974... MeanThread not optimised
	}

	MeanMultiThread(int low, int high) {
		this.low = low;
		this.high = high;
	}

	public double getMean() {
		return mean;
	}
	public void run() {
		// TODO: implement your actions here, e.g., computeMean(...)
		mean = computeMean(array);
	}
<<<<<<< HEAD

	private double computeMean(int[] array){
		long sum = 0;
		int localSize = high - low;
		for (int i = low; i < high;i++){
			sum += array[i];
		}
		double mean = (double)sum/localSize;
		return mean;
	}

=======
>>>>>>> parent of 630f974... MeanThread not optimised
}
