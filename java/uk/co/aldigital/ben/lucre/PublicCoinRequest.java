package uk.co.aldigital.ben.lucre;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;

class PublicCoinRequest {
    protected BigInteger m_biCoinRequest;

    PublicCoinRequest() {
    }
    PublicCoinRequest(BufferedReader rdr)
      throws IOException {
	read(rdr);
    }
    void write(PrintStream str) {
	Util.dumpNumber(str,"request=",m_biCoinRequest);
    }
    void read(BufferedReader rdr)
      throws IOException {
	m_biCoinRequest=Util.readNumber(rdr,"request=");
    }
    BigInteger getRequest() {
	return m_biCoinRequest;
    }
};
