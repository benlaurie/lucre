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
	m_bia=m_bank.generateExponent();
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
	    BigInteger p2=m_bank.getExponentGroupOrder();
	    BigInteger b=m_bank.getPrivateKey().multiply(m_bia.modInverse(p2))
	      .mod(p2);
	    Util.dumpNumber(str,"x=",b);
	    Util.dumpNumber("a= ",m_bia);
	    Util.dumpNumber("b= ",b);
	    Util.dumpNumber("ab=",b.multiply(m_bia).mod(p2));
	    Util.dumpNumber("k= ",m_bank.getPrivateKey());
	    Util.assert(b.multiply(m_bia).mod(p2)
			.equals(m_bank.getPrivateKey()),"ab=k");
	}
    }
    public void respond(String szResponse,String szChallenge)
      throws IOException {
	respond(Util.newFilePrintStream(szResponse),
		Util.newBufferedFileReader(szChallenge));
    }

}
