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

	Util.setDumper(System.err);

	BufferedReader rdrBank=Util.newBufferedFileReader(szBankFile);
	PrintStream strCoin=Util.newFilePrintStream(szCoinFile);
	PrintStream strPublicCoin=Util.newFilePrintStream(szPublicCoinFile);

	PublicBank bank=new PublicBank(rdrBank);

	CoinRequest req=new CoinRequest(bank);
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
	if(args.length >= 5)
	    nRepeats=Integer.valueOf(args[4]).intValue();
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
	Util.readNumber(rdrSignature,"request=");
	BigInteger biSignature=Util.readNumber(rdrSignature,"signature=");
	Util.dumpNumber("signature=",biSignature);
	Coin coin=req.processResponse(bank,biSignature);
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

    public static void main(String args[])
      throws IOException,NoSuchAlgorithmException {
	if(args.length < 1) {
	    System.err.println("... <function>");
	    System.exit(1);
	}

	String function=args[0];

	if(function.equals("bank-new")) {
	    doBankNew(args);
	} else if(function.equals("coin-request")) {
	    doCoinRequest(args);
	} else if(function.equals("bank-sign")) {
	    doBankSign(args);
	} else if(function.equals("coin-unblind")) {
	    doCoinUnblind(args);
	} else if(function.equals("bank-verify")) {
	    doBankVerify(args);
	} else {
	    System.err.println("Unknown function: "+function);
	    System.exit(2);
	}
    }
}
	    
