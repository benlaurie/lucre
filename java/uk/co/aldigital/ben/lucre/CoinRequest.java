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

	for( ; ; )
	    {
	    m_coin.random(bank.getCoinLength());

	    y=m_coin.generateCoinNumber(bank);

	    if(y.compareTo(bank.getPrime()) < 0)
		break;
	    }

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
