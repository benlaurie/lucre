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
	m_biGenerator=new BigInteger("2");
	m_biPrime=Util.generateGermainPrimeWithRemainder(nPrimeLengthBits,
							 new BigInteger("24"),
							 new BigInteger("11"),
							 1);
	m_biPrivateKey=new BigInteger(getPrimeLengthBits(),
				      Util.randomGenerator());
	m_biPublicKey=m_biGenerator.modPow(m_biPrivateKey,m_biPrime);
    }
    Bank(BufferedReader rdr)
      throws IOException {
	super(rdr);
	m_biPrivateKey=Util.readNumber(rdr,"private=");
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
