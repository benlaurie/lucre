/* ====================================================================
 * Copyright (c) 1999, 2000 Ben Laurie.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by Ben Laurie
 *    for use in the Lucre project."
 *
 * 4. The name "Lucre" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission.
 *
 * 5. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by Ben Laurie
 *    for use in the Lucre project."
 *
 * THIS SOFTWARE IS PROVIDED BY BEN LAURIE ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL BEN LAURIE OR
 * HIS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 * For more information on Lucre see http://anoncvs.aldigital.co.uk/lucre/.
 *
 */

package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Random;

class Util {
    static PrintStream strDump;
    public final static BigInteger ONE=BigInteger.valueOf(1);
    public final static BigInteger TWO=BigInteger.valueOf(2);

    static void setDumper(PrintStream str) {
	strDump=str;
    }
    static void hexDump(PrintStream out,String s,byte b[],int n) {
        int i;

        out.print(s);
	for(i=0 ; i < n ; ++i)
	    {
	    int x=b[i];
	    if(x < 0)
		x=256+x;
	    s=Integer.toString(x,16);
	    if(s.length() == 1)
		s="0"+s;
	    out.print(s);
	    }
	out.println();
    }
    static void hexDump(String s,byte b[],int n) {
        hexDump(strDump,s,b,n);
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
    static BigInteger generateGermainPrime(int nBitLength,int nCertainty) {
	Random rand=randomGenerator();
	for( ; ; ) {
	    BigInteger biPrime=new BigInteger(nBitLength,nCertainty,rand);
	    if(biPrime.shiftRight(1).isProbablePrime(nCertainty)) {
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
    static BigInteger random(BigInteger lower,BigInteger upper) {
	Random rand=randomGenerator();
	int nBits=upper.bitLength();
	for( ; ; ) {
	    BigInteger b=new BigInteger(nBits,rand);
	    if(b.compareTo(lower) >= 0 && b.compareTo(upper) <= 0)
		return b;
	}
    }
    static BigInteger random(int lower,BigInteger upper) {
	return random(BigInteger.valueOf(lower),upper);
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

/*
    static void assert(boolean truth,String failure) {
	if(!truth) {
	    System.err.println("assertion failed: "+failure);
	    System.exit(1);
	}
    }
*/
}
