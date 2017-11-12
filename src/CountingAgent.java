import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
public class CountingAgent extends Agent{

    private Vector<AID> vectorOfServers = new Vector<>();

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void setup()
    {
        super.setup();
        vectorOfServers.clear();
        searchServers();
        while(vectorOfServers.isEmpty()) {searchServers();}

            HandlingServerBehaviour handlingServerBehaviour = new HandlingServerBehaviour();
            handlingServerBehaviour.setServer(vectorOfServers.firstElement());
            addBehaviour(handlingServerBehaviour);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void searchServers()
    {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Arrays");
        sd.setName("DistributorAgent");
        template.addServices(sd);
        try
        {
            DFAgentDescription[] result = DFService.search(this, template);
            for(int i = 0; i < result.length; ++i)
                vectorOfServers.addElement(result[i].getName());
        }
        catch (FIPAException ex)
        {
            ex.printStackTrace();
        }
    }
}


