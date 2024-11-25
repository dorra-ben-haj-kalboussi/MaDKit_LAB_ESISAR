
package madkit.bees;

import static madkit.bees.BeeLauncher.BEE_ROLE;
import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.SCHEDULER_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;

import madkit.action.SchedulingAction;
import madkit.kernel.AbstractAgent;
import madkit.kernel.Message;
import madkit.message.ObjectMessage;
import madkit.message.SchedulingMessage;
import madkit.simulation.activator.GenericBehaviorActivator;

/**
 * @version 2.0.0.2
 * @author Fabien Michel, Olivier Gutknecht
 */
public class BeeScheduler extends madkit.kernel.Scheduler {

    private GenericBehaviorActivator<AbstractBee> bees;
  
    @Override
    public void activate() {
	super.activate();
	requestRole(COMMUNITY, SIMU_GROUP, SCHEDULER_ROLE);
	bees = new GenericBehaviorActivator<>(COMMUNITY, SIMU_GROUP, BEE_ROLE, "buzz");
	addActivator(bees); 
	
	GenericBehaviorActivator<AbstractAgent> viewer = new GenericBehaviorActivator<>(COMMUNITY, SIMU_GROUP, "bee observer", "observe");
	addActivator(viewer);
	// auto starting myself the agent way
	receiveMessage(new SchedulingMessage(SchedulingAction.RUN));
    }

    /**
     * Overriding just for adding the multicore option
     *
     * @see madkit.kernel.Scheduler#checkMail(madkit.kernel.Message)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void checkMail(Message m) {
	if (m != null) {
	    try {
		boolean mutiCore = ((ObjectMessage<Boolean>) m).getContent();
		if (mutiCore) {
			 bees.useMulticore(Runtime.getRuntime().availableProcessors());
             //hornets.useMulticore(Runtime.getRuntime().availableProcessors());
		}
		else {
		    bees.useMulticore(1); 
		    //hornets.useMulticore(1);
		}
	    }
	    catch(ClassCastException e) {
		super.checkMail(m);// default behavior
	    }
	}
    }

}
