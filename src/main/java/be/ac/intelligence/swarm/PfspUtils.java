package be.ac.intelligence.swarm;

public class PfspUtils {
	public static <T> T[][] deepCopy(T[][] matrix) {
		return java.util.Arrays.stream(matrix).map(el -> el.clone()).toArray($ -> matrix.clone());
	}

	public static Integer[][] transposeMatrix(Integer[][] m) {
		Integer[][] temp = new Integer[m[0].length][m.length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[0].length; j++)
				temp[j][i] = m[i][j];
		return temp;
	}
}
