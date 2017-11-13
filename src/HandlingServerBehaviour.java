import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class HandlingServerBehaviour extends CyclicBehaviour {
    private AID server;
    private int currentY = 0;
    private int currentX = 0;
    Vector<Integer> rowsInt = new Vector<Integer>();
    Vector<Integer> columnsInt = new Vector<Integer>();
    private int valueOfArrayC = 0;
    private StringBuilder msgStringBuilder = new StringBuilder("");
    private int failCalculations = 0;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setServer(AID server) {
        this.server = server;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void action() {
        isMaliciousAgent(); //normal or malicious agent?

        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.REQUEST) //MAKE CALCULATIONS
            {
                String[] partsMessage = msg.getContent().split(":"); //currentX:currentY:rows:columns
                currentX = Integer.parseInt(partsMessage[0]);
                currentY = Integer.parseInt(partsMessage[1]);

                //get rows and columns
                String[] rowsString = partsMessage[2].split(",");
                String[] columnsString = partsMessage[3].split(",");
                for (int i = 0; i < rowsString.length; i++)
                    rowsInt.addElement(Integer.parseInt(rowsString[i]));
                for (int i = 0; i < columnsString.length; i++)
                    columnsInt.addElement(Integer.parseInt(columnsString[i]));

                failCalculations = ThreadLocalRandom.current().nextInt(1, 10 + 1);//will be fail?
                if (failCalculations < 3) { //Fail
                    System.out.println("failCalculation " + currentX + currentY);

                    ACLMessage reply = new ACLMessage(ACLMessage.FAILURE);
                    reply.addReceiver(server);
                    msgStringBuilder.append(currentX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(currentY);
                    reply.setContent(msgStringBuilder.toString());
                    myAgent.send(reply);

                    block(2000); //block client
                } else { //No fail
                    for (int i = 0; i < rowsString.length; i++) //get fragment of arrayC
                        valueOfArrayC = valueOfArrayC + rowsInt.elementAt(i) * columnsInt.elementAt(i);

                    if (isMaliciousAgent()) //this is maliciousAgent? if yes, valueOfArrayC = 0;
                        valueOfArrayC = 0;

                    block(ThreadLocalRandom.current().nextInt(1000, 1500 + 1)); //block client

                    //send message with value
                    ACLMessage reply = new ACLMessage(ACLMessage.CONFIRM);
                    reply.addReceiver(server);;
                    msgStringBuilder.append(currentX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(currentY);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                    myAgent.send(reply);
                }

                //clear
                msgStringBuilder.delete(0, msgStringBuilder.length());
                valueOfArrayC = 0;
                rowsInt.clear();
                columnsInt.clear();
            } else if (msg.getPerformative() == ACLMessage.CANCEL) //DIE
            {
                myAgent.doDelete();
            } else if (msg.getPerformative() == ACLMessage.PROPOSE) //VERIFICATION LEVEL1 #agentTester
            {
                String[] partsMessage = msg.getContent().split(":"); //currentX:currentY:rows:columns
                currentX = Integer.parseInt(partsMessage[0]);
                currentY = Integer.parseInt(partsMessage[1]);

                //get rows and columns
                String[] rowsString = partsMessage[2].split(",");
                String[] columnsString = partsMessage[3].split(",");
                for (int i = 0; i < rowsString.length; i++)
                    rowsInt.addElement(Integer.parseInt(rowsString[i]));
                for (int i = 0; i < columnsString.length; i++)
                    columnsInt.addElement(Integer.parseInt(columnsString[i]));

                for (int i = 0; i < rowsString.length; i++) //get fragment of arrayC
                    valueOfArrayC = valueOfArrayC + rowsInt.elementAt(i) * columnsInt.elementAt(i);

                //create message
                ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                reply.addReceiver(server);
                msgStringBuilder.append(currentX);
                msgStringBuilder.append(":");
                msgStringBuilder.append(currentY);
                msgStringBuilder.append(":");
                reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                myAgent.send(reply);

                //clear
                msgStringBuilder.delete(0, msgStringBuilder.length());
                valueOfArrayC = 0;
                rowsInt.clear();
                columnsInt.clear();
            } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) //VERIFICATION LEVEL2 #agentJudge
            {
                String[] partsMessage = msg.getContent().split(":"); //currentX:currentY:rows:columns
                currentX = Integer.parseInt(partsMessage[0]);
                currentY = Integer.parseInt(partsMessage[1]);

                //get rows and columns
                String[] rowsString = partsMessage[2].split(",");
                String[] columnsString = partsMessage[3].split(",");
                for (int i = 0; i < rowsString.length; i++)
                    rowsInt.addElement(Integer.parseInt(rowsString[i]));
                for (int i = 0; i < columnsString.length; i++)
                    columnsInt.addElement(Integer.parseInt(columnsString[i]));

                for (int i = 0; i < rowsString.length; i++) //get fragment of arrayC
                    valueOfArrayC = valueOfArrayC + rowsInt.elementAt(i) * columnsInt.elementAt(i);

                //create message
                ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                reply.addReceiver(server);
                msgStringBuilder.append(currentX);
                msgStringBuilder.append(":");
                msgStringBuilder.append(currentY);
                msgStringBuilder.append(":");
                reply.setContent(msgStringBuilder.append(valueOfArrayC).toString());
                myAgent.send(reply);

                //clear
                msgStringBuilder.delete(0, msgStringBuilder.length());
                valueOfArrayC = 0;
                rowsInt.clear();
                columnsInt.clear();
            }
        } else {
            ACLMessage reply = new ACLMessage(ACLMessage.REQUEST); //I'm ready to get new fragment
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
}