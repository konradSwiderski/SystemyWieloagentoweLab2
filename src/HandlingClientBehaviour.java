import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class HandlingClientBehaviour extends CyclicBehaviour
{
    private int[][] arrayA;
    private int[][] arrayB;
    private int[][] arrayC;
    private int[][] progressArray;
    private StringBuilder rowsStringBuilder = new StringBuilder("");
    private StringBuilder columnsStringBuilder = new StringBuilder("");
    private StringBuilder msgStringBuilder = new StringBuilder("");
    private int currentY = 0;
    private int currentX = -1;
    private int numberOfFails = 0;
    private Vector<AID> vectorOfAgents = new Vector<>();
    private Vector<AID> bannedVectorOfAgents = new Vector<>();
    private int isVerification = 0;
    private int valueToVerification = 0;
    private int valueFromAgentTester = 0;
    private int valueFromAgentJudge = 0;
    private AID agentVerification = null;
    private AID agentTester = null;
    private AID agentJudge = null;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setArrayA(int[][] arrayA) {
        this.arrayA = arrayA;
    }

    public void setArrayB(int[][] arrayB) {
        this.arrayB = arrayB;
    }

    public void setArrayC(int[][] arrayC) {
        this.arrayC = arrayC;
    }

    public void setProgressArray(int[][] progressArray) {
        this.progressArray = progressArray;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void action()
    {
        ACLMessage msg = myAgent.receive();
        if (msg!= null)
        {
            if(!bannedVectorOfAgents.contains(msg.getSender())) //check for banned agents
            {
                //Add new Agent
                if(!vectorOfAgents.contains(msg.getSender()))
                    vectorOfAgents.addElement(msg.getSender());
                System.out.println("********************************************");
                for(int i =0; i < vectorOfAgents.size(); i++)
                    System.out.println("CORRECT: " + vectorOfAgents.elementAt(i));
                System.out.println("********");
                for(int i =0; i < bannedVectorOfAgents.size(); i++)
                    System.out.println("BANNED: " + bannedVectorOfAgents.elementAt(i));
                System.out.println("********************************************");
            }
            if(msg.getPerformative() == ACLMessage.REQUEST) //CLIENT IS READY #SENDING DATA TO HIM
            {
                if((currentY == arrayC[0].length - 1) && (currentX == arrayC.length - 1)) //it is the end of array?
                {
                    //checking progress array
                    System.out.println("____Time to check progress array____");
                    numberOfFails = 0;
                    outerLoop:
                    for(int i = 0; i < progressArray.length; i++)
                    {
                        for(int j = 0; j < progressArray[0].length; j++)
                        {
                            System.out.print(progressArray[i][j] + " ");
                            if(progressArray[i][j] != 2)
                            {
                                numberOfFails++;
                                System.out.println("__Found fail in position: " + i + j);

                                //Prepare rows and columns
                                prepareRowsAndColumns(i,j);

                                System.out.println("ROWS: " + rowsStringBuilder + "COLS: " + columnsStringBuilder + msg.getSender());

                                //Create msg with data
                                if(!bannedVectorOfAgents.contains(msg.getSender()))
                                {
//                                    ACLMessage reply = msg.createReply();
//                                    reply.setPerformative(ACLMessage.REQUEST);
//                                    msgStringBuilder.append(j);
//                                    msgStringBuilder.append(":");
//                                    msgStringBuilder.append(i);
//                                    msgStringBuilder.append(":");
//                                    msgStringBuilder.append(rowsStringBuilder);
//                                    msgStringBuilder.append(":");
//                                    reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
//                                    myAgent.send(reply);

                                    sendMessage(j,i,msg.getSender(), ACLMessage.REQUEST);
                                    //clear
                                    clearValues();
                                }
                                break outerLoop;
                            }
                        }
                        System.out.println();
                    }
                    if(numberOfFails == 0)
                    {
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.CANCEL);
                        myAgent.send(reply);
                        System.out.println("-----arrayC-----");
                        for(int i = 0; i < arrayC.length; i++)
                        {
                            for(int j = 0; j < arrayC[0].length; j++)
                            {
                                System.out.print(arrayC[i][j]);
                                System.out.print(" ");
                            }
                            System.out.println();
                        }
                        System.out.println("the end");
                    }

                    //reset
                    numberOfFails = 0;
                }
                else if (!bannedVectorOfAgents.contains(msg.getSender())) //it is not the end of array #check banned list
                {
                    if (currentX < arrayC.length - 1) {
                        currentX++;
                    } else {
                        currentX = 0;
                        currentY++;
                    }

                    //Prepare rows and columns
                    for (int i = 0; i < arrayA[0].length; i++)
                    {
                        rowsStringBuilder.append((arrayA[currentY][i]));
                        if (i != arrayA[0].length - 1)
                        {
                            rowsStringBuilder.append(",");
                        }

                    }
                    for (int i = 0; i < arrayB.length; i++)
                    {
                        columnsStringBuilder.append(arrayB[i][currentX]);
                        if (i != arrayB.length - 1)
                            columnsStringBuilder.append(",");
                    }

                    //Create msg with data
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.REQUEST);
                    msgStringBuilder.append(currentX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(currentY);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(rowsStringBuilder);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
                    myAgent.send(reply);

                    //State 1 = sent data to client
                    progressArray[currentY][currentX] = 1;

                    //Clear
                    clearValues();
                }
            }
            else if(msg.getPerformative() == ACLMessage.CONFIRM) //GET VALUE FROM CLIENT #VERIFICATIONS
            {
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);

                //Verification process
                isVerification++;
                if((isVerification % 3) == 0 && !bannedVectorOfAgents.contains(msg.getSender())) //Verification or not?
                {
                    agentVerification = msg.getSender();

                    System.out.println("Verification proces:  " + tempX + "," + tempY + " = " + Integer.parseInt(partsMessage[2]));
                    for(int i = 0; i < vectorOfAgents.size(); i++)
                    {
                        //Find agentTester
                        if(!vectorOfAgents.elementAt(i).toString().equals(agentVerification.toString()))
                        {
                            agentTester = vectorOfAgents.elementAt(i);
                            break;
                        }
                    }

                    //Prepare rows and columns
                    for (int k = 0; k < arrayA[0].length; k++)
                    {
                        rowsStringBuilder.append((arrayA[tempY][k]));
                        if (k != arrayA[0].length - 1)
                        {
                            rowsStringBuilder.append(",");
                        }
                    }
                    for (int k = 0; k < arrayB.length; k++)
                    {
                        columnsStringBuilder.append(arrayB[k][tempX]);
                        if (k != arrayB.length - 1)
                            columnsStringBuilder.append(",");
                    }

                    //Create and send message
                    ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                    reply.addReceiver(agentTester);
                    msgStringBuilder.append(tempX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(tempY);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(rowsStringBuilder);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
                    myAgent.send(reply);

                    //Clear
                    clearValues();

                    //Set value, which is in verification process
                    valueToVerification = Integer.parseInt(partsMessage[2]);
                }
                else if(!bannedVectorOfAgents.contains(msg.getSender())) //without verification process
                {
                    arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);

                    //State 2 = get value from client
                    progressArray[tempY][tempX] = 2;
                }
            }
            else if(msg.getPerformative() == ACLMessage.PROPOSE) //VERIFICATION LEVEL1 #agentTester
            {
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);

                if(valueToVerification == Integer.parseInt(partsMessage[2]))
                {
                    System.out.println("Result of verification: ACCEPT");

                    //The end of verification #Correct Value of arrayC
                    arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);

                    //State 2 = get value from client
                    progressArray[tempY][tempX] = 2;
                }
                else
                {
                    System.out.println("Result of verification: NOT ACCEPT");

                    //Find agentJudge
                    for(int i = 0; i < vectorOfAgents.size(); i++)
                    {
                        if(!vectorOfAgents.elementAt(i).toString().equals(agentVerification.toString()) && !vectorOfAgents.elementAt(i).toString().equals(agentTester.toString()))
                        {
                            agentJudge = vectorOfAgents.elementAt(i);
                            break;
                        }
                    }

                    //Prepare rows and columns
                    for (int k = 0; k < arrayA[0].length; k++)
                    {
                        rowsStringBuilder.append((arrayA[tempY][k]));
                        if (k != arrayA[0].length - 1) {
                            rowsStringBuilder.append(",");
                        }
                    }
                    for (int k = 0; k < arrayB.length; k++)
                    {
                        columnsStringBuilder.append(arrayB[k][tempX]);
                        if (k != arrayB.length - 1)
                            columnsStringBuilder.append(",");
                    }

                    //Create and send message
                    ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    reply.addReceiver(agentJudge);
                    msgStringBuilder.append(tempX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(tempY);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(rowsStringBuilder);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
                    myAgent.send(reply);

                    //Clear
                    clearValues();

                    //Set value from AgentTester
                    valueFromAgentTester = Integer.parseInt(partsMessage[2]);
                }
            }
            else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) //VERIFICATION LEVEL2 #agentJudge
            {
                if (!bannedVectorOfAgents.contains(msg.getSender()))
                {
                    String[] partsMessage = msg.getContent().split(":");
                    int tempX = Integer.parseInt(partsMessage[0]);
                    int tempY = Integer.parseInt(partsMessage[1]);

                    //Set value from AgentTester
                    valueFromAgentJudge = Integer.parseInt(partsMessage[2]);

                    //Who is fake?
                    if (valueFromAgentJudge == valueToVerification)
                    {
                        System.out.println("FAKE IS: " + agentTester);
                        bannedVectorOfAgents.addElement(agentTester);
                        vectorOfAgents.remove(agentTester);
                    }
                    else if (valueFromAgentJudge == valueFromAgentTester)
                    {
                        System.out.println("FAKE IS: " + agentVerification);
                        bannedVectorOfAgents.addElement(agentVerification);
                        vectorOfAgents.remove(agentVerification);
                    }

                    //The end of verification #Correct Value of arrayC
                    arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);

                    //State 2 = get value from client
                    progressArray[tempY][tempX] = 2;
                }
            }
            else if(msg.getPerformative() == ACLMessage.FAILURE) //FAIL FROM CLIENT
            {
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);

                //State -1 = fail from client
                progressArray[tempY][tempX] = -1;
            }
        }
        else
        {
            block();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void prepareRowsAndColumns(int x, int y)
    {
        for (int k = 0; k < arrayA[0].length; k++)
        {
            rowsStringBuilder.append((arrayA[x][k]));
            if (k != arrayA[0].length - 1)
            {
                rowsStringBuilder.append(",");
            }

        }
        for (int k = 0; k < arrayB.length; k++)
        {
            columnsStringBuilder.append(arrayB[k][y]);
            if (k != arrayB.length - 1)
                columnsStringBuilder.append(",");
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void clearValues()
    {
        msgStringBuilder.delete(0,msgStringBuilder.length());
        rowsStringBuilder.delete(0,rowsStringBuilder.length());
        columnsStringBuilder.delete(0,columnsStringBuilder.length());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void sendMessage(int x, int y, AID client, int type)
    {
        ACLMessage reply = new ACLMessage(type);
        reply.addReceiver(client);
        msgStringBuilder.append(x);
        msgStringBuilder.append(":");
        msgStringBuilder.append(y);
        msgStringBuilder.append(":");
        msgStringBuilder.append(rowsStringBuilder);
        msgStringBuilder.append(":");
        reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
        myAgent.send(reply);
    }
}