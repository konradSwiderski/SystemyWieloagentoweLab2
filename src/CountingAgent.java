import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Vector;

public class CountingAgent extends Agent {

    protected Vector<AID> vectorOfServers = new Vector<>();

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void setup() {
        super.setup();
        vectorOfServers.clear();

        //search servers
        while (vectorOfServers.isEmpty()) { searchServers(); }

        //ADD Handling Server behaviour and set members
        ClientCyclicBehaviour clientCyclicBehaviour = new ClientCyclicBehaviour();
        clientCyclicBehaviour.setServer(vectorOfServers.firstElement());
        addBehaviour(clientCyclicBehaviour);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected void searchServers() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Arrays");
        sd.setName("DistributorAgent");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            for (int i = 0; i < result.length; ++i)
                vectorOfServers.addElement(result[i].getName());
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
    }
}


