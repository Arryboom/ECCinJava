/**
 * 
 */
package fit5037A1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class represents communicating entity, such as Alice or Bob. This class contains a key pair, ECC curve and a key ring, etc.
 * This class can encrypt and decrypt messages, do a digital signature and verify digital signature. 
 * @author Yang Bo 24644242 
 * @version 1.0 
 */
public class Entity {

	/**
	 * A pari of keys, containing private and pubilc keys. 
	 * */
	KeyPair keyPair;
	/**
	 * The ECC curve the entity is using. Both communicating entities should use the same curve. 
	 * */
	EccCurve curve; 
	/**
	 * The name of this entity, for identity purpose.
	 * */
	String name;
	/**
	 * A key ring, containing public keys of the other communicators.  
	 * */
	Map<String, EccPoint> keyRing = new HashMap<String, EccPoint>();
	/**
	 * The facility object of the application, which is a singleton object. 
	 * */
	StaticUtility utility = StaticUtility.getUtility(); 
	/**
	 * A random value used to embed a message onto ECC curve. Security random generator of Java generates this value.
	 * */
	BigInteger embedK;
	/**
	 * The name of the source of the incoming message. This name is used to map to a public key. 
	 * */  
	String senderName;
	/**
	 * A flag to indicate whether to generate a key pair from a key file. 
	 * True- read a key file to get the key pair
	 * False - calculate a new pair of keys
	 * */
	boolean initKeyFromFile = false;
	/**
	 * The full path of a key file
	 * */
	String keyFilePath = "/Users/Yang/Documents/workplace/FIT5037A1-YangBo24644242/Alice.key";
	     
    /**
     * To start the initialization process. This process initialize the curve information, such as a, b, p,q, etc., and initialize key pairs.
     * @param curve The curve object. It appoints the curve object to the communicating entity.  
     * */
	private void init(EccCurve curve)
	{
		this. curve = curve;  
		if( !initKeyFromFile )
		generateKeyPair() ;  
		else 
			initKeyFromFile(keyFilePath);
	}
	/**
	 * To initialize the entity with a curve suit. This process initialize the entity, including the curve it is using and the key pair.
	 * @param curvesuit The curve suit, which contains parameters of the curve.
	 * */
	public void init( CurveSuit curvesuit )
	{
		this. curve = new EccCurve();
		curve.initFromSuit( curvesuit ) ;
		init( this. curve );
	}
	
	/**
	 * In the initialization process, read private and public key pair from key file. 
	 * @param fileName The name of the key file. 
	 * */
	private void initKeyFromFile(String fileName)
	{
		File file = new File( fileName );
		if( !file.exists() )
		{
			System.out.println("Key file does not exit.");
			return ;
		}
		BufferedReader bf = null;
		
		try
		{
			bf = new BufferedReader(new InputStreamReader( new FileInputStream( file ),"utf8" )); 
			String[] key =  bf.readLine().replace("exchangeKey|", "").split("\\)");
			 
			BigInteger privatekey = utility.getBigNum( key[1].trim().replace(",", "") ) ;
			key = key[0].split(",") ;
			
			int index1 = key[0].indexOf("=") ; 
			EccPoint tmpPoint  = new EccPoint( utility.getBigNum( key[0].substring(index1+2).trim() ) 
					,  utility.getBigNum( key[1].replace("(", "").replace(")", "").trim() ) , curve );
			
			keyPair = new KeyPair(privatekey, tmpPoint);  
			
		}catch(Exception e)
		{
			e.printStackTrace() ;
		}
		
	}
	
	/**
	 * It is involved in the initialization process, generate the private and public key pair. 
	 * */
	private void generateKeyPair()
	{ 
		BigInteger privatekey = null  ; 
		EccPoint tmpPoint = null ; 
		int round = 1; 
		
		while( tmpPoint==null && round< 10)
		{ 
			round++; 
			
			privatekey = utility.randomInteger(utility.getBigNum("1"), curve.p .subtract( utility.getBigNum("-1")) ) ;
		
			tmpPoint = curve.generator.multiply( privatekey, curve.generator) ;  
		} System.out.println( "generator: "+curve.generator );
		keyPair = new KeyPair(privatekey, tmpPoint ); System.out.println( "key pairs in line 141: "+keyPair);
	}  
	
	
	// {kG, P m + k P B ) }. 
	/**
	 * Encrypt plain-text ECC points into cipher points, using receiver's public key. The parameter 'receiverName' is the name of receiver's public key in the key-ring.
	 * @param msg The points from plain text. 
	 * @param receiverName The name of the receiver. There are pairs of name and public key in key-ring. The receiver's name can be used to map to the public key of the receiver.
	 * @return The encrypted points of the message.
	 * */
	public List<EccPoint[]> encryptMsg(List<EccPoint> msg, String receiverName ) 
	{
		System.out.println("start to encrypt msg ");
		Iterator<EccPoint> ite = msg.iterator() ;
		EccPoint tmp = null;
		List<EccPoint[]> res = new LinkedList<EccPoint[]>();
		utility.chr2pointMap.clear() ;
		
		while( ite.hasNext() )
		{
			EccPoint[] cipher = new EccPoint[2];
			cipher[0] = keyPair.pubicKey ;
			
			tmp = ite.next() ;
			
			if( tmp==null)
			{
				return null;
			}
			if( utility.chr2pointMap.get(tmp ) !=null )
			{ 
				cipher[1] = utility.chr2pointMap.get(tmp );
				res.add( cipher) ; 
				continue;
			}
			
			tmp = tmp.add( tmp.multiply( keyPair.privatekey , (EccPoint)keyRing.get( receiverName)) ) ; 
			if( tmp==null )
			{
				return null;
			}
			cipher[1] = tmp;
			res.add(cipher ) ;  
		}
		return res; 
	}
	
