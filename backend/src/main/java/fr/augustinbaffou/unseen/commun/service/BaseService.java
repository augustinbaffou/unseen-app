package fr.augustinbaffou.unseen.commun.service;

/**
 * Use case avec entrée et sortie.
 *
 * @param <INPUT>  type de l'entrée
 * @param <OUTPUT> type de la sortie
 */
public abstract class BaseService<INPUT, OUTPUT> {
    public abstract OUTPUT execute(INPUT input);
}
