package algorithms;

import java.awt.Point;

/**
 * Classe definissant un FVSPoint Un FVSPoint contient un noeud 'root', un poid
 * 'w' et un boolean 'isUsed' servant dans la detection de cycle comme marqueur
 * de passage
 * 
 * @author Affes, Shavgulidze
 *
 */
public class FVSPoint {
    private Point root;
    private double w;
    private boolean isUsed;

    /**
     * Constructeur
     * 
     * @param root
     *            : valeur affecte au noeud root de l'objet
     */
    public FVSPoint(Point root) {
	this.root = root;
	this.w = 1;
	this.isUsed = false;
    }

    /**
     * Constructeur
     * 
     * @param root
     *            : valeur affecte au noeud root de l'objet
     * @param w
     *            : poid du noeud
     */
    public FVSPoint(Point root, double w) {
	this.root = root;
	this.w = w;
	this.isUsed = false;
    }

    /**
     * retourne le noeud root de l'objet
     * 
     * @return root
     */
    public Point getRoot() {
	return root;
    }

    /**
     * retourne le poid w du noeud
     * 
     * @return w
     */
    public double w() {
	return this.w;
    }

    /**
     * affecte une nouvelle valeur au poid w du noeud
     * 
     * @param w
     *            : nouveau poid
     */
    public void setW(double w) {
	this.w = w;
    }

    /**
     * verifie si le noeud a ete marque
     * 
     * @return isUsed
     */
    public boolean getIsUsed() {
	return this.isUsed;
    }

    /**
     * modifie l'attribut isUsed pour marquer ou demarquer un noeud
     * 
     * @param val
     *            : nouvelle valeur de isUsed
     */
    public void setIsUsed(boolean val) {
	this.isUsed = val;
    }
}
