package prj02;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import HashTable.*;
import List.*;
import SortedList.*;
import Tree.*;


/**
 * The Huffman Encoding Algorithm
 *
 * This is a data compression algorithm designed by David A. Huffman and published in 1952
 *
 * What it does is it takes a string and by constructing a special binary tree with the frequencies of each character.
 * This tree generates special prefix codes that make the size of each string encoded a lot smaller, thus saving space.
 *
 * @author Fernando J. Bermudez Medina (Template)
 * @author A. ElSaid (Review)
 * @author Eliel J. Hernandez Vega 844196970 (Implementation)
 * @version 2.0
 * @since 10/16/2021
 */
public class HuffmanCoding {
	
	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		String data = load_data("input2.txt"); //You can create other test input files and add them to the inputData Folder

		/*If input string is not empty we can encode the text using our algorithm*/
		if(!data.isEmpty()) {
			Map<String, Integer> fD = compute_fd(data);
			BTNode<Integer,String> huffmanRoot = huffman_tree(fD);
			Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
			String output = encode(encodedHuffman, data);
			process_results(fD, encodedHuffman,data,output);
		} else {
			System.out.println("Input Data Is Empty! Try Again with a File that has data inside!");
		}

	}

	/**
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 *
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/*If input file is empty just return an empty string, if not just extract the data*/
			String extracted = in.readLine();
			if(extracted != null)
				line = extracted;

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	/**
	 * Receives in a parameter an input string, traverses the input string and 
	 * returns a map with the frequency distribution for each character.
	 *
	 * @param inputString an input String to be traverse and get the frequency of each character of the string
	 * @return Map with the frequency of each character in the input string
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		Map<String, Integer> map = new HashTableSC<>(new SimpleHashFunction<String>());

		/*Loops through every character in the string and adds it to the map with its calculated frequency*/
		for(int k = 0; k < inputString.length(); k++) {
			/*If the map already contains the character as a key, we just add one to the frequency of the character*/
			if(map.containsKey(String.valueOf(inputString.charAt(k)))) {
				map.put(String.valueOf(inputString.charAt(k)), map.get(String.valueOf(inputString.charAt(k))) + 1);
			}
			/*If the map does not contains the character, we add the character to the map with one as the initial frequency*/
			else{map.put(String.valueOf(inputString.charAt(k)), 1);}
		}

		return map;
	}


	/**
	 * Receives a map with the frequency distribution of the characters, 
	 * builds the tree assigning the left child, right child, and parents 
	 * to each node, and returns the root of the tree.
	 *
	 * @param fD Map with the frequency distribution of each character
	 * @return BTNode the root of the tree
	 */
	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> fD) {
		BTNode<Integer, String> rootNode = new BTNode<Integer, String>();
		List<String> c = fD.getKeys(); //List with keys in the fD map
		SortedLinkedList<BTNode<Integer, String>> L = new SortedLinkedList<BTNode<Integer, String>>(); //Empty SortedLinkedList

		/*Loops through each key in the map and adds them in the SortedLinkedList*/
		for(String k: c) {
			L.add(new BTNode<Integer, String>(fD.get(k), k));
		}

		/*Constructs the tree*/
		while(true) {
			if(L.size() == 1) {
				break;
			}

			/*Gets the first two elements of the SortedLinkedList*/
			BTNode<Integer, String> node1 = L.get(0);
			BTNode<Integer, String> node2 = L.get(1);

			/*Creates a new node, that is equal to the sum of the first two elements of the SortedLinkedList*/
			BTNode<Integer, String> newnode = new BTNode<Integer, String>(node1.getKey() + node2.getKey(), node1.getValue() + node2.getValue());
			rootNode = newnode;

			/*If the first two elements of the SortedLinkedList have the same frequency, the element with the highest character goes to the right child of the newnode*/
			if(node1.getKey().compareTo(node2.getKey()) == 0) {
				if(node1.getValue().compareTo(node2.getValue()) > 0) {
					newnode.setLeftChild(node2);
					newnode.setRightChild(node1);
				}
				else {
					newnode.setLeftChild(node1);
					newnode.setRightChild(node2);
				}
			}
			/*If the elements does not have the same frequency, assigns the first node to the left child of the newnode and the second node to the right child of the newnode*/
			else {
				newnode.setLeftChild(node1);
				newnode.setRightChild(node2);
			}

			/*Set the parent of the first two elements of the list*/
			node1.setParent(newnode);
			node2.setParent(newnode);

			/*Remove the first two elements and add the newnode to the SortedLinkedList*/
			L.remove(node1);
			L.remove(node2);
			L.add(newnode);
		}
		return rootNode; 
	}

	


	/**
	 * Receives the root of the Huffman tree, and returns a mapping of every symbol 
	 * to its corresponding Huffman code.
	 *
	 * @param huffmanRoot BTNode the root of the Huffman tree
	 * @return Map with every symbol and its corresponding Huffman code
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> huffmanRoot) {
		Map<String, String> map = new HashTableSC<>(new SimpleHashFunction<String>());

		/*Calls helper function that makes the mapping*/
		huffman_code_helper(map, huffmanRoot);
		
		return map; 
	}

	/**
	 * Receives the Huffman code map and the input string, traverses the input 
	 * string and for every character adds the code of the character to the 
	 * final code and returns the encoded string.
	 *
	 * @param encodingMap Map with the code for every symbol
	 * @param inputString String to be encoded
	 * @return String the encoded string
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) {
		String code = "";
		
		/*Loops through the input string and for every character add the code of the character to the final code*/
		for(int k = 0; k < inputString.length(); k++) {
			code += encodingMap.get(String.valueOf(inputString.charAt(k)));
		}

		return code; 
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable, the input string,
	 * and the output string, and prints the results to the screen (per specifications).
	 *
	 * Output Includes: symbol, frequency and code.
	 * Also includes how many bits has the original and encoded string, plus how much space was saved using this encoding algorithm
	 *
	 * @param fD Frequency Distribution of all the characters in input string
	 * @param encodedHuffman Prefix Code Map
	 * @param inputData text string from the input file
	 * @param output processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String output) {
		/*To get the bytes of the input string, we just get the bytes of the original string with string.getBytes().length*/
		int inputBytes = inputData.getBytes().length;

		/**
		 * For the bytes of the encoded one, it's not so easy.
		 *
		 * Here we have to get the bytes the same way we got the bytes for the original one but we divide it by 8,
		 * because 1 byte = 8 bits and our huffman code is in bits (0,1), not bytes.
		 *
		 * This is because we want to calculate how many bytes we saved by counting how many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes will give us how much space we "chopped off"
		 *
		 * So we have to subtract that "chopped off" percentage to the total (which is 100%)
		 * and that's the difference in space required
		 */
		String savings =  d.format(100 - (( (float) (outputBytes / (float)inputBytes) ) * 100));


		/**
		 * Finally we just output our results to the console
		 * with a more visual pleasing version of both our Hash Tables in decreasing order by frequency.
		 *
		 * Notice that when the output is shown, the characters with the highest frequency have the lowest amount of bits.
		 *
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer,String>> sortedList = new SortedLinkedList<BTNode<Integer,String>>();

		/* To print the table in decreasing order by frequency, we do the same thing we did when we built the tree
		 * We add each key with it's frequency in a node into a SortedList, this way we get the frequencies in ascending order*/
		for (String key : fD.getKeys()) {
			BTNode<Integer,String> node = new BTNode<Integer,String>(fD.get(key),key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order,
		 * we just traverse the list backwards and start printing the nodes key (character) and value (frequency)
		 * and find the same key in our prefix code "Lookup Table" we made earlier on in huffman_code().
		 *
		 * That way we get the table in decreasing order by frequency
		 * */
		for (int i = sortedList.size() - 1; i >= 0; i--) {
			BTNode<Integer,String> node = sortedList.get(i);
			System.out.println(node.getValue() + "\t" + node.getKey() + "\t    " + encodedHuffman.get(node.getValue()));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output);
		System.out.println("Decoded String: \n" + decodeHuff(output, encodedHuffman) + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + (int) outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}


	/*************************************************************************************
	 ** ADD ANY AUXILIARY METHOD YOU WISH TO IMPLEMENT TO FACILITATE YOUR SOLUTION HERE **
	 *************************************************************************************/

	/**
	 * Auxiliary Method that decodes the generated string by the Huffman Coding Algorithm
	 *
	 * Used for output Purposes
	 *
	 * @param output - Encoded String
	 * @param lookupTable
	 * @return The decoded String, this should be the original input string parsed from the input file
	 */
	public static String decodeHuff(String output, Map<String, String> lookupTable) {
		String result = "";
		int start = 0;
		List<String>  prefixCodes = lookupTable.getValues();
		List<String> symbols = lookupTable.getKeys();

		/*looping through output until a prefix code is found on map and
		 * adding the symbol that the code that represents it to result */
		for(int i = 0; i <= output.length();i++){

			String searched = output.substring(start, i);

			int index = prefixCodes.firstIndex(searched);

			if(index >= 0) { //Found it
				result= result + symbols.get(index);
				start = i;
			}
		}
		return result;
	}


	/**
	 * Receives the root of the Huffman tree and a map, the function traverses
	 * the huffman tree searching for leaf nodes and for every leaf node constructs 
	 * the code for the symbol and adds the symbol with its corresponding 
	 * huffman code to the map.
	 *
	 * @param map Map the map to add the symbols with its corresponding huffman code
	 * @param node BTNode initialy the root of the tree
	 */ 
	public static void huffman_code_helper(Map<String, String> map, BTNode<Integer, String> node) {
		BTNode<Integer, String> tempnode = node;
		boolean isleaf = false;

		/*Verify if the node is a leaf node*/
		if(node.getLeftChild() == null && node.getRightChild() == null) {
			isleaf = true;
		}

		/*If the node is a leaf node, construct the code for the symbol of the node*/
		if(isleaf) {
			/*Starting from the leaf node it goes trough the parent of each node until it gets to the root*/
			while(tempnode.getParent() != null) {
				/*If the tempnode is a left child, add a cero to the code*/
				if(tempnode.getParent().getLeftChild().equals(tempnode)) {
					/*If the map contains the symbol, just add the "0" to the existing code*/
					if(map.containsKey(node.getValue())) {
						String tempcode = "0" + map.get(node.getValue());
						map.put(node.getValue(), tempcode);
					}
					/*If the map does not contains the symbol, add the symbol and the "0" to the map*/
					else {
						map.put(node.getValue(), "0");
					}
				}
				/*If the tempnode is a right child, add a one to the code*/
				else if(tempnode.getParent().getRightChild().equals(tempnode)) {
					/*If the map contains the symbol, just add the "1" to the existing code*/
					if(map.containsKey(node.getValue())) {
						String tempcode = "1" + map.get(node.getValue());
						map.put(node.getValue(), tempcode);
					}
					/*If the map does not contains the symbol, add the symbol and the "0" to the map*/
					else {
						map.put(node.getValue(), "1");
					}
				}

				tempnode = tempnode.getParent();
			}
		}

		/*If the left child of the node is not null, calls the function again with the left child as the node*/
		if(node.getLeftChild() != null) {
			huffman_code_helper(map, node.getLeftChild());
		}
		/*If the right child of the node is not null, calls the function again with the right child as the node*/
		if(node.getRightChild() != null) {
			huffman_code_helper(map, node.getRightChild());
		}
	}
}
