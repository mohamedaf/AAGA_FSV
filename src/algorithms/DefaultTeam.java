package algorithms;

import java.awt.Point;
import java.util.ArrayList;

/**
 * Classe implementant l'algorithme Bafna-Berman-Fujito
 * 
 * @author Affes, Shavgulidze
 *
 */
public class DefaultTeam {

    private ArrayList<Point> originalPoints;
    private ArrayList<FVSPoint> G;
    private ArrayList<FVSPoint> F;
    private ArrayList<FVSPoint> STACK;

    /**
     * Point d'entree de notre algorithme, methode prenant la liste de points a
     * traier en parametre et retournant le resultat de l'algorithme
     * Bafna-Berman-Fujito
     * 
     * @param points
     *            : liste de points a traiter
     * @return : nouvelle liste de points apres traitement de l'algorithme
     *         Bafna-Berman-Fujito
     */
    public ArrayList<Point> calculFVS(ArrayList<Point> points) {
	G = new ArrayList<FVSPoint>();
	F = new ArrayList<FVSPoint>();
	STACK = new ArrayList<FVSPoint>();

	originalPoints = points;

	// Conversion et ajout des points au graphe G
	for (Point point : points) {
	    G.add(new FVSPoint((Point) point.clone()));
	}

	// Application de l'algorithme Bafna-Berman-Fujito sur le graphe G
	start();

	/*
	 * conversion des noeud restants vers des points, le resultat
	 * (representant les points qui devront etre retirees du graphe) est
	 * ensuite retournee
	 */
	return fvsListToPointList(F);
    }

    /**
     * Conversion d'une liste de FVSPoint vers une liste de Point
     * 
     * @param fvsPoints
     *            : liste de FVSPoint
     * @return points : liste de points convertie
     */
    private ArrayList<Point> fvsListToPointList(ArrayList<FVSPoint> fvsPoints) {
	ArrayList<Point> points = new ArrayList<>();
	for (FVSPoint fvsPoint : fvsPoints) {
	    points.add(fvsPoint.getRoot());
	}

	return points;
    }

    /**
     * Calcule le degre d'un FVSPoint
     * 
     * @param p
     *            : le FVSPoint point
     * @return res : le degre du point p
     */
    private int getDegree(FVSPoint p) {
	int res = 0;

	// le degre de p est egal au nombre de ses voisins
	for (FVSPoint fvsPoint : G) {
	    Point point = fvsPoint.getRoot();
	    if (point.distance(p.getRoot()) < 100 && !point.equals(p.getRoot())) {
		res++;
	    }
	}

	return res;
    }

    /**
     * retourne la liste des visins d'un noeud
     * 
     * @param p
     *            : un FVSPoint
     * @return result : la liste des voisins du noeud p
     */
    private ArrayList<FVSPoint> neighbour(FVSPoint p) {
	ArrayList<FVSPoint> result = new ArrayList<FVSPoint>();

	for (FVSPoint fvsPoint : G) {
	    Point point = fvsPoint.getRoot();
	    if (point.distance(p.getRoot()) < 100 && !point.equals(p.getRoot())) {
		result.add(fvsPoint);
	    }
	}

	return result;
    }

    /**
     * methode implementant l'algorithme Bafna-Berman-Fujito
     */
    private void start() {
	Evaluation evaluation = new Evaluation();

	// tant que le graphe n'est pas vide
	while (G.size() > 0) {
	    // parcours du graphe
	    for (FVSPoint point : G) {
		// si le point courant n'est pas encore parcouru
		if (point.getIsUsed() == false) {
		    /*
		     * recherche d'un cycle semi disjoint en parcourant depuis
		     * ce point
		     */
		    ArrayList<FVSPoint> semiDisjointCycle = getSemidisjointCycle(
			    null, point, new ArrayList<FVSPoint>());

		    /*
		     * si un tel cycle est retrouve, on lance le processus
		     * adequat
		     */
		    if (semiDisjointCycle != null) {
			processSemidisjointFound(semiDisjointCycle);
		    } else {
			// sinon on lance le processus adequat au deuxieme cas
			processSemidisjointNotFound(G);
		    }

		    /*
		     * mettre a jour l'etat du graphe en prenant en compte les
		     * eventuels suppressions a faire
		     */
		    updateGraph();
		    // supprimer les noeud du graphe de degre 0 ou 1
		    cleanUp();

		    break;
		}
	    }

	    // retirer les marquages sur les noeuds
	    for (FVSPoint point : G) {
		point.setIsUsed(false);
	    }
	}

	System.out.println(STACK.size());
	System.out.println(F.size());
	// supprimer les noeuds redondants
	while (STACK.size() != 0) {
	    FVSPoint u = STACK.get(STACK.size() - 1);
	    STACK.remove(u);
	    F.remove(u);

	    // si 'u' n'est pas redondant on le remet dans F
	    if (!evaluation.isValide(originalPoints, fvsListToPointList(F))) {
		F.add(u);
	    }
	}
	System.out.println(STACK.size());
	System.out.println(F.size());
    }

