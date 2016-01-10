package actors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import msg.FoundPredecessorMessage;
import msg.JoinMessage;
import msg.JoinReply;
import msg.SearchPredecessorMessage;
import msg.ShowFingerTable;
import msg.StartMessage;
import systeme.Data;
import systeme.FingerTable;
import systeme.Hashable;
import systeme.Interval;
import systeme.Key;
import systeme.TableEntry;
import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;

public class ChordActor extends UntypedActor implements Hashable{

	Key key;
	FingerTable fingerTable;
	ChordNode successor;
	ChordNode predecessor;
	ChordNode associatedNode;
	Data references = Data.getInstance();

	//Constructeur
	public ChordActor(Key key) {
		super();
		this.key = key;
		this.fingerTable = new FingerTable(createAssociatedNode());
		this.successor=fingerTable.getFingerTable().get(1).getReferent();
		this.predecessor=fingerTable.getFingerTable().get(1).getReferent();
		this.associatedNode = createAssociatedNode();

		//A la création de l'acteur, on le rajoute dans la classe Data qui contient les références de tous les acteurs (utile pour pouvoir désigner qui on connaît pour entrer dans le système)
		references.updateRef(key.getIntKey(), this.associatedNode, successor);
	}

	//Création du ChordNode associé à l'acteur
	public ChordNode createAssociatedNode(){
		ChordNode node = new ChordNode(key,this.getSelf());
		return node;
	}


	//getter sur la Key
	@Override
	public Key getKey() {
		// TODO Auto-generated method stub
		return this.key;
	}


	//getter sur la fingerTable
	public FingerTable getFingerTable() {
		return fingerTable;
	}


