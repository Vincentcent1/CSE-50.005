import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class FileOperation {
	private static File currentDirectory = new File(System.getProperty("user.dir"));
	public static void main(String[] args) throws java.io.IOException {

		String commandLine;

		BufferedReader console = new BufferedReader
				(new InputStreamReader(System.in));

		while (true) {
			// read what the user entered
			System.out.print("jsh>");
			commandLine = console.readLine();

			// clear the space before and after the command line
			commandLine = commandLine.trim();

			// if the user entered a return, just loop again
			if (commandLine.equals("")) {
				continue;
			}
			// if exit or quit
			else if (commandLine.equalsIgnoreCase("exit") | commandLine.equalsIgnoreCase("quit")) {
				System.exit(0);
			}

			// check the command line, separate the words
			String[] commandStr = commandLine.split(" ");
			ArrayList<String> command = new ArrayList<String>();
			for (int i = 0; i < commandStr.length; i++) {
				command.add(commandStr[i]);
			}

			if (commandStr[0].equalsIgnoreCase("create")) {
			// TODO: implement code to handle create here
				Java_create(currentDirectory, commandStr[1]);
			} else if (commandStr[0].equalsIgnoreCase("delete")) {
			// TODO: implement code to handle delete here
				Java_delete(currentDirectory, commandStr[1]);
			} else if (commandStr[0].equalsIgnoreCase("display")) {
			// TODO: implement code to handle display here
				Java_cat(currentDirectory, commandStr[1]);
			} else if (commandStr[0].equalsIgnoreCase("list")) {
			// TODO: implement code to handle list here
				if (commandStr.length == 1) {
					Java_ls(currentDirectory, "", "");				
				} else if (commandStr.length == 2) {
					Java_ls(currentDirectory, commandStr[1], "");							
				} else if (commandStr.length == 3){
					Java_ls(currentDirectory, commandStr[1], commandStr[2]);							
				} else {
					System.out.println("Invalid number of arguments for list");
					continue;
				}
			} else if (commandStr[0].equalsIgnoreCase("find")) {
			// TODO: implement code to handle find here
				Java_find(currentDirectory, commandStr[1]);
				
			} else if (commandStr[0].equalsIgnoreCase("tree")) {
			// TODO: implement code to handle tree here
				if (commandStr.length == 1) {
					Java_tree(currentDirectory, 1, "");				
				} else if (commandStr.length == 2 && commandStr[1].matches("\\d+")) {
					Java_tree(currentDirectory, Integer.valueOf(commandStr[1]), "");							
				} else if (commandStr.length == 3 && commandStr[1].matches("\\d+")){
					Java_tree(currentDirectory, Integer.valueOf(commandStr[1]), commandStr[2]);							
				} else {
					System.out.println("Invalid arguments for tree");
					continue;
				}

				
			} else {
			// other commands
				ProcessBuilder pBuilder = new ProcessBuilder(command);
				pBuilder.directory(currentDirectory);
				try{
					Process process = pBuilder.start();
					// obtain the input stream
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader br = new BufferedReader(isr);

					// read what is returned by the command
					String line;
					while ( (line = br.readLine()) != null)
						System.out.println(line);

					// close BufferedReader
					br.close();
				}
				// catch the IOexception and resume waiting for commands
				catch (IOException ex){
					System.out.println(ex);
					continue;
				}
			}
		}
	}

	/**
	 * Create a file
	 * @param dir - current working directory
	 * @param command - name of the file to be created
	 */
	public static void Java_create(File dir, String name) {
		// TODO: create a file
		try {
			File newFile = new File(dir, name);
			newFile.createNewFile();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("IO Exception caught");
		}
	}

	/**
	 * Delete a file
	 * @param dir - current working directory
	 * @param name - name of the file to be deleted
	 */
	public static void Java_delete(File dir, String name) {
		// TODO: delete a file
		File newFile = new File(dir, name);
		newFile.delete();
	}

	/**
	 * Display the file
	 * @param dir - current working directory
	 * @param name - name of the file to be displayed
	 */
	public static void Java_cat(File dir, String name) {
		// TODO: display a file
		File catFile = new File(dir, name);
		if (catFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(catFile));
				String line = "";
				while((line = br.readLine()) != null){
					System.out.println(line);
				}
			} catch (FileNotFoundException ex){
				ex.printStackTrace();
				System.out.println("FileNotFoundException caught");
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("IO Exception caught");
			} 
		} else {
			System.out.println("File \"" + name + "\" not found");
		}
	}

	/**
	 * Function to sort the file list
	 * @param list - file list to be sorted
	 * @param sort_method - control the sort type
	 * @return sorted list - the sorted file list
	 */
	private static File[] sortFileList(File[] list, String sort_method) {
		// sort the file list based on sort_method
		// if sort based on name
		if (sort_method.equalsIgnoreCase("name")) {
			Arrays.sort(list, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return (f1.getName()).compareTo(f2.getName());
				}
			});
		}
		else if (sort_method.equalsIgnoreCase("size")) {
			Arrays.sort(list, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.length()).compareTo(f2.length());
				}
			});
		}
		else if (sort_method.equalsIgnoreCase("time")) {
			Arrays.sort(list, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
				}
			});
		}
		return list;
	}

	/**
	 * List the files under directory
	 * @param dir - current directory
	 * @param display_method - control the list type
	 * @param sort_method - control the sort type
	 */
	public static void Java_ls(File dir, String display_method, String sort_method) {
		// TODO: list files
		File[] fileLists = dir.listFiles();
		if (display_method.equals("")) {
			for (File f : fileLists){
				System.out.println(f.getName());
			}
		} else if (display_method.equalsIgnoreCase("property")) {
			fileLists = sortFileList(fileLists, sort_method);
			int maxLength = 0;
			for (File f : fileLists ){
				String s = f.getName();
				if (s.length() > maxLength) {
					maxLength = s.length();
				}
			}
			String format = "%-" + maxLength + "s\t%s\t%s%n";
			for (File f : fileLists){
				Date date = new Date(f.lastModified());
				System.out.printf(format, f.getName(),f.length(),date.toString());
			}
		} else {
			for (File f : fileLists){
				System.out.println(f.getName());
			}			
			System.out.println("\nUnknown option \'" + display_method + "\'");
		}
	}	

	/**
	 * Find files based on input string
	 * @param dir - current working directory
	 * @param name - input string to find in file's name
	 * @return flag - whether the input string is found in this directory and its subdirectories
	 */
	public static boolean Java_find(File dir, String name) {
		boolean flag = false;
		File[] fileLists = dir.listFiles();
		for (File f: fileLists){
			if (f.isDirectory()) {
				Java_find(f,name);
			} else {
				if (f.getName().contains(name)) {
					System.out.println(f.getAbsolutePath());
				}
			}
		}
		// TODO: find files
		return flag;
	}

	/**
	 * Print file structure under current directory in a tree structure
	 * @param dir - current working directory
	 * @param depth - maximum sub-level file to be displayed
	 * @param sort_method - control the sort type
	 */
	public static void Java_tree(File dir, int depth, String sort_method) {
		// TODO: print file tree
		Java_tree_recurse(dir,depth,sort_method,depth);
	}	
	public static void Java_tree_recurse(File dir, int depth, String sort_method, int deepest) {
		// TODO: print file tree
		String prefixes = "";
		for (int i = 0; i < deepest - depth; i++) {
			prefixes += "  ";
		}
		if (deepest - depth != 0) {
			prefixes += "|-";
		}
		File[] fileLists = dir.listFiles();
		fileLists = sortFileList(fileLists, sort_method);
		for (File f: fileLists){
			if (f.isDirectory()) {
				System.out.println(prefixes + f.getName());
				if (depth > 1) {
					Java_tree_recurse(f, depth - 1, sort_method, deepest);
				}

			} else {
				System.out.println(prefixes + f.getName());
			}
		}
	}

	// TODO: define other functions if necessary for the above functions

}
