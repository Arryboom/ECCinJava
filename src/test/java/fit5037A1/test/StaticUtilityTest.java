package fit5037A1.test;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import fit5037A1.StaticUtility;

public class StaticUtilityTest {

	StaticUtility instance = StaticUtility.getUtility() ;
	@Test
	public void testfindSmallestPowerOf2() {
		System.out.println("======== Running in StaticUtilityTest ========");
		BigInteger[] a = instance.findSmallestPowerOf2( instance.getBigNum("8") ) ;
		assertEquals( instance.getBigNum("3"), a[0] );
		assertEquals( instance.getBigNum("8"), a[1] );
		
		a = instance.findSmallestPowerOf2( instance.getBigNum("10") ) ;
		assertEquals( instance.getBigNum("3"), a[0] );
		assertEquals( instance.getBigNum("8"), a[1] );
		
		a = instance.findSmallestPowerOf2( instance.getBigNum("1000") ) ;
		assertEquals( instance.getBigNum("9"), a[0] );
		assertEquals( instance.getBigNum("512"), a[1] );
	}
	
	

}
