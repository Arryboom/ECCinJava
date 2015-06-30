/**
 * 
 */
package fit5037A1;

import java.math.BigInteger;

/**
 * The pair of private and public keys
 * @author Yang Bo 24644242 
 * @version 1.0  
 */
public class KeyPair {

	/**
	 * A private key. It is a big integer.
	 * */
	BigInteger privatekey;
	/**
	 * A public key. It is an ECC point. 
	 * */
	EccPoint pubicKey;
	/**
	 * Constructor.
	 * @param privatekey The private key
	 * @param pubicKey The public key
	 * */
	public KeyPair(BigInteger privatekey,  EccPoint pubicKey )
	{
		if( pubicKey==null )
		{
			System.out.println("Failed to create key pair, the public key is null");
			return ;
		}
		this.privatekey = privatekey; 
		this.pubicKey = pubicKey; 
	}
	/**
	 * Get the information of the key pair.
	 * @return The information of the key pair, in the form 'Private key=... Public key=...'.
	 * */
	public String toString()
	{
		return "Private key="+privatekey+" Public key="+pubicKey ;
	}
	/**
	 * Get the public key.
	 * @return The public key.
	 * */
	public EccPoint getPubicKey() {
		return pubicKey;
	}
	/**
	 * Get the private key.
	 * @return The private key.
	 * */
	public BigInteger getPrivatekey() {
		return privatekey;
	} 
	
	
}
