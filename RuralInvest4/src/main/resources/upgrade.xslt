<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="xml" indent="yes" />
	
	<xsl:template match="void[@property='contribType']">
		<void property="oldDonor">
			<string>
				<xsl:value-of select="int"/>
				<xsl:text>-XRIVX--</xsl:text>
				<xsl:value-of select="../void[@property='contributor']/string"/>
			</string>
		</void>
	</xsl:template>
	
	<!-- add donors to project -->
	<xsl:template match="void[@property='projectName'][not(../void[@property='donors'])]">
		<void property="donors">
   			<object class="java.util.HashSet">
    			<void method="add">
				    <object class="riv.objects.project.Donor">
				    	<void property="contribType">
				    		<int>4</int>
				      	</void>
				      	<void property="description">
				       		<string>not specified</string>
				      	</void>
				      	<void property="notSpecified">
					       <boolean>true</boolean>
					    </void>
					    <void property="orderBy">
					       <int>0</int>
					    </void>
					    <void property="project">
					    	<object idref="Project0"/>
					    </void>
				    </object>
				</void>
				<!-- NIG project need "state/public" -->
				<xsl:variable name="incomeGen" select="../void[@property='incomeGen']/boolean"/>
				<xsl:if test="$incomeGen = 'false'">
					<void method="add">
					    <object class="riv.objects.project.Donor">
					    	<void property="contribType">
					    		<int>4</int>
					      	</void>
					      	<void property="description">
					       		<string>state-public</string>
					      	</void>
					      	<void property="notSpecified">
						       <boolean>false</boolean>
						    </void>
						    <void property="orderBy">
						       <int>1</int>
						    </void>
					    </object>
					</void>
				</xsl:if>
			</object>
		</void>
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
	
	<!-- donated field moved to own table -->
	<xsl:template match="void[@property='donated'][double]">
		<xsl:variable name="double" select="double" />
		<xsl:if test="$double != '0.0'">
			<void property="donations">
				<void method="put">
	       			<int>-1</int>
	       			<double><xsl:value-of select="$double"/></double>
	      		</void>
	     	</void>
     	</xsl:if>
	</xsl:template>
	<xsl:template match="object[void[@property='other1']]">
		<xsl:variable name="other1" select="void[@property='other1']/double" />
		<xsl:variable name="statePublic" select="void[@property='statePublic']/double" />
		<xsl:if test="$other1 != '0.0' or $statePublic != '0.0'">
			<void property="donations">
				<xsl:if test="$other1 != '0.0'">
					<void method="put">
		       			<int>0</int>
		       			<double><xsl:value-of select="$other1"/></double>
		      		</void>
	      		</xsl:if>
				<xsl:if test="$statePublic != '0.0'">
					<void method="put">
		       			<int>1</int>
		       			<double><xsl:value-of select="$statePublic"/></double>
		      		</void>
	      		</xsl:if>
	     	</void>
     	</xsl:if>
	</xsl:template>
	
	
	
	
	<!-- fields removed when refactoring BlockChron -->
	<xsl:template match="void[@property='firstPart']"/>
	<xsl:template match="void[@property='monthNum']"/>
	<xsl:template match="void[@property='chronType']"/>
	
	<!-- block.cycleLength changed to double -->
	<xsl:template match="void[@property='cycleLength'][int]">
		<xsl:variable name="int" select="int"/>
		<void property="cycleLength">
			<double>
				<xsl:value-of select="$int"/>
				<xsl:text>.0</xsl:text>
			</double>
		</void>
	</xsl:template>
	
	<!-- block pattern changed from int to double -->
	<xsl:template match="void[@property='qty'][int][following-sibling::void[@property='yearNum']]">
		<void property="qty">
			<double><xsl:value-of select="int"/><xsl:text>.0</xsl:text></double>
		</void>
	</xsl:template>

	<!-- some changes in storing organization logo in settings.riv -->
	<!-- remove empty "userLogo" -->
	<xsl:template match="void[@property='userLogo'][array[@length='0']]"/>
	<!-- change orgLogo to userLogo (if not empty) -->
	<xsl:template match="void[@property='orgLogo'][array[@length!='0']]">
		<void property="userLogo">
		<xsl:apply-templates />
		</void>
	</xsl:template>
	
	<!-- RIV4.0 refactored block -->
	<!-- userCode found before withWithout in Project object, here we want only projectBlock -->
	<!-- profile already has boolean, so we specify that we want to change if it has an int -->
	<xsl:template match="void[@property='withWithout'][int][not(preceding-sibling::void[@property='userCode'])]">
		<xsl:variable name="int" select="int"/>
		<void property="withProject">
			<boolean>
				<xsl:choose>
					<xsl:when test="$int='0'"><xsl:text>true</xsl:text></xsl:when>
					<xsl:when test="$int='1'"><xsl:text>false</xsl:text></xsl:when>
				</xsl:choose>
			</boolean>
		</void>
	</xsl:template>
	
	<!-- RIV1.X had category with configId -1, now changed to -5 -->
	<!-- Change in imported project -->
	<xsl:template match="void[@property='configId'][int[text()='-1']]">
		<void property="configId">
			<int>-5</int>
		</void>
	</xsl:template>
	
	<!-- Previous to RIV 2.2 projects had fixed-code status (int).  
	Post-2.2 has user-defined with pre-set defaults.  
	This method replaces the old with the new. -->
	<xsl:template match="void[@property='status'][int]">
		<xsl:variable name="int" select="int"/>
		<void property="status">
			<object class="riv.objects.config.Status">
				<void property="configId">
					<int>
						<xsl:choose>
							<xsl:when test="$int = '0'">-20</xsl:when>
							<xsl:when test="$int = '1'">-21</xsl:when>
							<xsl:when test="$int = '2'">-22</xsl:when>
							<xsl:when test="$int = '3'">-23</xsl:when>
							<xsl:otherwise><xsl:value-of select="$int"/></xsl:otherwise>
						</xsl:choose>
					</int>
				</void>
				<void property="description">
					<string> </string>
				</void>
			</object>
		</void>
	</xsl:template>
	
	<!-- Previous to RIV 2.0 projects had enviroClass (string).  
	Post-2.0 has enviroCategory (int).  
	This method replaces the old with the new. -->
	<xsl:template match="void[@property='enviroClass'][string]">
		<xsl:variable name="string" select="string"/>
		<void property="enviroCategory">
			<object class="riv.objects.config.EnviroCategory">
				<void property="configId">
					<int>
						<xsl:choose>
							<xsl:when test="$string = 'A'">-30</xsl:when>
							<xsl:when test="$string = 'B'">-31</xsl:when>
							<xsl:when test="$string = 'C'">-32</xsl:when>
							<xsl:when test="$string = 'D'">-33</xsl:when>
						</xsl:choose>
					</int>
					<void property="description"><string><xsl:value-of select="$string"/></string></void>
				</void>
			</object>
		</void>
	</xsl:template>
	
	<!-- changes in object names in RIV4.0 -->
	<xsl:template match="void[@property='projectBlock']">
		<void property="block">
			<xsl:apply-templates />
		</void>
	</xsl:template>
	<xsl:template match="void[@property='nongenInputs']">
		<void property="nongenMaterials">
			<xsl:apply-templates/>
		</void>
	</xsl:template>
	<xsl:template match="void[@property='nongenGenerals']">
		<void property="nongenMaintenance">
			<xsl:apply-templates/>
		</void>
	</xsl:template>

	<!-- change in object names in RIV4.0 -->
	<xsl:template match="object[@class]">
		<xsl:element name="object">
			<xsl:if test="@id">
				<xsl:attribute name="id">
					<xsl:value-of select="@id" />
				</xsl:attribute>
			</xsl:if>
			<!-- RIV4.0 erroneously leaves __$$_javaassist in some classnames -->
			<xsl:variable name="class">
				<xsl:choose>
					<xsl:when test="contains(@class,'_$$')"><xsl:value-of select="substring-before(@class,'_$$')"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="@class"/></xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="starts-with($class,'rivWeb.RivConfig')">
						<xsl:text>riv.web.config.RivConfig</xsl:text>
						<xsl:value-of select="substring-after($class,'rivWeb.RivConfig')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.Beneficiary')">
						<xsl:text>riv.objects.config.Beneficiary</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.Beneficiary')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.ProjectCategory')">
						<xsl:text>riv.objects.config.ProjectCategory</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.ProjectCategory')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.EnviroCategory')">
						<xsl:text>riv.objects.config.EnviroCategory</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.EnviroCategory')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.FieldOffice')">
						<xsl:text>riv.objects.config.FieldOffice</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.FieldOffice')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.Setting')">
						<xsl:text>riv.objects.config.Setting</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.Setting')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.Status')">
						<xsl:text>riv.objects.config.Status</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.Status')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.AppConfig')">
						<xsl:text>riv.objects.config.AppConfig</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.AppConfig')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.reference')">
						<xsl:text>riv.objects.reference</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.reference')" />
					</xsl:when>
					<!-- <RIV4.0 "without project" products are now a separate class -->
					<!-- case: profile has without products and this product is "without" -->
					<xsl:when test="$class='riv.ProfileProduct' and not(void[@property='withWithout']) and ../../../../void[@property='withWithout'][boolean='true']">
						<xsl:text>riv.objects.profile.ProfileProductWithout</xsl:text>
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.Profile')">
						<xsl:text>riv.objects.profile.Profile</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.Profile')" />
					</xsl:when>
					<!-- <RIV4.0 "without project" blocks are now a separate class -->
					<xsl:when test="$class='riv.ProjectBlock' and void[@property='withWithout'][int='1']">
						<xsl:text>riv.objects.project.BlockWithout</xsl:text>
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.ProjectBlock')">
						<xsl:text>riv.objects.project.Block</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.ProjectBlock')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.ProjectItemNongenInput')">
						<xsl:text>riv.objects.project.ProjectItemNongenMaterials</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.ProjectItemNongenInput')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.ProjectItemNongenGeneral')">
						<xsl:text>riv.objects.project.ProjectItemNongenMaintenance</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.ProjectItemNongenGeneral')" />
					</xsl:when>
					<xsl:when test="starts-with($class,'riv.Project')">
						<xsl:text>riv.objects.project.Project</xsl:text>
						<xsl:value-of select="substring-after($class,'riv.Project')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$class" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>