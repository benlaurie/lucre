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

class ZKVariant1Server {
    private BigInteger m_bia;
    private Bank m_bank;
    private PublicCoinRequest m_req;

    ZKVariant1Server(Bank bank,PublicCoinRequest req) {
	m_bank=bank;
	m_req=req;
    }
    ZKVariant1Server(Bank bank,BufferedReader rdr)
      throws IOException {
	m_bank=bank;
	read(rdr);
    }
    ZKVariant1Server(Bank bank,String szFile)
      throws IOException {
	m_bank=bank;
	read(szFile);
    }
    void generate() {
        BigInteger p1=m_bank.getPrime().subtract(Util.ONE);
        BigInteger p2=m_bank.getPrime().subtract(Util.ONE);
        for( ; ; )
	    {
	    m_bia=Util.random(1,p2);
	    // must be invertible module p-1 (so we can generate the inverse
	    // exponent)
	    if(m_bia.gcd(p1).equals(Util.ONE))
		break;
	    }
    }
    public void writePublic(PrintStream str) {
	BigInteger p=m_bank.getPrime();
	BigInteger Q=m_req.getRequest().modPow(m_bia,p);
	BigInteger A=m_bank.getGenerator().modPow(m_bia,p);

	Util.dumpNumber(str,"Q=",Q);
	Util.dumpNumber(str,"A=",A);
    }
    public void writePublic(String szFile)
      throws IOException {
	writePublic(Util.newFilePrintStream(szFile));
    }
    public void write(PrintStream str) {
	Util.dumpNumber(str,"a=",m_bia);
    }
    public void write(String szFile)
      throws IOException {
	write(Util.newFilePrintStream(szFile));
    }
    public void read(BufferedReader rdr)
      throws IOException {
	m_bia=Util.readNumber(rdr,"a=");
    }
    public void read(String szFile)
      throws IOException {
	read(Util.newBufferedFileReader(szFile));
    }
    // note that this uses the same name for either response, so the client
    // _must_ remember which it asked for. This is deliberate!
    public void respond(PrintStream str,BufferedReader rdr)
      throws IOException {
	BigInteger challenge=Util.readNumber(rdr,"challenge=");
	if(challenge.equals(BigInteger.valueOf(0)))
	    Util.dumpNumber(str,"x=",m_bia);
	else {
	    BigInteger p1=m_bank.getPrime().subtract(Util.ONE);
	    BigInteger b=m_bank.getPrivateKey().multiply(m_bia.modInverse(p1))
	      .mod(p1);
	    Util.dumpNumber(str,"x=",b);
	    Util.dumpNumber("a= ",m_bia);
	    Util.dumpNumber("b= ",b);
	    Util.dumpNumber("ab=",b.multiply(m_bia).mod(p1));
	    Util.dumpNumber("k= ",m_bank.getPrivateKey());
	    Util.assert(b.multiply(m_bia).mod(p1)
			.equals(m_bank.getPrivateKey()),"ab=k");
	}
    }
    public void respond(String szResponse,String szChallenge)
      throws IOException {
	respond(Util.newFilePrintStream(szResponse),
		Util.newBufferedFileReader(szChallenge));
    }

}