	//setter sur la fingerTable
	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}

	//Gestion de l'envoi de la demande de join
	public void askJoin(ActorRef entranceNode) {
		System.out.println("Je rentre dans la fonction askJoin");
		ChordNode node = associatedNode;
		JoinMessage cmsg = new JoinMessage(node);
		System.out.println(cmsg);
		entranceNode.tell(cmsg, this.getSelf());
	}



	//Gestion de tout le protocole de join
	public void handleJoin(JoinMessage msg) {
		ChordNode joiner = msg.getJoiner();

		//On verifie s'il n' a pas une boucle infinie. Si c'est au joiner de se gérer lui même, on règle le problème
		//Ex : Dans un système à 8 acteurs max, si 0 et 2 sont dans le système et qu'on veut rajouter 3, il y a une boucle infini car 0 forward à 2 et 2 à 0.
		if(this.getSelf()==joiner.getRef()){
			System.out.println("Boucle infinie");
		}
		else{

			//On fait le lookup  et on récupère la TableEntry et le numéro de ligne :
			TreeMap<Integer,TableEntry> fingerTableLine = this.lookup(joiner);
			TableEntry ligne = fingerTableLine.firstEntry().getValue();
			int lineNum = fingerTableLine.firstKey();

			//Si le reférent est l'acteur, il rajoute le joiner et se met à jour
			if (ligne.getReferent().getRef() == this.getSelf()) {

				//Stockage de tous les référents de la fingerTable dans une liste poour le joinReply
				HashSet<ChordNode> listeOfRef = calculSetOfRef();

				//Mise a jour de la fingerTable en partant de la ligne renvoyée par le lookup:
				updateTable(ligne,lineNum,joiner);
				updateSuccessor();
				//On met à jour la table des reférences
				references.updateRef(key.getIntKey(), this.associatedNode, successor);


				//Envoi du joinReply :
				JoinReply reply = new JoinReply(listeOfRef);
				System.out.println(reply);

				System.out.print("Par ailleurs, je t'informe que ma finger table contient le(s) noeud(s) :");
				for (ChordNode s : listeOfRef) {
					System.out.print(" " + s.getKey().getIntKey()+" ");
				}
				System.out.println("");
				joiner.getRef().tell(reply, getSelf());
			}


			//Sinon il forward :
			else {
				//Mise à jour de sa fingerTable et transmission du message au referent de la ligne dont l'intervalle contient le joiner

				//Mise a jour de la fingerTable en partant de la ligne renvoyée par le lookup:
				updateTable(ligne,lineNum,joiner);
				updateSuccessor();
				//On met à jour la table des reférences
				references.updateRef(key.getIntKey(), this.associatedNode, successor);

				//Transmission du message
				ligne.getReferent().getRef().forward(msg, getContext());
			}
		}
	}

	//Gestion du join reply
	public void handleJoinReply(JoinReply msg){
		Iterator<ChordNode> i=msg.getListeAct().iterator(); // on crée un Iterator pour parcourir notre HashSet
		while(i.hasNext()) // tant qu'on a un suivant
		{
			updateTable(null,0,i.next()); // on affiche le suivant
		}

		//On met à jour le successeur et le predecesseur
		updateSuccessor();
		searchPredecessor(associatedNode);

		//On dit aux noeuds de la fingerTable de mettre à jour leur predécesseur :
		HashSet<ChordNode> listeOfRef = calculSetOfRef();
		for (ChordNode s : listeOfRef) {
			SearchPredecessorMessage searchPredecessor = new SearchPredecessorMessage(s);
			s.getRef().tell(searchPredecessor,null);
		}
	}



	//Gestion du départ d'un acteur (volontaire ou pas)
	public void handleTerminated(Terminated msg) {
		ActorRef leaver = msg.actor();
		System.out.println("Depart");
		//On parcourt la fingerTable et on remplace l'acteur qui est parti par son successeur
		for(int i=1;i<fingerTable.getFingerTable().size();i++) {
			//Si un référent était le leaver, on le remplace par le successeur du leaver
			if(fingerTable.getFingerTable().get(i).getReferent().getRef()==leaver){
				fingerTable.getFingerTable().get(i).setReferent(references.calculSuccessor(leaver));
			}

			//Si le successeur était le leaver, on le remplace par le successeur du leaver
			if(successor.getRef()==leaver){
				successor=references.calculSuccessor(leaver);
			}

			//Si le predecessuer était le leaver, on recalcule le prédecesseur
			if(predecessor.getRef()==leaver){
				searchPredecessor(associatedNode);
			}
		}
	}



	//Fonction qui regarde à quelle intervalle appartient l'acteur et qui renvoie la ligne
	public TreeMap<Integer,TableEntry> lookup(ChordNode acteur) {
		//Voir si on retourne un Node ou un int
		TreeMap<Integer,TableEntry> res= new TreeMap<Integer,TableEntry>();
		System.out.println("Je regarde si "+acteur.getKey().getIntKey()+" est dans un intervalle de ma FingerTable.");

		//On parcourt la table à l'envers (gain de temps)
		for (int i=fingerTable.getFingerTable().size();i>=1;i--){
			TableEntry temp = fingerTable.getFingerTable().get(i);
			Interval interval = temp.getInterval();
			//On prend en compte le tour du cercle (ex : 6 n'appartient pas [4;0] mais à [4;8]
			int lower = interval.getLowerBound();
			int upper = interval.getUpperBound();
			if(upper < lower){
				lower -= Key.nb_acteurs;
			}

			if (acteur.getKey().getIntKey()<upper & acteur.getKey().getIntKey()>=lower){
				System.out.println(lower +" - " +upper + " Ligne : " + i);
				System.out.println("J'ai trouve l'intervalle. La fingerEntry correspondante est  : "+temp);
				res.put(i, temp);
			}
		}
		return res;
	}


	//Fonction qui calcul l'ensemble des références d'acteurs contenu dans la fingertable
	private HashSet<ChordNode> calculSetOfRef() {
		ArrayList<ChordNode> allNode = new ArrayList<ChordNode>();

		for(int i=1;i<fingerTable.getFingerTable().size();i++) {
			allNode.add(fingerTable.getFingerTable().get(i).getReferent());
		}
		HashSet<ChordNode> cn = new HashSet<ChordNode>(allNode);
		return cn;
	}

	//Fonction qui met à jour la fingertable
	private void updateTable(TableEntry ligne, int numLine, ChordNode joiner) {
		/*//On regarde si le joiner est meilleur que le référent actuel de l'intervalle retournée par la fonction lookup()
		boolean bestRef = isBestReferent(ligne,joiner);

		//Si c'est le cas, on met la fingertable à jour :

		if(bestRef){
			//On part de la ligne renvoyée par le lookup  et on remonte en remplacant le référent s'il est meilleur que le référent actuel
			System.out.println("Parcours vers le haut.");


			//On part vers le bas et on remplace le référent s'il est meilleur que le référent actuel
			System.out.println("Parcours vers le bas.");

			//On met à jour le successeur et le prédécesseur
			System.out.println("Mise à jour du successeur et du prédécesseur.");
		}*/
		for (int i=fingerTable.getFingerTable().size();i>=1;i--){
			TableEntry temp = fingerTable.getFingerTable().get(i);
			if(isBestReferent(temp,joiner)){
				fingerTable.getFingerTable().get(i).setReferent(joiner);
			}
		}
	}

	//Fonction qui calcule le meilleur référent d'un intervalle
	public boolean isBestReferent(TableEntry ligne, ChordNode joiner){
		boolean res=false;
		int lowerBound = ligne.getInterval().getLowerBound();
		int actualRef = ligne.getReferent().getKey().getIntKey();
		int joinerRef = joiner.getKey().getIntKey();

		//On calcule la distance des deux ChordNode avec le début de l'intervalle

		//Si l'id du noeud est plus petit que le début de l'intervalle, on lui rajoute 2^m (opération inverse du modulo)
		if(actualRef<lowerBound){
			actualRef += Key.nb_acteurs;
		}
		int distRef = actualRef - lowerBound;

		if(joinerRef<lowerBound){
			joinerRef += Key.nb_acteurs;
		}
		int distJoiner =  joinerRef - lowerBound;

		if(distJoiner < distRef){
			res=true;
		}
		return res;
	}

	@Override
	//Comportement d'un acteur à la réception d'un message
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof JoinMessage) {
			System.out.println("Je suis "+this.key.getIntKey()+" et je recois un message de type join");
			handleJoin((JoinMessage) msg);
		}
		else if (msg instanceof JoinReply){
			handleJoinReply((JoinReply) msg);
		}
		else if (msg instanceof Terminated){
			System.out.println("Détéction de départ !!!!!!!!!");
			Terminated term = (Terminated) msg;
			handleTerminated(term);
		}
		else if (msg instanceof StartMessage){
			System.out.println("Je suis "+this.key.getIntKey()+" et je recois un message de type start");
			StartMessage start = (StartMessage) msg;
			askJoin(start.getConnaissance());
		}
		else if (msg instanceof SearchPredecessorMessage){
			SearchPredecessorMessage searchPredecessor = (SearchPredecessorMessage) msg;
			searchPredecessor(searchPredecessor.getSearcher());
		}
		else if (msg instanceof FoundPredecessorMessage){
			FoundPredecessorMessage foundPredecessor = (FoundPredecessorMessage) msg;
			updatePredecessor(foundPredecessor.getPredecessor());
		}
		else if (msg instanceof ShowFingerTable){
			affichage();
		}
	}

	//Fonction qui affiche le contenu de la fingerTable
	public void affichage() {

		System.out.println("La fingerTable de l'acteur "+this.getKey().getIntKey()+ " est :");
		for (int i=1;i<=fingerTable.getFingerTable().size();i++){
			TableEntry temp = fingerTable.getFingerTable().get(i);
			System.out.println(temp);
		}
		System.out.println("Successeur : "+this.successor.getKey().getIntKey());
		System.out.println("Predecesseur : "+this.predecessor.getKey().getIntKey());
	}

	//Fonction qui met à jour le successeur de l'acteur
	public void updateSuccessor(){
		//On calcule le successeur en regardant qui est le premier référent de la finger table de l'acteur qui n'est pas lui même.
		int i=0;
		do{
			i++;
		}while(fingerTable.getFingerTable().get(i).getReferent().getKey()==this.getKey());

		this.successor = fingerTable.getFingerTable().get(i).getReferent();
	}

	//Getter sur le successeur
	public ChordNode getSuccesseur() {
		// TODO Auto-generated method stub
		return this.successor;
	}

	//Fonction qui cherche le meilleur predecesseur potentiel
	public ActorRef findBestPredecessor(ChordNode actorRef){
		//On parcourt les référents de la fingertable tant qu'ils sont différents de l'acteur.
		int i=0;
		do{
			i++;
		}while(fingerTable.getFingerTable().get(i).getReferent().getKey().getIntKey()!=actorRef.getKey().getIntKey() && i<Key.nbLignesFingerTable);

		//On revoie la référence
		return fingerTable.getFingerTable().get(i-1).getReferent().getRef();
	}

	//Fonction qui recherche le vrai predecesseur en fonction du meilleur predecesseur potentiel
	public void searchPredecessor(ChordNode actorRef){
		//Si le successeur de l'acteur est l'acteur cherchant à savoir qui est son prédéceseeur, on a trouvé le prédécesseur et on lui dit
		if(this.getSuccesseur()==actorRef){
			ChordNode predecessor = this.associatedNode;
			FoundPredecessorMessage foundPredecessor= new FoundPredecessorMessage(predecessor);
			actorRef.getRef().tell(foundPredecessor, this.getSelf());
		}

		//Sinon, on appelle findBestPredecessor et on renvoie le message au meilleur prédécesseur potentiel
		else{
			SearchPredecessorMessage searchPredecessor = new  SearchPredecessorMessage(actorRef);
			findBestPredecessor(actorRef).tell(searchPredecessor, this.getSelf());
		}
	}

	public void updatePredecessor(ChordNode predecessor){
		this.predecessor=predecessor;
	}

	//Getter sur le successeur
	public ChordNode getPredecessor() {
		// TODO Auto-generated method stub
		return this.predecessor;
	}

}
