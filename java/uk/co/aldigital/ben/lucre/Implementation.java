/* ====================================================================
 * Copyright (c) 1999, 2000, 2003 Ben Laurie.  All rights reserved.
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
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

class Implementation {
    static void doBankNew(String args[])
      throws IOException {
	if(args.length != 4) {
	    System.err.println("bank-new <key length> <private file> <public file>");
	    System.exit(1);
	}
	int nPrimeLength=Integer.valueOf(args[1]).intValue();
	String szFile=args[2];
	String szPublicFile=args[3];

	Bank bank=new Bank(nPrimeLength);
	bank.write(Util.newFilePrintStream(szFile));
	bank.writePublic(Util.newFilePrintStream(szPublicFile));
    }

    static void doCoinRequest(String args[])
      throws NoSuchAlgorithmException,IOException {
	if(args.length != 4) {
	    System.err.println("coin-request <bank public info> <coin request> <public coin requet>");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szCoinFile=args[2];
	String szPublicCoinFile=args[3];

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	PrintStream strCoin=Util.newFilePrintStream(szCoinFile);
	PrintStream strPublicCoin=Util.newFilePrintStream(szPublicCoinFile);

	PublicBank bank=new PublicBank(rdrBank);

	CoinRequest req=new CoinRequest(bank);
	req.write(strCoin);
	((PublicCoinRequest)req).write(strPublicCoin);
    }

    static void doCoinRequest2(String args[])
      throws NoSuchAlgorithmException,IOException {
	if(args.length != 4) {
	    System.err.println("coin-request <bank public info> <coin request> <public coin requet>");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szCoinFile=args[2];
	String szPublicCoinFile=args[3];

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	PrintStream strCoin=Util.newFilePrintStream(szCoinFile);
	PrintStream strPublicCoin=Util.newFilePrintStream(szPublicCoinFile);

	PublicBank bank=new PublicBank(rdrBank);

	DoubleCoinRequest req=new DoubleCoinRequest(bank);
	req.write(strCoin);
	((PublicCoinRequest)req).write(strPublicCoin);
    }
  
    static void doBankSign(String args[])
      throws IOException {
	if(args.length != 4 && args.length != 5) {
	    System.err.println("bank-sign <bank file> <coin request> <coin signature> [<signature repeats>]");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szRequest=args[2];
	String szSignature=args[3];
	int nRepeats=1;
	if(args.length >= 5) {
	    nRepeats=Integer.valueOf(args[4]).intValue();
	    Util.setDumper(null);
	}
	else
	    Util.setDumper(System.err);

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	BufferedReader rdrRequest=Util.newBufferedFileReader(szRequest);
	PrintStream strSignature=Util.newFilePrintStream(szSignature);

	Bank bank=new Bank(rdrBank);
	PublicCoinRequest req=new PublicCoinRequest(rdrRequest);
	for(int n=0 ; n < nRepeats-1 ; ++n)
	    bank.signRequest(req);
	BigInteger biSignature=bank.signRequest(req);
	req.write(strSignature);
	Util.dumpNumber(strSignature,"signature=",biSignature);
    }

    static void doCoinUnblind(String args[])
      throws IOException {
	if(args.length != 5) {
	    System.err.println("coin-unblind <bank public info> <private coin request> <signed coin request> <coin>");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szPrivateRequestFile=args[2];
	String szSignatureFile=args[3];
	String szCoinFile=args[4];

	Util.setDumper(System.err);

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	BufferedReader rdrPrivateRequest=
	  Util.newBufferedFileReader(szPrivateRequestFile);
	BufferedReader rdrSignature=
	  Util.newBufferedFileReader(szSignatureFile);
	PrintStream strCoin=Util.newFilePrintStream(szCoinFile);

	PublicBank bank=new PublicBank(rdrBank);
	CoinRequest req=new CoinRequest(rdrPrivateRequest);
	BlindedCoin blind=new BlindedCoin(rdrSignature);
	Coin coin=req.processResponse(bank,blind.getSignature());
	coin.write(strCoin);
    }
	
    static void doCoinUnblind2(String args[])
      throws IOException {
	if(args.length != 5) {
	    System.err.println("coin-unblind <bank public info> <private coin request> <signed coin request> <coin>");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szPrivateRequestFile=args[2];
	String szSignatureFile=args[3];
	String szCoinFile=args[4];

	Util.setDumper(System.err);

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	BufferedReader rdrPrivateRequest=
	  Util.newBufferedFileReader(szPrivateRequestFile);
	BufferedReader rdrSignature=
	  Util.newBufferedFileReader(szSignatureFile);
	PrintStream strCoin=Util.newFilePrintStream(szCoinFile);

	PublicBank bank=new PublicBank(rdrBank);
	DoubleCoinRequest req=new DoubleCoinRequest(rdrPrivateRequest);
	BlindedCoin blind=new BlindedCoin(rdrSignature);
	Coin coin=req.processResponse(bank,blind.getSignature());
	coin.write(strCoin);
    }
	
    static void doBankVerify(String args[])
      throws IOException,NoSuchAlgorithmException {
	if(args.length != 3) {
	    System.err.println("bank-verify <bank info> <coin>");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szCoinFile=args[2];

	Util.setDumper(System.err);

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	BufferedReader rdrCoin=Util.newBufferedFileReader(szCoinFile);

	Bank bank=new Bank(rdrBank);
	Coin coin=new Coin(rdrCoin);

	if(!bank.verify(coin)) {
	    System.err.println("Bad coin!");
	    System.exit(1);
	}
	System.exit(0);
    }

    static void doZK1Generate(String args[])
      throws IOException {
	if(args.length != 5) {
	    System.err.println("zk1-generate <bank info> <coin request> <private file> <public file>");
	    System.exit(1);
	}
	String szBankFile=args[1];
	String szRequestFile=args[2];
	String szPrivate=args[3];
	String szPublic=args[4];

	Bank bank=new Bank(szBankFile);
	CoinRequest req=new CoinRequest(szRequestFile);

	ZKVariant1Server zk=new ZKVariant1Server(bank,req);
	zk.generate();
	zk.write(szPrivate);
	zk.writePublic(szPublic);
    }

    static void doZK1Challenge(String args[])
      throws IOException {
	if(args.length != 2) {
	    System.err.println("zk1-challenge <challenge>");
	    System.exit(1);
	}
	String szChallenge=args[1];

	ZKVariant1Client zk=new ZKVariant1Client();
	zk.generate();
	zk.write(szChallenge);
    }

    static void doZK1Respond(String args[])
      throws IOException {
	if(args.length != 5) {
	    System.err.println("zk1-respond <bank private> <zk private> <challenge> <response>");
	    System.exit(1);
	}
	String szBankPrivate=args[1];
	String szZKPrivate=args[2];
	String szChallenge=args[3];
	String szResponse=args[4];

	Bank bank=new Bank(szBankPrivate);
	ZKVariant1Server zk=new ZKVariant1Server(bank,szZKPrivate);
	zk.respond(szResponse,szChallenge);
    }

    static void doZK1Verify(String args[])
      throws IOException {
	if(args.length != 7) {
	    System.err.println("zk1-respond <bank public> <zk public> <challenge> <response> <coin request> <coin>");
	    System.exit(1);
	}
	String szBankPublic=args[1];
	String szZKPublic=args[2];
	String szChallenge=args[3];
	String szResponse=args[4];
	String szRequest=args[5];
	String szCoin=args[6];

	PublicBank bank=new PublicBank(szBankPublic);
	PublicCoinRequest req=new PublicCoinRequest(szRequest);
	BlindedCoin coin=new BlindedCoin(szCoin);
	ZKVariant1Client zk=new ZKVariant1Client(bank,req,coin);
	zk.read(szChallenge);
	zk.readResponse(szResponse);
	zk.readCommitments(szZKPublic);
	if(!zk.verify()) {
	    System.err.println("ZK verification failed!");
	    System.exit(1);
	}
    }

    static void doVersion(String args[]) {
        if(args.length != 1)
	    {
	    System.err.println("version");
	    System.exit(1);
	    }
	System.out.println(Version.VERSION);
    }

    public static void main(String args[])
      throws IOException,NoSuchAlgorithmException {
	if(args.length < 1) {
	    System.err.println("... <function>");
	    System.exit(1);
	}

	String function=args[0];

	Util.setDumper(System.err);

	if(function.equals("bank-new")) {
	    doBankNew(args);
	} else if(function.equals("coin-request")) {
	    doCoinRequest(args);
	} else if(function.equals("coin-request2")) {
	    doCoinRequest2(args);
	} else if(function.equals("bank-sign")) {
	    doBankSign(args);
	} else if(function.equals("coin-unblind")) {
	    doCoinUnblind(args);
	} else if(function.equals("coin-unblind2")) {
	    doCoinUnblind2(args);
	} else if(function.equals("bank-verify")) {
	    doBankVerify(args);
	} else if(function.equals("zk1-generate")) {
	    doZK1Generate(args);
	} else if(function.equals("zk1-challenge")) {
	    doZK1Challenge(args);
	} else if(function.equals("zk1-respond")) {
	    doZK1Respond(args);
	} else if(function.equals("zk1-verify")) {
	    doZK1Verify(args);
	} else if(function.equals("version")) {
	    doVersion(args);
	} else {
	    System.err.println("Unknown function: "+function);
	    System.exit(2);
	}
    }
}
	    
