<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<InventoryVisibilityAPI URL="" HTTPMethod="" Content-Type="application/json">
<Input>
{ "lines":[
<xsl:apply-templates select="*" />
]
}
</Input>
</InventoryVisibilityAPI>
</xsl:template>

<xsl:template match="ItemList/Item">
                {
                        "deliveryMethod":"SHP",
                        "itemId":"<xsl:value-of select="@ItemID"/>",
                        "lineId": "<xsl:value-of select="@LineId"/>",
                        "productClass":"<xsl:value-of select="@ProductClass"/>",
                        "shipNodes": [<xsl:for-each select="ShipNodes/ShipNode">
                           "<xsl:value-of select="@ShipNode"/>"<xsl:if test="position()!=last()"><xsl:text>, </xsl:text>
        					</xsl:if>
                           
                        </xsl:for-each>],
                        
                        "unitOfMeasure":"<xsl:value-of select="@UnitOfMeasure"/>",
                        "segment":"<xsl:value-of select="@Segment"/>",
                        "segmentType":"<xsl:value-of select="@SegmentType"/>"
                        
                }
                <xsl:if test="./following-sibling::Item">,</xsl:if>

</xsl:template>
</xsl:stylesheet>

