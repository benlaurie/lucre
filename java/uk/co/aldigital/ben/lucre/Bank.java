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
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

class Bank extends PublicBank {
    private BigInteger m_biPrivateKey;

    Bank(int nPrimeLengthBits) {
	assert nPrimeLengthBits >= MIN_COIN_LENGTH+DIGEST_LENGTH;
	m_biGenerator=BigInteger.valueOf(4);
	m_biPrime=Util.generateGermainPrime(nPrimeLengthBits,20);

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
	assert !m_biGenerator.modPow(Util.TWO,m_biPrime).equals(Util.ONE)
	    : "g^2 != 1 (mod p)";
	assert m_biGenerator.modPow(m_biPrime.shiftRight(1),
				    m_biPrime).equals(Util.ONE)
	    : "g^((p-1)/2) = 1 (mod p)";
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

	return t.equals(coin.getSignature());
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
