import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;


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
        sd.setType("Arrays");
        sd.setName("DistributorAgent");
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
        InputContainer containerA = new InputContainer(6,6);
        containerA.fillArray();
        int[][] arrayA = containerA.getArray();

        InputContainer containerB = new InputContainer(6,6);
        containerB.fillArray();
        int[][] arrayB = containerB.getArray();

        InputContainer containerC = new InputContainer(containerA.getSizeX(),containerB.getSizeY());
//        containerC.initArray();
        int[][] arrayC = containerC.getArray();

        InputContainer progressContainer = new InputContainer(containerC.getSizeX(),containerC.getSizeY());
        progressContainer.initArray();
        int[][] progressArray = progressContainer.getArray();

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