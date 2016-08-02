#!/bin/sh

./run.sh -x ../xsd/maindoc/UBL-Invoice-2.1.xsd -l ../validation/SI-UBL-INV.xsl -m -c <<EOF
Message-ID: <54B039D8.6080502@tjeb.nl>
Date: Fri, 09 Jan 2015 21:28:08 +0100
From: Jelte Jansen <tjeb@tjeb.nl>
User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Icedove/31.3.0
MIME-Version: 1.0
To: si-test@tjeb.nl
Subject: test
Content-Type: multipart/mixed;
 boundary="------------010807030908000808090502"

This is a multi-part message in MIME format.
--------------010807030908000808090502
Content-Type: text/plain; charset=utf-8
Content-Transfer-Encoding: 7bit

test

--------------010807030908000808090502
Content-Type: text/xml;
 name="SI-INV-V1.0-COMPLIANT-MINIMAL.xml"
Content-Transfer-Encoding: 7bit
Content-Disposition: attachment;
 filename="SI-INV-V1.0-COMPLIANT-MINIMAL.xml"

<?xml version="1.0" encoding="UTF-8"?>
<Invoice xmlns="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2 UBL-Invoice-2.0.xsd">
	<cbc:UBLVersionID>2.0</cbc:UBLVersionID>
	<cbc:CustomizationID>urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.simplerinvoicing.org:si-ubl:invoice:ver1.0.x</cbc:CustomizationID>
	<cbc:ProfileID>urn:www.cenbii.eu:profile:bii04:ver1.0</cbc:ProfileID>
	<cbc:ID>I2013.03.4361</cbc:ID>
	<cbc:IssueDate>2013-08-06</cbc:IssueDate>
	<cbc:DocumentCurrencyCode listID="ISO 4217 Alpha" listAgencyID="6">EUR</cbc:DocumentCurrencyCode>
	<cac:AccountingSupplierParty>
		<cac:Party>
			<cac:PartyName>
				<cbc:Name>Innopay B.V.</cbc:Name>
			</cac:PartyName>
			<cac:PostalAddress>
				<cbc:StreetName>Main street</cbc:StreetName>
				<cbc:CityName>Big city</cbc:CityName>
				<cbc:PostalZone>54321</cbc:PostalZone>
				<cac:Country>
					<cbc:IdentificationCode listAgencyID="6" listID="ISO3166-1">NL</cbc:IdentificationCode>
				</cac:Country>
			</cac:PostalAddress>
		</cac:Party>
	</cac:AccountingSupplierParty>
	<cac:AccountingCustomerParty>
		<cac:Party>
			<cac:PartyName>
				<cbc:Name>Buyercompany VOF</cbc:Name>
			</cac:PartyName>
		</cac:Party>
	</cac:AccountingCustomerParty>
	<cac:LegalMonetaryTotal>
		<cbc:LineExtensionAmount currencyID="EUR">200</cbc:LineExtensionAmount>
		<cbc:TaxExclusiveAmount currencyID="EUR">200</cbc:TaxExclusiveAmount>
		<cbc:TaxInclusiveAmount currencyID="EUR">200</cbc:TaxInclusiveAmount>
		<cbc:PayableAmount currencyID="EUR">200</cbc:PayableAmount>
	</cac:LegalMonetaryTotal>
	<cac:InvoiceLine>
		<cbc:ID>1</cbc:ID>
		<cbc:InvoicedQuantity unitCode="10">3</cbc:InvoicedQuantity>
		<cbc:LineExtensionAmount currencyID="EUR">200</cbc:LineExtensionAmount>
		<cac:Item>
			<cbc:Name>Bitcoin Masterclass</cbc:Name>
		</cac:Item>
		<cac:Price>
			<cbc:PriceAmount currencyID="EUR">200</cbc:PriceAmount>
		</cac:Price>
	</cac:InvoiceLine>
</Invoice>

--------------010807030908000808090502--
EOF
