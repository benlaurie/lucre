package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;

class BlindedCoin extends PublicCoinRequest {
    private BigInteger m_biBlindedSignature;

    BlindedCoin(BufferedReader rdr)
      throws IOException {
	read(rdr);
    }
    BlindedCoin(String szFile)
      throws IOException {
	read(szFile);
    }
    void read(BufferedReader rdr)
      throws IOException {
	super.read(rdr);
	m_biBlindedSignature=Util.readNumber(rdr,"signature=");
	Util.dumpNumber("signature=",m_biBlindedSignature);
    }
    void read(String szFile)
      throws IOException {
	read(Util.newBufferedFileReader(szFile));
    }

    public BigInteger getSignature() {
	return m_biBlindedSignature;
    }
}
