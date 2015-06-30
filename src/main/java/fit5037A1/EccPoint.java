/**
 * 
 */
package fit5037A1;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * The class defines a point on ECC curve. It contains the coordinate values of x and y, and the curve it belongs to.
 * The operations, such as addition, multiplication and subtraction, are defined here.  
 * @author Yang Bo 24644242 
 * @version 1.0 
 */
public class EccPoint {

	/**
	 * Coordinate value of x.
	 * */
	BigInteger x;
	/**
	 * Coordinate value of y.
	 * */
	BigInteger y;    
	/**
	 * The facility object of the application, which is a singleton object. 
	 * */
	StaticUtility utility= StaticUtility.getUtility(); 
	/**
	 * The curve object, to which the point belongs.
	 * */
	EccCurve curve;
	boolean fortest = false; 
	  
	/**
	 * The constructor of ECC point.
	 * @param x coordinator value of x
	 * @param y coordinator value of y
	 * @param curve The curve object this point belongs to.
	 * */
	public EccPoint(BigInteger x, BigInteger y,EccCurve curve   )
	{
		this.x = x;
		this.y = y;
		if( curve!=null  )
		{
			this.curve = curve; 
		}  
	} 
	/**
	 * Start the process of initialization. It is commonly used in dynamically initialization. 
	 * @param curve The curve object this point belongs to.
	 * */
	public void init( EccCurve curve )
	{
		this.curve = curve; 
	}
	
	/**
	 * Multiplication of a point and an big integer. The formula is 'k*point'.
	 * @param k A big integer.
	 * @param point A ECC point.
	 * @return k*point 
	 * */
	public EccPoint multiply( BigInteger k, EccPoint point )
	{
		String binaryK = k.toString(2) ;   
		
		int len = binaryK.length() ;
		EccPoint tmp = point ;
		utility.tmpSaving2.clear() ;  
		utility.tmpSaving2.put(0, tmp) ;   
		List< EccPoint > reslist = new ArrayList< EccPoint >();
		int power = 0 ;
		
		for( int i=0;i<len;i++  )
		{
			if( binaryK.charAt(i)=='0' )
			{
				continue;
			}
			power = len-1-i;  
			
			if( utility.tmpSaving2.get(power )==null )
			{    
				tmp = point ;
				for( int j=1;j<=power;j++)
				{
					tmp = tmp.add( tmp) ; 
					if( tmp==null)
					{
						break;
					}
					utility.tmpSaving2.put(j, tmp) ;  //System.out.println( j+"power G="+tmp );
				}   
			}
			reslist.add(utility.tmpSaving2.get(power ) ) ;  
		}  
		tmp = reslist.get(0) ;
		for( int i=1;i<reslist.size() ;i++ )
		{
			tmp = tmp.add(reslist.get(i) ) ;
		}
		return tmp;
	} 
	 
	/**
	 * Addition operation of two ECC points. The formula is 'this + anotherpoint'. 
	 * @param anotherPoint The point added to this point.
	 * @return this+anotherPoint 
	 * */
	public EccPoint add(EccPoint anotherPoint)
	{
		if( equals(anotherPoint) )
		{
			return doubling( );
		}  
		if( this.x.compareTo(  anotherPoint.x.negate() ) ==0 ) 
		{// P=/=-Q  
			return null;
		} 
		utility.errorCode = 0;
		BigInteger s = utility.fractionalMod( this.y.subtract( anotherPoint.y), this.x.subtract( anotherPoint.x), curve.p);
		if( fortest )
		{ 
			System.out.println( "\r\n============ Start Point Addition \r\n"+this+"+"+anotherPoint+"\r\ns=(yP -yQ)/(xP -xQ) mod p" +
					"= ("+this.y+"-"+anotherPoint.y+")/("+this.x+"-"+anotherPoint.x+") mod "+curve.p +"="+s );
		}
		
		while( s.compareTo( utility.getBigNum("0") ) <0 )
		{
			s = s.add(curve.p ) ; 
		} 
		  
		BigInteger xR = s.multiply(s)  .subtract(this.x) .subtract( anotherPoint.x );
		if( xR.compareTo( utility.getBigNum("0") )   <0 )
		{   
			
			xR = xR.negate().divide(curve.p ).add( utility.getBigNum("1") ).multiply( curve.p ) .add( xR)  ;
 		} 
		xR = xR.mod(curve.p ) ; 
		if( fortest )
		{
			System.out.println( "xR=s^2 -xP -xQ mod p="+s+"^2 - "+this.x+"-"+anotherPoint.x+" mod "+curve.p +"="+xR ) ;
		}
		
		BigInteger yR = s.multiply( this.x.subtract(xR) ) .subtract(this.y) ;

		if( yR.compareTo( utility.getBigNum("0") )   <0 )
		{    
			yR = yR.negate().divide(curve.p).add( utility.getBigNum("1") ).multiply( curve.p ) .add( yR)  ;
		} 
		yR = yR.mod(curve.p);
		if( fortest )
		{
			System.out.println( "yR=s(xP -xR)-yP mod p ="+s+"("+this.x+"-"+xR+")-"+this.y + " mod "+curve.p
					+"= "+ yR+"\r\n=========== End of Addtion" ) ;
		}
		
		return new EccPoint( xR,  yR, curve );
	}
	
