
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author Jack Morgan
 * @version 1.0
 * Driver class for Ext2 File System Reader.
 */
public class Main 
{
	/**
	 * Provides the user with a set of file paths that can be traversed and read from.
	 * @param args - test parameter. Values can be input via console.
	 */
	public static void main(String[] args) 
	{
		Volume vol = new Volume("C:\\Users\\Jack Morgan\\Desktop\\EXT2FS\\ext2fs"); //--> "//home//lancs//morganj6//Desktop//VOL//ext2fs" 
		Ext2File file;																//--> Remove comments in Directory to print inodes.

		while(true) 
		{
			System.out.printf("|> (01): Quit %n|> (02): Read Superblock%n|> (03): Manual Search%n|> (04): Read Files%n|> ");
			Scanner get = new Scanner(System.in);
			int uInput = 0;
			
			while (true) 
			{
				try 
				{
					uInput = get.nextInt();
					break;
				}
				catch(InputMismatchException e) 
				{
					System.out.println("|> Please enter an integer. ");
					break;
				}
			}
			
			switch (uInput)
			{
				case 1: return;
				case 2: vol.sb.printMeta(); break;
				case 3: file = new Ext2File(vol, ""); file.manTraverse();
				case 4: //--> Display options 
				{
					System.out.printf("|> (01): [deep/down/in/the/filesystem/there/lived/a/file]%n");
					System.out.printf("|> (02): [big-dir]%n");
					System.out.printf("|> (03): [two-cities]%n");
					System.out.printf("|> (04): [lost+found]%n");
					System.out.printf("|> (05): [files]%n");
					System.out.printf("|> (06): [files/dir-s]%n");
					System.out.printf("|> (07): [files/dir-e]%n");
					System.out.printf("|> (08): [files/ind-s]%n");
					System.out.printf("|> (09): [files/ind-e]%n");
					System.out.printf("|> (10): [files/dbl-ind-s]%n");
					System.out.printf("|> (11): [files/dbl-ind-e]%n");
					System.out.printf("|> (12): [files/trpl-ind-s]%n");
					System.out.printf("|> (13): [files/trpl-ind-e]%n");
					System.out.printf("|> (14): [Return]%n");
					
					while (true) 
					{
						try 
						{
							uInput = get.nextInt();
							break;
						}
						catch(InputMismatchException e) 
						{
							System.out.println("|> Please enter an integer. ");
							break;
						}
					}
		
					switch(uInput) //--> Select a file to output its contents.
					{
						case 1:   file = new Ext2File(vol, "./deep/down/in/the/filesystem/there/lived/a/file"); System.out.format("%s\n", new String(file.read(21))); break;
						case 2:   file = new Ext2File(vol, "./big-dir"         ); file.read(0); break;
						case 3:   file = new Ext2File(vol, "./two-cities"      ); System.out.format("%s\n", new String(file.read(file.getSize()-100, 100))); break;
						case 4:   file = new Ext2File(vol, "./lost+found"      ); file.read(0); break; 
						case 5:   file = new Ext2File(vol, "./files"           ); file.read(0); break;
						case 6:   file = new Ext2File(vol, "./files/dir-s"     ); System.out.format("%s\n", new String(file.read(13))); break; 
						case 7:   file = new Ext2File(vol, "./files/dir-e"     ); System.out.format("%s\n", new String(file.read(11))); break; 
						case 8:   file = new Ext2File(vol, "./files/ind-s"     ); System.out.format("%s\n", new String(file.read(15))); break; 
						case 9:   file = new Ext2File(vol, "./files/ind-e"     ); System.out.format("%s\n", new String(file.read(13))); break; 
						case 10:  file = new Ext2File(vol, "./files/dbl-ind-s" ); System.out.format("%s\n", new String(file.read(21))); break; 
						case 11:  file = new Ext2File(vol, "./files/dbl-ind-e" ); System.out.format("%s\n", new String(file.read(20))); break; 
						case 12:  file = new Ext2File(vol, "./files/trpl-ind-s"); System.out.format("%s\n", new String(file.read(21))); break; 
						case 13:  file = new Ext2File(vol, "./files/trpl-ind-e"); System.out.format("%s\n", new String(file.read(20))); break;  
						case 14:  break;
					}
				}
			}
		} 
	}
}
