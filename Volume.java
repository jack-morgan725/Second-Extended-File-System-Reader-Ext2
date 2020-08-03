
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Jack Morgan
 * @version 1.0
 * Represents a file in a volume. The file can be read from and it's size and position can be returned. 
 */
public class Volume 
{
	private String volPath;
	public Superblock sb;
	
	/**
	 * Volume constructor.
	 * @param volPath (required) - path to the volume.
	 */
	public Volume(String volPath) 
	{
		this.volPath = volPath;
		sb = new Superblock(getBlock(1));
	}
	
	/**
	 * Returns a ByteBuffer containing a block of data (1024 Bytes).
	 * @param blockPoint - block number to start reading from.
	 * @return buff - ByteBuffer containing bytes located between {@value offset} 
	 * and {@value length}.
	 */
	public ByteBuffer getBlock(long blockPoint) 
	{
		ByteBuffer buff = ByteBuffer.allocate(Superblock.SIZE_BLOCK);
	
		try 
		{
			RandomAccessFile vol = new RandomAccessFile(new File(volPath), "r");
			
			byte[] buffArr = new byte[Superblock.SIZE_BLOCK];
	
			vol.seek(blockPoint * Superblock.SIZE_BLOCK); 
			vol.read(buffArr);
			
			buff.put(buffArr).flip();	
			
			vol.close();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		
		return buff;
	}

	/**
	 * Returns a ByteBuffer containing {@value length} bytes starting from {@value blockNum} 
	 * plus {@value offset}.
	 * @param blockNum - block number to start reading from.
	 * @param offset - number of bytes past the {@value blockNum} to start from.
	 * @param lenth - number of bytes to read from the volume.
	 * @return buff - ByteBuffer containing bytes located between {@value offset} 
	 * and {@value length}.
	 */
	public ByteBuffer getBytes(int blockNum, int bOffset, int length) 
	{
		ByteBuffer buff = ByteBuffer.allocate(length); 
	
		try 
		{
			RandomAccessFile vol = new RandomAccessFile(new File(volPath), "r");
			
			byte[] buffArr = new byte[length];
			
			int tOffset = (blockNum * Superblock.SIZE_BLOCK) + bOffset;
			
			vol.seek(tOffset);
			
			vol.read(buffArr);
			
			buff.put(buffArr).flip();
			
			vol.close();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		
		return buff;
	}
	
	/**
	 * Returns an node based on the {@value iNum} supplied.
	 * @param iNum - associated number of the node that will be returned.
	 * @return node - node associated with the supplied {@value iNum}.
	 */
    public Inode getIn(int iNum) 
    {
    	int gNum = (iNum + Superblock.inodesPerGroup - 1 - (iNum - 1) % Superblock.inodesPerGroup) / Superblock.inodesPerGroup;	//--> Check what group number the node is in.		
    	
        ByteBuffer buff = getBlock(2);	//--> Get Group Descriptor block.													
        
        int tpStart = ((32 * gNum) - 32) + 8; //--> Check position of table pointer. 											
        
        int iTabPoint = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(tpStart); //--> Get table pointer.
  
        int iTableNum = iNum - (Superblock.inodesPerGroup * (gNum - 1)); //--> Check what position the node is in its table.
        
        int tOffset = (iTableNum-1) * Superblock.inodeSize;	//--> Calculate the offset from the table start to the target node.
        
        return new Inode(getBytes(iTabPoint, tOffset, Superblock.inodeSize));		
    }
}




























