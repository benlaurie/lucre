all: web

web:
	ssh sepulchre.aldigital.co.uk "ssh root@sepulchre \"su -m web -c '( cd ~web/work/anoncvs.aldigital.co.uk-lucre; cvs update -Pd )'\""