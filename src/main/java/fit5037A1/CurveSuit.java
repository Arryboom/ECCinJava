/**
 * 
 */
package fit5037A1;

import java.math.BigInteger;

/**
 * This class contains several ECC curves suites, each of whom contains parameter suites. For test purpose and running usage, it is convenient to switch from one curve to another, or to allocate a suite dynamically.    
 * 
 * @author Yang Bo 24644242 
 * @version 1.0 
 */
public class CurveSuit {
	
	/**
	 * The position of parameter 'q' in the array.
	 * */
	static int indexP = 0;
	/**
	 * The position of parameter 'a' in the array.
	 * */
	static int indexA = 1;
	/**
	 * The position of parameter 'b' in the array.
	 * */
	static int indexB = 2;
	/**
	 * The position of parameter 'q' in the array.
	 * */
	static int indexQ = 3;
	/**
	 * The choice of one of the suits. For example, when suitChoice=2, the curve suite would be 2.
	 * */
	static int suitChoice = 1;
	
	/**
	 * The facility object of the application, which is a singleton object. 
	 * */
	StaticUtility utility = StaticUtility.getUtility(); 
	//---------------
	
	/**
	 * ECC curve suit 1, where p=11.
	 * */
	BigInteger[] suit1 = new BigInteger[]{ utility.getBigNum("11")
			, utility.getBigNum("1"), utility.getBigNum("6")
			, utility.getBigNum("13")  };
	/**
	 * The generator for ECC curve suit 1.
	 * */
	EccPoint generator1 = new EccPoint(utility.getBigNum("2"),
			utility.getBigNum("7"), null );
	
	//---------------
	/**
	 * ECC curve suit 2, where p=E95E4A5F737059DC60DFC7AD95B3D8139515620F.
	 * */
	BigInteger[] suit2 = new BigInteger[]{utility.getBigNum("E95E4A5F737059DC60DFC7AD95B3D8139515620F",16)
			, utility.getBigNum ("340E7BE2A280EB74E2BE61BADA745D97E8F7C300",16)
			, utility.getBigNum ("1E589A8595423412134FAA2DBDEC95C8D8675E58",16)
			, utility.getBigNum ("E95E4A5F737059DC60DF5991D45029409E60FC09",16)  } ;
	/**
	 * The generator for ECC curve suit 2.
	 * */
	EccPoint generator2 = new EccPoint(utility.getBigNum ("BED5AF16EA3F6A4F62938C4631EB5AF7BDBCDBC3",16),
			utility.getBigNum ("1667CB477A1A8EC338F94741669C976316DA6321",16), null );
	
	
	
	/**
	 * ECC curve suit 3, where p=29.
	 * */
		BigInteger[] suit3 = new BigInteger[]{utility.getBigNum("29")
				, utility.getBigNum("-1"), utility.getBigNum("16")
				, utility.getBigNum("31")  }; 
		EccPoint generator3 = new EccPoint(utility.getBigNum("5"),
				utility.getBigNum("7"), null );
	 //---------------
	
		//---------------
		// p=0 a=1 b=2 q=3 
		BigInteger[] suit4 = new BigInteger[]{ utility.getBigNum("23")
				, utility.getBigNum("1"), utility.getBigNum("1")
				, utility.getBigNum("13")  }; 
		EccPoint generator4 = new EccPoint(utility.getBigNum("2"),
				utility.getBigNum("7"), null );
		
		
		/**
		 * ECC curve X, which is for dynamic allocation of ECC curve.
		 * */
		 BigInteger[] suitX =null;
		 /**
			 * The generator for ECC curve suit X.
			 * */
		 EccPoint generatorX = null;
		  
		 /**
		  * Initialize a curve from a string, which would be from a file or other sources. 
		  * The format of the string is 'initParameters|p=2322,a=232,b=2323,q=2323,x=23223,y=322332'
		  * @param initinfo The input string of curve parameters.
		  * */
	public void initFromString(String initinfo)
	{
		initinfo = initinfo.replace("initParameters|", "") ;
		String[] parameters = initinfo.split(",") ;
		suitX = new BigInteger[4];
		suitX[indexP] = utility.getBigNum( parameters[0].replace("p=", "") ) ;
		suitX[this.indexA] = utility.getBigNum( parameters[1].replace("a=", "") ) ;
		suitX[this.indexB] = utility.getBigNum( parameters[2].replace("b=", "") ) ;
		suitX[this.indexQ] = utility.getBigNum( parameters[3].replace("q=", "") ) ;
		
		generatorX = new EccPoint( utility.getBigNum( parameters[4].substring(2)) , 
				utility.getBigNum(parameters[5].substring(2)) , null );
		System.out.println( generatorX );
		suitChoice = 100;
	}
	
	/**
	 * 
	 * According to the property 'suitChoice', return the ECC curve suit containing parameters 'a', 'b' and 'p', etc.
	 * @return  ECC curve suite
	 * */
	public BigInteger[] getSuit()
	{
		switch( suitChoice )
		{
		case 1: return suit1;
		case 2: return suit2;
		case 3: return suit3; 
		case 4: return suit4; 
		case 100: return suitX;
		} 
		return null;
	}
	
	/**
	 * According to the property 'suitChoice', return the corresponding generator. 
	 * @return The generator of the curve. 
	 * */
	public EccPoint getGenerator()
	{
		switch( suitChoice )
		{
		case 1: return generator1;
		case 2: return generator2;
		case 3: return generator3; 
		case 4: return generator4; 
		case 100: return generatorX;
		} 
		return null;
	} 

	/**
	 * Set the ECC curve suit number.
	 * @param suitChoice The number of the suit. 
	 * */
	public static void setSuitChoice(int suitChoice) {
		CurveSuit.suitChoice = suitChoice;
	}
	
}
