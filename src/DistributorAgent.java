import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;


public class DistributorAgent extends Agent {
    @Override
    protected void setup() {
        super.setup();

        //Register agent
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Arrays");
        sd.setName("DistributorAgent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        //init arrays A,B,C,progress
        ArrayContainer containerA = new ArrayContainer(4, 4);
        containerA.fillArray();
        int[][] arrayA = containerA.getArray();

        ArrayContainer containerB = new ArrayContainer(4, 4);
        containerB.fillArray();
        int[][] arrayB = containerB.getArray();

        ArrayContainer containerC = new ArrayContainer(containerA.getSizeX(), containerB.getSizeY());
        containerC.initArray();
        int[][] arrayC = containerC.getArray();

        ArrayContainer progressContainer = new ArrayContainer(containerC.getSizeX(), containerC.getSizeY());
        progressContainer.initArray();
        int[][] progressArray = progressContainer.getArray();

        //ADD Handling Client behaviour and set members
        ServerCyclicBehaviour serverCyclicBehaviour = new ServerCyclicBehaviour();
        serverCyclicBehaviour.setArrayA(arrayA);
        serverCyclicBehaviour.setArrayB(arrayB);
        serverCyclicBehaviour.setArrayC(arrayC);
        serverCyclicBehaviour.setProgressArray(progressArray);
        addBehaviour(serverCyclicBehaviour);

        System.out.println("Server has been started");
    }
}