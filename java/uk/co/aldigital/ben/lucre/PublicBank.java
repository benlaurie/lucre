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
        return m_biPrime.shiftRight(1);
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
    public boolean checkGroupMembership(BigInteger c) {
	BigInteger one=BigInteger.valueOf(1);
	BigInteger two=BigInteger.valueOf(2);

	return m_biGenerator.modPow(m_biPrime.subtract(one).divide(two),
				    m_biPrime).equals(one);
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
