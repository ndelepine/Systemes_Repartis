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
import msg.StabilizationMessage;
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
		this.associatedNode = createAssociatedNode(null,null);
		this.fingerTable = new FingerTable(this.associatedNode);
		this.successor= this.fingerTable.getFingerTable().firstEntry().getValue().getReferent();
		this.predecessor=this.successor;

		//A la création de l'acteur, on le rajoute dans la classe Data qui contient les références de tous les acteurs (utile pour pouvoir désigner qui on connaît pour entrer dans le système)
		references.updateRef(key.getIntKey(), this.associatedNode, successor);
	}

	//Création du ChordNode associé à l'acteur
	public ChordNode createAssociatedNode(ChordNode successeur, ChordNode predecesseur){
		ChordNode node = new ChordNode(key,this.getSelf(),successeur,predecesseur);
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
		ChordNode node = associatedNode;
		JoinMessage cmsg = new JoinMessage(node);
		entranceNode.tell(cmsg, this.getSelf());
	}



	//Gestion de tout le protocole de join
	public void handleJoin(JoinMessage msg) {
		ChordNode joiner = msg.getJoiner();
		//On fait le lookup  et on récupère la TableEntry et le numéro de ligne :
		TreeMap<Integer,TableEntry> fingerTableLine = lookup(joiner);
		TableEntry ligne = fingerTableLine.firstEntry().getValue();
		int lineNum = fingerTableLine.firstKey();

		HashSet<ChordNode> listeOfRef = calculSetOfRef();
		//Mise a jour de la fingerTable en partant de la ligne renvoyée par le lookup:
		updateTable(lineNum,joiner);
		updateSuccessor();
		//On met à jour la table des reférences
		references.updateRef(key.getIntKey(), this.associatedNode, successor);

		//Si le reférent est l'acteur, il rajoute le joiner et se met à jour
		//On verifie s'il n' a pas une boucle infinie. Si c'est au joiner de se gérer lui même, on règle le problème
		//Ex : Dans un système à 8 acteurs max, si 0 et 2 sont dans le système et qu'on veut rajouter 3, il y a une boucle infini car 0 forward à 2 et 2 à 0.
		if (ligne.getReferent().getRef() == this.getSelf() || ligne.getReferent().getKey() == joiner.getKey()) {
			//Stockage de tous les référents de la fingerTable dans une liste poour le joinReply


			//Envoi du joinReply :
			JoinReply reply = new JoinReply(listeOfRef);

			System.out.print("Bonjour, "+ joiner.getKey() +". Je viens de t'ajouter au système et je t'informe que ma finger table contient le(s) noeud(s) :");
			for (ChordNode s : listeOfRef) {
				System.out.print(" " + s.getKey().getIntKey()+" ");
			}
			System.out.println("");
			joiner.getRef().tell(reply, getSelf());
		}


		//Sinon il forward :
		else {
			//Transmission du message au referent de la ligne dont l'intervalle contient le joiner
			System.out.println("Ce n'est pas à moi de gérer ton ajout au système. Je transfère le message à "+ligne.getReferent().getKey());
			ligne.getReferent().getRef().forward(msg, getContext());
		}
	}

	//Gestion du join reply
	public void handleJoinReply(JoinReply msg){
		Iterator<ChordNode> i=msg.getListeAct().iterator(); // on crée un Iterator pour parcourir notre HashSet
		while(i.hasNext()) // tant qu'on a un suivant
		{
			updateTable(1,i.next()); // on met à jour
		}

		//On met à jour le successeur et le predecesseur
		updateSuccessor();
		this.predecessor=this.successor;
		
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
		System.out.println("Depart de l'acteur " + leaver);
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
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Data.getInstance().delete(leaver);
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
	private void updateTable(int numLine, ChordNode joiner) {
		/*//On regarde si le joiner est meilleur que le référent actuel de l'intervalle retournée par la fonction lookup()s
		TableEntry actualRef = fingerTable.getFingerTable().get(numLine);

		//Si c'est le cas, on met la fingertable à jour :

			//On part de la ligne renvoyée par le lookup  et on remonte en remplacant le référent s'il est meilleur que le référent actuel
			if(isBestReferent(actualRef,joiner)){
				int i=numLine;
				while(fingerTable.getFingerTable().get(i).getReferent()==actualRef.getReferent()){
					fingerTable.getFingerTable().get(i).setReferent(joiner);
					i--;
				}
			}

			//On part vers le bas et on remplace le référent s'il est meilleur que le référent actuel
			for (int i=numLine+1;i<=fingerTable.getFingerTable().size();i++){
				TableEntry temp = fingerTable.getFingerTable().get(i);
				if(isBestReferent(temp,joiner)){
					fingerTable.getFingerTable().get(i).setReferent(joiner);
				}
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
			JoinMessage join = (JoinMessage) msg;
			System.out.println("Je suis "+this.key.getIntKey()+" et je recois un message de type join. Je dois rajouter "+join.getJoiner().getKey()+" au système.");
			handleJoin(join);
		}
		else if (msg instanceof JoinReply){
			handleJoinReply((JoinReply) msg);
		}
		else if (msg instanceof Terminated){
			Terminated term = (Terminated) msg;
			handleTerminated(term);
		}
		else if (msg instanceof StartMessage){
			System.out.println("Je suis "+this.key.getIntKey()+" et je recois un message de type start. Je peux demander à entrer dans le système");
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
		else if (msg instanceof StabilizationMessage){
			stabilize();
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
		this.associatedNode.setSuccesseur(this.successor);
		
		//On met à jour les références		
		references.updateRef(key.getIntKey(), this.associatedNode, successor);
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
		//Si le successeur de l'acteur est l'acteur cherchant à savoir qui est son prédécesseur, on a trouvé le prédécesseur et on lui dit
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
			this.associatedNode.setPredecesseur(this.predecessor);
	}

	//Getter sur le successeur
	public ChordNode getPredecessor() {
		// TODO Auto-generated method stub
		return this.predecessor;
	}


	//Algorithme de stabilisation

	//Fonction qui demande à son predecesseur qui est son successeur et qui met à jour son predecesseur s'il le faut
	public void stabilize(){
		System.out.println("Le successeur du predecesseur de "+ this.getKey()+ " est "+this.predecessor.getSuccesseur().getKey());
		//On demande à son predecesseur qui est son successeur
		ChordNode predecessor = this.predecessor;
		ChordNode predecessorSuccessor = predecessor.getSuccesseur();
		if(predecessorSuccessor!=null){
			//Si le successeur du predecesseur est après lui dans le cercle, on met à jour le predecesseur
			if(predecessorSuccessor.getKey()!=this.getKey()){
				System.out.println("On met à jour le predecesseur de "+this.getKey());
				int actualPredecessor = this.predecessor.getKey().getIntKey();
				ChordNode potentialPredecessor = predecessorSuccessor;

				//On calcule la distance ente l'acteur et son predecesseur et le predecesseur potentiel avec l'actuel predecesseur

				//On crée une dummy tableEntry pour pouvoir appeler la fonction isBestReferent
				
				//On met la lowerbound au predecesseur actuel
				Interval interval = new Interval(actualPredecessor, 0);
				//On met le referent à l'acteur
				TableEntry dummy = new TableEntry(0, interval, this.associatedNode);
				//On regarde s'il faut mette à jour le successeur
				if (isBestReferent(dummy, potentialPredecessor)){
					System.out.println("Il faut mettre à jour le predecesseur de "+this.getKey());
					//On met à jour le successeur
					this.predecessor = potentialPredecessor;
					this.associatedNode.setPredecesseur(potentialPredecessor);
					
					//On update aussi la fingerTable au cas ou le nouveau predecesseur n'est pas connu
					updateTable(0, potentialPredecessor);
				}
			}
		}

	}

}
