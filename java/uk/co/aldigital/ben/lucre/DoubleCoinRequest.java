package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

class DoubleCoinRequest extends PublicCoinRequest {
    private BigInteger m_biBlindingFactorY;
    private BigInteger m_biBlindingFactorG;
    private UnsignedCoin m_coin=new UnsignedCoin();

    DoubleCoinRequest(PublicBank bank)
      throws NoSuchAlgorithmException {
	BigInteger y;

	for( ; ; )
	    {
	    m_coin.random(bank.getCoinLength());

	    y=m_coin.generateCoinNumber(bank);

	    if(y.compareTo(bank.getPrime()) < 0)
		break;
	    }

	// choose b_y
	m_biBlindingFactorY=new BigInteger(PublicBank.BLINDING_LENGTH*8,
					  Util.randomGenerator());
	Util.dumpNumber("by=       ",m_biBlindingFactorY);

	// choose b_g
	m_biBlindingFactorG=new BigInteger(PublicBank.BLINDING_LENGTH*8,
					  Util.randomGenerator());
	Util.dumpNumber("bg=       ",m_biBlindingFactorG);

	// calculate A->B: y^b_y g^b_g
	m_biCoinRequest=bank.getGenerator().modPow(m_biBlindingFactorG,
						   bank.getPrime());
	y=y.modPow(m_biBlindingFactorY,bank.getPrime());
	m_biCoinRequest=m_biCoinRequest.multiply(y).mod(bank.getPrime());
	Util.dumpNumber("A->B=     ",m_biCoinRequest);
    }	
    DoubleCoinRequest(BufferedReader rdr)
      throws IOException {
	read(rdr);
    }
    DoubleCoinRequest(String szFile)
      throws IOException {
	this(Util.newBufferedFileReader(szFile));
    }
    void write(PrintStream str) {
	super.write(str);
	m_coin.write(str);
	Util.dumpNumber(str,"blindingY=",m_biBlindingFactorY);
	Util.dumpNumber(str,"blindingG=",m_biBlindingFactorG);
    }
    void read(BufferedReader rdr)
      throws IOException {
	super.read(rdr);
	m_coin.read(rdr);
	m_biBlindingFactorY=Util.readNumber(rdr,"blindingY=");
	m_biBlindingFactorG=Util.readNumber(rdr,"blindingG=");
    }
    BigInteger unblind(BigInteger biSignedCoin,PublicBank bank) {
	BigInteger z=bank.getPublicKey().modPow(m_biBlindingFactorG,
						bank.getPrime());
	z=z.modInverse(bank.getPrime());
	z=z.multiply(biSignedCoin);
	z=z.mod(bank.getPrime());

	BigInteger p2=bank.getExponentGroupOrder();
	BigInteger byinv=m_biBlindingFactorY.modInverse(p2);
	z=z.modPow(byinv,bank.getPrime());

	return z;
    }
    Coin processResponse(PublicBank bank,
			 BigInteger biSignedCoinRequest) {
	BigInteger biCoinSignature=unblind(biSignedCoinRequest,bank);
	Util.dumpNumber("z=        ",biCoinSignature);

	return new Coin(m_coin,biCoinSignature);
    }
}