	/**
	 * Decrypt the cipher ECC points back to points corresponding to plain text, using the sender's public key. The parameter 'senderName' is the name corresponding to sender's public key.
	 * And it also verify the signature.  
	 * @param msg 
	 * @param senderName The source of the message. There are pairs of name and public key in key-ring. The sender name can be used to map to the public key of the sender.
	 * @return An array containing plain text and the result of signature. 
	 * */
	public String[] decryptMsg(List<EccPoint[]> msg, String senderName ) 
	{
		Iterator<EccPoint[]> ite = msg.iterator() ;
		EccPoint[] tmp = null;
		List<EccPoint> res = new LinkedList<EccPoint>();
		EccPoint tmpPoint = null;
		while( ite.hasNext() )
		{
			tmp= ite.next() ;
			
			tmpPoint = this.keyPair.pubicKey.multiply(this.keyPair.privatekey, (EccPoint)this.keyRing.get(senderName)  ) ;
			tmpPoint = tmpPoint.reverse() ;
			res.add( tmp[1].add(tmpPoint ) ) ;
		}
		String decryptedmsg = outbedMsg( res  ); 
		String[] plaintxt = decryptedmsg.split("\\|") ;
		boolean verifySign = verifySign(  utility.convertStrToPoint(plaintxt[1],  getCurve() ), plaintxt[0], senderName) ; 
		plaintxt[1] = verifySign+"";
		return plaintxt;
	}  
	
	// mk+ j  1<j<k
	/**
	 * Convert a plain-text message into points on ECC curve. 
	 * @param  msg   A plain-text message.
	 * @param msgEncodelist A container to take the points of the message.
	 * @return The number of points mapped onto the curve. 
	 * */
	public int embedMsg(String msg, List<EccPoint> msgEncodelist)
	{ 
		int len = msg.length() ;   
		int l = 0;
		msgEncodelist.clear() ;
		 
		while(true)
		{
			embedK =  utility.randomInteger( utility.getBigNum("1"), curve.p  ) ; 
			
			//test for K
			for( l=0;l<len; l++ )
			{  
				int chr = (int)msg.charAt(l) ;   
				
				if( utility.getBigNum((chr+1)+"").multiply( embedK ).compareTo(curve.p)>=0)
				{// if ( msg+1)*k >= p  then the k is not suitable  
					embedK.add( utility.getBigNum("1") ) ;
					break;
				}
			}
			if( l>=len )
			{
				break;
			}
		}  
		//System.out.println( "embed K="+ embedK );
		 
		BigInteger power = curve.p.add(utility.getBigNum( "1") ) .divide(utility.getBigNum( "4") ) ;  
		
		int count = 0; 
		int j =0; 
		
		for( int i=0;i< len;i++ )
		{
			int chr = (int)msg.charAt(i) ; //System.out.println( "start to map chr="+chr );
			if( utility.chr2pointMap.get( chr )!=null )
			{
				msgEncodelist.add( utility.chr2pointMap.get( chr ) ) ; 
				continue;
			}
			
			//mk
			BigInteger tmp1 = utility.getBigNum(chr+"").multiply( embedK) ; 
			
			for(  j=1; embedK.compareTo( utility.getBigNum(j+"") )>0 ;j++ )
			{
				//x = mk+j
				BigInteger xj =  tmp1 .add(utility.getBigNum(j+""))  ; 
				 
				//wj = x^3 +ax +b mod p
				BigInteger wj = xj.multiply( xj.multiply(xj)) .mod( curve.p) .mod(curve.p)
						.add(  curve.a.multiply( xj )  .add(curve.b ) ) .mod( curve.p ) 
						  ;
				//zj= wj^ power mod p
				//the bottle neck of the system
				BigInteger zj = wj.modPow(power, curve.p); // utility.modPower(wj, power, curve.p ) ;   
				
				if(  zj.multiply(zj).mod(curve.p ).compareTo(wj)==0 )
				{//( xj, zj) is the point
					EccPoint tmpPoint = new EccPoint(xj, zj, curve ) ;  
					msgEncodelist.add( tmpPoint ) ; 
					 
					//System.out.println("have found a point for "+chr );
					//System.out.println( tmpPoint ); 
					
					utility.chr2pointMap.put(chr, tmpPoint) ;
					count++; 
					break; 
				}   
			}
			
			if( embedK.compareTo( utility.getBigNum(j+"") ) ==0 )
			{
				System.out.println("-=-=-= can not encrypt the chr "+ chr );
			}
		}
		return count;
	} 
	 
