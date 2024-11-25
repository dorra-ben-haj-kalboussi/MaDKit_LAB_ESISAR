package madkit.bees;

import static madkit.bees.BeeLauncher.SIMU_GROUP;
import static madkit.bees.BeeLauncher.COMMUNITY; 
import static madkit.bees.BeeLauncher.BEE_ROLE;
import static madkit.bees.BeeLauncher.HORNET;

import java.awt.Point;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


import madkit.kernel.AbstractAgent;
import madkit.kernel.Message;
import madkit.message.StringMessage;

public class Hornet extends AbstractBee {

	private static final long serialVersionUID = -2393301912353816181L;
	AbstractBee target = null;
	int killCounter = 0;
	long beeSurroundTimer = 0;
	long targetSurroundTimer = 0;
	static float minDist = 150;

	@Override
	public void activate() {
		
		getLogger().info(() -> "HORNET activated");
		requestRole(COMMUNITY, SIMU_GROUP, HORNET, null);
		requestRole("buzz", SIMU_GROUP, "bee", null);
	}

	private AbstractBee findnearest() {
		getLogger().info(() -> "HORNET search nearest");
		final Point location = myInformation.getCurrentPosition();
		AbstractBee nearest_agent = null;
		double nearest_dist = 9999999f;
		for (int i = 0; i < beeWorld.beesList.size(); i++) {
			AbstractBee beebee = (AbstractBee) beeWorld.beesList.get(i);
			if (beebee == null)
				continue;
			Point Beeposition = beebee.myInformation.getCurrentPosition();
			double dist = Beeposition.distance(location);
			if (dist < nearest_dist) {
				nearest_dist = dist;
				nearest_agent = beebee;
			}
		}
		final AbstractBee nearest_agent_print = nearest_agent;
		if (nearest_agent_print != null) {
			getLogger().info(() -> "HORNET found nearest" + nearest_agent_print.getClass().getName());
		}
		return nearest_agent;
	}

	@Override
	public void buzz() {
	    gererMessagesEntrants();
	    trouverCible();
	    super.buzz();
	    List<Bee> abeillesProches = detecterAbeillesProches();

	    verifierEssaiAbeilles(abeillesProches);

	    gererAttaque(abeillesProches);
	} 
	
	private void gererMessagesEntrants() {
	    Message message = nextMessage();
	    if (message instanceof StringMessage) {
	        String msg = ((StringMessage) message).getContent();
	        if ("SURROUNDED".equals(msg)) {
	            getLogger().info(() -> "Hornet a reçu le message 'entouré' ! Il meurt...");
	            killAgent(this); 
	            return;
	        }
	    }
	} 
	private void trouverCible() {
	    if (target == null || !target.isAlive()) {
	        target = findnearest();
	    }
	}
	private List<Bee> detecterAbeillesProches() {
	    Point maPosition = myInformation.getCurrentPosition();
	    List<Bee> abeillesProches = new ArrayList<>();

	    for (AbstractAgent abeille : beeWorld.beesList) {
	        if (abeille instanceof Bee && abeille != this) {
	            Point positionAbeille = ((Bee) abeille).myInformation.getCurrentPosition();
	            if (maPosition.distance(positionAbeille) < minDist) {
	                abeillesProches.add((Bee) abeille);
	            }
	        }
	    }
	    return abeillesProches;
	}
	private void verifierEssaiAbeilles(List<Bee> abeillesProches) {
	    if (abeillesProches.size() > 7) {
	        if (beeSurroundTimer == 0) {
	            beeSurroundTimer = Instant.now().getEpochSecond();
	        } else if (beeSurroundTimer + 5 < Instant.now().getEpochSecond()) {
	            getLogger().info(() -> "HORNET meurt à cause d'un essaim d'abeilles.");
	            killAgent(this);
	        }
	    } else {
	        beeSurroundTimer = 0;
	    }

	    if (abeillesProches.size() > 4) {
	        getLogger().info(() -> "HORNET est entouré ! Impossible d'attaquer.");
	    }
	}
	private void gererAttaque(List<Bee> abeillesProches) {
	    if (abeillesProches.size() <= 4) {
	        for (Bee abeille : abeillesProches) {
	            getLogger().info(() -> "HORNET attaque une cible.");
	            try { 
	    
	                envoyerMessageEtAttaquer(abeille);
	            } catch (Exception e) {
	                getLogger().warning(() -> "Erreur lors de l'attaque : " + e.getMessage());
	            }
	        }
	    } else {
	        targetSurroundTimer = 0;
	    }
	}
	private void envoyerMessageEtAttaquer(Bee abeille) {
	    sendMessage(abeille.getAgentAddressIn(COMMUNITY, SIMU_GROUP, BEE_ROLE), new StringMessage("STOP"));

	    if (targetSurroundTimer == 0) {
	        targetSurroundTimer = Instant.now().getEpochSecond();
	    } else if (targetSurroundTimer + 3 < Instant.now().getEpochSecond()) {
	        sendMessage(abeille.getAgentAddressIn(COMMUNITY, SIMU_GROUP, BEE_ROLE), new StringMessage("KILL"));
	        getLogger().info(() -> "HORNET tue l'abeille.");
	        killAgent(abeille);
	    }
	}



	@Override
	protected void computeNewVelocities() {
		final Point location = myInformation.getCurrentPosition();
		// distances from bee to queen
		int dtx;
		int dty;

		if (target != null && target.isAlive()) {

			Point Beeposition = target.myInformation.getCurrentPosition();
			dtx = Beeposition.x - location.x;
			dty = Beeposition.x - location.y;
			float acc = 0;
			if (beeWorld != null) {
				acc = Math.round(beeWorld.getBeeAcceleration().getValue() * 1.5);
			}
			dX += (int)(dtx * acc);
			dY += (int)(dty * acc);
		}
	}

	@Override
	protected int getMaxVelocity() {
		if (beeWorld != null) {
			return beeWorld.getBeeVelocity().getValue();
		}
		return 0;
	}
}

