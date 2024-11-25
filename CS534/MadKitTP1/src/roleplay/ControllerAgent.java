package roleplay;

import madkit.kernel.Agent;
import madkit.kernel.Message;
import madkit.message.StringMessage;

public class ControllerAgent extends Agent {

    @Override
    protected void activate() {
        createGroupIfAbsent("communication", "GroupTest");
        requestRole("communication", "GroupTest", "controller");
        getLogger().info("ControllerAgent activé avec succès");
    }

    @Override
    protected void live() {
        while (true) {
            Message msg = waitNextMessage(); // Attendre les messages des agents compteurs
            if (msg instanceof StringMessage) {
                getLogger().info("Message reçu d'un compteur : " + ((StringMessage) msg).getContent());

                // Créer un nouvel agent compteur pour remplacer celui qui s'est transformé
                launchAgent(new CounterAgent());
            }
        }
    }
}



