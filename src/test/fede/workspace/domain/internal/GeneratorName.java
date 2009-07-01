/**
 *
 */
package test.fede.workspace.domain.internal;

public class GeneratorName {
	char[]	prefix	= null;

	public String newName() {
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

	public String newPackageName(int bn) {
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

}