package roleplay;

import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.StringMessage;

public class CounterAgent extends Agent {
	 private int messageCount = 0;

	    @Override
	    protected void activate() {
	        createGroupIfAbsent("communication", "GroupTest");
	        requestRole("communication", "GroupTest", "conteur");
	    }

	    @Override
	    protected void live() {
	        while (true) {
	            Message msg = waitNextMessage(); 
	            if (msg instanceof StringMessage) {
	                messageCount++;
	                getLogger().info("Message reçu : " + ((StringMessage) msg).getContent());

	                if (messageCount >= 5) { 
	                    getLogger().info("Transformation en émetteur après " + messageCount + " messages.");            
	                    AgentAddress controller = getAgentWithRole("communication", "GroupTest", "controller");
	                    if (controller != null) {
	                        sendMessage(controller, new StringMessage("Messages reçus : " + messageCount));
	                    }
	                    launchAgent(new EmetteurAgent());
	                    killAgent(this);
	                }
	            }
	        }
	    } }

