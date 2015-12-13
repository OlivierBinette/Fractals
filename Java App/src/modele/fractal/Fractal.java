package modele.fractal;

import console.CommandableNode;

/**
 * Interface utilitaire que doit implémenter toute classe représentant une fractale. Celle-ci est
 * utile pour le AbstractFracNav, qui prend un type Fractal sans avoir besoin d'accéder à ses
 * méthodes.
 */
public interface Fractal extends CommandableNode {}
