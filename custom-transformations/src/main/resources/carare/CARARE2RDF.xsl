<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE rdf:RDF [
        <!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#">
        <!ENTITY ns "http://www.carare.eu/carareSchema#">
        ]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:rdf="&rdf;" xmlns:util="http://java.net/util">

    <xsl:strip-space elements="*"/>
    <xsl:output indent="yes"/>

    <xsl:template name="id">
        <xsl:choose>
            <xsl:when test="./@id">
                <xsl:value-of select="concat(local-name(), ':', ./@id)" />
            </xsl:when>
            <xsl:when test="child::*[name() = 'id'][1]/text()">
                <xsl:value-of select="concat(local-name(), ':', child::*[name() = 'id'][1]/text())" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="util:uuid(local-name(), generate-id())" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="/">
        <rdf:RDF xmlns:rdf='&rdf;'
                 xmlns:ns="&ns;">
            <xsl:call-template name="element"/>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="*" name="element">
        <xsl:variable name="separate-descriptions"
                      select="*[count(@*|*)>0 and count(text())=0]"/>
        <xsl:variable name="uuid">
            <xsl:call-template name="id"/>
        </xsl:variable>
        <xsl:if test="local-name()">
        <rdf:Description rdf:about="{$uuid}" xmlns="&ns;">

            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}" namespace="&ns;">
                        <xsl:value-of select="."/>
                 </xsl:attribute>
            </xsl:for-each>

            <xsl:for-each select="$separate-descriptions">
                <xsl:variable name="uuid">
                    <xsl:call-template name="id"/>
                </xsl:variable>
                <xsl:element name="{local-name()}">
                    <xsl:attribute name="rdf:resource" select="$uuid"/>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="* except $separate-descriptions">
                <xsl:element name="{local-name()}">
                    <xsl:choose>
                        <xsl:when test="count(*)>0">
                            <xsl:attribute name="rdf:parseType"
                            >Literal</xsl:attribute>
                            <xsl:copy-of select="*|text()"
                                         copy-namespaces="no"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:element>
            </xsl:for-each>
            <xsl:element name="rdf:type"><xsl:value-of select="local-name()"/></xsl:element>
        </rdf:Description>
        </xsl:if>
        <xsl:apply-templates select="$separate-descriptions"/>
    </xsl:template>
</xsl:stylesheet>