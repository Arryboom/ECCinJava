/**
 * 
 */
package fit5037A1;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used in singleton module. There is only one object throughout the application. 
 * This class contains some basic and common methods and static cache, such as big number cache, ECC point cache, etc. 
 * 
 * @author Yang Bo 24644242 
 * @version 1.0 
 */
public class StaticUtility {
  
	/**
	 * The error code of various conditions. 
	 * */
	int errorCode= 0 ; 
	/**
	 * A cache holding big numbers. It avoid creating a lot of big number objects with the same value. 
	 * */
	public static final Map<String,BigInteger> bigNumMap= new HashMap<String,BigInteger>();
	/**
	 * A singleton instance of this class. This instance is shared throughout the application. 
	 * */
	public final static StaticUtility instance = new StaticUtility();
	/**
	 * A cache holding intermediate results in the process of multiplication.
	 * */ 
	public final static Map<Integer, EccPoint> tmpSaving2 = new HashMap<Integer, EccPoint >(); 
	/**
	 * A cache holding non-duplicated results in the process of encryption. 
	 * */
	public final static Map<Integer, EccPoint> chr2pointMap = new HashMap<Integer, EccPoint>();  
	
	/**
	 * Private constructor. It avoids creating new instance of this class.   
	 * */
	private StaticUtility()
	{
	}
	/**
	 * Get the singleton instance of the class.
	 * @return The singleton instance. 
	 * */
	public static StaticUtility getUtility()
	{
		return instance ;
	}
	
	/**
	 * Given a number, try to find the power of 2 and make the 2^number< the Given Number and 2^(number+1)>the Given number
	 * @param number The target number.
	 * @return An array holding the power of 2 and the number 2^power.
	 * @deprecated It is originally designed to accelerate the multiplication operation, and the multiplication operation upgraded and abandoned this method.  
	 * */
	public BigInteger[] findSmallestPowerOf2(BigInteger number)
	{
		String binstr = number.toString(2) ;
		BigInteger[] values = new BigInteger[2]; 
		values[0] = getBigNum ( (binstr.length()-1) +"" ) ; 
		values[1] = getBigNum("2").pow( values[0].intValue() ) ;
		 
		return values;
	} 
	/**
	 * This method applies modulo calculation to a fractional number. 
	 * @param numerator The numerator
	 * @param denominator The denominator
	 * @param mod The mod number
	 * @return A integer number from the modulo calculation on the fractional number. 
	 * */
	public BigInteger fractionalMod(BigInteger numerator, BigInteger denominator, BigInteger mod )
	{    
		return denominator.modInverse(mod).multiply(numerator ).mod(mod) ; 
	} 
	 
	/**
	 * Generate random numbers between [start, end]. For the security purpose and make random number unpredictable, 'SecureRandom' is used, and the random generating algorithm is 'SHA1PRNG'.
	 * @param aStart The lower bound of the random number
	 * @param aEnd The upper bound of the random number
	 * @return A random number
	 * */
	public BigInteger randomInteger(BigInteger aStart, BigInteger aEnd ){
		   
	    if ( aStart.compareTo( aEnd) >0 ) {
	    	System.out.println("start="+aStart+"  end="+aEnd);
	    	throw new IllegalArgumentException("Start cannot exceed End.");
	    } 
	    SecureRandom srdn = null;
	    try
	    {
	    	srdn = SecureRandom.getInstance( "SHA1PRNG" )  ;
	    }catch(Exception e)
	    {
	    	e.printStackTrace() ;
	    }
	    BigInteger randomBigNum = new BigInteger( aEnd.bitLength(), srdn );
	    while( randomBigNum.compareTo( aStart)<0 || randomBigNum.compareTo(aEnd )>0 )
	    { 
	    	randomBigNum = new BigInteger( aEnd.bitLength(), srdn ); 
	    } 
	    
	    return  randomBigNum ;   
  }  
	
	/**
	 * Get a big integer number from cache. 
	 * @return A big integer number
	 * */
	public BigInteger getBigNum(String num )
	{
		BigInteger tmp = bigNumMap.get(num ) ;
		if( tmp !=null )
		{
			return tmp ;
		}
		tmp = new BigInteger( num , 10 );  
		bigNumMap.put(num, tmp) ; 
		return tmp;  
	} 
	/**
	 * Get a big integer number from cache, with the type of the number, such as binary, hex or octonary number. 
	 * @return A big integer number. 
	 * */
	public BigInteger getBigNum(String num, int numtype)
	{
		BigInteger tmp = bigNumMap.get(num+"_"+numtype ) ;
		if( tmp !=null )
		{
			return tmp ;
		}
		tmp = new BigInteger( num , numtype );  
		bigNumMap.put(num+"_"+numtype, tmp) ; 
		return tmp;  
	} 
	
	/**
	 * Create a ECC point from a string. The string format is '(x,y)'. 
	 * @param str A string 
	 * @param curve The curve the point belongs to. 
	 * @return An ECC point. 
	 * */
	public EccPoint convertStrToPoint(String str, EccCurve curve )
	{
		String[] coordinaters = str.split(",") ;
		  
		return new EccPoint( getBigNum( coordinaters[0].replace("(", "").replace(")", "") ) 
				 , getBigNum( coordinaters[1].replace("(", "").replace(")", "")  ), curve );  
	}  
}
