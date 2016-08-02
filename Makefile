all: build

build:
	mkdir -p build
	(cd src; make)

crane-deps:
	cd deps &&\
	wget http://www.cranesoftwrights.com/resources/ublss/Crane-UBL2UNLK-20110203-1600z.zip &&\
	unzip Crane-UBL2UNLK-20110203-1600z.zip

run:
	./run.sh -x ./si-ubl-artefacts/xsd/maindoc/UBL-Invoice-2.1.xsd -l ./si-ubl-artefacts/SI-UBL-INV-1.1.xsl -i ./Testbedrijf\ Factuur\ 20150001\ Jelte.xml

pdf:
	./run.sh -c -x ./si-ubl-artefacts/xsd/maindoc/UBL-Invoice-2.1.xsd -l ./si-ubl-artefacts/SI-UBL-INV-1.1.xsl -i ./Testbedrijf\ Factuur\ 20150001\ Jelte.xml

clean:
	rm -rf build
