
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Jack Morgan
 * @version 1.0
 * Represents a file in a volume. The file can be read from and it's size and position can be returned. 
 */
public class Ext2File 
{
	private Volume vol;
	private String fPath;
	private long fPos;
	
	/**
	 * Ext2File constructor.
	 * @param vol (required) volume that the target file is stored on.
	 * @param filePath (required) path to the target file located within the volume.
	 * **/
	public Ext2File(Volume vol, String filePath) 
	{
		this.vol = vol;
		this.fPath = filePath;
		this.fPos = 0;
	}

	/**
	 * Returns the node for a file / directory and outputs the contents of a directory
	 * providing that the target element is a directory. 
	 * @return fNode - the node for this file.
	 */
	public Inode getFileNode() 
	{
		ByteBuffer buff = vol.getBlock(2);
		
		int pIndex = 0;
		int iTabPointer = buff.order(ByteOrder.LITTLE_ENDIAN).getInt(8);
		
		Inode iRoot = new Inode(vol.getBytes(iTabPointer, Superblock.inodeSize, Superblock.inodeSize)); 
		ArrayList<FileInfo> dir = Directory.getFileInfo(iRoot.getPointers(), vol);		
		
        String[] pathElements = fPath.split("/"); 
        
        while (true) //--> Loops through directory contents until final path element is found.
        {
        	int tempIndex = pIndex; 
        	
    		if (pIndex > pathElements.length-1) //--> If all path elements checked and file not found -> return null.
    			return null;
    		 
            for(int i = 0; i < dir.size(); i++) //--> Scan through all files in the current directory.
            {
            	if (pathElements[pIndex].equals(dir.get(i).getName()) && dir.get(i).getType() == 2)	  //--> If the path element is found and is a directory ->
            	{	
            		
            		Inode fInode = dir.get(i).getNode(); //--> Get node of the directory we're entering.
            		
            		dir = Directory.getFileInfo(fInode.getPointers(), vol); //--> Get directory contents.
            		pIndex++;
            		
            		if (pIndex > pathElements.length-1)	 //--> If the final path element was a directory -> display its contents and return node.
            		{
            			System.out.format("|> %n|> ");
            			
            			for(int j = 0; j < dir.size(); j++) 
            				System.out.format("%s %s %s %s %s %s %s %n|> ", 
            						dir.get(j).getPerm(), 
            						dir.get(j).getLinks(), 
            						dir.get(j).getGroup(), 
            						dir.get(j).getUser(), 
            						dir.get(j).getSize(), 
            						dir.get(j).getTime(), 
            						dir.get(j).getName());
           
            			return fInode; 
            		}
            	        		
            		break; 
            	}
            	else if (pathElements[pIndex].equals(dir.get(i).getName()) && dir.get(i).getType() == 1) //--> If the path element is found and is a regular file -> return node.
            	{
            		return dir.get(i).getNode();
            	}
            
            }
            
            if (tempIndex == pIndex) //--> If file not found (incorrect path entered) then return null.
            	return null;
        }
	}
	
	/**
	 * Returns an array of bytes of size {@value length} located at file start plus {@value offset}.
	 * @param offset - total number of bytes to skip before reading file.
	 * @param length - total number of bytes to read from the {@value offset} point.
	 * @return byteArr - length number of bytes read from the files byte position up to {@value length}.
	 */
	public byte[] read(long offset, long length) 
	{
		fPos = offset;
		
		try
		{
			if (offset > 0 && offset < this.getSize()) 
			{
				
				return this.read(length);
			}
			else 
			{
				throw new EOFException();
			}
		}
		catch(EOFException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Sets the file's byte position to {@value position}.
	 * @param position - updated file byte position.
	 */
	public void seek(long position) 
	{
		fPos = position;
	}
	
	/**
	 * Returns the current file byte position.
	 * @return fPos - current file byte position.
	 */
	public long position() 
	{
		return fPos;
	}
	
	/**
	 * Returns the size of a file. Returns 0 if file cannot be found.
	 * @return fSize - size of file.
	 */
	public long getSize() 
	{
		Inode fNode = this.getFileNode();
		
		if (fNode == null) 
			return 0;
		else 
			return fNode.getFileSize();
	}
	
	/**
	 * @param length - number of bytes to read from the file.
	 * @return byteArr - length number of bytes read from the files byte position up to {@value length}.
	 */
	public byte[] read(long length) 
	{
		ArrayList<Byte> bytes = new ArrayList<Byte>();	
		Inode fNode = this.getFileNode();

		if (fNode == null)
			return null;
		
		int cPos = 0;
		int[] blocks = fNode.getPointers();
		
		/**--> Direct <--**/
		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i] == 0) //--> If block pointer doesn't point anywhere -> Continue.
				continue;
	
			ByteBuffer fileBlock = vol.getBlock(blocks[i]);
			while(fileBlock.hasRemaining())
			{
				byte cByte = fileBlock.get();
				
				if (++cPos > fPos && bytes.size() < length)  
					bytes.add(cByte); 
			}
		}
		
