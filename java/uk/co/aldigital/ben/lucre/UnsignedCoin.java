package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class UnsignedCoin {
    private BigInteger m_biCoinID;

    public void random(int nCoinLength) {
	m_biCoinID=new BigInteger(nCoinLength*8,Util.randomGenerator());
    }
    public void set(BigInteger biCoinID) {
	m_biCoinID=biCoinID;
    }

    public BigInteger id() {
	return m_biCoinID;
    }

    BigInteger generateCoinNumber(PublicBank bank)
      throws NoSuchAlgorithmException {
	int nCoinLength=(m_biCoinID.bitLength()+7)/8;
	int nDigestIterations=(bank.getPrimeLength()-nCoinLength)
	  /PublicBank.DIGEST_LENGTH;

	if(nCoinLength != bank.getCoinLength())
	    return null;

	byte xplusd[]=new byte[bank.getPrimeLength()];

	Util.byteCopy(xplusd,0,m_biCoinID.toByteArray(),0,nCoinLength);

	//	Util.addCrypto();
	MessageDigest sha1=MessageDigest.getInstance("SHA-1");
	for(int n=0 ; n < nDigestIterations ; ++n) {
	    sha1.update(xplusd,0,nCoinLength+PublicBank.DIGEST_LENGTH*n);
	    Util.byteCopy(xplusd,nCoinLength+PublicBank.DIGEST_LENGTH*n,
			  sha1.digest(),0,PublicBank.DIGEST_LENGTH);
	}
	
	//	HexDump("x|hash(x)=",xplusd,
	//	nCoinLength+nDigestIterations*PublicBank.DIGEST_LENGTH);

	BigInteger bi=new BigInteger(xplusd);
	Util.dumpNumber(System.out,"y=        ",bi);

	return bi;
    }

    void read(BufferedReader reader)
      throws IOException {
	m_biCoinID=Util.readNumber(reader,"id=");
    }
    void write(PrintStream str) {
	Util.dumpNumber(str,"id=",m_biCoinID);
    }

    public static void main(String args[]) {
	try {
	    UnsignedCoin coin=new UnsignedCoin();

	    PublicBank bank=new PublicBank(new BufferedReader(new FileReader(args[0])));
	    coin.random(bank.getCoinLength());
	    coin.generateCoinNumber(bank);
	} catch(Exception e) {
	    System.err.println("Failed: "+e.toString());
	}
    }

}
