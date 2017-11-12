import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandlingClientBehaviour extends CyclicBehaviour
{
    private int[][] arrayA;
    private int[][] arrayB;
    private int[][] arrayC;
    private int[][] progressArray;
    private String rows = "";
    private String columns = "";
    private int currentY = 0;
    private int currentX = -1;

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
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST)); //set pattern and filter to request
        if (msg!= null)
        {
            System.out.println("Serwer odebral " + msg.getContent());
            if(msg.getContent().contains(":"))
            {
                String[] partsMessage = msg.getContent().split(":");
                System.out.println("!!!!!!!!!!" + partsMessage[0]);
                int tempX = Integer.parseInt(partsMessage[0]);
                int tempY = Integer.parseInt(partsMessage[1]);
                arrayC[tempY][tempX] = Integer.parseInt(partsMessage[2]);
            }
            if((currentY == arrayC[0].length - 1) && (currentX == arrayC.length - 1))
            {
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
            else
            {
                if (currentX < arrayC.length - 1)
                {
                    currentX++;
                } else {
                    currentX = 0;
                    currentY++;
                }

            }

            System.out.println("----");
            System.out.println(currentX);//in tab this is SECOND Coordiante
            System.out.println(currentY);//in tab this is FIRST Coordinate

            for(int i = 0; i < arrayA[0].length; i++) {
                if(i == arrayA[0].length - 1)
                {
                    rows = rows + Integer.toString(arrayA[currentY][i]);
                }
                else
                {
                    rows = rows + Integer.toString(arrayA[currentY][i]) + ",";
                }
                }

            for(int i = 0; i < arrayB.length; i++) {
                if(i == arrayB.length - 1)
                {
                    columns = columns + Integer.toString(arrayB[i][currentX]);
                }
                else
                {
                    columns = columns + Integer.toString(arrayB[i][currentX]) + ",";
                }
            }


            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.REQUEST);
            reply.setContent(currentX + ":" + currentY + ":" + rows + ":" + columns);
            myAgent.send(reply);
            rows = "";
            columns = "";
        }
        else
        {
            block();
        }
    }
}