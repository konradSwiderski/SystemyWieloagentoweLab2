import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class InputContainer
{
    private Integer sizeX = 0;
    private Integer sizeY = 0;
    private int[][] inputArray;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer getSizeX() {
        return sizeX;
    }

    public Integer getSizeY() {
        return sizeY;
    }

    public int[][] getInputArray() {
        return inputArray;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    InputContainer(int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        inputArray = new int[sizeX][sizeY];
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void fillArray()
    {
        int temp = 1;
        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                inputArray[i][j] = temp;
                temp++;
            }
        }
    }
}
