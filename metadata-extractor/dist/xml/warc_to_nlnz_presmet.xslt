<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:nz_govt_natlib_xsl_XSLTFunctions="nz.govt.natlib.xsl.XSLTFunctions">
  <xsl:strip-space elements="doc chapter section"/>
  <xsl:output omit-xml-declaration="yes" indent="yes" encoding="iso-8859-1" version="1.0"/>
  <xsl:template match="/">
    <File>
      <FileIdentifier>
        <xsl:value-of select="nz_govt_natlib_xsl_XSLTFunctions:determineFileIdentifier(string(WARC/METADATA/PID),string(WARC/METADATA/OID),string(WARC/METADATA/FILENAME),string(WARC/METADATA/FID))"/>
      </FileIdentifier>
      <xsl:for-each select="WARC/METADATA/PATH">
        <Path>
          <xsl:value-of select="."/>
        </Path>
      </xsl:for-each>
      <Filename>
        <xsl:for-each select="WARC/METADATA/FILENAME">
          <Name>
            <xsl:value-of select="."/>
          </Name>
        </xsl:for-each>
        <xsl:for-each select="WARC/METADATA/EXTENSION">
          <Extension>
            <xsl:value-of select="."/>
          </Extension>
        </xsl:for-each>
      </Filename>
      <xsl:for-each select="WARC/METADATA/PARENT">
        <Parent>
          <xsl:value-of select="."/>
        </Parent>
      </xsl:for-each>       
      <xsl:for-each select="WARC/METADATA/MODIFIED">
        <DateModified>
          <xsl:value-of select="."/>
        </DateModified>
      </xsl:for-each>       
      <xsl:for-each select="WARC/METADATA/FILELENGTH">
        <Size>
          <xsl:value-of select="."/>
        </Size>
      </xsl:for-each>
      <FileDateTime>
        <xsl:for-each select="WARC/METADATA/DATE">
          <Date format="yyyyMMdd">
            <xsl:value-of select="."/>
          </Date>
        </xsl:for-each>
        <xsl:for-each select="WARC/METADATA/TIME">
          <Time format="HHmmssSSS">
            <xsl:value-of select="."/>
          </Time>
        </xsl:for-each>
      </FileDateTime>
      <xsl:for-each select="WARC/METADATA/TYPE">
        <Mimetype>
          <xsl:value-of select="."/>
        </Mimetype>
      </xsl:for-each>
      <xsl:for-each select="WARC/METADATA/READ">
        <Read>
          <xsl:value-of select="."/>
        </Read>
      </xsl:for-each> 
      <xsl:for-each select="WARC/METADATA/WRITE">
        <Write>
          <xsl:value-of select="."/>
        </Write>
      </xsl:for-each>           
      <FileFormat>
        <Format>
          <xsl:value-of select="string('Internet Archive WARC File')"/>
        </Format>
      </FileFormat>
      <Warc>
        <WarcMetadata>
          <xsl:for-each select="WARC/WARCMETADATA/SOFTWARE">
            <Software>
              <xsl:value-of select="."/>
            </Software>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/HOSTNAME">
            <HostName>
              <xsl:value-of select="."/>
            </HostName>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/IP">
            <IP>
              <xsl:value-of select="."/>
            </IP>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/OPERATOR">
            <Operator>
              <xsl:value-of select="."/>
            </Operator>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/CREATEDDATE">
            <CreatedDate>
              <xsl:value-of select="."/>
            </CreatedDate>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/ROBOTPOLICY">
            <RobotPolicy>
              <xsl:value-of select="."/>
            </RobotPolicy>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/WARCDATE">
            <WarcDate>
              <xsl:value-of select="."/>
            </WarcDate>
          </xsl:for-each>          
          <xsl:for-each select="WARC/WARCMETADATA/WARCFORMAT">
            <WarcFormat>
              <xsl:value-of select="."/>
            </WarcFormat>
          </xsl:for-each>
          <xsl:for-each select="WARC/WARCMETADATA/CONFORMSTO">
            <ConformsTo>
              <xsl:value-of select="."/>
            </ConformsTo>
          </xsl:for-each>
        </WarcMetadata>

        <WarcInfo>
          <xsl:for-each select="WARC/WARCINFO/COMPRESSED">
            <Compressed>
              <xsl:value-of select="."/>
            </Compressed>
          </xsl:for-each>

          <ContentSummary>
            <xsl:for-each select="WARC/WARCINFO/CONTENTSUMMARY/MIMEREPORT">
              <MimeReport>
                <xsl:value-of select="."/>
              </MimeReport>
            </xsl:for-each>
            <WarcTypeReport>
	            <xsl:for-each select="WARC/WARCINFO/CONTENTSUMMARY/WARCTYPEREPORT">
	              <WarcType>
	                <xsl:value-of select="WARCTYPE"/>
	              </WarcType>	            
	              <Count>
	                <xsl:value-of select="COUNT"/>
	              </Count>
	            </xsl:for-each>                     
	        </WarcTypeReport>          
          </ContentSummary>

        </WarcInfo>

      </Warc>
    </File>
  </xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c)1998-2002 eXcelon Corp.
<metaInformation>
<scenarios ><scenario default="yes" name="Test" userelativepaths="yes" externalpreview="no" url="..\..\harvested\new native\Blue Lace 16.bmp.xml" htmlbaseurl="" processortype="internal" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/></scenarios><MapperInfo srcSchemaPath="bmp.dtd" srcSchemaRoot="WARC" srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="nlnz_file.xsd" destSchemaRoot="File" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
</metaInformation>
-->