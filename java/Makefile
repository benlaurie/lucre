.SUFFIXES: .java .class

.java.class:
	javac -deprecation -depend $<

LUCRE_CLASSES=LucreIOException Util PublicBank Bank UnsignedCoin Coin \
	PublicCoinRequest CoinRequest Implementation
LUCRE=$(LUCRE_CLASSES:%=uk/co/aldigital/ben/lucre/%.class)

all: lucre.jar

lucre.jar: $(LUCRE)
	jar -cf lucre.jar $(LUCRE)

I=java uk.co.aldigital.ben.lucre.Implementation

test: test-bank test-coin

test-bank: all
	$I bank-new 288 /tmp/bank-private /tmp/bank-public

test-coin: all
	$I coin-request /tmp/bank-public /tmp/coin-private /tmp/coin-request
	$I bank-sign /tmp/bank-private /tmp/coin-request /tmp/coin-signature
	$I coin-unblind /tmp/bank-public /tmp/coin-private /tmp/coin-signature /tmp/coin
	$I bank-verify /tmp/bank-private /tmp/coin

test2: lucre.jar
	java uk.co.aldigital.ben.lucre.Bank 288
	java uk.co.aldigital.ben.lucre.PublicBank /tmp/bank-public
	java uk.co.aldigital.ben.lucre.UnsignedCoin /tmp/bank-public

clean:
	rm $(LUCRE)
