<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:nz_govt_natlib_xsl_XSLTFunctions="nz.govt.natlib.xsl.XSLTFunctions">
  <xsl:strip-space elements="doc chapter section"/>
  <xsl:output omit-xml-declaration="yes" indent="yes" encoding="iso-8859-1" version="1.0"/>
  <xsl:template match="/">
    <File>
      <FileIdentifier>
        <xsl:value-of select="nz_govt_natlib_xsl_XSLTFunctions:determineFileIdentifier(string(FLAC/METADATA/PID),string(FLAC/METADATA/OID),string(FLAC/METADATA/FILENAME),string(FLAC/METADATA/FID))"/>
      </FileIdentifier>
      <xsl:for-each select="FLAC/METADATA/PATH">
        <Path>
          <xsl:value-of select="."/>
        </Path>
      </xsl:for-each>
      <Filename>
        <xsl:for-each select="FLAC/METADATA/FILENAME">
          <Name>
            <xsl:value-of select="."/>
          </Name>
        </xsl:for-each>
        <xsl:for-each select="FLAC/METADATA/EXTENSION">
          <Extension>
            <xsl:value-of select="."/>
          </Extension>
        </xsl:for-each>
      </Filename>
      <xsl:for-each select="FLAC/METADATA/FILELENGTH">
        <Size>
          <xsl:value-of select="."/>
        </Size>
      </xsl:for-each>
      <FileDateTime>
        <xsl:for-each select="FLAC/METADATA/DATE">
          <Date format="yyyyMMdd">
            <xsl:value-of select="."/>
          </Date>
        </xsl:for-each>
        <xsl:for-each select="FLAC/METADATA/TIME">
          <Time format="HHmmssSSS">
            <xsl:value-of select="."/>
          </Time>
        </xsl:for-each>
      </FileDateTime>
      <xsl:for-each select="FLAC/METADATA/TYPE">
        <Mimetype>
          <xsl:value-of select="."/>
        </Mimetype>
      </xsl:for-each>
      <FileFormat>
        <Format>
          <xsl:value-of select="string('FLAC (Free Lossless Audio Codec) File')"/>
        </Format>
      </FileFormat>

      <Flac>
        <FlacMetadata>

          <StreamInfo>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/MINBLOCKSIZE">
              <MinBlockSize>
                <xsl:value-of select="."/>
              </MinBlockSize>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/MAXBLOCKSIZE">
              <MaxBlockSize>
                <xsl:value-of select="."/>
              </MaxBlockSize>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/MINFRAMESIZE">
              <MinFrameSize>
                <xsl:value-of select="."/>
              </MinFrameSize>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/MAXFRAMESIZE">
              <MaxFrameSize>
                <xsl:value-of select="."/>
              </MaxFrameSize>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/SAMPLE-RATE">
              <SampleRate>
                <xsl:value-of select="."/>
              </SampleRate>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/SAMPLE-RATE-UNIT">
              <SampleRateUnit>
                <xsl:value-of select="."/>
              </SampleRateUnit>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/CHANNELS">
              <Channels>
                <xsl:value-of select="."/>
              </Channels>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/BITSPERSAMPLE">
              <BitsPerSample>
                <xsl:value-of select="."/>
              </BitsPerSample>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/TOTALSAMPLES">
              <TotalSamples>
                <xsl:value-of select="."/>
              </TotalSamples>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/STREAMINFO/MD5">
              <MD5>
                <xsl:value-of select="."/>
              </MD5>
            </xsl:for-each>
          </StreamInfo>

          <Application>
            <xsl:for-each select="FLAC/FLACMETADATA/APPLICATION/APPLICATION-ID">
              <ApplicationId>
                <xsl:value-of select="."/>
              </ApplicationId>
            </xsl:for-each>
          </Application>

          <Padding>
            <xsl:for-each select="FLAC/FLACMETADATA/PADDING/LENGTH">
              <Length>
                <xsl:value-of select="."/>
              </Length>
            </xsl:for-each>
          </Padding>

          <SeekTable>
            <xsl:for-each select="FLAC/FLACMETADATA/SEEKTABLE/SEEKPOINT">
              <SeekPoint>
                <SampleNumber>
                  <xsl:value-of select="SAMPLE-NUMBER"/>
                </SampleNumber>
                <StreamOffset>
                  <xsl:value-of select="STREAM-OFFSET"/>
                </StreamOffset>
                <FrameSamples>
                  <xsl:value-of select="FRAME-SAMPLES"/>
                </FrameSamples>
              </SeekPoint>
            </xsl:for-each>
          </SeekTable>

          <VorbisComment>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/VENDOR-STRING">
              <VendorString>
                <xsl:value-of select="."/>
              </VendorString>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ALBUM">
              <Album>
                <xsl:value-of select="."/>
              </Album>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ARRANGER">
              <Arranger>
                <xsl:value-of select="."/>
              </Arranger>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ARTIST">
              <Artist>
                <xsl:value-of select="."/>
              </Artist>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/AUTHOR">
              <Author>
                <xsl:value-of select="."/>
              </Author>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/COMMENT">
              <Comment>
                <xsl:value-of select="."/>
              </Comment>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/COMPOSER">
              <Composer>
                <xsl:value-of select="."/>
              </Composer>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/CONDUCTOR">
              <Conductor>
                <xsl:value-of select="."/>
              </Conductor>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/CONTACT">
              <Contact>
                <xsl:value-of select="."/>
              </Contact>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/COPYRIGHT">
              <Copyright>
                <xsl:value-of select="."/>
              </Copyright>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/DATE">
              <Date>
                <xsl:value-of select="."/>
              </Date>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/DESCRIPTION">
              <Description>
                <xsl:value-of select="."/>
              </Description>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/DISCNUMBER">
              <DiscNumber>
                <xsl:value-of select="."/>
              </DiscNumber>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/EAN-UPN">
              <EAN-UPN>
                <xsl:value-of select="."/>
              </EAN-UPN>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ENCODED-BY">
              <EncodedBy>
                <xsl:value-of select="."/>
              </EncodedBy>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ENCODING">
              <Encoding>
                <xsl:value-of select="."/>
              </Encoding>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ENSEMBLE">
              <Ensemble>
                <xsl:value-of select="."/>
              </Ensemble>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/GENRE">
              <Genre>
                <xsl:value-of select="."/>
              </Genre>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ISRC">
              <ISRC>
                <xsl:value-of select="."/>
              </ISRC>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/LABEL">
              <Label>
                <xsl:value-of select="."/>
              </Label>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/LABELNO">
              <LabelNo>
                <xsl:value-of select="."/>
              </LabelNo>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/LICENSE">
              <License>
                <xsl:value-of select="."/>
              </License>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/LOCATION">
              <Location>
                <xsl:value-of select="."/>
              </Location>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/LYRICIST">
              <Lyricist>
                <xsl:value-of select="."/>
              </Lyricist>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/OPUS">
              <Opus>
                <xsl:value-of select="."/>
              </Opus>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/ORGANIZATION">
              <Organization>
                <xsl:value-of select="."/>
              </Organization>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/PART">
              <Part>
                <xsl:value-of select="."/>
              </Part>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/PARTNUMBER">
              <PartNumber>
                <xsl:value-of select="."/>
              </PartNumber>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/PERFORMER">
              <Performer>
                <xsl:value-of select="."/>
              </Performer>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/PUBLISHER">
              <Publisher>
                <xsl:value-of select="."/>
              </Publisher>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/SOURCEMEDIA">
              <SourceMedia>
                <xsl:value-of select="."/>
              </SourceMedia>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/TITLE">
              <Title>
                <xsl:value-of select="."/>
              </Title>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/TRACKNUMBER">
              <TrackNumber>
                <xsl:value-of select="."/>
              </TrackNumber>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/VORBIS-COMMENT/VERSION">
              <Version>
                <xsl:value-of select="."/>
              </Version>
            </xsl:for-each>
          </VorbisComment>

          <CueSheet>
            <xsl:for-each select="FLAC/FLACMETADATA/CUESHEET/MEDIA-CATALOG-NUMBER">
              <MediaCatalogNumber>
                <xsl:value-of select="."/>
              </MediaCatalogNumber>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/CUESHEET/LEAD-IN-SAMPLES">
              <LeadInSamples>
                <xsl:value-of select="."/>
              </LeadInSamples>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/CUESHEET/IS-CD">
              <IsCD>
                <xsl:value-of select="."/>
              </IsCD>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/CUESHEET/NUMBER-OF-TRACKS">
              <NumberOfTracks>
                <xsl:value-of select="."/>
              </NumberOfTracks>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/CUESHEET/CUETRACK">
              <CueTrack>
                <TrackOffset>
                  <xsl:value-of select="TRACK-OFFSET"/>
                </TrackOffset>
                <TrackNumber>
                  <xsl:value-of select="TRACK-NUMBER"/>
                </TrackNumber>
                <ISRC>
                  <xsl:value-of select="ISRC"/>
                </ISRC>
                <IsAudio>
                  <xsl:value-of select="IS-AUDIO"/>
                </IsAudio>
                <PreEmphasis>
                  <xsl:value-of select="PRE-EMPHASIS"/>
                </PreEmphasis>
                <TrackIndexPoints>
                  <xsl:value-of select="TRACK-INDEX-POINTS"/>
                </TrackIndexPoints>
                <xsl:for-each select="CUETRACKINDEX">
                  <CueTrackIndex>
                    <Offset>
                      <xsl:value-of select="OFFSET"/>
                    </Offset>
                    <Number>
                      <xsl:value-of select="NUMBER"/>
                    </Number>
                  </CueTrackIndex>
                </xsl:for-each>
              </CueTrack>
            </xsl:for-each>
          </CueSheet>

          <Picture>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/PICTURE-TYPE">
              <PictureType>
                <xsl:value-of select="."/>
              </PictureType>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/MIME-TYPE">
              <MimeType>
                <xsl:value-of select="."/>
              </MimeType>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/DESCRIPTION">
              <Description>
                <xsl:value-of select="."/>
              </Description>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/WIDTH">
              <Width>
                <xsl:value-of select="."/>
              </Width>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/HEIGHT">
              <Height>
                <xsl:value-of select="."/>
              </Height>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/COLOUR-DEPTH">
              <ColourDepth>
                <xsl:value-of select="."/>
              </ColourDepth>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/COLOUR-DEPTH-UNIT">
              <ColourDepthUnit>
                <xsl:value-of select="."/>
              </ColourDepthUnit>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/NUM-COLOURS">
              <NumColours>
                <xsl:value-of select="."/>
              </NumColours>
            </xsl:for-each>
            <xsl:for-each select="FLAC/FLACMETADATA/PICTURE/DATA-LENGTH">
              <DataLength>
                <xsl:value-of select="."/>
              </DataLength>
            </xsl:for-each>
          </Picture>

        </FlacMetadata>
      </Flac>
    </File>
  </xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c)1998-2002 eXcelon Corp.
<metaInformation>
<scenarios ><scenario default="yes" name="Test" userelativepaths="yes" externalpreview="no" url="..\..\harvested\new native\Blue Lace 16.bmp.xml" htmlbaseurl="" processortype="internal" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/></scenarios><MapperInfo srcSchemaPath="bmp.dtd" srcSchemaRoot="FLAC" srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="nlnz_file.xsd" destSchemaRoot="File" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
</metaInformation>
-->