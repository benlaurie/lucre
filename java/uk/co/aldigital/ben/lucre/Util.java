package uk.co.aldigital.ben.lucre;

  //import cryptix.provider.Cryptix;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Random;

class Util {
    static PrintStream strDump;

    static void setDumper(PrintStream str) {
	strDump=str;
    }
    static void dumpNumber(PrintStream out,String s,BigInteger bi) {
	out.print(s);
	out.println(bi.toString(16));
    }
    static void dumpNumber(String s,BigInteger bi) {
	if(strDump == null)
	    return;
	dumpNumber(strDump,s,bi);
    }
    static void dumpNumber(PrintStream out,String s,int i) {
	out.println(s+i);
    }
    static BigInteger readNumber(BufferedReader reader,String title)
      throws LucreIOException,IOException {
	String line=reader.readLine();
	if(line == null)
	    throw new LucreIOException("End of file when expecting '"+title+"'");
	if(!line.startsWith(title))
	    throw new LucreIOException("Read '"+line+"' when expecting '"+title+"'");
	return new BigInteger(line.substring(title.length()),16);
    }

    static BigInteger generateGermainPrimeWithRemainder(int nBitLength,
							BigInteger biDivisor,
							BigInteger biRemainder,
							int nCertainty) {
	Random rand=randomGenerator();
	for( ; ; ) {
	    BigInteger biPrime=new BigInteger(nBitLength,nCertainty,rand);
	    if(biPrime.remainder(biDivisor).compareTo(biRemainder) == 0
	       && biPrime.shiftRight(1).isProbablePrime(nCertainty)) {
		System.out.println("");
		return biPrime;
	    }
	    System.out.print(".");
	    System.out.flush();
	}
    }
    static Random randomGenerator() {
	System.err.println("Warning! Low quality randomness in use!");
	return new Random();
    }
    static void byteCopy(byte dest[],int doff,byte src[],int soff,int len) {
	for(int n=0 ; n < len ; ++n)
	    dest[doff+n]=src[soff+n];
    }
    static BufferedReader newBufferedFileReader(String file)
      throws IOException {
	return new BufferedReader(new FileReader(file));
    }
    static PrintStream newFilePrintStream(String file)
      throws IOException {
	return new PrintStream(new FileOutputStream(file));
    }
    /*
    static boolean bCryptoAdded;
    static void addCrypto() {
	if(bCryptoAdded)
	    return;
	
	java.security.Security.addProvider(new cryptix.provider.Cryptix());
	bCryptoAdded=true;
    }
    */

    static void assert(boolean truth,String failure) {
	if(!truth) {
	    System.err.println("assertion failed: "+failure);
	    System.exit(1);
	}
    }
}
