import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Vector;

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
    private Vector<String> vectorOfAgents = new Vector<>();

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
            if(!vectorOfAgents.contains(msg.getSender().toString())) //Add Agent Vector
                vectorOfAgents.addElement(msg.getSender().toString());
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
                //potwierdzenie
                String[] partsMessage = msg.getContent().split(":");
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);
                arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);
                //State 2 = get value from client
                progressArray[tempY][tempX] = 2;
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
        }
        else
        {
            block();
        }
    }
}