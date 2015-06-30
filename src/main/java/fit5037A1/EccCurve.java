/**
 * 
 */
package fit5037A1;

import java.math.BigInteger;



/**
 * The class describes the ECC curve, including parameters 'a', 'b', 'p' and 'q', etc., and operations on the curve.
 * @author Yang Bo 24644242 
 * @version 1.0 
 */
public class EccCurve {

	/**
	 * Parameter a of the curve
	 * */
	BigInteger a;
	/**
	 * Parameter b of the curve
	 * */
	BigInteger b; 
	/**
	 * Parameter p of the curve
	 * */
	BigInteger p;
	/**
	 * Parameter q of the curve
	 * */
	BigInteger order; 
	  
	/**
	 * The generator of the curve
	 * */
	EccPoint generator;
	/**
	 * The facility object of the application, which is a singleton object. 
	 * */
	StaticUtility utility = StaticUtility.getUtility(); 
	 
	/**
	 * Constructor of the curve.
	 * */
	public EccCurve() 
	{ 
	}
	
	/**
	 * Start the initialization process from the 'CurveSuit' object, which contains all parameters.
	 * @param suit 'CurveSuit' object, which contains all parameters.
	 * */
	public void initFromSuit( CurveSuit suit )
	{
		BigInteger[] suitary = suit.getSuit() ;
		p = suitary[ suit.indexP ];
		a = suitary[ suit.indexA ];
		b = suitary[ suit.indexB ];
		order = suitary[ suit.indexQ ];
		generator = suit.getGenerator() ; 
		generator.init(this) ;
	} 
	
	/**
	 * Check whether a point is on the curve.
	 * @param point A ECC point.
	 * @return True if the point is on the curve, else false. 
	 * */
	public boolean isOnCurve(EccPoint point)
	{
		if( point==null)
		{
			System.out.println("Point is null");
			return false;
		}
		//long x1 = ( point.x*point.x*point.x+a*point.x+b )% p ;
		BigInteger x1 =  point.x.multiply(  point.x.multiply(point.x) )
		  .add( a.multiply(point.x)) .add( b).mod(p) ;
		 
		
		//long y1 = (point.y*point.y ) % p; 
		BigInteger y1 = point.y.multiply(  point.y).mod(p) ;
		return x1.compareTo(y1)==0 ;
	}
	
	/**
	 * Get parameter 'a', which is a big integer.
	 * */
	public BigInteger getA() {
		return a;
	} 
	/**
	 * Get parameter 'b', which is a big integer.
	 * */
	public BigInteger getB() {
		return b;
	} 
	/**
	 * Get parameter 'p', which is a big integer.
	 * */
	public BigInteger getP() {
		return p;
	} 
	/**
	 * Get parameter 'q', which is a big integer.
	 * */
	public BigInteger getOrder() {
		return order;
	}
	/**
	 * Get the generator of the curve.
	 * */
	public EccPoint getGenerator() {
		return generator;
	}  
}
