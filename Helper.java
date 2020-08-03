
/**
 * @author Jack Morgan
 * @version 1.0
 * Utility class used to print display a set of bytes in a human friendly manner.
 */
public class Helper 
{
    /**
     * Outputs a set of bytes as hexadecimal values in a human friendly block format.
     * @param bytes - array of bytes that will be displayed in block format.
     */
    public static void dumpHexBytes(byte[] bytes) {

        int i = 0;
        int lineNum = 1;
        
        int dumpCapacity = bytes.length + 32 - 1 - (bytes.length - 1) % 32; //--> Get nearest upper multiple of 32 of the byte arrays length.

        System.out.format("%n|>=======================================================================>%n|");

        if (bytes.length > 0) 
        {
        	
            for (i = 0; i < bytes.length; i++) 
            {
                if (i % 8 == 00 && i > 0) 
                    System.out.format("|");
                
                if (i % 16 == 0 && i > 0) 
                    System.out.format(" [%d]%n|", lineNum++);
                
                System.out.format(" %02X ", bytes[i]);
            }
        }

        for (; i < dumpCapacity; i++) 
        {
            if (i % 8 == 0 && i > 0) 
            	System.out.format("|");
                
            if (i % 16 == 0 && i > 0) 
                System.out.format(" [%d]%n|", lineNum++);
                
            System.out.format(" XX ");
        }
        System.out.format("| [%d]%n|>=======================================================================>%n", lineNum);
    }
}
