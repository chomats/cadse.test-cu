/**
 *
 */
package test.fede.workspace.domain.internal;

import java.util.HashSet;
import java.util.Random;

public class GeneratorName {
	HashSet<char[]> generated = new HashSet<char[]>();
	char[]	prefix	= null;
	
	
	Random random  = new Random();
	static int LENGTH_A_Z = 'z'-'a'+1;
	
	public String newName() {
		while (true){
			char[] word = newWord();
			if (generated.contains(word)) continue;
			generated.add(word);
			return new String(word);
		}
	}
	
	public String newName(char[] acceptFirst, char[] acceptNext, int flag, int minLength, int maxLength) {
		while (true){
			char[] word = newWord(acceptFirst, acceptNext, flag, minLength, maxLength);
			if (generated.contains(word)) continue;
			generated.add(word);
			return new String(word);
		}
	}

	private char[] newWord() {
		return newWord(null, null, F_FIRST_LOWER | F_FIRST_UPPER | F_NEXT_LOWER | F_NEXT_UPPER, 1, 25);
	}
	static int F_FIRST_UPPER = 0x0001;
	static int F_FIRST_LOWER = 0x0002;
	static int F_NEXT_LOWER = 0x0004;
	static int F_NEXT_UPPER = 0x0008;
	
	private char[] newWord(char[] acceptFirst, char[] acceptNext, int flag, int minLength, int maxLength) {
		int nb = minLength+random.nextInt(maxLength-minLength+1);
		char[] word = new char[nb];
		int i = 0;
	    while(true) {
			int c = random.nextInt(3);
			int v = 0;
			if (i == 0) {
				if (c == 0) {
					if ((flag & F_FIRST_UPPER) == 0) continue;
					v = 'A' + random.nextInt(LENGTH_A_Z);
				} else if (c == 1) {
					if ((flag & F_FIRST_LOWER) == 0) continue;
					v = 'a' + random.nextInt(LENGTH_A_Z);
				} else {
					if (acceptFirst ==  null) continue;
					v = acceptFirst[random.nextInt(acceptFirst.length)];
				}
			} else {
				if (c == 0) {
					if ((flag & F_NEXT_UPPER) == 0) continue;
					v = 'A' + random.nextInt(LENGTH_A_Z);
				} else if (c == 1) {
					if ((flag & F_NEXT_LOWER) == 0) continue;
					v = 'a' + random.nextInt(LENGTH_A_Z);
				} else {
					if (acceptNext ==  null) continue;
					v = acceptNext[random.nextInt(acceptNext.length)];
				}
			}
			
			word[i] = (char) v;
			i++;
			if (i == nb) return word;
		}
	}

	public String newPackageName(int bn) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bn; i++) {
			if (i != 0) sb.append('.');
			sb.append(newName(null, null, F_FIRST_LOWER|F_NEXT_UPPER|F_NEXT_LOWER, 2, 10));
		}
		return sb.toString();
	}
	
	public String newPackageName(int min_elt, int max_elt) {
		int bn = min_elt + random.nextInt(max_elt - min_elt +1);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bn; i++) {
			if (i != 0) sb.append('.');
			sb.append(newName(null, null, F_FIRST_LOWER|F_NEXT_UPPER|F_NEXT_LOWER, 2, 10));
		}
		return sb.toString();
	}
	public String newLowerName(int l) {
		if (prefix == null || prefix.length == 0) {
			prefix = new char[] { 'a' };
			return new String(prefix);
		}
		for (int i = 0; i < prefix.length; i++) {
			if (Character.isLowerCase(prefix[i] + 1)) {
				prefix[i]++;
				return new String(prefix);
			}
		}
		prefix = new char[prefix.length + 1];
		for (int i = 0; i < prefix.length; i++) {
			prefix[i] = 'a';
		}
		return new String(prefix);
	}

	public String newNameUpper() {
		if (prefix == null || prefix.length == 0) {
			prefix = new char[] { 'A' };
			return new String(prefix);
		}
		for (int i = 0; i < prefix.length; i++) {
			if (Character.isUpperCase(prefix[i] + 1)) {
				prefix[i]++;
				return new String(prefix);
			}
		}
		prefix = new char[prefix.length + 1];
		for (int i = 0; i < prefix.length; i++) {
			prefix[i] = 'A';
		}
		return new String(prefix);
	}
	
	public static void main(String[] args) {
		GeneratorName g =new GeneratorName();
		System.out.println(g.newName());
		System.out.println(g.newName());
		System.out.println(g.newName());
		System.out.println(g.newPackageName(3,8));
		System.out.println(g.newPackageName(5, 9));
		
	}

	public int getint(int nbcomp) {
		return random.nextInt(nbcomp);
	}

}