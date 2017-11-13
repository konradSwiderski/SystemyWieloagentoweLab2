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
    private String rows = "";
    private String columns = "";
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
            System.out.println("MY NAME IS: " + msg.getSender());
            if(!bannedVectorOfAgents.contains(msg.getSender()))
            {
                System.out.println("MY NAME IS: " + msg.getSender() + "++++++++++++++++++++++++++++++++Masz prawo agencie");
                if(!vectorOfAgents.contains(msg.getSender())) //Add Agent Vector
                    vectorOfAgents.addElement(msg.getSender());
                System.out.println("********************************************");
                for(int i =0; i < vectorOfAgents.size(); i++)
                {
                    System.out.println("CORRECT " + vectorOfAgents.elementAt(i));
                }
                System.out.println("********");
                for(int i =0; i < bannedVectorOfAgents.size(); i++)
                {
                    System.out.println("BANNED " + bannedVectorOfAgents.elementAt(i));
                }
                System.out.println("********************************************");
            }
            else
            {
                System.out.println("MY NAME IS: " + msg.getSender() + "++++++++++++++++++++++++++++++++Nie masz prawa agencie");
//                ACLMessage reply = msg.createReply();
//                reply.setPerformative(ACLMessage.CANCEL);
//                myAgent.send(reply);
            }


            if(msg.getPerformative() == ACLMessage.REQUEST) //Client is ready
            {
                if((currentY == arrayC[0].length - 1) && (currentX == arrayC.length - 1)) //ALL ELEMENTS?
                {
                    //CHECKING PROGRESS ARRAY
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
                                System.out.println("WYKRYLEM FAILA: " + j + i);
                                System.out.print("????????????????????????????????");
                                System.out.println(arrayB[j][i]);

                                ACLMessage reply = msg.createReply();
                                reply.setPerformative(ACLMessage.REQUEST);

                                //rows and columns
                                for (int k = 0; k < arrayA[0].length; k++) {
                                    rowsStringBuilder.append((arrayA[i][k]));
                                    if (k != arrayA[0].length - 1) {
                                        rowsStringBuilder.append(",");
                                    }

                                }
                                System.out.print("ROWS: ");
                                System.out.println(rowsStringBuilder);

                                for (int k = 0; k < arrayB.length; k++) {
                                    columnsStringBuilder.append(arrayB[k][j]);
                                    if (k != arrayB.length - 1)
                                        columnsStringBuilder.append(",");
                                }
                                System.out.print("COLUMNS: ");
                                System.out.println(columnsStringBuilder);
                                //Create msg
                                msgStringBuilder.append(j);
                                msgStringBuilder.append(":");
                                msgStringBuilder.append(i);
                                msgStringBuilder.append(":");
                                msgStringBuilder.append(rowsStringBuilder);
                                msgStringBuilder.append(":");
                                reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
                                myAgent.send(reply);
                                msgStringBuilder.delete(0,msgStringBuilder.length());
                                rowsStringBuilder.delete(0,rowsStringBuilder.length());
                                columnsStringBuilder.delete(0,columnsStringBuilder.length());
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
                        System.out.println("-----vectorOfAgents-----");
                        for(int i = 0; i < vectorOfAgents.size(); i++)
                            System.out.println(vectorOfAgents.elementAt(i));
                        System.out.println("the end");
                    }

                    //reset
                    numberOfFails = 0;
                }
                else {
                    if (currentX < arrayC.length - 1) {
                        currentX++;
                    } else {
                        currentX = 0;
                        currentY++;
                    }


                    for (int i = 0; i < arrayA[0].length; i++) {
                        rowsStringBuilder.append((arrayA[currentY][i]));
                        if (i != arrayA[0].length - 1) {
                            rowsStringBuilder.append(",");
                        }

                    }

                    for (int i = 0; i < arrayB.length; i++) {
                        columnsStringBuilder.append(arrayB[i][currentX]);
                        if (i != arrayB.length - 1)
                            columnsStringBuilder.append(",");
                    }

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
                    //State 1 = send to client
                    progressArray[currentY][currentX] = 1;

                    //Clear
                    rowsStringBuilder.delete(0, rowsStringBuilder.length());
                    columnsStringBuilder.delete(0, columnsStringBuilder.length());
                    msgStringBuilder.delete(0, msgStringBuilder.length());
                }
            }
            else if(msg.getPerformative() == ACLMessage.CONFIRM) //Get value of ArrayC
            {
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);
                //weryfikacja////////////////////////////////////////////////////////////////////////////////////////////////////
                isVerification++;
                if((isVerification % 3) == 0)
                {
                    agentVerification = msg.getSender();

                    System.out.println("////////////////////////////////////////Proces weryfikacji " + tempX + "," + tempY + " = " + Integer.parseInt(partsMessage[2]));
                    for(int i = 0; i < vectorOfAgents.size(); i++)
                    {

                        if(!vectorOfAgents.elementAt(i).toString().equals(agentVerification.toString()))
                        {
                            agentTester = vectorOfAgents.elementAt(i);
                            break;
                        }
                    }

                    System.out.println("agentWeryfikowany " + agentVerification);
                    System.out.println("agentTestowany " + agentTester);
                    System.out.println("////////////////////////////////////////");
                    //send msg with x,y,rows,and columns////////////////////////////////////////////////////////////////////////////
                    ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
                    reply.addReceiver(agentTester);
                    //rows and columns
                    for (int k = 0; k < arrayA[0].length; k++) {
                        rowsStringBuilder.append((arrayA[tempY][k]));
                        if (k != arrayA[0].length - 1) {
                            rowsStringBuilder.append(",");
                        }

                    }
                    System.out.print("ROWS: ");
                    System.out.println(rowsStringBuilder);

                    for (int k = 0; k < arrayB.length; k++) {
                        columnsStringBuilder.append(arrayB[k][tempX]);
                        if (k != arrayB.length - 1)
                            columnsStringBuilder.append(",");
                    }
                    System.out.print("COLUMNS: ");
                    System.out.println(columnsStringBuilder);
                    System.out.println("//////// KONIEC WERYFIKACJI ROWS " + rowsStringBuilder + "COLUMNS " + columnsStringBuilder);
                    //Create msg
                    msgStringBuilder.append(tempX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(tempY);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(rowsStringBuilder);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
                    myAgent.send(reply);
                    msgStringBuilder.delete(0,msgStringBuilder.length());
                    rowsStringBuilder.delete(0,rowsStringBuilder.length());
                    columnsStringBuilder.delete(0,columnsStringBuilder.length());

                    valueToVerification = Integer.parseInt(partsMessage[2]);
                }
                else
                {
                    arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);
                    progressArray[tempY][tempX] = 2; //State 2 = get value from client
                }

            }
            else if(msg.getPerformative() == ACLMessage.FAILURE)
            {
                System.out.println("Serwer odebral FAILA " + msg.getContent());
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY= Integer.parseInt(partsMessage[1]);

//                progressArray[tempY][tempX] = 0;
                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>.");
                System.out.println(arrayB[tempX][tempY]);
            }
            else if(msg.getPerformative() == ACLMessage.PROPOSE) //agentTester
            {
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);
                System.out.println("PROPOSE: " + myAgent.getName() + " X " + currentX + " Y " + currentY + " = " + Integer.parseInt(partsMessage[2]));

                if(valueToVerification == Integer.parseInt(partsMessage[2]))
                {
                    System.out.println(")))))))))))))))))Weryfikacja przeprowdzona: ZGODA");
                    arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);
                    progressArray[tempY][tempX] = 2; //State 2 = get value from client
                }
                else
                {
                    System.out.println(")))))))))))))))))Weryfikacja przeprowdzona: NIEZGODA");
                    arrayC[tempY][tempX] = -1;
                    progressArray[tempY][tempX] = 2; //State 2 = get value from client
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    for(int i = 0; i < vectorOfAgents.size(); i++)
                    {

                        if(!vectorOfAgents.elementAt(i).toString().equals(agentVerification.toString()) && !vectorOfAgents.elementAt(i).toString().equals(agentTester.toString()))
                        {
                            agentJudge = vectorOfAgents.elementAt(i);
                            System.out.println(")))))))))))))))))Weryfikacja przeprowdzona: NIEZGODA JUDGE " + agentJudge);
                            break;
                        }
                    }
//
                    ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    reply.addReceiver(agentJudge);
                    //rows and columns
                    for (int k = 0; k < arrayA[0].length; k++) {
                        rowsStringBuilder.append((arrayA[tempY][k]));
                        if (k != arrayA[0].length - 1) {
                            rowsStringBuilder.append(",");
                        }

                    }
                    System.out.print("ROWS: ");
                    System.out.println(rowsStringBuilder);

                    for (int k = 0; k < arrayB.length; k++) {
                        columnsStringBuilder.append(arrayB[k][tempX]);
                        if (k != arrayB.length - 1)
                            columnsStringBuilder.append(",");
                    }
                    System.out.print("COLUMNS: ");
                    System.out.println(columnsStringBuilder);
                    System.out.println("//////// KONIEC WERYFIKACJI ROWS " + rowsStringBuilder + "COLUMNS " + columnsStringBuilder);
                    //Create msg
                    msgStringBuilder.append(tempX);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(tempY);
                    msgStringBuilder.append(":");
                    msgStringBuilder.append(rowsStringBuilder);
                    msgStringBuilder.append(":");
                    reply.setContent(msgStringBuilder.append(columnsStringBuilder).toString());
                    myAgent.send(reply);
                    msgStringBuilder.delete(0,msgStringBuilder.length());
                    rowsStringBuilder.delete(0,rowsStringBuilder.length());
                    columnsStringBuilder.delete(0,columnsStringBuilder.length());
//
                    valueFromAgentTester = Integer.parseInt(partsMessage[2]);
//

                }
            }
            else if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) //agentJudge
            {
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);
                System.out.println("SERWER ACCEPT_PROPOSAL: " + myAgent.getName() + " X " + currentX + " Y " + currentY + " = " + Integer.parseInt(partsMessage[2]));
                valueFromAgentJudge = Integer.parseInt(partsMessage[2]);
                if(valueFromAgentJudge == valueToVerification)
                {
                    System.out.println("))))))))))))SERWER MOWI: KLAMCA JEST " +  agentTester);
                    bannedVectorOfAgents.addElement(agentTester);
                    vectorOfAgents.remove(agentTester);
                }
                else if(valueFromAgentJudge == valueFromAgentTester) {
                    System.out.println("))))))))))))SERWER MOWI: KLAMCA JEST " + agentVerification);
                    bannedVectorOfAgents.addElement(agentVerification);
                    vectorOfAgents.remove(agentVerification);
                }
                arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);
                progressArray[tempY][tempX] = 2; //State 2 = get value from client
            }

        }
        else
        {
            block();
        }
    }
}