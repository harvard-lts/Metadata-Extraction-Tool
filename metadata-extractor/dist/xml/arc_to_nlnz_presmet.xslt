<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:nz_govt_natlib_xsl_XSLTFunctions="nz.govt.natlib.xsl.XSLTFunctions">
  <xsl:strip-space elements="doc chapter section"/>
  <xsl:output omit-xml-declaration="yes" indent="yes" encoding="iso-8859-1" version="1.0"/>
  <xsl:template match="/">
    <File>
      <FileIdentifier>
        <xsl:value-of select="nz_govt_natlib_xsl_XSLTFunctions:determineFileIdentifier(string(ARC/METADATA/PID),string(ARC/METADATA/OID),string(ARC/METADATA/FILENAME),string(ARC/METADATA/FID))"/>
      </FileIdentifier>
      <xsl:for-each select="ARC/METADATA/PATH">
        <Path>
          <xsl:value-of select="."/>
        </Path>
      </xsl:for-each>
      <Filename>
        <xsl:for-each select="ARC/METADATA/FILENAME">
          <Name>
            <xsl:value-of select="."/>
          </Name>
        </xsl:for-each>
        <xsl:for-each select="ARC/METADATA/EXTENSION">
          <Extension>
            <xsl:value-of select="."/>
          </Extension>
        </xsl:for-each>
      </Filename>
      <xsl:for-each select="ARC/METADATA/FILELENGTH">
        <Size>
          <xsl:value-of select="."/>
        </Size>
      </xsl:for-each>
      <FileDateTime>
        <xsl:for-each select="ARC/METADATA/DATE">
          <Date format="yyyyMMdd">
            <xsl:value-of select="."/>
          </Date>
        </xsl:for-each>
        <xsl:for-each select="ARC/METADATA/TIME">
          <Time format="HHmmssSSS">
            <xsl:value-of select="."/>
          </Time>
        </xsl:for-each>
      </FileDateTime>
      <xsl:for-each select="ARC/METADATA/TYPE">
        <Mimetype>
          <xsl:value-of select="."/>
        </Mimetype>
      </xsl:for-each>
      <FileFormat>
        <Format>
          <xsl:value-of select="string('Internet Archive ARC File')"/>
        </Format>
      </FileFormat>
      <Arc>
        <ArcMetadata>
          <xsl:for-each select="ARC/ARCMETADATA/SOFTWARE">
            <Software>
              <xsl:value-of select="."/>
            </Software>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/HOSTNAME">
            <HostName>
              <xsl:value-of select="."/>
            </HostName>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/IP">
            <IP>
              <xsl:value-of select="."/>
            </IP>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/OPERATOR">
            <Operator>
              <xsl:value-of select="."/>
            </Operator>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/CREATEDDATE">
            <CreatedDate>
              <xsl:value-of select="."/>
            </CreatedDate>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/ROBOTPOLICY">
            <RobotPolicy>
              <xsl:value-of select="."/>
            </RobotPolicy>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/ARCFORMAT">
            <ArcFormat>
              <xsl:value-of select="."/>
            </ArcFormat>
          </xsl:for-each>
          <xsl:for-each select="ARC/ARCMETADATA/CONFORMSTO">
            <ConformsTo>
              <xsl:value-of select="."/>
            </ConformsTo>
          </xsl:for-each>
        </ArcMetadata>

        <ArcInfo>
          <xsl:for-each select="ARC/ARCINFO/COMPRESSED">
            <Compressed>
              <xsl:value-of select="."/>
            </Compressed>
          </xsl:for-each>

          <ContentSummary>
            <xsl:for-each select="ARC/ARCINFO/CONTENTSUMMARY">
              <MimeReport>
                <xsl:value-of select="MIMEREPORT"/>
              </MimeReport>
            </xsl:for-each>
          </ContentSummary>

        </ArcInfo>

      </Arc>
    </File>
  </xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c)1998-2002 eXcelon Corp.
<metaInformation>
<scenarios ><scenario default="yes" name="Test" userelativepaths="yes" externalpreview="no" url="..\..\harvested\new native\Blue Lace 16.bmp.xml" htmlbaseurl="" processortype="internal" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/></scenarios><MapperInfo srcSchemaPath="bmp.dtd" srcSchemaRoot="ARC" srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="nlnz_file.xsd" destSchemaRoot="File" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
</metaInformation>
-->