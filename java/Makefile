.SUFFIXES: .java .class

.java.class:
	javac -deprecation -depend $<

LUCRE_CLASSES=LucreIOException Util PublicBank Bank UnsignedCoin Coin \
	PublicCoinRequest CoinRequest BlindedCoin ZKVariant1Server \
	ZKVariant1Client Implementation DoubleCoinRequest
LUCRE=$(LUCRE_CLASSES:%=uk/co/aldigital/ben/lucre/%.class)

all: lucre.jar

lucre.jar: $(LUCRE)
	jar -cf lucre.jar $(LUCRE)

I=java uk.co.aldigital.ben.lucre.Implementation

test: test-bank test-coin test-coin2

test-bank: all
	$I bank-new 288 /tmp/bank-private /tmp/bank-public

test-coin: all
	$I coin-request /tmp/bank-public /tmp/coin-private /tmp/coin-request
	$I bank-sign /tmp/bank-private /tmp/coin-request /tmp/coin-signature
	$I coin-unblind /tmp/bank-public /tmp/coin-private /tmp/coin-signature /tmp/coin
	$I bank-verify /tmp/bank-private /tmp/coin
	$I zk1-generate /tmp/bank-private /tmp/coin-request /tmp/zk1-private /tmp/zk1-public
	$I zk1-challenge /tmp/zk1-challenge
	$I zk1-respond /tmp/bank-private /tmp/zk1-private /tmp/zk1-challenge /tmp/zk1-response
	$I zk1-verify /tmp/bank-public /tmp/zk1-public /tmp/zk1-challenge /tmp/zk1-response /tmp/coin-request /tmp/coin-signature

test-coin2: all
	$I coin-request2 /tmp/bank-public /tmp/coin-private /tmp/coin-request
	$I bank-sign /tmp/bank-private /tmp/coin-request /tmp/coin-signature
	$I coin-unblind2 /tmp/bank-public /tmp/coin-private /tmp/coin-signature /tmp/coin
	$I bank-verify /tmp/bank-private /tmp/coin

test2: lucre.jar
	java uk.co.aldigital.ben.lucre.Bank 288
	java uk.co.aldigital.ben.lucre.PublicBank /tmp/bank-public
	java uk.co.aldigital.ben.lucre.UnsignedCoin /tmp/bank-public

bench-bank: all
	$I bank-new 512 /tmp/bench-bank-private /tmp/bench-bank-public

bench: lucre.jar
	$I bank-sign /tmp/bank-private /tmp/coin-request /tmp/coin-signature
	time $I bank-sign /tmp/bench-bank-private /tmp/coin-request /tmp/coin-signature
	time $I bank-sign /tmp/bench-bank-private /tmp/coin-request /tmp/coin-signature 100

clean:
	rm $(LUCRE)
