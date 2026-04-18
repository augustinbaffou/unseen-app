package fr.augustinbaffou.unseen.commun.service;

/**
 * Use case sans entrée (requête sans paramètre).
 *
 * @param <OUTPUT> type de la sortie
 */
public abstract class BaseQueryService<OUTPUT> {
    public abstract OUTPUT execute();
}
