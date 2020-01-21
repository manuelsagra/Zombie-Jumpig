class Utils {
	
	public static String formatScore(long score) {
	    String res = "" + score;
	    int zeroes = 8 - res.length();
	    for (int i = 0; i < zeroes; i++) {
	        res = "0" + res;
	    }
		return res;
	}

}