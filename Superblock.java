
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * @author Jack Morgan
 * @version 1.0
 * Represents a file in a volume. The file can be read from and it's size and position can be returned. 
 */
public class Superblock 
{
	public static final int SIZE_BLOCK = 1024;
	public static short magicNumber;
	public static int totalInodes;
	public static int totalBlocks;
	public static int blocksPerGroup;
	public static int inodesPerGroup;
    public static int inodeSize;
    public static String volLabel;
    
    /**
     * Superblock constructor.
     * @param buff (required) - ByteBuffer containing the superblock contents.
     */
    public Superblock(ByteBuffer buff) 
    {
    	totalInodes = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(0);
    	totalBlocks = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(4);
    	blocksPerGroup = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(32);
    	inodesPerGroup = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(40);
    	magicNumber = buff.order(ByteOrder.LITTLE_ENDIAN).getShort(56);
    	inodeSize = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(88);
    	volLabel = new String(Arrays.copyOfRange(buff.array(), 120, 136));
    }

    /**
     * Prints out the contents of the superblock.
     */
    public void printMeta() 
    {
        System.out.println("|>=======================================================================>");
        System.out.println("|> Volume Label: "                     + volLabel);
        System.out.println("|> Total Number of Inodes: "           + totalInodes);
        System.out.println("|> Total Number of Blocks: "           + totalBlocks);
        System.out.println("|> Total Number of Blocks Per Group: " + blocksPerGroup);
        System.out.println("|> Total Number of Inodes Per Group: " + inodesPerGroup);
        System.out.printf ("|> Magic Number: %02X %n",               magicNumber);
        System.out.println("|> Inode Byte Size: "                  + inodeSize);
        System.out.println("|> Block Byte Size: "                  + SIZE_BLOCK);
        System.out.println("|> Total number of Block Groups: "     + (int)Math.ceil((float)totalBlocks / (float)blocksPerGroup));
        System.out.println("|> Total Volume Byte Size: "           + SIZE_BLOCK * totalBlocks);
        System.out.println("|>=======================================================================>");
    }
}
