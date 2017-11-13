public class InputContainer
{
    private Integer sizeX = 0;
    private Integer sizeY = 0;
    private int[][] array;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer getSizeX() {
        return sizeX;
    }

    public Integer getSizeY() {
        return sizeY;
    }

    public int[][] getArray() {
        return array;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    InputContainer(int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        array = new int[sizeX][sizeY];
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void fillArray()
    {
        int temp = 1;
        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                array[i][j] = temp;
                temp++;
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initArray()
    {
        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                array[i][j] = 0;
            }
        }
    }
}
