package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;

class PublicBank {
    public final static int BLINDING_LENGTH=8;
    public final static int MIN_COIN_LENGTH=16;
    public final static int DIGEST_LENGTH=20;	// Hmmph. Ought to be able to get this from somewhere...

    BigInteger m_biGenerator;
    BigInteger m_biPrime;
    BigInteger m_biPublicKey;	// i.e. g^k mod p

    protected PublicBank() {
    }
    public PublicBank(BufferedReader reader)
      throws LucreIOException, IOException {
	m_biGenerator=Util.readNumber(reader,"g=");
	m_biPrime=Util.readNumber(reader,"p=");
	m_biPublicKey=Util.readNumber(reader,"public=");
    }
    public PublicBank(String szFile)
      throws LucreIOException, IOException {
	this(Util.newBufferedFileReader(szFile));
    }
    public final BigInteger getPrime() {
	return m_biPrime;
    }
    public final BigInteger getGenerator() {
	return m_biGenerator;
    }
    public final BigInteger getPublicKey() {
	return m_biPublicKey;
    }
    public BigInteger getExponentGroupOrder() {
	return m_biPrime.subtract(BigInteger.valueOf(1))
	  .divide(BigInteger.valueOf(2));
    }
    public void dump(PrintStream out) {
	Util.dumpNumber(out,"p=        ",m_biPrime);
	Util.dumpNumber(out,"g=        ",m_biGenerator);
	Util.dumpNumber(out,"g^k=      ",m_biPublicKey);
    }
    public void write(PrintStream out) {
	Util.dumpNumber(out,"g=",m_biGenerator);
	Util.dumpNumber(out,"p=",m_biPrime);
	Util.dumpNumber(out,"public=",m_biPublicKey);
    }
    public int getCoinLength() {
	return MIN_COIN_LENGTH
	  +(getPrimeLength()-MIN_COIN_LENGTH)%DIGEST_LENGTH;
    }
    public int getPrimeLength() {
	return (getPrime().bitLength()+7)/8;
    }
    public int getPrimeLengthBits() {
	return getPrime().bitLength();
    }

    public static void main(String args[])
      throws IOException {
	if(args.length < 1) {
	    System.err.println("... <public bank file>");
	    System.exit(1);
	}
	PublicBank bank=new PublicBank(Util.newBufferedFileReader(args[0]));
	bank.dump(System.out);
    }
};
