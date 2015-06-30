package fit5037A1.test;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import fit5037A1.CurveSuit;
import fit5037A1.EccPoint;
import fit5037A1.Entity;

public class ECCTest {

	@Test
	public void test() {
		CurveSuit suit = new CurveSuit(); 
		suit.setSuitChoice(2); //decide which curve to use
		
		long time1 = System.currentTimeMillis() ;
		Entity Alice = new Entity();  
		Alice.setName("Alice");
		Alice.init( suit ) ;  
		
		Entity Bob = new Entity();  
		Bob.setName("Bob");
		Bob.init( suit) ; 
		
		Alice.exchangeKey(Bob) ;
		Bob.exchangeKey(Alice) ;  
		
		//String msg = "0123456789abcdefghijklmnopqrstuvwxyz)(*&^%$#@!";  
		String msg="abcdefg^&*^##";
		String plainTxt = msg;
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
		Bob.setEmbedK(Alice.getEmbedK());
		String[] decriptNVerifysign = Bob.decryptMsg(encryptedPoints, "Alice") ;  
		   
		assertEquals( decriptNVerifysign[0], plainTxt );
		assertEquals( decriptNVerifysign[1], "true" );
	}

}
