import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Vector;

public class HandlingServerBehaviour extends CyclicBehaviour
{
    private AID server;
    private int currentY = 0;
    private int currentX = 0;
    Vector<Integer> rowsInt = new Vector<Integer>();
    Vector<Integer> columnsInt = new Vector<Integer>();
    private int valueOfArrayC = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setServer(AID server) {
        this.server = server;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void action()
    {
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST)); //set pattern and filter to request
        if (msg!= null)
        {
            System.out.println("Client odebral " + msg.getContent());
            String[] partsMessage = msg.getContent().split(":");
            currentX = Integer.parseInt(partsMessage[0]);
            currentY = Integer.parseInt(partsMessage[1]);
            String[] rowsString = partsMessage[2].split(",");
            String[] columnsString = partsMessage[3].split(",");
            for(int i = 0; i < rowsString.length; i++)
                rowsInt.addElement(Integer.parseInt(rowsString[i]));
            for(int i = 0; i < columnsString.length; i++)
                columnsInt.addElement(Integer.parseInt(columnsString[i]));

            for(int i = 0; i < rowsString.length; i++)
                valueOfArrayC = valueOfArrayC + rowsInt.elementAt(i) * columnsInt.elementAt(i);


            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
            reply.addReceiver(server);
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent(currentX + ":" + currentY + ":" + valueOfArrayC);
            myAgent.send(reply);

            valueOfArrayC = 0;
            rowsInt.clear();
            columnsInt.clear();

        }
        else
        {
            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
            reply.addReceiver(server);
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent("hej");
            myAgent.send(reply);
            block();
        }
    }
}