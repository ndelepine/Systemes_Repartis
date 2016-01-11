package systeme;

public class Key {
	@Override
	public String toString() {
		return ""+key;
	}

	public static final int nb_acteurs = 16;
	public static final int nbLignesFingerTable = (int) (Math.log(nb_acteurs)/Math.log(2));
	int key;
	public Key(int key) {
		super();
		this.key = key;
	}

	public int getIntKey() {
		return key;
	}

	public int getNbLignesFingerTable() {
		return nbLignesFingerTable;
	}

	public static int getNbActeurs() {
		return nb_acteurs;
	}
	
	
	
}
