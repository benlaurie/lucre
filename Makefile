all: web

web:
	cd doc; make web
	ssh sepulchre.aldigital.co.uk "ssh -l root -p 222 localhost \"su -m web -c '( cd ~web/work/anoncvs.aldigital.co.uk-lucre; cvs update -Pd )'\""