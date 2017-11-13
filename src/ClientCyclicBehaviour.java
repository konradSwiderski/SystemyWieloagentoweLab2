import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class ClientCyclicBehaviour extends CyclicBehaviour {
    private AID server;
    private int currentY = 0;
    private int currentX = 0;
    private Vector<Integer> rowsInt = new Vector<>();
    private Vector<Integer> columnsInt = new Vector<>();
    private String[] rowsString;
    private String[] columnsString;
    private int valueOfArrayC = 0;
    private StringBuilder msgStringBuilder = new StringBuilder("");

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setServer(AID server) {
        this.server = server;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void action()
    {
        ACLMessage msg = myAgent.receive();
        if (msg != null)
        {
            if (msg.getPerformative() == ACLMessage.REQUEST) //MAKE CALCULATIONS
            {
                //parse message
                parseMessage(msg.getContent());

                //will be fail?
                int failCalculations = ThreadLocalRandom.current().nextInt(1, 10 + 1);

                if (failCalculations < 3) //Fail
                {
                    //prepare and send message
                    ACLMessage reply = prepareMessage(ACLMessage.FAILURE);
                    reply.setContent(msgStringBuilder.toString());
                    myAgent.send(reply);

                    //block client
                    block(2000);
                }
                else //No fail
                {
                    //get value of ArrayC
                    makeCalculations();

                    if (isMaliciousAgent()) //this is maliciousAgent? if yes, valueOfArrayC = 0;
                        valueOfArrayC = 0;

                    //block client
                    block(ThreadLocalRandom.current().nextInt(1000, 1500 + 1));

                    //prepare and send message
                    ACLMessage reply = prepareMessage(ACLMessage.CONFIRM);
                    reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                    myAgent.send(reply);
                }

                //clear
                clearAndReset();
            }
            else if (msg.getPerformative() == ACLMessage.PROPOSE) //VERIFICATION LEVEL1 #agentTester
            {
                //parse message
                parseMessage(msg.getContent());

                //get value of ArrayC
                makeCalculations();

                //prepare and send message
                ACLMessage reply = prepareMessage(ACLMessage.PROPOSE);
                reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                myAgent.send(reply);

                //clear
                clearAndReset();
            }
            else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) //VERIFICATION LEVEL2 #agentJudge
            {
                //parse message
                parseMessage(msg.getContent());

                //get value of ArrayC
                makeCalculations();

                //prepare and send message
                ACLMessage reply = prepareMessage(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                myAgent.send(reply);

                //clear
                clearAndReset();
            }
            else if (msg.getPerformative() == ACLMessage.CANCEL) //DIE
            {
                //kill agent
                myAgent.doDelete();
            }
        }
        else //SEND REQUEST #I'm ready
        {
            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
            reply.addReceiver(server);
            myAgent.send(reply);
            block();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isMaliciousAgent()
    {
        if (myAgent.getClass().toString().equals("class MaliciousAgent"))
            return true;
        else
            return false;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void parseMessage(String message)
    {
        String[] partsMessage = message.split(":"); //currentX:currentY:rows:columns
        currentX = Integer.parseInt(partsMessage[0]);
        currentY = Integer.parseInt(partsMessage[1]);

        //get rows and columns
        rowsString = partsMessage[2].split(",");
        columnsString = partsMessage[3].split(",");
        for (int i = 0; i < rowsString.length; i++)
            rowsInt.addElement(Integer.parseInt(rowsString[i]));
        for (int i = 0; i < columnsString.length; i++)
            columnsInt.addElement(Integer.parseInt(columnsString[i]));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void makeCalculations()
    {
        for (int i = 0; i < rowsString.length; i++)
            valueOfArrayC = valueOfArrayC + rowsInt.elementAt(i) * columnsInt.elementAt(i);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void clearAndReset()
    {
        msgStringBuilder.delete(0, msgStringBuilder.length());
        valueOfArrayC = 0;
        rowsInt.clear();
        columnsInt.clear();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ACLMessage prepareMessage(int type)
    {
        ACLMessage reply = new ACLMessage(type);
        reply.addReceiver(server);
        msgStringBuilder.append(currentX);
        msgStringBuilder.append(":");
        msgStringBuilder.append(currentY);
        msgStringBuilder.append(":");

        return reply;
    }
}