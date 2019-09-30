# Javass
Swiss playing cards game written in Java, including an optimised Monte Carlo type AI to play against, a graphical interface using JavaFX and a multiplayer LAN system, allowing to play with anyone on a network.

parametres donnés a l'application sous cette forme :
     * 
     *           
     * {j1}…{j4} [{graine}] où :
     *       {jn} spécifie le joueur n, ainsi:
     *                   h:{nom}  un joueur humain nommé {nom}"
     *       
     *                   s:{nom}:{n}  un joueur simulé nommé {nom} qui itère l'algorithme MCTS {n} fois"
     *       
     *                   r:{nom}:{adresse}  un joueur distant nommé {nom} et sur le serveur d'adresse : {adresse}
     *       
     *               Les agruments {nom}, {n} et {adresses} sont optionnelles et ont pour valeurs par défaut : 
     *       
     *                   Aline, Bastien, Colette et David attribués dans l'ordre, à défaut de {nom}
     *       
     *                   10 000 iterations de MCTS, à défaut de {n}
     *           et localhost, à défaut de {adresse}.
     *          
