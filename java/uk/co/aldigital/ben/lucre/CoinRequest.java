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

class CoinRequest extends PublicCoinRequest {
    private BigInteger m_biBlindingFactor;
    private UnsignedCoin m_coin=new UnsignedCoin();

    CoinRequest(PublicBank bank)
      throws NoSuchAlgorithmException {
	BigInteger y;

	m_coin.random(bank);
	y=m_coin.generateCoinNumber(bank);

	// choose b
	m_biBlindingFactor=new BigInteger(PublicBank.BLINDING_LENGTH*8,
					  Util.randomGenerator());
	Util.dumpNumber("b=        ",m_biBlindingFactor);

	// calculate A->B: y g^b
	m_biCoinRequest=bank.getGenerator().modPow(m_biBlindingFactor,
						   bank.getPrime());
	m_biCoinRequest=m_biCoinRequest.multiply(y).mod(bank.getPrime());
	Util.dumpNumber("A->B=     ",m_biCoinRequest);
    }	
    CoinRequest(BufferedReader rdr)
      throws IOException {
	read(rdr);
    }
    CoinRequest(String szFile)
      throws IOException {
	this(Util.newBufferedFileReader(szFile));
    }
    void write(PrintStream str) {
	super.write(str);
	m_coin.write(str);
	Util.dumpNumber(str,"blinding=",m_biBlindingFactor);
    }
    void read(BufferedReader rdr)
      throws IOException {
	super.read(rdr);
	m_coin.read(rdr);
	m_biBlindingFactor=Util.readNumber(rdr,"blinding=");
    }
    BigInteger unblind(BigInteger biSignedCoin,PublicBank bank) {
	BigInteger z=bank.getPublicKey().modPow(m_biBlindingFactor,
						bank.getPrime());
	z=z.modInverse(bank.getPrime());
	z=z.multiply(biSignedCoin);
	z=z.mod(bank.getPrime());

	return z;
    }
    Coin processResponse(PublicBank bank,
			 BigInteger biSignedCoinRequest) {
	BigInteger biCoinSignature=unblind(biSignedCoinRequest,bank);
	Util.dumpNumber("z=        ",biCoinSignature);

	return new Coin(m_coin,biCoinSignature);
    }
}