	/**
	 * Subtraction of two points. The formula is 'this - anotherPoint'.
	 * @param anotherPoint The point subtracted from this point.
	 * @return this-anotherPoint
	 * */
	public EccPoint substract(EccPoint anotherPoint)
	{
		anotherPoint = anotherPoint.reverse() ;
		if( fortest )
		{
			System.out.println( "\r\n=========== Subtract \r\n"+this+"+"+anotherPoint );
		}
		return add( anotherPoint ) ;
	}
	/**
	 * Get a reverse point of this point, namely (x, -y). 
	 * @return A new point (x, -y).
	 * */
	public EccPoint reverse()
	{
		return new EccPoint( this.x, this.y.multiply(new BigInteger("-1")), this.curve ); 
	}
	/**
	 * Doubling of this point. The formula is 'this+this'.
	 * @return this+this
	 * */
	private EccPoint doubling()
	{   
		if( fortest )
		{
			System.out.println("\r\n============In doubling\r\n 2"+this);
		}
		
		utility.errorCode = 0;  
		BigInteger tmp = this.x.multiply( this.x) ;  
		tmp = tmp.multiply( utility.getBigNum("3" ));   
		 
		BigInteger s = utility.fractionalMod( tmp.add( curve.a )   
				  , this.y.multiply( utility.getBigNum("2" ))  , curve.p ); 
		
		if( fortest )
		System.out.println( "s= ( 3*x^2+a )/ 2*y mod p = ( 3 * "+ this.x +"^ 2 + "+ curve.a +") / (2*"+this.y
			 +") mod "+ curve.p +"="+ s );
		  
		BigInteger xR = s.multiply(s) .subtract( this.x.shiftLeft(1) ) ; 
		
		if( xR.compareTo( utility.getBigNum("0") )   <0 )
		{   
			xR = xR.negate().divide(curve.p ).add( utility.getBigNum("1") ).multiply( curve.p ) .add( xR)  ;
		} 
		xR = xR.mod( utility.getBigNum(curve.p+"") ); 
		
		if( fortest )
		System.out.println( "xr= (s^2 - 2*xP) mod p = "+ s+"^2 - 2*"+curve.p + " mod "+curve.p +"="+xR );
		
		BigInteger yR = s.multiply( this.x.subtract(xR)  ).subtract( this.y ) ; 
		 
		if( yR.compareTo( utility.getBigNum("0") )   <0 )
		{    
			yR = yR.negate().divide(curve.p ).add( utility.getBigNum("1") ).multiply(curve.p ) .add( yR)  ;
		} 
		yR = yR.mod( utility.getBigNum(curve.p+"") ); 
		
		if( fortest )
		System.out.println( "yR= s(xP -xR)-yP mod p = "+s+"("+this.x+"-"+xR+") - "+this.y+" mod "+curve.p
				+"="+yR+"\r\n========= End of Doubling " );
		
			
		return new EccPoint( xR,  yR, curve);
	} 
	/**
	 * Return the information of this point.
	 * @return (x,y)
	 * */
	public String toString()
	{
		return "("+x+","+y+")";
	} 
	
	public BigInteger getX() {
		return x;
	} 
	public BigInteger getY() {
		return y;
	} 
	
	public static void main(String[] args)
	{ 
		StaticUtility utility= StaticUtility.getUtility(); 
		
		CurveSuit suit = new CurveSuit();
		suit.suitChoice = 4;
		
		EccCurve curve = new EccCurve();
		curve.initFromSuit(suit) ;
		
		EccPoint a = new EccPoint( utility.getBigNum("17")  ,utility.getBigNum("20"), curve);
		a.fortest = true;
		System.out.println( a.doubling() ) ;
		
		EccPoint b = new EccPoint( utility.getBigNum("18")  ,utility.getBigNum("3"), curve);
		b.fortest = true;
		System.out.println( a.add( b ) ) ;
		
		System.out.println( a.substract( b ) ) ;
	}
}
