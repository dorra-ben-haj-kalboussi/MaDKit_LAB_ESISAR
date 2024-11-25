package roleplay;

import madkit.gui.AgentFrame;
import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Madkit;
import madkit.message.StringMessage;

import java.util.Random;
import java.util.logging.Level;


public class EmetteurAgent extends Agent {
	 @Override
	    protected void activate() {
	        getLogger().setLevel(Level.FINEST);	
	        createGroupIfAbsent("communication", "GroupTest");
	        requestRole("communication", "GroupTest", "emetteur");
	        pause(500);  
	    }

	    @Override
	    protected void live() {
	        Random random = new Random();
	        int nbMessages = random.nextInt(5) + 1;  

	        for (int i = 0; i < nbMessages; i++) {
	            AgentAddress counterAgent = getAgentWithRole("communication", "GroupTest", "conteur");
	            if (counterAgent != null) {
	                ReturnCode code = sendMessage(counterAgent, new StringMessage("Message " + (i + 1)));
	                getLogger().info("Message envoyé au compteur : " + code);
	         
	                pause(random.nextInt(3000) + 1000);
	            } else {
	                getLogger().warning("Aucun agent compteur trouvé !");
	                break;
	            }
	        }
	        
	        killAgent(this);
	    }

	    @Override
	    public void setupFrame(AgentFrame frame) {
	        super.setupFrame(frame);
	        frame.setLocation(100, 350 * (hashCode() - 2));
	    }
	    @SuppressWarnings("unused")
		  public static void main(String[] argss) { 
			  new Madkit("--launchAgents", ControllerAgent.class.getName() + ",true,1;" +EmetteurAgent.class.getName()+",true,1;", CounterAgent.class.getName() +",true,1;"); 
			  
	 
	  }
}

