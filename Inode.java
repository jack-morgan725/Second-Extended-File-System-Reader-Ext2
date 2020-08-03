
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author Jack Morgan
 * @version 1.0
 * Represents a node within the volume.
 */
public class Inode 		
{
	private short fMode;
	private short userID;
	private short hLinks;
	private short groupID;
	
	private long accessTime;
	private long createTime;
	private long modTime;
	private long delTime;
	private long fSize; 
	
	private int[] bPointer = new int[12];
	private int sInd;
	private int dInd;
	private int tInd;
	private int lSize;
	private int uSize;
	
	/**
	 * Node constructor.
	 * @param inode (required) - ByteBuffer containing the bytes of an inode.
	 */
	public Inode(ByteBuffer inode) 
	{
		fMode = inode.order(ByteOrder.LITTLE_ENDIAN).getShort(0);
		userID  = inode.order(ByteOrder.LITTLE_ENDIAN).getShort(2);
		groupID = inode.order(ByteOrder.LITTLE_ENDIAN).getShort(24);
		hLinks  = inode.order(ByteOrder.LITTLE_ENDIAN).getShort(26);	
		
		lSize = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(4);
		uSize = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(108);
		
		accessTime = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(8);
		createTime = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(12);
		modTime = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(16);
		delTime = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(20);
	
        for (int i = 0, x = 0; i < (12*4); i += 4, x++) 
        	bPointer[x] = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(40+i);
        
        sInd = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(88);
        dInd = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(92);
        tInd = inode.order(ByteOrder.LITTLE_ENDIAN).getInt(96);
        
		fSize = uSize;
		fSize = (fSize << 32) | lSize;

	}
	
	/**
	 * Prints out the contents of an node.
	 */
	public void printNode() 
	{
		System.out.println("|>=======================================================================>");
		System.out.printf ("|> File Mode: %02X%n", fMode);
		System.out.println("|> User ID: " + userID);
		System.out.println("|> Group ID: " + groupID);
		System.out.println("|> File Size (L-32-Bits): " + lSize);
		System.out.println("|> File Size (U-32-Bits): " + uSize);
		System.out.println("|> Total File Size: " + fSize);
		System.out.println("|> Last Accessed (Seconds Past 1970): " + accessTime);
		System.out.println("|> Creation Time (Seconds Past 1970): " + createTime);
		System.out.println("|> Last Modified (Seconds Past 1970): " + modTime);
		System.out.println("|> Deletion Time (Seconds Past 1970): " + delTime);
		System.out.println("|> Hard Links : "    + hLinks);
		
		for (int i = 0; i < bPointer.length; i++) 
			System.out.format("|> Block Pointer %-2s ->  %s%n", (i+1), bPointer[i]);
		
		System.out.println("|> Single Indirect Pointer: " + sInd);
		System.out.println("|> Double Indirect Pointer: " + dInd);
		System.out.println("|> Triple Indirect Pointer: " + tInd);
		System.out.println("|>=======================================================================>");
	}
	
	/**
	 * Returns the file mode of the nodes associated file.
	 * @return fMod - file mode of the associated file.
	 */
	public short getMode() 
	{ 
		return fMode;   
	}
	
	/**
	 * Returns the user ID of the associated file.
	 * @return userID - user ID of the associated file.
	 */
	public short getUser() 
	{ 
		return userID;  
	}
	
	/**
	 * Returns the number of hard links that exist to the associated file.
	 * @return hLinks - number of hard links to the associated file.
	 */
	public short getLinks() 
	{ 
		return hLinks;  
	}
	
	/**
	 * Returns group ID of the associated file.
	 * @return groupID - group ID of the associated file.
	 */
	public short getGroup() 
	{ 
		return groupID; 
	}
	
	/**
	 * Returns the time the associated file was last accessed in seconds since 1970.
	 * @return accessTime - time the associated file was last accessed.
	 */
	public long getAccessTime() 
	{ 
		return accessTime; 
	}
	
	/**
	 * Returns the time the associated file was created in seconds since 1970.
	 * @return createTime - time the associated file was created.
	 */
	public long getCreationTime() 
	{ 
		return createTime; 
	}
	
	/**
	 * Returns the time the associated file was last modified in seconds since 1970. 
	 * @return modTime - time the associated file was last modified.
	 */
	public long getModTime() 
	{ 
		return modTime;    
	}
	
	/**
	 * Returns the time the associated file deleted in seconds since 1970.
	 * @return delTime - time the associated file was deleted.
	 */
	public long getDelTime() 
	{ 
		return delTime;    
	}
	
	/**
	 * Returns total size of the associated file in bytes.
	 * @return fSize - total file size in bytes.
	 */
	public long getFileSize() 
	{ 
		return fSize;      
	}
	
	/**
	 * Returns the single, indirect pointer of the associated file.
	 * @return sInd - single indirect pointer to the associated files data blocks.
	 */
	public int sInd() 
	{ 
		return sInd;  
	}
	
	/**
	 * Returns the double, indirect pointer of the associated file.
	 * @return dInt - double indirect pointer to the associated files data blocks.
	 */
	public int dInd() 
	{ 
		return dInd;  
	}
	
	/**
	 * Returns te triple, indirect pointer of the associated file.
	 * @return dInt - triple indirect pointer to the associated files data blocks.
	 */
	public int tInd() 
	{ 
		return tInd;  
	}
	
	/**
	 * Returns the lower 32-bits of the associated files size.
	 * @return lSize - lower 32-bits of the associated files size.
	 */
	public int getLoSize() 
	{ 
		return lSize; 
	}
	
	/**
	 * Returns upper 32-bits of the associated files size.
	 * @return uSize - upper 32-bits of the associated files size.
	 */
	public int getHiSize() 
	{
		return uSize; 
	}
	
	/**
	 * Returns an array of all 12 direct pointers for the associated file.
	 * @return blockPointer - 12 direct pointers that point to data blocks of the associated file.
	 */
	public int[] getPointers() 
	{ 
		return bPointer; 
	}
}






