import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.lang.System;
import java.lang.Runtime;


public class MedianThread {

	public static void main(String[] args) throws InterruptedException, FileNotFoundException  {
		// TODO: read data from external file and store it in an array
	       // Note: you should pass the file as a first command line argument at runtime.
		int[] array = new int[1048576];
		String[] stringArray = new String[1048576];
		Scanner sc = new Scanner(new File(args[0]));

		while(sc.hasNextLine()){
			stringArray = sc.nextLine().trim().split("\\s+");
			array = Arrays.stream(stringArray).mapToInt(Integer::parseInt).toArray();
		}


		//TESTING
		// int[] testArray = {7,6,5,4,3,2,0,1};

	// define number of threads
	int numOfThread = Integer.valueOf(args[1]);// this way, you can pass number of threads as
	     // a second command line argument at runtime.

	// TODO: partition the array list into N subArrays, where N is the number of threads

		int sizeOfSubarray = 1048576/numOfThread;
		int[][] arrayPartition = new int[numOfThread][sizeOfSubarray];

		for (int i = 0; i < numOfThread;i++){
			arrayPartition[i] = Arrays.copyOfRange(array, i*sizeOfSubarray, (i+1)*sizeOfSubarray);
		}


	// TODO: start recording time

		long start = System.nanoTime();

	// TODO: create N threads and assign subArrays to the threads so that each thread sorts
	    // its repective subarray. For example,
		MedianMultiThread[] arraysOfThread = new MedianMultiThread[numOfThread];

		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i] = new MedianMultiThread(i*sizeOfSubarray, (i+1)*sizeOfSubarray);
		}

	//Tip: you can't create big number of threads in the above way. So, create an array list of threads.

	// TODO: start each thread to execute your sorting algorithm defined under the run() method, for example,
		MedianMultiThread.initializeArray(array);
		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i].start();
		}

		for (int i = 0; i < numOfThread; i++){
			arraysOfThread[i].join();
		}

	// // TODO: use any merge algorithm to merge the sorted subarrays and store it to another array, e.g., sortedFullArray.
		MedianMultiThread util = new MedianMultiThread(0,0);
		// util.mergeSort(0,8);
		int offset = sizeOfSubarray;

		while (offset != 1048576){
			for (int i = 0; i < 1048576/offset;i+=2){
				util.merge(i*offset, (i+1)*offset, (i+2)*offset);
			}
			offset*=2;
		}






	//TODO: get median from sortedFullArray
		array = Arrays.copyOfRange(util.getInternal(),0,1048576);

		int median = (array[524287] + array[524288])/2;

	// TODO: stop recording time and compute the elapsed time

		long finish = System.nanoTime();


	// TODO: printout the final sorted array
		for (int i = 0; i < 262144;i++){
			// System.out.println("" + array[4*i] + " " + array[4*i + 1] + " " + array[4*i+2] + " " + array[4*i+3]);
		}
		// for (int i = 0; i < 8;i+=4){
		// 	System.out.println("" + testArray[i] + " " + testArray[i+1] + " " + testArray[i+2] + " " + testArray[i+3]);
		// }

		long runningTime = finish-start;

	// TODO: printout median
	System.out.println("The Median value is: " + median);
	System.out.println("Running time is " + runningTime/1000000 + " milliseconds\n");

	}

	// public static double computeMedian(ArrayList<Integer> inputArray) {
	//   //TODO: implement your function that computes median of values of an array
	// }

}

// extend Thread
class MedianMultiThread extends Thread {
	private static int[] array;
	private int low;
	private int high;

	public static void initializeArray(int[] newArray){
		array = newArray;
	}

	public int[] getInternal() {
		return array;
	}

	MedianMultiThread(int low, int high) {
		this.low = low;
		this.high = high;
	}

	public void run() {
		// called by object.start()
		mergeSort(low,high);

	}

	public void merge(int low, int mid, int high) {
		// System.out.println("" + low + " " + mid + " " + high);
		int[] left = Arrays.copyOfRange(array, low, mid);
		int[] right = Arrays.copyOfRange(array, mid, high);
		int arrayPointer = low;
		int leftCount = 0;
		int rightCount = 0;

		while (leftCount < left.length && rightCount < right.length){
			if (left[leftCount] <= right[rightCount]){
				array[arrayPointer] = left[leftCount];
				leftCount++;
			} else {
				array[arrayPointer] = right[rightCount];
				rightCount++;

			}
			arrayPointer++;
		}

		while (leftCount < left.length){
			array[arrayPointer] = left[leftCount];
			leftCount++;
			arrayPointer++;
		}

		while (rightCount < right.length){
			array[arrayPointer] = right[rightCount];
			rightCount++;
			arrayPointer++;
		}
		return;
	}

	// TODO: implement merge sort here, recursive algorithm
	public void mergeSort(int low, int high) {
		if((high-low)<= 1){
			return;
		} else {
			int mid = (low + high)/2;
			mergeSort(low, mid);
			mergeSort(mid, high);
			merge(low, mid, high);
			return;
		}
	}
}