	/**
	 * Get plain text from ECC points. Integer value of the message is from [x/k]. 
	 * @param decryptedPoints The decrypted ECC points.
	 * @return The plain text and its signature
	 * */
	private String outbedMsg( List<EccPoint> decryptedPoints )
	{
		Iterator<EccPoint> ite2 = decryptedPoints.iterator() ; 
		StringBuilder buffer = new StringBuilder();
		while( ite2.hasNext() )
		{
			EccPoint tmp2 = ite2.next() ; 
			BigInteger onechar = tmp2.x.divide( embedK ) ;
			buffer.append( (char)onechar.intValue() ) ; 
		}  
		return buffer.toString() ;
	}
	
	/**
	 * Calculate the digital signature of a message.
	 * @param msg A plain text to whom do the digital signature. 
	 * @return The digital signature of the message.
	 * */
	public EccPoint sign(String msg)
	{ 
		if( this.keyPair==null )
		{
			System.out.println("No Key Pair avaliable");
			return null;
		}
		
		//random number between [1, q-1]
		//random*Generator=>V, let r=xv mod p
		BigInteger randomV = null;
		BigInteger r = utility.getBigNum("0");
		BigInteger s = utility.getBigNum("0");
		String hashcode = hashCode( msg ) ; 
		System.out.println( "hashcode is :"+hashcode+" in line 342 "  );
		
		while( r.compareTo( utility.getBigNum("0") ) ==0 
				|| s.compareTo( utility.getBigNum("0") ) ==0 )
		{  
				randomV = utility.randomInteger(utility.getBigNum("1"), utility.getBigNum(""+curve.order).subtract( utility.getBigNum("1") )
						 ) ; 
				System.out.println("random number is :"+ randomV +" in line 349");
			 
			EccPoint nodeV = curve.generator.multiply( randomV, curve.generator) ; 
			System.out.println( "random number* generator"+"="+ randomV+"*"+ curve.generator+"=" +nodeV ); 
			//System.out.println( "\r\n"+name+" sign, random*Generator, "+randomV+"G="+nodeV );
			r = nodeV.x.mod( curve.order ) ; 
			System.out.println( "r=nodeV.x mod curve.order="+nodeV.x+" mod "+curve.order
					+"="+ r );
			
			try
			{
				s = utility.fractionalMod( r.multiply(keyPair.privatekey ).add( utility.getBigNum(hashcode+"",16 ) ) ,
						randomV, curve.order ) ; 
				System.out.println( "(r*private key+hashcod)/randomV  mod curve.order="
						+"("+r+"*"+keyPair.privatekey+"+"+hashcode+")/"+randomV+" mod "+curve.order  );
			}
			catch(Exception e)
			{
				e.printStackTrace();
			} 
			
		}
		EccPoint sign = new EccPoint( r,s,curve ); //System.out.println( "signature="+sign );
		return sign; 
	}
	
