#include "bank.h"

int main(int argc,char **argv)
    {
    if(argc != 4)
	{
	fprintf(stderr,"%s <bank file> <coin request> <coin signature>\n",
		argv[0]);
	exit(1);
	}
    const char *szBankFile=argv[1];
    const char *szRequest=argv[2];
    const char *szSignature=argv[3];

    SetDumper(stderr);

    BIO *bioBank=BIO_new_file(szBankFile,"r");
    BIO *bioRequest=BIO_new_file(szRequest,"r");
    BIO *bioSignature=BIO_new_file(szSignature,"w");

    Bank bank(bioBank);
    PublicCoinRequest req(bioRequest);
    BIGNUM *bnSignature=bank.SignRequest(req);
    req.WriteBIO(bioSignature);
    DumpNumber(bioSignature,"signature=",bnSignature);
    }
	
