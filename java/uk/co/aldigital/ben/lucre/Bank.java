package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

class Bank extends PublicBank {
    private BigInteger m_biPrivateKey;

    Bank(int nPrimeLengthBits) {
	Util.assert(nPrimeLengthBits >= MIN_COIN_LENGTH+DIGEST_LENGTH,"nPrimeLength >= MIN_COIN_LENGTH+DIGEST_LENGTH");
	m_biGenerator=BigInteger.valueOf(4);
	m_biPrime=Util.generateGermainPrime(nPrimeLengthBits,1);

	m_biPrivateKey=generateExponent();
	m_biPublicKey=m_biGenerator.modPow(m_biPrivateKey,m_biPrime);
	verifyGenerator();
    }
    Bank(BufferedReader rdr)
      throws IOException {
	super(rdr);
	m_biPrivateKey=Util.readNumber(rdr,"private=");
    }
    Bank(String szFile)
      throws IOException {
	this(Util.newBufferedFileReader(szFile));
    }
    BigInteger generateExponent() {
	return Util.random(getPrimeLengthBits()-1,
			   getExponentGroupOrder()
			   .subtract(BigInteger.valueOf(getPrimeLengthBits()
							+2+1)));
    }
    private void verifyGenerator() {
	// The generator is supposed to yield g^2 != 1 (mod p)
	// and g^((p-1)/2) = 1 (mod p)
	BigInteger one=BigInteger.valueOf(1);
	BigInteger two=BigInteger.valueOf(2);

	Util.assert(!m_biGenerator.modPow(two,m_biPrime).equals(one),
		    "g^2 != 1 (mod p)");
	Util.assert(m_biGenerator.modPow(m_biPrime.subtract(one).divide(two),
					 m_biPrime).equals(one),
		    "g^((p-1)/2) = 1 (mod p)");
    }
    public void write(PrintStream out) {
	writePublic(out);
	Util.dumpNumber(out,"private=",m_biPrivateKey);
    }
    public void writePublic(PrintStream out) {
	super.write(out);
    }
    public void dump(PrintStream out) {
	super.dump(out);
	Util.dumpNumber(out,"k=        ",m_biPrivateKey);
    }
    public BigInteger getExponentGroupOrder() {
	return m_biPrime.subtract(BigInteger.valueOf(1))
	  .divide(BigInteger.valueOf(2));
    }
    public BigInteger getPrivateKey() {
	return m_biPrivateKey;
    }
    public BigInteger signRequest(PublicCoinRequest req) {
	BigInteger BtoA=req.getRequest().modPow(getPrivateKey(),getPrime());
	Util.dumpNumber("B->A=     ",BtoA);

	return BtoA;
    }
    public boolean verify(Coin coin)
      throws NoSuchAlgorithmException {
	BigInteger t=coin.generateCoinNumber(this);
	if(t == null)
	    return false;
	t=t.modPow(getPrivateKey(),getPrime());
	Util.dumpNumber("y^k=      ",t);

	t=t.subtract(coin.getSignature());

	return t.equals(BigInteger.valueOf(0));
    }

    public static void main(String args[])
      throws IOException {
	if(args.length < 1) {
	    System.err.println("... <prime length>");
	    System.exit(1);
	}
	Bank bank=new Bank(Integer.valueOf(args[0]).intValue());
	bank.dump(System.out);
    }
}
