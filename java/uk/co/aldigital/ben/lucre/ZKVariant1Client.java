package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Math;
import java.math.BigInteger;

class ZKVariant1Client {
    PublicCoinRequest m_req;
    PublicBank m_bank;
    BlindedCoin m_coin;
    BigInteger m_biChallenge;
    BigInteger m_biA;
    BigInteger m_biQ;
    BigInteger m_biResponse;

    ZKVariant1Client() {
    }
    ZKVariant1Client(PublicBank bank,PublicCoinRequest req,BlindedCoin coin) {
	m_bank=bank;
	m_req=req;
	m_coin=coin;
    }
    void generate() {
	m_biChallenge=BigInteger.valueOf(Math.abs(Util.randomGenerator().nextInt())%2);
    }
    void write(PrintStream str) {
	Util.dumpNumber(str,"challenge=",m_biChallenge);
    }
    void write(String szFile)
      throws IOException {
	write(Util.newFilePrintStream(szFile));
    }
    void read(BufferedReader rdr)
      throws IOException {
	m_biChallenge=Util.readNumber(rdr,"challenge=");
    }
    void read(String szFile)
      throws IOException {
	read(Util.newBufferedFileReader(szFile));
    }
    void readResponse(BufferedReader rdr)
      throws IOException {
	m_biResponse=Util.readNumber(rdr,"x=");
    }
    void readResponse(String szFile)
      throws IOException {
	readResponse(Util.newBufferedFileReader(szFile));
    }
    void readCommitments(BufferedReader rdr)
      throws IOException {
	m_biQ=Util.readNumber(rdr,"Q=");
	m_biA=Util.readNumber(rdr,"A=");
    }
    void readCommitments(String szFile)
      throws IOException {
	readCommitments(Util.newBufferedFileReader(szFile));
    }
    boolean verify() {
	BigInteger p=m_bank.getPrime();
	if(m_biChallenge.equals(BigInteger.valueOf(1))) {
	    // we chose t
	    BigInteger t=m_biResponse;
	    BigInteger tmp=m_biA.modPow(t,p);
	    Util.dumpNumber("p=   ",m_bank.getPublicKey());
	    Util.dumpNumber("tmp1=",tmp);
	    if(!tmp.equals(m_bank.getPublicKey()))
		return false;
	    tmp=m_biQ.modPow(t,p);
	    Util.dumpNumber("s=   ",m_coin.getSignature());
	    Util.dumpNumber("tmp2=",tmp);
	    if(!tmp.equals(m_coin.getSignature()))
		return false;
	} else {
	    // we chose r
	    BigInteger r=m_biResponse;
	    BigInteger tmp=m_req.getRequest().modPow(r,p);
	    if(!tmp.equals(m_biQ))
		return false;
	    tmp=m_bank.getGenerator().modPow(r,p);
	    if(!tmp.equals(m_biA))
		return false;
	}
	return true;
    }
	    
}