		/**--> Single Indirection <--**/
		if (fNode.sInd() > 0) 
		{
			int sPoint = fNode.sInd();
			
			ByteBuffer blockOne = vol.getBlock(sPoint);
		
			while (blockOne.hasRemaining()) //--> Looping through initial data block from node pointer.
			{
				int blockPoint = blockOne.order(ByteOrder.LITTLE_ENDIAN).getInt();		            
				
				if (blockPoint == 0) //--> If block pointer doesn't point anywhere -> Continue.
					continue;
				
				ByteBuffer dataBlock = vol.getBlock(blockPoint);	
				
				while(dataBlock.hasRemaining()) //--> Reading file data blocks (non-inode pointer blocks).
				{
					byte cByte = dataBlock.get();	
					
					if (++cPos > fPos && bytes.size() < length) 				
						bytes.add(cByte);
				}
			}
		}
		
		/**--> Double Indirection <--**/
		if (fNode.dInd() > 0) 
		{
			int dPoint = fNode.dInd();
		
			ByteBuffer blockOne = vol.getBlock(dPoint);
			
			while (blockOne.hasRemaining()) //--> Looping through initial data block from node pointer.
			{
				
				int bPoint = blockOne.order(ByteOrder.LITTLE_ENDIAN).getInt(); 
				
				if (bPoint == 0) //--> If block pointer doesn't point anywhere -> Continue.
					continue;
				
				ByteBuffer blockTwo = vol.getBlock(bPoint);	
				
				while(blockTwo.hasRemaining()) //--> Looping through data blocks pointed to by initial block contents.
				{
					
					bPoint = blockTwo.order(ByteOrder.LITTLE_ENDIAN).getInt();
					
					if (bPoint == 0) 
						continue;
					
					ByteBuffer iDataBlock = vol.getBlock(bPoint);
					
					while(iDataBlock.hasRemaining()) //--> Reading file data blocks (non-node pointer blocks).
					{
						byte cByte = iDataBlock.get();	
						
						if (++cPos > fPos && bytes.size() < length) 				
							bytes.add(cByte);
					}
				}
			}
		}
		
		/**--> Triple Indirection <--**/
		if (fNode.tInd() > 0) 
		{
			int tPoint = fNode.tInd();
		
			ByteBuffer blockOne = vol.getBlock(tPoint);
			
			while (blockOne.hasRemaining()) //--> Looping through initial data block from node pointer.
			{
				int bPoint = blockOne.order(ByteOrder.LITTLE_ENDIAN).getInt();
		
				if (bPoint == 0) //--> If block pointer doesn't point anywhere -> Continue.
					continue;
				
				ByteBuffer blockTwo = vol.getBlock(bPoint);	
				
				while(blockTwo.hasRemaining())	//--> Looping through data blocks pointed to by initial block contents.
				{
					bPoint = blockTwo.order(ByteOrder.LITTLE_ENDIAN).getInt();
					
					if (bPoint == 0) 
						continue;
					
					ByteBuffer blockThree = vol.getBlock(bPoint);
					
					while(blockThree.hasRemaining()) //--> Looping through data blocks pointed to by previous block contents.
					{
						bPoint = blockThree.order(ByteOrder.LITTLE_ENDIAN).getInt();
						
						if (bPoint == 0) 
							continue;
						
						ByteBuffer dataBlock = vol.getBlock(bPoint);
					
						while(dataBlock.hasRemaining()) //--> Reading file data blocks (non-node pointer blocks).
						{
							byte cByte = dataBlock.get();	
							
							if (++cPos >= fPos && bytes.size() < length) 	 
								bytes.add(cByte);	
						}
					}
				}
			}
		}
	
		byte[] byteArr = new byte[bytes.size()]; 
		for(int i = 0; i < bytes.size(); i++) //--> Convert byte array list to primitive byte array.
		    byteArr[i] = bytes.get(i);
		
		fPos += length; //--> Update file pointer position.
		
		Helper.dumpHexBytes(byteArr); //--> Reveal the contents of the file in a hexadecimal box.
		
		return byteArr;
	}	
	
	
	/**
	 * Allows the user to traverse the file system via user input.
	 */
	public void manTraverse() 
	{
		fPath = "./";
		
		while(true) 
		{
			System.out.printf("%n|> Current Path: %s%n|> ", fPath);
			Scanner get = new Scanner(System.in);
			String startPath = fPath; //--> Save starting file position.
			String newElement = get.nextLine();
					
			if (!newElement.substring(newElement.length() - 1).equals("/")) //--> Include slash in path if not provided by user.
				newElement = newElement + "/";
					
			if (newElement.equals("../")) //--> Remove last path element if entry is '..'.
			{
				String[] pathElement = fPath.split("/"); 
						
				fPath = "";
						
				for (int i = 0; i < pathElement.length - 1; i++) 
					fPath = fPath + pathElement[i] + "/";	
						
				if (fPath.equals("")) 
					fPath = fPath + "./";		
			}
			else
				fPath = fPath + newElement;	//--> Update file path with new element.
				
			Inode tNode = this.getFileNode(); //--> Get the node for the new directory.
			
			int fType = 0;
			
			if (tNode != null) 
				fType = tNode.getMode();
				
			fType = (fType & 0x8000);
				
			if (fType == 0x8000) //--> Check if regular file -> read.
			{
				System.out.format("%s%n", new String(this.read(this.getSize()))); 	
				fPath = startPath; //--> Revert back to previous path.
			}
				
			if (tNode == null || newElement.equals("./")) //--> If file path is incorrect -> null returned. 	
				fPath = startPath; //--> If user wants to check current directory -> do not change path.	
			
			fPos = 0; //--> Reset file position so the same file can be read multiple times.
		}
	}
}






