
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * @author Jack Morgan
 * @version 1.0
 * Returns the contents of a directory in {@value fileInfo} format.
 */
public class Directory 
{
	/**
	 * Returns the contents of a directory in {@value fileInfo} format.
	 * @param dirPointers (required) - data block pointers to the directory.
	 * @param vol (required) - volume that contains the directory.
	 * @return fList - ArrayList containing all {@value FileInfo} objects for each file.
	 */
	public static ArrayList<FileInfo> getFileInfo(int[] dirPointers, Volume vol) 
	{			
		ArrayList<FileInfo> fList = new ArrayList<FileInfo>();
		
		for (int pIndex = 0; pIndex < dirPointers.length; pIndex++) //--> Loop through all directory block pointers.
		{
			if (dirPointers[pIndex] > 0) //--> Check if a block pointer exists.
			{
				ByteBuffer buff = vol.getBlock(dirPointers[pIndex]);	
		
				int startPos = 0;
				
				while(buff.hasRemaining()) //--> Search entire block.
				{
					int fInNum   = buff.order(ByteOrder.LITTLE_ENDIAN).getInt();
					short entLen = buff.order(ByteOrder.LITTLE_ENDIAN).getShort();
					byte namLen  = buff.get();
					byte fType   = buff.get();
					
					byte[] fName = new byte[namLen];
					buff.get(fName);
				
					Inode fNode = vol.getIn(fInNum);
					
					if (fNode.getLinks() < 0) //--> Ignore corrupted file remnants.
						break;
					
					//--> System.out.println("|> File Name: " + new String(fName));
					//--> fNode.printNode();

					fList.add(new FileInfo(fNode, fName, fType)); 
				
					startPos += entLen;	
					buff.position(startPos);
				}
			}
		}
		return fList;
	}
}