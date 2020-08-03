
import java.util.Date;

/**
 * @author Jack Morgan
 * @version 1.0
 * Represents a file within the volume.
 */
public class FileInfo 
{
	private Inode fInode;
	private String fName;
	
	private String fPerms;
	private short idUser;
	private short idGroup;
	
	private Date modTime;
	private short hLinks;
	private long fSize;
	private byte fType;
	
	/**
	 * FileInfo constructor.
	 * @param fInode (required) - the files node.
	 * @param fName  (required) - name of the file.
	 * @param fType  (required) - type of the file.
	 */
	public FileInfo(Inode fInode, byte[] fName, byte fType) 
	{
		this.fInode = fInode;
		this.fName = new String(fName);
		
		this.fPerms = fileModeToString(fInode.getMode());
		this.idUser = fInode.getUser();
		this.idGroup = fInode.getGroup();
		
		this.modTime = new Date(fInode.getModTime() * 1000);
		this.hLinks = fInode.getLinks();
		this.fType = fType;
	
		fSize = fInode.getHiSize();
		fSize = (fSize << 32) | fInode.getLoSize();
	}
	
	/**
	 * Converts the 16-bit fileMode value into a Unix style permissions String.
	 * @param fileMode - 16-bit value describing the files permissions.
	 * @return fPerms - file 
	 */
    private static String fileModeToString(short fileMode)
    {
        int[] fileModes = {0x8000, 0x4000, 0x0100, 0x0080, 0x0040, 0x0020, 0x0010, 0x0008, 0x0004, 0x0002, 0x0001}; //--> List of file permissions to check against.

        char[] fPerms = new char[10];   

        for (int i = 0, x = 0; i < fileModes.length; i++) 
        {
            int currMode = fileModes[i] & fileMode;

            if (currMode == fileModes[i])
            {
                switch(i)
                {
                    case 0:  { fPerms[x++] = 'a'; break; }
                    case 1:  { fPerms[x++] = 'd'; break; }
                    case 2:  { fPerms[x++] = 'r'; break; }
                    case 3:  { fPerms[x++] = 'w'; break; }
                    case 4:  { fPerms[x++] = 'x'; break; }
                    case 5:  { fPerms[x++] = 'r'; break; }
                    case 6:  { fPerms[x++] = 'w'; break; }
                    case 7:  { fPerms[x++] = 'x'; break; }
                    case 8:  { fPerms[x++] = 'r'; break; }
                    case 9:  { fPerms[x++] = 'w'; break; }
                    case 10: { fPerms[x++] = 'x'; break; }
                }
            } 
            else if (i > 1)
            	fPerms[x++] = '-';
            
        }
        return new String(fPerms);
    }
    
    /**
     * Returns the file name.
     * @return fName - name of the file.
     */
    public String getName() 
    { 
    	return fName; 
    }
    
    /**
     * Returns the file permissions of the file.
     * @return fPerms - file permissions of the file.
     */
    public String getPerm() 
    { 
    	return fPerms; 
    }
    
    /**
     * returns the user ID that owns this file.
     * @return idUser - user ID of the file.
     */
    public short getUser() 
    { 
    	return idUser; 
    }
    
    /**
     * Returns the group ID that owns this file.
     * @return idGroup - group ID of the file.
     */
    public short getGroup() 
    { 
    	return idGroup; 
    }
    
    /**
     * Returns the last time the file was modified.
     * @return modTime - last time the file was modified.
     */
    public Date getTime() 
    { 
    	return modTime; 
    }
   
    /**
     * Returns the number of hard links the file has.
     * @return hLinks - number of hard links the file has.
     */
    public short getLinks() 
    { 
    	return hLinks; 
    }
    
    /**
     * Returns the size of the file in bytes.
     * @return fSize - total file size in bytes.
     */
    public long getSize() 
    { 
    	return fSize; 
    }
    
    /**
     * Returns the node of the file.
     * @return fInode - this files associated inode.
     */
    public Inode getNode() 
    { 
    	return fInode; 
    }
    
    /**
     * Returns the type of the file.
     * @return fType - the files type.
     */
    public byte getType() 
    {
    	return fType; 
    }
}









