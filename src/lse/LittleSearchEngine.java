package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() 
	{
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException 
	{
		HashMap<String,Occurrence> map = new HashMap<String,Occurrence>();
		String word = "";
		File doc = new File (docFile);
		
		
		if (!doc.isFile())
		{
			throw new FileNotFoundException ("No such file exists");
		}
		
		Scanner sc = new Scanner(doc);
		while (sc.hasNext()) 
		{
			word = getKeyword(sc.next());
			if (!map.containsKey(word))
			{
				Occurrence numTimes = new Occurrence(null,0);
				numTimes.document = docFile;
				numTimes.frequency = 1;
				map.put(word,numTimes);
			}
			else
			{
				Occurrence numTimes = new Occurrence(null,0);
				numTimes = map.get(word);
				numTimes.frequency++;
				map.put(word, numTimes);
			}
		}
		sc.close();
		return map;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws)
	{
		Iterator <String> keys = kws.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next();
			if(!keywordsIndex.containsKey(key))
			{
				ArrayList<Occurrence> newOccs = new ArrayList <Occurrence> ();
				newOccs.add(kws.get(key));
				keywordsIndex.put(key,newOccs);
			}
			else
			{
				ArrayList<Occurrence> newOccs = keywordsIndex.get(key);
				Occurrence occ = kws.get(key);
				newOccs.add(occ);
				insertLastOccurrence(newOccs);
				keywordsIndex.put(key, newOccs);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) 
	{
		String newWord = "";
		boolean warning = false;
		for (int i = 0; i < word.length(); i++)
		{
			if (!warning)
			{
				if (!Character.isLetter(word.charAt(i)))
				{
					warning = true;
				}
				else
				{
					newWord = newWord + word.charAt(i);
				}
			}
			else
			{
				if (Character.isLetter(word.charAt(i)))
				{
					return null;
				}
			}
			
		}
		
		newWord = newWord.toLowerCase();
		if (noiseWords.contains(newWord))
		{
			return null;
		}
		
		if (newWord.length() == 0)
		{
			return null;
		}
		return newWord;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) 
	{
		if (occs.size() <= 1)
		{
			return null;
		}
		
		ArrayList <Integer> midpoints = new ArrayList<Integer>();
		Occurrence lastOcc = occs.get(occs.size()-1);
		int lastFreq = occs.get(occs.size()-1).frequency;
		occs.remove(occs.get(occs.size()-1));
		
		int arrSize = occs.size();
		int begin = 0;
		int end = occs.size()-1;
		int mid = (begin + end)/2;
		int midFreq = 0;
		int addIndex = 0;
		
		while(mid < arrSize)
		{
			midpoints.add(mid);
			midFreq = occs.get(mid).frequency;
			if(begin == end || begin + 1 == end)
			{
				addIndex = begin;
				break;
			}
			if(midFreq == lastFreq)
			{
				addIndex = mid;
				break;
			}
			else if(midFreq < lastFreq)
			{
				end = mid - 1;
			}	
			else
			{
				begin = mid + 1;
			}
			mid = (begin+end)/2;
		}
		occs.add(addIndex,lastOcc);
		return midpoints;
		
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) 
	{
		ArrayList<String> results = new ArrayList<String>();
		ArrayList <Occurrence> Occ1 = new ArrayList<Occurrence>();
		ArrayList <Occurrence> Occ2 = new ArrayList<Occurrence>();
		Occurrence temp1;
		Occurrence temp2;
		boolean oneNull = false;
		
		if (!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2))
		{
			return null;
		}
		Occ1 = keywordsIndex.get(kw1);
		Occ2 = keywordsIndex.get(kw2);
		
		if(Occ1 == null)
		{
			temp1 = null;
		}
		else
		{
			temp1 = Occ1.get(0);
		}
		
		if(Occ2 == null)
		{
			temp2 = null;
		}
		else
		{
			temp2 = Occ2.get(0);
		}
		
		if (temp1 == null && temp2 == null)
		{
			return null;
		}
		else if (temp1 == null || temp2 == null)
		{
			oneNull = true;
		}
		
		while(true)
		{
			if (results.size() == 5)
			{
				break;
			}
			if (oneNull)
			{
				if (temp1 == null)
				{
					if(results.isEmpty())
					{
						results.add(temp2.document);
						Occ2.remove(0);
						if(Occ2.isEmpty())
						{
							break;
						}
						temp2 = Occ2.get(0);
					}
					else
					{
						if (!results.contains(temp2.document))
						{
							results.add(temp2.document);
							Occ2.remove(0);
							if(Occ2.isEmpty())
							{
								break;
							}
							temp2 = Occ2.get(0);
						}
					}
				}
				else
				{
					if(results.isEmpty())
					{
						results.add(temp1.document);
						Occ1.remove(0);
						if(Occ1.isEmpty())
						{
							break;
						}
						temp1 = Occ1.get(0);
					}
					else
					{
						if (!results.contains(temp1.document))
						{
							results.add(temp1.document);
							Occ1.remove(0);
							if(Occ1.isEmpty())
							{
								break;
							}
							temp1 = Occ1.get(0);
						}
					}
				}
			}
			else
			{
				if(temp1.frequency >= temp2.frequency)
				{
					if(results.isEmpty())
					{
						results.add(temp1.document);
						Occ1.remove(0);
						if(Occ1.isEmpty())
						{
							break;
						}
						temp1 = Occ1.get(0);
					}
					else
					{
						if (!results.contains(temp1.document))
						{
							results.add(temp1.document);
							Occ1.remove(0);
							if(Occ1.isEmpty())
							{
								break;
							}
							temp1 = Occ1.get(0);
						}
					}
				}
				else
				{
					if(results.isEmpty())
					{
						results.add(temp2.document);
						Occ2.remove(0);
						if(Occ2.isEmpty())
						{
							break;
						}
						temp2 = Occ2.get(0);
					}
					else
					{
						if (!results.contains(temp2.document))
						{
							results.add(temp2.document);
							Occ2.remove(0);
							if(Occ2.isEmpty())
							{
								break;
							}
							temp2 = Occ2.get(0);
						}
					}
				}
			}
		}
		return results;
	}
}