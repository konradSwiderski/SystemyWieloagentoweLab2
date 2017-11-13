import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class MaliciousAgent extends CountingAgent {
    protected Vector<AID> vectorOfServers = new Vector<>();
    protected void setup() {
        super.setup();
        System.out.println("HAY TU OSZUST");
        searchServers();
        
    }

//        vectorOfServers.clear();
//        searchServers();
//        while(vectorOfServers.isEmpty()) {searchServers();}
//
//        HandlingServerBehaviour handlingServerBehaviour = new HandlingServerBehaviour();
//        handlingServerBehaviour.setServer(vectorOfServers.firstElement());
//        addBehaviour(handlingServerBehaviour);
//    }
    protected void searchServers()
    {
        super.searchServers();
    }

}
