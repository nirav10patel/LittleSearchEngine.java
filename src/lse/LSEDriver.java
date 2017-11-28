package lse;

import java.io.*;
import java.util.*;

public class LSEDriver 
{
	public static void main (String[]args) throws FileNotFoundException
	{
		LittleSearchEngine LSE = new LittleSearchEngine();
		LSE.noiseWords = new HashSet<String>();
		Scanner sc = new Scanner(new File("noisewords.txt"));
		while (sc.hasNext())
		{
			LSE.noiseWords.add(sc.next());
		}
		sc.close();
		
		HashMap<String,Occurrence> Alice = LSE.loadKeywordsFromDocument("AliceCh1.txt");
		HashMap<String,Occurrence> Wow = LSE.loadKeywordsFromDocument("WowCh1.txt");
		HashMap<String,Occurrence> Alice2 = LSE.loadKeywordsFromDocument("Alice2");
		

		
		//System.out.println(Alice5);
		System.out.println(Wow);
		
		LSE.mergeKeywords(Alice);
		LSE.mergeKeywords(Wow);
		LSE.mergeKeywords(Alice2);
	
		
		System.out.println(LSE.top5search("discussed","half"));
		
		//getKeyword check
		/*
				System.out.println(LSE.getKeyword("distance."));
				System.out.println(LSE.getKeyword("equi-distant"));
				System.out.println(LSE.getKeyword("Rabbit"));
				System.out.println(LSE.getKeyword("Between"));
				System.out.println(LSE.getKeyword("we're"));
				System.out.println(LSE.getKeyword("World..."));
				System.out.println(LSE.getKeyword("World?!"));
				System.out.println(LSE.getKeyword("What,ever"));
				System.out.println(LSE.getKeyword("..."));
				System.out.println(LSE.getKeyword("about"));
				*/
	}
}
