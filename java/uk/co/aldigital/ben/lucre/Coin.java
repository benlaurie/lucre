package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.IOException;
import java.math.BigInteger;

class Coin extends UnsignedCoin {
    private BigInteger m_biSignature;

    Coin(BufferedReader reader)
      throws IOException {
	read(reader);
    }
    Coin(UnsignedCoin coin,BigInteger biCoinSignature) {
	set(coin,biCoinSignature);
    }
    void read(BufferedReader reader)
      throws IOException {
	super.read(reader);
	m_biSignature=Util.readNumber(reader,"signature=");
    }
    void write(PrintStream str) {
	super.write(str);
	Util.dumpNumber(str,"signature=",m_biSignature);
    }
    public void set(BigInteger biCoinID,BigInteger biCoinSignature) {
	m_biSignature=biCoinSignature;
	set(biCoinID);
    }
    public void set(UnsignedCoin coin,BigInteger biCoinSignature) {
	set(coin.id(),biCoinSignature);
    }
    public BigInteger getSignature() {
	return m_biSignature;
    }
}

