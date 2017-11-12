import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.concurrent.ThreadLocalRandom;


public class DistributorAgent extends Agent
{
    @Override
    protected void setup()
    {
        super.setup();

        //Register agent
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("generator_of_tokens");
        sd.setName("serverAgent");
        dfd.addServices(sd);
        try
        {
            DFService.register(this, dfd);
        }
        catch(FIPAException ex)
        {
            ex.printStackTrace();
        }

        //init arrays
        InputContainer inputContainerA = new InputContainer(3,3);
        inputContainerA.fillArray();
        int[][] arrayA = inputContainerA.getInputArray();

        InputContainer inputContainerB = new InputContainer(3,3);
        inputContainerB.fillArray();
        int[][] arrayB = inputContainerA.getInputArray();

        int[][] arrayC = new int[inputContainerA.getSizeX()][inputContainerB.getSizeY()];

        OutputContainer outputContainerC = new OutputContainer (3,3);
        outputContainerC.initProgressArray();
        int[][] progressArray = outputContainerC.getProgressArray();

        //ADD Handling Client behaviour
        HandlingClientBehaviour handlingClientBehaviour = new HandlingClientBehaviour();
        handlingClientBehaviour.setArrayA(arrayA);
        handlingClientBehaviour.setArrayB(arrayB);
        handlingClientBehaviour.setArrayC(arrayC);
        handlingClientBehaviour.setProgressArray(progressArray);
        addBehaviour(handlingClientBehaviour);

        System.out.println("Server has been started");

    }
}