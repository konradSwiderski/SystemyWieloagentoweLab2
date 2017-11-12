public class OutputContainer
{
    private Integer sizeX = 0;
    private Integer sizeY = 0;
    private int[][] outputArray;
    private int[][] progressArray;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Integer getSizeX() {
        return sizeX;
    }

    public Integer getSizeY() {
        return sizeY;
    }

    public int[][] getProgressArray() {
        return progressArray;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    OutputContainer(int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        outputArray = new int[sizeX][sizeY];
        progressArray = new int[sizeX][sizeY];
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initProgressArray()
    {
        for(int i = 0; i < sizeX; i++)
        {
            for(int j = 0; j < sizeY; j++)
            {
                progressArray[i][j] = 0;
            }
        }
    }
}