    /**
     * le traitement a faire lirsqu'un cycle semi disjoint est retrouve
     * 
     * @param points
     *            : les points du cycle semi disjoint
     */
    private void processSemidisjointFound(ArrayList<FVSPoint> points) {
	double gama = Double.MAX_VALUE;

	/*
	 * recherche du poid minimal dans points (gamma ← min{w(u) :
	 * u∈V(points)}, , points = (V, E))
	 */
	for (FVSPoint p : points) {
	    gama = Math.min(gama, p.w());
	}

	// changements de la valeurs des poids des noeuds dans points
	for (FVSPoint p : points) {
	    p.setW(p.w() - gama);
	}
    }

    /**
     * le traitement a faire lirsqu'un cycle semi disjoint n'est pas retrouve
     * 
     * @param points
     *            : les points du graphe G
     */
    private void processSemidisjointNotFound(ArrayList<FVSPoint> points) {
	double gama = Double.MAX_VALUE;

	// calcul de gama (gama ← min{w(u) : u∈V(points)}, points = (V, E))
	for (FVSPoint p : points) {
	    gama = Math.min(gama, p.w() / (getDegree(p) - 1));
	}

	// changements de la valeurs des poids des noeuds dans points
	for (FVSPoint p : points) {
	    p.setW(p.w() - gama * (getDegree(p) - 1));
	}
    }

    /**
     * Suppression des noeuds ayant un poid 0 du graphe G, et ajout de ces
     * points a la liste de noeuds F ainsi qu'a la pile
     */
    private void updateGraph() {
	for (int i = G.size() - 1; i >= 0; i--) {
	    if (Math.abs(G.get(i).w()) < 0.00001) {
		FVSPoint p = G.get(i);
		G.remove(i);
		F.add(p);
		STACK.add(p);
	    }
	}
    }

    /**
     * Suppression dans G des noeuds de degre 0 ou 1
     */
    private void cleanUp() {
	for (int i = G.size() - 1; i >= 0; i--) {
	    if (getDegree(G.get(i)) <= 1) {
		G.remove(i);
	    }
	}
    }

    /**
     * Verifier si la liste points est un cycle semi disjoint
     * 
     * @param points
     *            : liste de points a verifier
     * @return 'true' si cycle semi disjoint trouve, 'false' sinon
     */
    private boolean isSemidisjointCycle(ArrayList<FVSPoint> points) {
	int cntNotTwo = 0;

	for (int i = 0; i < points.size(); i++) {
	    if (getDegree(points.get(i)) != 2) {
		cntNotTwo++;

		if (cntNotTwo > 1) {
		    return false;
		}
	    }
	}

	return true;
    }

    /**
     * Recherche un cycle semi disjoint en debutant le parcours depuis current
     * 
     * @param parent
     *            : le parent du noeud courant null au debut
     * @param current
     *            : noeud courant
     * @param path
     *            : liste de noeuds parcourus
     * @return
     */
    private ArrayList<FVSPoint> getSemidisjointCycle(FVSPoint parent,
	    FVSPoint current, ArrayList<FVSPoint> path) {
	// recuperation de la liste des voisins du noeud courant
	ArrayList<FVSPoint> neighbours = neighbour(current);

	// si on retombe sur un noeud deja parcouru
	if (current.getIsUsed() == true) {
	    // on ajoute les noeud du cycle dans la liste result
	    ArrayList<FVSPoint> result = new ArrayList<FVSPoint>();
	    result.add(current);
	    for (int i = path.size() - 1; i >= 0; i--) {
		if (path.get(i) == current) {
		    break;
		}
		result.add(path.get(i));
	    }

	    // si cycle semi disjoint retourve on retourne le resultat
	    if (result.size() >= 3 && isSemidisjointCycle(result)) {

		System.out.println("SEMIDISJOINT CYCLE IS FOUND");
		current.setIsUsed(false);
		return result;
	    }

	    // sinon on arrete de parcourir et retournon null
	    return null;
	}

	/*
	 * sinon le noeud courant n'a pas ete parcouru auparavant on l'ajout
	 * donc a la liste de noeud parcouru
	 */
	path.add(current);
	// on marque le noeud courant pour marquer le parcours
	current.setIsUsed(true);

	// on effectue un appel recursif sur les voisin (pas le pere)
	for (FVSPoint point : neighbours) {
	    if (parent != point) {
		ArrayList<FVSPoint> result = getSemidisjointCycle(current,
			point, path);
		/*
		 * si le parcours retourne un resultat positif on supprime le
		 * noued courant du path puis retournon le resultat
		 */
		if (result != null) {
		    path.remove(path.size() - 1);
		    current.setIsUsed(false);
		    return result;
		}
	    }
	}

	// aucun resultat positif retourvé on supprime le noeud courant du path
	path.remove(path.size() - 1);

	return null;
    }

}