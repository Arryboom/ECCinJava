package fit5037A1;

import java.util.LinkedList;
import java.util.List;



/**
 * This is a test program. It contains all the procedures supported in the crypto module, including signature, verify signature, encryption and decryption. 
 * @author Yang Bo 24644242 
 * @version 1.0 
 */
public class Test {

	/**
	 * This method shows all procedures in this crypto module.  
	 */
	public static void main(String[] args) { 
		  
		CurveSuit suit = new CurveSuit();
		suit.suitChoice = 2; //This line decide which curve to use
		
		long time1 = System.currentTimeMillis() ;
		Entity Alice = new Entity(); 
		Alice.name = "Alice"; 
		Alice.init( suit ) ;  
		
		Entity Bob = new Entity(); 
		Bob.name = "Bob"; 
		Bob.init( suit) ; 
		
		Alice.exchangeKey(Bob) ;
		Bob.exchangeKey(Alice) ;  
		
		//String msg = "0123456789abcdefghijklmnopqrstuvwxyz)(*&^%$#@!";  
		String msg="abcdefg";
		System.out.println("=================================\r\nThe plain text from Alice is :"+ msg);
		EccPoint signedPoint = Alice.sign(msg);  
		msg = msg+"|"+signedPoint.toString() ;   
		  
		long time3 = System.currentTimeMillis() ;
		List<EccPoint> msgEncodelist = new LinkedList<EccPoint>();
		Alice.embedMsg( msg , msgEncodelist) ;  
		List<EccPoint[]> encryptedPoints= Alice.encryptMsg(msgEncodelist, "Bob") ;
		if( encryptedPoints==null )
		{
			System.out.println("encryption failed");
			return ;
		}
		System.out.println( "The time used for embeding and encryption:"+ (System.currentTimeMillis()-time3 ) );
			   
		System.out.println("\r\n-=-=-=-=-= Decrypted Points and Msg");
		long time2 = System.currentTimeMillis() ; 
		Bob.embedK = Alice.embedK ; 
		String[] decriptNVerifysign = Bob.decryptMsg(encryptedPoints, "Alice") ; 
		System.out.println( "Plain text is : "+decriptNVerifysign[0] ); 
		System.out.println( "Bob verify Alice's signature "+ decriptNVerifysign[1]) ; 
		System.out.println( "The time used for decryption: "+(System.currentTimeMillis()-time2 )); 
		System.out.println( "The total time used for whole process:"+ (System.currentTimeMillis()-time1) );
		System.out.println("=================================\r\n");
		
	}

}