	/**
	 * Calculate the hash code of a message. The hash code algorithm is SHA-1.
	 * @param msg The message whose hash code is calculated on. 
	 * @return The SHA-1 hash code.
	 * */
	private String hashCode(String msg)
	{
		try
		{
			MessageDigest cript = MessageDigest.getInstance("SHA-1"); 
			return byteToHex(cript.digest( msg.getBytes() )  );  
		}catch(Exception e)
		{
			e.printStackTrace() ;
		}
			 
		return null;
	} 
	/**
	 * Convert an array of bytes to a hex number, and the form a string of these hex numbers. 
	 * @param hash The byte array.
	 * @return A string of hex numbers. 
	 * */
	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	/**
	 * Verify the signature of a message from someone, using his/her public key. 
	 * @param signedPoint Digital signature, attached with the plain text. 
	 * @param msg A plain-text message. 
	 * @param opponentName The name of the sender of the message. It is used to locate the public key of the sender in the key ring. 
	 * */
	public boolean verifySign(EccPoint signedPoint, String msg,String opponentName)
	{  
		if( signedPoint==null )
		{ 
			System.out.println( "signedPoint==null true" );
			return false;
		}
		if( signedPoint.x.compareTo( utility.getBigNum(curve.order+"" ) )>=0 
			|| signedPoint.x.compareTo( utility.getBigNum("1" ) )<0  ) 
		{
			System.out.println("x not in [1,order-1]");
			 return false;
		}
		if( signedPoint.y.compareTo( utility.getBigNum(curve.order+"" ) )>=0  
				|| signedPoint.y.compareTo( utility.getBigNum("1" ) )<0  ) 
		{
			System.out.println("y not in [1,order-1]");
			return false;
		}
		// 1/s  mod order  
		BigInteger w =  signedPoint.y.modInverse(curve.order) ; 
		 
		//System.out.println("w= 1/s mod order = 1/"+ signedPoint.y +" mod "+ curve.order +" = " +w);
		
		String hashcode = hashCode(msg ) ;
	    BigInteger u1 = utility.getBigNum( hashcode, 16 ).multiply(w ).mod(curve.order) ;
		//System.out.println( "u1 = hash code * w mode order = "+ hashcode +"*"+w+" mod "+curve.order+" = "+u1 );
		
		BigInteger u2 = signedPoint.x.multiply(w ).mod(curve.order  ) ;
		//System.out.println( "u2 = r*w mod order = "+ signedPoint.x+"*"+w+" mod "+ curve.order +"=" +u2 ); 
		
		EccPoint opponetPublickey = (EccPoint)keyRing.get(opponentName) ;
		
		EccPoint tmp1 = curve.generator ;  
		EccPoint tmpPoint = tmp1.multiply(u1, tmp1).add(opponetPublickey.multiply(u2,opponetPublickey  ) ) ;
		//System.out.println( "generator= "+curve.generator);
		//System.out.println( "u1*G+u2*Q="+u1+"*"+curve.generator+"+"+u2+"*"+opponetPublickey+"="+tmpPoint );
		 
		if( tmpPoint==null )
		{ 
			System.out.println( "tmpPoint==null true" );
			return false;
		} 
		return tmpPoint.x.mod( curve.order  ).compareTo( signedPoint.x.mod( curve.order ) )==0; 
		 
	}
	 
	
	/**
	 * Get the key ring inside the communicating party. 
	 * @return The key ring object. 
	 * */
	public Map<String, EccPoint> getKeyRing() {
		return keyRing;
	}
	
	/**
	 * Exchange public key with another communicating party. 
	 * @param opposite The object of another communicating party.
	 * */
	public void exchangeKey(Entity opposite)
	{ 
		keyRing.put( opposite.name,  opposite.keyPair.pubicKey ) ;
	} 
	/**
	 * Exchange public key with another communicating party. 
	 * @param name The name of a communicating party
	 * @param publicKey The public key of the communicating party. 
	 * */
	public void exchangeKey(String name, EccPoint publicKey)
	{
		keyRing.put( name, publicKey );
	}
	/**
	 * Set the name of the communicating party. It is the name of the communicating party himself/herself.
	 * @param name The name of the communicating party
	 * */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Get the name of the message sender. 
	 * @return The name of the message sender.
	 * */
	public String getSenderName() {
		return senderName;
	}
	/**
	 * Set the name of the message sender.
	 * @param The name of the message sender.  
	 * */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	/**
	 * Get the key pair, namely private and public keys.
	 * @return The key pair.
	 * */
	public KeyPair getKeyPair() {
		return keyPair;
	} 
	/**
	 * Set the flag indicating whether to generate keys from a key file. 
	 * @param initKeyFromFile The flag, true or false. True means to read keys from a key file. 
	 * */
	public void setInitKeyFromFile(boolean initKeyFromFile) {
		this.initKeyFromFile = initKeyFromFile;
	}
	/**
	 * Get the random number which is used to embed a message onto the curve.
	 * @return The random big integer number.
	 * */
	public BigInteger getEmbedK() {
		return embedK;
	}
	/**
	 * Set the random big integer number to embed a message onto the curve. 
	 * @param  embedK The random big integer number.
	 * */
	public void setEmbedK(BigInteger embedK) {
		this.embedK = embedK;
	}
	/**
	 * Get the curve, on which the communicating party is working. 
	 * @return The curve.
	 * */
	public EccCurve getCurve() {
		return curve;
	}
	
	public static void main(String[] args)
	{
		Entity entity = new Entity();
		
		StaticUtility utility= StaticUtility.getUtility(); 
		
		CurveSuit suit = new CurveSuit();
		suit.suitChoice = 4;
		 
		entity.init( suit ) ;
		System.out.println( entity.sign("a") ) ;
		
	}
	public String getName() {
		return name;
	}
	
	
	  
}







