import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class HandlingServerBehaviour extends CyclicBehaviour
{
    private AID server;
    private int currentY = 0;
    private int currentX = 0;
    Vector<Integer> rowsInt = new Vector<Integer>();
    Vector<Integer> columnsInt = new Vector<Integer>();
    private int valueOfArrayC = 0;
    private StringBuilder msgStringBuilder = new StringBuilder("");
    private int failCalucaltion = 0;
    private int konyTemp = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setServer(AID server) {
        this.server = server;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void action()
    {
        ACLMessage msg = myAgent.receive(); //set pattern and filter to request
        if (msg!= null)
        {
            if(msg.getPerformative() == ACLMessage.REQUEST) //Make calculation
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

                failCalucaltion = ThreadLocalRandom.current().nextInt(1, 10 + 1);
                if(failCalucaltion < 3)
                {
                    System.out.println("_____________FAIL: my posioton " + currentX + currentY );

                    ACLMessage reply = new ACLMessage(ACLMessage.FAILURE);
                    reply.addReceiver(server);
                    msgStringBuilder.append(currentX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(currentY);
                    reply.setContent(msgStringBuilder.toString());
                    myAgent.send(reply);
                    block(2000);
                }
                else
                {
                    for (int i = 0; i < rowsString.length; i++)
                        valueOfArrayC = valueOfArrayC + rowsInt.elementAt(i) * columnsInt.elementAt(i);

                    block(ThreadLocalRandom.current().nextInt(1000, 1500 + 1));

                    ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
                    reply.addReceiver(server);
                    reply.setPerformative(ACLMessage.CONFIRM);
                    msgStringBuilder.append(currentX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(currentY);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                    System.out.println("_____________WARTOSC:" + valueOfArrayC);
                    myAgent.send(reply);
                }
                //clear
                msgStringBuilder.delete(0,msgStringBuilder.length());
                valueOfArrayC = 0;
                rowsInt.clear();
                columnsInt.clear();
            }
            else if(msg.getPerformative() == ACLMessage.CANCEL) //Die
            {
                myAgent.doDelete();
            }
        }
        else
        {
            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
            reply.addReceiver(server);
            myAgent.send(reply);
            block();
        }
    }
}