<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:nz_govt_natlib_xsl_XSLTFunctions="nz.govt.natlib.xsl.XSLTFunctions">
  <xsl:strip-space elements="doc chapter section"/>
  <xsl:output omit-xml-declaration="yes" indent="yes" encoding="iso-8859-1" version="1.0"/>
  <xsl:template match="/">
    <File>
      <FileIdentifier>
        <xsl:value-of select="nz_govt_natlib_xsl_XSLTFunctions:determineFileIdentifier(string(WORD/METADATA/PID),string(WORD/METADATA/OID),string(WORD/METADATA/FILENAME),string(WORD/METADATA/FID))"/>
      </FileIdentifier>
      <xsl:for-each select="WORD/METADATA/PATH">
        <Path>
          <xsl:value-of select="."/>
        </Path>
      </xsl:for-each>
      <Filename>
        <xsl:for-each select="WORD/METADATA/FILENAME">
          <Name>
            <xsl:value-of select="."/>
          </Name>
        </xsl:for-each>
        <xsl:for-each select="WORD/METADATA/EXTENSION">
          <Extension>
            <xsl:value-of select="."/>
          </Extension>
        </xsl:for-each>
      </Filename>
      <xsl:for-each select="WORD/METADATA/FILELENGTH">
        <Size>
          <xsl:value-of select="."/>
        </Size>
      </xsl:for-each>
      <FileDateTime>
        <xsl:for-each select="WORD/METADATA/DATE">
          <Date format="yyyyMMdd">
            <xsl:value-of select="."/>
          </Date>
        </xsl:for-each>
        <xsl:for-each select="WORD/METADATA/TIME">
          <Time format="HHmmssSSS">
            <xsl:value-of select="."/>
          </Time>
        </xsl:for-each>
      </FileDateTime>
      <xsl:for-each select="WORD/METADATA/TYPE">
        <Mimetype>
          <xsl:value-of select="."/>
        </Mimetype>
      </xsl:for-each>
      <FileFormat>
        <Format>
          <xsl:value-of select="string('Microsoft Word')"/>
        </Format>
        <Version>
          <xsl:value-of select="nz_govt_natlib_xsl_XSLTFunctions:determineWordVersion(string(WORD/PROPERTIES/APPLICATION))"/>
        </Version>
      </FileFormat>
      <Text>
        <CharacterSet>
          <xsl:value-of select="string('UTF-8')"/>
        </CharacterSet>
        <MarkupLanguage>
          <xsl:value-of select="string('English')"/>
        </MarkupLanguage>
      </Text>
      <Summary>
		<xsl:for-each select="WORD/SUMMARY/TITLE">
          <Title>
            <xsl:value-of select="."/>
          </Title>
        </xsl:for-each>
        <xsl:for-each select="WORD/SUMMARY/AUTHOR">
          <Author>
            <xsl:value-of select="."/>
          </Author>
        </xsl:for-each>
        <xsl:for-each select="WORD/SUMMARY/TEMPLATE">
          <Template>
            <xsl:value-of select="."/>
          </Template>
        </xsl:for-each>    
        <xsl:for-each select="WORD/SUMMARY/SUBJECT">
          <Subject>
            <xsl:value-of select="."/>
          </Subject>
        </xsl:for-each>              
      </Summary> 
      <Properties>
		<xsl:for-each select="WORD/PROPERTIES/CHARACTERS">
          <Characters>
            <xsl:value-of select="."/>
          </Characters>
        </xsl:for-each>
		<xsl:for-each select="WORD/PROPERTIES/CHARACTERSWITHSPACES">
          <CharactersWithSpaces>
            <xsl:value-of select="."/>
          </CharactersWithSpaces>
        </xsl:for-each>        
        <xsl:for-each select="WORD/PROPERTIES/CREATED">
          <DateCreated>
            <xsl:value-of select="."/>
          </DateCreated>
        </xsl:for-each>
        <xsl:for-each select="WORD/PROPERTIES/REVISION">
          <DateModified>
            <xsl:value-of select="."/>
          </DateModified>
        </xsl:for-each>    
        <xsl:for-each select="WORD/PROPERTIES/REVISIONCOUNT">
          <RevisionCount>
            <xsl:value-of select="."/>
          </RevisionCount>
        </xsl:for-each> 
        <xsl:for-each select="WORD/PROPERTIES/EDITTIME">
          <EditTime>
            <xsl:value-of select="."/>
          </EditTime>
        </xsl:for-each>        
        <xsl:for-each select="WORD/PROPERTIES/PAGES">
          <Pages>
            <xsl:value-of select="."/>
          </Pages>
        </xsl:for-each> 
        <xsl:for-each select="WORD/PROPERTIES/WORDS">
          <Words>
            <xsl:value-of select="."/>
          </Words>
        </xsl:for-each>
        <xsl:for-each select="WORD/PROPERTIES/LINES">
          <Lines>
            <xsl:value-of select="."/>
          </Lines>
        </xsl:for-each>
        <xsl:for-each select="WORD/PROPERTIES/PARAGRAPHS">
          <Paragraphs>
            <xsl:value-of select="."/>
          </Paragraphs>
        </xsl:for-each>  
        <xsl:for-each select="WORD/PROPERTIES/SECURITY">
          <Security>
            <xsl:value-of select="."/>
          </Security>
        </xsl:for-each> 
        <xsl:for-each select="WORD/PROPERTIES/COMPANY">
          <Company>
            <xsl:value-of select="."/>
          </Company>
        </xsl:for-each>     
        <xsl:for-each select="WORD/PROPERTIES/LINKSUPTODATE">
          <LinksUpToDate>
            <xsl:value-of select="."/>
          </LinksUpToDate>
        </xsl:for-each> 
        <xsl:for-each select="WORD/PROPERTIES/TITLEOFPARTS">
          <TitleOfParts>
            <xsl:value-of select="."/>
          </TitleOfParts>
        </xsl:for-each> 
        <xsl:for-each select="WORD/PROPERTIES/HEADINGPAIRS">
          <HeadingPairs>
            <xsl:value-of select="."/>
          </HeadingPairs>
        </xsl:for-each> 
        <xsl:for-each select="WORD/PROPERTIES/HYPERLINKS">
          <Hyperlinks>
            <xsl:value-of select="."/>
          </Hyperlinks>
        </xsl:for-each> 
        <xsl:for-each select="WORD/FIB/ISTEMPLATE">
          <IsTemplate>
            <xsl:value-of select="."/>
          </IsTemplate>
        </xsl:for-each>
        <xsl:for-each select="WORD/FIB/ISCOMPLEX">
          <IsComplex>
            <xsl:value-of select="."/>
          </IsComplex>
        </xsl:for-each>    
        <xsl:for-each select="WORD/FIB/ISENCRYPTED">
          <IsEncrypted>
            <xsl:value-of select="."/>
          </IsEncrypted>
        </xsl:for-each> 
        <xsl:for-each select="WORD/FIB/HASPICTURES">
          <HasPictures>
            <xsl:value-of select="."/>
          </HasPictures>
        </xsl:for-each> 
        <xsl:for-each select="WORD/FIB/ISGLOSSARY">
          <IsGlossary>
            <xsl:value-of select="."/>
          </IsGlossary>
        </xsl:for-each>    
        <xsl:for-each select="WORD/FIB/LID">
          <LID>
            <xsl:value-of select="."/>
          </LID>
        </xsl:for-each>   
        <xsl:for-each select="WORD/FIB/DOPOFFSET">
          <DOPOffset>
            <xsl:value-of select="."/>
          </DOPOffset>
        </xsl:for-each>                                                  
      </Properties>           
    </File>
  </xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c)1998-2002 eXcelon Corp.
<metaInformation>
<scenarios ><scenario default="yes" name="test1" userelativepaths="yes" externalpreview="no" url="..\..\harvested\nlnz_dd\A PSALM OF LIFE wordv97&#x2D;v2000.doc.xml" htmlbaseurl="" processortype="internal" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/></scenarios><MapperInfo srcSchemaPath="word_ole.dtd" srcSchemaRoot="WORD" srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="nlnz_file.xsd" destSchemaRoot="File" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
</metaInformation>
-->