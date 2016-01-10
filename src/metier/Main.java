package metier;

import java.util.InputMismatchException;
import java.util.Scanner;

import msg.ShowFingerTable;
import msg.StartMessage;
import systeme.Data;
import systeme.Key;
import actors.ChordActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


		//Création du système d'acteurs
		ActorSystem chord = ActorSystem.create("Chord");

		System.out.println("Bonjour ! Bienvenue dans notre superbe application qui implémente Chord en Java.");

		//On initialise un premier acteur
		Scanner sc = new Scanner(System.in);
		System.out.println("Pour le moment, il n'y a pas d'acteur dans le système alors il va falloir en créer un. Qui voulez vous ajouter (0-"+(Key.nb_acteurs-1)+") : ");
		int choix=-1;
		try{
			choix = sc.nextInt();
		}
		catch(InputMismatchException ime){
			System.out.println("Il faut rentrer un nombre : ");
		}

		while(choix<0 || choix>Key.nb_acteurs-1){
			if(choix<0 || choix>Key.nb_acteurs-1){
				System.out.println("Le nombre rentré est incorrect. Il faut en ressaisir un : ");
				try{
					choix = sc.nextInt();
				}
				catch(InputMismatchException ime){
					System.out.println("Il faut rentrer un nombre : ");
				}
			}
		}


		//On ajoute l'acteur au système
		Key key = new Key(choix);
		chord.actorOf(Props.create(ChordActor.class,key), "ChordActor-"+key.getIntKey());
		System.out.println("Un premier acteur a été ajouté");

		Scanner scan = new Scanner(System.in);
		int menu = -1;
		
		affichageMenu();

		try {
			do {
				try {
					menu = scan.nextInt();
				} catch(InputMismatchException ime) {
					System.out.println("Il faut rentrer un nombre.");
				}

				while (menu<1 || menu >4) {
					if (menu<1 || menu >4) {
						System.out.println("Veuillez rentrer une option valide");
						try {
							menu = scan.nextInt();
						} catch(InputMismatchException ime) {
							System.out.println("Il faut rentrer un nombre.");
						}
					}
				}
				
				switch(menu) {
				case 1: ajouter(choix, chord);
				break;
				case 2: 
					System.out.println("Afficher la FingerTable de quel acteur ? ");
					int acteur=-1;
					try{
						acteur = sc.nextInt();
					}
					catch(InputMismatchException ime){
						System.out.println("Il faut rentrer un nombre : ");
					}

					while(acteur<0 || acteur>Key.nb_acteurs-1){
						if(acteur<0 || acteur>Key.nb_acteurs-1){
							System.out.println("Le nombre rentré est incorrect. Il faut en ressaisir un : ");
							try{
								acteur = sc.nextInt();
							}
							catch(InputMismatchException ime){
								System.out.println("Il faut rentrer un nombre : ");
							}
						}
					}
					
					afficheFT(acteur);
					break;
				case 3: //stuff
					break;
				case 4: System.out.println("Au revoir !");
				break;
				default:
					System.out.println("Je ne sais pas comment tu es arrivé(e) ici mais tu es fort(e) !");
					 break;
				}
			} while (menu != 4);
			
		} catch(Exception ex) {
			System.out.println("Erreur avec le programme");
			scan.next();
		} finally {
			scan.close();
			sc.close();
			System.exit(0);
		}
	}
	
	public static void affichageMenu() {
		//Affichage du menu principal
		System.out.println("--------------");
		System.out.println("Menu principal");
		System.out.println("--------------");
		System.out.println("1 - Ajouter un acteur");
		System.out.println("2 - Afficher la FingerTable d'un acteur");
		System.out.println("3 - Faire quitter un acteur du système");
		System.out.println("4 - Quitter l'application");
	}

	public static void ajouter(int choix, ActorSystem chord) {
		//On demande à l'utilisateur qui il veut rajouter
		System.out.println("Vous pouvez maintenant rajouter un acteur. Qui voulez vous ajouter (0-"+(Key.nb_acteurs-1)+") : ");
		Scanner sc = new Scanner(System.in);
		int choix2=-1;

		try{
			choix2 = sc.nextInt();
		}
		catch(InputMismatchException ime){
			System.out.println("Il faut rentrer un nombre : ");
		}

		while(choix2<0 || choix2>Key.nb_acteurs-1 ||choix2==choix){
			if(choix2<0 || choix2>Key.nb_acteurs-1 || choix2==choix){
				System.out.println("Le nombre rentré est incorrect. Il faut en ressaisir un : ");

				try{
					choix2 = sc.nextInt();
				}
				catch(InputMismatchException ime){
					System.out.println("Il faut rentrer un nombre : ");
				}
			}
		}

		//On crée l'acteur choisi
		Key key2 = new Key(choix2);
		ActorRef testActor = chord.actorOf(Props.create(ChordActor.class,key2), "ChordActor-"+key2.getIntKey());

		//On demande par le biais de qui il veut rentrer dans le système
		System.out.println("Pour rentrer dans le système, il faut connaitre quelqu'un. Qui connaissez vous ? (0-"+(Key.nb_acteurs-1)+") : ");
		choix=-1;
		try{
			choix = sc.nextInt();
		}
		catch(InputMismatchException ime){
			System.out.println("Il faut rentrer un nombre : ");
		}

		while(choix<0 || choix>Key.nb_acteurs-1 || Data.getInstance().calculRef(choix) == null){
			if(choix<0 || choix>Key.nb_acteurs-1){
				System.out.println("Le nombre rentré est incorrect. Il faut en ressaisir un : ");
				try{
					choix = sc.nextInt();
				}
				catch(InputMismatchException ime){
					System.out.println("Il faut rentrer un nombre : ");
				}
			}
			if (Data.getInstance().calculRef(choix) == null) {
				System.out.println("Cet acteur n'existe pas dans le système. Veuillez en spécifier un autre : ");
				try{
					choix = sc.nextInt();
				}
				catch(InputMismatchException ime){
					System.out.println("Il faut rentrer un nombre : ");
				}
			}
			//Rajouter un try catch dans handleJoin de ChordActor (NullPointerException) si l'utilisateur indique que l'acteur voulant entrer est celui qu'il connaît
		}

		StartMessage start = new StartMessage(Data.getInstance().calculRef(choix).getRef());
		testActor.tell(start, testActor);
	}
	
	public static void afficheFT(int acteur) {
		
		ShowFingerTable show = new ShowFingerTable();
		Data.getInstance().calculRef(acteur).getRef().tell(show, null);
		
	}
	
}